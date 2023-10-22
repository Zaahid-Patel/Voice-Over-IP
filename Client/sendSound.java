package Client;
import javax.sound.sampled.*;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;

import java.util.*;

public class sendSound extends Thread {
    InetSocketAddress multiAddress;

    TargetDataLine inLine;
    SourceDataLine outLine;

    HashMap<InetSocketAddress, LinkedList<byte[]> > sourceFrames;

    boolean running = true;
    int counter = 0;

    int frameSize;
    float frameRate;

    int port;
    String hostname;
    public sendSound(String hostname, int port) {
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

            sourceFrames = new HashMap<InetSocketAddress, LinkedList<byte[]> >();

            AudioFormat format = new AudioFormat(8000, 16, 1, true, true);
            frameSize = format.getFrameSize();
            frameRate = format.getFrameRate();

            inLine = AudioSystem.getTargetDataLine(format);
            inLine.open(format);

            inLine.start();

            int packetSize = frameSize * 10; // 10 Frames per packet

            byte[] sendData = new byte[packetSize];

            ByteBuffer sendBuf = ByteBuffer.allocate(packetSize);

            while (running) {
                if (inLine.available() >= packetSize) {
                    inLine.read(sendData, 0, packetSize);
                    sendBuf.clear();
                    sendBuf.put(sendData);

                    DatagramPacket packet = new DatagramPacket(sendBuf.array(), packetSize, groupAddress);
                    socket.send(packet);
                }
            }                
            
            // Leave the multicast group and close the socket
            socket.leaveGroup(groupAddress, networkInterface);
            socket.close();
            inLine.stop();
            inLine.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopRunning() {
        running = false;
    }
    
    
}
