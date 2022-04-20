/*
   Connection represents the server-side handling of a client's chat phase.
   It is created by the server when a new client successfully connects to the server.

*/

import java.util.*;
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
         while (!messageType.equals("END_REQUEST")) {
            message = rcvClient.readLine();
            messageType = message.split(" ")[0];
            switch (messageType) {
               case "CHAT REQUEST":
                  //determine if requested user is available

                     //available

                     //not available
                  break;

               case "HISTORY_REQ":
                  //send history response
                  break;

               case "END_REQUEST":
                  //exit loop
                  break;
            
               default:
                  System.out.println("ERROR: Invalid or unrecognized message type: " + messageType);
                  System.out.println("Message: " + message);
                  break;
            }
      }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         System.out.println("ERROR: Client " + clientID +  " input or output not found.");
         e.printStackTrace();
      }
      
      

      System.out.println("TCP connection with " + clientID + " closed");
      
   }


}
