package src;
import java.util.concurrent.BlockingQueue;

public class TestThread extends Thread{
   BlockingQueue<Integer> q;
   public TestThread(BlockingQueue<Integer> q) {
      this.q = q;
   }

   public void run() {
      System.out.println(testfile.hi);
      System.out.println("hi");
      q.add(10);
   }
}
