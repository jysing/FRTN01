import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Main {
	public static void main(String [] args){
	//Communication m = new Communication();
	
	Gyro g = new Gyro();
	g.getAngleVelocity();
	
	}
}
