import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.*;
import lejos.robotics.EncoderMotor;

public class Regul extends Thread {		
	private PID pid;
	private Gyro gyro;
	private Position posReader;
	//RegulatedMotor motorA;
	//RegulatedMotor motorB;
	EncoderMotor motorA;
	EncoderMotor motorB;
	
	private double u, e; // Control signal to/from PID
	private double angVel, ang; // angluarVelocity and current angle
	private static final double weightAng = 0.003, weightAngVel = 1;
	private static final double normalizedWeightAng = weightAng/(weightAng + weightAngVel);
	private static final double normalizedWeightAngVel = weightAngVel/(weightAng + weightAngVel);
	private float position, positionVel; // Position and position velocity


    /** Constructor. */
    public Regul (Gyro gyro, int priority) {
    	setPriority(priority);
    	this.gyro = gyro;
    	pid = new PID();
    	motorA = new NXTMotor(MotorPort.A);
    	motorA.flt();
    	motorB = new NXTMotor(MotorPort.D);
    	motorB.flt();
    	posReader = new Position(motorA);
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
    /*In i run:
    	e=?weight1?*Position.getPostition()+?weight2?*Position.getVelocity();
    	 }*/

	public void run() {
		setMotor(0);
    	calculateOffset();
    	while (true) {
    		position = posReader.getPosition();
    		LCD.drawString("position: " + position, 0, 3);
    		positionVel = posReader.getPosVelocity();
    		LCD.drawString("positionVel: " + positionVel, 0, 4);
    		angVel = gyro.getAngleVelocity();
    		ang = gyro.getAngle();
    		e = normalizedWeightAngVel*angVel+normalizedWeightAng*ang;
    		u = pid.calculateOutput(e, 0);
    		pid.updateState(u);
    		setMotor(u);
    	}
    }
    
    public void calculateOffset() {
    	double offset = 0;
    	double sample = 0;
		int count = 100;
		for(int i = 0; i<count; i++){
			sample = gyro.getAngleVelocity();
			offset = offset + sample;
			try {
				sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		gyro.setOffset((offset/count)-0.156);
	}
    
    
    //Get methods to be used by Communication to 
    //send information needed to build graphs
    public double getU() {
    	return u;
    }

	public double getE() {
		return e;
	}
	
	public double getA() {
		return ang/1000;
	}
	
	public double getV() {
		return angVel;
	}
	
	public double getP(){
		return position;
	}
	public double getB(){
		return positionVel;
	}
}
