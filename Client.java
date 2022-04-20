
/*
   Client represents any User that wishes to properly connect to the server
*/

enum State {
   offline, wait_challenge, wait_auth, connecting, connected
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
