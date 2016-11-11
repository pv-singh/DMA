package net.jankenpoi.sudokuki.solver;

import net.jankenpoi.sudokuki.model.GridModel;

public class GridSolution {

	private GridModel gridModel;
	
	private boolean isSolved;

	public GridSolution(boolean b, GridModel gridModel) {
		this.gridModel = gridModel;
		this.isSolved = b;
	}

	public boolean isSolved() {
		return isSolved;
	}

	public GridModel getSolutionGrid() {
		return gridModel;
	}
	
}
