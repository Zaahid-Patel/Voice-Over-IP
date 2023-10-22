package api;

import GUI.serverScene.*;
/**
 * Gui thread
 */
public class startServerGUI extends Thread {
    ServerScene gui;
    String[] args;
    startServerGUI(String[] args) {
        this.args = args;
    }
    @Override
    public void run() {
        gui = new ServerScene("");
        gui.launchGUI(args);

        System.exit(0);
    }
}
