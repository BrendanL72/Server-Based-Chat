import java.util.ArrayList;
import java.util.Arrays;

/*
   Chat History helps keep track of all the messages sent in a session and the clients that were involved.
*/

public class ChatHistory {
   //keep track of the ids of all the participants
   private int[] chatters = new int[2];
   private int sessionID;
   ArrayList<String> messages;

   public ChatHistory(int[] chatters, int sessionID) throws IllegalArgumentException{
      if (chatters.length != this.chatters.length) {
         throw new IllegalArgumentException(chatters.toString());
      }
      this.chatters = chatters;
      this.sessionID = sessionID;
   }

   //Create a method that returns if a pair of IDs matches the chatters
   public boolean isSameClients(int[] chatters) {
      if (chatters.length != this.chatters.length) {
         return false;
      }
      return Arrays.asList(this.chatters).containsAll(Arrays.asList(chatters));
   }

   public ArrayList<String> getMessages() {
      return messages;
   }

   public int getSessionID() {
      return sessionID;
   }

   public void addMessage(String newMessage) {
      messages.add(newMessage);
   }
}
