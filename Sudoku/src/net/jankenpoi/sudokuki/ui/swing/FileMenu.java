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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.jankenpoi.i18n.I18n;
import net.jankenpoi.i18n.LocaleListener;
import net.jankenpoi.sudokuki.ui.L10nComponent;

@SuppressWarnings("serial")
public class FileMenu extends JMenu implements L10nComponent {

	private final JMenuItem itemNew = new JMenuItem();
	private final JMenuItem itemOpen = new JMenuItem();
	private final JMenuItem itemSaveAs = new JMenuItem();
	private final JMenuItem itemPrint = new JMenuItem();
	private final JMenuItem itemPrintMulti = new JMenuItem();
	private final JMenuItem itemQuit = new JMenuItem();
	private final Action actionNew;
	private final Action actionOpen;
	private final Action actionSaveAs;
	private final Action actionQuit = new QuitAction();
	private final Action actionPrint;
	private final Action actionPrintMulti;
	
	private final LocaleListener localeListener;
	public void setL10nMessages(Locale locale, String languageCode) {
		setText(gtxt("File"));

		itemNew.setText(gtxt("New"));
		actionNew.putValue(Action.SMALL_ICON, StockIcons.ICON_NEW);
		actionNew.putValue(Action.SHORT_DESCRIPTION, gtxt("New"));
		actionNew.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));

		itemOpen.setText(gtxt("Open"));
		actionOpen.putValue(Action.SMALL_ICON, StockIcons.ICON_OPEN);
		actionOpen.putValue(Action.SHORT_DESCRIPTION, gtxt("Open"));
		actionOpen.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		
		itemSaveAs.setText(gtxt("Save as"));
		actionSaveAs.putValue(Action.SMALL_ICON, StockIcons.ICON_SAVE_AS);
		actionSaveAs.putValue(Action.SHORT_DESCRIPTION, gtxt("Save as"));
		actionSaveAs.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));

		itemPrint.setText(gtxt("Print"));
		actionPrint.putValue(Action.SMALL_ICON, StockIcons.ICON_PRINT);
		actionPrint.putValue(Action.SHORT_DESCRIPTION, gtxt("Print the grid"));
		actionPrint.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));

		itemPrintMulti.setText(gtxt("Print x 4..."));
		actionPrintMulti.putValue(Action.SMALL_ICON,
				StockIcons.ICON_PRINTER_INFO);
		actionPrintMulti.putValue(Action.SHORT_DESCRIPTION,
				gtxt("Print four grids"));

		itemQuit.setText(gtxt("Quit"));
		actionQuit.putValue(Action.SMALL_ICON, StockIcons.ICON_QUIT);
		actionQuit
				.putValue(Action.SHORT_DESCRIPTION, gtxt("Quit the application"));
		actionQuit.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_Q));
	}

	FileMenu(JFrame parent, ActionsRepository actions, SwingGrid grid, SwingView view) {
		super();
		setMnemonic(KeyEvent.VK_F);
		getAccessibleContext().setAccessibleDescription("File menu");

		actionNew = new NewGridAction(parent, view, actions);
		actions.put("NewGrid", actionNew);
		actionOpen = new OpenGridAction(parent, view);
		actions.put("OpenGrid", actionOpen);
		actionSaveAs = new SaveAsAction(parent, view);
		actions.put("SaveAs", actionSaveAs);
		actionPrint = new PrintAction(grid);
		actions.put("Print", actionPrint);
		actionPrintMulti = new PrintMultiAction(parent, view);
		actions.put("PrintMulti", actionPrintMulti);
		addItems();
		setL10nMessages(null, gtxt("DETECTED_LANGUAGE"));
		localeListener = new LocaleListenerImpl(this);
		I18n.addLocaleListener(localeListener);
	}

	private void addItems() {
	
		itemNew.setAction(actionNew);
		itemNew.setIcon(StockIcons.ICON_NEW);
		itemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.CTRL_MASK));
		add(itemNew);

		itemPrint.setAction(actionPrint);
		add(itemPrint);

		itemPrintMulti.setAction(actionPrintMulti);
		add(itemPrintMulti);

		addSeparator();

		itemOpen.setAction(actionOpen);
		itemOpen.setIcon(StockIcons.ICON_OPEN);
		itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.CTRL_MASK));
		add(itemOpen);

		itemSaveAs.setAction(actionSaveAs);
		itemSaveAs.setIcon(StockIcons.ICON_SAVE_AS);
		itemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		itemSaveAs.setMnemonic(KeyEvent.VK_S);
		add(itemSaveAs);

		addSeparator();

		itemQuit.setAction(actionQuit);
		itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK));
		add(itemQuit);
	}
}
