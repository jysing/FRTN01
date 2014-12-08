import lejos.hardware.lcd.LCD;
import lejos.robotics.EncoderMotor;

public class Position {
	private long time, difference, h;
	private EncoderMotor motorA;
	private double oldValue, value, tempValue, filterValue, oldFilterValue;
	private double meterPerDegree;
	private boolean reset;

	public Position(EncoderMotor motorA) {
		this.motorA = motorA;
		time = System.currentTimeMillis();
		oldValue = 0;
		value = 0;
		tempValue = 0;
		meterPerDegree = 0.000697778;
		reset = false;
	}

	public double getPosVelocity() {
		difference = System.currentTimeMillis() - time;
		time += difference;
		if(difference != 0)	{
			tempValue = getPosition();
			value = ((tempValue - oldValue) / difference);
			oldValue = tempValue;
			h = difference;
			filterValue = (-(h-2)/(h*2))*oldFilterValue+((h/(h*2))*value)+((h/(h*2))*oldValue);
			oldFilterValue = filterValue;
		}
		return filterValue;
	}

	public double getPosition() {
		if(reset){
			motorA.resetTachoCount();
			reset = false;
		}
		return motorA.getTachoCount()*meterPerDegree;
	}
	
	public void reset() {
		reset = true;
		tempValue = 0;
		oldValue = 0;
		value = 0;
	}
}