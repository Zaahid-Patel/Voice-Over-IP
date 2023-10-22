package api;
import GUI.clientScene.*;
/**
 * Gui thread
 */
public class startClientGUI extends Thread {
    ClientMainScene gui;

    String[] args;

    startClientGUI(String[] args) {
        this.args = args;
    }
    @Override
    public void run() {
        gui = new ClientMainScene("");
        gui.launchGUI(args);

        System.exit(0);
    }
    
}
