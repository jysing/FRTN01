import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.lcd.LCD;

public class Communication extends Thread {

	private static final long period = 10;
	private final ServerSocket serverSocket;
	private Socket server;
	private DataOutputStream out;
	private DataInputStream in;
	private Regul regul;

	public Communication(Regul regul, int port, int timeout, int priority)
			throws IOException {
		setPriority(priority);
		this.regul = regul;
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(timeout);
	}

	public void run() {
		while (true) {
			if(this.isConnected()) {
				String message = String.valueOf(regul.getU());
				send(message);				
			} else {
				LCD.drawString("It is not connected", 0, 1);
			}
			try {
				sleep(period);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void connect() throws IOException {
		server = serverSocket.accept();
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

	public void send(String message) {
		try {
			LCD.drawString(message, 0, 3);
			out.writeUTF(message);
		} catch (IOException e) {
			LCD.drawString("Can't send message", 0, 3);
		}//hehje
	}

	public String receive() throws IOException {
		String response = in.readUTF();
		return response;
	}
}