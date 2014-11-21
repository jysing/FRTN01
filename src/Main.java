import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Main {
	public final static int precicion = 10;
	
	public static void main(String [] args){
	//Communication m = new Communication();
		Regul regul = new Regul(6); //Priority 6
		Gyro g = new Gyro(precicion);
		g.getAngleVelocity();
	}
}
