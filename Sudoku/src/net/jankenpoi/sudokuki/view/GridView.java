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
package net.jankenpoi.sudokuki.view;

import net.jankenpoi.sudokuki.controller.GridController;
import net.jankenpoi.sudokuki.model.GridChangedEvent;
import net.jankenpoi.sudokuki.model.GridModel;
import net.jankenpoi.sudokuki.model.GridModel.GridValidity;

public abstract class GridView implements GridListener {
	private GridController controller = null;
	private GridModel model = null;
	
	public GridView(GridModel model) {
		this.model = model;
	}

	public final GridController getController() {
		return controller;
	}

	public final void setController(GridController controller) {
		this.controller = controller;
	}

	public abstract void gridChanged(GridChangedEvent event);

	public abstract void display();

	public abstract void close();

	public byte getValueAt(int li, int co) {
		return model.getValueAt(li, co);
	}

	public boolean isCellValueSet(int li, int co, Byte value) {
		return model.isCellValueSet(li, co, value);
	}

	public boolean isCellValueSet(int li, int co) {
		for (byte v = 1; v<=9; v++) {
			if (isCellValueSet(li, co, Byte.valueOf(v))) {
				return true;
			}
		}
		return false;
	}

	public boolean isCellReadOnly(int li, int co) {
		return model.isCellReadOnly(li, co);
	}
	
	public boolean isCellMemoSet(int li, int co, byte memo) {
		return model.isCellMemoSet(li, co, memo);
	}

	public boolean isGrigComplete() {
		return model.isGridComplete();
	}
	
	public GridValidity getGridValidity() {
		return model.getGridValidity();
	}
	
}
