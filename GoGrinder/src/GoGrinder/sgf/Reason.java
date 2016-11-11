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

import java.util.*;

import GoGrinder.Messages;

/**
 *
 * @author  tkington
 */
public class Reason implements TagListener {
    private int num;
    private String text;
    private ArrayList marks;

    Reason(int n, String s, boolean converting) throws SGFParseException {
        num = n;
        
        int index = s.lastIndexOf(']');
        if(index == -1)
            text = s;
        else {
            marks = new ArrayList();
            text = s.substring(index + 1);
            SGFParser.parseTags(s.substring(0, index + 1), this);
        }
        
        if(converting)
        	text = text.replaceAll("\n", "<br>"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void addTag(String tag, ArrayList propVals) throws SGFParseException {
        if(!NodeMark.create(marks, tag, propVals))
            System.out.println(Messages.getString("Reason.UnhandledTag") + tag); //$NON-NLS-1$
    }
    
    public ArrayList getPropertyList(String tag) { return new ArrayList(); }

    public boolean replaceLink(String linkDest) {
        int pos1 = text.indexOf('_');
        int pos2 = text.indexOf('_', pos1 + 1);
        if(pos1 != -1 || pos2 != -1) {
            String linkText = text.substring(pos1 + 1, pos2);
            text = text.substring(0, pos1) + "<a href=\"" //$NON-NLS-1$
                    + linkDest + "\">" + linkText + "</a>" //$NON-NLS-1$ //$NON-NLS-2$
                    + text.substring(pos2 + 1);
            return true;
        }

        return false;
    }
    
    public void toFileFormat(StringBuffer out) {
    	out.append("  XS[" + num + ":"); //$NON-NLS-1$ //$NON-NLS-2$
    	SGFUtils.printMarks(marks, out, false);
    	out.append(text + "]\n"); //$NON-NLS-1$
    }
    
    public int getNum() { return num; }
    public String getText() { return text; }
    public ArrayList getMarks() { return marks; }
}