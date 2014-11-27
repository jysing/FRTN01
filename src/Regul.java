import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.*;
import lejos.robotics.EncoderMotor;

public class Regul extends Thread {		
	private PID pid;
	private Gyro gyro;
	//RegulatedMotor motorA;
	//RegulatedMotor motorB;
	EncoderMotor motorA;
	EncoderMotor motorB;
	
	private double u; // Control signal from PID
	private double angVel, ang; // angluarVelocity and current angle
	private static final double weightAng = 1, weightAngVel = 2;

    /** Constructor. */
    public Regul (Gyro gyro, int priority) {
    	setPriority(priority);
    	this.gyro = gyro;
    	pid = new PID();
    	motorA = new NXTMotor(MotorPort.A);
    	motorA.flt();
    	motorB = new NXTMotor(MotorPort.B);
    	motorB.flt();
    	//motorA = new EV3LargeRegulatedMotor(MotorPort.A);
    	//motorB = new EV3LargeRegulatedMotor(MotorPort.B);
    }
    
    /** Sets the parameters of the PID controller */
    public void setPIDParameters(PIDParameters p) {
    	pid.setParameters(p);
    }
    
    /** Returns the parameters of the PID controller */
    public PIDParameters getPIDParameters() {
    	return pid.getParameters();
    }
    
    public void setMotor(double speed){
    	speed = limitSpeed(speed);
    	if (speed < 0){
    		motorA.backward();
    		motorB.backward();
    	} else {
    		motorA.forward();
    		motorB.forward();
    	}
    	motorA.setPower(Math.abs((int)speed));
    	motorB.setPower(Math.abs((int)speed));
    }
    
    private double limitSpeed(double speed) {
    	if(speed > 100) {
    		speed = 100;
    	} else if (speed < -100) {
    		speed = -100;
    	}
    	return speed;
	}

	public void run() {
    	calculateOffset();
    	while (true) {
    		angVel = (double)gyro.getAngleVelocity();
    		ang = (double)gyro.getAngle();
    		u = pid.calculateOutput(weightAngVel*angVel+weightAng*ang, 0);
    		pid.updateState(u);
    		setMotor(u);
    	}
    }
    
    public void calculateOffset() {
    	float offset = 0;
    	float sample = 0;
		int count = 1000;
		for(int i = 0; i<count; i++){
			sample = gyro.getAngleVelocity();
			offset = offset + sample;
			try {
				sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		gyro.setOffset(offset/count);
	}
    
    public double getU() {
    	return u;
    }
}


