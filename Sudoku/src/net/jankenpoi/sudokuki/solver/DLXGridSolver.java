package net.jankenpoi.sudokuki.solver;

import java.util.Random;

import net.jankenpoi.sudokuki.model.GridModel;
import net.sourceforge.plantuml.sudoku.DLXEngine;

public class DLXGridSolver implements GridSolver {

	private final GridModel originalGrid;

	public DLXGridSolver(GridModel originalGrid) {
		this.originalGrid = originalGrid;
	}
	
	public GridSolution resolve() {
		DLXEngine engine = new DLXEngine(new Random());
		String gridToSolve = "";
		for (int li=0; li<9; li++) {
			for (int co=0; co<9; co++) {
				int value = originalGrid.getValueAt(li, co);
				if (value == 0) {
					gridToSolve += ".";
				} else {
					gridToSolve += value;
				}
			}
		}
		String solution = engine.solve(gridToSolve).substring(0, 81);
		if (DLXEngine.DBG) {
			System.out.println("solution = "+solution);
		}
		return new GridSolution(true, new GridModel(solution));
	}

	public void cancel() {
		// TODO Auto-generated method stub
	}

}
