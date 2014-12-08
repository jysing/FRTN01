import lejos.robotics.EncoderMotor;
import lejos.robotics.filter.LowPassFilter;

public class Position {
	private long time, difference;
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
		filterValue = 1;
		oldFilterValue = 0;
	}

	public double getPosVelocity() {
		difference = System.currentTimeMillis() - time;
		time += difference;
		if(difference != 0)	{
			tempValue = getPosition();
			value = ((tempValue - oldValue) / difference);
			oldValue = tempValue;
			filterValue = (-(difference-2)/(difference+2))*oldFilterValue+
					((difference/(difference+2))*value)+((difference/(difference+2))*oldValue);
			oldFilterValue = filterValue;
		}
		return 10;
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