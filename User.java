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
import java.util.*;
import java.util.concurrent.BlockingQueue;

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

         // dont need this anymore?????
         //establish TCP connection and send CONNECT
         System.out.println("Attempting TCP connection at " + destIPaddress + ":" + newPortNum);
         Socket socket = new Socket(destIPaddress, newPortNum);
         //PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
         //BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

         //wait for connected response message
         receivedPacket = new DatagramPacket(outBuf, outBuf.length);
         clientSocket.receive(receivedPacket);
         rcvMessage = UDPMethods.byteToString(outBuf);
         System.out.println(rcvMessage);

         //wait for CONNECTED signal
         System.out.println("TCP connection successful.");

/////////////////////////////////////////////////////////////////////////
         //CHAT SESSION SECTION
/////////////////////////////////////////////////////////////////////////

         Scanner scanner = new Scanner(System.in);
         String userInput = "";
         String[] userTokens;
         //boolean currentlyChatting = false;
         BlockingQueue<Message> q = new LinkedBlockingQueue<Message>();

         System.out.println("To initiate chat, type: Chat <target user ID>");
         System.out.println("To logout, just type \"Log off\"");

         /**
          * this is where i've started implementing the chat phase
          */
         //thread for tcp input
         // thread for user input
         new UserTCPReader(socket, q);
         new UserReader(q);

         userInput = scanner.nextLine();

//         if(!userInput.isEmpty)
//         {
//
//         }
         while (!userInput.equals("Log off")) {
            // create both data streams


            // create UserTCPReader thread (listening thread)
            //new UserTCPReader(socket);

            // outstream writes to server
            // instream reads from server
            long sessionID = -1;
            int partnerID = -1;
            PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String[] packetTokens;

            userInput = scanner.nextLine();
            userTokens = userInput.split(" ");
            //sendMessage(userInput);

            // how do i implement the queue ?? ****

            /** listens to packets from server */
            //try
            //{
               // DECRYPT THE PACKET***
               String gotPacket = inStream.readLine();
               gotPacket = receivePacket(new Message("TCP", gotPacket));
               // SHOULD WE HAVE A ISCHATTING BOOL? no
               packetTokens = gotPacket.split(" ");

               if(packetTokens[0].toUpperCase().equals("UNREACHABLE"))
               {
                  System.out.println("Correspondent unreachable");
                  break;
               }
               else if(packetTokens[0].toUpperCase().equals("END_NOTIF"))
               {
                  // CALL SEND PACKET FUNCTION
                  System.out.println("Chat ended");
                  break;
               }
               else if(packetTokens[0].toUpperCase().equals("CHAT_STARTED"))
               {
                  Date d = new Date(95, 1, 15);

                  System.out.println("Chat started");
                  sessionID = d.getTime();
                  System.out.println("Session ID: " + sessionID);
                  break;
               }

//            }
//            catch()
//            {
//
//            }

            /** looks at user input*/
            //try
            //{
               // encrypt tcp mssg
               // write object to server with tcp
               // read from server
               // decrypt



               if(userTokens[0].toUpperCase().equals("HISTORY"))
               {
                  // HISTORY REQUEST FUNCTION
                  // send packet to server to request chat history
               }
               else if(userTokens[0].toUpperCase().equals("END") && userTokens[1].toUpperCase().equals("CHAT"))
               {
                  // END_REQUEST FUNCTION
                  // send packet to server to destroy tcp connection
                  break;
               }
               else if(userTokens[0].toUpperCase().equals("CHAT"))
               {
                  //CHAT_STARTED FUNCTION
                  // wait for input from user
                  break;
               }
               //else
               //{
                  //maybe now it can be send packet (send to other client)
                  //String encryptedChat = sendPacket(userInput);
                  //*** send tcp packet
               //}

               String encryptedChat = sendPacket(new Message("USER", gotPacket));
            //}
//            catch()
//            {
//
//            }


            // while(true) {} or while(!userinput.equals("log off")
            // .writeobject() sends mssg to server
            // sendmessage(new Message("USER",userinput))

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


   // lol might not need this either
// kidding maybe make this return a string
   public static String sendPacket(Message msg)
   {
//   try{
      // encrypt tcp mssg

      //A8 encrypt = new A8();
      //int key = encrypt.cipherKey(secretKey, rand)

      // write object to server with tcp


      return "-1";
//   }
//   catch()
//   {
//
//   }
      // once message is done processing, remove from queue (someone else remind me of the syntax for that?)
   }

   // kidding maybe we dont need this
   // kidding maybe make this return a string
   public static String receivePacket(Message msg)
   {
//   try{
      // read from server
      // decrypt packet msg

      // A8 decrypt = newA8();
      // int key = decrypt.cipherKey(secretKey, rand)

      // return decrypted msg?


      return "-1";
//   }
//   catch()
//   {
//
//   }
   }
}



/*
while (!userInput.equals("Log off")) {
   userInput = scanner.nextLine();
   userTokens = userInput.split(" ");
            if (userTokens[0].equalsIgnoreCase("Chat")) {
      if (currentlyChatting) {
         System.out.println("You're already chatting with someone!");
         break;
      }
      int destID = Integer.parseInt(userTokens[1]);
      //send request to server
      outStream.println("CHAT_REQUEST " + destID);
      System.out.println("SENT CHAT REQUEST ");
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
}*/
