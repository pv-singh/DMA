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

import java.util.*;
import java.awt.*;

import GoGrinder.sgf.*;

/**
 *
 * @author  tkington
 */
public abstract class LineTest extends Test {
    protected int defaultNum;
    
    protected LineMark badLine;
    protected SimpleMark firstPoint;
    
    /** Creates a new instance of LineTest */
    public LineTest(ArrayList props) throws SGFParseException {
    	defaultNum = -1;
        answers = new ArrayList();
        
        for(int i = 0; i < props.size(); i++) {
            String [] t = ((String)props.get(i)).split("\\:"); //$NON-NLS-1$
            Point p1 = SGFUtils.stringToPoint(t[0].substring(0,2));
            Point p2 = SGFUtils.stringToPoint(t[0].substring(2,4));
            int n = Integer.parseInt(t[1]);
            if(p1.x == 19 && p1.y == 19 && p2.x == 19 && p2.y == 19) {
                defaultNum = n;
                continue;
            }
            
            answers.add(new LineTestAnswer(p1, p2, n));
        }
    }
    
    public LineTestAnswer getAnswer(int x, int y) {
        LineTestAnswer a = null;
        for(int i = 0; i < answers.size(); i++) {
            a = (LineTestAnswer)answers.get(i);
            if(a.checkPoints(firstPoint.getPoint(), x, y))
                return a;
        }
        
        return null;
    }

    public void toFileFormat(StringBuffer out) {
    	out.append("  " + getTag()); //$NON-NLS-1$
    	if(defaultNum != -1)
    		out.append("[tttt:" + defaultNum + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    	for (Iterator iter = answers.iterator(); iter.hasNext();) {
			TestAnswer a = (TestAnswer) iter.next();
			a.toFileFormat(out);
		}
    	out.append("\n"); //$NON-NLS-1$
    }
    
    public void init() {
        reason = null;
        badLine = null;
        firstPoint = null;
    }
    
    public abstract String getTag();
}
