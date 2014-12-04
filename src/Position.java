import lejos.robotics.EncoderMotor;

public class Position {
	private long time, difference;
	private EncoderMotor motorA;
	private double oldValue, value;

	public Position(EncoderMotor motorA) {
		this.motorA = motorA;
		time = System.currentTimeMillis();
		oldValue = 0;
		value = 0;
	}

	public double getPosVelocity() {
		difference = System.currentTimeMillis() - time;
		time = time + difference;
		value = ((getPosition() - oldValue) / difference);
		oldValue = getPosition();
		return value;
	}

	public double getPosition() {
		return motorA.getTachoCount();
	}
}