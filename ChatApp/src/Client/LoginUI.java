package Client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Protocol.Peer;
import Protocol.Tags;
import Protocol.Message;
import Client.ClientUI;
import Client.LoginUI;

public class LoginUI {

	
	 private static String WELCOME_MESSAGE = "CONNECT WITH SERVER\n";
	 private static String ASWR_IP_MESSAGE = "IP Server: ";
	 private static String ASWR_PORT_MESSAGE = "Port Server: ";
	 private static String ASWR_NAME_MESSAGE = "User Name: ";
	 private static String PASSWORD_MESSAGE = "Password: ";
	 private static String LOGIN_BTN_MESSAGE = "Login";
	 private static String SIGNUP_BTN_MESSAGE = "Sign up";

	 private static String NAME_FAILED = "CONNECT WITH OTHER NAME";
	 private static String CLIENT_EXIST = "CLIENT IS EXISTED";
	 private static String SERVER_NOT_START = "SERVER NOT START";
	    
	 private static String DEFAULT_SERVER_PORT = "8080";
	    
	 private JFrame frame;
	 private JLabel lbError;
	 private JTextField textIP, textPort, textName, textPass;
	 private JButton btnLogin, btnClear;
	    
	public LoginUI() {
        initializeFrame();
        initializeLabel();
        initializeTextBox();
        initializeButton();
    }
    
    private void initializeFrame() {
        frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 448, 204);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
    }

    private void initializeLabel() {
        JLabel lbWelcome = new JLabel(WELCOME_MESSAGE);
		lbWelcome.setBounds(10, 11, 258, 14);
        frame.getContentPane().add(lbWelcome);
        
        JLabel lbAnswerIP = new JLabel(ASWR_IP_MESSAGE);
		lbAnswerIP.setBounds(10, 50, 86, 20);
        frame.getContentPane().add(lbAnswerIP);
        
        JLabel lbAnswerPort = new JLabel(ASWR_PORT_MESSAGE);
		lbAnswerPort.setBounds(263, 53, 95, 14);
        frame.getContentPane().add(lbAnswerPort);

        JLabel lbAnswerName = new JLabel(ASWR_NAME_MESSAGE);
		lbAnswerName.setBounds(10, 82, 86, 17);
        frame.getContentPane().add(lbAnswerName);
        
        JLabel lbPass = new JLabel(PASSWORD_MESSAGE);
		lbPass.setBounds(10, 115, 86, 17);
        frame.getContentPane().add(lbPass);

        lbError = new JLabel("");
		lbError.setBounds(120, 150, 380, 14);
		frame.getContentPane().add(lbError);
    }

    private void initializeTextBox() {
        textPort = new JTextField();
		textPort.setText(DEFAULT_SERVER_PORT);
		textPort.setBounds(356, 50, 65, 20);
        frame.getContentPane().add(textPort);
        
        textIP = new JTextField();
        textIP.setBounds(101, 46, 152, 28);
        frame.getContentPane().add(textIP);

        textName = new JTextField();
		textName.setBounds(101, 77, 152, 30);
        frame.getContentPane().add(textName);
        
        textPass = new JTextField();
		textPass.setBounds(101, 110, 152, 30);
        frame.getContentPane().add(textPass);
    }

	private void initializeButton() {
	        
	    btnLogin = new JButton(LOGIN_BTN_MESSAGE);
	    btnLogin.setBounds(263, 77, 169, 28);
	    frame.getContentPane().add(btnLogin);
	    btnLogin.addActionListener(new ActionListener() {
	
	    public void actionPerformed(ActionEvent arg0) {
	    	String user = textName.getText();
	        String pass = textPass.getText();
	        String IP = textIP.getText();
	        lbError.setVisible(false);
	               
	        try {
	        	InetAddress serverIP;
	            int serverPort = Integer.parseInt(textPort.getText());
	
	            Random random = new Random();
	            int peerPort = 10000 + random.nextInt() % 10000;
	            Message message = new Message("login", user, pass, Integer.toString(peerPort), "SERVER");
	            if (!user.isEmpty() && !pass.isEmpty()) {
	            	serverIP = InetAddress.getByName(IP);
	            	Socket clientSocket = new Socket(serverIP, serverPort);
	            	ObjectOutputStream  sender = new ObjectOutputStream(clientSocket.getOutputStream());
	            	sender.writeObject(message); sender.flush();
	            	ObjectInputStream listener = new ObjectInputStream(clientSocket.getInputStream());
	            	message = (Message) listener.readObject();
	                        
	            	System.out.println(message);
	            	clientSocket.close();
	            }
	            else {
	            	lbError.setText("Please enter your username/password!");
	            	lbError.setVisible(true);
	            }
	                        
					/*
					 * if (message.content1.equals("TRUE")) { lbError.setText(message.type);
					 * lbError.setVisible(true); return; }
					 */
	            System.out.println("aaaa");
	            new ClientUI(IP, peerPort, message.recipient, message.content1);
	            System.out.println("aaaa");
				frame.dispose();
	        }
	        catch (Exception e) {
	            lbError.setText(SERVER_NOT_START);
				lbError.setVisible(true);
				e.printStackTrace();
	        }
	    }
	    });
	    
	    JButton btnSignup = new JButton("Sign up");
	    btnSignup.setBounds(263, 110, 169, 28);
	    frame.getContentPane().add(btnSignup);
	    btnSignup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String user = textName.getText();
		        String pass = textPass.getText();
		        String IP = textIP.getText();
				InetAddress serverIP;
				try {
					serverIP = InetAddress.getByName(IP);
					int serverPort = Integer.parseInt(textPort.getText());
		
		            Random random = new Random();
		            int peerPort = 10000 + random.nextInt() % 10000;
		            Message message = new Message("signup", user, pass, Integer.toString(peerPort), "SERVER");
		            
		            if (!user.isEmpty() && !pass.isEmpty()) {
		            	Socket clientSocket = new Socket(serverIP, serverPort);
		            	ObjectOutputStream  sender = new ObjectOutputStream(clientSocket.getOutputStream());
		            	sender.writeObject(message); sender.flush();
		            	ObjectInputStream listener = new ObjectInputStream(clientSocket.getInputStream());
		            	message = (Message) listener.readObject();
		            	System.out.println(message);
		            	clientSocket.close();
		            }
		            else {
		            	lbError.setText("Please enter your username/password!");
		            	lbError.setVisible(true);
		            }
		            if (message.content1.equals("TRUE")) {
		            	lbError.setText(message.type);
						lbError.setVisible(true);
						return;
		            }
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
			}
	    	
	    });
	}
	                        
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						LoginUI window = new LoginUI();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		});
	}

}
