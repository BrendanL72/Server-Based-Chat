/*
   User is the client-side part of the chat.
   User connects to the server and communicates with it using various messages 
   Note: for local testing, use localhost as the ip

   TODO:
   Remove HELLO and END message test code
   Create the universe
   Make an apple pie
*/

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Scanner;

public class User {
   
   public static void main(String[] args) {
      //format: java User <ip> <user id>
      if (args.length != 2) { 
         System.out.println("try java User <dest ip> <user id>");
         System.exit(1);
      }

      DatagramPacket sendPacket;
      DatagramPacket receivedPacket;

      byte[] inBuf = new byte[512];
      byte[] outBuf = new byte[512];

      final int userID = Integer.parseInt(args[1]);

      try {
         InetAddress destIPaddress = InetAddress.getByName(args[0]);
         //create a new socket to send and receive data
         DatagramSocket clientSocket = new DatagramSocket();
         printSocketInfo(clientSocket);

         //send HELLO
         sendUDPPacket(inBuf, "HELLO", clientSocket, destIPaddress, 4445);

         //wait for response
         receivedPacket = new DatagramPacket(outBuf, outBuf.length);
         clientSocket.receive(receivedPacket);

         System.out.println(byteToString(outBuf));

         //send END
         sendUDPPacket(inBuf, "END", clientSocket, destIPaddress, 4445);

         //wait for response
         receivedPacket = new DatagramPacket(outBuf, outBuf.length);
         clientSocket.receive(receivedPacket);

         System.out.println(byteToString(outBuf));

         //wait for CHALLENGE(rand)

         //respond with RESPONSE(Res)

         //wait for AUTH message

            //AUTH_FAIL

            //AUTH_SUCCESS

         //establish TCP connection and send CONNECT
         System.out.println("Attempting TCP connection...");

         //wait for CONNECTED signal

         System.out.println("TCP connection successful.");

         //Chat session
         Scanner scanner = new Scanner(System.in);
         String userInput = "";
         String[] userTokens;
         boolean currentlyChatting = false;

         System.out.println("To initiate chat, type: Chat <target user ID>");
         System.out.println("To logout, just type \"Log off\"");

         while (!userInput.equals("Log off")) {
            System.out.print(">");
            userInput = scanner.nextLine();
            userTokens = userInput.split(" ");
            if (userTokens[0] == "Chat") {
               int destID = Integer.parseInt(userTokens[1]);
               //send request to server
               
               //wait for server response
               if (currentlyChatting) {
                  //chat started
                  System.out.println("Chat started");
               }
               else if (!currentlyChatting) {
                  System.out.println("Sorry, user " + destID + " was not available.");
               }
               else {
                  System.out.println("ERROR: Server sent invalid message. Expected CHAT_STARTED or UNREACHABLE");
               }
            }
            else if (userInput == "Log off") {
               break;
            }
            else if (userInput == "End chat"){
               //normal chat
            }
         }

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

      System.out.println("Socket created under: " + serverIP + ":" + portNum);
   }

   private static String byteToString(byte[] buf) {
      String convert = new String(buf, Charset.forName("UTF-8"));
      return convert.trim();
   }

   private static void sendUDPPacket(byte[] buf, String message, DatagramSocket clientSocket, InetAddress destIP, int destPort) {
      try {
         buf = message.getBytes("UTF-8");
         DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, destIP, destPort);
         clientSocket.send(sendPacket);
      } catch (UnsupportedEncodingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
