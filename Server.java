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

   public static void main(String[] args) {
      final int MAX_ID = 1000000;
      final int MAX_SECRET_KEY = 1000000;

      //keep track of each potential client's connection state. (Client ID, Current State of client)
      Hashtable <Integer, State> clientStates = new Hashtable<>(); 
      //subscirbers keeps track of valid client IDs and their corresponding secret keys. (Client ID, Secret Key)
      Hashtable<Integer, Integer> subscribers = new Hashtable<>();

      //Keep track of all chat histories (Session ID, Session)
      Hashtable<Integer, Session> sessions = new Hashtable<>();
      //create a semaphore for each session to avoid collisions (Session ID, Semaphore)
      Hashtable<Integer, Semaphore> sessionSemaphores = new Hashtable<>();

      //Keep track of all authenticated connecting clients
      Hashtable<InetSocketAddress, Integer> connectingClients = new Hashtable<>();

      //keeps track of all cookies that have been sent out and 
      Hashtable<Integer, Integer> cookies = new Hashtable<>();

      final int portNumber = 4445;
      int TCPportnum = portNumber;

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
                     int rand = secretKey;
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
                  int resp = Integer.parseInt(tokens[2]);
                  //determine if response is valid or not. For now it's gonna be if the secret key matches
                  if (resp == subscribers.get(clientID)) {
                     //generate random cookie
                     int rand = (int) (Math.random() * 1000);
                     cookies.put(rand, clientID);
                     //send AUTH_SUCCESS
                     System.out.println("hello");
                     UDPMethods.sendUDPPacket("AUTH_SUCCESS " + rand + " " + portNumber, serverSocket, receivedAddress, receivedPort);
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
                  //send CONNECTED and create TCP connection and thread
                  UDPMethods.sendUDPPacket("CONNECTED", serverSocket, receivedAddress, receivedPort);
                  TCPportnum += 1;
                  int connectedClientID = connectingClients.get(new InetSocketAddress(receivedAddress, TCPportnum));
                  Socket clientSocket = new Socket(receivedAddress, receivedPort);
                  int secretKey = subscribers.get(connectedClientID);
                  Thread newTCPConnection = new Connection(clientSocket, connectedClientID, secretKey);
                  newTCPConnection.start();

                  //set user to connected
                  clientStates.put(connectedClientID, State.connected);
                  connectingClients.remove(new InetSocketAddress(receivedAddress, receivedPort));
                  break;
            
               default:
                  System.out.println("Unknown or invalid protocol message type received from: " + receivedAddress + ":" + receivedPort);
                  //TODO: print sender ID
                  System.out.println("Sender ID: ");
                  break;
            }
         }
         System.out.println("Closing server socket...");
         serverSocket.close();

      } catch (Exception e) {
         //TODO: handle exception
         System.out.println("Problem?");
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
   
}