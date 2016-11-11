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

import javax.swing.JEditorPane;

import GoGrinder.*;
import GoGrinder.sgf.*;

/**
 *
 * @author  tkington
 */
public abstract class Test {
    public abstract void init();
    public abstract void handleClick(Board b, WGFNode curNode,
                           ArrayList globalReasons, int x, int y, int modifiers);
    
    public static WGFController controller;
    protected Reason reason;
    protected ArrayList answers;
    
    public static Test create(String tag, ArrayList list) throws SGFParseException {
        if(tag.equals("YA")) { //$NON-NLS-1$
            for(int i = 0; i < list.size(); i++) {
                if(((String)list.get(i)).indexOf('=') != -1)
                    return new MultipleChoiceTest(list);
            }
            
            String [] t = ((String)list.get(0)).split("\\:"); //$NON-NLS-1$
            if(t[0].length() == 4)
                return new AllLineTest(list);
            return new AllTest(list);
        }
        
        if(tag.equals("YN")) { //$NON-NLS-1$
            String [] t = ((String)list.get(0)).split("\\:"); //$NON-NLS-1$
            if(t.length == 1 || t[0].length() == 2)
                return new OneTest(list);
            else if(t[0].length() == 4)
                return new OneLineTest(list);
        }
        
        if(tag.equals("YO")) //$NON-NLS-1$
            return new OrderedTest(list);
        
        if(tag.equals("YS")) //$NON-NLS-1$
            return new SequenceTest(list);
        
        throw new SGFParseException(Messages.getString("Test.UnknownTestType")); //$NON-NLS-1$
    }
    
    public void updateReason(Board b, JEditorPane textPane,
    						 ArrayList globalReasons, int num,
                             WGFNode curNode, boolean isRight) {
        
        if(reason != null) {
            ArrayList marks = reason.getMarks();
            if(marks != null) {
                for(int i = 0; i < marks.size(); i++) {
                    NodeMark m = (NodeMark)marks.get(i);
                    b.removeMark(m);
                }
            }
        }

        reason = null;

        ArrayList reasons = curNode.getReasons();
        if(reasons != null) {
            for(int i = 0; i < reasons.size(); i++) {
                Reason r = (Reason)reasons.get(i);
                if(r.getNum() == num) {
                    reason = r;
                    break;
                }
            }
        }

        if(reason == null && globalReasons != null) {
            for(int i = 0; i < globalReasons.size(); i++) {
                Reason r = (Reason)globalReasons.get(i);
                if(r.getNum() == num) {
                    reason = r;
                    break;
                }
            }
        }

        if(reason == null)
            textPane.setText(curNode.getComment());
        else {
            String cmt = curNode.getComment().trim();
            
            if(cmt.endsWith("</html>") || cmt.endsWith("</HTML>")) { //$NON-NLS-1$ //$NON-NLS-2$
            	cmt = cmt.substring(0, cmt.length() - 7);
            }
            
            cmt += "<br><br><font color=\""; //$NON-NLS-1$
            cmt += isRight ? "blue" : "red"; //$NON-NLS-1$ //$NON-NLS-2$
            textPane.setText(cmt + "\">" + reason.getText() + "</font></html>"); //$NON-NLS-1$ //$NON-NLS-2$
            ArrayList marks = reason.getMarks();
            if(marks != null) {
                for(int i = 0; i < marks.size(); i++) {
                    b.addMark((NodeMark)marks.get(i));
                }
            }
        }
    }
    
    public TestAnswer getRightAnswer() { 
        for(int i = 0; i < answers.size(); i++) {
            TestAnswer a = (TestAnswer)answers.get(i);
            if(a.num == 0)
                return a;
        }
        return null;
    }
    
    public abstract boolean hasAnswer();
    public abstract void showAnswer(Board b, WGFNode curNode);
    public abstract void toFileFormat(StringBuffer out);
}
