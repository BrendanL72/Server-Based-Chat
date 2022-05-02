/*
   Should probably rename this lol

   Here is a bunch of helpful methods that were useful for both User and Server UDP Socket sending
   TODO: improve sendUDPPacket
*/

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Arrays;


public class UDPMethods {
   //determines if the message has the expected number of tokens and the correct header/type
   static boolean isExpectedMessage(String expectedToken, int expectedLength, String message) {
      String[] tokens = message.split(" ");
      if (!tokens[0].equals(expectedToken)) {
         System.out.println("Unexpected token. Expected: " + expectedToken);
         return false;
      }
      else if (tokens.length != expectedLength) {
         System.out.println("Too many arguments in " + expectedToken + " datagram. Found: " + tokens.length + " Expected: " + expectedLength);
         return false;
      }
      else {
         return true;
      }
   }

   static void printSocketInfo(DatagramSocket socket) {
      InetAddress serverIP = socket.getLocalAddress();
      int portNum = socket.getLocalPort();
   
      System.out.println("Socket created under: " + serverIP + ":" + portNum);
   }

   static void sendUDPPacket(String message, DatagramSocket socket, InetAddress destIP, int destPort) {
      try {
         byte[] buf = new byte[512];
         Arrays.fill(buf, (byte)0);
         buf = message.getBytes("UTF-8");
         DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, destIP, destPort);
         socket.send(sendPacket);
         System.out.println("Sent: " + message);
      } catch (UnsupportedEncodingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   //simple byte array to string conversion under UTF-8 format
   public static String byteToString(byte[] buf) {
      String convert = new String(buf, Charset.forName("UTF-8"));
      return convert.trim();
   }
   
}
