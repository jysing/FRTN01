import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.*;
import lejos.robotics.EncoderMotor;

public class Regul extends Thread {		
	private PID pid;
	private Gyro gyro;
	private Position posReader;
	EncoderMotor motorA;
	EncoderMotor motorB;
	
	private double u, e; // Control signal to/from PID
	private double angVel, ang; // angluarVelocity and current angle
	private static final double weightAng = 0.003, weightAngVel = 1;
	private static final double normalizedWeightAng = weightAng/(weightAng + weightAngVel);
	private static final double normalizedWeightAngVel = weightAngVel/(weightAng + weightAngVel);
	private double position, positionVel; // Position and position velocity

	public Regul (Gyro gyro, int priority) {
    	setPriority(priority);
    	this.gyro = gyro;
    	pid = new PID();
    	motorA = new NXTMotor(MotorPort.A);
    	motorA.flt();
    	motorB = new NXTMotor(MotorPort.D);
    	motorB.flt();
    	posReader = new Position(motorA);
    }
    
    public void setPIDParameters(PIDParameters p) {
    	pid.setParameters(p);
    }
    
    public PIDParameters getPIDParameters() {
    	return pid.getParameters();
    }
    
    public void setMotor(double speedLeft, double speedRight){
    	speedLeft = limitSpeed(speedLeft);
    	speedRight = limitSpeed(speedRight);
    	if (speedLeft < 0){
    		motorB.backward();
    	} else {
    		motorB.forward();
    	}
    	if(speedRight < 0) {
    		motorA.backward();
    	} else {
    		motorA.forward();
    	}
    	motorB.setPower(Math.abs((int)speedLeft));
    	motorA.setPower(Math.abs((int)speedRight));
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
		setMotor(30, 30);
		setMotor(0, 0);
    	calculateOffset();
    	
    	while (true) {
    		position = posReader.getPosition();
    		positionVel = posReader.getPosVelocity();
    		angVel = gyro.getAngleVelocity();
    		ang = gyro.getAngle();
    		e = normalizedWeightAngVel*angVel+normalizedWeightAng*ang;
    		u = pid.calculateOutput(e, 0);
    		pid.updateState(u);
    		setMotor(u, u);
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
		pid.reset();
		gyro.setOffset((offset/count)-0.130); //0.156 utan EMAOFFSET
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
