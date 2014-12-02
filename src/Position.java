import lejos.robotics.EncoderMotor;

public class Position {
	private long time, difference;
	private EncoderMotor motorA;
	
	public Position(EncoderMotor motorA) {
		this.motorA = motorA;
		time = System.currentTimeMillis();	
	}
	
	public float getPosVelocity() {
		difference = System.currentTimeMillis() - time;
		time = time + difference;
		float oldValue = getPosition();
		return ((getPosition()-oldValue)/difference)/1000;
	}
		
	public float getPosition() {
		return motorA.getTachoCount();
	}
}