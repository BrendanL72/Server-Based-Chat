import java.util.Scanner;

public class testfile {
   public static void main(String[] args) {
      Scanner scanner = new Scanner(System.in);
      int i = 0;
      while (true) {
         if (scanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
         }
         else {
            System.out.println(i++);
         }
      }
   }
}
