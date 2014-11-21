import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.HiTechnicGyro;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


public class Gyro extends Thread{
	private Port port;
	private HiTechnicGyro sensor;
	public SampleProvider rate;
	public float sample[];
	public float offset=0;
	private int precicion, current;
	private String[] sampleBuffer;
	
	
	//Gyro can deliver 300 measurements per sample
	public Gyro(int precicion){
		this.precicion = precicion;
		sampleBuffer = new String[precicion];
		
		for(int i = 0; i < precicion; i++) {
			sampleBuffer[i] = "0";
		}
		
		port = LocalEV3.get().getPort("S1");
		sensor = new HiTechnicGyro(port);
		sample = new float[sensor.sampleSize()];
		calculateOffset();		
	}
	public void getAngleVelocity(){
		while(true){
		long averageSample = 0;
		for(int i = 0; i < precicion; i++) {
			sensor.fetchSample(sample, 0);
			String s = String.format("%.2f", sample[0]);
			if(!sampleBuffer[current].equals(s)){
				current++;
				current = current%10;
				sampleBuffer[current] = s;
			}
		}
		for(String nbr: sampleBuffer) {
			averageSample += Long.parseLong(nbr);
		}
		averageSample = averageSample/10;
		LCD.drawString(String.format("%3.2f", averageSample-offset) + " m        "+ sensor.sampleSize(), 0, 3);
		}
		//return sample[0]-offset;
	}
	//Beräkna offset i gyrosensor
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
}


