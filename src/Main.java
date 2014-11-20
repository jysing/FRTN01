import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;




public class Main {
	public static void main(String [] args){
	
		/*LCD.drawString("Waiting",2,0);
		LCD.refresh();
		Delay.msDelay(3000);
		LCD.clear();
		LCD.drawString("Dead",2,0);
		LCD.refresh(); */
	Communication m = new Communication();
	
	Gyro g = new Gyro();
	g.getAngleVelocity();
	
}
}
