import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

public class Graph implements ActionListener, KeyListener {
	
	JFrame frame;
	JButton button;
	JPanel panel;
	SocketClient sc;
	private ArrayList<TimeSeries> TimeSeriesList;
	int up, down, left, right;
	
	public Graph(SocketClient sc) {
		this.sc = sc;
		setup();
	}
	private void setup(/*SocketClient sc*/){
		TimeSeriesList = new ArrayList<TimeSeries>();
 		frame = new JFrame("Plots");
 		panel = new JPanel();
 		frame.setLayout(new GridLayout(3,2));
 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 		
 		frame.addKeyListener(this);
 		panel.setFocusable(true);
 		frame.setFocusable(true);
	}
	
	public void createButton(String buttonName) {
 		button = new JButton(buttonName);
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
		System.out.println("actionPerformed");
		try {
			sc.send("C");
		}  catch (Exception ex){
			System.out.println("Graph: actionPerformed(Actionevent e) failed.");
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		gen.key = e.getKeyCode();
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		gen.key = 0;
	}
	
	@Override
	public void keyTyped(KeyEvent e){
		// Do nothing.
	}

	static class gen implements Runnable {
		private String message;
		private SocketClient sc;
		private ArrayList<TimeSeries> TimeSeriesList;
		public static int key;
		
		public gen(SocketClient sc, ArrayList<TimeSeries> TimeSeriesList) {
			this.TimeSeriesList = TimeSeriesList;
			this.sc = sc;
		}

		public void run() {
			while (true) {
				try {
					if (sc.isConnected()) {
						message = sc.receive();	
					} else {
						System.out.println("Socket not connected");
					}
				} catch (Exception e) {
					System.out.println("soc.receive() doesn't work.");
				}
				//////////////////////////////////////////
				//		Flags for different data		//
				/////////////////////////////////////////
				if (!message.equals("Fel")) {
					switch(message.charAt(0)) {
					case 'U': updateGraph(0);
						break;
					case 'E': updateGraph(1);
						break;
					case 'A': updateGraph(2);
						break;
					case 'V': updateGraph(3);
						break;
					case 'P': updateGraph(4);
						break;
					case 'B': updateGraph(5);
						break; 
					}
				}
				
				switch(key){
				case 0: sc.send("S");
					break;
				case 65: sc.send("L");
					break;
				case 68: sc.send("R");
					break;
				case 87: sc.send("F");
					break;
				case 83: sc.send("B");
					break;
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