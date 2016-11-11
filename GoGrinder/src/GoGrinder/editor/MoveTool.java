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

package GoGrinder.editor;

import java.awt.Point;
import java.awt.event.MouseEvent;

import GoGrinder.WGFController;
import GoGrinder.command.MoveCommand;

/**
 * @author tkington
 */
public class MoveTool extends EditTool {
	public MoveTool(WGFController controller) {
		super(controller);
	}

	public boolean mouseClicked(int x, int y, int modifiers) {
		int color;
		if((modifiers & MouseEvent.BUTTON1_MASK) > 0)
			color = 1;	// Black
		else color = -1;

		MoveCommand c = new MoveCommand(color, new Point(x, y));
		boolean hasCmd = controller.hasCommand(c);
		if(!hasCmd && controller.hasStoneAt(x, y))
			return false;
		
		if(hasCmd)
			controller.removeCommand(c);
		else controller.addCommand(c);
		
		return true;
	}
}
