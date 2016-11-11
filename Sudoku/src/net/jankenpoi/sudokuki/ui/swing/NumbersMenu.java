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
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import net.jankenpoi.i18n.I18n;
import net.jankenpoi.sudokuki.preferences.UserPreferences;
import net.jankenpoi.sudokuki.ui.L10nComponent;

@SuppressWarnings("serial")
public class NumbersMenu extends JMenu implements L10nComponent {

	private final SwingView view;
	private final JRadioButtonMenuItem itemStandardNumbers = new JRadioButtonMenuItem();
	private final JRadioButtonMenuItem itemChineseNumbers = new JRadioButtonMenuItem();
	private final JRadioButtonMenuItem itemArabicNumbers = new JRadioButtonMenuItem();
    private final Action actionStandardNumbers;
	private final Action actionChineseNumbers;
	private final Action actionArabicNumbers;
	private LocaleListenerImpl localeListener;

	public NumbersMenu(SwingView view) {
		this.view = view;

		actionStandardNumbers = new AbstractAction(gtxt("Standard"), null) {
		    
		    public void actionPerformed(ActionEvent arg0) {
		        setNumbersMode(0);
		    }
		};
		
		actionChineseNumbers = new AbstractAction(gtxt("Chinese"), null) {

			public void actionPerformed(ActionEvent arg0) {
			    setNumbersMode(1);
			}
		};

		actionArabicNumbers = new AbstractAction(gtxt("Arabic"), null) {
		    
		    public void actionPerformed(ActionEvent arg0) {
		        setNumbersMode(2);
		    }
		};
		
		addItems();
		setIcon(StockIcons.ICON_FONT);
		
		setL10nMessages(null, gtxt("DETECTED_LANGUAGE"));
		localeListener = new LocaleListenerImpl(this);
		I18n.addLocaleListener(localeListener);
		
		setEnabled(true);
	}

	public void setL10nMessages(Locale locale, String languageCode) {
		setText(gtxt("Numbers"));
		itemStandardNumbers.setText(gtxt("Standard"));
		itemChineseNumbers.setText(gtxt("Chinese"));
		itemArabicNumbers.setText(gtxt("Arabic"));
	}
	
	private void addItems() {
		
		ButtonGroup numbersGroup = new ButtonGroup();
		
		int numbersMode = UserPreferences.getInstance().getInteger("NumbersMode", Integer.valueOf(0)).intValue();
		
        itemStandardNumbers.setAction(actionStandardNumbers);
        numbersGroup.add(itemStandardNumbers);
        itemStandardNumbers.setSelected(numbersMode == 0);
        add(itemStandardNumbers);
        
		itemChineseNumbers.setAction(actionChineseNumbers);
		numbersGroup.add(itemChineseNumbers);
		itemChineseNumbers.setSelected(numbersMode == 1);
		add(itemChineseNumbers);
		
		itemArabicNumbers.setAction(actionArabicNumbers);
		numbersGroup.add(itemArabicNumbers);
		itemArabicNumbers.setSelected(numbersMode == 2);
		add(itemArabicNumbers);
	}
	
	private void setNumbersMode(int mode) {
		UserPreferences.getInstance().set("numbersMode", Integer.valueOf(mode));
		view.getController().notifyGridChanged();
	}
	
}
