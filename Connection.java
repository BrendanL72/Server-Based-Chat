/*
   Connection is created by the server when a new client connects to the server.
   A new thread is created to handle each connection.

   UPDATE: This class is pointless, as we use UDP, which does not require a listening socket for each connection
*/

import java.util.*;
import java.lang.Thread;
import java.net.*;

public class Connection extends Thread{

   public void run() {
      final int clientID;
      final int secretKey;
      DatagramPacket packet;
      DatagramSocket socket;

      System.out.println("New connection created.");
      //wait for HELLO message from client

      //send CHALLENGE message using UDP

      //wait for RESPONSE message 

      //determine if RESPONSE was correct

         //incorrect, send AUTH_FAIL

      //correct, send AUTH_SUCC and generate encrypt key

      //wait for CONNECT(rand_cookie)

      //keep servicing messages until LOGOUT message is sent
      String message = "";
      while (!message.equals("Log out ")) {

      }

      
   }


}
