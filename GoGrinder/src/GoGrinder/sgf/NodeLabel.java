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

import java.awt.Point;

/**
 *
 * @author  tkington
 */
public class NodeLabel extends SimpleMark {
    private String label;
    
    /** Creates a new instance of Label */
    public NodeLabel(String s, Point p) throws SGFParseException {
        super(LABEL, p);
        
        SGFUtils.checkPoint(p, 19);
        
        //  Remove leading 0's from numbers
        try {
            int i = Integer.parseInt(s);
            label = String.valueOf(i);
        }
        catch(NumberFormatException e) {
            label = s;
        }
    }
    
    public Object clone() {
    	try {
    		return new NodeLabel(label, new Point(pt));
    	}
    	catch(SGFParseException e) {
    		return null;
    	}
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof NodeLabel))
            return false;
        
        NodeLabel l = (NodeLabel)o;
        if(!pt.equals(l.pt))
            return false;
        if(!label.equals(l.label))
            return false;
        return true;
    }
    
    public String getText() { return label; }
    
	public void printBody(StringBuffer out) {
		out.append("[" + SGFUtils.pointToString(pt) + ":" + label + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
