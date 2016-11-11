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
public class OneTest extends PointTest {
    
    /** Creates a new instance of OneTest */
    public OneTest(ArrayList props) throws SGFParseException {
        super(props);
    }
    
    public void init() {
        reason = null;
    }
    
    public void handleClick(Board b, WGFNode curNode,
                            ArrayList globalReasons, int x, int y, int modifiers) {
        TestAnswer a = getAnswer(x, y);
        
        int num;
        if(a == null)
            num = defaultNum;
        else num = a.num;
        
        updateReason(b, controller.getTextPane(), globalReasons,
        			num, curNode, num == 0);
        
        if(num == 0 && a != null) {
            boolean mark = true;
            if(reason != null) {
                ArrayList marks = reason.getMarks();
                if(marks != null) {
                    for(int i = 0; i < marks.size(); i++) {
                        NodeMark m = (NodeMark)marks.get(i);
                        if(m instanceof SimpleMark) {
                            Point p = ((SimpleMark)m).getPoint();
                            if(p.x == a.p.x && p.y == a.p.y) {
                                mark = false;
                                break;
                            }
                        }
                    }
                }
            }
            
            if(mark)
                b.addMark(new SimpleMark(NodeMark.GREENTRI, a.p));
        }
    }
    
    public boolean hasAnswer() {
        //  If user already got answer, just advance
        if(reason != null && reason.getNum() == 0)
            return false;
        return true;
    }
    
    public void showAnswer(Board b, WGFNode curNode) {
        updateReason(b, controller.getTextPane(), null, 0, curNode, true);
        
        //  If answer is 'take sente', do nothing else
        if(defaultNum == 0)
            return;
        
        TestAnswer a = getRightAnswer();
        boolean mark = true;
        
        if(reason != null) {
            ArrayList marks = reason.getMarks();
            if(marks != null) {
                for(int i = 0; i < marks.size(); i++) {
                    NodeMark m = (NodeMark)marks.get(i);
                    if(m instanceof SimpleMark) {
                        Point p = ((SimpleMark)m).getPoint();
                        if(p.x == a.p.x && p.y == a.p.y) {
                            mark = false;
                            break;
                        }
                    }
                }
            }
        }
            
        if(mark)
            b.addMark(new SimpleMark(NodeMark.GREENTRI, a.p));
    }
    
    public String getTag() { return "YN"; } //$NON-NLS-1$
}
