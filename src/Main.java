import java.io.IOException;

public class Main {
	
	public static void main(String [] args){
		int timeout = 10000;
		Communication ms;
		try {
			ms = new Communication(6666, timeout);
			ms.theMain();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Graph graph = new Graph();
		Gyro gyro = new Gyro();
		//Communication m = new Communication();
		//gyro.start();
		Regul regul = new Regul(gyro,2); //Next highest priority.
									//Reason is to get more accurate samples.
									//Might need to be changed to highest later on.
		regul.start();
	}
}
