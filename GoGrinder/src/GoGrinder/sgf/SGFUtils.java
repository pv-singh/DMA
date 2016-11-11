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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import GoGrinder.Messages;

public class SGFUtils {
    private static int charToNum(char c) {
        if(c >= 'a' && c <= 'z')
            return c - 'a';
        return c - 'A';
    }
    
    public static Point stringToPoint(String s) throws SGFParseException {
        if(s.length() != 2)
            throw new SGFParseException(Messages.getString("invalid_coords") + " \"" //$NON-NLS-1$ //$NON-NLS-2$
                                        + s + "\" " + Messages.getString("in_sgf_file")); //$NON-NLS-1$ //$NON-NLS-2$
        return new Point(charToNum(s.charAt(0)), charToNum(s.charAt(1)));
    }
    
    public static ArrayList stringToPoints(String s) throws SGFParseException {
    	ArrayList list = new ArrayList();
    	if(s.length() == 2) {
    		list.add(stringToPoint(s));
    		return list;
    	}
    	
    	if(s.length() != 5 || s.charAt(2) != ':')
            throw new SGFParseException(Messages.getString("invalid_coords") + " \"" //$NON-NLS-1$ //$NON-NLS-2$
                    + s + "\" " + Messages.getString("in_sgf_file")); //$NON-NLS-1$ //$NON-NLS-2$

    	Point p1 = stringToPoint(s.substring(0, 2));
    	Point p2 = stringToPoint(s.substring(3, 5));

    	int minx = Math.min(p1.x, p2.x);
    	int miny = Math.min(p1.y, p2.y);
    	int maxx = Math.max(p1.x, p2.x);
    	int maxy = Math.max(p1.y, p2.y);
    	
    	for(int x = minx; x <= maxx; x++) {
    		for(int y = miny; y <= maxy; y++) {
    			list.add(new Point(x,y));
    		}
    	}
    	
    	return list;
    }
    
    public static void checkPoint(Point p, int size) throws SGFParseException {
        if(p.x < 0 || p.x >= size || p.y < 0 || p.y >= size)
            throw new SGFParseException(Messages.getString("invalid_coords_in_sgf") //$NON-NLS-1$
                                        + " " + p.x + "," + p.y); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private static String numToString(int n) {
    	return Character.toString((char)('a' + n));
    }
    
    public static String pointToString(Point p) {
    	return numToString(p.x) + numToString(p.y);
    }
    
    public static void flipPoint(int size, Point p, boolean flip, int rot) {
        if(flip)
            p.x = size - p.x - 1;
        
        for(int i = 0; i < rot; i++) {
        	rotatePoint(size, p);
        }
    }
    
    public static void rotatePoint(int size, Point p) {
    	int oldY = p.y;
    	p.y = p.x;
    	p.x = size - oldY - 1;
    }
    
    public static void printMarks(ArrayList marks, StringBuffer out, boolean format) {
    	if(marks == null || marks.size() == 0)
    		return;
    	
    	if(format)
    		out.append("  "); //$NON-NLS-1$
    	
    	Collections.sort(marks);
    	String lastMark = ""; //$NON-NLS-1$
    	
    	for (Iterator iter = marks.iterator(); iter.hasNext();) {
			NodeMark m = (NodeMark) iter.next();
			
			String tag = m.getTag();
			if(!lastMark.equals(tag)) {
				if(format && lastMark.length() > 0)
					out.append("\n  "); //$NON-NLS-1$
				out.append(tag);
				lastMark = tag;
			}
			
			m.printBody(out);
		}
    	
    	if(format)
    		out.append("\n"); //$NON-NLS-1$
    }
}
