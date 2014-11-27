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
    Communication comm;
    //public static void main(String[] args) throws InterruptedException {
    	public Graph (Communication com) throws InterruptedException {
    	comm = com;
    	gen myGen = new gen(comm);
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
    	Communication comm;
    	String message;
    	public gen (Communication com){
    		comm = com;
    	}

        public void run() {
            while(true) {
            	try{
            	message = comm.receive();
            	} catch (Exception e){
            		System.out.println("comm.receive() doesn't work. (Typsikt MAC)");
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
// Reference:
//
// http://stackoverflow.com/questions/1389285/real-time-graphing-in-java