package api;
import GUI.clientScene.*;
import Client.*;
//Import GUI and Client here
/**
 * Interaction between client and GUI
 */
public class clientAPI {
    //public static Client client //Add client here
    //public static clientGUI gui //Add gui here
    public static ClientMainScene gui;
    public static startClientGUI startGUI;

    public static ClientSignaller signaller;
    public static void main(String[] args) {
        startGUI = new startClientGUI(args);
        startGUI.start();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        String hostname = args[0];
        short port = Short.parseShort(args[1]) ;
        signaller = new ClientSignaller(hostname, port);
       
        signaller.start();

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void setGUI(ClientMainScene newGUI) {
        gui = newGUI;
    }

    //Client -> GUI

    /**
     * Prints [print] onto client gui. 
     * @param print string to print
     */
    public static void printClient(String print) {
        System.out.println("Client API: " + print);
        gui.printClient(print);
    }

    /**
     * Updates list of currently active users
     * @param users
     */
    public static void updateActiveUsers(String[] users) {
        gui.addUsers(users);
    }

    //GUI -> Client

    /**
     * 
     * @param receiver
     * @param message
     */
    public static void sendMessage(String receiver, String message) {
        signaller.sendMessage(receiver, message);
    }
    
    /**
     * Tells client to connect to spesific room
     * @param room Name of room TODO: (Note to self) determine what type of variable room will be
     */
    public static void callRoom(short room) {
        signaller.connectToRoom(room);
    }

    /**
     * disconects
     */
    public static void disconnect() {
        signaller.stopRunning();
    }

    //TODO: (Note to self) Figure out how file recording and sending will work
    /**
     * Sends [fileName] to client with [username]
     * @param username Name of client to send file to
     * @param fileName Name of file
     */
    public static void sendFile(String username, String fileName) {
        //TODO: Add function in client for sending [fileName] to the client with [username]
    }
}
