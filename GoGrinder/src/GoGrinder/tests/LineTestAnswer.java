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

package GoGrinder.tests;

import java.awt.*;

import GoGrinder.sgf.SGFUtils;

/**
 *
 * @author  tkington
 */
public class LineTestAnswer extends TestAnswer {
    Point p2;

    LineTestAnswer(Point p1, Point p2, int n) {
        super(p1, n);
        this.p2 = p2;
        num = n;
    }

    public boolean checkPoints(Point pt, int x, int y) {
        if((pt.x == p.x && pt.y == p.y && x == p2.x && y == p2.y) ||
           (pt.x == p2.x && pt.y == p2.y && x == p.x && y == p.y))
            return true;
        return false;
    }
    
    public void toFileFormat(StringBuffer out) {
    	out.append("[" + SGFUtils.pointToString(p) //$NON-NLS-1$
    			+ SGFUtils.pointToString(p2) + ":" + num + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}