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
			comm.start();
		} catch (IOException e) {
			LCD.drawString("massive connection error", 0, 0);
		}
<<<<<<< HEAD
		
		Gyro gyro = new Gyro();
		Regul regul = new Regul(gyro,comm, 2);
		regul.start();
		
=======
		regul.start();		
>>>>>>> eeb3968c94580bc0557f4eef6dad851f26546e91
	}

}
