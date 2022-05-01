import java.util.ArrayList;
import java.util.Arrays;

/*
   Session helps keep track of a session, including the clients and the message history.
*/

public class Session {
   //keep track of the ids of all the participants
   private int[] chatters = new int[2];
   private int sessionID;
   //change to keep track of who sent what
   ArrayList<String> chatHistory;

   public Session(int[] chatters, int sessionID) throws IllegalArgumentException{
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

   public ArrayList<String> getHistory() {
      return chatHistory;
   }

   public int getSessionID() {
      return sessionID;
   }

   public void recordMessage(String newMessage) {
      chatHistory.add(newMessage);
   }

   public int[] getMembers() {
      return chatters;
   }
}
