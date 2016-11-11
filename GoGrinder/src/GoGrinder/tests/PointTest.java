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
import java.util.*;

import GoGrinder.sgf.*;

/**
 *
 * @author  tkington
 */
public abstract class PointTest extends Test {
    protected int defaultNum = -1;
    
    public PointTest() { /* Just for subclassing */ }
    
    public PointTest(ArrayList props) throws SGFParseException {
        answers = new ArrayList();
        
        for(int i = 0; i < props.size(); i++) {
            String [] t = ((String)props.get(i)).split("[\\:\\=]"); //$NON-NLS-1$
            
            for(int j = 0; j < t[0].length(); j += 2) {
                Point p = SGFUtils.stringToPoint(t[0].substring(j, j + 2));
                int n = Integer.parseInt(t[1]);
                if(p.x == 19 && p.y == 19) {
                    defaultNum = n;
                    continue;
                }

                answers.add(new TestAnswer(p, n));
            }
        }
    }
    
    TestAnswer getAnswer(int x, int y) {
        TestAnswer a = null;
        for(int i = 0; i < answers.size(); i++) {
            a = (TestAnswer)answers.get(i);
            if(a.p.x == x && a.p.y == y)
                return a;
        }
        
        return null;
    }
    
    public void toFileFormat(StringBuffer out) {
    	out.append("  " + getTag()); //$NON-NLS-1$
    	if(defaultNum != -1)
    		out.append("[tt:" + defaultNum + "]");  //$NON-NLS-1$//$NON-NLS-2$
    	for (Iterator iter = answers.iterator(); iter.hasNext();) {
			TestAnswer a = (TestAnswer) iter.next();
			a.toFileFormat(out);
		}
    	out.append("\n"); //$NON-NLS-1$
    }
    
    public abstract String getTag();
}
