package src;

/*
   Client represents any User that wishes to properly connect to the server
   
   UPDATE:
   I've decided against using this as secret keys and client state are not related
*/

enum State {
   offline, wait_challenge, wait_auth, connecting, connected, wait_chat, chatting
}

public class Client {
   public State state = State.offline;
   public int ID_number;
   private int secretKey;

   public Client(int ID, int key) {
      this.ID_number = ID;
      this.secretKey = key;
   }
}
