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
	
	private long time, difference;

	// Gyro can deliver 300 measurements per second
	public Gyro() {
		port = LocalEV3.get().getPort("S2");
		sensor = new HiTechnicGyro(port);
		lowPass = new LowPassFilter(sensor, (float)0.1);
		sample = new float[sensor.sampleSize()];
		time = System.currentTimeMillis();
	}

	public double getAngleVelocity() {
		//sensor.fetchSample(sample, 0);
		lowPass.fetchSample(sample, 0);
		//offset = (float) (EMAOFFSET*sample[0]+(1-EMAOFFSET)*offset);
		return sample[0]-offset; // 0.05
	}
	public double getAngle() {
		difference = System.currentTimeMillis() - time;
		time = time + difference;
		
		//not needed
		LCD.drawString("Difference: " + difference, 0, 5);
		double angleVel = getAngleVelocity();
		LCD.drawString("angVel: " + angleVel, 0, 6);
		
		double temp = (angleVel * difference);

		//if(Math.abs(temp/1000) > 0.02){
			angle += temp;
		//}

		return angle;
	}

	public void setOffset(double offset) {
		this.offset = offset;
		angle = 0;
	}
}
