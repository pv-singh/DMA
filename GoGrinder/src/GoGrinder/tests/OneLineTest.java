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
public class OneLineTest extends LineTest {
    /** Creates a new instance of OneLineTest */
    public OneLineTest(ArrayList props) throws SGFParseException {
        super(props);
    }
    
    public void handleClick(Board b, WGFNode curNode,
                            ArrayList globalReasons, int x, int y, int modifiers) {                        
        if(badLine != null) {
            b.removeMark(badLine);
            badLine = null;
        }
            
        if(firstPoint == null) {
            firstPoint = new SimpleMark(NodeMark.TRI, new Point(x, y));
            b.addMark(firstPoint);
            return;
        }
        
        LineTestAnswer a = getAnswer(x, y);
        
        int num;
        if(a != null)
            num = a.num;
        else num = defaultNum;
        
        if(num == 0)    
            b.addMark(new LineMark("LN", a.p, a.p2)); //$NON-NLS-1$
        else {
            badLine = new LineMark("LS", firstPoint.getPoint(), new Point(x, y)); //$NON-NLS-1$
            b.addMark(badLine);
        }
        
        b.removeMark(firstPoint);
        firstPoint = null;
        
        updateReason(b, controller.getTextPane(), globalReasons,
        			num, curNode, num == 0);
    }
    
    public boolean hasAnswer() {
        //  If user already got answer, just advance
        if(reason != null && reason.getNum() == 0)
            return false;
        return true;
    }
    
    public void showAnswer(Board b, WGFNode curNode) {        
        if(badLine != null) {
            b.removeMark(badLine);
            badLine = null;
        }
        
        if(firstPoint != null) {
            b.removeMark(firstPoint);
            firstPoint = null;
        }
        
        updateReason(b, controller.getTextPane(), null, 0, curNode, true);
        
        LineTestAnswer a = (LineTestAnswer)getRightAnswer();
        b.addMark(new LineMark("LN", a.p, a.p2)); //$NON-NLS-1$
    }
    
    public String getTag() { return "YN"; } //$NON-NLS-1$
}
