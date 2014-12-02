import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class Graph implements ActionListener{
	
	JFrame frame;
	JButton button;
	JPanel panel;
	SocketClient sc;
	private ArrayList<TimeSeries> TimeSeriesList;
	
	public Graph(SocketClient sc) {
		this.sc = sc;
		setup();
	}
	private void setup(/*SocketClient sc*/){
		TimeSeriesList = new ArrayList<TimeSeries>();
 		frame = new JFrame("Plot deluxe");
 		frame.setLayout(new GridLayout(3,2));
 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 		
	}
	
	public void createButton() {
		panel = new JPanel();
 		button = new JButton("Calibrate");
 		button.addActionListener(this);
 		panel.add(button);
 		frame.add(panel);
 		frame.pack();
 		frame.setVisible(true);
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
	
	
	public void actionPerformed(ActionEvent e) {
		try {
			sc.send("#");
		}  catch (Exception ex){
			System.out.println("Graph: actionPerformed(Actionevent e) failed.");
		}
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
						updateGraph(0);
					} else if (message.charAt(0) == 'E') {
						updateGraph(1);
					} else if (message.charAt(0) == 'A') {
						updateGraph(2);
					} else if (message.charAt(0) == 'V') {
						updateGraph(3);
					} else if (message.charAt(0) == 'P') { //Position
						updateGraph(4);
					} else if (message.charAt(0) == 'B') { //Position velocity
						updateGraph(5);
					}else {
						System.out.println("Not a recognized value");
					}
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException ex) {
					System.out.println(ex);
				}
			}
		}
		
		private void updateGraph(int pos) {
			message = message.substring(1);
			double num = Double.parseDouble(message);
			TimeSeriesList.get(pos).addOrUpdate(new Millisecond(), num);
		}
	}
}