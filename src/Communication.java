import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import lejos.hardware.lcd.LCD;

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

	/*
	public void theMain() {
		int port = 6666;
		int timeout = 30000;
		Communication ss = null;

		try {
			LCD.drawString("1", 0, 1);
			ss = new Communication(port, timeout);
			LCD.drawString("2", 0, 1);
		} catch (IOException ex) {
			Logger.getLogger(Communication.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		try {
			LCD.drawString("3", 0, 1);
			ss.connect();
			LCD.drawString("4", 0, 1);
		} catch (java.net.SocketTimeoutException ex) {
			Logger.getLogger(ex.toString());
		} catch (IOException ex) {
			Logger.getLogger(Communication.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		LCD.drawString("5", 0, 1);
		if (ss.isConnected()) {
			LCD.drawString("6", 0, 1);
			String response = null;
			try {
				LCD.drawString("7", 0, 1);
				response = ss.sendAndReceive("1");
				System.out.println("Response  " + response);
				response = ss.sendAndReceive("2");
				System.out.println("Response  " + response);
				response = ss.sendAndReceive("3");
				System.out.println("Response  " + response);
				LCD.drawString("8", 0, 1);
			} catch (IOException ex) {
				Logger.getLogger(Communication.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
	}
	*/
}