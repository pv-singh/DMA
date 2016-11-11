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

import javax.swing.ImageIcon;

import net.jankenpoi.sudokuki.resources.UIResources;

public class Images {

	public final static ImageIcon ICON_APPLICATION_LOGO = new ImageIcon(
			UIResources.class.getResource("images/logo.png"));
	
	public final static ImageIcon ICON_APPLICATION_LOGO_SMALL = new ImageIcon(
			UIResources.class.getResource("images/logo_small.png"));

	public final static ImageIcon ICON_APPLICATION = new ImageIcon(
			UIResources.class.getResource("images/sudokuki_icon.png"));
}
