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

package GoGrinder.command;

import java.awt.*;

import GoGrinder.Board;
import GoGrinder.sgf.*;

/**
 *
 * @author  tkington
 */
public class RemoveCommand extends Command {
    private Point p;
    private int color;
    
    /** Creates a new instance of RemoveCommand */
    public RemoveCommand(MoveCommand m) {
        p = new Point(m.getPoint());
        color = m.getPlayer();
    }
    
    public boolean doIt(Board b) {
        b.remove(p.x, p.y);
        return true;
    }
    
    public void undoIt(Board b) {
        b.addStone(color, p.x, p.y);
    }
    
    public Rectangle getBounds() {
        return new Rectangle(p);
    }
    
    public void flip(int size, boolean flip, int rot, boolean c) {
        SGFUtils.flipPoint(size, p, flip, rot);
    }
    
    public void validatePoints(int size) throws SGFParseException {
        SGFUtils.checkPoint(p, size);
    }
    
    public Point getPoint() { return p; }

    
    public void toFileFormat(StringBuffer out) { /* */ }
}
