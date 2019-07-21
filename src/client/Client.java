package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class Client implements ActionListener {

	private final static int PORT = 36979;

	private static Scanner scanner;
	private static InetAddress ip;
	private static Socket socket;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private static Thread sendMessage, receiveMessage;

	private JPanel middlePanel;
	private JTextArea display;
	private JScrollPane scroll;
	private JTextField inputText;
	private JFrame frame;

	public Client() {

		middlePanel = new JPanel();
		middlePanel.setBorder(new TitledBorder(new EtchedBorder(), "Display Area"));

		// create the middle panel components
		display = new JTextArea(22, 58);
		display.setEditable(false); // set textArea non-editable
		scroll = new JScrollPane(display);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		// Add Textarea in to middle panel
		middlePanel.add(scroll);

		// Text input.
		inputText = new JTextField(58);
		inputText.addActionListener(this);
		middlePanel.add(inputText);

		// My code
		frame = new JFrame();
		frame.add(middlePanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		scanner = new Scanner(System.in);

		try {
			// Get localhost ip address.
			ip = InetAddress.getByName("crewchat.hopto.org");
//			ip = InetAddress.getByName("67.61.132.91");

			// Open socket & bind to port.
			socket = new Socket(ip, PORT);

			// Get data streams.
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());

			// Thread for sending messages.
//			sendMessage = new Thread(new Runnable() {
//				@Override
//				public void run() {
//					String message;
//					while (true) {
//						// Read in message to deliver to server.
//						message = inputText.getText();
//
//						try {
//							// Write message to output stream.
//							dos.writeUTF(message);
//						} catch (IOException e) {
//							System.err.println("ERROR:  COULD NOT SEND MESSAGE TO SERVER!");
//							e.printStackTrace();
//							System.exit(-1);
//						}
//					}
//				}
//			});

			// Thread for receiving messages.
			receiveMessage = new Thread(new Runnable() {
				@Override
				public void run() {
					String message;
					while (true) {
						try {
							// Receive the message sent to us.
							message = dis.readUTF();
							// Display the message.
							System.out.println(message);
							display.append(message + "\n");
						} catch (IOException e) {
							System.err.println("ERROR:  COULD NOT RECEIVE MESSAGE!");
							e.printStackTrace();
							System.exit(-1);
						}
					}
				}
			});

//			sendMessage.start();
			receiveMessage.start();

		} catch (UnknownHostException e) {
			System.err.println("ERROR:  COULD NOT GET OUR IP ADDRESS!");
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("ERROR:  COULD NOT CREATE SOCKET AND BIND TO PORT!");
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public static void main(String[] args) {

		new Client();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		try {
			dos.writeUTF(inputText.getText());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inputText.setText("");
	}

}
