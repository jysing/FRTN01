package PC;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketClient {

	private final String server;
	private final int port;
	private Socket client;
	private DataInputStream in;
	private DataOutputStream out;

	public SocketClient(String server, int port) {
		this.server = server;
		this.port = port;
	}

	public void connect() throws IOException {
		System.out.println("Connecting to " + server + " on port " + port);
		client = new Socket(server, port);
		System.out.println("Just connected to "
				+ client.getRemoteSocketAddress());
		in = new DataInputStream(client.getInputStream());
		out = new DataOutputStream(client.getOutputStream());
	}

	public void send(String message) { // throws IOException {
		try {
			out.writeUTF(message);
			out.flush();
			System.out.println("Managed to send: " + message);
		} catch (IOException e) {
			System.out.println("SocketClient: Send " + message + " failed.");
		}
	}

	public String receive() {
		String message;
		try {
			message = in.readUTF();
		} catch (IOException e) {
			message = "Fel";
		}
		return message;
	}

	public boolean isConnected() {
		if (client == null)
			return false;
		return client.isConnected();
	}

	public void close() throws IOException {
		client.close();
	}
}