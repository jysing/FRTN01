
public class SimpsonIteration {

		
		   public static double f(double x) {
		      return Math.exp(- x * x / 2) / Math.sqrt(2 * Math.PI);
		   }
		   
		   public static double integrate(double a, double b, double n) {
		      double h = (b - a) / (n - 1);
		 
		      double sum = 1.0 / 3.0 * (f(a) + f(b));
		      
		      for (int i = 1; i < n - 1; i += 2) {
		         double x = a + h * i;
		         sum += 4.0 / 3.0 * f(x);
		      }

		      for (int i = 2; i < n - 1; i += 2) {
		         double x = a + h * i;
		         sum += 2.0 / 3.0 * f(x);
		      }

		      return sum * h;
		   }
}
//http://introcs.cs.princeton.edu/java/93integration/SimpsonsRule.java.html