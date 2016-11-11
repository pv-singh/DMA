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

import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.jankenpoi.i18n.I18n;
import net.jankenpoi.i18n.LocaleListener;
import net.jankenpoi.sudokuki.ui.L10nComponent;
import net.jankenpoi.sudokuki.view.GridView;

@SuppressWarnings("serial")
public class CheatMenu extends JMenu implements L10nComponent {
        
        private final LocaleListener localeListener;
        public void setL10nMessages(Locale locale, String languageCode) {
                setText(gtxt("Solution..."));
                setIcon(StockIcons.ICON_SOLUTION_MENU);

                itemSetMemosHere.setText(gtxt("Memos"));
                actionSetMemosHere.putValue(Action.SHORT_DESCRIPTION, gtxt("Set memos"));
                itemSetAllMemos.setText(gtxt("All memos"));
                actionSetAllMemos.putValue(Action.SHORT_DESCRIPTION, gtxt("Set memos in all cells"));
                
                itemResolve.setText(gtxt("Resolve"));
                actionResolve.putValue(Action.SMALL_ICON, StockIcons.ICON_GO_JUMP);
                actionResolve.putValue(Action.SHORT_DESCRIPTION, gtxt("Resolve the grid"));
                actionResolve.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
        }

        private final JMenuItem itemSetMemosHere = new JMenuItem();
        private final JMenuItem itemSetAllMemos = new JMenuItem();
        private final JMenuItem itemResolve = new JMenuItem();
        private final Action actionSetMemosHere;
        private final Action actionSetAllMemos;
        private final Action actionResolve;
        
        public CheatMenu(ActionsRepository actions, JFrame parent, GridView view) {
                actionSetMemosHere = new SetMemosHereAction(gtxt("Memos"),
                                StockIcons.ICON_SET_MEMOS_HERE, gtxt("Set memos"), new Integer(
                                                KeyEvent.VK_T), view);
                actions.put("SetMemosHere", actionSetMemosHere);
                
                actionSetAllMemos = new SetAllMemosAction(gtxt("All memos"),
                                StockIcons.ICON_SET_ALL_MEMOS, gtxt("Set memos in all cells"), new Integer(
                                                KeyEvent.VK_X), view);
                actions.put("SetAllMemos", actionSetAllMemos);
                
                actionResolve = new ResolveAction(parent, gtxt("Resolve"),
                                StockIcons.ICON_GO_JUMP, gtxt("Resolve the grid"), new Integer(
                                                KeyEvent.VK_R), view);
                actions.put("ResolveGrid", actionResolve);
                
                addItems();
                setEnabled(true);
                setL10nMessages(null, gtxt("DETECTED_LANGUAGE"));
                localeListener = new LocaleListenerImpl(this);
                I18n.addLocaleListener(localeListener);
        }

        private void addItems() {
                itemSetMemosHere.setAction(actionSetMemosHere);
                add(itemSetMemosHere);
                
                itemSetAllMemos.setAction(actionSetAllMemos);
                add(itemSetAllMemos);

                addSeparator();
                
                itemResolve.setAction(actionResolve);
                add(itemResolve);
        }
}