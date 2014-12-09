import lejos.robotics.EncoderMotor;

public class Position {
	private long time, difference;
	private EncoderMotor motorA;
	private double oldValue, value, tempValue, filterValue, oldFilterValue,
			filterConstant;
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
		filterValue = 0;
		oldFilterValue = 0;
		filterConstant = 0.1;
	}

	public synchronized double getPosVelocity() {
		difference = System.currentTimeMillis() - time;
		time += difference;
		if (difference != 0) {
			tempValue = getPosition();
			value = ((tempValue - oldValue) / difference);
			oldValue = tempValue;
			filterValue = value - filterConstant * oldFilterValue;
			/*
			 * (-(difference-2)/(difference+2))*oldFilterValue+
			 * ((difference/(difference
			 * +2))*value)+((difference/(difference+2))*oldValue);
			 */
			oldFilterValue = filterValue;
		}
		return filterValue;
	}

	public synchronized double getPosition() {
		if (reset) {
			motorA.resetTachoCount();
			reset = false;
			return 0;
		} else {
			return motorA.getTachoCount() * meterPerDegree;
		}
	}

	public synchronized void reset() {
		motorA.resetTachoCount();
		reset = true;
		tempValue = 0;
		oldValue = 0;
		value = 0;
		filterValue = 0;
		oldFilterValue = 0;
	}
}