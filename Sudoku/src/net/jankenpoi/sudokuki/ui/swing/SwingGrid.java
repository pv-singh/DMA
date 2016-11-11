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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.PaintEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.jankenpoi.sudokuki.model.GridModel.GridValidity;
import net.jankenpoi.sudokuki.model.Position;
import net.jankenpoi.sudokuki.preferences.UserPreferences;
import net.jankenpoi.sudokuki.view.GridView;

public class SwingGrid extends JPanel implements Printable {

	private static final long serialVersionUID = 1L;

	private GridView view;

	private static final int offset = 2;

	private final int MIN_CELL_SIZE = 20;
	private int MAX_CELL_SIZE = 40;
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		MAX_CELL_SIZE = (Math.min(screenSize.width, screenSize.height)- 2*offset)/9;
	}
	private int CELL_SIZE = 32;

	private int FONT_SIZE = CELL_SIZE*5/6;

	private MouseListener innerMouseListener = new InnerMouseListener();

	private KeyListener innerKeyListener = new InnerKeyListener();

	private ComponentListener componentListener = new ComponentAdapter() {
		
		@Override
		public void componentResized(ComponentEvent e) {
        	Component component = ((Component)e.getSource());
            CELL_SIZE = (Math.min(component.getSize().width - 2*offset - 3, component.getSize().height - 2*offset - 3)) / 9;
            CELL_SIZE = Math.max(CELL_SIZE, MIN_CELL_SIZE);
            CELL_SIZE = Math.min(CELL_SIZE, MAX_CELL_SIZE);
        	FONT_SIZE = CELL_SIZE*5/6;

    		final int width = 9 * CELL_SIZE + 2 * offset + 3;
            component.setPreferredSize(new Dimension(width, width));
            
            component.repaint();
            parent.pack();
         
            /* 
             * Hack - read more details below
             */
            timer.stop();
            timer.start();
		}
	};

	/*
	 * Hack in order to avoid paint issues when running JVM 1.7 on MS-Windows 7
	 * (no such issue on GNU/Linux or MS-Windows XP). The hack is to ensure
	 * frame.pack() will be called 500 ms after the user finished resizing the
	 * window. The paint issue (JVM 1.7 + MS-Windows 7 only) was seen when
	 * resizing with the mouse and releasing it while the frame had a
	 * rectangular shape: the frame would remain as is (rectangular) instead of
	 * being resized to a square by ComponentListener.componentResized(). The
	 * extra part in the rectangle part outside the square was black.
	 */
    private final Timer timer = new Timer(500, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            CELL_SIZE = (Math.min(getSize().width - 2*offset, getSize().height - 2*offset)) / 9;
            CELL_SIZE = Math.max(CELL_SIZE, MIN_CELL_SIZE);
            CELL_SIZE = Math.min(CELL_SIZE, MAX_CELL_SIZE);
        	FONT_SIZE = CELL_SIZE*5/6;

    		final int width = 9 * CELL_SIZE + 2 * offset + 3;
            setPreferredSize(new Dimension(width, width));
            repaint();
            parent.pack();
        }
    });
    {
        timer.setRepeats(false);
    }

	/*
	 * Another hack, also for resize issues when stressing the resize process
	 * (start resizing, don't release the mouse, move the mouse very quickly in
	 * circles and suddenly release the mouse button). This sends lots of
	 * PaintEvents that are received in a random order, the last one ruling over
	 * the other ones... With this hack, frame.pack() will be called 500 ms
	 * after the PaintEvent-s flood.
	 */
    {
        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

            public void eventDispatched(AWTEvent event) {
            	
            	// remove this if AWTEvent.PAINT_EVENT_MASK
                if (! (event instanceof PaintEvent)) {
                    return;
                }
                SwingGrid.this.timer.stop();
                SwingGrid.this.timer.start();
            }
        }, AWTEvent.PAINT_EVENT_MASK);
    }

	/*
	 * Column number of the focus mark
	 */
	private int posX = 4;

	/*
	 * Line number of the focus mark
	 */
	private int posY = 4;
	
	private JFrame parent;

	SwingGrid(GridView view, JFrame parent) {
		this.parent = parent;
		this.view = view;

		addMouseListener(innerMouseListener);
		addKeyListener(innerKeyListener);
		
		addComponentListener(componentListener);

		final int width = 9 * CELL_SIZE + 2 * offset + 3;
		setPreferredSize(new Dimension(width, width));
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
		
		int x = startPos(co) + (CELL_SIZE - w ) / 2;
		int y = startPos(li) + (CELL_SIZE + h/2 ) / 2;
		
		return new Point(x, y + 1);
	}

	/**
	 * Returns the position where to draw a memo in the grid.
	 * 
	 * @param li
	 *            Line number (must be between 1 and 9) of a cell in the grid
	 * @param co
	 *            Bar number (must be between 1 and 9) of a cell in the grid
	 * @return A Point giving the position where to draw the digit for cell (li,
	 *         co)
	 */
	private Point getPositionForMemo(Graphics2D g2, int li, int co, int value) {
		if (!(0 <= li && li < 9 && 0 <= co && co < 9)) {
			throw new IllegalArgumentException();
		}
		
 		FontMetrics fm = getFontMetrics(g2.getFont());
 		int h = fm.getHeight();
		int w = fm.stringWidth("X");
 
		int x = startPos(co) + CELL_SIZE / 2 - w / 2;
		int y = startPos(li) + CELL_SIZE / 2 + h / 4;

 		int xx = (9 - value) % 3 - 1;
 		int yy = (9 - value) / 3 - 1;
 		x = x - xx * (CELL_SIZE/3) + 1;
		y = y - yy * (CELL_SIZE/3) + 1;
 
 		return new Point(x, y);
 	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		paintGridBoard(g2);
		paintFocusMark(g2);
		int numbersMode = UserPreferences.getInstance().getInteger("numbersMode", Integer.valueOf(0)).intValue();
		paintGridNumbers(g2, numbersMode);
		paintPlayerMemos(g2);
		paintPlayerNumbers(g2, numbersMode);
	}


	private void paintGridNumbers(Graphics2D g2, int numbersMode) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.BLACK);
		// Font font = new Font("Serif", Font.BOLD, FONT_SIZE);
		Font font = new Font("Serif", Font.BOLD, FONT_SIZE
				- (numbersMode==1 ? 4 : 0));
		g2.setFont(font);

		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				if (view.isCellReadOnly(li, co)) {
				    String digit = getValueAsStringAt(li, co, numbersMode);
					Point pos = getPosition(g2, li, co, digit);
					g2.drawString(digit, pos.x, pos.y);
				}
			}
		}
	}

	private static String[] digits = { "", "1", "2", "3", "4", "5", "6", "7", "8", "9",
	    "", "\u4e00", "\u4E8C", "\u4e09", "\u56DB", "\u4E94", "\u516D", "\u4E03", "\u516B", "\u4E5D",
        "", "\u0661", "\u0662", "\u0663", "\u0664", "\u0665", "\u0666", "\u0667", "\u0668", "\u0669",
	};
	
	private String getValueAsStringAt(int li, int co, int numbersMode) {
		int value = view.getValueAt(li, co);
		String result = digits[value + (10 * numbersMode)];
		return result; 
	}

	private void paintFocusMark(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2.setColor(Color.DARK_GRAY);
		g2.drawRect(startPos(posX), startPos(posY),
				CELL_SIZE,
				CELL_SIZE);
		g2.setColor(Color.GRAY);
		g2.drawRect(startPos(posX) + 1 , startPos(posY) + 1,
				CELL_SIZE - 2,
				CELL_SIZE - 2);
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawRect(startPos(posX) + 2 , startPos(posY) + 2,
				CELL_SIZE - 4,
				CELL_SIZE - 4);
	}
	
	private void paintPlayerNumbers(Graphics2D g2, int numbersMode) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		Font font = new Font("Serif", Font.PLAIN, FONT_SIZE- (numbersMode==1?4:0));
		g2.setFont(font);

		GridValidity validity = view.getGridValidity();
		Integer firstErrorLine = validity.getFirstErrorLine(); 
		Integer firstErrorColumn = validity.getFirstErrorColumn();
		Integer firstErrorSquareX = validity.getFirstErrorSquareX();
		Integer firstErrorSquareY = validity.getFirstErrorSquareY();
		
		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				if (!view.isCellReadOnly(li, co)) {
				    String digit = getValueAsStringAt(li, co, numbersMode);
				    Point pos = getPosition(g2, li, co, digit);
					
					if ((firstErrorLine != null && firstErrorLine.intValue() == li)
							|| (firstErrorColumn != null && firstErrorColumn.intValue() == co)
							|| ((firstErrorSquareX != null && firstErrorSquareX.intValue() <= co && co < firstErrorSquareX.intValue() + 3) &&
									((firstErrorSquareY != null && firstErrorSquareY.intValue() <= li && li < firstErrorSquareY.intValue() + 3)))) {
						g2.setColor(Color.RED);
					} else {
						if (view.isGrigComplete()) {
							g2.setColor(Color.DARK_GRAY);
						} else {
							g2.setColor(Color.BLUE);
						}
					}
					
					g2.drawString(digit, pos.x, pos.y);
				}
			}
		}
	}

	/**
	 * Paint the memos that help the user remember which values are possible in
	 * a given cell
	 * 
	 */
	private void paintPlayerMemos(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.BLUE);
		Font font = new Font("Serif", Font.PLAIN, 9 * FONT_SIZE / 22);
		g2.setFont(font);

		for (int li = 0; li < 9; li++) {
			for (int co = 0; co < 9; co++) {
				for (int k = 1; k <= 9; k++) {
					if (view.isCellMemoSet(li, co, (byte) k)) {
						Point pos = getPositionForMemo(g2, li, co, k);
						g2.drawString(String.valueOf(k), pos.x, pos.y);
					}
				}
			}
		}
	}

	private int startPos(int x) {
		return offset + x * CELL_SIZE + (x/3)%3;
	}
	private int endPos(int x) {
		return offset + (x+1) * CELL_SIZE + (x/3)%3;
	}
	
	private void paintGridBoard(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2.setColor(Color.WHITE);
		g2.fillRect(startPos(0), startPos(0),
				endPos(8) - 1, endPos(8) - 1);

		g2.setColor(new Color(0xEEEEEE));
		g2.fillRect(startPos(3), startPos(0),
				endPos(5) - startPos(3),
				endPos(8));
		g2.fillRect(startPos(0), startPos(3),
				endPos(8), endPos(5)
						- startPos(3));
		g2.setColor(Color.WHITE);
		g2.fillRect(startPos(3), startPos(3),
				endPos(5) - startPos(3), endPos(5)
						- startPos(3));

		g2.setColor(Color.BLACK);
		for (int li = 0; li < 9; li++) {
			g2.drawLine(startPos(0), startPos(li),
					endPos(8), startPos(li));
			g2.drawLine(startPos(0), endPos(li),
					endPos(8), endPos(li));
		}
		for (int co = 0; co < 9; co++) {
			g2.drawLine(startPos(co), startPos(0),
					startPos(co), endPos(8));
			g2.drawLine(endPos(co), startPos(0),
					endPos(co), endPos(8));
		}
		g2.setColor(Color.BLACK);
		g2.drawRect(startPos(0) - 1, startPos(0) - 1,
				endPos(8) - startPos(0) + 2,
				endPos(8) - startPos(0) + 2);
	}

	/**
	 * 
	 * @param inPos
	 *            The position (pixels, pixels) in the grid.
	 * @return A Point corresponding to the cell where the input position falls
	 *         in the grid. The x and y of this Point correspond to the column
	 *         and the line of the cell. x or y is 0 in case the given position
	 *         in pixels falls out of the grid.
	 */
	private Position getLiCoForPos(Point inPos) {
		int li = -1;
		int co = -1;
		for (int l = 0; l < 9; l++) {
			if (startPos(l) + 2 <= inPos.y && inPos.y < endPos(l) + 2) {
				li = l;
				break;
			}
		}
		for (int c = 0; c < 9; c++) {
			if (startPos(c) <= inPos.x
					&& inPos.x < endPos(c)) {
				co = c;
				break;
			}
		}

		if (li == -1) {
			/*
			 * line is out of the grid
			 */
		}
		if (co == -1) {
			/*
			 * column is out of the grid
			 */
		}

		return new Position(li, co);
	}

	private Point getTopLeftPoint(int li, int co) {
		int x = startPos(co);
		int y = startPos(li);
		return new Point(x, y);
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
		g2.translate(-160, 140);

		paintGridBoard(g2);
		int numbersMode = UserPreferences.getInstance().getInteger("numbersMode", Integer.valueOf(0)).intValue();
		paintGridNumbers(g2, numbersMode);
		paintPlayerNumbers(g2, numbersMode);

		return PAGE_EXISTS;
	}

	private class InnerMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent evt) {
			switch (evt.getButton()) {
			case MouseEvent.BUTTON1:
				pressedLeft(evt);
				return;
			case MouseEvent.BUTTON3:
				pressedRight(evt);
				return;
			default:
				/*
				 * Center button pressed??
				 */
			}
		}

		private void pressedLeft(MouseEvent evt) {
			Point pos = evt.getPoint();
			Position cellPos = getLiCoForPos(pos);

			int li = cellPos.getLi();
			int co = cellPos.getCo();
			if (li == -1 || co == -1) {
				return;
			}
			posY = li;
			posX = co;
			repaint();
			selectValue(li, co, pos.x, pos.y);
			view.getController().notifyFocusPositionChanged(li, co);
		}

		private void pressedRight(MouseEvent evt) {
			Point pos = evt.getPoint();
			Position cellPos = getLiCoForPos(pos);

			int li = cellPos.getLi();
			int co = cellPos.getCo();
			if (li == -1 || co == -1) {
				return;
			}
			posY = li;
			posX = co;
			repaint();
			selectMemos(li, co, pos.x, pos.y);
			view.getController().notifyFocusPositionChanged(li, co);
		}

	}

	private void selectValue(int li, int co, int x, int y) {
		if (view.isGrigComplete() || view.isCellReadOnly(li, co)) {
			return;
		}
		pickUpValueOrMemos(true, li, co, x, y);
	}
	
	private void selectMemos(int li, int co, int x, int y) {
		if (view.isGrigComplete() || view.isCellReadOnly(li, co)) {
			return;
		}
		pickUpValueOrMemos(false, li, co, x, y);
	}

	private void pickUpValueOrMemos(boolean valuePickerOnTop, int li, int co, int x, int y) {
		
		byte previousValue = view.getValueAt(li, co);
		Vector<Byte> vec = new Vector<Byte>();
		for (byte i = 1; i <= 9; i++) {
			if (view.isCellMemoSet(li, co, i)) {
				vec.add(new Byte(i));
			}
		}
		Byte[] previousMemos = new Byte[vec.size()];
		previousMemos = vec.toArray(previousMemos);

		DualSwingSelector selector = new DualSwingSelector(valuePickerOnTop, SwingGrid.this.parent,
				SwingGrid.this, x, y, previousValue, previousMemos);
		
		// Handle possible value selection
		int selected = selector.retrieveNumber();
		if (selected == previousValue) {
			selected = 0; // Clear the value
		}
		if (0 <= selected && selected <= 9) {
			view.getController().notifyGridValueChanged(li, co, selected, false);
		}

		// Handle possible memos selection
		byte[] selectedMemos = selector.retrieveMemos();
		if (selectedMemos != null) {
			view.getController().notifyGridMemosChanged(li, co, selectedMemos);
		}
	}

	private class InnerKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			int code = ke.getKeyCode();
			boolean hasMoved = false;
			if (code == KeyEvent.VK_KP_DOWN || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_J) {
				if (posY < 8) {
					posY++;
					hasMoved = true;
					repaint();
				}
			}
			else if (code == KeyEvent.VK_KP_UP || code == KeyEvent.VK_UP || code == KeyEvent.VK_K) {
				if (posY > 0) {
					posY--;
					hasMoved = true;
					repaint();
				}
			}
			else if (code == KeyEvent.VK_KP_LEFT || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_H) {
				if (posX > 0) {
					posX--;
					hasMoved = true;
					repaint();
				}
			}
			else if (code == KeyEvent.VK_KP_RIGHT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				if (posX < 8) {
					posX++;
					hasMoved = true;
					repaint();
				}
			}
			if (hasMoved) {
				view.getController().notifyFocusPositionChanged(posY, posX);
			}
		}

		@Override
		public void keyReleased(KeyEvent ke) {
			int code = ke.getKeyCode();
			if (code == KeyEvent.VK_SPACE) {
				Point pos = getTopLeftPoint(posY, posX);
				selectValue(posY, posX, pos.x, pos.y);
			} else if (code == KeyEvent.VK_SHIFT) {
				Point pos = getTopLeftPoint(posY, posX);
				selectMemos(posY, posX, pos.x, pos.y);
			}
		}
	}
	
}
