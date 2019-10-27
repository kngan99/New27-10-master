package Client;

import Client.ChatUI;
import Protocol.Message;
import Protocol.Tags;
import Protocol.DFile;
import Protocol.Decode;
import Protocol.Encode;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.TextArea;
import java.awt.Label;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.JScrollPane;

	public class ChatUI {
		
	    private JFrame frame;
	    private JPanel panelFile, panelMessage;
	    private JTextField textName, textPath, textSend;
	    private TextArea textDisplayChat;
	    private JButton btnDisconnect, btnSend, btnChoose, btnDel, btnUpLoad, btnAddFriend;
	    private JTextPane txtDisplayFile;
	    private JLabel fileState;
	    
	    private String userName = "";
	    private String friendName = "";
	    private String fileName = "";
	    private String friendIP = "";
	    private String clientIP = "";
	    private String friendPort = "";
	    private Socket socket;
	    private ServerSocket serverClient;
	    private String serverIP;
	    public boolean isStop = false, isSendFile = false, isReceiveFile = false;
	    private static String URL_DIR = System.getProperty("user.dir");
	    private static String TEMP = "/temp/";
	    private ChatRoom chat;
	    private int portServer = 0;
	    
	    public ChatUI() {
	        initializeFrame();
	        initializeChatBox();
	        initalizeMessagePanel();
	        initalizeFilePanel();
	        (new GetReply()).start();
	    }
	    
	    public ChatUI(String userName ,String friendName, String clientIP, String friendIP, String friendPort, Socket socket, ServerSocket serverClient) {
	    	
	    	this.userName = userName;
	    	this.friendName = friendName;
	    	this.socket = socket;
	    	this.serverClient = serverClient;
	    	this.friendIP = friendIP;
	    	this.friendPort = friendPort;
	    	this.clientIP = clientIP;
	    	
	    	EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						ChatUI window = new ChatUI();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	    }

	    private void initializeFrame() {
	        frame = new JFrame();
			frame.setResizable(false);
			frame.setBounds(200, 200, 688, 559);
			frame.getContentPane().setLayout(null);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    }

	    private void initializeChatBox() {
	    	JLabel lblClientIP = new JLabel("Chatting with: ");
			lblClientIP.setBounds(6, 12, 155, 16);
	        frame.getContentPane().add(lblClientIP);
	        textName = new JTextField("nameUser");
			textName.setBounds(110, 6, 200, 28);
			frame.getContentPane().add(textName);
			textName.setText(friendName);
	        textName.setColumns(10);
	        System.out.println(friendName);
	        textDisplayChat = new TextArea();
			textDisplayChat.setEditable(false);
			textDisplayChat.setBounds(6, 40, 668, 317);
	        frame.getContentPane().add(textDisplayChat);
	        
	        btnDisconnect = new JButton("DISCONNECT");
	        btnDisconnect.setBounds(560, 6, 113, 29);
	        frame.getContentPane().add(btnDisconnect);
	        
	        btnAddFriend = new JButton("ADD FRIEND");
	        btnAddFriend.setBounds(300, 6, 113, 29);
	        frame.getContentPane().add(btnAddFriend);
	        
			btnDisconnect.addActionListener(new ActionListener() {
			 	public void actionPerformed(ActionEvent arg0) {
			 		
			 		Socket disconnectReq;
					try {
						
						disconnectReq = new Socket(friendIP, Integer.parseInt(friendPort));
						Message message = Message.disconnect(Tags.CHAT_DENY_TAG, clientIP, friendIP);
						
						ObjectOutputStream  sender = new ObjectOutputStream(disconnectReq.getOutputStream());
	                    sender.writeObject(message); sender.flush();
	                    ObjectInputStream listener = new ObjectInputStream(disconnectReq.getInputStream());
	                    message = (Message) listener.readObject();		
	                
	                    sender.close();
	                    disconnectReq.close();
	                    
					} catch (NumberFormatException | IOException | ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
	        
	        btnAddFriend.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						serverIP = Inet4Address.getLocalHost().getHostAddress();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						
						Socket reqFriend = new Socket(serverIP,8080);
						ObjectOutputStream  sender = new ObjectOutputStream(reqFriend.getOutputStream());
						Message message = new Message("Add friend", userName, friendName, "", "SERVER");
		            	sender.writeObject(message); sender.flush();
		            	ObjectInputStream listener = new ObjectInputStream(reqFriend.getInputStream());
		            	Message recv = (Message)listener.readObject();
		            	
		            	if(recv.content1 == "ADD COMPLETE") {
		            		textDisplayChat.append("You and" + friendName + "has became friend");
		            	}
		            	
					} catch (IOException | ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
			
				}
	        });
	    }

	    
	    private void initalizeMessagePanel() {
	        panelMessage = new JPanel();
			panelMessage.setBounds(6, 420, 670, 71);
			panelMessage.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Message"));
			frame.getContentPane().add(panelMessage);
	        panelMessage.setLayout(null);
	        
	        textSend = new JTextField("");
			textSend.setBounds(10, 21, 479, 39);
			panelMessage.add(textSend);
	        textSend.setColumns(10);
	        
	        textSend.addKeyListener(new KeyListener() {

			 	@Override
			 	public void keyTyped(KeyEvent arg0) {

			 	}

			 	@Override
			 	public void keyReleased(KeyEvent arg0) {

			 	}

			 	@Override
			 	public void keyPressed(KeyEvent arg0) {
			 		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			 			send_and_displayText();
			 		};
			 	}
			 });


			btnSend = new JButton("SEND");
			btnSend.setBounds(530, 29, 80, 23);
	        panelMessage.add(btnSend);
	        
			btnSend.addActionListener(new ActionListener() {

			 	public void actionPerformed(ActionEvent arg0) {
			 		
			 		send_and_displayText();
			 		
			 	}
			 });
	    }
	    
	    private void appendText(String sender, String message) {
	    	
	    	textDisplayChat.append(sender + " : " + message);
	    	
	    }
	    
	    private void send_and_displayText() {
	    	
	    	Message send = sendMessages();		 
	 		ObjectOutputStream sender;
			try {
				sender = new ObjectOutputStream(socket.getOutputStream());
				sender.writeObject(send); sender.flush();
				sender.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}                  
			
			String text = textSend.getText();
			appendText(userName, text);
			textSend.setText("");
						
	    }
	    
	    private Message sendMessages() {
	    	
	    	String message = textSend.getText();
	 		Message sendMess = new Message("Send Message", userName, message, "Text Message", friendName); 		
	 		return sendMess;
	        
	    }
	    
	    private class GetReply extends Thread {
	    	@Override
			public void run() {
				super.run();
				try {
					while(true) {
											
					  String responseText = recvMessage(); 
					  
					  SwingUtilities.invokeLater(
							  
							  new Runnable() { public void run() { appendText(friendName, responseText); } });
					  
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    }
	    private String recvMessage() throws Exception {
	    	
	    	Socket socket = serverClient.accept();
	    	
	    	ObjectInputStream listener = new ObjectInputStream(socket.getInputStream());
	    	
	    	Message reply = (Message) listener.readObject();
	    	
	    	listener.close();
	    	
	    	return reply.content1;
	    		
	    }
	    
	    private void initalizeFilePanel() {
	        panelFile = new JPanel();
			panelFile.setBounds(6, 363, 670, 60);
			frame.getContentPane().add(panelFile);
	        panelFile.setLayout(null);

	        Label label = new Label("Link send file: ");
			label.setBounds(10, 21, 80, 22);
	        panelFile.add(label);
	        
	        textPath = new JTextField("");
			textPath.setBounds(100, 21, 388, 25);
			panelFile.add(textPath);
			textPath.setEditable(false);
	        
	        btnChoose = new JButton("Browse");
			btnChoose.setBounds(500, 21, 50, 25);
	        panelFile.add(btnChoose);
	        btnChoose.setBorder(BorderFactory.createEmptyBorder());
	        btnChoose.setContentAreaFilled(false);
	        btnChoose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(System
							.getProperty("user.home")));
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int result = fileChooser.showOpenDialog(frame);
					if (result == JFileChooser.APPROVE_OPTION) {
						isSendFile = true;
						String path_send = (fileChooser.getSelectedFile()
								.getAbsolutePath()) ;
						System.out.println(path_send);
						fileName = fileChooser.getSelectedFile().getName();
						textPath.setText(path_send);
					}
				}
			});
			btnChoose.setBorder(BorderFactory.createEmptyBorder());
			btnChoose.setContentAreaFilled(false);
			
			
	        
			// btnChoose.addActionListener(new ActionListener() {
			// 	public void actionPerformed(ActionEvent arg0) {
			// 		;
			// 	}
	        // });
	        
	        btnUpLoad = new JButton("Send");
			btnUpLoad.setBounds(550, 21, 50, 25);
	        panelFile.add(btnUpLoad);
	        
	        btnUpLoad.setContentAreaFilled(false);
			btnUpLoad.setBorder(BorderFactory.createEmptyBorder());

			btnUpLoad.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
						try {
							chat.sendMessage(Encode.sendFile(fileName));
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			});

	        btnDel = new JButton("Clear");
			btnDel.setBounds(600, 21, 50, 25);
	        panelFile.add(btnDel);
	        btnDel.setContentAreaFilled(false);
	        btnDel.setBorder(BorderFactory.createEmptyBorder());
	        
	       
	        txtDisplayFile = new JTextPane();
			txtDisplayFile.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			txtDisplayFile.setEditable(false);
			txtDisplayFile.setContentType( "text/html" );
			txtDisplayFile.setMargin(new Insets(6, 6, 6, 6));
			txtDisplayFile.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
			txtDisplayFile.setBounds(6, 59, 670, 291);
			appendToPane(txtDisplayFile, "<div class='clear' style='background-color:white'></div>"); //set default background
				    
			frame.getContentPane().add(txtDisplayFile);
		
	        
	        JScrollPane scrollPane = new JScrollPane(txtDisplayFile);
	        scrollPane.setBounds(22, 307, 638, 43);
	        frame.getContentPane().add(scrollPane);
	        
	        fileState = new JLabel("");
	        fileState.setBounds(30, 502, 46, 14);
	        frame.getContentPane().add(fileState);
	        
			btnDel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					;
				}
			});
			
	    }

	    
		public class ChatRoom extends Thread {

			private Socket connect;
			private ObjectOutputStream outPeer;
			private ObjectInputStream inPeer;
			private boolean continueSendFile = true, finishReceive = false;
			private int sizeOfSend = 0, sizeOfData = 0, sizeFile = 0,
					sizeReceive = 0;
			private String nameFileReceive = "";
			private InputStream inFileSend;
			private DFile dataFile;

			public ChatRoom(Socket connection, String name, String guest)
					throws Exception {
				connect = new Socket();
				connect = connection;
				friendName = guest;
			}

			@Override
			public void run() {
				super.run();
				OutputStream out = null;
				while (!isStop) {
					try {
						inPeer = new ObjectInputStream(connect.getInputStream());
						Object obj = inPeer.readObject();
						if (obj instanceof String) {
							String msgObj = obj.toString();
							if (msgObj.equals(Tags.CHAT_CLOSE_TAG)) {
								isStop = true;
								Tags.show(frame, friendName 
										+ " closed chat with you! This windows will also be closed.", false);
								try {	
									isStop = true;
									frame.dispose();
									chat.sendMessage(Tags.CHAT_CLOSE_TAG);
									chat.stopChat();
									System.gc();
								} catch (Exception e) {
									e.printStackTrace();
								}
								connect.close();
								break;
							}
							if (Decode.checkFile(msgObj)) {
								isReceiveFile = true;
								nameFileReceive = msgObj.substring(10,
										msgObj.length() - 11);
								int result = Tags.show(frame, friendName
										+ " send file " + nameFileReceive
										+ " for you", true);
								if (result == 0) {
									File fileReceive = new File(URL_DIR + TEMP
											+ "/" + nameFileReceive);
									if (!fileReceive.exists()) {
										fileReceive.createNewFile();
									}
									String msg = Tags.FILE_REQ_ACK_OPEN_TAG
											+ Integer.toBinaryString(portServer)
											+ Tags.FILE_REQ_ACK_CLOSE_TAG;
									sendMessage(msg);
								} else {
									sendMessage(Tags.FILE_REQ_NOACK_TAG);
								}
							} else if (Decode.checkFeedBack(msgObj)) {
								btnChoose.setEnabled(false);

								new Thread(new Runnable() {
									public void run() {
										try {
											sendMessage(Tags.FILE_DATA_BEGIN_TAG);
											updateChat_notify("You are sending file: " + fileName);
											isSendFile = false;
											sendFile(textPath.getText());
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}).start();
							} else if (msgObj.equals(Tags.FILE_REQ_NOACK_TAG)) {
								Tags.show(frame, friendName
										+ " don't want receive file", false);
							} else if (msgObj.equals(Tags.FILE_DATA_BEGIN_TAG)) {
								finishReceive = false;
								
								out = new FileOutputStream(URL_DIR + TEMP
										+ nameFileReceive);
							} else if (msgObj.equals(Tags.FILE_DATA_CLOSE_TAG)) {
								updateChat_receive("You receive file: " + nameFileReceive + " with size " + sizeReceive + " KB");
								sizeReceive = 0;
								out.flush();
								out.close();
								
								new Thread(new Runnable() {

									@Override
									public void run() {
										showSaveFile();
									}
								}).start();
								finishReceive = true;
//							} else if (msgObj.equals(Tags.FILE_DATA_CLOSE_TAG) && isFileLarge == true) {
//								updateChat_receive("File " + nameFileReceive + " too large to receive");
//								sizeReceive = 0;
//								out.flush();
//								out.close();
//								lblReceive.setVisible(false);
//								finishReceive = true;
							} else {
								String message = Decode.getMessage(msgObj);
								updateChat_receive(message);
							}
						} else if (obj instanceof DFile) {
							DFile data = (DFile) obj;
							++sizeReceive;
							out.write(data.data);
						}
					} catch (Exception e) {
						File fileTemp = new File(URL_DIR + TEMP + nameFileReceive);
						if (fileTemp.exists() && !finishReceive) {
							fileTemp.delete();
						}
					}
				}
			}

			
			private void getData(String path) throws Exception {
				File fileData = new File(path);
				if (fileData.exists()) {
					sizeOfSend = 0;
					dataFile = new DFile();
					sizeFile = (int) fileData.length();
					sizeOfData = sizeFile % 1024 == 0 ? (int) (fileData.length() / 1024)
							: (int) (fileData.length() / 1024) + 1;
					inFileSend = new FileInputStream(fileData);
				}
			}

			public void sendFile(String path) throws Exception {
				getData(path);
				fileState.setVisible(true);
				if (sizeOfData > Tags.MAX_MSG_SIZE/1024) {
					fileState.setText("File is too large...");
					inFileSend.close();
//					isFileLarge = true;
//					sendMessage(Tags.FILE_DATA_CLOSE_TAG);
					textPath.setText("");
					btnChoose.setEnabled(true);
					isSendFile = false;
					inFileSend.close();
					return;
				}
				
				
				fileState.setText("Sending ...");
				do {
					System.out.println("sizeOfSend : " + sizeOfSend);
					if (continueSendFile) {
						continueSendFile = false;
//						updateChat_notify("If duoc thuc thi: " + String.valueOf(continueSendFile));
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									inFileSend.read(dataFile.data);
									sendMessage(dataFile);
									sizeOfSend++;
									if (sizeOfSend == sizeOfData - 1) {
										int size = sizeFile - sizeOfSend * 1024;
										dataFile = new DFile(size);
									}
									
									if (sizeOfSend >= sizeOfData) {
										inFileSend.close();
										isSendFile = true;
										sendMessage(Tags.FILE_DATA_CLOSE_TAG);
										fileState.setVisible(false);
										isSendFile = false;
										textPath.setText("");
										btnChoose.setEnabled(true);
										updateChat_notify("File sent complete");
										inFileSend.close();
									}
									continueSendFile = true;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					}
				} while (sizeOfSend < sizeOfData);
			}

			private void showSaveFile() {
				while (true) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(System
							.getProperty("user.home")));
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int result = fileChooser.showSaveDialog(frame);
					if (result == JFileChooser.APPROVE_OPTION) {
						File file = new File(fileChooser.getSelectedFile()
								.getAbsolutePath() + "/" + nameFileReceive );
						if (!file.exists()) {
							try {
								file.createNewFile();
								Thread.sleep(1000);
								InputStream input = new FileInputStream(URL_DIR
										+ TEMP + nameFileReceive);
								OutputStream output = new FileOutputStream(
										file.getAbsolutePath());
								copyFileReceive(input, output, URL_DIR + TEMP
										+ nameFileReceive);
							} catch (Exception e) {
								Tags.show(frame, "Your file receive has error!!!",
										false);
							}
							break;
						} else {
							int resultContinue = Tags.show(frame,
									"File is exists. You want save file?", true);
							if (resultContinue == 0)
								continue;
							else
								break;
						}
					}
				}
			}
			
			
			//void send Message
			public synchronized void sendMessage(Object obj) throws Exception {
				outPeer = new ObjectOutputStream(connect.getOutputStream());
				// only send text
				if (obj instanceof String) {
					String message = obj.toString();
					outPeer.writeObject(message);
					outPeer.flush();
					if (isReceiveFile)
						isReceiveFile = false;
				} 
				// send attach file
				else if (obj instanceof DFile) {
					outPeer.writeObject(obj);
					outPeer.flush();
				}
			}

			public void stopChat() {
				try {
					connect.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void copyFileReceive(InputStream inputStr, OutputStream outputStr,
				String path) throws IOException {
			byte[] buffer = new byte[1024];
			int lenght;
			while ((lenght = inputStr.read(buffer)) > 0) {
				outputStr.write(buffer, 0, lenght);
			}
			inputStr.close();
			outputStr.close();
			File fileTemp = new File(path);
			if (fileTemp.exists()) {
				fileTemp.delete();
			}
		}
		
		// send html to pane
		  private void appendToPane(JTextPane tp, String msg){
		    HTMLDocument doc = (HTMLDocument)tp.getDocument();
		    HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
		    try {
		    	
		      editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
		      tp.setCaretPosition(doc.getLength());
		      
		    } catch(Exception e){
		      e.printStackTrace();
		    }
		  }
		  
		  
		  public void updateChat_receive(String msg) {
				appendToPane(txtDisplayFile, "<div class='left' style='width: 40%; background-color: #f1f0f0;'>"+ msg +"</div>");
			}
		  public void updateChat_notify(String msg) {
				appendToPane(txtDisplayFile, "<table class='bang' style='color: white; clear:both; width: 100%;'>"
						+ "<tr align='right'>"
						+ "<td style='width: 59%; '></td>"
						+ "<td style='width: 40%; background-color: #f1c40f;'>" + msg 
						+"</td> </tr>"
						+ "</table>");
			}
	    public static void main(String[] args) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						ChatUI window = new ChatUI();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
