package net.jankenpoi.sudokuki.solver;

import net.jankenpoi.sudokuki.model.GridModel;

public class BruteForceGridSolver implements GridSolver {

	private static final int MAX_ITER_NB = 20000;
	/*
	 * Maybe spurious field - is it useful ??
	 */
	private final GridModel originalGrid;
	/**
	 * Equivalent of 81 grids
	 */
	private final int[] cellShadowMemory = new int[81 * GRID_LENGTH];

	private int currentIndex = 0;

	private Boolean cancelled = Boolean.FALSE;

	public BruteForceGridSolver(GridModel originalGrid) {
		this.originalGrid = originalGrid;

		int[] originalFlags = originalGrid.cloneCellInfosAsInts();
		System.arraycopy(originalFlags, 0, cellShadowMemory, 0,
				originalFlags.length);
	}

	private boolean cancelRequested() {
		synchronized (cancelled) {
			return cancelled.booleanValue();
		}
	}
	
	public void cancel() {
		synchronized (cancelled) {
			cancelled = Boolean.TRUE;
		}
	}
	
	public GridSolution resolve() {
		GridShadow gs = new GridShadow(cellShadowMemory, currentIndex, true); // 2.1
		gs.debugDump();
		boolean totalDeadEndNoSolution = false;
		
		for (int iter = 1; totalDeadEndNoSolution == false && iter < MAX_ITER_NB; iter++) {
			if (cancelRequested()) {
				return null;
			}

			int[] liCo = gs.popFirstCellWithMinPossValues(); // 3, 4
			int li = liCo[0];
			int co = liCo[1];

			if (li == 10 && co == 10) {
				/**
				 * TABLE COMPLETE
				 * 
				 * Return the solution
				 */
				short[] shorts = new short[GRID_LENGTH];
				for (int i=0; i<GRID_LENGTH; i++) {
					shorts[i] = (short) cellShadowMemory[currentIndex + i];
				}
				GridModel solModel = new GridModel(shorts,
						0);
				GridSolution solution = new GridSolution(true, solModel);
				return solution;
			}

			if (li == 11 && co == 11) {
				/**
				 * DEAD END
				 * 
				 * Go back one position and continue the solving process
				 */
				totalDeadEndNoSolution = backToPreviousPosition();
				if (totalDeadEndNoSolution) {
					break;
				}
				gs = new GridShadow(cellShadowMemory, currentIndex, false);
				gs.debugDump();
				continue;
			}

			byte value = gs.popFirstValueForCell(li, co); // 5

			copyCurrentFlagsToNextPosition(); // 6

			gs = new GridShadow(cellShadowMemory, currentIndex, false); // 7
			gs.debugDump();
			
			gs.setCellValueScreened(li, co, value); // 7

			forwardToNextPosition(); // 8
			gs = new GridShadow(cellShadowMemory, currentIndex, false); // 8
			gs.debugDump();

			boolean deadEnd = gs.setCellValueAt(li, co, value); // 8.1
			if (deadEnd) {
				totalDeadEndNoSolution = backToPreviousPosition(); // 9
				gs = new GridShadow(cellShadowMemory, currentIndex, false); // 9.1
				gs.debugDump();
			}

		}

		GridSolution solution = new GridSolution(false, originalGrid);
		return solution;
	}

	void copyCurrentFlagsToNextPosition() {
		if (currentIndex + GRID_LENGTH >= cellShadowMemory.length) {
			throw new IllegalStateException(
					"Already reached the end of the solver memory");
		}
		System.arraycopy(cellShadowMemory, currentIndex, cellShadowMemory,
				currentIndex + GRID_LENGTH, GRID_LENGTH);
	}

	void forwardToNextPosition() {
		currentIndex = currentIndex + GRID_LENGTH;
	}

	boolean backToPreviousPosition() {
		currentIndex = currentIndex - GRID_LENGTH;
		if (currentIndex < 0) {
			return true;
		}
		return false;
	}

	/*
	 * FOR TESTING ONLY
	 */
	int[] getCellShadowMemory() {
		return cellShadowMemory;
	}

	/*
	 * FOR TESTING ONLY
	 */
	int getCurrentIndex() {
		return currentIndex;
	}

}
