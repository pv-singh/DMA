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
package net.jankenpoi.sudokuki.ui;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 1 && "test".equals(args[0])) {
			System.out.println("Main.main() Starting Sudokuki for a test ");
			UIApp app = new TestApp();
			app.start();
			return;
		}

		String uiName = null;
		if (args.length < 2 || !"-ui".equals(args[0])) {
			uiName = "Swing";
		}

		if (uiName == null) {
			uiName = args[1];
		}
		String subPack = uiName.toLowerCase();
		String uiAppName = "net.jankenpoi.sudokuki.ui." + subPack + "."
				+ uiName + "App";

		UIApp app = null;
		try {
			Class<?> appClass = Class.forName(uiAppName);
			app = (UIApp) appClass.newInstance();
		} catch (ClassNotFoundException e) {
			System.out
					.println("ClassNotFoundException: couldn't find Sudokuki UI class "
							+ uiAppName + "...");
		} catch (InstantiationException e) {
			System.out
					.println("InstantiationException: couldn't instantiate Sudokuki UI of type '"
							+ uiName + "'...");
		} catch (IllegalAccessException e) {
			System.out
					.println("IllegalAccessException: illegal access to Sudokuki UI of type '"
							+ uiName + "'...");
		} finally {
			if (app == null) {
				System.out
						.println("Unable to start Sudokuki with a UI of type '"
								+ uiName + "'...");
				return;
			}
		}

		app.start();
	}
}
