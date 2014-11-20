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
		/*// get a port instance
		port = LocalEV3.get().getPort("S1");

		// Get an instance of the Ultrasonic EV3 sensor
		sensor = new HiTechnicGyro(port);

		// get an instance of this sensor in measurement mode
		rate= sensor.getMode("");
		
		// initialize an array of floats for fetching samples. 
					// Ask the SampleProvider how long the array should be
		sample = new float[rate.sampleSize()];


					// fetch a sample
		while(true){
				rate.fetchSample(sample, 0);
					}
					*/
		
		port = LocalEV3.get().getPort("S1");
		sensor = new HiTechnicGyro(port);
		
		sample = new float[sensor.sampleSize()];
		calculateOffset();
		while (!Button.ESCAPE.isDown()) {
	         sensor.fetchSample(sample, 0);
	         LCD.drawString(String.format("%3.2f", sample[0]) + " m        ", 0, 3);
	      }
		sensor.close();
		
		
		
	}
	public float[] getAngleVelocity(){
			rate.fetchSample(sample, 0);
			return sample;
		
	}
	//Beräkna offset i gyrosensor
	public float calculateOffset(){
		int count = 10;
		float offset=0;
		for(int i = 0; i<count; i++){
			sensor.fetchSample(sample,0);
			offset = offset + sample[0];
			wait(10);
		}
		offset = offset/count;
		return offset;
	}
}


