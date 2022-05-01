import java.util.Scanner;

public class UserReader extends Thread{
   
   public void run() {
      Scanner scanner = new Scanner(System.in);
      String userInput = "";
      while (userInput != "Log off") {
         System.out.println(">");
         //read inputs
         userInput = scanner.nextLine();
         //send user input to queue
      }
      //close thread
      
   }
}
