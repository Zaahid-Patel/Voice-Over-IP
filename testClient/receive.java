package testClient;
import javax.sound.sampled.*;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;

import java.util.*;

public class receive {
    static DatagramChannel channel;
    static InetSocketAddress multiAddress;

    static TargetDataLine inLine;
    static SourceDataLine outLine;

    static HashMap<InetSocketAddress, LinkedList<byte[]> > sourceFrames;

    static boolean running = true;
    static int counter = 0;

    static int frameSize;
    static float frameRate;
    public static void main(String[] args) {
        try {

            // Create a multicast socket and join the multicast group
            MulticastSocket socket = new MulticastSocket(9999);
            InetSocketAddress groupAddress = new InetSocketAddress("225.123.123.123", 9999);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

            System.out.println(networkInterface);
            socket.joinGroup(groupAddress, networkInterface);

            AudioFormat format = new AudioFormat(8000, 16, 1, true, true);
            frameSize = format.getFrameSize();
            frameRate = format.getFrameRate();

            outLine = AudioSystem.getSourceDataLine(format);
            outLine.open(format);

            outLine.start();
            int packetSize = frameSize * 10; // 10 Frames per packet

            ByteBuffer recBuf = ByteBuffer.allocate(packetSize);

            while (running) {
                recBuf.clear();
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                outLine.write(packet.getData(), 0, packetSize);
            }
            
            // Leave the multicast group and close the socket
            socket.leaveGroup(groupAddress, networkInterface);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
