/*
   Connection represents the server-side handling of a client's chat phase.
   It is created by the server when a new client successfully connects to the server and the server creates a TCP sockett to communicate with them

   TODO:
   create two threads to handle client TCP messages and messages from the server 
*/

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;


public class Connection extends Thread{

   private Server.State state;
   private Socket socket;
   private final int clientID;
   private final int secretKey;
   private PrintWriter sendClient;
   private BlockingQueue<Message> messageQueue;
   
   public Connection(Socket socket, int clientID, int secretKey) {
      this.state = Server.State.connected;
      this.socket = socket;
      this.clientID = clientID;
      //sets name of thread to the client ID 
      this.setName(Integer.toString(clientID));
      this.secretKey = secretKey;
      messageQueue = new LinkedBlockingQueue<Message>();
   }
   

   public void run() {
      System.out.println("Running new TCP connection for " +  clientID + " at " + socket);
      //create thread to wait for client messages and them to the message queue
      Thread clientMessageHandler = new ConnectionTCPReader(socket, messageQueue);
      clientMessageHandler.start();

      //write to TCP connections
      try {
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
                     String chatMessage = tokens[2];
                     //send message to session/other user
                        //get other user from sessions
                        int users[] = Server.getSession(sessionID).getMembers();
                        for (int userID : users) {
                           if (userID != this.clientID) {
                              //send the other user's connection the chat message
                              messageOtherConnection(userID, chatMessage);
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
                     //other client sent a chat, send it to our client
                     sendClient.println(actualMessage);

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
   }

   public BlockingQueue<Message> getMessageQueue() {
      return messageQueue;
   }

   public Server.State getUserState() {
      return this.state;
   }

   private static void messageOtherConnection(int targetClientID, String message) {
      Server.getActiveConnection(targetClientID).messageQueue.add(new Message("Server", message));
   }

}
