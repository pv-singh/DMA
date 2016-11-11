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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import net.jankenpoi.i18n.I18n;
import net.jankenpoi.i18n.LocaleListener;
import net.jankenpoi.sudokuki.ui.L10nComponent;

@SuppressWarnings("serial")
public class ToolBar extends JToolBar implements L10nComponent {
	
	private JFrame frame;
	private Action actionInvokeLanguageDialog;
	private final LocaleListener localeListener;
	
	ToolBar(JFrame frame, ActionsRepository actions) {
		this.frame = frame;
		setFloatable(false);
		addButtons(actions);

		Component[] components = getComponents();
		for (int i=0; i<components.length; i++) {
			components[i].setFocusable(false);
		}
		
        setL10nMessages(null, gtxt("DETECTED_LANGUAGE"));
        localeListener = new LocaleListenerImpl(this);
        I18n.addLocaleListener(localeListener);
	}

	private void addButtons(ActionsRepository actions) {
		Action newGridAction = actions.get("NewGrid");
		Action printAction = actions.get("Print");
		Action openAction = actions.get("OpenGrid");
		Action saveAction = actions.get("SaveAs");
		Action openUpdateSiteAction = actions.get("OpenUpdateSite");

		JButton button = new JButton(StockIcons.ICON_NEW);
		button.setAction(newGridAction);
	    this.add(button);
	    
		button = new JButton(printAction);
	    this.add(button);

		button = new JButton(openAction);
	    this.add(button);

		button = new JButton(saveAction);
	    this.add(button);

	    button = new JButton(openUpdateSiteAction);
		button.setText("");
	    button.setEnabled(false);
	    this.add(button);
	    
	    actionInvokeLanguageDialog = new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				LanguageDialog dlg = new LanguageDialog(frame, ToolBar.this);
				dlg.setVisible(true);
			}

	    };
	    button = new JButton(actionInvokeLanguageDialog);
	    button.setEnabled(true);
	    actionInvokeLanguageDialog.putValue(Action.SMALL_ICON, LanguageMenu.languageIcon(gtxt("DETECTED_LANGUAGE")));
	    actionInvokeLanguageDialog.putValue(Action.SHORT_DESCRIPTION, gtxt("Language"));

		button.setText("");
	    this.add(button);
	}

	public void setL10nMessages(Locale locale, String languageCode) {
	    actionInvokeLanguageDialog.putValue(Action.SMALL_ICON, LanguageMenu.languageIcon(languageCode));
		actionInvokeLanguageDialog.putValue(Action.SHORT_DESCRIPTION, gtxt("Language"));
	}

}
