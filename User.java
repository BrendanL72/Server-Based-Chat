/*
   User is the client-side part of the chat.
   User connects to the server and communicates with it using various messages 
   Note: for local testing, use localhost as the ip

   Features:
   Chat History
   Security
   
*/

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

public class User {
   
   public static void main(String[] args) {
      //format: java User <ip>
      if (args.length != 1) { 
         System.out.println("try java User <dest ip>");
         System.exit(1);
      }

      DatagramPacket sendPacket;
      DatagramPacket receivedPacket;

      byte[] inBuf = new byte[512];
      byte[] outBuf = new byte[512];

      try {
         InetAddress destIPaddress = InetAddress.getByName(args[0]);
         //create a new socket to send and receive data
         DatagramSocket clientSocket = new DatagramSocket();
         printSocketInfo(clientSocket);

         //send HELLO
         inBuf = "HELLO".getBytes("UTF-8");
         sendPacket = new DatagramPacket(inBuf, inBuf.length, destIPaddress, 4445);
         clientSocket.send(sendPacket);

         //wait for response
         receivedPacket = new DatagramPacket(outBuf, outBuf.length);
         clientSocket.receive(receivedPacket);

         System.out.println(byteToString(outBuf));

         //send END
         inBuf = "END".getBytes("UTF-8");
         sendPacket = new DatagramPacket(inBuf, inBuf.length, destIPaddress, 4445);
         clientSocket.send(sendPacket);

         //wait for response
         receivedPacket = new DatagramPacket(outBuf, outBuf.length);
         clientSocket.receive(receivedPacket);

         System.out.println(byteToString(outBuf));

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

   private static String byteToString(byte[] buf) {
      return new String(buf, Charset.forName("UTF-8"));
   }

}
