/*
   User is the client-side part of the chat.
   User connects to the server and communicates with it using various messages 
   Note: for local testing, client will connect the localhost as the host 

   Features:
   Chat History
   Security
   
*/

import java.io.*;
import java.net.*;

public class User {
   
   public static void main(String[] args) {
      //format: java User <ip> <port>
      if (args.length != 3) { 
         System.out.println("try java User <self port> <dest ip> <dest port>");
         System.exit(1);
      }

      DatagramPacket sendPacket;
      DatagramPacket receivedPacket;

      byte[] inBuf = new byte[512];
      byte[] outBuf = new byte[512];

      try {
         int portNumber = Integer.parseInt(args[0]);
         InetAddress IPaddress = InetAddress.getLocalHost();

         InetAddress destIPaddress = InetAddress.getByName(args[1]);
         int destPortNumber = Integer.parseInt(args[2]);
         //create a new socket to send and receive data
         DatagramSocket clientSocket = new DatagramSocket(portNumber, IPaddress);
         printSocketInfo(clientSocket);

         //send HELLO
         inBuf = "HELLO".getBytes("UTF-8");
         sendPacket = new DatagramPacket(inBuf, inBuf.length, destIPaddress, destPortNumber);
         clientSocket.send(sendPacket);

         //wait for response
         receivedPacket = new DatagramPacket(outBuf, outBuf.length);
         clientSocket.receive(receivedPacket);

         //send END
         inBuf = "END".getBytes("UTF-8");
         sendPacket = new DatagramPacket(inBuf, inBuf.length, IPaddress, portNumber);
         clientSocket.send(sendPacket);

      } catch (SocketException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (UnknownHostException e) {
         // Occurs when ip address given could not be found
         e.printStackTrace();
      } catch (IOException e) {
         // Created by clientSocket send
         e.printStackTrace();
      }
      
   }

   private static void printSocketInfo(DatagramSocket socket) {
      InetAddress serverIP = socket.getLocalAddress();
      int portNum = socket.getLocalPort();

      System.out.println("Socket created under: " + serverIP + " " + portNum);
   }

}
