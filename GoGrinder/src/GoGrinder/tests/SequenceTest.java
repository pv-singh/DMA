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
public class SequenceTest extends Test {
    private ArrayList states;
    private int state;
    private int defaultNum;
    
    /** Creates a new instance of SequenceTest */
    public SequenceTest(ArrayList props) throws SGFParseException {
    	defaultNum = -1;
        states = new ArrayList();
        
        ArrayList answers = new ArrayList();
        states.add(answers);
        
        STAnswer lastRightAnswer = null;
        
        for(int i = 0; i < props.size(); i++) {
            String prop = (String)props.get(i);
            
            if(prop.indexOf('@') != -1) {
                String [] t = prop.split("@"); //$NON-NLS-1$
                Point p = SGFUtils.stringToPoint(t[0]);
                
                NodeMark m;
                if(t[1].equals("b")) //$NON-NLS-1$
                    m = new SimpleMark(NodeMark.FAKEB, p);
                else if(t[1].equals("w")) //$NON-NLS-1$
                    m = new SimpleMark(NodeMark.FAKEW, p);
                else throw new SGFParseException(Messages.getString("SequenceTest.UnknownTagInYS") + t[1]); //$NON-NLS-1$
                
                lastRightAnswer.marks.add(m);
            }
            else {
                String [] t = prop.split("[\\:\\=]"); //$NON-NLS-1$

                for(int j = 0; j < t[0].length(); j += 2) {
                    Point p = SGFUtils.stringToPoint(t[0].substring(j, j + 2));
                    int n = Integer.parseInt(t[1]);
                    if(p.x == 19 && p.y == 19) {
                        defaultNum = n;
                        continue;
                    }
                    
                    boolean right = n == 0;
                    if(t.length == 3) {
                        right = true;
                        n = Integer.parseInt(t[2]);
                    }

                    STAnswer a = new STAnswer(p, n, right);
                    answers.add(a);
                    
                    if(right) {
                        lastRightAnswer = a;
                        answers = new ArrayList();
                        states.add(answers);
                    }
                }
            }
        }
        
        states.remove(states.size() - 1);
    }
    
    public void init() {
        state = 0;
        reason = null;
    }
    
    public void handleClick(Board b, WGFNode curNode,
                            ArrayList globalReasons, int x, int y, int modifiers) {
        STAnswer a = getSTAnswer(x, y);
        
        int num;
        if(a == null)
            num = defaultNum;
        else num = a.num;
        
        if(a != null && a.isRight) {
            state++;
            if(state == states.size()) {
                controller.next();
                return;
            }
            
            for(int i = 0; i < a.marks.size(); i++) {
                b.addMark((NodeMark)a.marks.get(i));
            }
        }
        
        updateReason(b, controller.getTextPane(), globalReasons,
        			num, curNode, a != null && a.isRight);
    }
    
    public STAnswer getSTAnswer(int x, int y) {
        ArrayList ans = (ArrayList)states.get(state);
        for(int i = 0; i < ans.size(); i++) {
            STAnswer a = (STAnswer)ans.get(i);
            if(a.pt.x == x && a.pt.y == y)
                return a;
        }
        
        return null;
    }
    
    public void toFileFormat(StringBuffer out) {
    	out.append("  YS"); //$NON-NLS-1$
    	if(defaultNum != -1)
    		out.append("[tt:" + defaultNum + "]");  //$NON-NLS-1$//$NON-NLS-2$
    	for (Iterator iter = states.iterator(); iter.hasNext();) {
			ArrayList state = (ArrayList) iter.next();
			for (Iterator iter2 = state.iterator(); iter2.hasNext();) {
				STAnswer a = (STAnswer) iter2.next();
				a.toFileFormat(out);
			}
		}
    	out.append("\n"); //$NON-NLS-1$
    }
    
    public boolean hasAnswer() { return false; }
    public void showAnswer(Board b, WGFNode n) { /* */ }
    
    static class STAnswer {
        Point pt;
        int num;
        boolean isRight;
        ArrayList marks;
        
        STAnswer(Point p, int num, boolean right) {
            pt = p;
            this.num = num;
            isRight = right;
            if(right)
                marks = new ArrayList();
        }
        
        public void toFileFormat(StringBuffer out) {
        	out.append("[" + SGFUtils.pointToString(pt)); //$NON-NLS-1$
        	if(isRight)
        		out.append(":0"); //$NON-NLS-1$
        	if(num != 0)
        		out.append(":" + num); //$NON-NLS-1$
        	out.append("]"); //$NON-NLS-1$
        	
        	if(marks == null)
        		return;
        	
        	for (Iterator iter = marks.iterator(); iter.hasNext();) {
				SimpleMark m = (SimpleMark) iter.next();
				int type = m.getType();
				switch(type) {
				case SimpleMark.FAKEB:
					out.append("[" + SGFUtils.pointToString(m.getPoint()) + "@b]"); //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case SimpleMark.FAKEW:
					out.append("[" + SGFUtils.pointToString(m.getPoint()) + "@w]");  //$NON-NLS-1$//$NON-NLS-2$
					break;
				}
			}
        }
    }
}
