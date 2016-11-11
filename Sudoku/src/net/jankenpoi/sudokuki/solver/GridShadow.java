package net.jankenpoi.sudokuki.solver;

public class GridShadow {

	public static final int MASK_FOR_CURRENT_VALUE = 0x0000000F; // 0000.0000-0000.0000:0000.0000-0000.1111

	public static final int MASK_POSSIBLE_VALUES = 0x00001FF0; // __0000.0000-0000.0000:0001.1111-1111.0000

	public static final int FLAG_POSSIBLE_VALUE_1 = 0x00000010; // _0000.0000-0000.0000:0000.0000-0001.0000
	public static final int FLAG_POSSIBLE_VALUE_2 = 0x00000020; // _0000.0000-0000.0000:0000.0000-0010.0000
	public static final int FLAG_POSSIBLE_VALUE_3 = 0x00000040; // _0000.0000-0000.0000:0000.0000-0100.0000
	public static final int FLAG_POSSIBLE_VALUE_4 = 0x00000080; // _0000.0000-0000.0000:0000.0000-1000.0000
	public static final int FLAG_POSSIBLE_VALUE_5 = 0x00000100; // _0000.0000-0000.0000:0000.0001-0000.0000
	public static final int FLAG_POSSIBLE_VALUE_6 = 0x00000200; // _0000.0000-0000.0000:0000.0010-0000.0000
	public static final int FLAG_POSSIBLE_VALUE_7 = 0x00000400; // _0000.0000-0000.0000:0000.0100-0000.0000
	public static final int FLAG_POSSIBLE_VALUE_8 = 0x00000800; // _0000.0000-0000.0000:0000.1000-0000.0000
	public static final int FLAG_POSSIBLE_VALUE_9 = 0x00001000; // _0000.0000-0000.0000:0001.0000-0000.0000

	public static final int MASK_SCREENED_VALUES = 0x1FF00000; // __0001.1111-1111.0000:0000.0000-0000.0000
	public static final int FLAG_SCREENED_VALUE_1 = 0x00100000; // _0000.0000-0001.0000:0000.0000-0000.0000
	public static final int FLAG_SCREENED_VALUE_2 = 0x00200000; // _0000.0000-0010.0000:0000.0000-0000.0000
	public static final int FLAG_SCREENED_VALUE_3 = 0x00400000; // _0000.0000-0100.0000:0000.0000-0000.0000
	public static final int FLAG_SCREENED_VALUE_4 = 0x00800000; // _0000.0000-1000.0000:0000.0000-0000.0000
	public static final int FLAG_SCREENED_VALUE_5 = 0x01000000; // _0000.0001-0000.0000:0000.0000-0000.0000
	public static final int FLAG_SCREENED_VALUE_6 = 0x02000000; // _0000.0010-0000.0000:0000.0000-0000.0000
	public static final int FLAG_SCREENED_VALUE_7 = 0x04000000; // _0000.0100-0000.0000:0000.0000-0000.0000
	public static final int FLAG_SCREENED_VALUE_8 = 0x08000000; // _0000.1000-0000.0000:0000.0000-0000.0000
	public static final int FLAG_SCREENED_VALUE_9 = 0x10000000; // _0001.0000-0000.0000:0000.0000-0000.0000

	/**
	 * Either FLAG_CELL_READ_ONLY or FLAG_CELL_FILLED might be unnecessary, or
	 * maybe even that only one of the three flags below is needed... ?
	 */
	public static final int FLAG_CELL_READ_ONLY = 0x00002000; // __0000.0000-0000.0000:0010.0000-0000.0000
	// public static final int FLAG_CELL_SCREENED = 0x00004000; //
	// ___0000.0000-0000.0000:0100.0000-0000.0000
	// public static final int FLAG_CELL_FILLED = 0x00008000; //
	// _____0000.0000-0000.0000:1000.0000-0000.0000

	/**
	 * 
	 * Flags for all cells used in the solving process. Each 'cell' in the
	 * GridShadow corresponds to a given cell in the grid (GridModel) that is
	 * being solved.
	 * 
	 * @see Important: see also 'offset'
	 */
	private final int[] cellFlags;
	/**
	 * Start index where this GridShadow is located inside the (possibly big)
	 * cellFlags array
	 */
	private final int offset;

	public GridShadow(int[] bigTable, int startIndex, boolean initFlags) {
		this.cellFlags = bigTable;
		this.offset = startIndex;
		if (initFlags) {
			initAllFlags();
		}
	}

	public int[] popFirstCellWithMinPossValues() {
		int li = -1;
		int co = -1;
		int minNB = 9;
		boolean deadEnd = false;
		boolean tableComplete = true;

		scanGrid: for (int l = 0; l < 9; l++) {
			for (int c = 0; c < 9; c++) {
				if (isCellFilled(l, c)) {
					continue;
				}
				/* cell not filled */
				tableComplete = false;
				int nb = getNumberOfPossibleValues(l, c);
				if (nb == 0) {
					deadEnd = true;
					break scanGrid;
				} else if (nb < minNB) {
					li = l;
					co = c;
					if (nb == 1) {
						break scanGrid;
					}
					minNB = nb;
				}
			}
		}

		if (tableComplete) {
			/* means TABLE COMPLETE */
			return new int[] { 10, 10 };
		}
		if (deadEnd) {
			/* means DEAD END */
			return new int[] { 11, 11 };
		}
		return new int[] { li, co };
	}

	public byte popFirstValueForCell(int li, int co) {
		if (isCellFilled(li, co))
			return 0;
		for (byte v = 1; v <= 9; v++) {
			if (isCellValuePossible(li, co, v) && !isCellValueScreened(li, co, v)) {
				return v;
			}
		}
		return 0;
	}

	public boolean setCellValueAt(int li, int co, byte value) {

		cellFlags[offset + 9 * li + co] &= ~MASK_FOR_CURRENT_VALUE;
		cellFlags[offset + 9 * li + co] |= FLAG_CELL_READ_ONLY | value;

		// ligne
		for (int c = 0; c < 9; c++) {
			if (c != co) {
				if (!isCellFilled(li, c) && isCellValuePossible(li, c, value)) {
					unsetCellValuePossible(li, c, value);
					setCellValueScreened(li, c, value);
					if (getNumberOfPossibleValues(li, c) == 0) {
						/* DEAD END */
						return true;
					}
				}
			}
		}

		// colonne
		for (int l = 0; l < 9; l++) {
			if (l != li) {
				if (!isCellFilled(l, co) && isCellValuePossible(l, co, value)) {
					unsetCellValuePossible(l, co, value);
					setCellValueScreened(l, co, value);
					if (getNumberOfPossibleValues(l, co) == 0) {
						/* DEAD END */
						return true;
					}
				}
			}
		}
		
		int X = 3 * (co / 3); // column start of the square
		int Y = 3 * (li / 3); // line start of the square
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				int c = X + x;
				int l = Y + y;
				if (!isCellFilled(l, c) && isCellValuePossible(l, c, value)) {
					unsetCellValuePossible(l, c, value);
					setCellValueScreened(l, c, value);
					if (getNumberOfPossibleValues(l, c) == 0) {
						/* DEAD END */
						return true;
					}
				}
			}
		}
		
		return false;
	}

	public void setCellReadOnly(int li, int co) {
		cellFlags[offset + 9 * li + co] |= FLAG_CELL_READ_ONLY;
	}

	public void setCellValueScreened(int li, int co, byte value) {
		cellFlags[offset + 9 * li + co] |= getScreenedValuesFlag(value);
	}
	
	private boolean isCellValueScreened(int li, int co, byte value) {
		return (cellFlags[offset + 9 * li + co] & getScreenedValuesFlag(value)) != 0;
	}

	
	boolean isCellFilled(int li, int co) {
		/**
		 * Unless proved wrong, FLAG_CELL_FILLED can be replaced by
		 * FLAG_CELL_READ_ONLY
		 */
		return (cellFlags[offset + 9 * li + co] & FLAG_CELL_READ_ONLY) != 0;
	}

	/**
	 * ******************* PACKAGE methods - below *******************
	 */

	byte getValueAt(int li, int co) {
		byte value = (byte) (cellFlags[offset + 9 * li + co] & MASK_FOR_CURRENT_VALUE);
		return value;
	}

	int getNumberOfPossibleValues(int li, int co) {
		if (isCellFilled(li, co)) {
			return 0;
		}
		int number = 0;
		for (byte v = 1; v <= 9; v++) {
			if (isCellValuePossible(li, co, v) && !isCellValueScreened(li, co, v)) {
				number++;
			}
		}
		return number;
	}

	boolean isCellValuePossible(int li, int co, byte value) {
		return (cellFlags[offset + 9 * li + co] & getPossibleValuesFlag(value)) != 0;
	}

	void unsetCellValuePossible(int li, int co, byte value) {
		if (isCellValuePossible(li, co, value)) {
		}
		cellFlags[offset + 9 * li + co] &= ~getPossibleValuesFlag(value);
	}

	/**
	 * ******************* PRIVATE methods - below *******************
	 */

	private void initAllFlags() {
		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				if (!isCellFilled(li, co)) {
					cellFlags[offset + 9 * li + co] = MASK_POSSIBLE_VALUES;
				}
			}
		}

		// FIXME: TODO: ..................
		// parcourir tous les carres
		// - pour chaque carre, noter les valeurs possibles
		// -- pour chaque cellule, cribler les valeurs possibles
		//
		for (int X = 0; X < 9; X += 3) { // left pos of a square
			for (int Y = 0; Y < 9; Y += 3) { // top pos of a square
				int currentValuesMask = 0;
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						int co = X + x;
						int li = Y + y;
						if (isCellFilled(li, co)) {
							byte value = getValueAt(li, co);
							currentValuesMask |= (MASK_SCREENED_VALUES & ~getScreenedValuesFlag(value)) | getPossibleValuesFlag(value);
						}
					}
				}
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						int co = X + x;
						int li = Y + y;
						if (!isCellFilled(li, co)) {
							cellFlags[offset + 9 * li + co] &= ~currentValuesMask;
						}
					}
				}
			}
		}

		// parcourir toutes les lignes
		// - pour chaque ligne, noter les valeurs possibles
		// -- pour chaque cellule, cribler les valeurs possibles
		//
		for (int li = 0; li < 9; li++) { // each line
			int currentValuesMask = 0;
			for (int co = 0; co < 9; co++) {
				if (isCellFilled(li, co)) {
					byte value = getValueAt(li, co);
					currentValuesMask |= (MASK_SCREENED_VALUES & ~getScreenedValuesFlag(value)) | getPossibleValuesFlag(value);
				}
			}
			for (int co = 0; co < 9; co++) {
				if (!isCellFilled(li, co)) {
					cellFlags[offset + 9 * li + co] &= ~currentValuesMask;
				}
			}
		}

		// parcourir toutes les colonnes
		// - pour chaque colonne, noter les valeurs possibles
		// -- pour chaque cellule, cribler les valeurs possibles
		for (int co = 0; co < 9; co++) { // each column
			int currentValuesMask = 0;
			for (int li = 0; li < 9; li++) {
				if (isCellFilled(li, co)) {
					byte value = getValueAt(li, co);
					currentValuesMask |= (MASK_SCREENED_VALUES & ~getScreenedValuesFlag(value)) | getPossibleValuesFlag(value);
				}
			}
			for (int li = 0; li < 9; li++) {
				if (!isCellFilled(li, co)) {
					cellFlags[offset + 9 * li + co] &= ~currentValuesMask;
				}
			}
		}

		{

			
			
			// TODO: use MASK_NUMB_POSS_VALUES and set the max number of
			// possible values for each cell shadow
			for (int li = 0; li < 9; li++) {
				for (int co = 0; co < 9; co++) {
					if (!isCellFilled(li, co)) {
						int possibleValuesNb = 0;
						for (byte n = 1; n <= 9; n++) {
							if (isCellValuePossible(li, co, n)) {
								possibleValuesNb++;
							}
						}
						cellFlags[offset + 9 * li + co] |= possibleValuesNb;
					}
				}
			}
		}

	}

	private int getPossibleValuesFlag(byte value) {
		switch (value) {
		case 1:
			return FLAG_POSSIBLE_VALUE_1;
		case 2:
			return FLAG_POSSIBLE_VALUE_2;
		case 3:
			return FLAG_POSSIBLE_VALUE_3;
		case 4:
			return FLAG_POSSIBLE_VALUE_4;
		case 5:
			return FLAG_POSSIBLE_VALUE_5;
		case 6:
			return FLAG_POSSIBLE_VALUE_6;
		case 7:
			return FLAG_POSSIBLE_VALUE_7;
		case 8:
			return FLAG_POSSIBLE_VALUE_8;
		case 9:
			return FLAG_POSSIBLE_VALUE_9;
		default:
			throw new IllegalArgumentException("getPossibleValuesFlag() value "
					+ value + " is illegal");
		}
	}

	private int getScreenedValuesFlag(byte value) {
		switch (value) {
		case 1:
			return FLAG_SCREENED_VALUE_1;
		case 2:
			return FLAG_SCREENED_VALUE_2;
		case 3:
			return FLAG_SCREENED_VALUE_3;
		case 4:
			return FLAG_SCREENED_VALUE_4;
		case 5:
			return FLAG_SCREENED_VALUE_5;
		case 6:
			return FLAG_SCREENED_VALUE_6;
		case 7:
			return FLAG_SCREENED_VALUE_7;
		case 8:
			return FLAG_SCREENED_VALUE_8;
		case 9:
			return FLAG_SCREENED_VALUE_9;
		default:
			throw new IllegalArgumentException("getScreenedValuesFlag() value "
					+ value + " is illegal");
		}
	}

	public void debugDump() {
//		for (int li = 0; li < 9; li++) {
//			for (int co = 0; co < 9; co++) {
//				if (!isCellFilled(li, co)) {
//					System.out.print("-");
//				} else {
//					System.out.print(getValueAt(li, co));
//				}
//			}
//			System.out.println();
//		}
//		System.out.println();
	}

	
}
