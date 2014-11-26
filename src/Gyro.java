import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.HiTechnicGyro;
import lejos.robotics.SampleProvider;

public class Gyro {// extends Thread{
	private static final long period = 5;
	private Port port;
	private HiTechnicGyro sensor;
	public SampleProvider rate;
	public float sample[];
	public float offset=0;
	private double angle;
	
	private long time, difference;
	
	//Gyro can deliver 300 measurements per second
	public Gyro(){	
		//setPriority(priority);
		
		port = LocalEV3.get().getPort("S1");
		sensor = new HiTechnicGyro(port);
		sample = new float[sensor.sampleSize()];
		//calculateOffset();
		time = System.currentTimeMillis();
	}
	
	public float getAngleVelocity(){
		sensor.fetchSample(sample, 0);
		LCD.drawString(String.format("%3.2f", sample[0]-offset) + " m        "+ sensor.sampleSize(), 0, 4);
		return sample[0]-offset;
	}
	
	public double getAngle() {
		difference = System.currentTimeMillis() - time;
		time = time + difference;
		LCD.drawString(String.format("%3.2f", angle) + " m        "+ sensor.sampleSize(), 0, 3);
		return angle += (double)getAngleVelocity()*difference/1000;
		
		//return angle;
	}
	
	/*
	public void calculateOffset() {
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
	*/

	public void setOffset(float offset) {
		this.offset = offset;
	}
	
	/*
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
	*/
}
