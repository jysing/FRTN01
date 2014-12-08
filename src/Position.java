import lejos.hardware.lcd.LCD;
import lejos.robotics.EncoderMotor;

public class Position {
	private long time, difference;
	private EncoderMotor motorA;
	private double oldValue, value, tempValue, medelValue1, medelValue2;
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
		medelValue1 = 0;
		medelValue2 = 0;
	}

	public double getPosVelocity() {
		medelValue2 = medelValue1;
		medelValue1 = value;
		difference = System.currentTimeMillis() - time;
		time += difference;
		tempValue = getPosition();
		if(difference != 0)	value = ((tempValue - oldValue) / difference);
		oldValue = tempValue;
		return (value+medelValue1+medelValue2)/3;
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