	import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class Graph implements ActionListener, KeyListener {
	
	private JFrame frame;
	private JButton button;
	private JPanel panel;
	private JTextArea textArea,textArea2;
	SocketClient sc;
	private ArrayList<TimeSeries> TimeSeriesList;
	int up, down, left, right;
	double K = 0, Td = 0, Ti = 0;
	
	public Graph(SocketClient sc) {
		this.sc = sc;
		setup();
	}
	private void setup(/*SocketClient sc*/){
		TimeSeriesList = new ArrayList<TimeSeries>();
 		frame = new JFrame("Plots");
 		panel = new JPanel();
 		String text = "Control the Lego Segwy by using the following keyboard keys:"
 				+ "\nA:Left\nS:Down\nD:Right\nW:Up";
 		textArea = new JTextArea(text, 5, 10);
 		panel.add(textArea);
 		textArea2 = new JTextArea("PID parameters:", 10, 20);
 		panel.add(textArea2);
 		updateParameters(1, 2, 3);
 		frame.setLayout(new GridLayout(3,2));
 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 		
 		frame.addKeyListener(this);
 		panel.setFocusable(true);
 		frame.setFocusable(true);
	}
	
	public void updateParameters(double K, double Td, double Ti){
		textArea2.setText("PID parameters:"
				+"\nK: " + K 
				+"\nTd: " + Td
				+"\nTi: " + Ti);
	}
	
	public void build(){
		frame.add(panel);
	}
	
	public void createButton(String buttonName) {
 		button = new JButton(buttonName);
 		button.addActionListener(this);
 		panel.add(button);
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
		//System.out.println("actionPerformed");
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
					case 'X': String[] param = new String[6];
						param = message.substring(1).split(",");
						//beta, K, Ti, Tr, Td, N
						updateParameters(Double.valueOf(param[0]),
								Double.valueOf(param[1]),
								Double.valueOf(param[2]),
								Double.valueOf(param[3]),
								Double.valueOf(param[4]),
								Double.valueOf(param[5]));
						break;
					case 'U': updateGraph(0, 120);
						break;
					case 'E': updateGraph(1, 200);
						break;
					case 'A': updateGraph(2, 20);
						break;
					case 'V': updateGraph(3, 200);
						break;
					case 'P': updateGraph(4, 200);
						break;
					case 'B': updateGraph(5, 100);
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
		
		private void updateGraph(int pos, double maxValue) {
			message = message.substring(1);
			double num = Double.parseDouble(message);
			if (num > maxValue){
				num = maxValue;
			} else if (num < (-maxValue)){
				num = -maxValue;
			}
			TimeSeriesList.get(pos).addOrUpdate(new Millisecond(), num);
		}
	}
}