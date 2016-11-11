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
package net.jankenpoi.sudokuki.controller;

import java.util.ArrayList;
import java.util.List;

import net.jankenpoi.sudokuki.model.GridChangedEvent;
import net.jankenpoi.sudokuki.model.GridModel;
import net.jankenpoi.sudokuki.model.GridModel.GridValidity;
import net.jankenpoi.sudokuki.view.GridView;

public class GridController {

	private GridModel model;

	List<GridView> views = new ArrayList<GridView>();

	public GridController(GridModel model) {
		this.model = model;
	}

	public void displayViews() {
		for (GridView view : views) {
			view.display();
		}
	}

	public void closeViews() {
		for (GridView view : views) {
			view.close();
		}
	}

	public void notifyGridChanged() {
		model.fireGridChanged(new GridChangedEvent(model, 0, 0, (short)0));
	}
	
	public void notifyGridValueChanged(int li, int co, int value, boolean silent) {
		model.setCellValue(li, co, value, silent);
	}

	public void notifyGridMemosChanged(int li, int co, byte[] memos) {
		model.clearCellMemos(li, co);
		model.setCellMemos(li, co, memos);
	}

	public void addView(GridView view) {
		views.add(view);
		view.setController(this);
		view.gridChanged(new GridChangedEvent(model, 0, 0, (short)0));
		model.addGridListener(view);
		view.display();
	}

	public void notifySetAllMemosRequested() {
		model.setMemosForAllCells();
	}

	public void notifyClearAllMovesRequested() {
		model.clearAllUserMoves();
	}
	
	public void notifyClearAllMemosRequested() {
		model.clearAllUserMemos();
	}
	
	public void notifyEnterCustomGridMode() {
		model.enterCustomGridMode();
	}
	
	public void notifyExitCustomGridModeRequested() {
		GridValidity validity = model.getGridValidity();
		if (validity.isGridValid()) {
			model.exitCustomGridMode();
		}
		model.fireGridChanged(new GridChangedEvent(model, 0, 0, (short)0));
	}
	
	public void notifyNewGridRequested() {
		if (model.getCustomGridMode()) {
			model.exitCustomGridMode();
		}
		model.requestNewGrid();
		model.fireGridChanged(new GridChangedEvent(model, 0, 0, (short)0));
	}

	public void notifyResetGridFromShorts(short[] externalCellInfos) {
		if (model.getCustomGridMode()) {
			model.exitCustomGridMode();
		}
		model.resetGridModelFromShorts(externalCellInfos);
		model.fireGridChanged(new GridChangedEvent(model, 0, 0, (short)0));
	}

	public int[] getCellInfosFromModel() {
		return model.asIntArray();
	}

	public void notifyGridResolutionSuccess() {
		notifyGridChanged();
		model.setGridResolved();
	}

	public void notifyGridComplete() {
		notifyGridChanged();
		model.setGridComplete();
	}
	
	private int lastLI = 4;
	private int lastCO = 4;

	public void notifyFocusPositionChanged(int li, int co) {
		lastLI = li;
		lastCO = co;
	}

	public void notifySetMemosHere() {
		model.setMemosForThisCell(lastLI, lastCO);
	}
}
