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
import GoGrinder.sgf.NodeMark;
import GoGrinder.sgf.SimpleMark;

/**
 * @author tkington
 */
public class LocalMoveTool extends EditTool {
	public LocalMoveTool(WGFController controller) {
		super(controller);
	}

	public boolean mouseClicked(int x, int y, int modifiers) {
		int type;
		if((modifiers & MouseEvent.BUTTON1_MASK) > 0)
			type = NodeMark.LOCALB;
		else type = NodeMark.LOCALW;
		
		SimpleMark m = controller.getSimpleMark(x, y, type);
		
		if(m == null && controller.hasStoneAt(x, y))
			return false;
		
		if(m != null)
			controller.removeMark(m);
		else controller.addMark(new SimpleMark(type, new Point(x, y)));
		
		return true;
	}
}
