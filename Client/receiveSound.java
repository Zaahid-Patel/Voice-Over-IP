package Client;
import javax.sound.sampled.*;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;

import java.util.*;
public class receiveSound extends Thread {

    DatagramChannel channel;
    InetSocketAddress multiAddress;

    SourceDataLine outLine;

    HashMap<InetSocketAddress, LinkedList<byte[]> > sourceFrames;

    boolean running = true;
    int counter = 0;

    int frameSize;
    float frameRate;

    int port;
    String hostname;
    public receiveSound(String hostname, int port) {
        this.port = port; //9999
        this.hostname = hostname; //"225.123.123.123"
    }
    @Override
    public void run() {
        try {
            // Create a multicast socket and join the multicast group
            MulticastSocket socket = new MulticastSocket(port);
            InetSocketAddress groupAddress = new InetSocketAddress(hostname, port);
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
            outLine.stop();
            outLine.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopRunning() {
        running = false;
    }
    
}
