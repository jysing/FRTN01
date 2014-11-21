import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Main {
	public final static int precicion = 10;
	
	public static void main(String [] args){
	//Communication m = new Communication();
	
	Gyro g = new Gyro(precicion);
	g.getAngleVelocity();
	
	}
}
