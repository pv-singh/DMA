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

import java.awt.*;
import java.util.*;

import GoGrinder.ui.GobanPanel;

/**
 *
 * @author  tkington
 */
public abstract class NodeMark implements Comparable {
	//	This order is important - it specifies the order
	//	in which marks will be written to file, and
	//	eventually drawn on the screen
    public static final int FAKEB = 0;
    public static final int FAKEW = 1;
    public static final int LOCALB = 2;
    public static final int LOCALW = 3;
    public static final int CIR = 4;
    public static final int TRI = 5;
    public static final int SQU = 6;
    public static final int X = 7;
    public static final int TERRB = 8;
    public static final int TERRW = 9;
    public static final int GREYSQ = 10;
    public static final int GROUSE = 11;
    public static final int GREYSTONE = 12;
    public static final int GREENTRI = 13;
    public static final int REDTRI = 14;
    public static final int GREENCIR = 15;
    public static final int REDCIR = 16;
    public static final int LABEL = 17;
    public static final int LINE = 18;
    public static final int EMPTY = 19;
    
    protected int type;
    
    public int getType() { return type; }
    
    public static int getSimpleMarkType(String t) {
        if(t.equals("SQ")) //$NON-NLS-1$
            return SQU;
        if(t.equals("CR")) //$NON-NLS-1$
            return CIR;
        if(t.equals("TR")) //$NON-NLS-1$
            return TRI;
        if(t.equals("MA")) //$NON-NLS-1$
            return X;
        if(t.equals("XB")) //$NON-NLS-1$
            return FAKEB;
        if(t.equals("XW")) //$NON-NLS-1$
            return FAKEW;
        if(t.equals("TB")) //$NON-NLS-1$
            return TERRB;
        if(t.equals("TW")) //$NON-NLS-1$
            return TERRW;
        if(t.equals("TT")) //$NON-NLS-1$
            return GREYSQ;
        if(t.equals("XG")) //$NON-NLS-1$
            return GROUSE;
        if(t.equals("XD") || t.equals("XZ")) //$NON-NLS-1$ //$NON-NLS-2$
            return GREYSTONE;
        if(t.equals("YB")) //$NON-NLS-1$
            return LOCALB;
        if(t.equals("YW")) //$NON-NLS-1$
            return LOCALW;
        if(t.equals("XE")) //$NON-NLS-1$
            return EMPTY;
        return -1;
    }
    
    public static String getSimpleMarkTag(int type) {
    	switch(type) {
    	case SQU: return "SQ"; //$NON-NLS-1$
    	case CIR: return "CR"; //$NON-NLS-1$
    	case TRI: return "TR"; //$NON-NLS-1$
    	case X: return "MA"; //$NON-NLS-1$
    	case FAKEB: return "XB"; //$NON-NLS-1$
    	case FAKEW: return "XW"; //$NON-NLS-1$
    	case TERRB: return "TB"; //$NON-NLS-1$
    	case TERRW: return "TW"; //$NON-NLS-1$
    	case GREYSQ: return "TT"; //$NON-NLS-1$
    	case GROUSE: return "XG"; //$NON-NLS-1$
    	case GREYSTONE: return "XD"; //$NON-NLS-1$
    	case LOCALB: return "YB"; //$NON-NLS-1$
    	case LOCALW: return "YW"; //$NON-NLS-1$
    	case EMPTY: return "XE"; //$NON-NLS-1$
    	case LABEL: return "LB"; //$NON-NLS-1$
    	}
    	
    	return null;
    }
    
    public static boolean create(ArrayList marks, String tag,
                                 ArrayList propVals) throws SGFParseException {
        int type = SimpleMark.getSimpleMarkType(tag);
        
        if(type != -1) {
            for(int i = 0; i < propVals.size(); i++) {
            	ArrayList pts = SGFUtils.stringToPoints((String)propVals.get(i));
            	for(int j = 0; j < pts.size(); j++) {
            		marks.add(new SimpleMark(type, (Point)pts.get(j)));
            	}
            }
            return true;
        }
        
        if(tag.equals("LB") || tag.equals("XA")) { //$NON-NLS-1$ //$NON-NLS-2$
            for(int i = 0; i < propVals.size(); i++) {
                String [] t = ((String)propVals.get(i)).split(":"); //$NON-NLS-1$
                String label;
                if(t.length == 1)
                    label = String.valueOf(i + 1);
                else label = t[1];
                marks.add(new NodeLabel(label, SGFUtils.stringToPoint(t[0])));
            }
            return true;
        }
        
        if(tag.equals("L")) { //$NON-NLS-1$
        	int num = 0;
        	for(int i = 0; i < propVals.size(); i++) {
        		String prop = (String)propVals.get(i);
        		ArrayList pts = SGFUtils.stringToPoints(prop);
        		for(int j = 0; j < pts.size(); j++) {
            		char c = (char)('A' + num);
        			marks.add(new NodeLabel("" + c, (Point)pts.get(j))); //$NON-NLS-1$
        			num++;
        		}
        	}
        }
        
        if(tag.equals("LN") || tag.equals("LR") || tag.equals("LS")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            for(int i = 0; i < propVals.size(); i++) {
                String [] t = ((String)propVals.get(i)).split(":"); //$NON-NLS-1$
                marks.add(new LineMark(tag, SGFUtils.stringToPoint(t[0]), SGFUtils.stringToPoint(t[1])));
            }
            return true;
        }
        
        if(tag.equals("XX") || tag.equals("XY")) { //$NON-NLS-1$ //$NON-NLS-2$
            String label = tag.substring(1,2);
            for(int i = 0; i < propVals.size(); i++) {
                marks.add(new NodeLabel(label, SGFUtils.stringToPoint((String)propVals.get(i))));
            }
            return true;
        }
        
        return false;
    }
    
	public int compareTo(Object o) {
		NodeMark m = (NodeMark)o;
		
		//	Don't change order of local moves
		if((type == LOCALB || type == LOCALW) &&
				(m.type == LOCALB || m.type == LOCALW))
			return 0;
		
		if(type < m.type)
			return -1;
		else if(type > m.type)
			return 1;
		return 0;
	}
	
    public abstract void flip(int size, boolean flip, int rot);
    public abstract Object clone();
    public abstract boolean equals(Object o);
    public abstract void validatePoints(int size) throws SGFParseException;
    public abstract void invalidatePanel(GobanPanel panel);
    public abstract Rectangle getBounds();
    public abstract String getTag();
    public abstract void printBody(StringBuffer out);
}
