import java.io.IOException;
import lejos.hardware.lcd.LCD;

public class Main {

	public static void main(String [] args){
		int port = 6666;
		int timeout = 30000;
		Communication comm = null;
		try {
			comm = new Communication(port, timeout);
			comm.connect();
		} catch (IOException e) {
			LCD.drawString("massive connection error", 0, 2);
			e.printStackTrace();
		}
		
		try {
		Graph graph = new Graph(comm);
		} catch (Exception e){
			System.out.println("Cannot create graph()");
		}
		Gyro gyro = new Gyro();
		Regul regul = new Regul(gyro,comm,2);
		regul.start();
		
	}

}
