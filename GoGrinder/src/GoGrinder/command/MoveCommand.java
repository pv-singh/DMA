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
import java.util.*;

import GoGrinder.Board;
import GoGrinder.sgf.*;

/**
 *
 * @author  tkington
 */
public class MoveCommand extends Command {
    private int player;
    private Point move;
    private AddCommand replaceStones;
    private Point oldKoPoint;
    
    /** Creates a new instance of MoveCommand */
    public MoveCommand(int p, ArrayList props) throws SGFParseException {
        player = p;
        move = SGFUtils.stringToPoint((String)props.get(0));
    }
    
    public MoveCommand(int p, Point pos) {
        player = p;
        move = pos;
    }
    
    public boolean doIt(Board b) {
        ArrayList capturedStones = new ArrayList();
        
        oldKoPoint = (Point)b.getKoPoint().clone();
        
        b.move(player, move.x, move.y, capturedStones);
        if(!capturedStones.isEmpty()) {
            replaceStones = new AddCommand(player * -1);
            replaceStones.setPoints(capturedStones);
        }
        return true;
    }
    
    public void undoIt(Board b) {
        if(replaceStones != null) {
            replaceStones.doIt(b);
        }
        
        b.remove(move.x, move.y);
        b.setKoPoint(oldKoPoint);
    }
    
    public Rectangle getBounds() { return new Rectangle(move); }
    
    public void flip(int size, boolean flip, int rot, boolean color) {
        if(color)
            player *= -1;
        SGFUtils.flipPoint(size, move, flip, rot);
    }
    
    public void validatePoints(int size) throws SGFParseException {
        SGFUtils.checkPoint(move, size);
    }
    
    public int getPlayer() { return player; }
    public Point getPoint() { return move; }

    public void toFileFormat(StringBuffer out) {
    	out.append("  "); //$NON-NLS-1$
    	
    	if(player == 1)
    		out.append("B"); //$NON-NLS-1$
    	else out.append("W"); //$NON-NLS-1$
    	
    	out.append("[" + SGFUtils.pointToString(move) + "]\n");  //$NON-NLS-1$//$NON-NLS-2$
    }
    
    public boolean equals(Object o) {
    	if(!(o instanceof MoveCommand))
    		return false;
    	
    	MoveCommand c = (MoveCommand)o;
    	if(!move.equals(c.move))
    		return false;
    	if(player != c.player)
    		return false;
    	return true;
    }
}
