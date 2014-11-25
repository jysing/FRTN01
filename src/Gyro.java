import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.HiTechnicGyro;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Gyro extends Thread{
	private static final long period = 10;
	private Port port;
	private HiTechnicGyro sensor;
	public SampleProvider rate;
	public float sample[];
	public float offset=0;
	private float angle;
	
	//Gyro can deliver 300 measurements per second
	public Gyro(int priority){	
		setPriority(priority);
		
		port = LocalEV3.get().getPort("S1");
		sensor = new HiTechnicGyro(port);
		sample = new float[sensor.sampleSize()];
		calculateOffset();
	}
	
	public float getAngleVelocity(){
		sensor.fetchSample(sample, 0);
		LCD.drawString(String.format("%3.2f", sample[0]-offset) + " m        "+ sensor.sampleSize(), 0, 3);
		return sample[0]-offset;
	}
	
	public float getAngle() {
		return angle;
	}
	
	private void calculateOffset() {
		int count = 10;
		for(int i = 0; i<count; i++){
			sensor.fetchSample(sample,0);
			offset = offset + sample[0];
			try {
				sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		offset = offset/count;
	}
	
	public void run() {
		while(true) {
			angle = angle + getAngleVelocity()*period;
		
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}