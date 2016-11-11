package sdma.rtc.ui;

import java.awt.BasicStroke;
import java.awt.Panel;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;

import sdma.rtc.entity.ClassCtx;
import sdma.rtc.entity.Coupling;
import sdma.rtc.entity.MethodCtx;

/** 
 * 
 * **/
public class ClassTimeChart extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7212515509645530578L;
	// Name of the class for which we need to generate graph
	private String className = null;

	// Time gap interval
	private Double timeInterval = 600000.0;
	// time series for out coupling
	private List<Coupling> couplings = null;

	// date set collection
	private TimePeriodValuesCollection dataset1 = null;
	private DefaultCategoryDataset dataset2 = null;

	private int lastProcess = 0;
	private int processInterval = 20;

	private ChartPanel chartPanel2 = null;

	public ClassTimeChart() {
		super();
	}

	public ClassTimeChart(String className) {
		super();
		initUI(className);
	}

	private void initUI(String className) {
		setLayout(null);
		couplings = new ArrayList<Coupling>();
		this.className = className;
		// intialize and add series to dataset
		dataset1 = new TimePeriodValuesCollection();
		dataset1.addSeries(new TimePeriodValues("Out Coupling"));
		dataset1.addSeries(new TimePeriodValues("In Coupling"));

		dataset2 = new DefaultCategoryDataset();

		ChartPanel chartPanel1 = new ChartPanel(createChart1(dataset1));
		chartPanel1.setBounds(0, 0, 500, 400);

		chartPanel2 = new ChartPanel(createChart2(dataset2));
		chartPanel2.setBounds(525, 0, 500, 400);

		add(chartPanel1);
		add(chartPanel2);
	}

	private JFreeChart createChart2(final CategoryDataset dataset) {
		JFreeChart barChart = ChartFactory.createBarChart(className,
				"Coupling", "Time Slot", dataset, PlotOrientation.VERTICAL,
				true, true, false);

		return barChart;

	}

	private JFreeChart createChart1(final XYDataset dataset) {
		final JFreeChart result = ChartFactory.createTimeSeriesChart(className,
				"Time", "Coupling", dataset, true, true, false);
		final XYPlot plot = result.getXYPlot();

		// make the line width to 3 size
		plot.getRenderer().setSeriesStroke(0, new BasicStroke(3));
		plot.getRenderer().setSeriesStroke(1, new BasicStroke(3));

		// get time axis and show graph left to right and init auto range
		ValueAxis axis = plot.getDomainAxis();
		// axis.setInverted(true);
		axis.setFixedAutoRange(timeInterval);
		plot.getRenderer().setSeriesToolTipGenerator(0, new ToolTipGenerator());
		plot.getRenderer().setSeriesToolTipGenerator(1, new ToolTipGenerator());
		return result;
	}

	public void update(Coupling coupling) {

		Coupling lclCoupling = null;
		try {
			lclCoupling = (Coupling) coupling.clone();
			couplings.add(lclCoupling);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		// add data to time series graph and update
		this.dataset1.getSeries(0).add(
				new TimePeriodValue(new Millisecond(), lclCoupling
						.getMessageScope(2)));
		this.dataset1.getSeries(1).add(
				new TimePeriodValue(new Millisecond(), lclCoupling
						.getMessageScope(1)));
		//couplings.add(lclCoupling);
		int nextProcess = (lastProcess * processInterval) + processInterval;
		int size = couplings.size();

		if (nextProcess <= size) {
			Coupling balanceCoupling = processSlot(couplings.size());
			dataset2.addValue(balanceCoupling.getMessageScope(2)
					+ balanceCoupling.getMessageScope(2), "EC", nextProcess + "");
			dataset2.addValue(balanceCoupling.getMessageScope(1)
					+ balanceCoupling.getMessageScope(1), "IC", nextProcess + "");
			
			chartPanel2.validate();
			lastProcess++;
		}
	}

	private Coupling processSlot(int end) {
		try {
			Coupling baseCoupling = (Coupling) couplings.get(
					lastProcess * processInterval).clone();
			Coupling currCoupling = (Coupling) couplings.get(end - 1).clone();
			//System.out.println("[Processing for slot]\n\n\n\n\n\n\n\n\n\n");
			return currCoupling.barBalanceCoupling(baseCoupling);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	class ToolTipGenerator implements XYToolTipGenerator {
		public String generateToolTip(XYDataset xyDataset, int series, int item) {

			Coupling coupling = couplings.get(item);

			StringBuffer buffer = new StringBuffer();
			if (coupling != null) {
				if (series == 1) {
					buffer.append("<html><body>");
					if (coupling.getInCoupling() != null
							&& coupling.getInCoupling().size() > 0) {
						buffer.append("Coupling Detail<br>");
						for (ClassCtx cls : coupling.getInCoupling()) {
							buffer.append("Class: " + cls.getName() + "<br>");
							if (cls.getMethodCtxs().size() > 0) {
								buffer.append("Methods:"
										+ cls.getMethodCtxs().size() + "<br>");
								for (MethodCtx mtd : cls.getMethodCtxs()) {
									buffer.append(mtd.getSignature()
											+ ":Call Count:"
											+ mtd.getCallCount() + "<br>");
								}
							} else {
								buffer.append("No Methods<br>");
							}
						}
					} else {
						buffer.append("IC=0");
					}
					buffer.append("</body></html/>");
				} else {
					buffer.append("<html><body>");
					if (coupling.getOutCoupling() != null
							&& coupling.getOutCoupling().size() > 0) {
						buffer.append("Coupling Detail<br>");
						for (ClassCtx cls : coupling.getOutCoupling()) {
							if (cls.getMethodCtxs().size() > 0) {
								buffer.append("Class: " + cls.getName()
										+ "<br>");
								if (cls.getMethodCtxs().size() > 0) {
									buffer.append("Methods: "
											+ cls.getMethodCtxs().size()
											+ "<br>");
									for (MethodCtx mtd : cls.getMethodCtxs()) {
										buffer.append(mtd.getSignature()
												+ ":Call Count:"
												+ mtd.getCallCount() + "<br>");
									}
								} else {
									buffer.append("No Methods<br>");
								}
							} else {
								buffer.append("EC=0");
							}
						}
					}
				}
				buffer.append("</body></html/>");
			}

			return buffer.toString();
		}
	}
	
	public Coupling getFinalCoupling(){
		return couplings.get(couplings.size()-1);
	}

}
