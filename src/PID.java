import lejos.hardware.lcd.LCD;

public class PID {
	private PIDParameters p;

	private double I; // Integrator state
	private double v; // Desired control signal
	private double e; // Current control error
	private double D; // Derivative state
	private double yOld = 0;
	private double y = 0;
	private double ad;
	private double bd;
	private long time, interval;

	public PID(String type) {
		PIDParameters p = new PIDParameters();
		time = System.currentTimeMillis();
		if (type.equals("Ang")) {
			p.Beta = 1.0;
			p.integratorOn = true;
			p.K = 35;
			p.Ti = 0.05;
			p.Tr = 0.5;
			p.Td = 0.25;
			p.N = 5;

			setParameters(p);
		} else if (type.equals("Pos")) {
			p.Beta = 1.0;
			p.integratorOn = true;
			p.K = -10;
			p.Ti = 0.05;
			p.Tr = 0.5;
			p.Td = 0.25;
			p.N = 5;

			setParameters(p);
		} else {
			LCD.drawString("Wrong type of pid", 0, 3);
		}
		this.I = 0;
		this.v = 0;
		this.e = 0;
		this.D = 0;
	}

	public synchronized double calculateOutput(double y, double yref) {
		interval = System.currentTimeMillis() - time;
		time = time + interval;
		this.y = y;
		this.e = yref - y;
		ad = 0;
		if (p.Td != 0) ad = p.Td / (p.Td + p.N * interval);
		bd = p.K * ad * p.N;
		this.D = ad * D - bd * (y - yOld);
		this.v = p.K * (p.Beta * yref - y) + I + D; // I is 0.0 if integratorOn
													// is false
		return this.v;
	}

	public synchronized void updateState(double u) {
		if (p.integratorOn) {
			I = I + ((p.K * interval / p.Ti) * e + (interval / p.Tr) * (u - v));
		} else {
			I = 0.0;
		}
		yOld = y;
	}

	public synchronized void setParameters(PIDParameters newParameters) {
		p = (PIDParameters) newParameters.clone();
		if (!p.integratorOn) {
			I = 0.0;
		}
	}

	public synchronized void reset() {
		I = 0;
		D = 0;
	}

	public synchronized PIDParameters getParameters() {
		return (PIDParameters) p.clone();
	}
}
