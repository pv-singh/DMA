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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import net.jankenpoi.i18n.I18n;
import net.jankenpoi.i18n.LocaleListener;
import net.jankenpoi.sudokuki.ui.L10nComponent;

@SuppressWarnings("serial")
public class LanguageMenu extends JMenu implements L10nComponent {

        private HashMap<String, JRadioButtonMenuItem> itemsMap = new HashMap<String, JRadioButtonMenuItem>();

        private String langCode;
        
        public LanguageMenu() {
                addItems();
                setIcon(languageIcon(gtxt("DETECTED_LANGUAGE")));
                
                addMenuListener(new MenuListener() {
                        
                         public void menuSelected(MenuEvent arg0) {
                                final String detectedLanguage = gtxt("DETECTED_LANGUAGE");
                                langCode = detectedLanguage;
                        }
                        
                        public void menuDeselected(MenuEvent arg0) {
                                I18n.reset(langCode);
                        }
                        
						public void menuCanceled(MenuEvent arg0) {
                                I18n.reset(langCode);
                        }
                });
                
                final String detectedLanguage = gtxt("DETECTED_LANGUAGE");
                JRadioButtonMenuItem selectedItem = itemsMap.get(detectedLanguage);
                setText(gtxt("Language"));
                if (selectedItem != null) {
                        selectedItem.setSelected(true);
                }
                localeListener = new LocaleListenerImpl(this);
                I18n.addLocaleListener(localeListener);
        }
		        
        private void addItems() {
                ButtonGroup myGroup = new ButtonGroup();
                addItem("ar", "\u0627\u0644\u0639\u0631\u0628\u064a\u0629", myGroup);
                addItem("de", "Deutsch", myGroup);
                addItem("el", "E\u03bb\u03bb\u03b7\u03bd\u03b9\u03ba\u03ac", myGroup);
                addItem("en", "English", myGroup);
                addItem("eo", "Esperanto", myGroup);
                addItem("es", "Espa\u00f1ol", myGroup);
                addItem("fr", "Fran\u00e7ais", myGroup);
                addItem("hu", "Magyar", myGroup);
                addItem("ja", "\u65e5\u672c\u8a9e", myGroup);
                addItem("lv", "Latvie\u0161u", myGroup);
                addItem("nl", "Nederlands", myGroup);
                addItem("pl", "Polski", myGroup);
                addItem("pt", "Portugu\u00eas", myGroup);
                addItem("pt_BR", "Portugu\u00eas (Brasil)", myGroup);
                addItem("ru", "\u0420\u0443\u0441\u0441\u043a\u0438\u0439", myGroup);
                addItem("ta", "\u0BA4\u0BAE\u0BBF\u0BB4\u0BCD\u0020\u0028\u0B87\u0BA8\u0BCD\u0BA4\u0BBF\u0BAF\u0BBE\u0029", myGroup);
                addItem("tr", "\u0054\u00FC\u0072\u006B\u00E7\u0065", myGroup);
                addItem("zh", "\u4e2d\u6587", myGroup);
        }

        private void addItem(final String code, String language, ButtonGroup group) {
                JRadioButtonMenuItem radioItem;

                radioItem = new JRadioButtonMenuItem(language);
                itemsMap.put(code, radioItem);
                radioItem.setAction(new AbstractAction(language, languageIcon(code)) {

                        public void actionPerformed(ActionEvent arg0) {
                                I18n.reset(code);
                        }
                });

                radioItem.addMouseListener(new MouseAdapter() {

                        @Override
                        public void mouseEntered(MouseEvent e) {
                                I18n.reset(code);
                        }
                        
                        @Override
                        public void mouseExited(MouseEvent e) {
                                I18n.reset(langCode);
                        }

                });
                group.add(radioItem);
                add(radioItem);
        }

        private final LocaleListener localeListener;
			public void setL10nMessages(Locale locale, String languageCode) {
			setText(gtxt("Language"));
			setIcon(languageIcon(languageCode));

		JRadioButtonMenuItem selectedItem = itemsMap.get(languageCode);
		if (selectedItem != null) {
			selectedItem.setSelected(true);
		}								
			
			if (this.isSelected()) {
				return;
			}
		}

		public static Icon languageIcon(final String langCode) {
			if ("ar".equals(langCode)) {
				return StockIcons.ICON_FLAG_AR;
			} else if ("de".equals(langCode)) {
				return StockIcons.ICON_FLAG_DE;
			} else if ("el".equals(langCode)) {
				return StockIcons.ICON_FLAG_EL;
			} else if ("eo".equals(langCode)) {
				return StockIcons.ICON_FLAG_EO;
			} else if ("en".equals(langCode)) {
				return StockIcons.ICON_FLAG_EN;
			} else if ("es".equals(langCode)) {
				return StockIcons.ICON_FLAG_ES;
			} else if ("fr".equals(langCode)) {
				return StockIcons.ICON_FLAG_FR;
			} else if ("hu".equals(langCode)) {
				return StockIcons.ICON_FLAG_HU;
			} else if ("ja".equals(langCode)) {
				return StockIcons.ICON_FLAG_JA;
			} else if ("lv".equals(langCode)) {
				return StockIcons.ICON_FLAG_LV;
			} else if ("nl".equals(langCode)) {
				return StockIcons.ICON_FLAG_NL;
                        } else if ("pl".equals(langCode)) {
                                return StockIcons.ICON_FLAG_PL;
			} else if ("pt".equals(langCode)) {
				return StockIcons.ICON_FLAG_PT;
			} else if ("pt_BR".equals(langCode)) {
				return StockIcons.ICON_FLAG_BR;
			} else if ("ru".equals(langCode)) {
				return StockIcons.ICON_FLAG_RU;
			} else if ("ta".equals(langCode)) {
				return StockIcons.ICON_FLAG_TA;
			} else if ("tr".equals(langCode)) {
				return StockIcons.ICON_FLAG_TR;
			} else if ("zh".equals(langCode)) {
				return StockIcons.ICON_FLAG_ZH;
			}
			return StockIcons.ICON_GO_HOME;
		}
		
}
