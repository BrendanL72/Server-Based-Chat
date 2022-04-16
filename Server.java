/*
   Server handles the various concurrent connections initiated by any number of clients.
   It does this by creating a thread for each connection initiated. 
   The thread will handle all messages to be sent and received by the client.
*/

import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

   public static void main(String[] args) {
      final int MAX_ID = 1000000;
      final int MAX_SECRET_KEY = 1000000;

      //ID_keys keeps track of valid client IDs and their corresponding secret keys 
      Hashtable<Integer, Integer> ID_keys = new Hashtable<>();

      //get port number from command line arguments
      if (args.length != 1) {
         System.err.println("Try java Server <port number>");
         System.exit(1);
      } 
      int portNumber = Integer.parseInt(args[0]);

      try {
         ServerSocket socket = new ServerSocket(portNumber);
         while (true) {
            Socket newClient = socket.accept();
            System.out.println("New client connected");
            //generate new client ID and secret key using hashing algo
            int newID = (int) Math.floor(Math.random() * MAX_ID);
            int newSecretKey = (int) Math.floor(Math.random() * MAX_SECRET_KEY);
            ID_keys.put(newID, newSecretKey);

            //create new client object to run
            Connection newConnection = new Connection(newID, newSecretKey);

            //run client object on thread
            Thread clientThread = new Thread(newConnection);
         }

         socket.close();

      } catch (Exception e) {
         //TODO: handle exception
      }
   }
}