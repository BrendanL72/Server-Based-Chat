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


public class Connection extends Thread{

   enum State {
      connected, wait_chat, chatting
   }

   private State state;
   private Socket socket;
   private final int clientID;
   private final int secretKey;

   public Connection(Socket socket, int clientID, int secretKey) {
      state = State.connected;
      this.socket = socket;
      this.clientID = clientID;
      //sets name of thread to the client ID 
      this.setName(Integer.toString(clientID));
      this.secretKey = secretKey;
   }

   public void run() {
      try {
         PrintWriter sendClient = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader rcvClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         System.out.println("New TCP connection created.");
         String message = "";
         String messageType = "";
         String[] tokens;
         while (!messageType.equals("END_REQUEST")) {
            message = rcvClient.readLine();
            tokens = message.split(" ");
            messageType = tokens[0];
            switch (messageType) {
               case "CHAT":
                  //format CHAT <session ID> <chat message>
                  
                  //add message to chat history

                  break;
               case "CHAT_REQUEST":
                  if (state != State.connected) {
                     System.out.println("Invalid request from " + clientID +  " due to state.");
                  }
                  else {
                     //determine if requested user is available
                     
                        //available
                        
                           //add new session

                           //change state to chatting 

                        //not available
                  }
                  break;

               case "HISTORY_REQ":
                  //determine target id

                  //send history responses, one packet per line

                  
                  break;

               case "END_REQUEST":
                  if (state != State.chatting) {
                     System.out.println("Invalid request, not currently chatting with anyone");
                  }
                  else {
                     //exit loop
                     break;
                  }
                  
               default:
                  System.out.println("ERROR: Invalid or unrecognized message type: " + messageType);
                  System.out.println("Message: " + message);
                  break;
            }

            //end connection with client
            socket.close();
            System.out.println("TCP connection with " + clientID + " closed");
      }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         System.out.println("ERROR: Client " + clientID +  " input or output not found.");
         e.printStackTrace();
      }
      
   }


}
