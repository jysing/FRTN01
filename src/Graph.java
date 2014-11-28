import java.awt.GridLayout;
import java.util.ArrayList;

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
	
	JFrame frame;
	private ArrayList<TimeSeries> TimeSeriesList;
	
	public Graph(SocketClient sc) {
		setup(sc);
	}
	private void setup(SocketClient sc){
		TimeSeriesList = new ArrayList<TimeSeries>();
 		frame = new JFrame("Plot deluxe");
 		frame.setLayout(new GridLayout(1,0));
 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void createWindow(String graphName, String xValue, String yValue, String data) throws InterruptedException {
		@SuppressWarnings("deprecation")
		TimeSeries ts = new TimeSeries(data, Millisecond.class);
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
 		TimeSeriesList.add(ts);
	}
	
	public void start(SocketClient sc) {
		gen myGen = new gen(sc, TimeSeriesList);
		new Thread(myGen).start();		
	}

	static class gen implements Runnable {
		private String message;
		private SocketClient soc;
		private ArrayList<TimeSeries> TimeSeriesList;
		
		public gen(SocketClient sc, ArrayList<TimeSeries> TimeSeriesList) {
			this.TimeSeriesList = TimeSeriesList;
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
					if (message.charAt(0) == 'U'){
						message = message.substring(1);
						double num = Double.parseDouble(message);
						System.out.println(num);
						TimeSeriesList.get(0).addOrUpdate(new Millisecond(), num);
					} else if (message.charAt(0) == 'E') {
						message = message.substring(1);
						double num = Double.parseDouble(message);
						System.out.println(num);
						TimeSeriesList.get(1).addOrUpdate(new Millisecond(), num);
					}					
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException ex) {
					System.out.println(ex);
				}
			}
		}
	}
}