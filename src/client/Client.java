package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private final static int PORT = 36979;

	private static Scanner scanner;
	private static InetAddress ip;
	private static Socket socket;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private static Thread sendMessage, receiveMessage;

	public static void main(String[] args) {

		scanner = new Scanner(System.in);

		try {
			// Get localhost ip address.
			ip = InetAddress.getByName("localhost");

			// Open socket & bind to port.
			socket = new Socket(ip, PORT);

			// Get data streams.
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());

			// Thread for sending messages.
			sendMessage = new Thread(new Runnable() {
				@Override
				public void run() {
					String message;
					while (true) {
						// Read in message to deliver to server.
						message = scanner.nextLine();

						try {
							// Write message to output stream.
							dos.writeUTF(message);
						} catch (IOException e) {
							System.err.println("ERROR:  COULD NOT SEND MESSAGE TO SERVER!");
							e.printStackTrace();
							System.exit(-1);
						}
					}
				}
			});

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
						} catch (IOException e) {
							System.err.println("ERROR:  COULD NOT RECEIVE MESSAGE!");
							e.printStackTrace();
							System.exit(-1);
						}
					}
				}
			});
			
			sendMessage.start();
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

}
