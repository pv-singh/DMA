package net.jankenpoi.sudokuki.generator;

import net.jankenpoi.sudokuki.SudokuGrid;
import net.sourceforge.plantuml.sudoku.DLXEngine;

public abstract class SudokuGenerator {

	protected static void printGrid(int[] tab) {
		for (int i = 0; i < tab.length; i++) {
			if (i % 3 == 0) {
				if (DLXEngine.DBG) {
					System.out.print(" ");
				}
			}
			if (i % 9 == 0) {
				if (DLXEngine.DBG) {
					System.out.println();
				}
			}
			if (i % 27 == 0) {
				if (DLXEngine.DBG) {
					System.out.println();
				}
			}
			if (DLXEngine.DBG) {
				System.out.print("" + (tab[i] == 0 ? "-" : Integer.valueOf(tab[i])));
			}
		}
		if (DLXEngine.DBG) {
			System.out.println();
		}
	}
	
	public abstract SudokuGrid generateGrid(final int minRating, final int maxRating);

}
