package Server;

import java.net.*;
import java.util.*;
import api.serverAPI;
import java.io.*;
import java.nio.channels.*;

/**
 * Main Class for the entire server Responsible for Getting input from the terminal, Starting the
 * ServerListener thread, Rerouting input from a ServerChannelThread thread to the ServerListener
 * thread, Properly closing each thread when program ends.
 */
public class MainServer extends Thread{
    static SenderTCPSignalListener listener;
	private static int portNumber;
	static List<String> clientUsernamesWithIDs;

		public MainServer(int port) {
			portNumber = port;
		}

		public static void main(String args[]) {
			
			if (args.length < 1) {
				System.err.println("No port given. FORMAT: java Server/MainServer port");
            	System.exit(1);
        	}

			portNumber = Integer.parseInt(args[0]);

		}

		//Added///////////////////////@Zander
		/**
		 * Starts the server
		 */
		@Override
		public void run() {
		
			System.out.println("Starting Server...");           

			for (int i=0; i < 5; i++) {
				
				System.out.print(".");

				try {
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			System.out.println("");			
			System.out.println("Server online...");

			serverAPI.printServer("Server online...");
			
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Okaerinasai, goshujinsama (* ^ ω ^)");

			Thread currentThread = Thread.currentThread();
        		currentThread.setName("_MainServer");
        		System.out.println("Type /help for more commands");

        		listener = new SenderTCPSignalListener(portNumber);
        		listener.start();

		}

		/**
		 * Stops the MainServer thread and exists the program
		 */
		public void KillServer() {
				
				System.out.println("Thread MainServer ended");
				currentThread().interrupt();
				System.exit(0);
		}

        // use this to test if api and server can communicate properly
		//public String getInfo() {
		// 	return "hi this is server: " + this.getName();
		//}
		//////////////////////////////

    /**
     * Code given by ChatGPT, modified. https://chat.openai.com/chat
     * 
     * Used to print out spesific threads that are currently running. Only used for
     * testing/experiments.
     */
    public static void printRunningThreads() {
        // Get the root thread group
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup().getParent();

        // Create a buffer to hold the list of threads
        Thread[] threads = new Thread[rootGroup.activeCount()];

        // Fill the buffer with the list of threads
        int count = rootGroup.enumerate(threads, true);

        // Print out the list of threads
        System.out.println("List of running threads:");
        for (int i = 0; i < count; i++) {
            String name = threads[i].getName();
            // Only prints out designated threads
            if (name.contains("_")) {
                System.out.println(name);
            }
        }
    }


}


