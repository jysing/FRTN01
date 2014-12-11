package PC;
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
	private JButton resetButton, updateButton;
	private JPanel panel;
	private JTextArea textArea, textArea2, textArea3;
	private SocketClient sc;
	private ArrayList<TimeSeries> TimeSeriesList;
	private double K_outer = 0, Ti_outer = 0, Td_outer = 0, Tr_outer = 0,
			N_outer = 0, beta_outer = 0;
	private double K_inner = 0, Ti_inner = 0, Td_inner = 0, Tr_inner = 0,
			N_inner = 0, beta_inner = 0;
	private String paraString;
	private int key;

	public Graph(SocketClient sc) {
		this.sc = sc;
		setupPlots();
	}

	private void setupPlots() {
		TimeSeriesList = new ArrayList<TimeSeries>();
		frame = new JFrame("Plots");
		panel = new JPanel();
		String text = "Control the Lego Segwy by using the following keyboard keys:"
				+ "\nA:Left\nS:Down\nD:Right\nW:Up";
		textArea = new JTextArea(text, 5, 10);
		panel.add(textArea);
		textArea2 = new JTextArea("PID parameters Outer:", 5, 10);
		textArea3 = new JTextArea("PID parameters Inner:", 5, 10);
		panel.add(textArea2);
		panel.add(textArea3);
		updateParametersOuter(0, 0, 0, 0, 0, 0);
		updateParametersInner(0, 0, 0, 0, 0, 0);
		frame.setLayout(new GridLayout(3, 2));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(this);
		panel.setFocusable(true);
		frame.setFocusable(true);
	}

	public void updateParametersOuter(double beta, double K, double Ti,
			double Tr, double Td, double N) {
		this.beta_outer = beta;
		this.K_outer = K;
		this.Ti_outer = Ti;
		this.Tr_outer = Tr;
		this.Td_outer = Td;
		this.N_outer = N;

		paraString = "PID (Outer):" + "\nBeta:" + beta + "\nK:" + K + "\nTi:"
				+ Ti + "\nTr:" + Tr + "\nTd:" + Td + "\nN:" + N;
		textArea2.setText(paraString);
	}

	public void updateParametersInner(double beta, double K, double Ti,
			double Tr, double Td, double N) {
		this.beta_inner = beta;
		this.K_inner = K;
		this.Ti_inner = Ti;
		this.Tr_inner = Tr;
		this.Td_inner = Td;
		this.N_inner = N;

		paraString = "PID (Inner):" + "\nBeta:" + beta + "\nK:" + K + "\nTi:"
				+ Ti + "\nTr:" + Tr + "\nTd:" + Td + "\nN:" + N;
		textArea3.setText(paraString);
	}

	public void build() {
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	public void createButtons() {
		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		panel.add(resetButton);
		updateButton = new JButton("Update param");
		updateButton.addActionListener(this);
		panel.add(updateButton);
	}

	public void createWindow(String graphName, String xValue, String yValue,
			String data) throws InterruptedException {
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
		if (e.getSource() == resetButton) {
			try {
				updateParametersOuter(beta_outer, K_outer, Ti_outer, Tr_outer,
						Td_outer, N_outer);
				updateParametersInner(beta_inner, K_inner, Ti_inner, Tr_inner,
						Td_inner, N_inner);
				sc.send("C");
			} catch (Exception ex) {
				System.out
						.println("Graph: actionPerformed(Actionevent e) failed.");
			}
		}
		if (e.getSource() == updateButton) {
			String newParam = textArea2.getText();
			sc.send("N" + newParam);
			String newParam2 = textArea3.getText();
			sc.send("M" + newParam2);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		key = e.getKeyCode();
		System.out.println("                    " + key + " from keyPressed");
		switch (key) {
		case 65:
			sc.send("L");
			break;
		case 68:
			sc.send("R");
			break;
		case 87:
			sc.send("F");
			break;
		case 83:
			sc.send("B");
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		sc.send("S");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Do nothing
	}

	static class gen extends Thread {
		private String message;
		private SocketClient sc;
		private ArrayList<TimeSeries> TimeSeriesList;
		private Graph graph;
		private static final long period = 100;

		public gen(SocketClient sc, ArrayList<TimeSeries> TimeSeriesList,
				Graph graph) {
			this.graph = graph;
			this.TimeSeriesList = TimeSeriesList;
			this.sc = sc;
		}

		public void run() {
			String[] param = new String[6];
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
					switch (message.charAt(0)) {
					case 'X':
						param = message.substring(1).split(",");
						// beta, K, Ti, Tr, Td, N
						graph.updateParametersOuter(Double.valueOf(param[0]),
								Double.valueOf(param[1]),
								Double.valueOf(param[2]),
								Double.valueOf(param[3]),
								Double.valueOf(param[4]),
								Double.valueOf(param[5]));
						break;
					case 'Y':
						param = message.substring(1).split(",");
						// beta, K, Ti, Tr, Td, N
						graph.updateParametersInner(Double.valueOf(param[0]),
								Double.valueOf(param[1]),
								Double.valueOf(param[2]),
								Double.valueOf(param[3]),
								Double.valueOf(param[4]),
								Double.valueOf(param[5]));
						break;
					case 'U':
						updateGraph(0, 120);
						break;
					case 'E':
						updateGraph(1, 200);
						break;
					case 'A':
						updateGraph(2, 20);
						break;
					case 'V':
						updateGraph(3, 10000);
						break;
					case 'P':
						updateGraph(4, 200);
						break;
					case 'B':
						updateGraph(5, 100);
						break;
					case 'W':
						updateGraph(6, 200);
						break;
					}
					case 'W':
						updateGraph(6, 200);
						break;
				}

				try {
					Thread.sleep(period);
				} catch (InterruptedException ex) {
					System.out.println(ex);
				}
			}
		}

		private void updateGraph(int pos, double maxValue) {
			message = message.substring(1);
			double num = Double.parseDouble(message);
			if (num > maxValue) {
				num = maxValue;
			} else if (num < (-maxValue)) {
				num = -maxValue;
			}
			TimeSeriesList.get(pos).addOrUpdate(new Millisecond(), num);
		}
	}
}