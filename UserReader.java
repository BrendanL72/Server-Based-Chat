/*
   UserReader is created by User to read in chat messages entered by the user.
*/

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class UserReader extends Thread{
   
   private BlockingQueue<Message> q;
   public UserReader(BlockingQueue<Message> q){
      this.q = q;
   }

   public void run() {
      Scanner scanner = new Scanner(System.in);
      String userInput = "";
      while (userInput != "Log off") {
<<<<<<< HEAD
         //System.out.print(">");
=======
         System.out.print("> ");
>>>>>>> 2d6e8100699aec68d757975e1172503738d378af
         //read inputs
         userInput = scanner.nextLine();
         //send user input to queue
         q.add(new Message("User", userInput));
      }
      //close thread
      
   }
}
