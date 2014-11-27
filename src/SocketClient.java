import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketClient extends Thread{

    private final String server;
    private final int port;
    private static final long period = 100;
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;

    public SocketClient(String server, int port) {
        this.server = server;
        this.port = port;
    }
 
    public void run() {
    	while(true) {
    		String message = receive();
    		System.out.println(message);
    		
    		try {
				sleep(period);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public void connect() throws IOException {
        System.out.println("Connecting to " + server + " on port " + port);
        client = new Socket(server, port);
        System.out.println("Just connected to " + client.getRemoteSocketAddress());
        in = new DataInputStream(client.getInputStream());
        out = new DataOutputStream(client.getOutputStream());
    }

    public void send(String message) throws IOException {
    	out.writeUTF(message);
    }
    
    public String receive() {
    	String message;
    	try {
			message = in.readUTF();
		} catch (IOException e) {
			message = "Fel";
		}
    	System.out.println(message);
    	return message;    	
    }

    public boolean isConnected() {
        if(client==null)
            return false;
        return client.isConnected();
    }

    public void close() throws IOException {
        client.close();
    }
/*
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
    }
    */
}