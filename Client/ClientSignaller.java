package Client;
import java.net.*;
import java.util.*;
import api.clientAPI;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;


public class ClientSignaller extends Thread {

    InetSocketAddress serverAddress;
    SocketChannel socket;

    //TODO: Needs list of all usernames
    String username;
    boolean usernameACKReceived;
    boolean usernameAccepted;
    
    short roomCount = 0;
    short[] rooms = {};
    byte[] roomIP = {};
    int roomPort = 0;
    short roomNumber = -1;

    short userCount = 0;
    User[] users = {};
    
    boolean lastMessageACKReceived;
    boolean lastMessageAccepted;

    boolean running = true;

    String senderHostname;
    short senderPort;

    receiveSound receive;
    sendSound send;

    String bMessage = "";
    String bSender = "";

    public ClientSignaller(String senderHostname, short senderPort) {
        this.senderHostname = senderHostname;
        this.senderPort = senderPort;
    }

    @Override
    public void run() {

        try {
            socket = SocketChannel.open();
            socket.configureBlocking(true);
            System.out.println(senderHostname + " : " + senderPort);
            socket.connect(new InetSocketAddress(senderHostname, senderPort));
            while (!socket.finishConnect()) {}

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // TODO: handle exception
            }

            sendUsername(InetAddress.getLocalHost().toString() + ";" + Math.random() * 100);
            while (running) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int bytesRead = socket.read(buffer);

                if (bytesRead != 0) {
                    byte code = buffer.get(0);
                    char next;

                    switch (code) {
                        // ACK username
                        case 1:
                            usernameACKReceived = true;
                            byte accepted = buffer.get(1);
                            usernameAccepted = accepted != 0;
                            break;
                        // Broadcast rooms
                        case 2:
                            roomCount = buffer.getShort(1);
                            rooms = new short[roomCount];
                            for (int i = 0; i < roomCount; i++) {
                                rooms[i] = buffer.getShort(3 + 2*i);
                            }
                            break;
                        // Broadcast users
                        case 3:
                            userCount = buffer.getShort(1);
                            users = new User[userCount];
                            String[] userNames = new String[userCount];
                            for (int i = 0; i < userCount; i++) {
                                String uname = "";

                                buffer.position(3+258*i);
                                next = (char) buffer.get();
                                while (next != '\0'){
                                    uname += next;
                                    next = (char) buffer.get();
                                }
                                short roomNum = buffer.getShort(259 + 258*i);
                                users[i] = new User(uname, roomNum);
                                userNames[i] = uname;
                            }
                            clientAPI.updateActiveUsers(userNames);
                            break;
                        // Room details
                        case 5:
                        //TODO: replace ip bytes with string
                            roomIP = new byte[4];
                            for (int i = 0; i < 4; i++) {
                                roomIP[i] = buffer.get(i + 1);
                            }
                            roomPort = buffer.getShort(5);
                            //TODO: added rooms
                            //TODO: replace "225.123.123.123" with proper broadcast ports later
                            if (receive != null) {
                                receive.stopRunning();
                                send.stopRunning();
                            }

                            System.out.println(roomPort);
                            System.out.println(Arrays.toString(roomIP));
                            System.out.println(InetAddress.getByAddress(roomIP).getHostAddress());

                            receive = new receiveSound(InetAddress.getByAddress(roomIP).getHostAddress(), roomPort);
                            receive.start();

                            send = new sendSound(InetAddress.getByAddress(roomIP).getHostAddress(), roomPort);
                            send.start();

                            break;
                        // ACK message
                        case 7:
                            lastMessageACKReceived = true;
                            byte acc = buffer.get(1);
                            lastMessageAccepted = acc != 0;
                            break;
                        // Receive broadcast message
                        case 8:
                            buffer.position(1);
                            bSender = "";
                            next = (char) buffer.get();
                            while (next != '\0'){
                                bSender += next;
                                next = (char) buffer.get();
                            }
                            buffer.position(257);
                            bMessage = "";
                            next = (char) buffer.get();
                            while (next != '\0'){
                                bMessage += next;
                                next = (char) buffer.get();
                            }
                            System.out.println(bSender + ": " + bMessage);
                            break;
                        // Info for client who wants file
                        case 10:
                            byte[] ip = new byte[4];
                            buffer.get(1, ip, 0, ip.length);
                            short port = buffer.getShort(5);
                            String filename = "";
                            buffer.position(8);
                            next = (char) buffer.get();
                            while (next != '\0'){
                                filename += next;
                                next = (char) buffer.get();
                            }
                            //TODO: Send file from here, or pass back to GUI?
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //TODO: Proper exception handling
            e.printStackTrace();
        }

    }

    public synchronized void stopRunning() {
        running = false;
    }

    /**
     * Send username to server; Uses code 0
     * @param uname     Username to send to server
     */
    public void sendUsername(String uname) {
        username = uname;

        String[] u = {username};
        clientAPI.updateActiveUsers(u);

        ByteBuffer data = ByteBuffer.allocate(257);
        data.position(0);
        data.put((byte) 0);
        data.put(uname.getBytes());
        data.put((byte) '\0');

        data.flip();
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isUsernameACKReceived() {
        return usernameACKReceived;
    }

    public boolean isUsernameAccepted() {
        return usernameAccepted;
    }

    /**
     * Connects to room; Uses code 4
     * @param roomNum   The id of the room to connect to
     */
    public void connectToRoom(short roomNum) {
        roomNumber = roomNum;
        ByteBuffer data = ByteBuffer.allocate(3);
        data.position(0);
        data.put((byte) 4);
        data.putShort(roomNumber);
        data.flip();
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends message to specific receiver; Uses code 6
     * @param receiver  Username of receiver
     * @param message   Message to send
     */
    public void sendMessage(String receiver, String message) {
        ByteBuffer data = ByteBuffer.allocate(769);
        data.position(0);
        data.put((byte) 6);
        data.put(receiver.getBytes());
        data.put((byte) '\0');
        data.position(257);
        message = message;
        data.put(message.getBytes());
        data.put((byte) '\0');
        data.flip();
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLastMessageACKReceived() {
        return lastMessageACKReceived;
    }

    public boolean isLastMessageAccepted() {
        return lastMessageAccepted;
    }
    
    /**
     * Send request to server to command receiver to open port for file transfer
     * @param receiver  User to send file to
     * @param filename  Name of file to be sent
     */
    public void sendFileInfo(String receiver, String filename) {
        ByteBuffer data = ByteBuffer.allocate(513);
        data.position(0);
        data.put((byte) 9);
        data.put(receiver.getBytes());
        data.put((byte) '\0');
        data.position(257);
        data.put(filename.getBytes());
        data.put((byte) '\0');
        data.flip();
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finish() {
        running = false;
    }

    public class User {
        public String name;
        public short room;

        public User(String n, short r) {
            name = n;
            room = r;
        }
    }
}
