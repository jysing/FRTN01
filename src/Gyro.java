import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.HiTechnicGyro;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


public class Gyro {
	private Port port;
	private HiTechnicGyro sensor;
	public SampleProvider rate;
	public float sample[];
	
	
	public Gyro(){
		
		port = LocalEV3.get().getPort("S1");
		sensor = new HiTechnicGyro(port);
		sample = new float[sensor.sampleSize()];
		int offset  = calculateOffset();
		while (!Button.ESCAPE.isDown()) {
	         sensor.fetchSample(sample, offset);
	         LCD.drawString(String.format("%3.2f", sample[0]) + " m        ", 0, 3);
	      }
		sensor.close();
				
	}
	public float[] getAngleVelocity(){
			rate.fetchSample(sample, 0);
			return sample;
		
	}
	//Beräkna offset i gyrosensor
	public int calculateOffset() {
		int count = 10;
		float offset=0;
		for(int i = 0; i<count; i++){
			sensor.fetchSample(sample,0);
			offset = offset + sample[0];
			try {
				wait(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		offset = offset/count;
		offset = Math.round(offset);		
		return (int)offset;
	}
}


