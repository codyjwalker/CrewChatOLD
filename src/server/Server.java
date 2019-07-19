package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

	private final static int PORT = 36979;

	private static int numClients;
	private static Vector<ClientHandler> activeClients;
	private static Socket socket;
	private static ServerSocket serverSocket;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private static ClientHandler clientHandler;
	private static Thread thread;

	public static void main(String[] args) {

		activeClients = new Vector<>();
		numClients = 0;

		// Bind to port.
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("ERROR:  COULD NOT BIND TO PORT!");
			e.printStackTrace();
			System.exit(-1);
		}

		// Infinite loop for receiving client requests.
		while (true) {
			// Accept incoming request.
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				System.err.println("ERROR:  PROBLEM WITH accept()!");
				e.printStackTrace();
				System.exit(-1);
			}
			System.out.println("NEW CLIENT REQUEST RECEIVED: " + socket);

			// Get data streams.
			try {
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.err.println("ERROR:  COULD NOT GET DATA STREAMS!");
				e.printStackTrace();
				System.exit(-1);
			}

			// Create new ClientHandler object for handling this client's request.
			System.out.println("CREATING NEW HANDLER FOR CLIENT.");
			clientHandler = new ClientHandler(socket, "client" + numClients, dis, dos);

			// Create new Thread out of ClientHandler.
			thread = new Thread(clientHandler);

			// Add client to activeClients list.
			System.out.println("ADDING CLIENT TO ACTIVE CLIENT LIST.");
			activeClients.add(clientHandler);

			// Start the new thread & increment numClients.
			thread.start();
			numClients++;
		}
	}
}
