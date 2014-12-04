import lejos.robotics.EncoderMotor;

public class Position {
	private long time, difference;
	private EncoderMotor motorA;

	public Position(EncoderMotor motorA) {
		this.motorA = motorA;
		time = System.currentTimeMillis();
	}

	public double getPosVelocity() {
		double oldValue = getPosition();
		difference = System.currentTimeMillis() - time;
		time = time + difference;
		return ((getPosition() - oldValue) / difference);
	}

	public double getPosition() {
		return motorA.getTachoCount();
	}
}