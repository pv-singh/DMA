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
import net.jankenpoi.i18n.LocaleListener;
import net.jankenpoi.sudokuki.preferences.UserPreferences;
import net.jankenpoi.sudokuki.ui.L10nComponent;

@SuppressWarnings("serial")
public class LevelMenu extends JMenu implements L10nComponent {

	private final Action actionLevel1; 
	private final Action actionLevel2; 
	private final Action actionLevel3; 
	private final Action actionLevel4; 
	private final Action actionLevel5;
	private final JRadioButtonMenuItem itemLevel1 = new JRadioButtonMenuItem();
	private final JRadioButtonMenuItem itemLevel2 = new JRadioButtonMenuItem();
	private final JRadioButtonMenuItem itemLevel3 = new JRadioButtonMenuItem();
	private final JRadioButtonMenuItem itemLevel4 = new JRadioButtonMenuItem();
	private final JRadioButtonMenuItem itemLevel5 = new JRadioButtonMenuItem();
	private final LocaleListener localeListener;
	
	public LevelMenu() {
		actionLevel1 = new AbstractAction(gtxt("Level 1"), null) {

			public void actionPerformed(ActionEvent arg0) {
				setLevelRatingBounds(0, 5700);			
			}
		};
		actionLevel2 = new AbstractAction(gtxt("Level 2"), null) {

			public void actionPerformed(ActionEvent arg0) {
				setLevelRatingBounds(5700, 6700);
			}
		};
		actionLevel3 = new AbstractAction(gtxt("Level 3"), null) {
			public void actionPerformed(ActionEvent arg0) {
				setLevelRatingBounds(6700, 11000);
			}
		};
		actionLevel4 = new AbstractAction(gtxt("Level 4"), null) {

			public void actionPerformed(ActionEvent arg0) {
				setLevelRatingBounds(11000, 15000);
			}
		};
		actionLevel5 = new AbstractAction(gtxt("Level 5"), null) {

			public void actionPerformed(ActionEvent arg0) {
				setLevelRatingBounds(15000, Integer.MAX_VALUE);
			}
		};

		addItems();
		setIcon(StockIcons.ICON_PLUS);

		setL10nMessages(null, gtxt("DETECTED_LANGUAGE"));
		localeListener = new LocaleListenerImpl(this);
		I18n.addLocaleListener(localeListener);
		
		setEnabled(true);
	}

	public void setL10nMessages(Locale locale, String languageCode) {
		setText(gtxt("Level"));
		itemLevel1.setText(gtxt("Level 1"));
		itemLevel2.setText(gtxt("Level 2"));
		itemLevel3.setText(gtxt("Level 3"));
		itemLevel4.setText(gtxt("Level 4"));
		itemLevel5.setText(gtxt("Level 5"));
	}
	
	private void addItems() {
		
		ButtonGroup levelsGroup = new ButtonGroup();
		
		levelsGroup.add(itemLevel1);
		itemLevel1.setAction(actionLevel1);
		itemLevel1.setSelected(true);
		add(itemLevel1);
		
		itemLevel2.setAction(actionLevel2);
		levelsGroup.add(itemLevel2);
		add(itemLevel2);
		
		itemLevel3.setAction(actionLevel3);
		levelsGroup.add(itemLevel3);
		add(itemLevel3);
		
		itemLevel4.setAction(actionLevel4);
		levelsGroup.add(itemLevel4);
		add(itemLevel4);
		
		itemLevel5.setAction(actionLevel5);
		levelsGroup.add(itemLevel5);
		add(itemLevel5);
		
	}
	
	private void setLevelRatingBounds(final int minRating, final int maxRating) {
		UserPreferences.getInstance().set("minRating", Integer.valueOf(minRating));
		UserPreferences.getInstance().set("maxRating", Integer.valueOf(maxRating));
	}
	
}
