import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class UserReader extends Thread{
   
   BlockingQueue<Message> q;
   public UserReader(BlockingQueue<Message> q){
      this.q = q;
   }

   public void run() {
      Scanner scanner = new Scanner(System.in);
      String userInput = "";
      while (userInput != "Log off") {
         System.out.println(">");
         //read inputs
         userInput = scanner.nextLine();
         //send user input to queue
         q.add(new Message("User", userInput));
      }
      //close thread
      
   }
}
