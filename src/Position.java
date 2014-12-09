import lejos.robotics.EncoderMotor;

public class Position {
	private long time, difference;
	private EncoderMotor motorA;
	private double oldValue, value, tempValue, filterValue, oldFilterValue, preReset,
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
		filterValue = 0;
		oldFilterValue = 0;
		filterConstant = 0.1;
		preReset = 0;
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
			preReset = motorA.getTachoCount();
			reset = false;
		}
			return (motorA.getTachoCount()-preReset )* meterPerDegree;
	}

	public synchronized void reset() {
		//motorA.resetTachoCount();
		reset = true;
		tempValue = 0;
		oldValue = 0;
		value = 0;
		filterValue = 0;
		oldFilterValue = 0;
	}
}