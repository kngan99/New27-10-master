package Client;

import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import Client.ClientUI;
import Protocol.Message;
//import server.Server;
import Protocol.Tags;

public class ClientUI {
	
	  	private JFrame frame, popup;
	    private JButton btnChat, btnExit;
	    private JTextField textYourName, textFriendName;
		private TextArea textList;
		private HashMap<String, String> list;
    	private JList listview = new JList();
    	private JList  popupListView = new JList();
    	private DefaultListModel listModel = new DefaultListModel();	
    	private DefaultListModel listModelPopup = new DefaultListModel();	
    	private ServerSocket server;
    	private Socket socket,connectReq;
		private static int serverPort = 8080;
		private static String clientIP = "";
		private static String userName = "";
		private static String userData = "";
		private static int clientPort = 0;

	    public ClientUI() {
	      	try {
				server  = new ServerSocket(clientPort);
			} catch (IOException e) {
				e.printStackTrace();
			}
	        initializeFrame();
	        initializeLabel();
	        initializeButton(); 
	        initializeListView(userData); 
	        (new WaitForClient()).start();
		}
	    
	    private void initializeFrame() {
	        frame = new JFrame();
			frame.setResizable(false);
			frame.setBounds(100, 100, 400, 556);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.getContentPane().setLayout(null);	
	    }

	    private void initializeLabel() {
	        JLabel label = new JLabel("User Name: " + userName);
			label.setBounds(10, 17, 180, 16);
	        frame.getContentPane().add(label);
	    }
	    
	    private void initializeButton() {
	    	
	    	JButton btnRefresh = new JButton();
	    	btnRefresh.setText("Refresh");
	    	btnRefresh.setBounds(290, 12, 90, 30);
	    	frame.getContentPane().add(btnRefresh);
	    	
	    	btnRefresh.addActionListener(new ActionListener() {
	    		
		    	public void actionPerformed(ActionEvent arg0) {
		    	    	updateListView("request");		    	    	
		    	}		
		    	
	    	});    		    	
	    	
	    	JButton btnViewListFriend = new JButton();
	    	btnViewListFriend.setText("Friend");
	    	btnViewListFriend.setBounds(180, 12, 90, 30);
	    	frame.getContentPane().add(btnViewListFriend);
	    	
	    	btnViewListFriend.addActionListener(new ActionListener() {
	    		
		    	public void actionPerformed(ActionEvent arg0) {
		    			popup = new JFrame();
		    			popup.setResizable(false);
		    			popup.setBounds(100, 100, 400, 500);
		    			popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    			popup.getContentPane().setLayout(null);
		    			popup.setVisible(true);
		    			JButton btnRefresh = new JButton();
		    	    	btnRefresh.setText("Refresh");
		    	    	btnRefresh.setBounds(290, 12, 90, 30);
		    	    	popup.getContentPane().add(btnRefresh);
		    	    	
		    	    	btnRefresh.addActionListener(new ActionListener() {
		    	    		
		    		    	public void actionPerformed(ActionEvent arg0) {
		    		    		//popupListView = updateListView("friendList");
		    		    		updateListView("friendlist");
		    		    	}		 	    		    	
		    	    	});    	
		    			
		    			Message message = new Message("friendlist", userName, "", "", "SERVER");
		    			Socket req;
						try {
							req = new Socket(clientIP, serverPort);
			    	    	ObjectOutputStream sender = new ObjectOutputStream(req.getOutputStream());
			                sender.writeObject(message); sender.flush();
			                ObjectInputStream listener = new ObjectInputStream(req.getInputStream());
			                message = (Message) listener.readObject();
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String friendsList = message.content1;
						System.out.println(message.content1);
						initializeListView(friendsList);
						popupListView = SetActionforItem(popupListView);
						popup.getContentPane().add(popupListView);												
		    	}		 
		    	
	    	});    		    	
	    }
	    
	    private String reRequest(String Type) throws UnknownHostException, IOException, ClassNotFoundException {
	    	
	    	Socket req = new Socket(clientIP, serverPort);
	    	Message message = new Message(Type, userName, "", "", "SERVER");
	    	ObjectOutputStream sender = new ObjectOutputStream(req.getOutputStream());
            sender.writeObject(message); sender.flush();
            ObjectInputStream listener = new ObjectInputStream(req.getInputStream());
            message = (Message) listener.readObject();
            System.out.println(message.type);
            System.out.println(message.content1);
            req.close();
            return	message.content1;
	    }
	    
	    private void initializeListView(String data) {

	    	String[] lst = getClient(data);
	    	
	    	if(data == userData) {
		    	for(int i = 0; i < lst.length; i++) { 
				  listModel.addElement(lst[i]);						  
		    	}	 
  	
			  	listview = new JList(listModel);
		    	listview.setBounds(2, 50, 400, 500);
		     	listview = SetActionforItem(listview);
		     	//JScrollPane scrollpane = new JScrollPane(listview);
				frame.getContentPane().add(listview);	
	    	}
	    	else {
	    		
	    		for(int i = 0; i < lst.length; i++) { 
		  			  listModelPopup.addElement(lst[i]); 
		  	    }	 
	  		 		 
	  		   popupListView = new JList(listModelPopup);
	  		   popupListView.setBounds(2, 50, 200, 300);
	  		   popupListView = SetActionforItem(popupListView);
	  		   //JScrollPane scrollpane = new JScrollPane(popupListView);
	  		   popup.getContentPane().add(popupListView);	
	    	}

	    }
	    
	    private JList SetActionforItem(JList listView) {
	    	
	    	MouseListener mouseListener = new MouseAdapter() {
	    		
	    	    public void mouseClicked(MouseEvent e) {
	    	    	
	    	        if (e.getClickCount() == 1) {
	    	        
	    	           if(listView.getSelectedValue() == null || listView.getSelectedValue().toString().contains(userName)) {
	    	        	   
	    	        	   return;
	    	           }
	    	           
	    	           String selectedItem = (String) listView.getSelectedValue();
	    	           
	    	           String friendIP, friendPort, friendName;
	    	           System.out.println(selectedItem);
	    	           friendName = selectedItem.split(":")[1];
	    	           System.out.println(friendName);
	    	           	    	      
	    	           int find = friendName.indexOf("(");	   
	    	           if(find != -1)
	    	        	   friendName = friendName.substring(0, find);

	    	           friendIP = selectedItem.split(":")[0].split(",")[1];
	    	           System.out.println(friendIP);

	    	           friendPort = selectedItem.split(":")[0].split(",")[0];	    	           
	    	           
	    	           try {
	    	        	   
		    	           if (!selectedItem.contains("Reply Now")) {
		    	        	   			    	          
									connectReq = new Socket(friendIP, Integer.parseInt(friendPort));
									
									Message message = Message.sendRequestChat(friendName, friendIP);
									
									ObjectOutputStream  sender = new ObjectOutputStream(connectReq.getOutputStream());
			                        sender.writeObject(message); sender.flush();
			                        ObjectInputStream listener = new ObjectInputStream(connectReq.getInputStream());
			                        message = (Message) listener.readObject();	
			                        
		    	           }    
		    	           
		    	           else {
		    	        	   
		    	        	   ObjectOutputStream sender = new ObjectOutputStream(socket.getOutputStream());
		    	        	   sender.writeObject(Tags.CHAT_ACCEPT_TAG); sender.flush();
		    	        	   sender.close();		    	        	   
		    	           }
	    	           }	    	           
	    	           catch (NumberFormatException | IOException | ClassNotFoundException e1) {
							e1.printStackTrace();
						}
	    	           	    	        
	    	           new ChatUI(userName, friendName, clientIP, friendIP, friendPort, connectReq, server);
	    	               	           
	    	        }
	    	    }
	    	};
	    	
	    	listView.addMouseListener(mouseListener);
	    	return listView;
	    }
	    
	    public class WaitForClient extends Thread {

			@Override
			public void run() {
				super.run();
				try {
					while (true) {
						if (checkConnect()) {

							ObjectOutputStream sender = new ObjectOutputStream(socket.getOutputStream());
							sender.writeObject(new Message("Response", userName, "Server Online", "", "")); sender.flush();
							sender.close();

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	    }
	    
	    private boolean checkConnect() throws Exception {
	    	
	    	socket = server.accept();
	    	
	    	ObjectInputStream listener = new ObjectInputStream(socket.getInputStream());		
	        Message message = (Message) listener.readObject();
		
		   for(int i = 0; i < listModel.getSize(); i++) {
			   if(listModel.getElementAt(i).toString().contains(message.content2)) {
				   listModel.setElementAt(listModel.getElementAt(i).toString() + "  Reply Now", i); 
			   } 
		   }
		   
	        return true;
	    }
	    
	    private void updateListView(String request) {	    	

	    	try {				
					String new_result = reRequest(request);
					String[] new_lst = getClient(new_result);
					
					if(request == "request")
					{
						listModel.removeAllElements();
						for(int i = 0; i < new_lst.length; i++) {
				    		listModel.addElement(new_lst[i]);
				    	}
	
						listview = new JList(listModel);
					}
					
					else {
						listModelPopup.removeAllElements();
						for(int i = 0; i < new_lst.length; i++) {
							listModelPopup.addElement(new_lst[i]);
				    	}
	
						popupListView = new JList(listModelPopup);
					}

			} catch (ClassNotFoundException | IOException e) {

				e.printStackTrace();
			}

	    }
	    
		public ClientUI(String ip, int port, String name, String message) throws Exception {
			
			clientIP = ip;
			clientPort = port;
			userName = name;
			userData = message;
			
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						ClientUI window = new ClientUI();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
	
		private String[] getClient(String data) {
		  
			  HashMap<String, String> lstClient = new HashMap<String, String>();	  
			  
			  String[] list = data.split(";");
			  	  
			  System.out.println(data);
				
			  for (int i = 0; i < list.length ; i++) {
				  
				  String[] temp = list[i].split(","); 
				  System.out.println(temp);
				  if (temp.length == 1) lstClient.put("",temp[0]);
				  else if (temp.length == 3) lstClient.put(temp[2] + "," + temp[1].substring(1, temp[1].length()) , temp[0]);		 
			  
			  }	  
			  		 
			  String lst[] = new String[lstClient.size()];
			  
			  int index = 0;
			  
			  for (Entry<String, String> entry : lstClient.entrySet()) {
			  
				  lst[index] = (entry.getKey() + ":" + entry.getValue()); 
				  index += 1;
		  
			  }	  		
			  return lst; 
		}
	 
		
		public static void main(String[] args) {
			// TODO Auto-generated method stub
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						ClientUI window = new ClientUI();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

}
