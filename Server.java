/*
   Server handles the various concurrent connections initiated by any number of clients.
   It does this by creating a thread for each connection initiated. 
   The thread will handle all messages to be sent and received by the client.

   TODO:
   Make the server keep track of Clients
   Implement message response
   Create TCP connection for each succesfully connected User
*/

import java.util.*;
import java.net.*;
import java.nio.charset.Charset;

public class Server {

   public static void main(String[] args) {
      final int MAX_ID = 1000000;
      final int MAX_SECRET_KEY = 1000000;

      //ID_keys keeps track of valid client IDs and their corresponding secret keys 
      Hashtable<Integer, Client> ID_keys = new Hashtable<>();
      //Keep track of all chat histories
      Hashtable<Integer, Session> chatHistories = new Hashtable<>();

      //get port number from command line arguments
      final int portNumber = 4445;

      //set up server
      try {
         DatagramSocket serverSocket = new DatagramSocket(portNumber);
         printServerInfo(serverSocket);

         DatagramPacket receivedPacket;
         DatagramPacket sendPacket;

         byte[] inBuf = new byte[512];
         
         //read datagrams 
         while (!(byteToString(inBuf).equals("END"))) {
            //clear buffers after each packet
            inBuf = new byte[512];
            byte[] outBuf = new byte[512];
            //System.out.println("Looking for packets...");
            receivedPacket = new DatagramPacket(inBuf, inBuf.length);
            serverSocket.receive(receivedPacket);

            //get information from packet to send to client
            InetAddress receivedAddress = receivedPacket.getAddress();
            int receivedPort = receivedPacket.getPort();
            System.out.println("Packet received from: " + receivedAddress + ":" + receivedPort);

            String message = byteToString(inBuf);
            String messageType = message.split(" ")[0];
            System.out.println(message);

            //determine what to send them, for now it just returns what they send
            outBuf = inBuf;
            sendPacket = new DatagramPacket(outBuf, outBuf.length, receivedAddress, receivedPort);
            serverSocket.send(sendPacket);

            //determine what to do based on sender's current state and message
            switch (messageType) {
               case "HELLO":
                  //add user to memory
                  //send CHALLENGE

                  //set user to wait_auth
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

   //simple byte array to string conversion under UTF-8 format
   private static String byteToString(byte[] buf) {
      String convert = new String(buf, Charset.forName("UTF-8"));
      return convert.trim();
   }

   //simple debugging method that prints the server info
   private static void printServerInfo(DatagramSocket serverSocket) {
      InetAddress serverIP = serverSocket.getLocalAddress();
      int portNum = serverSocket.getLocalPort();

      System.out.println("Server created under: " + serverIP + ":" + portNum);
   }

   //simple debugging method that prints any client that connects to the socket unoffically
   private static void printClientInfo(Socket client) {
      InetAddress clientIP = client.getInetAddress();
      System.out.println("New client connected from: " + clientIP);
      
   }
   
}