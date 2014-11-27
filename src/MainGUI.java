import java.io.IOException;

public class MainGUI {

	public static void main(String[] args) {
		String serverName = "10.0.1.1";
	    int port = 6666;  
	    SocketClient sc = new SocketClient(serverName, port);
	    try {
	    	sc.connect();
	    } catch (IOException e) {
	        System.out.println("Failed to connect");
	    }
	        sc.start();		
		
	    try {
		Graph graph = new Graph(sc);
		System.out.println("Graph is operating...");
		} catch (Exception e){
			System.out.println("Cannot create graph()");
		}
	}
}