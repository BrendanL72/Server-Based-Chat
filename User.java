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
import java.util.Scanner;


public class User {
   
   public static void main(String[] args) {
      //format: java User <ip> <user id>
      if (args.length != 2) { 
         System.out.println("try java User <dest ip> <user file>");
         System.exit(1);
      }

      DatagramPacket sendPacket;
      DatagramPacket receivedPacket;

      byte[] outBuf = new byte[512];
      int userID = 0;
      int secretKey = 0;

      //read in userID and secretkey from the text file
      try {
         FileReader userFile = new FileReader(args[1]);
         Scanner userFileInput = new Scanner(userFile);
         String userInfo = userFileInput.nextLine();
         String tokens[] = userInfo.split(" ");
         userID = Integer.parseInt(tokens[0]);
         secretKey = Integer.parseInt(tokens[1]);
      } catch (FileNotFoundException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      System.out.println("User ID: " + userID);
      System.out.println("Secret Key: " + secretKey);

      String message = "";
      String rcvMessage = "";
      String[] rcvTokens;
      String rcvMessageType = "";

      try {
         //CONNECTION PHASE
         InetAddress destIPaddress = InetAddress.getByName(args[0]);
         //create a new socket to send and receive data
         DatagramSocket clientSocket = new DatagramSocket();
         UDPMethods.printSocketInfo(clientSocket);

         //send HELLO
         message = "HELLO " + userID;
         UDPMethods.sendUDPPacket(message, clientSocket, destIPaddress, 4445);

         //wait for CHALLENGE(rand)
         receivedPacket = new DatagramPacket(outBuf, outBuf.length);
         clientSocket.receive(receivedPacket);
         rcvMessage = UDPMethods.byteToString(outBuf);
         rcvTokens = rcvMessage.split(" ");
         rcvMessageType = rcvTokens[0];

         System.out.println(rcvMessage);
         if (!UDPMethods.isExpectedMessage("CHALLENGE", 2, rcvMessage)) {
            System.exit(0);
         }

         //generate response using auth. for now it just returns the number it received
         int rand = Integer.parseInt(rcvTokens[1]);
         A3 hasher = new A3();
         String response = hasher.hash(secretKey, rand);

         //respond with RESPONSE(Res)
         message = "RESPONSE " + userID + " " + response;
         UDPMethods.sendUDPPacket(message, clientSocket, destIPaddress, 4445);

         //wait for AUTH message
         receivedPacket = new DatagramPacket(outBuf, outBuf.length);
         clientSocket.receive(receivedPacket);
         rcvMessage = UDPMethods.byteToString(outBuf);
         System.out.println(rcvMessage);
         
         rcvMessage = UDPMethods.byteToString(outBuf);
         rcvTokens = rcvMessage.split(" ");
         rcvMessageType = rcvTokens[0];
         int newPortNum = -1;

         //System.out.println(message);
         switch (rcvMessageType) {
            case "AUTH_FAIL":
               //commit die
               System.exit(0);
               break;

            case "AUTH_SUCCESS":
               //generate CK-A key and decrypt
               //parse port number and connect to TCP socket
               newPortNum = Integer.parseInt(rcvTokens[2]);
               //send CONNECTED datagram
               UDPMethods.sendUDPPacket("CONNECT", clientSocket, destIPaddress, 4445);
               break;
         
            default:
               System.out.println("Unexpected token. Expected: AUTH_FAIL or AUTH_SUCCESS");
               System.exit(1);
               break;
         }


         //establish TCP connection and send CONNECT
         System.out.println("Attempting TCP connection at " + destIPaddress + ":" + newPortNum);
         Socket socket = new Socket(destIPaddress, newPortNum);
         PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

         //wait for connected response message
         receivedPacket = new DatagramPacket(outBuf, outBuf.length);
         clientSocket.receive(receivedPacket);
         rcvMessage = UDPMethods.byteToString(outBuf);
         System.out.println(rcvMessage);

         //wait for CONNECTED signal
         System.out.println("TCP connection successful.");

         //CHAT SESSION SECTION
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
               if (currentlyChatting) {
                  System.out.println("You're already chatting with someone!");
                  break;
               }
               int destID = Integer.parseInt(userTokens[1]);
               //send request to server
               outStream.println("CHAT_REQUEST " + destID);
               
               //wait for server response
               message = inStream.readLine();
               String messageType = message.split(" ")[0];
               if (messageType.equals("CHAT_STARTED")) {
                  //chat started
                  System.out.println("Chat started");
                  currentlyChatting = true;
               }
               else if (messageType.equals("UNREACHABLE")) {
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
               //end connection with user B
               
            }
            else {
               //normal chat (change this)
               outStream.println(userInput);
            }
         }
         scanner.close();
         socket.close();

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
