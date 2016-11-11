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
package net.jankenpoi.sudokuki.model;

import java.util.ArrayList;
import java.util.List;

import net.jankenpoi.sudokuki.SudokuGrid;
import net.jankenpoi.sudokuki.generator.SudokuGeneratorFactory;
import net.jankenpoi.sudokuki.preferences.UserPreferences;
import net.jankenpoi.sudokuki.view.GridListener;

/**
 * (MVC) model for a sudoku grid
 * 
 * @author Sylvain Vedrenne
 * 
 */
public class GridModel implements Cloneable {

	public static final int MASK_CELL_VALUES = 0x000F; // ___0000.0000-0000.1111
	public static final int MASK_CELL_MEMOS = 0x1FF0; // ____0001.1111-1111.0000

	public static final int FLAG_CELL_MEMO_1 = 0x0010; // ___0000.0000-0001.0000
	public static final int FLAG_CELL_MEMO_2 = 0x0020; // ___0000.0000-0010.0000
	public static final int FLAG_CELL_MEMO_3 = 0x0040; // ___0000.0000-0100.0000
	public static final int FLAG_CELL_MEMO_4 = 0x0080; // ___0000.0000-1000.0000
	public static final int FLAG_CELL_MEMO_5 = 0x0100; // ___0000.0001-0000.0000
	public static final int FLAG_CELL_MEMO_6 = 0x0200; // ___0000.0010-0000.0000
	public static final int FLAG_CELL_MEMO_7 = 0x0400; // ___0000.0100-0000.0000
	public static final int FLAG_CELL_MEMO_8 = 0x0800; // ___0000.1000-0000.0000
	public static final int FLAG_CELL_MEMO_9 = 0x1000; // ___0001.0000-0000.0000
	public static final int FLAG_CELL_READ_ONLY = 0x2000; // 0010.0000-0000.0000
	
	public static final int FLAG_GRID_COMPLETE = 0x4000; // _0100.0000-0000.0000

	private List<GridListener> listeners = new ArrayList<GridListener>();
	
	private boolean isCustomGridModeON = false;

	/**
	 * 
	 * Values and flags for all cells
	 */
	private short[] cellInfos = new short[81];

	public GridModel(short[] flagsTable, int startIdx) {
		copyFlagsToGrid(flagsTable, startIdx);
	}
	
	private void copyFlagsToGrid(short [] flagsTable, int startIdx) {
		for (int i = 0; i < cellInfos.length; i++) {
			short value = (short) (flagsTable[startIdx + i] & MASK_CELL_VALUES);
			cellInfos[i] = value;
			if (1 <= value && value <= 9) {
				cellInfos[i] |= FLAG_CELL_READ_ONLY;
			}
		}
	}
	
	public GridModel() {
		newGrid();
	}

	public void resetGridModelFromShorts(short[] externalCellInfos) {
		cellInfos = new short[81];
		for (int i=0; i<81; i++) {
			cellInfos[i] = externalCellInfos[i];
		}
	}
	
	private void newGrid() {
		final int minRating = UserPreferences.getInstance().getInteger("minRating", Integer.valueOf(0)).intValue();
		final int maxRating = UserPreferences.getInstance().getInteger("maxRating", Integer.valueOf(Integer.MAX_VALUE)).intValue();
		SudokuGrid grid = SudokuGeneratorFactory.getGenerator().generateGrid(minRating, maxRating);

		for (int i = 0; i < cellInfos.length; i++) {
			short value = (short) grid.getValueAt(i);
			cellInfos[i] = value;
			if (1 <= value && value <= 9) {
				cellInfos[i] |= FLAG_CELL_READ_ONLY;
			}
		}
	}

	/**
	 * FIXME: For the moment, this constructor is for testing use only....
	 * 
	 * @param strValues
	 */
	public GridModel(String strValues) {
		if (strValues == null) {
			return;
		}
//		System.out
//				.println("GridModel.GridModel() length:" + strValues.length());
//		System.out.println("GridModel.GridModel() strValues:" + strValues);
		for (int i = 0; i < strValues.length(); i++) {
			short value = Short.valueOf(strValues.substring(i, i + 1)).shortValue();
//			System.out.print(value);
			cellInfos[i] = value;
			if (1 <= value && value <= 9) {
				cellInfos[i] |= FLAG_CELL_READ_ONLY;
			}
		}
//		System.out.println();
	}

	public void addGridListener(GridListener view) {
		listeners.add(view);
	}

	short getCellInfosAt(int li, int co) {
		return cellInfos[9 * li + co];
	}

	public void setMemosForAllCells() {
		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				if (!isCellFilled(li, co)) {
					cellInfos[9 * li + co] = MASK_CELL_MEMOS;
				}
			}
		}

		// parcourir tous les carres
		// - pour chaque carre, cribler les memos
		// -- pour chaque cellule, cribler les memos
		//
		for (int X = 0; X < 9; X += 3) { // left pos of a square
			for (int Y = 0; Y < 9; Y += 3) { // top pos of a square
				short currentValuesMask = 0;
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						int co = X + x;
						int li = Y + y;
						if (isCellFilled(li, co)) {
							byte value = getValueAt(li, co);
							currentValuesMask |= getMemoFlag(value);
						}
					}
				}
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						int co = X + x;
						int li = Y + y;
						if (!isCellFilled(li, co)) {
							cellInfos[9 * li + co] &= ~currentValuesMask;
						}
					}
				}
			}
		}

		// parcourir toutes les lignes
		// - pour chaque ligne, cribler les memos
		// -- pour chaque cellule, cribler les memos
		//
		for (int li = 0; li < 9; li++) { // each line
			short currentValuesMask = 0;
			for (int co = 0; co < 9; co++) {
				if (isCellFilled(li, co)) {
					byte value = getValueAt(li, co);
					currentValuesMask |= getMemoFlag(value);
				}
			}
			for (int co = 0; co < 9; co++) {
				if (!isCellFilled(li, co)) {
					cellInfos[9 * li + co] &= ~currentValuesMask;
				}
			}
		}

		// parcourir toutes les colonnes
		// - pour chaque colonne, cribler les memos
		// -- pour chaque cellule, cribler les memos
		for (int co = 0; co < 9; co++) { // each column
			short currentValuesMask = 0;
			for (int li = 0; li < 9; li++) {
				if (isCellFilled(li, co)) {
					byte value = getValueAt(li, co);
					currentValuesMask |= getMemoFlag(value);
				}
			}
			for (int li = 0; li < 9; li++) {
				if (!isCellFilled(li, co)) {
					cellInfos[9 * li + co] &= ~currentValuesMask;
				}
			}
		}
		fireGridChanged(new GridChangedEvent(this, 0, 0, (short)0));
	}

	public void setMemosForThisCell(int cellLi, int cellCo) {
		if (isCellFilled(cellLi, cellCo))
			return;
		cellInfos[9 * cellLi + cellCo] = MASK_CELL_MEMOS;

		
		// parcourir le carre courant
		// - pour chaque carre, cribler les memos
		// -- pour chaque cellule, cribler les memos
		//
		{ // left pos of a square

			int X = 3 * (cellCo / 3);
			int Y = 3 * (cellLi / 3);
			short currentValuesMask = 0;
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					int co = X + x;
					int li = Y + y;
					if (isCellFilled(li, co)) {
						byte value = getValueAt(li, co);
						currentValuesMask |= getMemoFlag(value);
					}
				}
			}
			cellInfos[9 * cellLi + cellCo] &= ~currentValuesMask;
		}

		// parcourir la ligne de cette cellule
		// - pour chaque ligne, cribler les memos
		// -- pour chaque cellule, cribler les memos
		//
		{
			short currentValuesMask = 0;
			for (int co = 0; co < 9; co++) {
				if (isCellFilled(cellLi, co)) {
					byte value = getValueAt(cellLi, co);
					currentValuesMask |= getMemoFlag(value);
				}
			}
			for (int co = 0; co < 9; co++) {
				if (!isCellFilled(cellLi, co)) {
					cellInfos[9 * cellLi + co] &= ~currentValuesMask;
				}
			}
		}

		// parcourir la colonne de cette cellule
		// - pour chaque colonne, cribler les memos
		// -- pour chaque cellule, cribler les memos
		{
			short currentValuesMask = 0;
			for (int li = 0; li < 9; li++) {
				if (isCellFilled(li, cellCo)) {
					byte value = getValueAt(li, cellCo);
					currentValuesMask |= getMemoFlag(value);
				}
			}
			for (int li = 0; li < 9; li++) {
				if (!isCellFilled(li, cellCo)) {
					cellInfos[9 * li + cellCo] &= ~currentValuesMask;
				}
			}
		}
		fireGridChanged(new GridChangedEvent(this, cellLi, cellCo, cellInfos[9
				* cellLi + cellCo]));
	}
	
	public void setCellMemos(int li, int co, byte[] values) {
		for (int i = 0; i < values.length; i++) {
			setCellMemo(li, co, values[i]);
		}
		fireGridChanged(new GridChangedEvent(this, li, co, cellInfos[9 * li
				+ co]));
	}

	private short getMemoFlag(byte value) {
		switch (value) {
		case 1:
			return FLAG_CELL_MEMO_1;
		case 2:
			return FLAG_CELL_MEMO_2;
		case 3:
			return FLAG_CELL_MEMO_3;
		case 4:
			return FLAG_CELL_MEMO_4;
		case 5:
			return FLAG_CELL_MEMO_5;
		case 6:
			return FLAG_CELL_MEMO_6;
		case 7:
			return FLAG_CELL_MEMO_7;
		case 8:
			return FLAG_CELL_MEMO_8;
		case 9:
			return FLAG_CELL_MEMO_9;
		default:
			throw new IllegalArgumentException("GridModel.getMemoFlag() value "
					+ value + " is illegal");
		}
	}

	public byte getNbOfPossibleValues(int li, int co) {
		short infos = cellInfos[9 * li + co];
		byte nb = 0;
		for (byte i = 1; i <= 9; i++) {
			nb += ((infos & getMemoFlag(i)) != 0) ? 1 : 0;
		}
		return nb;
	}

	private void setCellMemo(int li, int co, byte value) {
		cellInfos[9 * li + co] |= getMemoFlag(value);
	}

	public void clearCellMemos(int li, int co) {
		cellInfos[9 * li + co] &= ~MASK_CELL_MEMOS;
	}

	public boolean isCellValueSet(int li, int co, Byte value) {
		return (cellInfos[9 * li + co] & value.byteValue()) != 0;
	}

	public boolean isCellFilled(int li, int co) {
		return (cellInfos[9 * li + co] & MASK_CELL_VALUES) != 0;
	}

	/**
	 * For use for initial values in the grid, and when doing a custom grid, for
	 * instance...
	 * 
	 * @param li
	 * @param co
	 */
	public void setCellReadOnly(int li, int co) {
		cellInfos[9 * li + co] |= FLAG_CELL_READ_ONLY;
	}

	public void setCellValue(int li, int co, int value, boolean silent) {
		cellInfos[9 * li + co] &= ~MASK_CELL_VALUES;
		cellInfos[9 * li + co] |= value;
		clearCellMemos(li, co);
		
		if (silent == false) {
			fireGridChanged(new GridChangedEvent(this, li, co, cellInfos[9 * li
				+ co]));
		}
		
		if (!silent && isGridFull()) {
			GridValidity validity = checkGridValidity();
			if (validity.isGridValid()) {
				setGridComplete();
			}
			fireGridChanged(new GridChangedEvent(this, 0, 0, (short)0));
		}
	}

	public void fireGridChanged(GridChangedEvent event) {

		for (GridListener listener : listeners) {
			listener.gridChanged(event);
		}
	}

	public boolean isCellMemoSet(int li, int co, byte value) {
		return (cellInfos[9 * li + co] & getMemoFlag(value)) != 0;
	}

	public boolean isCellReadOnly(int li, int co) {
		return !isCustomGridModeON && (cellInfos[9 * li + co] & FLAG_CELL_READ_ONLY) != 0;
	}

	public boolean isGridFull() {
		for (int li=0; li<9; li++) {
			for (int co=0; co<9; co++) {
				if (!isCellFilled(li, co)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isGridComplete() {
		return (cellInfos[0] & FLAG_GRID_COMPLETE) != 0;
	}

	public void setGridResolved() {
		cellInfos[0] |= FLAG_GRID_COMPLETE;
		fireGridResolved();
	}
	
	private void fireGridResolved() {
		for (GridListener listener : listeners) {
			listener.gridResolved();
		}
	}
	
	public void setGridComplete() {
		cellInfos[0] |= FLAG_GRID_COMPLETE;
		fireGridComplete();
	}

	private void fireGridComplete() {
		for (GridListener listener : listeners) {
			listener.gridComplete();
		}
	}
	
	public byte getValueAt(int li, int co) {
		byte value = (byte) (cellInfos[9 * li + co] & MASK_CELL_VALUES);
		return value;
	}

	public int[] cloneCellInfosAsInts() {
		int[] ints = new int[cellInfos.length];
		for (int i=0; i<cellInfos.length; i++) {
			ints[i] = cellInfos[i];
		}
		return ints;
	}

	public void requestNewGrid() {
		newGrid();
	}

	public void clearAllUserMoves() {
		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				if (!isCellReadOnly(li, co)) {
					cellInfos[9 * li + co] &= ~MASK_CELL_VALUES;
				}
			}
		}
		fireGridChanged(new GridChangedEvent(this, 0, 0, (short)0));
	}

	public void clearAllUserMemos() {
		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				clearCellMemos(li, co);
			}
		}
		fireGridChanged(new GridChangedEvent(this, 0, 0, (short)0));
	}
	
	public void enterCustomGridMode() {
		isCustomGridModeON = true;
		fireGridChanged(new GridChangedEvent(this, 0, 0, (short)0));
	}
	
	public GridValidity getGridValidity() {
		if (!isCustomGridModeON && !isGridFull()) {
			return GridValidity.VALID;
		}
		return checkGridValidity();
	}
	
	public void exitCustomGridMode() {
		isCustomGridModeON = false;
		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				if (isCellFilled(li, co)) {
					setCellReadOnly(li, co);
				} else {
					cellInfos[9 * li + co] &= ~FLAG_CELL_READ_ONLY;
				}
			}
		}
		fireGridChanged(new GridChangedEvent(this, 0, 0, (short)0));
	}
	
	public boolean getCustomGridMode() {
		return isCustomGridModeON;
	}
	
	public static class GridValidity {
		public static final GridValidity VALID = new GridValidity(true, null, null, null, null);
		
		private boolean isValid = true;
		private Integer firstErrorLine = null;
		private Integer firstErrorColumn = null;
		private Integer firstErrorSquareX = null;
		private Integer firstErrorSquareY = null;
		
		private GridValidity(boolean isValid, Integer lineWithError, Integer columnWithError,
				Integer squareWithErrorX, Integer squareWithErrorY) {
			this.isValid = isValid;
			this.firstErrorLine = lineWithError;
			this.firstErrorColumn = columnWithError;
			this.firstErrorSquareX = squareWithErrorX;
			this.firstErrorSquareY = squareWithErrorY;
		}
		
		private static GridValidity valueOf(boolean isValid, Integer lineWithError, Integer columnWithError,
				Integer squareWithErrorX, Integer squareWithErrorY) {
			if (isValid) {
				return VALID;
			} else {
				return new GridValidity(isValid, lineWithError, columnWithError,
						squareWithErrorX, squareWithErrorY);
			}
			
		}
		
		public boolean isGridValid() {
			return isValid;
		}
				
		public Integer getFirstErrorLine() {
			return firstErrorLine;
		}
		
		public Integer getFirstErrorColumn() {
			return firstErrorColumn;
		}
		
		public Integer getFirstErrorSquareX() {
			return firstErrorSquareX;
		}
		
		public Integer getFirstErrorSquareY() {
			return firstErrorSquareY;
		}
		
	}

	private GridValidity checkGridValidity() {
		boolean isValid = true;
		Integer lineWithError = null;
		Integer columnWithError = null;
		Integer squareWithErrorX = null;
		Integer squareWithErrorY = null;
		
		// Check validity of all lines
		for (int li = 0; isValid && li < 9; li++) {
			byte[] numbers = new byte[10];
			for (int co = 0; co < 9; co++) {
				byte value = getValueAt(li, co);
				if (numbers[value] != 0) {
					isValid = false;
					lineWithError = Integer.valueOf(li);
					break;
				}
				numbers[value] = value;
			}
		}
		// Check validity of all columns
		for (int co = 0; isValid && co < 9; co++) {
			byte[] numbers = new byte[10];
			for (int li = 0; li < 9; li++) {
				byte value = getValueAt(li, co);
				if (numbers[value] != 0) {
					isValid = false;
					columnWithError = Integer.valueOf(co);
					break;
				}
				numbers[value] = value;
			}
		}
		// Check validity of all squares
		for (int X = 0; isValid && X < 9; X += 3) { // left pos of a square
			for (int Y = 0; isValid && Y < 9; Y += 3) { // top pos of a square
				int[] numbers = new int[10];
				for (int x = 0; isValid && x < 3; x++) {
					for (int y = 0; isValid && y < 3; y++) {
						int co = X + x;
						int li = Y + y;
						byte value = getValueAt(li, co);
						if (numbers[value] != 0) {
							isValid = false;
							squareWithErrorX = Integer.valueOf(X);
							squareWithErrorY = Integer.valueOf(Y);
							break;
						}
						numbers[value] = value;
					}
				}
			}
		}
		return GridValidity.valueOf(isValid, lineWithError, columnWithError,
				squareWithErrorX, squareWithErrorY);
	}
	
	public boolean areSomeMemosSet() {
		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				if ((cellInfos[9 * li + co] & MASK_CELL_MEMOS) != 0)
					return true;
			}
		}
		return false;
	}
	
	public boolean areSomeCellsFilled() {
		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				if (isCellReadOnly(li, co))
					continue;
				if ((cellInfos[9 * li + co] & MASK_CELL_VALUES) != 0)
					return true;
			}
		}
		return false;
	}
	
	public boolean areSomeCellsEmpty() {
		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				if (isCellFilled(li, co)) {
					continue;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	public int[] asIntArray() {
		int[] ints = new int[cellInfos.length];
		for (int i=0; i<cellInfos.length; i++) {
			ints[i] = cellInfos[i];
		}
		return ints;
	}
	
}
