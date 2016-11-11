package net.sourceforge.plantuml.sudoku;

import java.util.Random;

import net.jankenpoi.sudokuki.SudokuGrid;
import net.jankenpoi.sudokuki.generator.SudokuGenerator;

public class SuexgGenerator2 extends SudokuGenerator {

	@Override
	public SudokuGrid generateGrid(int minRating, int maxRating) {
		DLXEngine dlxEngine = new DLXEngine(new Random());
		String sudoku = dlxEngine.generate(minRating, maxRating);
		if (DLXEngine.DBG) {
			System.out.println("sudoku : \n"+sudoku+" length = "+sudoku.length());
		}
		final int[] intGrid = new int[81];
		int idx = 0;
		for (int i=0; i<81; i++) {
			int val = -1;
			while (val == -1) {
				char ch = sudoku.charAt(idx++);
				if ('1' <= ch && ch <= '9') {
					val = ch - '0';
				} else if ('\n' != ch) {
					val = 0; 
				}
			}
			intGrid[i] = val;
		}
		SudokuGrid grid = new SudokuGrid(intGrid);
		return grid;
	}

	private static final SudokuGenerator INSTANCE = new SuexgGenerator2();
	
	public final static SudokuGenerator getInstance() {
		return INSTANCE;
	}

}
