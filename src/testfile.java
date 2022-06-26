package src;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class testfile {
   protected static int hi = 1;
   public static void main(String[] args) {
      BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
      Thread test = new TestThread(queue);
      test.start();
      while (true) {
         if (!queue.isEmpty()) {
            System.out.println(queue.remove());
         }
      }
   }
}

