package api;
import Server.*;
import GUI.serverScene.*;
/**
 * Interaction between client and GUI
 */
public class serverAPI {
    public static Server.MainServer server;
    public static ServerScene gui;

    public static startServerGUI startGUI;
    
    public static void main(String[] args) {
        
        // gui = new ServerScene();
        startGUI = new startServerGUI(args);
        startGUI.start();
        server = new MainServer(Integer.parseInt(args[0]) );
		server.start();
        
    }

    /**
     * 
     * @param newGui
     */
    public static void setGUI(ServerScene newGui) {
        gui = newGui;
    }
    

    //Server -> GUI
    /**
     * Prints [print] onto server gui. 
     * @param print string to print
     */
    public static void printServer(String print) {
        System.out.println("Server API: " + print);
        
        gui.printServer(print);
    }

    /**
     * Updates list of currently active users
     * @param users
     */
    public static void updateActiveUsers(String[] users) {
        // System.out.println(users[0]);
        gui.addUsers(users);        
    }
}
