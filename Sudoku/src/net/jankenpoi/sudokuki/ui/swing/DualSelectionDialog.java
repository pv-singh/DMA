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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class DualSelectionDialog extends JDialog {

	private SelectNumberPanel numberPanel;
	private SelectMemosPanel memosPanel;
	
	public DualSelectionDialog(boolean valuePickerOnTop, JFrame parent, byte previousValue,
			Byte[] previousMemos) {
		super(parent, true);
		tabbedPane = new JTabbedPane();
		numberPanel = new SelectNumberPanel(this, previousValue);
		memosPanel = new SelectMemosPanel(this, previousMemos);
		tabbedPane.addTab(gtxt("Select"), numberPanel);
		tabbedPane.addTab(gtxt("Memos"), memosPanel);
		
		tabbedPane.setSelectedComponent(valuePickerOnTop ? numberPanel
				: memosPanel);
		add(tabbedPane);
		pack();

		setResizable(false);
	}

	private JTabbedPane tabbedPane;
	JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
	private int value = -1;
	private byte[] memos = null;
	
	public byte[] getSelectedMemos() {
		return memos;
	}

	public int getClickedDigit() {
		return value;
	}

	void memosPanelConfirmed() {
		memos = memosPanel.getSelectedMemos();
		if (memosPanel.memosChanged()) {
			value = 0;
		}
		dispose();
	}

	public void numberPanelConfirmed() {
		value = numberPanel.getClickedDigit();
		memos = null;
		dispose();
	}
	
	public void memosPanelEscaped() {
		inputCancelled();
	}

	public void numberPanelEscaped() {
		inputCancelled();
	}
	
	private void inputCancelled() {
		value = -1;
		memos = null;
		dispose();
	}
	
}
