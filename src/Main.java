import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Main {
	
	public static void main(String [] args){
		Communication m = new Communication();
		Regul regul = new Regul(2); //Next highest priority
		Gyro g = new Gyro(regul, 1);
	}
}
