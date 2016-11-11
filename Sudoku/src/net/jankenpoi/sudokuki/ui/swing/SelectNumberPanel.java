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

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author svedrenne
 */
@SuppressWarnings("serial")
public class SelectNumberPanel extends JPanel {

	private static int FONT_SIZE = 20;
	Font font = new Font("Serif", Font.PLAIN, FONT_SIZE);

	private int digit = -1;
	private JButton btn1 = new JButton();
    private JButton btn2 = new JButton();
    private JButton btn3 = new JButton();
    private JButton btn4 = new JButton();
    private JButton btn5 = new JButton();
    private JButton btn6 = new JButton();
    private JButton btn7 = new JButton();
    private JButton btn8 = new JButton();
    private JButton btn9 = new JButton();
    private JButton[] btns = new JButton[] { btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9 };
    private JPanel panel789 = new JPanel(new GridLayout());
    private JPanel panel456 = new JPanel(new GridLayout());
    private JPanel panel123 = new JPanel(new GridLayout());
	private byte previousValue = 0;

	private InnerKeyListener innerKeyListener = new InnerKeyListener();
	private int focusedElement = 4;
	private InnerFocusListener innerFocusListener = new InnerFocusListener();
	
	private DualSelectionDialog parent;

	public SelectNumberPanel(DualSelectionDialog parent, byte previousValue) {
		this.parent = parent;
        this.previousValue = previousValue;
        initComponents();
        btns[focusedElement].requestFocusInWindow();
		parent.getTabbedPane().addKeyListener(innerKeyListener);
		parent.getTabbedPane().addFocusListener(innerFocusListener);
    }

    private void configureButton(JButton btn, String text, final int value) {
    	btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    	if (previousValue == value) {
    		btn.setText("");
    	} else {
    		btn.setText(text);
    	}
    	btn.setToolTipText(text);
    	btn.setFont(font);
    	btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    	btn.addActionListener(new java.awt.event.ActionListener() {
    		public void actionPerformed(java.awt.event.ActionEvent evt) {
    			buttonClicked(value);
    		}
    	});
    	btn.addKeyListener(innerKeyListener);
    	btn.addFocusListener(innerFocusListener);
    }
    
    private void initComponents() {
        configureButton(btn1, "1", 1);
        configureButton(btn2, "2", 2);
        configureButton(btn3, "3", 3);
        configureButton(btn4, "4", 4);
        configureButton(btn5, "5", 5);
        configureButton(btn6, "6", 6);
        configureButton(btn7, "7", 7);
        configureButton(btn8, "8", 8);
        configureButton(btn9, "9", 9);

        GridLayout btnLayout = new GridLayout(3, 1);
        setLayout(btnLayout);
        add(panel789);
        add(panel456);
        add(panel123);

        panel789.add(btn7, BorderLayout.LINE_START);
        panel789.add(btn8, BorderLayout.CENTER);
        panel789.add(btn9, BorderLayout.LINE_END);
        panel456.add(btn4, BorderLayout.LINE_START);
        panel456.add(btn5, BorderLayout.CENTER);
        panel456.add(btn6, BorderLayout.LINE_END);
        panel123.add(btn1, BorderLayout.LINE_START);
        panel123.add(btn2, BorderLayout.CENTER);
        panel123.add(btn3, BorderLayout.LINE_END);
    }

	private boolean isTabSelected() {
		int idx = parent.getTabbedPane().getSelectedIndex();
		return (idx == 0);
	}

	private void buttonClicked(int button) {
    	digit = button;
		parent.numberPanelConfirmed();
    }

	public int getClickedDigit() {
		return digit;
	}
	
	private class InnerKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			if (focusedTabPane() && isTabSelected()) {
				int code = ke.getKeyCode();
				if (code == KeyEvent.VK_L) {
					int index = parent.getTabbedPane().getSelectedIndex();
					int newIndex = (index == 0)?1:0;
					parent.getTabbedPane().setSelectedIndex(newIndex);
					parent.getTabbedPane().requestFocusInWindow();
					return;
				}
				else if (code == KeyEvent.VK_H) {
					return;
				}
			}
			if (!isTabSelected()) {
				return;
			}
			int code = ke.getKeyCode();
			if (code == KeyEvent.VK_KP_DOWN || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_J) {
				if (focusedTabPane()) {
					focusedElement = 7;
					btns[focusedElement].requestFocusInWindow();
					return;
				}
				if (focusedElement/3 == 0) {
					return;
				}
				focusedElement = Math.max(0, focusedElement-3);
				btns[focusedElement].requestFocusInWindow();
			}
			else if (code == KeyEvent.VK_KP_UP || code == KeyEvent.VK_UP || code == KeyEvent.VK_K) {
				if (focusedTabPane()) {
					return;
				}
				if (focusedElement/3 == 2) {
					parent.getTabbedPane().requestFocusInWindow();
					return;
				}
				focusedElement = Math.min(8, focusedElement+3);
				btns[focusedElement].requestFocusInWindow();
			}
			else if (code == KeyEvent.VK_KP_LEFT || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_H) {
				if (focusedElement%3 == 0) {
					return;
				}
				focusedElement = Math.max(0, focusedElement-1);
				btns[focusedElement].requestFocusInWindow();
			}
			else if (code == KeyEvent.VK_KP_RIGHT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				if (focusedElement%3 == 2) {
					return;
				}
				focusedElement = Math.min(8, focusedElement+1);
				btns[focusedElement].requestFocusInWindow();
			}
			else if (code == KeyEvent.VK_ESCAPE) {
				parent.numberPanelEscaped();
			}
		}

		private boolean focusedTabPane() {
			boolean res = parent.getTabbedPane().hasFocus();
			return res;
		}

	}
	
	private class InnerFocusListener extends FocusAdapter {

		@Override
		public void focusGained(FocusEvent e) {

			Component comp = e.getComponent();
			if (comp == btns[focusedElement]) {
				return;
			}
			for (int i=0; i<btns.length; i++) {
				if (comp == btns[i]) {
					focusedElement = i;
					return;
				}
			}
//			if (comp == parent.getTabbedPane()) {
//				focusedElement = 7;
//				return;
//			}
		}
		
	}

}
