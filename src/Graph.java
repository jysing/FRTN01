import java.awt.GridLayout;

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
	
	//static TimeSeries ts2 = new TimeSeries("Measurement 2", Millisecond.class);
	JFrame frame;
	
	public Graph(SocketClient sc) {
		setup(sc);
	}
	private void setup(SocketClient sc){
 		frame = new JFrame("Plot deluxe");
 		frame.setLayout(new GridLayout(1,0));
 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void createWindow(SocketClient sc, String graphName, String xValue, String yValue, String data) throws InterruptedException {
		@SuppressWarnings("deprecation")
		final TimeSeries ts = new TimeSeries(data, Millisecond.class);
		TimeSeriesCollection dataset = new TimeSeriesCollection(ts);
 		JFreeChart chart = ChartFactory.createTimeSeriesChart(graphName,
 				xValue, yValue, dataset, true, true, false);
 		final XYPlot plot = chart.getXYPlot();
 		ValueAxis axis = plot.getDomainAxis();
 		axis.setAutoRange(true);
 		axis.setFixedAutoRange(60000.0);
 		ChartPanel label = new ChartPanel(chart);
 		frame.getContentPane().add(label);
 		frame.pack();
 		frame.setVisible(true);
 		
 		gen myGen = new gen(sc, ts);
 		new Thread(myGen).start();
	}

	static class gen implements Runnable {
		private String message;
		private SocketClient soc;
		private TimeSeries ts;
		
		public gen(SocketClient sc, TimeSeries timeseries) {
			ts = timeseries;
			soc = sc;
		}

		public void run() {
			while (true) {
				try {
					if (soc.isConnected()) {
						message = soc.receive();
					} else {
						System.out.println("Socket not connected");
					}
				} catch (Exception e) {
					System.out.println("soc.receive() doesn't work.");
				}
				//////////////////////////////////////////
				//		Flags for different data		//
				//////////////////////////////////////////
				if (!message.equals("Fel")) {
					//if (message.charAt(0) == '#'){ 				// # - Flag for control signal
						double num = Double.parseDouble(message);
						System.out.println(num);
						ts.addOrUpdate(new Millisecond(), num);
						try {
							Thread.sleep(10);
						} catch (InterruptedException ex) {
							System.out.println(ex);
						}
					/*} else if (message.charAt(0) == '%'){ 		// % - Flag for Measurement 2
						double num = Double.parseDouble(message);
						System.out.println(num);
						ts.addOrUpdate(new Millisecond(), num); // Ska vara ts2!
						try {
							Thread.sleep(10);
						} catch (InterruptedException ex) {
							System.out.println(ex);
						}
					}*/					
				}
			}
		}
	}
}