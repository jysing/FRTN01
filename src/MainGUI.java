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
		System.out.println("1");
		graph.createWindow("Control signal", "Time", "Value 1", "Control signal");
		System.out.println("2");
		graph.createWindow("Measurement 2", "Time", "Value 2", "Measurement 2");
		System.out.println("3");
		graph.start(sc);
		System.out.println("4");
		System.out.println("Graph is operating...");
		} catch (Exception e){
			System.out.println("Cannot create graph()");
		}
	}
}