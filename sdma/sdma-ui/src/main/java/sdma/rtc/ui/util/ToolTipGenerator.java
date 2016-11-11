package sdma.rtc.ui.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

public class ToolTipGenerator implements XYToolTipGenerator {
	public String generateToolTip(XYDataset xyDataset, int series, int item) {
		// maxi moy mini direct temp

				DecimalFormat form = new DecimalFormat("00.0");
				SimpleDateFormat sdf = new SimpleDateFormat(
						"'Relevé du 'dd/MM/yy ' à ' HH:mm '\n'");
				Number x = xyDataset.getX(series, item);
				return (sdf.format(x)) + "-----------------------------------\n"
						+ "Maxi\t\t: " + form.format(xyDataset.getYValue(1, item))
						+ "\n" + "Moyenne\t: "
						+ form.format(xyDataset.getYValue(1, item)) + "\n"
						+ "Mini\t\t: " + form.format(xyDataset.getYValue(0, item))
						+ "\n" + "Temp\t\t: " + xyDataset.getYValue(1, item)
				// + "\n"
				;
	}
}