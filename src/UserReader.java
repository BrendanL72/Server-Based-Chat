package src;
/*
   UserReader is created by User to read in chat messages entered by the user.
*/

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class UserReader extends Thread{
   private Scanner scanner;

   private BlockingQueue<Message> q;
   public UserReader(Scanner scanner, BlockingQueue<Message> q){
      this.q = q;
      this.scanner = scanner;
   }

   public void run() {
      String userInput = "";
      while (userInput != "Log off") {
         //System.out.print(">");
         //read inputs
         userInput = scanner.nextLine();
         //send user input to queue
         q.add(new Message("User", userInput));
      }
      //close thread
      
   }
}
