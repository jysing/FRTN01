
public class PID {
	// Current PID parameters
	private PIDParameters p;

	private double I; // Integrator state
	private double v; // Desired control signal
	private double e; // Current control error
	private double D; // Derivative state
	private double yOld=0;
	private double y = 0;
	
	// Constructor
	public PID(){
		PIDParameters p = new PIDParameters();
		  p.Beta = 1.0;
		  p.H = 0.02;
		  p.integratorOn = false;
		  p.K = 0.2;
		  p.Ti = 0.0;
		  p.Tr = 0.0;
		  p.Td = 0.0;

		  p.N = 10;//micke suger
		  
		  setParameters(p);
		  
		  this.I = 0;
		  this.v = 0;
		  this.e = 0;
		  this.D = 0;
	}
	
	// Calculates the control signal v.
	// Called from BallAndBeamRegul.	
	public synchronized double calculateOutput(double y, double yref){
		this.y = y;
		this.e = yref - y;
		this.D = p.ad*D - p.bd*(y - yOld);
		this.v = p.K * (p.Beta * yref - y) + I + D; // I is 0.0 if integratorOn is false
		return this.v;
	}
	
	// Updates the controller state.
	// Should use tracking-based anti-windup
	// Called from BallAndBeamRegul.
	public synchronized void updateState(double u){
		 if (p.integratorOn) {
			  I = I + (p.K * p.H / p.Ti) * e + (p.H / p.Tr) * (u - v);
			  
		  } else {
			  I = 0.0;
		  }
		 yOld = y;
	}
	
	// Returns the sampling interval expressed as a long.
	// Explicit type casting needed.
	public synchronized long getHMillis(){
		return (long)(p.H * 1000.0);
	}
	
	// Sets the PIDParameters.
	// Called from PIDGUI.
	// Must clone newParameters.
	public synchronized void setParameters(PIDParameters newParameters){
		p = (PIDParameters)newParameters.clone();
		if (!p.integratorOn) {
			I = 0.0;
		}
	}
	
	 public synchronized void reset() {
		 I = 0;
		 D = 0;
	 }
	 
	 public synchronized PIDParameters getParameters() {
		 return p;
	 }
}
