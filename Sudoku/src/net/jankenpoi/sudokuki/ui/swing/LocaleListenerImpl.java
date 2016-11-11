package net.jankenpoi.sudokuki.ui.swing;

import java.util.Locale;

import net.jankenpoi.i18n.LocaleListener;
import net.jankenpoi.sudokuki.ui.L10nComponent;
import static net.jankenpoi.i18n.I18n.gtxt;

class LocaleListenerImpl implements LocaleListener {

	final private L10nComponent l10nComp;

	LocaleListenerImpl(L10nComponent menu) {
		l10nComp = menu;
	}
	
	public void onLocaleChanged(Locale locale) {
		String languageCode = gtxt("DETECTED_LANGUAGE");
		l10nComp.setL10nMessages(locale, languageCode);
	}
	
}
