import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class Graph {
	@SuppressWarnings("deprecation")
	static TimeSeries ts = new TimeSeries("data", Millisecond.class);
	JFrame frame;
	
	public Graph(SocketClient sc) {
		setup(sc);
	}
	private void setup(SocketClient sc){
		gen myGen = new gen(sc);
 		new Thread(myGen).start();
 		frame = new JFrame("Plot deluxe");
 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	//////////////////////////////////////////////////
		// FIXA ANTAL GRAPHS I SAMMA FRAME \\
	//////////////////////////////////////////////////
	public void createWindow(SocketClient sc, String graphName) throws InterruptedException {
 		
		TimeSeriesCollection dataset = new TimeSeriesCollection(ts);
 		JFreeChart chart = ChartFactory.createTimeSeriesChart(graphName,
 				"Time", "Value", dataset, true, true, false);
 		final XYPlot plot = chart.getXYPlot();
 		ValueAxis axis = plot.getDomainAxis();
 		axis.setAutoRange(true);
 		axis.setFixedAutoRange(60000.0);
 		ChartPanel label = new ChartPanel(chart);
 		frame.getContentPane().add(label);
 		frame.pack();
 		frame.setVisible(true);
	}

	static class gen implements Runnable {
		private String message;
		private SocketClient soc;

		public gen(SocketClient sc) {
			soc = sc;
		}

        public void run() {
            while(true) {
            	try{
            	message = soc.receive();
            	} catch (Exception e){
            		System.out.println("soc.receive() doesn't work.");
            	}           	
            	if (message != "Fel"){
            		int num = Integer.parseInt(message); //Mätdata
            		System.out.println(num);
            		ts.addOrUpdate(new Millisecond(), num);
            		try {
            			Thread.sleep(20);
            		} catch (InterruptedException ex) {
            			System.out.println(ex);
            		}
            	}
            }
        }
    }
}