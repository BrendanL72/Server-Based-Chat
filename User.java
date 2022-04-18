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
      if (args.length != 2) { 
         System.out.println("try java User <ip> <port>");
         System.exit(1);
      }

      DatagramPacket sendPacket;
      DatagramPacket receivedPacket;

      byte[] inBuf = new byte[512];
      byte[] outBuf = new byte[512];

      try {
         InetAddress IPaddress = InetAddress.getByName(args[0]);
         int portNumber = Integer.parseInt(args[1]);
         DatagramSocket clientSocket = new DatagramSocket(portNumber, IPaddress);

         //send HELLO
         inBuf = "HELLO".getBytes("UTF-8");
         sendPacket = new DatagramPacket(inBuf, inBuf.length, IPaddress, portNumber);
         clientSocket.send(sendPacket);

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

}
