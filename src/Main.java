import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Main {
	
	public static void main(String [] args){
		//Communication m = new Communication();
		Gyro gyro = new Gyro(1);
		Regul regul = new Regul(gyro,2); //Next highest priority.
									//Reason is to get more accurate samples.
									//Might need to be changed to highest later on.
	}
}
