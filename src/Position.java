import lejos.robotics.EncoderMotor;

public class Position {
	
	private EncoderMotor motor;
	
	public Position(EncoderMotor motorA) {
		this.motor = motorA;
		
	}
		public float getVelocity() {
			//skriv klass som deriverar position
			return (float) 0;
		}

		public double getPosition() {
			return motor.getTachoCount();
		}

}