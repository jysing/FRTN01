import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.HiTechnicGyro;
import lejos.robotics.filter.IntegrationFilter;

public class Gyro {
	private Port port;
	private HiTechnicGyro sensor;
	//public SampleProvider rate; //cant create an object from an interface...
	public IntegrationFilter integration; //changed to IntegrationFilter that implements the interface sampleProvider
	public float sample[];
	public float sampleAng[];
	public float offset = 0;
	
	private double angle;
	public double EMAOFFSET = 0.0005;
	public SimpsonIteration Integrator; //anv�nd denna vid getAngle

	private long time, difference;

	// Gyro can deliver 300 measurements per second
	public Gyro() {
		port = LocalEV3.get().getPort("S1");
		sensor = new HiTechnicGyro(port);
		sample = new float[sensor.sampleSize()];
		sampleAng = new float[sensor.sampleSize()]; //Test med Angle
		time = System.currentTimeMillis();
		

		angle=0;

		//Integrator = new SimpsonIteration();
	}

	public float getAngleVelocity() {
		sensor.fetchSample(sample, 0);
		offset = (float) (EMAOFFSET*sample[0]+(1-EMAOFFSET)*offset);
		//integration = new IntegrationFilter(rate);
		//integration.fetchSample(sampleAng, 0);
		
		//LCD.drawString(String.format("%3.2f", sample[0] - offset)
		//		+ " m        " + sensor.sampleSize(), 0, 4);
		return (float) (sample[0] - offset); //-0.05
	}

	public double getAngle() {
		difference = System.currentTimeMillis() - time;
		time = time + difference;
		//LCD.drawString(
		//		String.format("%3.2f", angle) + " m        "
		//				+ sensor.sampleSize(), 0, 3);
		return angle += (double) getAngleVelocity() * difference / 1000;
	}

	public void setOffset(float offset) {
		this.offset = offset;
	}
}
