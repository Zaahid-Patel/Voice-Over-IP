package Server;

import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 * Listener Thread
 */

public class SenderTCPSignalListener extends Thread {
    
    int port;
    String fileName;
	boolean running = true;
    byte[] IP;

   
    ArrayList<SenderTCPSignaller> connections = new ArrayList<SenderTCPSignaller>();
    
    /**
     * Constructor for SenderTCPSignalListener that takes a port
     * @param p
     */
    public SenderTCPSignalListener(int p) {
	    port = p;
    }

    /**
     * Constructor for SenderTCPSignalListener
     */
    public SenderTCPSignalListener() {

    }

    /**
     * Starts the thread
     */
    @Override
    public void run() {
 
        Thread currentThread = Thread.currentThread();
	    currentThread.setName("_SenderTCPSignalListener");

        try (ServerSocketChannel socket = ServerSocketChannel.open()) {
            
            socket.socket().bind(new InetSocketAddress(port));
            this.IP = socket.socket().getInetAddress().getAddress();
            System.out.println(InetAddress.getLocalHost() + " : " + port);
            socket.configureBlocking(false);
            while (running) {
                SocketChannel s = socket.accept();
                if (s != null && fileName != "") {
                    System.out.println("Sender Connected");
                    SenderTCPSignaller tcpSignaller = new SenderTCPSignaller(s);
                    tcpSignaller.start();
                    connections.add(tcpSignaller);
                    
                } else if (s != null && fileName == "") {
			System.out.println("Client Connected");
			SenderTCPSignaller tcpSignaller = new SenderTCPSignaller(s, port);
			tcpSignaller.start();
			connections.add(tcpSignaller);
		}
            }
            socket.close();
            System.out.println("Server socket properly closed:" + !socket.isOpen());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the active connections
     * for use by SenderTCPSignaller
     * @return
     */
    public ArrayList<SenderTCPSignaller> getConnections() {
        return this.connections;
    }

    /**
     * Sets all active users' running state to false
     * This exits the while loop for each user looking for input
     * and kills the client
     */
	synchronized void killAllClients() {
	
		for (SenderTCPSignaller t : connections) {
			t.running = false;
		}

	}   

}
