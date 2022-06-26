package src;


public class test {
    public static void main(String[] args) {
    
        State state = State.chatting;
        State state2 = State.chatting;
        
        System.out.println(state.equals(state2));
    
    }
}
