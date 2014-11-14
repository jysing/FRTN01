import java.io.DataInputStream;
import java.io.IOException;

import lejos.hardware.Bluetooth;
import lejos.hardware.lcd.LCD;
import lejos.remote.nxt.NXTCommConnector;
import lejos.remote.nxt.NXTConnection;

public class Communication extends Thread {

	public Communication() {
		this.start();
	}

	public void run(){
		Boolean isrunning=true;
		while (true)
		{
			LCD.drawString("Waiting",0,0);
			LCD.refresh();
			//Listen for incoming connection
			NXTCommConnector btc = Bluetooth.getNXTCommConnector();
			NXTConnection connection = btc.waitForConnection(1000000,NXTConnection.RAW);
			LCD.clear();
			LCD.drawString("Connected",0,0);
			LCD.refresh();  
			//The InputStream for read data 
			DataInputStream dis = connection.openDataInputStream();
			//loop for read data    
			while(isrunning){
				Byte n=-1;
				try {
					n=dis.readByte();
				} catch (IOException e) {
					LCD.drawString("Reading from input aborted", 0, 0);
				}
				LCD.clear();
				LCD.drawInt(n, 4, 4);
			}
			try {
				dis.close();
			} catch (IOException e1) {
				LCD.drawString("Failed to close DataInputStream", 0, 0);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				LCD.drawString("Sleep", 0, 0);
			} 
			// wait for data to drain
			LCD.clear();
			LCD.drawString("Closing",0,0);
			LCD.refresh();
			try {
				connection.close();
			} catch (IOException e) {
				LCD.drawString("Failed to close connection", 0, 0);
				e.printStackTrace();
			}
			LCD.clear();
		}
	}
}