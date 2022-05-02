import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.BlockingQueue;

public class UserTCPReader extends Thread{
    private Socket socket;
    BufferedReader inStream;
    BlockingQueue<Message> messageQueue;

    public UserTCPReader() {
        //*** defaults?
    }

    // use this one bc we already have the socket created but not the queue yet
    public UserTCPReader(Socket socket) {
        this.socket = socket;
    }

    public UserTCPReader(Socket s, BlockingQueue<Message> q) throws IOException
    {
        this.socket = s;
        this.messageQueue = q;
        //inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run()
    {
        String in = "";
        while(true)
        {
            try {
                inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                in = inStream.readLine();
                this.messageQueue.add(new Message("Server", in));
                // decrypt? ***
                //System.out.println("tcp mssg added to queue");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
