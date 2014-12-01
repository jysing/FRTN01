import lejos.robotics.EncoderMotor;

public class Position {
	private long time, difference;
	private EncoderMotor motorA;
	
	public Position(EncoderMotor motorA) {
		this.motorA = motorA;
		time = System.currentTimeMillis();
		
	}
		public double getVelocity() {
			float oldValue = getPosition();
			return (double) ((getPosition()-oldValue)/difference)/1000;
		}

		public float getPosition() {
			difference = System.currentTimeMillis() - time;
			time = time + difference;
			return motorA.getTachoCount();
		}

}