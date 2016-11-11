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

package GoGrinder.sgf;

import java.awt.*;

import GoGrinder.ui.GobanPanel;

/**
 *
 * @author  tkington
 */
public class SimpleMark extends NodeMark {
    protected Point pt;
    
    /** Creates a new instance of Mark */
    public SimpleMark(int t, Point p) {
        type = t;
        pt = p;
    }
    
    public Point getPoint() { return pt; }
    
    public Object clone() {
    	return new SimpleMark(type, new Point(pt));
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof SimpleMark))
            return false;
        
        SimpleMark m = (SimpleMark)o;
        if(!pt.equals(m.pt))
            return false;
        if(type != m.type)
            return false;
        return true;
    }
    
    public void flip(int size, boolean flip, int rot) {
        SGFUtils.flipPoint(size, pt, flip, rot);
    }
    
    public void validatePoints(int size) throws SGFParseException {
        SGFUtils.checkPoint(pt, size);
    }
    
    public void invalidatePanel(GobanPanel panel) {
        panel.repaint(pt.x, pt.y);
    }
    
    public Rectangle getBounds() {
        return new Rectangle(pt);
    }
    
    public String getTag() { return NodeMark.getSimpleMarkTag(type); }
    
    public void printBody(StringBuffer out) {
    	out.append("[" + SGFUtils.pointToString(pt) + "]");  //$NON-NLS-1$//$NON-NLS-2$
    }
}
