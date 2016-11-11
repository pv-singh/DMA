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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.jankenpoi.sudokuki.SudokuGrid;
import net.jankenpoi.sudokuki.preferences.UserPreferences;

public class SwingMultiGrid extends JPanel implements Printable {

	private static final long serialVersionUID = 1L;

	private static final int offX = 2;

	private static final int offY = 2;

	private static final int CELL_SIZE = 22;

	private int FONT_SIZE = 18;

	private SudokuGrid su1;
	private SudokuGrid su2;
	private SudokuGrid su3;
	private SudokuGrid su4;

	SwingMultiGrid(SudokuGrid su1, SudokuGrid su2, SudokuGrid su3, SudokuGrid su4) {
        setPreferredSize(new Dimension(columns[columns.length - 1].getEnd()
                                       - columns[0].getStart() + offX * 2 + 1,
                                       rows[rows.length - 1].getEnd() - rows[0].getStart() + offY * 2 + 1));
		this.su1 = su1;
		this.su2 = su2;
		this.su3 = su3;
		this.su4 = su4;
	}

	/**
	 * Returns the position where to draw a digit in the grid.
	 * 
	 * @param li
	 *            Line number (must be between 1 and 9) of a cell in the grid
	 * @param co
	 *            Bar number (must be between 1 and 9) of a cell in the grid
	 * @return A Point giving the position where to draw the digit for cell (li,
	 *         co)
	 */
    private Point getPosition(Graphics2D g2, int li, int co, String digit) {
        if (!(0 <= li && li < 9 && 0 <= co && co < 9)) {
            throw new IllegalArgumentException();
        }

        FontMetrics fm = getFontMetrics(g2.getFont());
        int h = fm.getHeight();
        int w = fm.stringWidth(digit);

        int x = columns[co].getStart() + (CELL_SIZE - w ) / 2;
        int y = rows[li].getStart() + (CELL_SIZE + h / 2) / 2;

        return new Point(x, y + 1);
    }

	private void paintGridNumbers(Graphics2D g2, SudokuGrid grid, int numbersMode) {
		g2.setColor(Color.BLACK);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// Font font = new Font("Serif", Font.BOLD, FONT_SIZE);
		Font font = new Font("Serif", Font.BOLD, FONT_SIZE - (numbersMode==1?4:0));
		g2.setFont(font);

		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
			    
                String digit = getValueAsStringAt(grid, li, co, numbersMode);
                Point pos = getPosition(g2, li, co, digit);
				g2.drawString(digit, pos.x, pos.y);
			}
		}
	}

    private static String[] digits = { "", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "", "\u4e00", "\u4E8C", "\u4e09", "\u56DB", "\u4E94", "\u516D", "\u4E03", "\u516B", "\u4E5D",
        "", "\u0661", "\u0662", "\u0663", "\u0664", "\u0665", "\u0666", "\u0667", "\u0668", "\u0669",
    };
    
    private String getValueAsStringAt(SudokuGrid grid, int li, int co, int numbersMode) {
        int value = grid.getValueAt(li, co);
        String result = digits[value + (10 * numbersMode)];
        return result; 
    }
	
	private void paintGridBoard(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		g2.fillRect(columns[0].getStart(), rows[0].getStart(),
				columns[columns.length-1].getEnd() - 1, rows[rows.length-1].getEnd() - 1);

		g2.setColor(new Color(0xEEEEEE));
		g2.fillRect(columns[3].getStart(), rows[0].getStart(),
				columns[5].getEnd() - columns[3].getStart(), rows[rows.length-1].getEnd());
		g2.fillRect(columns[0].getStart(), rows[3].getStart(),
				columns[columns.length-1].getEnd(), rows[5].getEnd() - rows[3].getStart());
		g2.setColor(Color.WHITE);
		g2.fillRect(columns[3].getStart(), rows[3].getStart(),
				columns[5].getEnd() - columns[3].getStart(), rows[5].getEnd()
						- rows[3].getStart());

		g2.setColor(Color.BLACK);
		for (int li = 0; li < 9; li++) {
			g2.drawLine(columns[0].getStart(), rows[li].getStart(),
					columns[columns.length-1].getEnd(), rows[li].getStart());
			g2.drawLine(columns[0].getStart(), rows[li].getEnd(),
					columns[columns.length-1].getEnd(), rows[li].getEnd());
		}
		for (int co = 0; co < 9; co++) {
			g2.drawLine(columns[co].getStart(), rows[0].getStart(),
					columns[co].getStart(), rows[rows.length-1].getEnd());
			g2.drawLine(columns[co].getEnd(), rows[0].getStart(),
					columns[co].getEnd(), rows[rows.length-1].getEnd());
		}
		g2.setColor(Color.BLACK);
		g2.drawRect(columns[0].getStart() - 1, rows[0].getStart() - 1,
				columns[columns.length-1].getEnd() - columns[0].getStart() + 2,
				rows[rows.length-1].getEnd() - rows[0].getStart() + 2);
	}

	private class Strip {
		private int start, end;

		Strip(int s, int e) {
			start = s;
			end = e;
		}

		int getStart() {
			return start;
		}

		int getEnd() {
			return end;
		}
	}

	public int print(Graphics graphics, PageFormat pf, int pageIndex)
			throws PrinterException {

		if (pageIndex != 0)
			return NO_SUCH_PAGE;

		Graphics2D g2 = (Graphics2D) graphics;
		/*
		 * User (0,0) is typically outside the imageable area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
		 */
		Font font = new Font("Serif", Font.PLAIN, 24);
		g2.setFont(font);
		FontMetrics metrics = g2.getFontMetrics();
		int fontHeight = metrics.getHeight();
		g2.translate(pf.getImageableX(), pf.getImageableY());
		g2.translate(0, fontHeight);
		g2.drawString("Sudokuki - essential sudoku game", 0, 0);
		g2.translate(0, fontHeight);
		font = new Font("Serif", Font.PLAIN, 20);
		g2.setFont(font);
		fontHeight = g2.getFontMetrics().getHeight();
		g2.drawString("http://sudokuki.sourceforge.net/", 0, fontHeight);
		g2.translate(240, -40);
		ImageIcon icon = Images.ICON_APPLICATION_LOGO_SMALL;
		g2.drawImage(icon.getImage(), 130, 0, this);
		/* Now we perform our rendering */
		g2.translate(-240, 140);

		int numbersMode = UserPreferences.getInstance().getInteger("numbersMode", Integer.valueOf(0)).intValue();
		paintGridBoard(g2);
		paintGridNumbers(g2, su1, numbersMode);

		g2.translate(230, 0);
		paintGridBoard(g2);
		paintGridNumbers(g2, su2, numbersMode);

		g2.translate(-230, 250);
		paintGridBoard(g2);
		paintGridNumbers(g2, su3, numbersMode);

		g2.translate(230, 0);
		paintGridBoard(g2);
		paintGridNumbers(g2, su4, numbersMode);

		return PAGE_EXISTS;
	}

	private Strip[] rows = new Strip[] {
			new Strip(offY, offY + CELL_SIZE),
			new Strip(offY + CELL_SIZE, offY + 2 * CELL_SIZE),
			new Strip(offY + 2 * CELL_SIZE, offY + 3 * CELL_SIZE),

			new Strip(offY + 3 * CELL_SIZE + 1, offY + 4 * CELL_SIZE + 1),
			new Strip(offY + 4 * CELL_SIZE + 1, offY + 5 * CELL_SIZE + 1),
			new Strip(offY + 5 * CELL_SIZE + 1, offY + 6 * CELL_SIZE + 1),

			new Strip(offY + 6 * CELL_SIZE + 2, offY + 7 * CELL_SIZE + 2),
			new Strip(offY + 7 * CELL_SIZE + 2, offY + 8 * CELL_SIZE + 2),
			new Strip(offY + 8 * CELL_SIZE + 2, offY + 9 * CELL_SIZE + 2), };

	private Strip[] columns = new Strip[] {
			new Strip(offX, offX + CELL_SIZE),
			new Strip(offX + CELL_SIZE, offX + 2 * CELL_SIZE),
			new Strip(offX + 2 * CELL_SIZE, offX + 3 * CELL_SIZE),

			new Strip(offX + 3 * CELL_SIZE + 1, offX + 4 * CELL_SIZE + 1),
			new Strip(offX + 4 * CELL_SIZE + 1, offX + 5 * CELL_SIZE + 1),
			new Strip(offX + 5 * CELL_SIZE + 1, offX + 6 * CELL_SIZE + 1),

			new Strip(offX + 6 * CELL_SIZE + 2, offX + 7 * CELL_SIZE + 2),
			new Strip(offX + 7 * CELL_SIZE + 2, offX + 8 * CELL_SIZE + 2),
			new Strip(offX + 8 * CELL_SIZE + 2, offX + 9 * CELL_SIZE + 2),

	};

}
