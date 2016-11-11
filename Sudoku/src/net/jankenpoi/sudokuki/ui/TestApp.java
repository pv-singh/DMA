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

import net.jankenpoi.sudokuki.controller.GridController;
import net.jankenpoi.sudokuki.model.GridModel;
import net.jankenpoi.sudokuki.ui.swing.SwingView;
import net.jankenpoi.sudokuki.ui.text.TextView;

public class TestApp implements UIApp {

	public void start() {
		GridModel model = new GridModel();
		GridController controller = new GridController(model);
		controller.addView(new TextView(model));
		controller.addView(new SwingView(model));
	}
}
