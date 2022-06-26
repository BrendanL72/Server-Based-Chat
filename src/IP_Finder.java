package src;
import java.net.*;
import java.util.Enumeration;

//run to figure out some IPs that you can use
public class IP_Finder {
   public static void main(String[] args) {
      Enumeration<NetworkInterface> e;
      try {
         e = NetworkInterface.getNetworkInterfaces();
         while(e.hasMoreElements())
         {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration<InetAddress> ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
               InetAddress i = (InetAddress) ee.nextElement();
               System.out.println(i.getHostAddress());
            }
         }
      } catch (SocketException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
   }
}
