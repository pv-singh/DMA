/*
 * Sudokuki - essential sudoku game
 * Copyright (C) 2007-2016 Sylvain Vedrenne
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jankenpoi.sudokuki.ui.swing;

import static net.jankenpoi.i18n.I18n.gtxt;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.jankenpoi.sudokuki.SudokuGrid;
import net.jankenpoi.sudokuki.generator.SudokuGeneratorFactory;
import net.jankenpoi.sudokuki.preferences.UserPreferences;
import net.jankenpoi.sudokuki.view.GridView;
/**
 * CheckUpdateDialog.java
 * 
 * @author svedrenne
 */
@SuppressWarnings("serial")
public class PrintMultiDialog extends JDialog {

	private JFrame parent;

	private final SwingWorker<Integer, Void> worker;

	private Object lock = new Object();
	private boolean cancelledDialog = false;

	public PrintMultiDialog(JFrame parent, final GridView view) {

		super(parent, true);
		this.parent = parent;
		setResizable(false);

		short[] flagsTable = new short[81];
		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				if (view.isCellReadOnly(li, co)) {
					flagsTable[9 * li + co] = view.getValueAt(li, co);
				}
			}
		}

		worker = new SwingWorker<Integer, Void>() {

			@Override
			/* Executed in the SwingWorker thread */
			protected Integer doInBackground() {
				return Integer.valueOf(generateFourGrids());
			}

			@Override
			/* Executed in the EDT, triggered when the SwingWorker has completed */
			protected void done() {
				try {
					get();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				} catch (ExecutionException e) {
					e.printStackTrace();
					return;
				} catch (CancellationException ce) {
					ce.printStackTrace();
				} finally {
					dispose();
				}
			}
		};
		initComponents();
		worker.execute();
	}

	private void initComponents() {

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		Container pane = getContentPane();
		GridLayout layout = new GridLayout(3, 1);
		pane.setLayout(layout);

		
		JLabel messageLbl1 = new JLabel(	
		"<html>"
		+ "<table border=\"0\">" + "<tr>"
		+ gtxt("Generating and sending four grids") + "</tr>"
		+ "</html>");
		JLabel messageLbl2 = new JLabel(	
				"<html>"
				+ "<table border=\"0\">" + "<tr>"
				+ gtxt("to the printer...") + "</tr>"
				+ "</html>");
		JButton cancelBtn = new JButton(gtxt("Cancel"));
		cancelBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		cancelBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clickedCancel();
			}
		});

		pane.add(messageLbl1);
		pane.add(messageLbl2);
		
		
		FlowLayout btnLayout = new FlowLayout(1);
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(btnLayout);
		btnPanel.add(cancelBtn);
		pane.add(btnPanel);

		pack();
		setLocationRelativeTo(parent);
	}

	private void clickedCancel() {
		synchronized (lock) {
			cancelledDialog = true;
		}
		/**
		 * CANCELLED
		 */
		worker.cancel(true);
	}

	private boolean dialogCancelled() {
		synchronized (lock) {
			return cancelledDialog;
		}
	}
	
	private int generateFourGrids() {
		if (dialogCancelled()) {
			return 1;
		}
		PrinterJob job = PrinterJob.getPrinterJob();
		if (dialogCancelled()) {
			return 1;
		}
		final int minRating = UserPreferences.getInstance().getInteger("minRating", Integer.valueOf(0)).intValue();
		final int maxRating = UserPreferences.getInstance().getInteger("maxRating", Integer.valueOf(Integer.MAX_VALUE)).intValue();
		SudokuGrid su1 = SudokuGeneratorFactory.getGenerator().generateGrid(minRating, maxRating);
		if (dialogCancelled()) {
			return 1;
		}
		SudokuGrid su2 = SudokuGeneratorFactory.getGenerator().generateGrid(minRating, maxRating);
		if (dialogCancelled()) {
			return 1;
		}
		SudokuGrid su3 = SudokuGeneratorFactory.getGenerator().generateGrid(minRating, maxRating);
		if (dialogCancelled()) {
			return 1;
		}
		SudokuGrid su4 = SudokuGeneratorFactory.getGenerator().generateGrid(minRating, maxRating);
		job.setPrintable(new SwingMultiGrid(su1, su2, su3, su4));
		if (dialogCancelled()) {
			return 1;
		}
		boolean doPrint = job.printDialog();
		if (doPrint) {
			try {
				if (dialogCancelled()) {
					return 1;
				}
				job.print();
				if (dialogCancelled()) {
					return 1;
				}
			} catch (PrinterException pEx) {
				/* The job did not successfully complete */
				pEx.printStackTrace();
			}
		}
		return 0;
	}

}
