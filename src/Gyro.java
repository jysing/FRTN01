import lejos.hardware.ev3.LocalEV3;
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
	private boolean firstAng = true;
	
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
	}

	public double getAngleVelocity() {
		lowPass.fetchSample(sample, 0);
		//offset = (float) (EMAOFFSET*sample[0]+(1-EMAOFFSET)*offset);
		return sample[0]-offset; // 0.05
	}
	public double getAngle() {
		if(firstAng==true){ //kollar time har istallet forsta gangen for att difference skall bli liten
			time = System.currentTimeMillis(); 
			firstAng = false;
		}
		difference = System.currentTimeMillis() - time;
		time = time + difference;
		return (getAngleVelocity()*difference);
	}

	public void setOffset(double offset) {
		this.offset = offset;
		firstAng = true;
		angle = 0;
	}
}
