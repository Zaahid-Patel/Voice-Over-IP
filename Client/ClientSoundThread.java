package Client;
import javax.sound.sampled.*;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;

import java.util.*;


public class ClientSoundThread extends Thread {

    DatagramChannel channel;
    InetSocketAddress pairAddress;

    TargetDataLine inLine;
    SourceDataLine outLine;

    boolean running = true;

    int frameSize;
    float frameRate;

    /**
     * Run as main class for testing purposes
     * @param args      args[0] should contain pair ip
     *                  args[1] should contain port
     */
    public static void main(String[] args) {
        try {
            ClientSoundThread t = new ClientSoundThread(
                                        InetAddress.getByName(args[0]),
                                        Integer.parseInt(args[1]),
                                        Integer.parseInt(args[2])
                                        );
            t.start();
            Scanner scanner = new Scanner(System.in);
            String code = scanner.nextLine();
            while (!code.equals("stop")) {
                code = scanner.nextLine();
            }
            t.stopRunning();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor
     *
     * @param pAddress  Pair to connect to
     * @param pPort     Port to send to
     * @param hPort     Port to listen on
     */
    ClientSoundThread(InetAddress pAddress, int pPort, int hPort) {

        try {
            channel = DatagramChannel.open().bind(new InetSocketAddress(hPort));
            channel.configureBlocking(false);
            pairAddress = new InetSocketAddress(pAddress, pPort);
            channel.connect(pairAddress);

            //TODO: Create format based on what hardware supports
            AudioFormat format = new AudioFormat(8000, 16, 1, true, true);
            frameSize = format.getFrameSize();
            frameRate = format.getFrameRate();

            inLine = AudioSystem.getTargetDataLine(format);
            inLine.open(format);
            outLine = AudioSystem.getSourceDataLine(format);
            outLine.open(format);
        } catch (Exception e) {
            //TODO: Proper exception handling
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        inLine.start();
        outLine.start();

        int packetSize = frameSize * 10; // 10 Frames per packet
        byte[] recData = new byte[packetSize];
        byte[] sendData = new byte[packetSize];
        ByteBuffer recBuf = ByteBuffer.allocate(packetSize);
        ByteBuffer sendBuf = ByteBuffer.allocate(packetSize);

        int bytesRead;

        try {
        System.out.println("Local: " + channel.getLocalAddress().toString());
        System.out.println("Remote: " + channel.getRemoteAddress().toString());
        } catch (Exception e) {

        }
        while (running) {

            try {
                if (inLine.available() >= packetSize) {
                    inLine.read(sendData, 0, packetSize);
                    sendBuf.clear();
                    sendBuf.put(sendData);
                    channel.send(sendBuf, pairAddress);
                }

                recBuf.clear();
                // bytesRead = channel.read(recBuf);
                SocketAddress s = channel.receive(recBuf);
                
                if (s != null) {
                    System.out.println(Arrays.toString(recBuf.array()));
                    outLine.write(recBuf.array(), 0, packetSize);
                }
            } catch (Exception e) {
                //TODO: Proper exception handling
                e.printStackTrace();
            }


        }

        try {
            inLine.close();
            outLine.close();
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRunning() {
        running = false;
    }

}
