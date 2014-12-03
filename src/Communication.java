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
				switch(i) {
				case 0: message = "U" + String.valueOf(regul.getU());
					break;
				case 1: message = "E" + String.valueOf(regul.getE());
					break;
				case 2: message = "A" + String.valueOf(regul.getA());
					break;
				case 3: message = "V" + String.valueOf(regul.getV());
					break;
				case 4: message = "P" + String.valueOf(regul.getP());
					break;
				case 5: message = "B" + String.valueOf(regul.getB());
					break;
				}
				i++;
				i = i % 6;
				send(message);
				try {
					message = receive();
					LCD.drawString(message, 0, 5);
				} catch (IOException e1) {
					message = "Fel";
				}
				switch(message) {
				case "S": regul.setMotor(0, 0);
					break;
				case "C": regul.calculateOffset();
					break;
				case "F": regul.setMotor(20, 20);
					break;
				case "B": regul.setMotor(-20, -20);
					break;
				case "L": regul.setMotor(-10, 10);
					break;
				case "R": regul.setMotor(10, -10);
					break;
				case "Fel": LCD.drawString("Not doing stuff", 0, 6);
					break;
				}
			} else {
				LCD.drawString("It is not connected", 0, 3);
			}

			try {
				sleep(period);
			} catch (InterruptedException e) {
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
			out.writeUTF(message);
			out.flush();
		} catch (IOException e) {
			LCD.drawString("Can't send message", 0, 3);
		}
	}

	public String receive() throws IOException {
		String response = "Fel";
		try {
			if(in.available() >= 8) response = in.readUTF();
		} catch (IOException e) {
			response = "Fel";
		}
		return response;
	}
}