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
	double u; // Control signal from PID

    /** Constructor. */
    public Regul (int priority) {
    	setPriority(priority);
    	pid = new PID();
    	motorA = new EV3LargeRegulatedMotor(MotorPort.A);


    	motorB = new EV3LargeRegulatedMotor(MotorPort.B);
		//g = new Gyro();

		//g.getAngleVelocity();
		motorA.setSpeed(1300);
		motorA.forward();
		motorB.setSpeed(1300);
		motorB.forward();
		//motorB.rotate(360);
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
    	/*try {
    		mutex.acquire();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}*/
    	while (true) {
    		u = pid.calculateOutput((double)g.getAngleVelocity(), 0);
    		pid.updateState(u);
    				//L�gg in en sleep funktion
    	}
    	//mutex.release();
    }
}


