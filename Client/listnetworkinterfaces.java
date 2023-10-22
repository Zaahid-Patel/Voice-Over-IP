package Client;
import java.net.*;
import java.util.*;

public class listnetworkinterfaces {

    public static void main(String[] args) {
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = e.nextElement();
                System.out.println(n.getDisplayName() + " :\t" + n.getIndex());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
