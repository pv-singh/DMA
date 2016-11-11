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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import static net.jankenpoi.i18n.I18n.gtxt;

/**
 * 
 * @author svedrenne
 */
@SuppressWarnings("serial")
public class SelectMemosPanel extends JPanel {

	private JButton btnClear;
	private JCheckBox[] ckb = new JCheckBox[9];
	private int focusedElement = 4;
	private JButton btnConfirm;
	private JPanel panelClear = new JPanel(new GridLayout());
	private JPanel panel789 = new JPanel(new GridLayout());
	private JPanel panel456 = new JPanel(new GridLayout());
	private JPanel panel123 = new JPanel(new GridLayout());
	private JPanel panelConfirm = new JPanel(new GridLayout());
	private HashSet<Byte> memos = new HashSet<Byte>();
	private HashSet<Byte> previousMemos = new HashSet<Byte>();

	private Font NORMAL_FONT = new Font("Serif", Font.PLAIN, 18);

	private InnerKeyListener innerKeyListener = new InnerKeyListener();
	private InnerFocusListener innerFocusListener = new InnerFocusListener();
	
	private DualSelectionDialog parent;
	
	public SelectMemosPanel(DualSelectionDialog parent, Byte[] previousMemos) {
		super(true);
		this.parent = parent;
		initComponents(previousMemos);
		parent.getTabbedPane().addKeyListener(innerKeyListener);
		parent.getTabbedPane().addFocusListener(innerFocusListener);
	}

	private void configureCheckBox(JCheckBox btn, String text, final int button) {

		btn.setFont(NORMAL_FONT);

		btn.setToolTipText(text);
		btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		btn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonClicked(button);
			}
		});
		btn.addKeyListener(innerKeyListener);
		btn.addFocusListener(innerFocusListener);
	}

	private void initComponents(Byte[] previousMemos) {

		if (previousMemos != null) {
			for (int i = 0; i < previousMemos.length; i++) {
				memos.add(previousMemos[i]);
				this.previousMemos.add(previousMemos[i]);
			}
		}
		for (int i = 0; i < ckb.length; i++) {
			ckb[i] = new JCheckBox(String.valueOf(i+1));
		}
		btnConfirm = new JButton();

		btnClear = new JButton();
		btnClear.setText(gtxt("Clear memos"));
		btnClear.setEnabled(true);
		btnClear.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearClicked();
			}
			private void clearClicked() {
				for (byte i=1; i<=ckb.length; i++) {
					memos.remove(Byte.valueOf(i));
					ckb[i-1].setSelected(false);
				}
			}
		});
		btnClear.addKeyListener(innerKeyListener);
		btnClear.addFocusListener(innerFocusListener);
		configureCheckBox(ckb[6], "7", 6);
		configureCheckBox(ckb[7], "8", 7);
		configureCheckBox(ckb[8], "9", 8);
		configureCheckBox(ckb[3], "4", 3);
		configureCheckBox(ckb[4], "5", 4);
		configureCheckBox(ckb[5], "6", 5);
		configureCheckBox(ckb[0], "1", 0);
		configureCheckBox(ckb[1], "2", 1);
		configureCheckBox(ckb[2], "3", 2);
		btnConfirm.setText(gtxt("Ok"));
		btnConfirm.setEnabled(true);
		btnConfirm.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				confirmClicked();
			}

			private void confirmClicked() {
				parent.memosPanelConfirmed();
			}
		});
		btnConfirm.addKeyListener(innerKeyListener);
		btnConfirm.addFocusListener(innerFocusListener);

		Iterator<Byte> it = memos.iterator();
		while (it.hasNext()) {
			JToggleButton button = ckb[it.next().intValue()-1];
			button.setSelected(true);
		}
		

		GridLayout btnLayout = new GridLayout(5, 1);
		setLayout(btnLayout);
		add(panelClear);
		add(panel123);
		add(panel456);
		add(panel789);
		add(panelConfirm);

		panelClear.add(btnClear);
		panel789.add(ckb[6], BorderLayout.LINE_START);
		panel789.add(ckb[7], BorderLayout.CENTER);
		panel789.add(ckb[8], BorderLayout.LINE_END);
		panel456.add(ckb[3], BorderLayout.LINE_START);
		panel456.add(ckb[4], BorderLayout.CENTER);
		panel456.add(ckb[5], BorderLayout.LINE_END);
		panel123.add(ckb[0], BorderLayout.LINE_START);
		panel123.add(ckb[1], BorderLayout.CENTER);
		panel123.add(ckb[2], BorderLayout.LINE_END);
		panelConfirm.add(btnConfirm);

		ckb[4].requestFocusInWindow();
		focusedElement = 4;
	}

	private boolean isTabSelected() {
		int idx = parent.getTabbedPane().getSelectedIndex();
		return (idx == 1);
	}
	
	private void buttonClicked(int button) {
		byte value = (byte)(button + 1);
		if (ckb[button].isSelected()) {
			memos.add(Byte.valueOf(value));
		} else {
			memos.remove(Byte.valueOf(value));
		}
	}

	public byte[] getSelectedMemos() {
		byte[] memosArray = new byte[memos.size()];

		Iterator<Byte> it = memos.iterator();
		int i = 0;
		while (it.hasNext()) {
			memosArray[i] = it.next().byteValue();
			i++;
		}
		return memosArray;
	}

	private class InnerKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			if (isTabSelected() && focusedTabPane()) {
				int code = ke.getKeyCode();
				if (code == KeyEvent.VK_H) {
					int index = parent.getTabbedPane().getSelectedIndex();
					int newIndex = (index == 0)?1:0;
					parent.getTabbedPane().setSelectedIndex(newIndex);
					parent.getTabbedPane().requestFocusInWindow();
					return;
				}
				else if (code == KeyEvent.VK_L) {
					return;
				}
			}
			if (!isTabSelected()) {
				return;
			}
			int code = ke.getKeyCode();
			if (code == KeyEvent.VK_KP_UP || code == KeyEvent.VK_UP || code == KeyEvent.VK_K) {
				if (focusedTabPane()) {
					return;
				}
				if (focusedClearButton()) {
					parent.getTabbedPane().requestFocusInWindow();
					return;
				}
				if (focusedOkButton()) {
					ckb[focusedElement].requestFocusInWindow();
					return;
				}
				if (focusedElement / 3 == 0) {
					btnClear.requestFocusInWindow();
					return;
				}
				focusedElement = Math.max(0, focusedElement-3);
				ckb[focusedElement].requestFocusInWindow();
			}
			else if (code == KeyEvent.VK_KP_DOWN || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_J) {
				if (focusedTabPane()) {
					btnClear.requestFocusInWindow();
					return;
				}
				if (focusedOkButton()) {
					return;
				}
				if (focusedClearButton()) {
					focusedElement = 1;
					ckb[focusedElement].requestFocusInWindow();
					return;
				}
				if (focusedElement / 3 == 2) {
					btnConfirm.requestFocusInWindow();
					return;
				}
				focusedElement = Math.min(8, focusedElement+3);
				ckb[focusedElement].requestFocusInWindow();
			}
			else if (code == KeyEvent.VK_KP_LEFT || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_H) {
				if (focusedElement%3 == 0 || btnConfirm.hasFocus() || btnClear.hasFocus()) {
					return;
				}
				focusedElement = Math.max(0, focusedElement-1);
				ckb[focusedElement].requestFocusInWindow();
			}
			else if (code == KeyEvent.VK_KP_RIGHT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				if (focusedElement%3 == 2 || focusedOkButton() || focusedClearButton()) {
					return;
				}
				focusedElement = Math.min(8, focusedElement+1);
				ckb[focusedElement].requestFocusInWindow();
			}
			else if (code == KeyEvent.VK_ESCAPE) {
				parent.memosPanelEscaped();
			}
		}

		private boolean focusedTabPane() {
			return parent.getTabbedPane().hasFocus();
		}

		@Override
		public void keyReleased(KeyEvent ke) {
			if (!isTabSelected()) {
				return;
			}
			int code = ke.getKeyCode();
			if (code == KeyEvent.VK_ENTER) {
				if (focusedClearButton() || focusedOkButton()) {
					/*
					 * Buttons actions are managing this so Nothing to do.
					 */
					return;
				}
				parent.memosPanelConfirmed();
			}
		}

	}
	
	private class InnerFocusListener extends FocusAdapter {

		@Override
		public void focusGained(FocusEvent e) {
			
			Component comp = e.getComponent();
			if (comp == ckb[focusedElement]) {
				return;
			}
			for (int i=0; i<ckb.length; i++) {
				if (comp == ckb[i]) {
					focusedElement = i;
					return;
				}
			}
		}
	}
	
	private boolean focusedOkButton() {
		return btnConfirm.hasFocus();
	}

	private boolean focusedClearButton() {
		return btnClear.hasFocus();
	}

	public boolean memosChanged() {
		if (previousMemos.equals(memos)) {
			return false;
		}
		return true;
	}
}
