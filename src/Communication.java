import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

//package uk.co.moonsit.sockets;
/**
 *
 * @author Rupert Young
 */
public class Communication {

	private final ServerSocket serverSocket;
	private Socket server;
	private DataOutputStream out;
	private DataInputStream in;

	public Communication(int port, int timeout) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(timeout);
	}

	public void connect() throws IOException {
		System.out.println("Wait on " + serverSocket.getLocalPort() + "...");
		server = serverSocket.accept();

		System.out.println("Connected to " + server.getRemoteSocketAddress());
		out = new DataOutputStream(server.getOutputStream());
		in = new DataInputStream(server.getInputStream());
	}

	public int getPort() {
		int port = 0;
		if (serverSocket != null) {
			port = serverSocket.getLocalPort();
		}
		return port;
	}

	public boolean isConnected() {
		if (server == null) {
			return false;
		}
		return server.isConnected();
	}

	public String socketStatus() {
		return server == null ? "null" : "not null";
	}

	public void connectionLost() {
		server = null;
	}

	public String sendAndReceive(String message) throws IOException {
		out.writeUTF(message);
		String response = in.readUTF();
		return response;
	}

	@SuppressWarnings("null")
	public void theMain() {
		int port = 6666;
		int timeout = 30000;
		Communication ss = null;

		try {
			ss = new Communication(port, timeout);
		} catch (IOException ex) {
			Logger.getLogger(Communication.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		try {
			ss.connect();
		} catch (java.net.SocketTimeoutException ex) {
			Logger.getLogger(ex.toString());
		} catch (IOException ex) {
			Logger.getLogger(Communication.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		if (ss.isConnected()) {
			String response = null;
			try {
				response = ss.sendAndReceive("1");
				System.out.println("Response  " + response);
				response = ss.sendAndReceive("2");
				System.out.println("Response  " + response);
				response = ss.sendAndReceive("3");
				System.out.println("Response  " + response);
			} catch (IOException ex) {
				Logger.getLogger(Communication.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
	}
}