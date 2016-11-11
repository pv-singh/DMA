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

import GoGrinder.ui.GobanPanel;

/**
 *
 * @author  tkington
 */
public class LineMark extends NodeMark {
    private static final Stroke basicStroke = new BasicStroke(3);
    private static final Stroke dashedStroke = new BasicStroke(3, 
                                                               BasicStroke.CAP_SQUARE,
                                                               BasicStroke.JOIN_MITER,
                                                               10,
                                                               new float[] {10, 10},
                                                               0);
    private String tag;
    private Color color;
    private Point p1, p2;
    private Stroke stroke;
    
    /** Creates a new instance of LineMark */
    public LineMark(String tag, Point p1, Point p2) {
        type = LINE;
        this.tag = tag;
        if(tag.equals("LN")) //$NON-NLS-1$
            color = Color.blue;
        else if(tag.equals("LR")) //$NON-NLS-1$
            color = Color.red;
        
        if(tag.equals("LS")) { //$NON-NLS-1$
            color = Color.black;
            stroke = dashedStroke;
        }
        else stroke = basicStroke;
        
        
        this.p1 = p1;
        this.p2 = p2;
    }
    
    public Object clone() {
    	return new LineMark(tag, new Point(p1), new Point(p2));
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof LineMark))
            return false;
        
        LineMark m = (LineMark)o;
        if(!p1.equals(m.p1))
            return false;
        if(!p2.equals(m.p2))
            return false;
        if(!color.equals(m.color))
            return false;
        return true;
    }
    
    public Color getColor() { return color; }
    public Stroke getStroke() { return stroke; }
    public Point getP1() { return p1; }
    public Point getP2() { return p2; }
    
    public void flip(int size, boolean flip, int rot) {
        SGFUtils.flipPoint(size, p1, flip, rot);
        SGFUtils.flipPoint(size, p2, flip, rot);
    }
    
    public void validatePoints(int size) throws SGFParseException {
        SGFUtils.checkPoint(p1, size);
        SGFUtils.checkPoint(p2, size);
    }
    
    public void invalidatePanel(GobanPanel panel) {
        panel.repaint();
    }
    
    public Rectangle getBounds() {
        Rectangle r = new Rectangle(p1);
        r.add(p2);
        return r;
    }
    
    public String getTag() { return tag; }
    
    public void printBody(StringBuffer out) {
    	out.append("[" + SGFUtils.pointToString(p1) //$NON-NLS-1$
    				+ ":" + SGFUtils.pointToString(p2) + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
