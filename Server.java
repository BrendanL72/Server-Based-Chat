/*
   Server handles the various concurrent connections initiated by any number of clients.
   It does this by creating a thread for each connection initiated. 
   The thread will handle all messages to be sent and received by the client.
*/

import java.util.*;
import java.net.*;
import java.nio.charset.Charset;

public class Server {

   public static void main(String[] args) {
      final int MAX_ID = 1000000;
      final int MAX_SECRET_KEY = 1000000;

      //ID_keys keeps track of valid client IDs and their corresponding secret keys 
      Hashtable<Integer, Integer> ID_keys = new Hashtable<>();

      //get port number from command line arguments
      if (args.length != 1) {
         System.err.println("Try java Server <port number>");
         System.exit(1);
      } 
      int portNumber = Integer.parseInt(args[0]);

      //set up server
      try {
         DatagramSocket serverSocket = new DatagramSocket(portNumber);
         printServerInfo(serverSocket);

         byte[] inBuf = new byte[512];
         byte[] outBuf = new byte[512];

         DatagramPacket receivedPacket;
         DatagramPacket sendPacket;
         
         while (new String(inBuf, Charset.forName("UTF-8")).equals("END")) {
            receivedPacket = new DatagramPacket(inBuf, inBuf.length);
            serverSocket.receive(receivedPacket);

            //get information from packet to send to client
            InetAddress receivedAddress = receivedPacket.getAddress();
            int receivedPort = receivedPacket.getPort();

            //determine what to send them, for now it just returns what they send
            outBuf = inBuf;

            sendPacket = new DatagramPacket(outBuf, outBuf.length, receivedAddress, receivedPort);
            serverSocket.send(sendPacket);
         }

         serverSocket.close();

      } catch (Exception e) {
         //TODO: handle exception
      }
   }

   //simple debugging method that prints the server info
   private static void printServerInfo(DatagramSocket serverSocket) {
      InetAddress serverIP = serverSocket.getInetAddress();
      int portNum = serverSocket.getLocalPort();

      System.out.println("Server created under: " + serverIP + " " + portNum);
   }

   //simple debugging method that prints any client that connects to the socket unoffically
   private static void printClientInfo(Socket client) {
      InetAddress clientIP = client.getInetAddress();
      System.out.println("New client connected from: " + clientIP);
      
   }

   
}