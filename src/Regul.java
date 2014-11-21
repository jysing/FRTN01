import lejos.hardware.sensor.*;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.*;
import lejos.hardware.Device.*;
import lejos.hardware.ev3.*;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;

import java.util.concurrent.Semaphore;

public class Regul extends Thread {		
	
	public final static int precicion = 10;
	private PID pid;
	private Semaphore mutex;
	private Gyro g;
	RegulatedMotor motorA;
	RegulatedMotor motorB;

    /** Constructor. */
    public Regul (int priority) {
    	setPriority(priority);
    	pid = new PID();
    	motorA = new EV3LargeRegulatedMotor(MotorPort.A);
    	motorB = new EV3LargeRegulatedMotor(MotorPort.B);
		g = new Gyro(precicion);
		g.getAngleVelocity();
		motorA.setSpeed(3);
		motorA.forward();
		motorB.rotate(360);
    }
    
    /** Sets the parameters of the PID controller */
    public void setPIDParameters(PIDParameters p) {
    	pid.setParameters(p);
    }
    
    /** Returns the parameters of the PID controller */
    public PIDParameters getPIDParameters() {
    	return pid.getParameters();
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
   		
       		t = t + pid.getHMillis();
			duration = t - System.currentTimeMillis();
			if (duration > 0) {
				try {
					sleep(duration);
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}
			break;
    	}
    	mutex.release();
    }
}


