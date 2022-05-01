import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.BlockingQueue;

public class UserTCPReader extends Thread{
    private Socket socket;
    BufferedReader inStream;
    BlockingQueue<Message> messageQueue;

    public UserTCPReader(Socket s, BlockingQueue<Message> q) throws IOException
    {
        this.socket = s;
        this.messageQueue = q;
        inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run()
    {
        while(true)
        {
            try {
                messageQueue.add(new Message("TCP", inStream.readLine()));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
