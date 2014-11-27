import java.io.IOException;
import lejos.hardware.lcd.LCD;

public class Main {

	public static void main(String [] args){
		int port = 6666;
		int timeout = 30000;
		Communication comm;
		try {
			comm = new Communication(port, timeout);
			comm.connect();
			if (comm.isConnected()) {
				comm.send("oj oj oj vad detta fungerade bra");
			} 
		} catch (IOException e) {
			LCD.drawString("massive connection error", 0, 2);
			e.printStackTrace();
		}
		/*
		Graph graph = new Graph();
		Gyro gyro = new Gyro();
		Regul regul = new Regul(gyro,2);
		regul.start();
		*/
	}

}
