public class Main {
	
	public static void main(String [] args){
		Gyro gyro = new Gyro();
		//gyro.start();
		Regul regul = new Regul(gyro,2); //Next highest priority.
									//Reason is to get more accurate samples.
									//Might need to be changed to highest later on.
		regul.start();
	}
}
