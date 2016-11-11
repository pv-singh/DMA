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
public class AllTest extends PointTest {
    int numLeft;
    
    /** Creates a new instance of AllTest */
    public AllTest(ArrayList props) throws SGFParseException {
        super(props);
    }
    
    public void init() {
        reason = null;
        
        numLeft = 0;
        for(int i = 0; i < answers.size(); i++) {
            TestAnswer a = (TestAnswer)answers.get(i);
            a.clicked = false;
            if(a.num == 0)
                numLeft++;
        }
    }
    
    public void handleClick(Board b, WGFNode curNode,
                            ArrayList globalReasons, int x, int y, int modifiers) {
        TestAnswer a = getAnswer(x, y);
        
        int num;
        if(a == null)
            num = defaultNum;
        else num = a.num;
        
        if(num == 0) {
            if(!a.clicked) {
                a.clicked = true;
                numLeft--;
                
                if(numLeft == 0) {
                    controller.next();
                    return;
                }
                
                b.addMark(new SimpleMark(NodeMark.GREENTRI, a.p));
            }
        }
        
        updateReason(b, controller.getTextPane(), globalReasons,
        			num, curNode, num == 0);
    }
    
    public boolean gotAnswer(Point p) {
        for(int i = 0; i < answers.size(); i++) {
            TestAnswer a = (TestAnswer)answers.get(i);
            if(p.equals(a.p))
                return a.clicked;
        }
        return false;
    }
    
    public boolean hasAnswer() { return false; }
    public void showAnswer(Board b, WGFNode n) { /* */ }
    public String getTag() { return "YA"; } //$NON-NLS-1$
}
