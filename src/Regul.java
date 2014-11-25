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
	private Gyro gyro;
	RegulatedMotor motorA;
	RegulatedMotor motorB;
	double u; // Control signal from PID
	double angVel; // angluarVelocity

    /** Constructor. */
    public Regul (Gyro gyro, int priority) {
    	setPriority(priority);
    	this.gyro = gyro;
    	pid = new PID();
    	motorA = new EV3LargeRegulatedMotor(MotorPort.A);


    	motorB = new EV3LargeRegulatedMotor(MotorPort.B);

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
    		angVel = (double)gyro.getAngleVelocity();
    		u = pid.calculateOutput(angVel, 0);
    		pid.updateState(u);

    		setMotor(u);
    		//L�gg in en sleep funktion
    	}
    	//mutex.release();
    }
    
    public void setMotor(double speed){
    	if (speed < 0){
    		motorA.backward();
    		motorB.backward();
    	} else {
    		motorA.forward();
			motorB.forward();
    	}
		motorA.setSpeed((int)speed);
    	motorB.setSpeed((int)speed);
    }


}


