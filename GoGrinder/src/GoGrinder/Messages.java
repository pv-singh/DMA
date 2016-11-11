/*
* GoGrinder - a program to practice Go problems
* Copyright (c) 2004-2006 Tim Kington
* timkington@users.sourceforge.net
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
*/

package GoGrinder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author tkington
 */
public class Messages {
	private static final String BUNDLE_NAME = "GoGrinder.messages";//$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE;
	
	static {
		Locale loc = getLocale();
		RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, loc);
	}
	
	private static Locale getLocale() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(Main.LOCALE_FILE)); //$NON-NLS-1$
			String locale = in.readLine();
			in.close();
			
			return new Locale(locale);
		}
		catch(IOException e) {
			//	Do nothing, just use default locale
		}
		
		return Locale.getDefault();
	}

	private Messages() { /* */ }

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}