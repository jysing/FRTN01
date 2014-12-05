
public class PID {
	private PIDParameters p;

	private double I; // Integrator state
	private double v; // Desired control signal
	private double e; // Current control error
	private double D; // Derivative state
	private double yOld=0;
	private double y = 0;
	private  double ad;
    private double bd; 
    private long time, interval;
    
	
	// Constructor
	public PID(){
		PIDParameters p = new PIDParameters();
		time = System.currentTimeMillis();
		  p.Beta = 1.0;
		 // p.H = 0.02;
		  p.integratorOn = true;
		  p.K = 50; //K =2.5 //0.84
		  p.Ti = 0.5; //Ti = 0.5
		  p.Tr = 0.5;
		  p.Td = 0;
		  p.N = 5;
		  
		  setParameters(p);
		  
		  this.I = 0;
		  this.v = 0;
		  this.e = 0;
		  this.D = 0;
	}
	
	// Calculates the control signal v.
	public synchronized double calculateOutput(double y, double yref){
		interval = System.currentTimeMillis() - time;
		time = time + interval;
		this.y = y;
		this.e = yref - y;
		ad = 0;
		if(p.Td != 0) ad = p.Td/(p.Td+p.N*interval);
        bd = p.K*ad*p.N; 
		this.D = ad*D - bd*(y - yOld);
		this.v = p.K * (p.Beta * yref - y) + I + D; // I is 0.0 if integratorOn is false
		return this.v;
	}
	
	// Updates the controller state.
	public synchronized void updateState(double u){
		 if (p.integratorOn) {
			  I = I + ((p.K * interval / p.Ti) * e + (interval / p.Tr) * (u - v))/1000;  
			 
		  } else {
			  I = 0.0;
		  }
		 yOld = y;
	}
	
	// Returns the sampling interval expressed as a long.
	public synchronized long getHMillis(){
		return (long)(interval * 1000.0);
	}
	
	// Sets the PIDParameters.
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
