package Client;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;

public class multicastsendtest {

    public static void main(String[] args) {
        
        try {
            DatagramChannel channel = DatagramChannel.open().bind(new InetSocketAddress(9999));
            channel.join(InetAddress.getByName("225.123.123.123"), 
                         NetworkInterface.getByIndex(1));
            channel.configureBlocking(false);

            InetSocketAddress address = new InetSocketAddress("225.123.123.123", 9999);

            for (int i = 0; i < 100000; i++) {
                ByteBuffer out = ByteBuffer.allocate(4);
                out.putInt(i);
                out.position(0);
                channel.send(out, address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}

