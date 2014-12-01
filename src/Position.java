import lejos.robotics.EncoderMotor;

public class Position {
	
	private EncoderMotor motorA;
	
	public Position(EncoderMotor motorA) {
		this.motorA = motorA;
		
	}
		public float getVelocity() {
			//skriv klass som deriverar position
			return (float) 0;
		}

		public double getPosition() {
			return motorA.getTachoCount();
		}

}