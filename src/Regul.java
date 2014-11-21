import lejos.hardware.sensor.*;
import lejos.hardware.port.*;
import lejos.hardware.Device.*;
import lejos.hardware.ev3.*;
import lejos.robotics.SampleProvider;
import java.util.concurrent.Semaphore;

public class Regul extends Thread {		
	private PID pid;
	private Semaphore mutex;
	
	/** Inner monitor class for controller mode **/
	class ModeMonitor {
		// Synchronized access methods
		public synchronized void setMode(int newMode) {
			mode = newMode;
			controllerInner.reset();
			controllerOuter.reset();
		}

		public synchronized int getMode() {
			return mode;
		}
	}
    
    /** Constructor. */
    public Regul (int priority) {
    	setPriority(priority);
    	try {
    		analogInAngle = new AnalogIn(0);
    		analogInPosition = new AnalogIn(1);
    		analogOut = new AnalogOut(0);
    	} catch (IOChannelException e) {
    		System.out.println("ERROR: IOChannelException");
    	}
    	controllerInner = new PI("PI");
    	controllerOuter = new PID("PID");
    	modeMon = new ModeMonitor();
    	mutex = new Semaphore(1);
    	doRun = true;
    }
    
    /** Sets up a reference to Opcom. Called by Main. */
    public void setOpCom(OpCom o) {
    	opCom = o;
    }
    
    /** Sets up a reference to RefGen. Called by Main. */
    public void setRefGen(ReferenceGenerator r) {
    	refGen = r;
    }
    
    /** Sets the parameters of the inner PI controller (inner controller). */
    public void setInnerParameters(PIParameters p) {
    	controllerInner.setParameters(p);
    }
    
    /** Returns the parameters of the inner PI controller (inner controller). */
    public PIParameters getInnerParameters() {
    	return controllerInner.getParameters();
    }
    
    /** Sets the parameters of the outer PID controller (outer controller). */
    public void setOuterParameters(PIDParameters p) {
    	controllerOuter.setParameters(p);
    }
    
    /** Returns the parameters of the outer PID controller (outer controller). */
    public PIDParameters getOuterParameters() {
    	return controllerOuter.getParameters();
    }
    
    /** Set the controller in OFF mode. */
    public void setOFFMode() {
    	modeMon.setMode(OFF);
    }
    
    /** Set the controller in BEAM mode. */
    public void setBEAMMode() {
    	modeMon.setMode(BEAM);
    }
    
    /** Set the controller in BALL mode. */
    public void setBALLMode() {
    	modeMon.setMode(BALL);
    }
    
    /** Returns the initial control mode. */
    public int getMode() {
    	return modeMon.getMode();
    }
    
    /** Called from Opcom as the Stop button is pressed. */
    public synchronized void shutDown() {
    	doRun = false;
    	try {
    		mutex.acquire();
    	}
    	catch (InterruptedException e) {
    		System.out.println(e);
    		e.printStackTrace();
    	}
    }
    
    public void run() {
    	long duration;
    	long t = System.currentTimeMillis();
    	startTime = t;

    	try {
    		mutex.acquire();
    	} catch (InterruptedException e) {
    		System.out.println(e);
    		e.printStackTrace();
    	}
    	
    	
    	double yp = 0;
    	double ya = 0;
		double ua; // OUTER
		double uv; // INNER
		double ref;
    	while (doRun) {
    		try {
    			yp = analogInPosition.get();
    			ya = analogInAngle.get();
    		} catch (IOChannelException e) {
    			System.out.println("ERROR: IOChannelException");
    		}
    		ref = refGen.getRef();
    		
    		switch (modeMon.getMode()) {
    		
    		case BALL:
    			synchronized (controllerOuter) {
    				ua = controllerOuter.calculateOutput(yp, ref);
    				ua = saturate(ua, U_MIN, U_MAX);
    				controllerOuter.updateState(ua);
    				
    				synchronized (controllerInner) {
    					uv = controllerInner.calculateOutput(ya, ua);
    					uv = saturate(uv, U_MIN, U_MAX);
    					try {
    						analogOut.set(uv);
    					} catch (IOChannelException e) {
    						
    					}
    					sendDataToOpCom(ref, yp, uv);
    					controllerInner.updateState(uv);
					}
				}
    			break;
    			
    		case BEAM:
    			uv = controllerInner.calculateOutput(ya, ref);
    			try {
					analogOut.set(uv);
				} catch (IOChannelException e) {
					
				}
    			sendDataToOpCom(ref, ya, uv);
    			controllerInner.updateState(uv);
    			break;
    			
    		case OFF:
    			
    			try {
					analogOut.set(0);
				} catch (IOChannelException e) {
					
				}
    			sendDataToOpCom(0, 0, 0);
    			controllerInner.reset();
    			controllerOuter.reset();
    			break;
    			
    		default:
    			System.out.println("ERROR: Illegal mode.");// Should not happen
    		}
    		
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
    
    private double saturate(double u, double minValue, double maxValue) {
		if (u > maxValue) {
			u = maxValue;
		} else if (u < minValue) {
			u = minValue;
		}			
		return u;
	}
    
    private void sendDataToOpCom(double yref, double y, double u) {
		double x = (double)(System.currentTimeMillis() - startTime) / 1000.0;
		DoublePoint dp = new DoublePoint(x,u);
		PlotData pd = new PlotData(x,yref,y);
		opCom.putControlDataPoint(dp);
		opCom.putMeasurementDataPoint(pd);
	}
	
}


