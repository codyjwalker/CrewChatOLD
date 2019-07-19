package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable {

	private final DataInputStream dis;
	private final DataOutputStream dos;

	private String name;
	private Socket socket;
	private boolean currentlyLoggedIn;

	public ClientHandler(Socket socket, String name, DataInputStream dis, DataOutputStream dos) {
		this.socket = socket;
		this.name = name;
		this.dis = dis;
		this.dos = dos;
		this.currentlyLoggedIn = true;
	}

	@Override
	public void run() {
		String recvStr, message, recipient;
		StringTokenizer stringToken;
		while (true) {
			
			// Receive the string from the client.
			try {
				recvStr = dis.readUTF();
				System.out.println(recvStr);
				
				// Check to see if client wishes to disconnect.
				if (recvStr.equals("logout")) {
					this.currentlyLoggedIn = false;
					this.socket.close();
					break;
				}
				
				// Break up string into two parts: recipient and message.
				stringToken = new StringTokenizer(recvStr, "#");
				message = stringToken.nextToken();
				recipient = stringToken.nextToken();
				
				// Search for recipient in connected devices list.
				for (ClientHandler clientHandler : Server.getActiveClients()) {
					// If recipient found, write on its output stream.
					if (clientHandler.name.equals(recipient) && clientHandler.currentlyLoggedIn) {
						clientHandler.dos.writeUTF(this.name + " : " + message);
						break;
					}
				}
			} catch (IOException e) {
				System.err.println("ERROR:  COULD NOT RECEIVE STRING!");
				e.printStackTrace();
				System.exit(-1);
			}
		}
		// Cleanup.
		try {
			this.dis.close();
			this.dos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
