package sdma.rtc.ui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import sdma.rtc.entity.Coupling;



public class CouplingListCellRenderer extends JPanel implements
		ListCellRenderer<Coupling> {

	private JLabel classLabel = null;
	private JLabel inCoupleLabel = null;
	private JLabel outCoupleLabel = null;

	public CouplingListCellRenderer() {
		super();

		JPanel classPn = new JPanel(new FlowLayout(0));
		JPanel inPn = new JPanel(new FlowLayout(0));
		JPanel outPn = new JPanel(new FlowLayout(0));

		GridBagLayout gb = new GridBagLayout();
		gb.columnWeights = new double[] { 3.0, 1.0, 1.0 };
		this.setLayout(gb);

		classLabel = new JLabel();
		inCoupleLabel = new JLabel();
		outCoupleLabel = new JLabel();

		classPn.add(classLabel);
		inPn.add(inCoupleLabel);
		outPn.add(outCoupleLabel);

		classPn.setPreferredSize(new Dimension(650, 20));
		inPn.setPreferredSize(new Dimension(200, 20));
		outPn.setPreferredSize(new Dimension(200, 20));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		add(classPn, c);
		c.anchor = GridBagConstraints.WEST;
		add(inPn, c);
		c.anchor = GridBagConstraints.WEST;
		add(outPn, c);

	}

	public Component getListCellRendererComponent(
			JList<? extends Coupling> list, Coupling value, int index,
			boolean isSelected, boolean cellHasFocus) {
		if (value != null && value.getClassName() != null
				&& !value.getClassName().trim().equals("")) {
			this.classLabel.setText(value.getClassName().substring(1,
					value.getClassName().length() - 1));
			this.inCoupleLabel.setText("CSC:"+value.getClassScope(1) + ", MTC:"
					+ value.getMethodScope(1) + ", MSC:" + value.getMessageScope(1));
			this.outCoupleLabel.setText("CSC:"+value.getClassScope(2) + ", MTC:"
					+ value.getMethodScope(2) + ", MSC" + value.getMessageScope(2));
			return this;
		}else{
			return null;
		}
	}

}
