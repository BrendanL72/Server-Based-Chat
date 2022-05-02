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
         int response = rand;

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

         //wait for connected response message
         outBuf = new byte[512];
         receivedPacket = new DatagramPacket(outBuf, outBuf.length);
         clientSocket.receive(receivedPacket);
         rcvMessage = UDPMethods.byteToString(outBuf);
         System.out.println(rcvMessage);

         //establish TCP connection and send CONNECT
         System.out.println("Attempting TCP connection...");
         Socket socket = new Socket(destIPaddress, newPortNum);
         PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

         //wait for CONNECTED signal
         System.out.println("TCP connection successful.");

         //CHAT SESSION SECTION
         Scanner scanner = new Scanner(System.in);
         //Message userInput;
         Message nextTask = new Message();
         String[] userTokens;
<<<<<<< Updated upstream
         boolean currentlyChatting = false;
=======
         //boolean currentlyChatting = false;
         BlockingQueue<Message> q = new LinkedBlockingQueue<Message>();
         int sessionID = 0;
         int partnerID = -1;
         PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);

>>>>>>> Stashed changes

         System.out.println("To initiate chat, type: Chat <target user ID>");
         System.out.println("To logout, just type \"Log off\"");

<<<<<<< Updated upstream
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
=======
         /**
          * this is where i've started implementing the chat phase
          */
         //thread for tcp input
         // thread for user input
         Thread tcpRead = new UserTCPReader(socket, q);
         tcpRead.start();
         Thread userRead = new UserReader(q);
         userRead.start();
         long sessionID = -1;
         int partnerID = -1;
         String input = "";

<<<<<<< HEAD
         PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);

//         if(!userInput.isEmpty)
//         {
//
//         }
         while (!input.toUpperCase().equals("LOG OFF")) {
            // outstream writes to server
            // instream reads from server
            
            
=======
         //userInput = scanner.nextLine();
         //System.out.println(userInput);
         //System.out.println(q.poll().message);

         /**
          * instead of user input, use Message userInput = q.take(); -> userInput.messageType.equals(server or client)
          * String userTokens = userInput.message.split(" ")
          * usertokens[0] -> switch blocks and such
          */

         //userInput = q.take

         while (!nextTask.message.toUpperCase().equals("LOG OFF")) {
            // create both data streams


            // create UserTCPReader thread (listening thread)
            //new UserTCPReader(socket);

            // outstream writes to server
            // instream reads from server

>>>>>>> 2d6e8100699aec68d757975e1172503738d378af
            //BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //String[] packetTokens;
            //String[] userTokens;
            //DECRYPT THE PACKET ***
            //String gotPacket = inStream.readLine();
            //q.add(new Message("Server", gotPacket));

            // pop off the queue?
            /**
             * user types initate chat
             * client writes chat_request
             * client reads chat_started
             * client display user chat started
             * user sends chat -> client writes to server -> writes to other client -> client displays to user
             * ^^ repeats
             * user sends end chat
             * client writes end_request to server
             * server writes end_notification to client
             * client displays chat ended to user
             */
System.out.println("success inside loop");
            try
            {
               //q.take() throws interrupted exception e
               nextTask = q.take();

               String messageType = nextTask.messageType;
<<<<<<< HEAD
               input = nextTask.message;

=======
               String input = nextTask.message;
System.out.println(messageType + ", " + input);
>>>>>>> 2d6e8100699aec68d757975e1172503738d378af
               if(messageType.toUpperCase().equals("SERVER"))
               {
                  //***DECRYPT PACKET
                  String[] packetTokens = input.split(" ");
<<<<<<< HEAD
                  switch(packetTokens[0])
=======
                  switch(packetTokens[0].toUpperCase())
>>>>>>> 2d6e8100699aec68d757975e1172503738d378af
                  {
                     case "UNREACHABLE":
                        // READ UNREACHABLE
                        // DISPLAY CORRESPONDENT UNREACHABLE
                        System.out.println("CORRESPONDENT UNREACHABLE");
                        break;
                     case "END_NOTIF":
                        // READ END_NOTIF SESSIONID
                        // DISPLAY CHAT SESSIONID ENDED
                        System.out.println("SESSION " +sessionID + " ENDED");
                        break;
                     case "CHAT_REQUEST":
                        // READ CHAT_REQUEST CLIENTID
                        // DISPLAY CHAT REQUEST CLIENTID
                        partnerID = Integer.valueOf(packetTokens[1]);
                        sessionID++;
                        System.out.println("CHAT REQUEST " + partnerID);
                        break;
                     case "CHAT_STARTED":
                        // READ CHAT_STARTED CLIENTID
                        // DISPLAY CHAT SESSIONID STARTED
                        sessionID = Integer.valueOf(packetTokens[1]);
                        System.out.println("CHAT " + sessionID + " STARTED");
                        break;
                     case "CHAT":
                        // READ CHAT SESSIONID MESSAGE
                        // DISPLAY @ MESSAGE
<<<<<<< HEAD
                        String chatMsg = "";
                        String author = packetTokens[2];
                        //System.out.println(Arrays.toString(packetTokens));
                        for (int i = 3; i < packetTokens.length; i++) {
                           chatMsg = chatMsg.concat(packetTokens[i] + " ");
                        }
                        System.out.println(author + ": " + chatMsg);
                        //System.out.println(packetTokens[2]);
=======
                        //System.out.println("@" + packetTokens[2]);
                        System.out.println("@" + input);
                        //System.out.println(packetTokens[]);
>>>>>>> 2d6e8100699aec68d757975e1172503738d378af
                        break;
                     case "HISTORY_RESP":
                        // READ HISTORY_RESP SENDINGCLIENTID MESSAGE
                        // DISPLAY SENDINGCLIENT MESSAGE (1111 @HI THERE)
                        System.out.println("CHAT HISTORY: *** NOT YET WORKING");
                        System.out.println(""/**CHAT HISTORY*/);
                        break;
                     
                     default:
<<<<<<< HEAD
                        System.out.println("Unrecognized packet type: " + input);
                        break;
                  }
               }
               else if(messageType.equals("User")) {
                  userTokens = input.split(" ");
                  switch (userTokens[0]) {
=======
//                        String userMssg = "";
//
//                        // int i = 1; ?
//                        for (int i = 0; i < packetTokens.length; i++) {
//                           userMssg.concat(packetTokens[i] + " ");
//                        }
                        //***ENCRYPT PACKET
                        outStream.println("CHAT " + sessionID + " " + nextTask.message);
                        System.out.println("CHAT " + sessionID + " " + nextTask.message);
                        break;
                  }
               }
               else if(messageType.toUpperCase().equals("USER")) {
                  userTokens = input.split(" ");
                  switch (userTokens[0].toUpperCase()) {
                     case "LOG":
                        // USER SENDS LOG
                        // WRITE END_CONNECTION CLIENTID
                        outStream.write("END_REQUEST " + userID);
                        System.out.println("END_REQUEST " + userID);
                        break;
>>>>>>> 2d6e8100699aec68d757975e1172503738d378af
                     case "END":
                        // USER SENDS END CHAT
                        // WRITE END_REQUEST SESSIONID

                        //***ENCRYPT PACKET
                        outStream.println("END_REQUEST " + sessionID);
                        System.out.println("END_REQUEST " + sessionID);
                        //outStream.println(new Message());
                        break;
                     case "CHAT":
                        // USER SENDS CHAT CLIENTID B
                        // WRITE CHAT_REQUEST CLIENTID MESSAGE
                        //Date d = new Date(95, 1, 15);
                        //sessionID = d.getTime();

                        //TODO: error checking for things out of format

                        partnerID = Integer.valueOf(userTokens[1]);

                        //***ENCRYPT PACKET
                        outStream.println("CHAT_REQUEST " + partnerID);
                        System.out.println("CHAT_REQUEST " + partnerID);
                        break;
                     case "@":
                        // SENDING MESSAGES BETWEEN CLIENTS
                        // USER SENDS @ MESSAGE
                        // WRITE CHAT SESSIONID MESSAGE
//
//                        String userMssg = "";
//
//                        // int i = 1; ?
//                        for (int i = 2; i < userTokens.length; i++) {
//                           userMssg.concat(userTokens[i] + " ");
//                        }
//                        //***ENCRYPT PACKET

<<<<<<< HEAD
                        /* System.out.println("Sending message...");

                        String userMssg = "";

                        for (int i = 2; i < userTokens.length; i++) {
                           userMssg.concat(userTokens[i] + " ");
                        }
                        //***ENCRYPT PACKET
                        outStream.println("CHAT " + sessionID + input); */
=======
                        outStream.println("CHAT " + sessionID + " " + input);
                        System.out.println("CHAT " + sessionID + " " + input);
>>>>>>> 2d6e8100699aec68d757975e1172503738d378af
                        break;
                     case "HISTORY":
                        // USER SENDS SHOW HISTORY
                        // WRITE HISTORY_REQ CLIENTID

                        //***ENCRYPT PACKET

                        break;

                     case "LOG":
                        System.out.println(input);
                        if (input.equals("LOG OFF")) {
                           outStream.println("END_CONNECTION");
                           break;
                        }
                        break;
                     default:
<<<<<<< HEAD
                        //KEEP WAITING FOR USER INPUT

                        String userMssg = "";

                        for (int i = 2; i < userTokens.length; i++) {
                           userMssg.concat(userTokens[i] + " ");
                        }
                        //***ENCRYPT PACKET
                        
                        outStream.println("CHAT " + sessionID + " " + userID + " " + input);
=======
                        // SENDING MESSAGES BETWEEN CLIENTS
                        // USER SENDS @ MESSAGE
                        // WRITE CHAT SESSIONID MESSAGE

                        // input is message.message
//                        String userMssg = "";
//
//                        // int i = 1; ?
//                        for (int i = 0; i < userTokens.length; i++) {
//                           userMssg.concat(userTokens[i] + " ");
//                        }
                        //***ENCRYPT PACKET
                        outStream.println("CHAT " + sessionID + " " + input);
                        System.out.println("CHAT " + sessionID + " " + input);
>>>>>>> 2d6e8100699aec68d757975e1172503738d378af
                        break;
                  }
>>>>>>> Stashed changes
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
