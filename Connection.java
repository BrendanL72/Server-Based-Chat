/*
   Connection represents the server-side handling of a client's chat phase.
   It is created by the server when a new client successfully connects to the server and the server creates a TCP sockett to communicate with them

*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Thread;
import java.net.*;
import java.util.*;


public class Connection extends Thread{

  

   private Server.State state;
   private Socket socket;
   private final int clientID;
   private final int secretKey;

   public Connection(Socket socket, int clientID, int secretKey) {
      state = Server.State.connected;
      this.socket = socket;
      this.clientID = clientID;
      //sets name of thread to the client ID 
      this.setName(Integer.toString(clientID));
      this.secretKey = secretKey;
   }

   public void run() {
      try {
         System.out.println("Running new TCP connection for " +  clientID + " at " + socket);
         PrintWriter sendClient = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader rcvClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         System.out.println("New TCP connection created.");
         String message = "";
         String messageType = "";
         String[] tokens;

         //service any messages sent by the user
         while (!messageType.equals("END_REQUEST")) {
            //read message and split into tokens
            message = rcvClient.readLine();
            tokens = message.split(" ");
            messageType = tokens[0];

            //determine behavior based on message header
            switch (messageType) {
               case "CHAT":
                  //format CHAT <session ID> <chat message>
                  int sessionID = Integer.parseInt(tokens[1]);
                  String chatMessage = tokens[2];
                  //send message to session/other user
                  
                  //add message to chat history
                  

                  break;
               case "CHAT_REQUEST":
               System.out.println("IN CHAT REQUEST");
               //check if connection is connected
                  if (state != Server.State.connected) {
                     System.out.println("Invalid request from " + clientID +  " due to state.");
                  }
                  else {

                     //determine if requested user is available
                     int destID = Integer.parseInt(tokens[1]);
                     Server.State destState = Server.getClientStatesHashtable(destID); // destination's state
                     Server.State shouldBe = Server.getClientStatesHashtable(clientID);// "connected"
                     if(destState.equals(shouldBe)){
                       //available
                           System.out.println("INSIDE AVAILABLE");
                           //add new session
                           int[] chatters = {clientID, Integer.parseInt(tokens[1])}; //int[2] of both chatters clientIDs
                           Session session = new Session(chatters, ++Server.sessionCounter);
                           Server.putSessionsHashtable(Server.sessionCounter, session);
                           
                           //change state to chatting
                           Server.putClientStatesHashtable(clientID, Server.State.chatting);
                           Server.putClientStatesHashtable(Integer.parseInt(tokens[1]), Server.State.chatting);
                           
                           //Send CHAT_STARTED
                           sendClient.println("CHAT_STARTED " + Server.sessionCounter + " " + destID);
                          

                     }else{
                        //not available
                        System.out.println("Client-ID " + tokens[1] + " is already in a chat");
                     }
                        

                  }
                  break;

               case "HISTORY_REQ":
                  //determine target id

                  //get access 
                  //send history responses, one packet per line

                  
                  break;

               case "END_REQUEST":
                  if (state != Server.State.chatting) {
                     System.out.println("Invalid request, not currently chatting with anyone");
                  }
                  else {
                     //skip to end of while loop to exit loop
                     continue;
                  }
                  break;
                  
               default:
                  System.out.println("ERROR: Invalid or unrecognized message type: " + messageType);
                  System.out.println("Message: " + message);
                  break;
            }

            //end connection with client
          //  socket.close();
          //  System.out.println("TCP connection with " + clientID + " closed");
      }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         System.out.println("ERROR: Client " + clientID +  " input or output not found.");
         e.printStackTrace();
      }
      
   }


}
