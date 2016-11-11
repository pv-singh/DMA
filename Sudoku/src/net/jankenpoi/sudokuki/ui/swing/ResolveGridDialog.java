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
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.jankenpoi.sudokuki.model.GridModel;
import net.jankenpoi.sudokuki.solver.DLXGridSolver;
import net.jankenpoi.sudokuki.solver.GridSolution;
import net.jankenpoi.sudokuki.solver.GridSolver;
import net.jankenpoi.sudokuki.view.GridView;
/**
 * CheckUpdateDialog.java
 * 
 * @author svedrenne
 */
@SuppressWarnings("serial")
public class ResolveGridDialog extends JDialog {

	private final JFrame parent;

	private int status = -1;

	private final GridView view;

	private final SwingWorker<Integer, Void> worker;

	private final GridSolver solver;
	private final GridModel gridToSolve;

	public ResolveGridDialog(JFrame parent, final GridView view) {
		super(parent, true);
		this.parent = parent;
		this.view = view;
		setResizable(false);

		short[] flagsTable = new short[81];
		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				if (view.isCellReadOnly(li, co)) {
					flagsTable[9 * li + co] = view.getValueAt(li, co);
				}
			}
		}
		gridToSolve = new GridModel(flagsTable, 0);
		solver = new DLXGridSolver(gridToSolve);
		
		worker = new SwingWorker<Integer, Void>() {

			@Override
			/* Executed in the SwingWorker thread */
			protected Integer doInBackground() {
				return resolveGrid();
			}

			@Override
			/* Executed in the EDT, triggered when the SwingWorker has completed */
			protected void done() {
				try {
					status = get().intValue();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				} catch (ExecutionException e) {
					e.printStackTrace();
					return;
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
		+ gtxt("Grid resolution in progress...") + "</tr>"
		+ "</html>");
		
		JLabel messageLbl3 = new JLabel("");
		JButton cancelBtn = new JButton(gtxt("Cancel"));
		cancelBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		cancelBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clickedCancel();
			}
		});

		pane.add(messageLbl1);
		pane.add(messageLbl3);

        FlowLayout btnLayout = new FlowLayout(1);
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(btnLayout);
        btnPanel.add(cancelBtn);
        pane.add(btnPanel);
        
		pack();
		setLocationRelativeTo(parent);
	}

	private void clickedCancel() {
		/**
		 * CANCELLED
		 */
		solver.cancel();
	}

	/**
	 * 
	 * @return <b>0</b> if the resolution was successful<br/>
	 *         <b>1</b> if the solving process was canceled by the user before
	 *         completion<br/>
	 *         <b>2</b> if the process failed to resolve the grid
	 */
	public int getResult() {
		return status;
	}

	private Integer resolveGrid() {

		GridSolution solution = solver.resolve();
		if (solution == null) {
			/**
			 * RESOLUTION PROCESS CANCELLED BEFORE COMPLETION
			 */
			return Integer.valueOf(1);
		}
		if (solution.isSolved()) {
			GridModel solGrid = solution.getSolutionGrid();
			for (int li = 0; li < 9; li++) {
				for (int co = 0; co < 9; co++) {
					byte value = solGrid.getValueAt(li, co);
					view.getController().notifyGridValueChanged(li, co, value, true);
				}
			}
			/**
			 * RESOLUTION SUCCESSFULL
			 */
			return Integer.valueOf(0);
		} else {
			/**
			 * RESOLUTION PROCESS WAS UNABLE TO SOLVE THIS GRID
			 */
			return Integer.valueOf(2);
		}
	}
}
