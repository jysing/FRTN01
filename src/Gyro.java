import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.HiTechnicGyro;
import lejos.robotics.filter.IntegrationFilter;
import lejos.robotics.filter.LowPassFilter;

public class Gyro {
	private Port port;
	private HiTechnicGyro sensor;
	public IntegrationFilter integration; //changed to IntegrationFilter that implements the interface sampleProvider
	public LowPassFilter lowPass; //low pass filter for att filtrera gyro signal
	public float sample[];
	public float sampleAng[];
	public float sampleLowPass[];
	public double offset = 0;
	
	private double angle;
	public double EMAOFFSET = 0.0005;
	public SimpsonIteration Integrator; //anv�nd denna vid getAngle

	private long time, difference;

	// Gyro can deliver 300 measurements per second
	public Gyro() {
		port = LocalEV3.get().getPort("S2");
		sensor = new HiTechnicGyro(port);
		sample = new float[sensor.sampleSize()];
		//sampleAng = new float[sensor.sampleSize()]; //Test med Angle
		//sampleLowPass = new float[sensor.sampleSize()];
		time = System.currentTimeMillis();
		//Integrator = new SimpsonIteration();
		//Clone my ass
	}

	public double getAngleVelocity() {
		sensor.fetchSample(sample, 0);
		//lowPass = new LowPassFilter(sensor, (float)0.01); //Lagpass med tidskonstant 0.01 osaker pa enhet
		//offset = (float) (EMAOFFSET*sample[0]+(1-EMAOFFSET)*offset);
		//LCD.drawString(String.format("%3.2f", sample[0] - offset)
		//		+ " m        " + sensor.sampleSize(), 0, 4);
		return sample[0] - offset - 0.05; //-0.05
	}
	public double getAngle() {

		difference = System.currentTimeMillis() - time;
		time = time + difference;
		long temp = time;
		LCD.drawString("sec = " + difference, 0, 5);
		double temp2 = getAngleVelocity();
		LCD.drawString("angVel = " + temp2, 0, 6);
		//integration = new IntegrationFilter(sensor);
		//integration.fetchSample(sampleAng, 0);
		//LCD.drawString(
		//		String.format("%3.2f", angle) + " m        "
		//				+ sensor.sampleSize(), 0, 3);
		//return sampleAng[0];
		return angle += (temp2 * difference);
	}

	public void setOffset(double offset) {
		this.offset = offset;
		angle = 0;
	}
}
