import lejos.robotics.EncoderMotor;

public class Position {
	private long time, difference;
	private EncoderMotor motorA;
	private double oldValue, value;
	private double meterPerDegree;

	public Position(EncoderMotor motorA) {
		this.motorA = motorA;
		time = System.currentTimeMillis();
		oldValue = 0;
		value = 0;
		meterPerDegree = 0.000697778;
	}

	public double getPosVelocity() {
		difference = System.currentTimeMillis() - time;
		time += difference;
		oldValue = getPosition();
		if(difference != 0)	value = ((getPosition() - oldValue) / difference);
		oldValue = value;
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