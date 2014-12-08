import lejos.robotics.EncoderMotor;
import lejos.robotics.filter.LowPassFilter;

public class Position {
	private long time, difference;
	private EncoderMotor motorA;
	private double oldValue, value, tempValue;
	private double meterPerDegree;
	private LowPassFilter lowPass;

	public Position(EncoderMotor motorA) {
		this.motorA = motorA;
		time = System.currentTimeMillis();
		oldValue = 0;
		value = 0;
		tempValue = 0;
		meterPerDegree = 0.000697778;
		lowPass = new LowPassFilter(motorA, (float)0.1);
	}

	public double getPosVelocity() {
		difference = System.currentTimeMillis() - time;
		time += difference;
		tempValue = getPosition();
		if(difference != 0)	value = ((tempValue - oldValue) / difference);
		oldValue = tempValue;
		return value;
	}

	public double getPosition() {
		return motorA.getTachoCount()*meterPerDegree;
	}
	
	public void reset() {
		oldValue = 0;
		value = 0;
	}
}