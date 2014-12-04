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
	private JButton button1, button2;
	private JPanel panel;
	private JTextArea textArea, textArea2;
	SocketClient sc;
	private ArrayList<TimeSeries> TimeSeriesList;
	int up, down, left, right;
	double K = 0, Ti = 0, Td = 0, Tr = 0, N = 0, Beta = 0; 
	String paraString;
	
	public Graph(SocketClient sc) {
		this.sc = sc;
		setupPlots();
	}
	private void setupPlots(){
		TimeSeriesList = new ArrayList<TimeSeries>();
 		frame = new JFrame("Plots");
 		panel = new JPanel();
 		String text = "Control the Lego Segwy by using the following keyboard keys:"
 				+ "\nA:Left\nS:Down\nD:Right\nW:Up";
 		textArea = new JTextArea(text, 5, 10);
 		panel.add(textArea);
 		textArea2 = new JTextArea("PID parameters:", 5, 10);
 		panel.add(textArea2);
 		updateParameters(0, 0, 0, 0, 0, 0);
 		frame.setLayout(new GridLayout(3,2));
 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 		
 		frame.addKeyListener(this);
 		panel.setFocusable(true);
 		frame.setFocusable(true);
 		
	}
	
	public void updateParameters(double K, double Ti, double Td, double Tr, double N, double Beta){
		paraString = "PID parameters:"
				+ "\nK:" + K
				+ "\nTd:" + Ti
				+ "\nTd:" + Td
				+ "\nTr:" + Tr
				+ "\nN:" + N
				+ "\nBeta: " + Beta;
		textArea2.setText(paraString);
	}
	
	public void build(){
		frame.add(panel);
		frame.pack();
 		frame.setVisible(true);
	}
	
	public void createButtons() {
 		button1 = new JButton("Calibrate");
 		button1.addActionListener(this);
 		panel.add(button1);
 		button2 = new JButton("Joakim");
 		button2.addActionListener(this);
 		panel.add(button2);
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
		gen myGen = new gen(sc, TimeSeriesList, this);
		new Thread(myGen).start();
	}
	
	public void actionPerformed(ActionEvent e) { 
		if(e.getSource() == button1){
			try {
				sc.send("C");
			}  catch (Exception ex){
				System.out.println("Graph: actionPerformed(Actionevent e) failed.");
			}
		}
		if(e.getSource() == button2){
			String newParam = textArea2.getText();
			System.out.println(newParam);
			System.out.println(newParam.split(" "));
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
		private Graph graph;
		
		public gen(SocketClient sc, ArrayList<TimeSeries> TimeSeriesList, Graph graph) {
			this.graph = graph;
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
				if (!message.equals("Fel")) {
					switch(message.charAt(0)) {
					case 'X': String[] param = new String[6];
						param = message.substring(1).split(",");
						//beta, K, Ti, Tr, Td, N
						graph.updateParameters(Double.valueOf(param[0]),
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
					case 'V': updateGraph(3, 10000);
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