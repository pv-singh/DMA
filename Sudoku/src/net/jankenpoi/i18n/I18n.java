package net.jankenpoi.i18n;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * HOW TO USE THIS CLASS:<br/>
 * import static net.jankenpoi.i18n.gtxt; 
 */
public class I18n {

	private static ResourceBundle catalog;

	private static Locale currentLocale;

	private static Object lock = new Object();

	private final static ArrayList<LocaleListener> listeners = new ArrayList<LocaleListener>();

	static {
		reset("");
	}

	public static void reset(final String localeString) {
		String[] split = localeString.split("_");
		String lang = split[0];
		String country = split.length > 1 ? split[1] : "";
		synchronized (lock) {
			try {
				if (!"".equals(localeString)) {
					currentLocale = new Locale(lang, country);
				}
				if (currentLocale == null)
					currentLocale = new Locale(System.getenv("LANG"));
				ResourceBundle rb = ResourceBundle.getBundle(I18n.class
						.getName().replace("I18n", "sudokuki"), currentLocale);
				catalog = rb;
				for (int i = 0; i < listeners.size(); i++) {
					listeners.get(i).onLocaleChanged(currentLocale);
				}
			} catch (Exception e1) {
				try {
					if (catalog == null) {
						if (System.getenv("LANG") != null) {
							System.out
									.println("I18n unable to find translations for (LANG) locale "
											+ System.getenv("LANG"));
						}
						catalog = ResourceBundle.getBundle(I18n.class.getName()
								.replace("I18n", "sudokuki"), Locale
								.getDefault());
						currentLocale = Locale.getDefault();
					}
				} catch (Exception e2) {
					System.out
							.println("I18n unable to find translations for (JVM) locale "
									+ Locale.getDefault());
					catalog = null;
				}
			}
		}
	}

	public static void addLocaleListener(LocaleListener listener) {
		synchronized (listeners) {
			if (listener != null) {
				listeners.add(listener);
			}
		}
	}

	public static void removeLocaleListener(LocaleListener listener) {
		synchronized (listeners) {
			if (listener != null) {
				listeners.remove(listener);
			}
		}
	}

	public static String gtxt(String msgid) {
		synchronized (lock) {
			if (catalog != null) {
				return gnu.gettext.GettextResource.gettext(catalog, msgid);
			} else {
				return msgid;
			}
		}
	}
}
