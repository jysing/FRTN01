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
    static TimeSeries ts = new TimeSeries("data", Millisecond.class);
    private SocketClient sc;
    //public static void main(String[] args) throws InterruptedException {
    	public Graph (SocketClient sc) throws InterruptedException {
    	this.sc = sc;
    	gen myGen = new gen(sc);
        new Thread(myGen).start();

        TimeSeriesCollection dataset = new TimeSeriesCollection(ts);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "GraphTest",
            "Time",
            "Value",
            dataset,
            true,
            true,
            false
        );
        final XYPlot plot = chart.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);

        JFrame frame = new JFrame("GraphTest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChartPanel label = new ChartPanel(chart);
        frame.getContentPane().add(label);
        //Suppose I add combo boxes and buttons here later

        frame.pack();
        frame.setVisible(true);
    //}
    	}

    static class gen implements Runnable {
    	private Communication comm;
    	private String message;
    	private SocketClient sc;
    	public gen (SocketClient sc){
    		this.sc = sc;
    	}

        public void run() {
            while(true) {
            	try{
            	if (sc.isConnected()) {
            		message = sc.receive();            		
            	} else {
            		System.out.println("Socket not connected");
            	}
            	} catch (Exception e){
            		System.out.println("soc.receive() doesn't work.");
            	}
            	if (!message.equals("Fel")){
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