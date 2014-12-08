import java.io.IOException;
import lejos.hardware.lcd.LCD;

public class Main {
	private static final int port = 6666, timeout = 30000;
	private static final int regulPrio = 2, graphPrio = 1;
	public static void main(String [] args){
		Gyro gyro = new Gyro(); 
		Regul regul = new Regul(gyro, regulPrio);
		Communication comm = null;
		try {
			comm = new Communication(regul, port, timeout, graphPrio);
			comm.connect();
			comm.sendPIDAngValues();
			comm.sendPIDPosValues();
			comm.start();
		} catch (IOException e) {
			LCD.drawString("massive connection", 0, 0);
			LCD.drawString(" error", 0, 1);
		}
		regul.start();		
	}
}
