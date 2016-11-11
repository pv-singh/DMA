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
package net.jankenpoi.sudokuki.model;

import java.util.EventObject;

public class GridChangedEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private int line;
	private int column;
	private short infos;

	public GridChangedEvent(Object source, int li, int co, short infos) {
		super(source);
		this.line = li;
		this.column = co;
		this.infos = infos;
	}

	public Position getPosition() {
		return new Position(line, column);
	}

	public short getInfos() {
		return infos;
	}
	
}
