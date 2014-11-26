import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.Bluetooth;
import lejos.hardware.lcd.LCD;
import lejos.remote.ev3.RemoteEV3;
import lejos.remote.nxt.NXTCommConnector;
import lejos.remote.nxt.NXTConnection;

public class Communication extends Thread {
	
	RemoteEV3 ev3;

	public Communication() {
		this.start();
	}

	public void run(){
		Boolean isrunning=true;
		
		while (true)
		{			
			LCD.drawString("Waiting",0,0);
			//Listen for incoming connection
			try{
				ev3 = new RemoteEV3("130.235.126.71");
				ev3.getBluetoothDevice().authenticate("130.235.126.71", "123456");
				LCD.drawString("Success.", 0, 0);
				break;
				} catch (Exception e){
					
			}
			/*
			NXTCommConnector btc = Bluetooth.getNXTCommConnector();
			LCD.drawString("Got connector",0,1);
			NXTConnection connection = btc.waitForConnection(10000,NXTConnection.RAW);
			LCD.drawString("Connected",0,1);
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
			LCD.clear();*/
		}
	}
}