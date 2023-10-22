package Server;
import java.net.*;
import java.util.*;

import Client.ClientSignaller.User;
import api.serverAPI;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 * Thread that implements signalling
 */
public class SenderTCPSignaller extends Thread {
    
    InetSocketAddress receiverAddress;
    //TODO: existing Usernames not updating properly
    List<String> existingUsernames = new ArrayList<String>();
    List<Short> roomList = new ArrayList<Short>();
    List<Byte[]> IPList = new ArrayList<Byte[]>();//first 5 are group rooms
    List<Short> portList = new ArrayList<Short>();//first 5 are group rooms
    ArrayList<SenderTCPSignaller> clientConnections = new ArrayList<SenderTCPSignaller>();
    
    public SocketChannel socket;

    boolean running = true;
    boolean haveReceiverAddress = false;
    
    String UserName;
    short room = 0;
    String fileName;
    String Sender;
    String Receiver;
    String Message;
    int portNumber;
    byte[] IP;

    /**
     * Constructor for SenderTCPSignaller that takes a socket
     * @param s
     */
    SenderTCPSignaller(SocketChannel s) {
        socket = s;
    }

    /**
     * Constructor for SenderTCSignaller that takes a socket and port
     * @param s
     * @param port
     */
    SenderTCPSignaller(SocketChannel s, int port) {
	    socket = s;
	    portNumber = port;
    }

    /**
     * Starts an instance of SenderTCPSignaller
     */
    @Override
    public void run() {
        
	Thread currentThread = Thread.currentThread();
	
	currentThread.setName("_SenderTCPSignaller: ");  
	  try {
            socket.configureBlocking(false);
            
            generateIPs();
            generatePortNumbers();

            while (!socket.finishConnect()) {}

            while (running) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int bytesRead = socket.read(buffer);
                String UserName = "";
                String Receiver = "";
                String Message = "";
                String FileName = "";

                if (bytesRead != 0) {
                //0 4 6 9        
			    int code = buffer.get(0);
			    //Code 0 Send Username [client -> Server]
                //Server receives username fom client, adds to list of existingUserNames
                //[Code 1 byte] [Username 256 String]
			    if (code == 0) {
                    
                    char next;
                    buffer.position(1); 
                                do {
                                    next = (char) buffer.get();
                                    UserName += next;
                                } while (next != '\0');
                                if (checkUserNameUnique(UserName) == (byte) 1) {
                                    existingUsernames.add(UserName);
                                    this.UserName = UserName;
                                    serverAPI.printServer(UserName + " has connected");
                                    // broadcastUserNames(roomList, existingUsernames);
                                    //TODO: username not correct. Still empty
                                    // serverAPI.updateActiveUsers(existingUsernames.toArray(new String[0]));
                                } else {
                                    serverAPI.printServer("This Username already exists");
                                }
                                serverAPI.updateActiveUsers(existingUsernames.toArray(new String[0]));
			    }

			    //Code 4 Connect to a Room
                //[Code 1 byte] [RoomNumber 2 short]
			    if (code == 4) {
				    System.out.println("Code: 4");

                    short roomNumber; 
                    roomNumber = buffer.getShort(1);
                    roomList.add(roomNumber);
                    //TODO Check for username and add roomnumber to user
                    this.room = roomNumber;
                    serverAPI.printServer("Connecting to channel " + roomNumber);

                    roomDetails(roomNumber, roomList);
			    }

			    //Code 6 Send a message
                //[Code 1 byte] [Receiver 256 String] [Message 512 String]
			    if (code == 6) {
				    System.out.println("Code: 6");
                
                    char next;
                    buffer.position(1);
                    Receiver = "";
                    do {
                        next = (char) buffer.get();
                        Receiver += next;
                    } while (next != '\0');
                    Message = "";
                    buffer.position(257);
                    char next2;
                    do {
                        next2 = (char) buffer.get();
                        Message += next2;
                    } while (next2 != '\0');

                    this.Message = Message;
                    this.Receiver = Receiver;
                    
                    serverAPI.printServer(Receiver + " receives message: " + Message);
                    for (SenderTCPSignaller con: clientConnections){
                        if (con.getUserName().equals(Receiver)) {
                            con.broadcastMessage(this.UserName, Message);
                        }
                    }

			    }
		
			    //Code 9 Client wants to send file [Client -> Server]
                //[Code 1 byte] [Receiver 256 byte] [Filename 256 byte] 
			    if (code == 9) {
				    System.out.println("Code: 9");
                    
                    char next;
                    buffer.position(1);
                    do {
                        next = (char) buffer.get();
                        Receiver += next;
                    } while (next != '\0');
                    buffer.position(258);
                    char next2;
                    do {
                        next2 = (char) buffer.get();
                        FileName += next2;
                    } while (next2 != '\0');
                    //TODO I assume i need to give client the receiver details?
                    this.Receiver = Receiver;
                    this.fileName = FileName;
                    serverAPI.printServer(Receiver + " receives file: " + FileName);
			    }


            }

            
            }
            

        } catch (IOException e) {
            serverAPI.printServer(UserName + " has disconnected");
            existingUsernames.remove(UserName);
            serverAPI.updateActiveUsers(existingUsernames.toArray(new String[0]));
            // e.printStackTrace();
        }
    }

    /**
     * Checks if the Username given by a client is unique
     * @param Username Given by the client
     * @return 0 if the Username already exists, 1 otherwise
     */
    public byte checkUserNameUnique(String Username) {

        byte ack;
        
        System.out.println(Arrays.toString(existingUsernames.toArray()));
        System.out.println(Username);
        if (existingUsernames.contains(Username)) {
            ack = (byte) 0;
        } else {
            ack = (byte) 1;
        }

        return ack;
        
    }
	
    /**
     * Exits the while loop, no longer checks for input. Which stops the Thread
     */
	synchronized void stopRunning() {
		running = false;
	}
	/**
     * Gets the filename from client
     * @param placeholder
     * @return
     */
	public String getFileName(String placeholder) {
		return null;
	}
    /**
     * Sets the filename
     * @param name
     */
	public void setFileName(String name) {
		
	}

    // 1 2 3 5 7 8 10
    //[Server -> Client]
    //Code 1
    //[code 1 byte] [Ack 1 byte]
    
    /**
     * Checks if the Username is valid and adds the ack to the buffer 
     * @param UserName
     */
	public void ackUserName(String UserName) {
        byte ackB;

		if (!existingUsernames.contains(UserName)) {
        
					existingUsernames.add(UserName);
                    ackB = 0;
                    this.addUserToConnections();
                    serverAPI.updateActiveUsers((String[])existingUsernames.toArray());
			
		} else {
            //Send back ack to say invalid username
            ackB = 1;

        }

        ByteBuffer data = ByteBuffer.allocate(2);
        data.position(0);
        data.put((byte) 1);
        data.put(ackB);
        data.flip();

        data.flip();
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

    //2
    //[Code 1 byte] [RoomCount 2 short] [rooms 2*RC short[]]

    /**
     * Ouput the rooms that are active, aswell as how many
     * @param roomList
     */
    public void broadcastRooms(List<Short> roomList) {

        short roomCount;
        

        roomCount = (short) roomList.size();
        short[] rooms = new short[roomCount];

        ByteBuffer data = ByteBuffer.allocate(3 + 2*roomCount);
        for (int i = 0; i < roomCount; i++) {

            rooms[i] = (short) roomList.get(i);
        }

        data.put((byte) 2);
        data.putShort(roomCount);
        for (int i = 0; i < rooms.length; i++) {
            data.putShort(rooms[i]);
        }
        data.flip();
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //3
    //[Code 1 byte] [UserCount 2 short] [[Username 256 String] [room 2 short]]

    /**
     * Broadcasts all connected Users and to which room they are connected
     * @param roomList
     * @param existingUsernames
     */
    public void broadcastUserNames(List<Short> roomList, List<String> existingUsernames) {
        short UserCount;

        UserCount = (short) existingUsernames.size();
        String[] UserNames = existingUsernames.toArray(new String[existingUsernames.size()]);
        Short[] rooms = roomList.toArray(new Short[roomList.size()]);

        for (int i = 0; i < UserCount; i++) {
            UserNames[i] = existingUsernames.get(i);
        }

        for (int i = 0; i < roomList.size(); i++) {
            rooms[i] = roomList.get(i);
        }

        ByteBuffer data = ByteBuffer.allocate((3 + (256 + 2))*UserCount);
        data.put((byte) 3);
        for (int i = 0; i < UserCount; i++) {
            data.put(UserNames[i].getBytes());
            data.put((byte) '\0');
            data.putShort(rooms[i]);
            data.put((byte) '\0');
        }
        data.flip();
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //5
    //[Code 1 byte] [RoomIP 4 byte] [RoomPort 2 Short]

    /**
     * Sends the details of a room to the client. Room IP and Room Port
     * @param room
     * @param roomList
     */
    public void roomDetails(Short room, List<Short> roomList) {
        
        ByteBuffer data = ByteBuffer.allocate(7);
        int roomNum = room.intValue();

        data.put((byte) 5);
        data.put(IPList.get(roomNum)[0]);
        data.put(IPList.get(roomNum)[1]);
        data.put(IPList.get(roomNum)[2]);
        data.put(IPList.get(roomNum)[3]);
        data.putShort(portList.get(roomNum));
        data.flip();
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //7
    //[Code 1 byte] [Ack 1 byte]

    /**
     * Sends an ack to the client that the message was received
     */
    public void ackMessageSent() {

        ByteBuffer data = ByteBuffer.allocate(2);
        data.put((byte) 7);
        data.put((byte) 1);
        data.flip();
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //8
    //[Code 1 byte] [Sender 256 string] [message 256 string]

    /**
     * Sends the message to the client and who the sender is
     * GUI should check which room the user is connected to and only display the message to that room
     * @param Sender
     * @param Message
     */
    public void broadcastMessage(String Sender, String Message) {   
    
        String tempSender = "";
        String tempMessage = "";

        ByteBuffer data = ByteBuffer.allocate(769);
        data.put((byte) 8);
        data.put(Sender.getBytes());
        data.put((byte) '\0');
        data.position(257);
        data.put(Message.getBytes());
        data.put((byte) '\0');
        data.flip();

        serverAPI.printServer(Sender + " : " + Message);
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    //10
    //[Code 1 byte] [IP to connect to 4 byte] [port 2 short] [filename 256 string]

    /**
     * Sends the IP and port the file needs to be sent to, aswell as the filename
     */
    public void infoForClientWhoNeedsFile() {
        
    }

    /**
     * This users's Username
     * @return
     */
    public String getUserName() {
        return this.UserName;
    }

    /**
     * This room this user is connected to
     * @return
     */
    public short getRoomNumber() {
        return this.room;
    }

    /**
     * The filename for this user (send or receive)
     * @return
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * The username of the receiver of the file
     * @return
     */
    public String getReceiver() {
        return this.Receiver;
    }

    /**
     * The username of the sender of the file
     * @return
     */
    public String getSender() {
        return this.Sender;
    }

    /**
     * The message to be sent
     * @return
     */
    public String getMessage() {
        return this.Message;
    }

    /**
     * Adds a user to the list of active users
     */
    synchronized void addUserToConnections() {
    
        SenderTCPSignaller user = this;
        SenderTCPSignalListener con = new SenderTCPSignalListener();
        con.connections.add(user);
        //Think this works
    }

    /**
     * Assigns a IP for this user
     */
    synchronized void getUserIP() {
        SenderTCPSignaller user = this;
        SenderTCPSignalListener con = new SenderTCPSignalListener();
        for (int i = 0; i < con.connections.size(); i++) {
            if (con.connections.get(i).UserName.equals(UserName)) {
                this.IP = con.connections.get(i).IP;
            }
        }
    }

    /**
     * Assigns a Port for this user
     */
    synchronized void getUserPort() {
        SenderTCPSignaller user = this;
        SenderTCPSignalListener con = new SenderTCPSignalListener();
        for (int i = 0; i < con.connections.size(); i++) {
            if (con.connections.get(i).UserName.equals(UserName)) {
                this.portNumber = con.connections.get(i).portNumber;
            }
        }
    }

    /**
     * Generates a set amount of IPs for use by the server
     */
    public void generateIPs() {
        int numOfIps = 50;
        Byte[] IP = new Byte[4];
        IP[0] = (byte) 225;
        IP[1] = (byte) 0;
        IP[2] = (byte) 0;
        IP[3] = (byte) 0;
        
        for (int i = 0; i < numOfIps; i++) {
            IPList.add(i, IP.clone());
            
            IP[3]++;
        }
    }

    /**
     * Generates a set amount of Ports for use by the server
     */
    public void generatePortNumbers() {
        int numOfPorts = 50;
        short port = 0;

        for (int i = 0; i < numOfPorts; i++) {
            portList.add(i, port);
            port++;
        }
    }

}
