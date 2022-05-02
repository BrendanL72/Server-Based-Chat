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
<<<<<<< Updated upstream
         System.out.println("Running new TCP connection for" +  clientID + " at " + socket);
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
                  if (state != State.connected) {
                     System.out.println("Invalid request from " + clientID +  " due to state.");
                  }
                  else {
                     //determine if requested user is available
                     
                        //available
                        
                           //add new session

                           //change state to chatting
=======
         sendClient = new PrintWriter(socket.getOutputStream(), true);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      Session currentSession = null;

      //message parsing variables
      Message message = null;
      String actualMessage = "";
      String messageType = "";
      String messageOrigin = "";
      String[] tokens;

      //other parsing variables that java is being annoying about
      int targetClientID;

      // messageType is not message.messageType its the first element of actualMessage.split(" ")
      while (!messageType.equals("END_CONNECTION")) {
         try {
            message = messageQueue.take();
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         //finest Italian cuisine offered here
         actualMessage = message.message;
         messageOrigin = message.messageType;
         tokens = actualMessage.split(" ");
         messageType = tokens[0];

         System.out.println(actualMessage);

         switch (messageOrigin) {
            case "Client":
               switch (messageType) {
                  case "CHAT":
                     //format: CHAT <session ID> <chat message>
                     int sessionID = Integer.parseInt(tokens[1]);
                     if (state != Server.State.chatting) {
                        System.out.println("Ignoring chat message, user is not in a session.");
                        break;
                     }
                     //send message to session/other user
                        //get other user from sessions
                        int users[] = Server.getSession(sessionID).getMembers();
                        for (int userID : users) {
                           if (userID != this.clientID) {
                              //send the other user's connection the chat message
                              messageOtherConnection(userID, actualMessage);
                           }
                        }
                        
                     //add message to chat history
                     break;

                  case "CHAT_REQUEST":
                     //format: CHAT_REQUEST <dest client ID>
                     targetClientID = Integer.parseInt(tokens[1]);
                     Server.State targetUserState = Server.getUserState(targetClientID);

                     //determine if requested user is available 
                     //available
                     if (targetUserState == Server.State.connected) {
                           //create new session
                           int[] sessionUsers = new int[]{this.clientID, targetClientID};

                           //create unique session id
                           int newSessionID = 0;
                           do {
                              newSessionID = (int) Math.random()*1000000;
                           } while (Server.getSessionsIDs().contains(newSessionID));
                           
                           currentSession = new Session(sessionUsers, newSessionID);
                           Server.putSessionsHashtable(newSessionID, currentSession);
                           
                           //inform this connection's user
                           sendClient.println("CHAT_STARTED " + newSessionID + " " + targetClientID);

                           //inform the other user's connection
                           messageOtherConnection(targetClientID, "SESSION_STARTED " + newSessionID + " " + this.clientID);

                           //change state to chatting
                           this.state = Server.State.chatting;
                     }
                     //not available
                     else {
                        //send unreachable
                        sendClient.println("UNREACHABLE " + clientID);
                     }
                     break;

                  case "HISTORY_REQ":
                     //format: HISTORY_REQ <target client ID>

                     //determine target id

                     //get access to session history

                     //send history responses, one packet per line
                     break;

                  case "END_REQUEST":
                     //format: END_REQUEST <session-ID>
                     if (state != Server.State.chatting) {
                        System.out.println("Invalid request, not currently chatting with anyone");
                     }
                     else {
                        //get members of session
                        int[] sessionMembers = currentSession.getMembers();
                        
                        //inform other client that session has ended 
                        for (int userID : sessionMembers) {
                           if (userID != this.clientID) {
                              messageOtherConnection(userID, "SESSION_ENDED " + currentSession.getSessionID());
                           }
                        }

                        //set user state to connected
                        this.state = Server.State.connected;
                        //make current session none
                        currentSession = null;
                     }

                     break;
               
                  default:
                     System.out.println("Unfamiliar Client Message: " + actualMessage);
                     break;
               }
               break;
         
            case "Server":
               switch (messageType) {
                  case "CHAT":
                     System.out.println("Received chat: " + actualMessage);
                     //other client sent a chat, send it to our client
                     sendClient.println(actualMessage);
                     break;
<<<<<<< HEAD

=======
>>>>>>> 2d6e8100699aec68d757975e1172503738d378af
                  case "SESSION_STARTED":
                     //format SESSION_STARTED session_id clientID
                     //another client started a session with this user
                     int newSessionID = Integer.parseInt(tokens[1]);
                     targetClientID = Integer.parseInt(tokens[2]);

                     //add session as current session
                     currentSession = Server.getSession(newSessionID);

                     //inform this client about the started session 
                     sendClient.println("CHAT_STARTED " + newSessionID + " " + targetClientID);
                     //change state to active chat
                     this.state = Server.State.chatting;
                     break;

                  case "SESSION_ENDED":
                     //another client sent a request to end the session
                     //send packet to inform the user
                     int sessionID = Integer.parseInt(tokens[1]);
                     sendClient.println("END_NOTIF " + sessionID);
                     //change user state to connected
                     this.state = Server.State.connected;
                     break;
               
                  default:
                     break;
               }
               break;
            default:
               System.out.println("Unknown packet source");
               break;
         }
      }

      try {
         socket.close();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         System.out.println("Error with socket close");
         e.printStackTrace();
      }
   }
>>>>>>> Stashed changes

                        //not available

                           //do nothing?
                  }
                  break;

               case "HISTORY_REQ":
                  //determine target id

                  //get access 
                  //send history responses, one packet per line

                  
                  break;

               case "END_REQUEST":
                  if (state != State.chatting) {
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
