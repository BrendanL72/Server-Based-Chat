import java.io.IOException;
import java.net.Socket;

/*
   User is the client-side part of the chat. 
   User connects to the server and communicates with it using various messages 
   Note: for local testing, client will connect the localhost as the host 

   Features:
   Chat History
   Security
   
*/

import java.io.*;

public class User {
   
   public static void main(String[] args) {
      //format: java User <host> <port>
      String hostName = args[0];
      int portNumber = Integer.parseInt(args[1]);

      //try connection
      try {
         Socket clientSocket = new Socket(hostName, portNumber);

         //read initial ID and secret key from server

         //

      } catch (Exception e) {
         //TODO: handle exception
      }
      
   }

}
