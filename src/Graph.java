import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;
import lejos.robotics.SampleProvider;

public class Graph implements RMISampleProvider {
	float[] info;
	
	public Graph() throws Exception {
		info = new float[10000];
	}

	@Override
	public void close() throws RemoteException {
		
	}

	@Override
	public float[] fetchSample() throws RemoteException {
		for(int i = 0; i < 10000; i++) {
			info[i] = i;
		}
		return info;
	}

	
}
