package Lego;
import lejos.robotics.EncoderMotor;

public class Position {
	private long time, difference;
	private EncoderMotor motorA;
	private EncoderMotor motorB;
	private double oldValue, value, tempValue, filterValue, oldFilterValue, preReset,
			filterConstant;
	private double meterPerDegree;
	private boolean reset;

	public Position(EncoderMotor motorA, EncoderMotor motorB) {
		this.motorB = motorB;
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
			oldFilterValue = filterValue;
		}
		return filterValue;
	}

	public synchronized double getPosition() {
		double temp = (motorA.getTachoCount() + motorB.getTachoCount())/2;
		if (reset) {
			preReset = temp;
			reset = false;
		}
			return (temp-preReset )* meterPerDegree;
	}

	public synchronized void reset() {
		reset = true;
		tempValue = 0;
		oldValue = 0;
		value = 0;
		filterValue = 0;
		oldFilterValue = 0;
	}
}