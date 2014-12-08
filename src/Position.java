import lejos.robotics.EncoderMotor;

public class Position {
	private long time, difference;
	private EncoderMotor motorA;
	private double oldValue, value, tempValue;
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
		tempValue = getPosition();
		if(difference != 0)	value = ((tempValue - oldValue) / difference);
		oldValue = tempValue;
		return value;
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