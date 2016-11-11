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
public class AddCommand extends Command {
    private int player;
    private ArrayList points;
    
    /** Creates a new instance of AddCommand */
    public AddCommand(int p, ArrayList props) throws SGFParseException {
        player = p;
        points = new ArrayList();
        for(int i = 0; i < props.size(); i++) {
            points.addAll(SGFUtils.stringToPoints((String)props.get(i)));
        }
    }
    
    public AddCommand(int p) {
        player = p;
    }
    
    public void setPoints(ArrayList pts) { points = pts; }
    
    public boolean doIt(Board b) {
        for(int i = 0; i < points.size(); i++) {
            Point pt = (Point)points.get(i);
            b.addStone(player, pt.x, pt.y);
        }
        return true;
    }
    
    public void undoIt(Board b) {
        for(int i = 0; i < points.size(); i++) {
            Point pt = (Point)points.get(i);
            b.remove(pt.x, pt.y);
        }
    }
    
    public Rectangle getBounds() {
        Rectangle ret = null;
        for(int i = 0; i < points.size(); i++) {
            Point p = (Point)points.get(i);
            if(ret == null)
                ret = new Rectangle(p);
            else ret.add(p);
        }
        return ret;
    }
    
    public void flip(int size, boolean flip, int rot, boolean color) {
        for(int i = 0; i < points.size(); i++) {
            Point p = (Point)points.get(i);
            SGFUtils.flipPoint(size, p, flip, rot);
        }
        if(color)
            player *= -1;
    }
    
    public void validatePoints(int size) throws SGFParseException {
        for(int i = 0; i < points.size(); i++) {
            Point p = (Point)points.get(i);
            SGFUtils.checkPoint(p, size);
        }
    }
    
    public void addPoint(Point p) {
    	if(points == null)
    		points = new ArrayList();
    	points.add(p);
    }
    
    public void removePoint(Point p) {
    	for(int i = 0; i < points.size(); i++) {
    		Point p2 = (Point)points.get(i);
    		if(p.equals(p2)) {
    			points.remove(i);
    			i--;
    		}
    	}
    }
    
    public boolean removePoint(int x, int y) {
    	boolean found = false;
    	for(int i = 0; i < points.size(); i++) {
    		Point p = (Point)points.get(i);
    		if(p.x == x && p.y == y) {
    			points.remove(i);
    			i--;
    			found = true;
    		}
    	}
    	
    	return found;
    }
    
    public void toFileFormat(StringBuffer out) {
    	if(points.size() == 0)
    		return;
    	
    	out.append("  "); //$NON-NLS-1$
    	
    	if(player == 1)
    		out.append("AB"); //$NON-NLS-1$
    	else out.append("AW"); //$NON-NLS-1$
    	
    	for (Iterator iter = points.iterator(); iter.hasNext();) {
			Point p = (Point) iter.next();
			out.append("[" + SGFUtils.pointToString(p) + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
    	out.append("\n"); //$NON-NLS-1$
    }
    
    public boolean hasPoint(int x, int y) {
    	for (Iterator iter = points.iterator(); iter.hasNext();) {
			Point p = (Point) iter.next();
			if(p.x == x && p.y == y)
				return true;
		}
    	
    	return false;
    }
    
    public int getPlayer() { return player; }
}
