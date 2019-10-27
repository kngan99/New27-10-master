package Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Protocol.Tags;
import Protocol.Message;
import Protocol.Peer;

public class Server {
    private ArrayList<Peer> peerList = null;
    private Socket socket;
    private ServerSocket server;						
    private ObjectOutputStream sender;		
    private ObjectInputStream listener;
    public boolean isStop = false, isExit = false;
    public Database db;
    private Message message;
    private int msgType;
    private String friendsList;

    public Server(int port) throws Exception {
		server = new ServerSocket(port);
		peerList = new ArrayList<Peer>();
		db = new Database(ServerUI.filePath);
		(new WaitForConnect()).start();			
    }
    
    private String sendSessionAccept() throws Exception {
    	int size = peerList.size();
		String msg = "";
    	if (size == 0) return msg;
		msg += peerList.get(size-1).getName();
		msg += ",";
		msg += peerList.get(size-1).getHost();
		msg += ",";
		msg += peerList.get(size-1).getPort();
		for (int i = 0; i < size - 1; i++) {
			msg += ";";
			Peer peer = peerList.get(i);				
			msg += peer.getName();
			msg += ",";
			msg += peer.getHost();
			msg += ",";
			msg += peer.getPort();
		}
		return msg;
	}
    
    public Peer getUser(String name) {
    	Peer peer = new Peer("", "", 0);
    	for(int i = 0; i < peerList.size(); i++) {
    		if(peerList.get(i).getName() == name) {
    			 peer = peerList.get(i);
    			 break;
    		}
    	}
    	return peer;
    }
    
    private boolean isExsistName(String name) throws Exception {
		int size = peerList.size();
		for (int i = 0; i < size; i++) {
			Peer peer = peerList.get(i);
			if (peer.getName().equals(name))
				return true;
		}
		return false;
	}

    public void stopserver() throws Exception {
		isStop = true; server.close();	socket.close();						
    }
    
    private String getFriendsList() {
    	if (friendsList.length() == 0) return "";
    	String[] list = friendsList.split(",");
    	for (int i = 0; i < list.length; i++) {
    		try {
				if (isExsistName(list[i]))
						list[i] = Integer.toString(peerList.get(i).getPort()) + "," + peerList.get(i).getHost() + "," + list[i] + "(Active Now)";
				else list[i] += "(Not Active)";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return String.join(";", list);
    }
    
    private boolean checkConnect() throws Exception {
		socket = server.accept();			
		listener = new ObjectInputStream(socket.getInputStream());		
        message = (Message) listener.readObject();
		ServerUI.updateMessage(message.toString());											
        					
		if (message.type.equals("request")) {
			msgType = 1;
			return true;
		}
		
		else if (message.type.equals("signup")) {
            if (!db.userExists(message.sender)) {
            	db.addUser(message.sender, message.content1);
        	            
        		ServerUI.updateMessage(message.sender + " Sign Up Successful!");
        		msgType = 2;
        		return true;
            } else {
            	ServerUI.updateMessage(message.sender + " Sign Up Failed! - user exists on DB");
            	msgType = 3;
            	return false;
        	}
		}
		
		else if (message.type.equals("login")) {
			if (!isExsistName(message.sender)) {
				if (db.checkLogin(message.sender, message.content1)) {
					peerList.add(new Peer(message.sender, 
        	        		socket.getInetAddress().toString(),
        	                Integer.parseInt(message.content2)
        	        		));
        	            
        			ServerUI.updateMessage(message.sender + " Login Successful!");
        			msgType = 6;
        			return true;
				}
				else {
					ServerUI.updateMessage(message.sender + " Login Failed! Wrong Password!");
					msgType = 4;
					return false;
				}
			}
			else {
				ServerUI.updateMessage(message.sender + " Login Failed! Duplicate User!");
				msgType = 5;
				return false;
			}
		}
		
		else if (message.type.equals("Add friend")) {
			
			String friendtoAdd = message.content2;
			String userName = message.content1;
			db.addFriendtoUser(friendtoAdd, userName);
			msgType = 8;
			return true;
			
		}
		else if (message.type.contentEquals("friendlist")){
			friendsList = db.getListFriends(message.sender);
			msgType = 9;
			if (friendsList.length() == 0) msgType = 10;
			return true;
		}
			
		else {
			msgType = 7;
			return false;
		}
		
	}

    public class WaitForConnect extends Thread {

		@Override
		public void run() {
			super.run();
			try {
				while (!isStop) {
					if (checkConnect()) {
						if (isExit) {
							isExit = false;
						} 
						else 
						{
								sender = new ObjectOutputStream(socket.getOutputStream());
								switch (msgType) {
								case 1:
									sender.writeObject(new Message("ACCEPTED", "SERVER", sendSessionAccept(), "", message.sender));
									break;
								case 2:
									sender.writeObject(new Message(Tags.SIGNIN_SUCCESSFUL, "SERVER", "TRUE", "", message.sender));
									break;
								case 6:
									sender.writeObject(new Message("SUCCESSFUL", "SERVER", sendSessionAccept(), "", message.sender));
									break;
								case 8:
									sender.writeObject(new Message("SUCCESSFUL", "SERVER", "ADD COMPLETE", "", message.sender));
									break;
								case 9:
									sender.writeObject(new Message("SUCCESSFUL", "SERVER", getFriendsList(), "", message.sender));
									break;
								case 10:
									sender.writeObject(new Message(Tags.NO_FRIEND, "SERVER", "", "", message.sender));
									break;
								}
								sender.flush();
								sender.close();
						}
					} else {
						sender = new ObjectOutputStream(socket.getOutputStream());
						switch (msgType) {
						case 3:
							sender.writeObject(new Message(Tags.EXISTS_DB, "SERVER", "TRUE", "", message.sender)); 
							break;
						case 4:
							sender.writeObject(new Message(Tags.WRONG_PASSWORD, "SERVER", "TRUE", "", message.sender)); 
							break;
						case 5:
							sender.writeObject(new Message(Tags.DUPLICATE_USER, "SERVER", "TRUE", "", message.sender)); 
							break;
						case 7:
							sender.writeObject(new Message(Tags.INVALID_REQUEST, "SERVER", "TRUE", "", message.sender)); 
						}
						sender.flush(); 
                        sender.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
}
