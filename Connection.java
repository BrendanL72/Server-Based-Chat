/*
Connection is created by the server when a new client connects to the server.
A new thread is created to handle each connection.
*/

public class Connection implements Runnable{
   final int clientID;
   final int secretKey;

   public Connection(int ID, int key) {
      clientID = ID;
      secretKey = key;
   }

   public void run() {
      System.out.println("Connection " + clientID + " established.");
      //send client 
   }


}
