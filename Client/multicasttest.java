package Client;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;

public class multicasttest {

    public static void main(String[] args) {
        
        try {
            int niIndex = Integer.parseInt(args[0]);
            DatagramChannel channel = DatagramChannel.open().bind(new
            InetSocketAddress(9001));
            channel.join(InetAddress.getByName("225.123.123.123"), 
                         NetworkInterface.getByIndex(niIndex));
            channel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress("225.123.123.123",
                                                              9001);

            int i = 0;
            while (true) {
                ByteBuffer in = ByteBuffer.allocate(4);
                SocketAddress s = channel.receive(in);
                in.position(0);
                if (s != null) {
                    System.out.println(in.getInt());
                }

                ByteBuffer out = ByteBuffer.allocate(4);
                out.putInt(i);
                channel.send(out, address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
