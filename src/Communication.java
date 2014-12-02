import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.lcd.LCD;

public class Communication extends Thread {

	private static final long period = 20;
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
		int i = 0;
		while (true) {
			String message;
			if (this.isConnected()) {
				message = "Fel";
				if (i == 0) {
					message = "U" + String.valueOf(regul.getU());
				} else if (i == 1) {
					message = "E" + String.valueOf(regul.getE());
				} else if (i == 2) {
					message = "A" + String.valueOf(regul.getA());
				} else if (i == 3) {
					message = "V" + String.valueOf(regul.getV());
				} else if (i == 4) {
					message = "P" + String.valueOf(regul.getP());
				} else if (i == 5) {
					message = "B" + String.valueOf(regul.getB());
				} else {
					LCD.drawString("Something is very wrong with i", 0, 4);
				}
				i++;
				i = i % 6;
				send(message);
			} else {
				LCD.drawString("It is not connected", 0, 3);
			}
			
			try {
				message = receive();
				LCD.drawString(message, 0, 7);
			} catch (IOException e1) {
				message = "Fel";
			}
			if(message.charAt(0)=='#'){
				regul.calculateOffset();
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
		LCD.drawString("Wait on " + serverSocket.getLocalPort() + "...", 0, 0);
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

	public void send(String message) {
		try {
			LCD.drawString(message, 0, 3);
			out.writeUTF(message);
		} catch (IOException e) {
			LCD.drawString("Can't send message", 0, 3);
		}
	}

	public String receive() throws IOException {
		String response;
    	try {
			response = in.readUTF();
		} catch (IOException e) {
			response = "Fel";
		}
    	return response;
	}
}