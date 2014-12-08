import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.*;
import lejos.robotics.EncoderMotor;

public class Regul extends Thread {		
	//private PID pid;
	private PID pidAng, pidPos;
	private Gyro gyro;
	private Position posReader;
	EncoderMotor motorA;
	EncoderMotor motorB;
	
	private boolean manual;
	private double manualSpeedLeft, manualSpeedRight;
	private static final long period = 5;
	private double u, e, ref; // Control signal to/from PID
	private double angVel, ang; // angluarVelocity and current angle
	private double position, positionVel; // Position and position velocity
	private static final double weightAng = 1, weightAngVel = 0.1;
	private static final double weightPos = 4, weightPosVel = 0;
	private static final double normalizedWeightAng = weightAng/(weightAng + weightAngVel);
	private static final double normalizedWeightAngVel = weightAngVel/(weightAng + weightAngVel);
	private static final double normalizedWeightPos = weightPos/(weightPos + weightPosVel);
	private static final double normalizedWeightPosVel = weightPosVel/(weightPos + weightPosVel);

	public Regul (Gyro gyro, int priority) {
    	setPriority(priority);
    	this.gyro = gyro;
    	manualSpeedLeft = 1;
    	manualSpeedRight = 1;
    	pidAng = new PID("Ang");
    	pidPos = new PID("Pos");
    	motorA = new NXTMotor(MotorPort.A);
    	motorA.flt();
    	motorB = new NXTMotor(MotorPort.D);
    	motorB.flt();
    	posReader = new Position(motorA);
    }
	
	public void setPIDAngParameters(PIDParameters p) {
    	pidAng.setParameters(p);
    }
    
    public PIDParameters getPIDAngParameters() {
    	return pidAng.getParameters();
    }
    
    public void setPIDPosParameters(PIDParameters p) {
    	pidPos.setParameters(p);
    }
    
    public PIDParameters getPIDPosParameters() {
    	return pidPos.getParameters();
    }
    
    public synchronized void manualControl(double speedLeft, double speedRight, double angRef) {
    	manualSpeedLeft = speedLeft;
    	manualSpeedRight = speedRight;
    	ref = angRef;
    	manual = true;
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
    	motorB.setPower((int) Math.abs(speedLeft));
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
    	manual = false;
    	while (true) {
    		if(!manual) {
    			synchronized (pidPos) {
    				position = posReader.getPosition();
    				positionVel = (posReader.getPosVelocity()*1000);
    				e = position*normalizedWeightPos+positionVel*normalizedWeightPosVel;
    				ref = -pidPos.calculateOutput(e, 0);
    			}    			
    		}
    		
    		synchronized (pidAng) {
    			angVel = gyro.getAngleVelocity();
    			ang = (gyro.getAngle()/1000);
    			e = normalizedWeightAngVel*angVel+normalizedWeightAng*ang;
    			u = pidAng.calculateOutput(e, ref);
    			setMotor(u/manualSpeedLeft, u/manualSpeedRight);
			}
    		
    		try {
				Thread.sleep(period);
			} catch (InterruptedException e1) {
				LCD.drawString("Could not sleep regul", 0, 1);
			}
    	}
    }
    
    public synchronized void calculateOffset() {
    	setMotor(0, 0);
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
		setMotor(0, 0);
		pidAng.reset();
		pidPos.reset();
		gyro.setOffset((offset/count)-0.130); //0.156 utan EMAOFFSET
		angVel = 0;
		ang = 0;
		posReader.reset();
		position = 0;
		positionVel = 0;
	}
    
    public double getU() {
    	return u;
    }

	public double getE() {
		return e;
	}
	
	public double getA() {
		return ang;
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

	public String sendPIDAngValues() {
		PIDParameters p = pidAng.getParameters();
		return valuesToString(p, 'X');
	}

	public String sendPIDPosValues() {
		PIDParameters p = pidPos.getParameters();
		return valuesToString(p, 'Y');
	}
	
	private String valuesToString(PIDParameters p, char identifier) {
		StringBuilder sb = new StringBuilder();
		sb.append(identifier);
		sb.append(p.Beta + ",");
		sb.append(p.K +",");
		sb.append(p.Ti + ",");
		sb.append(p.Tr + ",");
		sb.append(p.Td + ",");
		sb.append(p.N + ",");
		return sb.toString();
	}

	public synchronized void setManualFalse() {
		manualSpeedLeft = 1;
    	manualSpeedRight = 1;
		manual = false;
	}
}
