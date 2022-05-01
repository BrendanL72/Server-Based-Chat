public class Message {
   
    public String message;
    public String messageType;

    public Message(String source, String m)
    {
        this.message = m;
        this.messageType = source;
    }
}
