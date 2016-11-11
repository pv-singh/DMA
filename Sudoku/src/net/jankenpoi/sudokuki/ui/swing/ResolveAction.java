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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.jankenpoi.sudokuki.view.GridView;
import static net.jankenpoi.i18n.I18n.gtxt;

@SuppressWarnings("serial")
public class ResolveAction extends AbstractAction {

	private JFrame frame;

	private GridView view;

	public ResolveAction(JFrame frame, String text, Icon icon, String desc,
			Integer mnemonic, GridView view) {
		super(text, icon);
		this.frame = frame;
		putValue(SHORT_DESCRIPTION, desc);
		putValue(MNEMONIC_KEY, mnemonic);
		this.view = view;
	}

	public void actionPerformed(ActionEvent e) {
		ResolveGridDialog dlg = new ResolveGridDialog(frame, view);
		dlg.setVisible(true);
		int result = dlg.getResult();
		if (result == 0) {
			setEnabled(false);
			view.getController().notifyGridResolutionSuccess();
		} else if (result == 2) {
			JOptionPane.showMessageDialog(frame, "<html>"
					+ "<table border=\"0\">" + "<tr>"
					+ gtxt("This grid has no solution.")+"<br/><br/>"
					+ "</tr>" + "</html>", "Sudokuki",
					JOptionPane.WARNING_MESSAGE);
		} else if (result == 1) {
			/**
			 * Operation cancelled by the user
			 */
		} else {
			System.out.println("ResolveAction.actionPerformed() an unexpected error has occurred");
			Thread.dumpStack();
		}
	}
}
