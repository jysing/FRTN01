package Lego;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.lcd.LCD;

public class Communication extends Thread {

	private static final long period = 50;//10
	private static final double posDiff = 0.00000001;
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
		PIDParameters p;
		while (true) {
			String message;
			if (this.isConnected()) {
				message = "Fel";
				switch (i) {
				case 0:
					message = "U" + String.valueOf(regul.getU());
					break;
				case 1:
					message = "E" + String.valueOf(regul.getE_inner());
					break;
				case 2:
					message = "A" + String.valueOf(regul.getA());
					break;
				case 3:
					message = "V" + String.valueOf(regul.getV());
					break;
				case 4:
					message = "P" + String.valueOf(regul.getP());
					break;
				case 5:
					message = "B" + String.valueOf(regul.getB());
					break;
				case 6:
					message = "W" + String.valueOf(regul.getE_inner());
					break;
				}
				i++;
				i = i % 7;
				send(message);
				message = "Fel";
				try {
					message = receive();
				} catch (IOException e1) {
					message = "Fel";
				}
				if (!message.equals("Fel")) {
					switch (message.charAt(0)) {
					case 'N':
						p = newParam(message);
						regul.setPIDAngParameters(p);
						regul.calculateOffset();
						break;
					case 'M':
						p = newParam(message);
						regul.setPIDPosParameters(p);
						regul.calculateOffset();
						break;
					case 'S':
						regul.setManualFalse();
						break;
					case 'C':
						regul.calculateOffset();
						break;
					case 'F':
						LCD.drawString("moving forward", 0, 4);
						regul.manualControl(1, 1, -0.005);
						break;
					case 'B':
						regul.manualControl(1, 1, 0.001);
						break;
					case 'L':
						regul.manualControl(0.7, 1, -0.0005);
						break;
					case 'R':
						regul.manualControl(1, 0.7, -0.0005);
						break;
					}
				}
			} else {
				LCD.drawString("It is not connected", 0, 3);
				try {
					this.connect();
				} catch (IOException e) {
					LCD.drawString("Could not reconnect", 0, 3);
					sendPIDAngValues();
					sendPIDPosValues();
				}
			}

			try {
				sleep(period);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private PIDParameters newParam(String message) {
		String[] newParam = message.substring(1).split("\n"); // starts at 1
		PIDParameters p = new PIDParameters();
		p.Beta = Double.valueOf(newParam[1].split(":")[1]);
		p.K = Double.valueOf(newParam[2].split(":")[1]);
		p.Ti = Double.valueOf(newParam[3].split(":")[1]);
		p.Tr = Double.valueOf(newParam[4].split(":")[1]);
		p.Td = Double.valueOf(newParam[5].split(":")[1]);
		p.N = Double.valueOf(newParam[6].split(":")[1]);
		return p;
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
			if (in.available() >= 8)
				response = in.readUTF();
		} catch (IOException e) {
			response = "Fel";
		}
		return response;
	}

	public void sendPIDAngValues() {
		send(regul.sendPIDAngValues());
	}

	public void sendPIDPosValues() {
		send(regul.sendPIDPosValues());
	}
}