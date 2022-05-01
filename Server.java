/*
   Server handles the prospective connectors via UDP and the various concurrent connections initiated by any number of clients.
   It does this by creating a thread for each TCP connection initiated. 
   The thread will handle all messages to be sent and received by the client.

   Note: running java Server.java will cause an IllegalAccessError
*/

import java.util.*;
import java.util.concurrent.Semaphore;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.net.*;

public class Server {

   enum State {
      offline, wait_challenge, wait_auth, connecting, connected, wait_chat, chatting
   }


   //keep track of each potential client's connection state. (Client ID, Current State of client)
   private static Hashtable <Integer, State> clientStates = new Hashtable<>(); 

   //Keep track of all chat histories (Session ID, Session)
   private static Hashtable<Integer, Session> sessions = new Hashtable<>();
   static int sessionCounter = 0;

   //Keeps track of all TCP connections made (clientID, Connection)
   protected static Hashtable<Integer, Connection> activeConnections = new Hashtable<>();

   public static void main(String[] args) {
      final int MAX_ID = 1000000;
      final int MAX_SECRET_KEY = 1000000;

     
      //subscirbers keeps track of valid client IDs and their corresponding secret keys. (Client ID, Secret Key)
      Hashtable<Integer, Integer> subscribers = new Hashtable<>();

      
      //create a semaphore for each session to avoid collisions (Session ID, Semaphore)
      Hashtable<Integer, Semaphore> sessionSemaphores = new Hashtable<>();

      //Keep track of all authenticated connecting clients
      Hashtable<InetSocketAddress, Integer> connectingClients = new Hashtable<>();

      //keeps track of all cookies that have been sent out
      Hashtable<Integer, Integer> cookies = new Hashtable<>();

      final int portNumber = 4445;
      int TCPportnum = portNumber + 1;
      int newTCPPortNum = TCPportnum;

      //read in the list of valid subscribers to the server
      try {
         String absolutePath = new File(".").getAbsolutePath();
         absolutePath = absolutePath.substring(0, absolutePath.length()-1);
         FileReader inFile = new FileReader("subscribers.txt");
         Scanner input = new Scanner(inFile);
         String line;
         String[] tokens;
         while (input.hasNextLine()) {
            line = input.nextLine();
            tokens = line.split(" ");
            int clientID = Integer.parseInt(tokens[0]);
            int secretKey = Integer.parseInt(tokens[1]);
            subscribers.put(clientID, secretKey);
         }
      } catch (FileNotFoundException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      

      //set up server
      try {
         DatagramSocket serverSocket = new DatagramSocket(portNumber);
         ServerSocket TCPSocket = new ServerSocket(TCPportnum);
         printServerInfo(serverSocket);

         DatagramPacket receivedPacket;

         byte[] inBuf = new byte[512];
         
         //read datagrams 
         while (!(UDPMethods.byteToString(inBuf).equals("END"))) {
            //clear buffers after each packet
            inBuf = new byte[512];
            System.out.println("Looking for packets...");
            receivedPacket = new DatagramPacket(inBuf, inBuf.length);
            serverSocket.receive(receivedPacket);

            //get information from packet to send to client
            InetAddress receivedAddress = receivedPacket.getAddress();
            int receivedPort = receivedPacket.getPort();
            System.out.println("Packet received from: " + receivedAddress + ":" + receivedPort);

            String message = UDPMethods.byteToString(inBuf);
            String[] tokens = message.split(" ");
            String messageType = tokens[0];
            System.out.println(message);
            
            //determine what to send them, for now it just returns what they send
            //outBuf = inBuf;
            //sendPacket = new DatagramPacket(outBuf, outBuf.length, receivedAddress, receivedPort);
            //serverSocket.send(sendPacket);

            //determine what to do based on sender's current state and message
            switch (messageType) {
               case "HELLO":
                  //parse message
                  try {
                     if (tokens.length != 2) {
                        throw new Exception(message + " has insufficient tokens");
                     }
                     int clientID = Integer.parseInt(tokens[1]);

                     //If sender is not a sub, don't do anything
                     if (!subscribers.containsKey(clientID)) {
                        throw new Exception("Client " + clientID + " not found.");
                     }

                     //determine if client isn't already trying to connect
                     if (clientStates.containsKey(clientID)) {
                        throw new Exception("Client " + clientID + " has already tried to connect.");
                     }
                     
                     //grab subscriber's secret key
                     int secretKey = subscribers.get(clientID);

                     //add user to memory
                     subscribers.put(clientID, secretKey);
                     //send CHALLENGE <rand>
                     //TODO: implement rand properly idk
                     int rand = (int) (Math.random() * 1000);
                     cookies.put(clientID, rand);
                     UDPMethods.sendUDPPacket("CHALLENGE " + rand, serverSocket, receivedAddress, receivedPort);

                     //set user to waiting for authentification
                     clientStates.put(clientID, State.wait_auth);
   
                  } catch (Exception e) {
                     //TODO: handle exception
                     System.out.println(e);
                  }
                  break;

               case "RESPONSE":
                  //format: RESPONSE <client ID> <resp>
                  if (tokens.length != 3) {
                     throw new Exception(message + " has insufficient tokens for " + "RESPONSE");
                  }
                  int clientID = Integer.parseInt(tokens[1]);
                  String resp = tokens[2];

                  //get hashedKey to compare to response
                  int secretKey = subscribers.get(clientID);
                  A3 hasher = new A3();
                  System.out.println("cookies int: " + cookies.get(clientID));
                  String hashedKey = hasher.hash(secretKey, cookies.get(clientID));

                  //determine if response is valid or not. For now it's gonna be if the secret key matches
                  if (resp.equals(hashedKey)) {
                     
                     //send AUTH_SUCCESS
                     newTCPPortNum += 1;
                     UDPMethods.sendUDPPacket("AUTH_SUCCESS " + cookies.get(clientID) + " " + newTCPPortNum, serverSocket, receivedAddress, receivedPort);
                     //set user to connecting 
                     clientStates.put(clientID, State.connecting);
                     connectingClients.put(new InetSocketAddress(receivedAddress, receivedPort), clientID);
                  }
                  else {
                     //send AUTH_FAIL
                     UDPMethods.sendUDPPacket("AUTH_FAIL", serverSocket, receivedAddress, receivedPort);

                     //set user to offline 
                     clientStates.put(clientID, State.offline);
                  }
                  break;

               case "CONNECT":
                  //format: CONNECT <rand cookie>
                  //TODO: change connectedClientID to be based on rand cookies
                  InetSocketAddress thingy = new InetSocketAddress(receivedAddress, receivedPort);
                  int connectedClientID = connectingClients.get(thingy);

                  //send CONNECTED and create TCP connection and thread
                  ServerSocket newTCPSocket = new ServerSocket(newTCPPortNum);
                  UDPMethods.sendUDPPacket("CONNECTED", serverSocket, receivedAddress, receivedPort);
                  Socket clientSocket = newTCPSocket.accept();
                  secretKey = subscribers.get(connectedClientID);
                  
                  

                //  Thread newTCPConnection = new Connection(clientSocket, connectedClientID, secretKey);
                  Connection newTCPConnection = new Connection(clientSocket, connectedClientID, secretKey);
                  Thread thread = new Thread(newTCPConnection);

                  //Add the Connection object to active connections hashtable
                  activeConnections.put(connectedClientID, newTCPConnection);

                  //run the thread
                  thread.start();
                  
                  //set user to connected
                  clientStates.put(connectedClientID, State.connected);
                  
                  break;
            
               default:
                  System.out.println("Unknown or invalid protocol message type received from: " + receivedAddress + ":" + receivedPort);
                  //TODO: print sender ID
                  System.out.println("Sender ID: ");
                  break;
            }
         }
         System.out.println("Closing server socket...");
         TCPSocket.close();
         serverSocket.close();

      } catch (Exception e) {
         //TODO: handle exception
         System.out.println(e);
      }
   }

   //simple debugging method that prints the server info
   public static void printServerInfo(DatagramSocket serverSocket) {
      InetAddress serverIP = serverSocket.getLocalAddress();
      int portNum = serverSocket.getLocalPort();
   
      System.out.println("Server created under: " + serverIP + ":" + portNum);
   }

   //simple debugging method that prints any client that connects to the socket unoffically
   public static void printClientInfo(Socket client) {
      InetAddress clientIP = client.getInetAddress();
      System.out.println("New client connected from: " + clientIP);
      
   }

   //Getter for the clientStates hashtable
   public static Hashtable<Integer, State> getClientStatesHashtable(){
      return clientStates;
   }

   //Getter for a state in the clientStates hashtable
   public static State getClientStatesHashtable(int clinetID){
      return clientStates.get(clinetID);
   }

   //Putter for the clientStates hashtable
   public static void putClientStatesHashtable(int clientID, State state){
      clientStates.put(clientID, state);
   }
   
   //Putter for the sessions hashtable
   public static void putSessionsHashtable(int value, Session session){
      sessions.put(value, session);
   }

   //Getter for an active connection
   public static Connection getActiveConnection(int clientID){
      return activeConnections.get(clientID);
   }

   public static Server.State getUserState(int clientID) {
      //get thread
      if (getActiveConnection(clientID) == null) {
         return Server.State.offline;
      }
      return getActiveConnection(clientID).getUserState();
   } 

   public static Session getSession(int sessionID) {
      return sessions.get(sessionID);
   }

   public static Set<Integer> getSessionsIDs() {
      return sessions.keySet();
   }

}