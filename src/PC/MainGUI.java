package PC;

import java.io.IOException;

public class MainGUI {
	private static final String serverName = "10.0.1.1";
	private static final int port = 6666;

	public static void main(String[] args) {
		SocketClient sc = new SocketClient(serverName, port);
		try {
			sc.connect();
		} catch (IOException e) {
			System.out.println("Failed to connect");
		}
		try {
			Graph graph = new Graph(sc);
			graph.createWindow("Control signal", "Time", "Value 1", "Control signal");
			graph.createWindow("Inner error", "Time", "Value 2", "Inner error");
			graph.createWindow("Angle", "Time", "Value 3", "Angle");
			graph.createWindow("Angle Velocity", "Time", "Value 4", "Angle Velocity");
			graph.createWindow("Position", "Time", "Value 5", "Position");
			graph.createWindow("Position Velocity", "Time", "Value 6", "Position Velocity");
			graph.createWindow("Outer error", "Time", "Value 7", "Outer error");
			graph.createButtons();
			graph.build();
			graph.start(sc);
			System.out.println("Graph is operating...");
		} catch (Exception e) {
			System.out.println("Cannot create graph()");
		}
	}
}