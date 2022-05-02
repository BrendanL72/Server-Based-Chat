/*
   ConnectionTCPReader is created by Connection.java to read in messages from the client
*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ConnectionTCPReader extends Thread{
   private BlockingQueue<Message> q;
   private Socket socket;
   public ConnectionTCPReader(Socket s, BlockingQueue<Message> q) {
      this.socket = s;
      this.q = q;
   }

   public void run() {
      try {
         BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         String clientMessage;
         while (true) { 
            clientMessage = inStream.readLine();

            q.add(new Message("Client", clientMessage));
         }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      
   }
}