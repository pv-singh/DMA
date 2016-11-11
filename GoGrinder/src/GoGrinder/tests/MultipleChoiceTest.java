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

import GoGrinder.*;
import GoGrinder.sgf.*;

/**
 *
 * @author  tkington
 */
public class MultipleChoiceTest extends PointTest {
    int numLeft;
    
    /** Creates a new instance of MultipleChoiceTest */
    public MultipleChoiceTest(ArrayList props) throws SGFParseException {
        answers = new ArrayList();
        
        for(int i = 0; i < props.size(); i++) {
            String prop = (String)props.get(i);
            ArrayList pts = new ArrayList();
            int n;
            
            int index = prop.indexOf(':');
            if(index != -1) {
                String [] t = prop.split("\\:"); //$NON-NLS-1$
                
                Point p = SGFUtils.stringToPoint(t[0]);
                n = Integer.parseInt(t[1]);
                
                if(p.x == 19 && p.y == 19) {
                    defaultNum = n;
                    continue;
                }
                
                pts.add(p);
            }
            else {
                String [] t = prop.split("\\="); //$NON-NLS-1$
                
                for(int j = 0; j < t[0].length(); j += 2)
                    pts.add(SGFUtils.stringToPoint(t[0].substring(j, j + 2)));
                n = Integer.parseInt(t[1]);
            }
            
            answers.add(new MCAnswer(pts, n));
        }
    }
    
    public void init() {
        reason = null;
        
        numLeft = 0;
        for(int i = 0; i < answers.size(); i++) {
            MCAnswer a = (MCAnswer)answers.get(i);
            a.clicked = false;
            if(a.num == 0)
                numLeft++;
        }
    }
    
    public void handleClick(Board b, WGFNode curNode,
                            ArrayList globalReasons, int x, int y, int modifiers) {
        MCAnswer a = getMCAnswer(x, y);
        
        int num;
        if(a == null)
            num = defaultNum;
        else num = a.num;
        
        if(num == 0) {
            if(a.clicked) {
                if(a.getOldPt() != -1)
                    b.removeMark(new SimpleMark(NodeMark.GREENTRI, (Point)a.pts.get(a.getOldPt())));
            }
            else {
                a.clicked = true;
                numLeft--;
                
                if(numLeft == 0) {
                    controller.next();
                    return;
                }
            }
                
            b.addMark(new SimpleMark(NodeMark.GREENTRI, (Point)a.pts.get(a.getCurPt())));
        }
        
        updateReason(b, controller.getTextPane(), globalReasons,
        			num, curNode, num == 0);
    }
    
    public MCAnswer getMCAnswer(int x, int y) {
        for(int i = 0; i < answers.size(); i++) {
            MCAnswer a = (MCAnswer)answers.get(i);
            for(int j = 0; j < a.pts.size(); j++) {
                Point p = (Point)a.pts.get(j);
                if(x == p.x && y == p.y) {
                    a.setCurPt(j);
                    return a;
                }
            }
        }
        
        return null;
    }
    
    public void toFileFormat(StringBuffer out) {
    	out.append("  YA"); //$NON-NLS-1$
    	if(defaultNum != -1)
    		out.append("[tt:" + defaultNum + "]");  //$NON-NLS-1$//$NON-NLS-2$
    	for (Iterator iter = answers.iterator(); iter.hasNext();) {
			MCAnswer a = (MCAnswer) iter.next();
			a.toFileFormat(out);
		}
    	out.append("\n"); //$NON-NLS-1$
    }
    
    public boolean hasAnswer() { return false; }
    public void showAnswer(Board b, WGFNode n) { /* */ }
    public String getTag() { return "ERROR"; } //$NON-NLS-1$
    
    static class MCAnswer {
        ArrayList pts;
        int num;
        boolean clicked;
        private int curPt;
        private int oldPt;
        
        MCAnswer(ArrayList pts, int num) {
            this.pts = pts;
            this.num = num;
            oldPt = curPt = -1;
        }
        
        public void setCurPt(int c) { oldPt = curPt; curPt = c; }
        
        public int getCurPt() { return curPt; }
        public int getOldPt() { return oldPt; }
        
        public void toFileFormat(StringBuffer out) {
        	out.append("["); //$NON-NLS-1$
        	for (Iterator iter = pts.iterator(); iter.hasNext();) {
				Point p = (Point) iter.next();
				out.append(SGFUtils.pointToString(p));
			}
        	out.append("=" + num + "]");  //$NON-NLS-1$//$NON-NLS-2$
        }
    }
}
