/*
   Server handles the prospective connectors via UDP and the various concurrent connections initiated by any number of clients.
   It does this by creating a thread for each TCP connection initiated. 
   The thread will handle all messages to be sent and received by the client.

   TODO:
   Make the server keep track of Clients
   Create a file that keeps track of subs' secret keys using client IDs and then have Server read it in 
   Implement message response
   Create TCP connection for each succesfully connected User
*/

import java.util.*;
import java.util.concurrent.Semaphore;
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

      final int portNumber = 4445;

      //set up server
      try {
         DatagramSocket serverSocket = new DatagramSocket(portNumber);
         printServerInfo(serverSocket);

         DatagramPacket receivedPacket;
         DatagramPacket sendPacket;

         byte[] inBuf = new byte[512];
         
         //read datagrams 
         while (!(UDPMethods.byteToString(inBuf).equals("END"))) {
            //clear buffers after each packet
            inBuf = new byte[512];
            byte[] outBuf = new byte[512];
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
                        throw new Exception("Client " + clientID + "not found.");
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
                     UDPMethods.sendUDPPacket(outBuf, "CHALLENGE " + rand, serverSocket, receivedAddress, receivedPort);

                     //set user to waiting for authentification
                     clientStates.put(clientID, State.wait_auth);
   
                  } catch (Exception e) {
                     //TODO: handle exception
                     System.out.println(e);
                  }
                  break;

               case "RESPONSE":
                  //determine if response is valid or not
                  
                     //send AUTH_SUCCESS

                        //set user to connecting 

                     //send AUTH_FAIL

                        //set user to offline 
                  break;

               case "CONNECT":
                  //send CONNECTED and create TCP connection and thread

                  //set user to connected

                  break;
            
               default:
                  System.out.println("Unknown or invalid protocol message type received from: " + receivedAddress + ":" + receivedPort);
                  //TODO: print sender ID
                  System.out.println("Sender ID: ");
                  break;
            }
         }

         serverSocket.close();

      } catch (Exception e) {
         //TODO: handle exception
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