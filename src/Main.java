import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import lejos.hardware.lcd.LCD;

public class Main {
	
	public static void main(String [] args){

		/*
		int timeout = 10000;
		Communication ms;
		try {
			ms = new Communication(6666, timeout);
			ms.theMain();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
			int port = 6666;
			int timeout = 30000;
			Communication ss = null;
			try {
				LCD.drawString("1", 0, 1);
				ss = new Communication(port, timeout);
				LCD.drawString("2", 0, 1);
			} catch (IOException ex) {
				Logger.getLogger(Communication.class.getName()).log(Level.SEVERE,
						null, ex);
			}

			try {
				LCD.drawString("3", 0, 1);
				ss.connect();
				LCD.drawString("4", 0, 1);
			} catch (java.net.SocketTimeoutException ex) {
				Logger.getLogger(ex.toString());
			} catch (IOException ex) {
				Logger.getLogger(Communication.class.getName()).log(Level.SEVERE,
						null, ex);
			}
			LCD.drawString("5", 0, 1);
			if (ss.isConnected()) {
				LCD.drawString("6", 0, 1);
				String response = null;
				try {
					LCD.drawString("7", 0, 1);
					response = ss.sendAndReceive("1");
					System.out.println("Response  " + response);
					response = ss.sendAndReceive("2");
					System.out.println("Response  " + response);
					response = ss.sendAndReceive("3");
					System.out.println("Response  " + response);
					LCD.drawString("8", 0, 1);
				} catch (IOException ex) {
					Logger.getLogger(Communication.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		
		
		
		
		
		
		
		
		
		
		
		Graph graph = new Graph();
		Gyro gyro = new Gyro();
		//Communication m = new Communication();
		//gyro.start();
		Regul regul = new Regul(gyro,2); //Next highest priority.
									//Reason is to get more accurate samples.
									//Might need to be changed to highest later on.
		regul.start();
	}
}
