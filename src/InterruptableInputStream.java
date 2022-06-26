package src;
import java.io.*;
import java.util.concurrent.Callable;



public class InterruptableInputStream implements Callable<String> {

    public InterruptableInputStream(){
    }   

  public String call() throws IOException {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(System.in));
    System.out.println("ConsoleInputReadTask run() called.");
    String input;
    do {
      System.out.println(">");
      try {
        // wait until we have data to complete a readLine()
        while (!br.ready()  /*  ADD SHUTDOWN CHECK HERE */) {
            System.out.println("Thread sleepin");
            Thread.sleep(200);
            
        }
        input = br.readLine();
      } catch (InterruptedException e) {
        System.out.println("ConsoleInputReadTask() cancelled");
        return null;
      }
    } while ("".equals(input));
    System.out.println("Thank You for providing input!");
    return input;
  }
}