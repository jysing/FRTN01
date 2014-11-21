import lejos.hardware.sensor.*;
import lejos.hardware.port.*;
import lejos.hardware.Device.*;
import lejos.hardware.ev3.*;
import lejos.robotics.SampleProvider;
import java.util.concurrent.Semaphore;

public class Regul extends Thread {		
	private PID pid;
	private Semaphore mutex;

    
    /** Constructor. */
    public Regul (int priority) {
    	setPriority(priority);
    }
    
    /** Sets the parameters of the outer PID controller (outer controller). */
    public void setOuterParameters(PIDParameters p) {
    	controllerOuter.setParameters(p);
    }
    
    /** Returns the parameters of the outer PID controller (outer controller). */
    public PIDParameters getOuterParameters() {
    	return controllerOuter.getParameters();
    }
    
    public void run() {
    	long duration;
    	long t = System.currentTimeMillis();
    	long startTime = t;

    	try {
    		mutex.acquire();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    	
    	while (true) {
    		
    		
    		
       		t = t + controllerInner.getHMillis();
			duration = t - System.currentTimeMillis();
			if (duration > 0) {
				try {
					sleep(duration);
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}
    	}
    	mutex.release();
    }
}


