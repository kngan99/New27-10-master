package Server;

import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Server.Server;
import Server.ServerUI;

public class ServerUI extends javax.swing.JFrame {
	
    private JFrame frame;
    private JTextField textIP, textPort;
    private static TextArea textMessage;
    public static String filePath = "E:/New26-10ver2-master/NewChat26-10-master/ChatApp/src/Server/Data.xml";
    private JFileChooser fileChooser;
    private Server server;
    private JTextField textFile;

    public ServerUI() {
        initializeFrame();
        initializeLabel();
        initializeTextBox();
        initializeButton();
        fileChooser = new JFileChooser();
    }

    private void initializeFrame() {
        frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(200, 200, 622, 442);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
    }

    private void initializeLabel() {
        JLabel lblIP = new JLabel("IP ADDRESS: ");
		lblIP.setBounds(36, 55, 95, 16);
        frame.getContentPane().add(lblIP);
        
        JLabel lbPort = new JLabel("PORT: ");
		lbPort.setBounds(315, 55, 61, 16);
        frame.getContentPane().add(lbPort);
        
        JLabel lbDB = new JLabel("Database File: ");
		lbDB.setBounds(36, 20, 106, 16);
		frame.getContentPane().add(lbDB);
    }

    private void initializeTextBox() {
        textIP = new JTextField();
		textIP.setBounds(130, 49, 176, 28);
        frame.getContentPane().add(textIP);
        
        try {
            InetAddress localhost = Inet4Address.getLocalHost();
            String localhostAddress = localhost.getHostAddress(); 
			textIP.setText(localhostAddress);
        } catch (UnknownHostException e) {e.printStackTrace();}
        
        textPort = new JTextField();
        textPort.setBounds(366, 49, 208, 28);
        textPort.setText("8080");
        frame.getContentPane().add(textPort);
        
        textMessage = new TextArea();					
		textMessage.setEditable(false);
		textMessage.setBounds(6, 130, 602, 270);
		frame.getContentPane().add(textMessage);
		
		textFile = new JTextField();
		textFile.setText(filePath);
		textFile.setEditable(false);
		textFile.setBounds(130, 20, 310, 20);
		frame.getContentPane().add(textFile);
    }

    private void initializeButton() {
        JButton btnStart = new JButton("START");
        btnStart.setBounds(36, 90, 269, 29);
		frame.getContentPane().add(btnStart);

		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					server = new Server(8080);
					ServerUI.updateMessage("START SERVER");
				} catch (Exception e) {
					ServerUI.updateMessage("START ERROR");
					e.printStackTrace();
				}
			}
        });

        JButton btnStop = new JButton("STOP");
        btnStop.setBounds(315, 90, 260, 29);
        frame.getContentPane().add(btnStop);
        
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
                try {
					server.stopserver();
					ServerUI.updateMessage("STOP SERVER");
				} catch (Exception e) {
					e.printStackTrace();
					ServerUI.updateMessage("STOP SERVER");
				}
			}
		});
		
		
		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.setBounds(470, 20, 100, 20);
		frame.getContentPane().add(btnBrowse);
		
		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnBrowseActionPerformed();
			}
		});
    }
    
    private void btnBrowseActionPerformed() {
    	fileChooser.showDialog(this, "Select");
		File file = fileChooser.getSelectedFile();
		
		if (file != null) {
            filePath = file.getPath();
            if (this.isWin32()) { filePath = filePath.replace("\\", "/"); }
            textFile.setText(filePath);
        }
	}
    
    public boolean isWin32(){
        return System.getProperty("os.name").startsWith("Windows");
    }
    
    public static void updateMessage(String msg) {
		textMessage.append(msg + "\n");
	}

    public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerUI window = new ServerUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
