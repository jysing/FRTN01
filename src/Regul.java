import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.*;
import lejos.robotics.EncoderMotor;

public class Regul extends Thread {		
	private PID pid;
	private Gyro gyro;
	private Position posReader;
	EncoderMotor motorA;
	EncoderMotor motorB;
	
	private boolean manual;
	private double manualSpeedLeft, manualSpeedRight;
	private static final long period = 100;
	private double u, e; // Control signal to/from PID
	private double angVel, ang; // angluarVelocity and current angle
	private static final double weightAng = 1, weightAngVel = 0.1, weightPos = 4, weightPosVel = 0;
	private static final double normalizedWeightAng = weightAng/(weightAng + weightAngVel + weightPos + weightPosVel);
	private static final double normalizedWeightAngVel = weightAngVel/(weightAng + weightAngVel + weightPos + weightPosVel);
	private static final double normalizedWeightPos = weightPos/(weightAng + weightAngVel + weightPos + weightPosVel);
	private static final double normalizedWeightPosVel = weightPosVel/(weightAng + weightAngVel + weightPos + weightPosVel);
	private double position, positionVel; // Position and position velocity

	public Regul (Gyro gyro, int priority) {
    	setPriority(priority);
    	this.gyro = gyro;
    	manualSpeedLeft = 0;
    	manualSpeedRight = 0;
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
    
    public synchronized void manualControl(double speedLeft, double speedRight) {
    	manualSpeedLeft = speedLeft;
    	manualSpeedRight = speedRight;
    	//manual = true;
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
    	int i = 0;
    	manual = false;
    	while (true) {
    		if(manual) {
    			if (i == 0) setMotor(manualSpeedLeft, manualSpeedRight);
    			i++;
    			if(i >= 5) {
    				manual = false;
    				i = 0;
    			}
    			try {
    				Thread.sleep(period);
    			} catch (InterruptedException e1) {
    				e1.printStackTrace();
    			}
    		} else {
    			position = posReader.getPosition();
    			positionVel = (posReader.getPosVelocity()*1000);
    			angVel = gyro.getAngleVelocity();
    			ang = (gyro.getAngle()/1000);
    			e = normalizedWeightAngVel*angVel+normalizedWeightAng*ang+position*normalizedWeightPos+positionVel*normalizedWeightPosVel;
    			u = pid.calculateOutput(e, 0);
    			pid.updateState(u);
    			setMotor(u, u);    			
    		}    		
    		if(manual) {
    			LCD.drawString("true", 0, 4);
    		} else {
    			LCD.drawString("false", 0, 4);
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
		pid.reset();
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

	public String sendPIDValues() {
		PIDParameters p = pid.getParameters();
		StringBuilder sb = new StringBuilder();
		sb.append("X");
		sb.append(p.Beta + ",");
		sb.append(p.K +",");
		sb.append(p.Ti + ",");
		sb.append(p.Tr + ",");
		sb.append(p.Td + ",");
		sb.append(p.N + ",");
		return sb.toString();
	}

	public void setManualFalse() {
		manual = false;
	}
}
