import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.HiTechnicGyro;
import lejos.robotics.SampleProvider;

public class Gyro extends Thread{
	private static final long period = 5;
	private Port port;
	private HiTechnicGyro sensor;
	public SampleProvider rate;
	public float sample[];
	public float offset=0;
	private double angle;
	
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
		LCD.drawString(String.format("%3.2f", sample[0]-offset) + " m        "+ sensor.sampleSize(), 0, 4);
		return sample[0]-offset;
	}
	
	public double getAngle() {
		LCD.drawString(String.format("%3.2f", angle) + " m        "+ sensor.sampleSize(), 0, 3);
		return angle;
	}
	
	private void calculateOffset() {
		int count = 100;
		for(int i = 0; i<count; i++){
			sensor.fetchSample(sample,0);
			offset = offset + sample[0];
			try {
				sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		offset = offset/count;
	}
	
	public void run() {
		while(true) {
			angle = (double)(angle + getAngleVelocity()*period/1000);
		
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
