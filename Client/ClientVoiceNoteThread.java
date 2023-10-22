package Client;
import javax.sound.sampled.*;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;

import java.util.*;
import java.io.*;


public class ClientVoiceNoteThread extends Thread {

    InetSocketAddress address;
    SocketChannel socket;
    boolean done = false;
    File file;
    TargetDataLine inLine;

        
    String recieverName;
    int recieverPort;

    // SocketChannel socket;
    int dataSize = 16384;

	boolean running = true;

    String fileName;
    FileInputStream newFile;
    int fileSize;
    int bytesSent = 0;
 

    public static void main(String[] args) {

        ClientVoiceNoteThread test = new ClientVoiceNoteThread("test.wav", null);
        test.start();

        try {
            Thread.sleep(5000);
        } catch (Exception e) {

        }

        test.finish();

    }

    ClientVoiceNoteThread(String fileName, SocketChannel s) {
        file = new File(fileName);
        socket = s;
    }

    @Override
    public void run() {

        try {
            AudioFormat format = new AudioFormat(8000, 16, 1, true, true);
            int frameSize = format.getFrameSize();
            float frameRate = format.getFrameRate();

            AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

            inLine = AudioSystem.getTargetDataLine(format);
            inLine.open(format);

            inLine.start();  
 
            AudioInputStream stream = new AudioInputStream(inLine);
            AudioSystem.write(stream, fileType, file);
            

            if (socket == null) {
                System.out.println("Voicenote socket is null");
                System.out.println("Should be testing audio");
                System.out.println("Exiting.");
                System.exit(0);
            }
            
            file = new File(fileName);

            socket.configureBlocking(true);
            while (!socket.finishConnect()) {}

            // sendMetadata(getPacketsToSend(), fileName);

            while (running) {
                byte[] buffer = new byte[dataSize];

                int bufferSize = newFile.read(buffer);

                if (bufferSize == -1) {
                    break;
                }

                ByteBuffer message = ByteBuffer.allocate(bufferSize);
                message.position(0);
                message.put(buffer, 0, bufferSize);
                message.flip();

                bytesSent += socket.write(message);
            }
            System.out.println("Closing channel");
            newFile.close();
            socket.close();
        } catch (Exception e) {
            //TODO: Proper error handling
            e.printStackTrace();
        }

    }

    public void finish() {
        inLine.stop();
        inLine.close();
    }

}
