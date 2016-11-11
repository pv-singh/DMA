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
import java.util.*;

import GoGrinder.*;
import GoGrinder.command.*;

/**
 *
 * @author  tkington
 *
 * This class corresponds to gametree in the SGF spec
 *
 */
public abstract class Node implements TagListener {
    static Random rand = new Random();
    
    protected Node parent;
    protected ArrayList ch;
    protected ArrayList cmds;
    protected HashMap props;
    
    protected int size;
    protected String comment;
    protected ArrayList marks;
    
    protected Node(Node parent) {
        this.parent = parent;
        ch = new ArrayList();
        cmds = new ArrayList();
        props = new HashMap();
        marks = new ArrayList();
    }
    
    protected void parse(String s) throws SGFParseException {
    	cmds.clear();
    	props.clear();
    	marks.clear();
    	comment = null;
    	size = 0;
    	
		try {
            SGFParser.parseTags(s, this);
        }
        catch(Exception e) {
            throw new SGFParseException(e.getMessage() + " sgf=" + s); //$NON-NLS-1$
        }
        
        String sz = getProperty("SZ"); //$NON-NLS-1$
        if(sz != null)
            size = Integer.parseInt(sz);
	}

	public ArrayList getPropertyList(String tag) {
        ArrayList l = (ArrayList)props.get(tag);
        if(l == null) {
            l = new ArrayList();
            props.put(tag, l);
        }
        /*else {
            if(!tag.equals("XS") && !tag.equals("YG") && !tag.equals("B") && //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            !tag.equals("W") && !tag.equals("YB") && !tag.equals("YW") && //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            !tag.equals("LB") && !tag.equals("XB") && !tag.equals("XW") && //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            !tag.equals("LS") && !tag.equals("LN") && !tag.equals("TW") && //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            !tag.equals("TB") && !tag.equals("XG") && !tag.equals("LR")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                printProp(tag); //System.out.println("Mult: " + tag);
        }*/
        
        return l;
    }
    
    public void addTag(String tag, ArrayList propVals) throws SGFParseException {
        if(tag.equals("C")) { //$NON-NLS-1$
            comment = (String)propVals.get(0);
        }
        else if(!NodeMark.create(marks, tag, propVals)) {
            Command cmd = Command.createCommand(tag, propVals);
            if(cmd != null)
                cmds.add(cmd);
        }
        
        getPropertyList(tag).addAll(propVals);
    }
    
    public void execute(Board board) {
        board.undoTempCommands();
        
        board.setMarks(marks);        
        CompositeCommand c = new CompositeCommand(cmds);
        board.executeCommand(c);
    }
    
    public void updateState(Board b) {
    	b.setMarks(marks);
    }
    
    public boolean hasAddCommand(int x, int y, int color) {
    	for (Iterator iter = cmds.iterator(); iter.hasNext();) {
			Object o = iter.next();
			if(o instanceof AddCommand) {
				AddCommand c = (AddCommand)o;
				if(c.getPlayer() == color &&
						c.hasPoint(x, y))
					return true;
			}
		}
    	
    	return false;
    }
    
    public void removeAddCommand(int x, int y, int color) {
    	for (Iterator iter = cmds.iterator(); iter.hasNext();) {
			Object o = iter.next();
			if(o instanceof AddCommand) {
				AddCommand c = (AddCommand)o;
				if(c.getPlayer() == color &&
						c.removePoint(x, y))
					return;
			}
		}
    }
    
    public void addAddCommand(int x, int y, int color) {
    	AddCommand c = null;
    	for (Iterator iter = cmds.iterator(); iter.hasNext();) {
			Object o = iter.next();
			if(o instanceof AddCommand) {
				AddCommand a = (AddCommand)o;
				if(a.getPlayer() == color) {
					c = a;
					break;
				}
			}
		}
    	
    	if(c == null) {
    		c = new AddCommand(color);
    		cmds.add(c);
    	}
    	
    	c.addPoint(new Point(x, y));
    }
    
    public boolean hasCommand(Command c) {
    	return cmds.contains(c);
    }
    
    public void addCommand(Command c) {
    	cmds.add(c);
    }
    
    public boolean removeCommand(Command c) { 
    	return cmds.remove(c);
    }
    
    public String getProperty(String p) {
        ArrayList l = (ArrayList)props.get(p);
        if(l == null)
            return null;
        
        return (String)l.get(0);
    }
    
    public void addChild(Node n) {
        ch.add(n);
    }
    
    public void validatePoints(int boardSize) throws SGFParseException {
        for(int i = 0; i < marks.size(); i++) {
            NodeMark m = (NodeMark)marks.get(i);
            m.validatePoints(boardSize);
        }
    
        for(int i = 0; i < cmds.size(); i++) {
            Command c = (Command)cmds.get(i);
            c.validatePoints(boardSize);
        }
    
        for(int i = 0; i < ch.size(); i++) {
            Node n = (Node)ch.get(i);
            n.validatePoints(boardSize);
        }
    }
    
    public Node getFirstChild() {
        if(ch.isEmpty())
            return null;
        return (Node)ch.get(0);
    }
    
    public void printProp(String prop) {
        System.out.println(prop);
        ArrayList p = (ArrayList)props.get(prop);
        if(p == null) {
            System.out.println(":null"); //$NON-NLS-1$
            return;
        }
        
        for(int i = 0; i < p.size(); i++) {
            System.out.println("[" + p.get(i) + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    public SimpleMark getSimpleMark(int x, int y, int type) {
        for(int i = 0; i < marks.size(); i++) {
            Object o = marks.get(i);
            if(o instanceof SimpleMark) {
            	SimpleMark m = (SimpleMark)o;
	            Point p = m.getPoint();
	            if(p.x == x && p.y == y && m.getType() == type) {
	                return m;
	            }
            }
        }
        
        return null;
    }
    
	protected SimpleMark getMarkAt(Point p, boolean ignoreStoneMarks) {
	    for(int j = 0; j < marks.size(); j++) {
	        NodeMark m = (NodeMark)marks.get(j);
	        if(m instanceof SimpleMark) {
	        	SimpleMark s = (SimpleMark)m;
	        	if(p.equals(s.getPoint()) && (s.getType() > 3 || !ignoreStoneMarks))
	        		return (SimpleMark)m;
	        }
	    }
	    return null;
	}
    
    public String getNextLabel() {
    	for(int j = -1; j < 26; j++) {
    		String prefix = ""; //$NON-NLS-1$
    		if(j != -1) {
    			char c = (char)('A' + j);
    			prefix += c;
    		}
    		
	    	for(int i = 0; i < 26; i++) {
	    		char c = (char)('A' + i);
	    		String l = prefix + c;
	    		
	    		boolean found = false;
	    		for (Iterator iter = marks.iterator(); iter.hasNext();) {
					Object o = iter.next();
					if(o instanceof NodeLabel) {
						NodeLabel label = (NodeLabel)o;
						if(l.equals(label.getText())) {
							found = true;
							break;
						}
					}
				}
	    		
	    		if(!found)
	    			return l;
	    	}
    	}
    	
    	return "You must be joking"; //$NON-NLS-1$
    }
    
    public String getNextNumberLabel() {
    	for(int i = 1; i < 500; i++) {
    		String l = String.valueOf(i);
    		
    		boolean found = false;
    		for (Iterator iter = marks.iterator(); iter.hasNext();) {
				Object o = iter.next();
				if(o instanceof NodeLabel) {
					NodeLabel label = (NodeLabel)o;
					if(l.equals(label.getText())) {
						found = true;
						break;
					}
				}
			}
    		
    		if(!found)
    			return l;
    	}
    	
    	return null;
    }
    
    public boolean hasStoneMarkAt(int x, int y) {
        for(int i = 0; i < marks.size(); i++) {
            Object o = marks.get(i);
            if(o instanceof SimpleMark) {
            	SimpleMark m = (SimpleMark)o;
	            Point p = m.getPoint();
	            if(p.x == x && p.y == y) {
	            	switch(m.getType()) {
	            	case NodeMark.FAKEB:
	            	case NodeMark.FAKEW:
	            	case NodeMark.GREYSTONE:
	            	case NodeMark.LOCALB:
	            	case NodeMark.LOCALW:
	            		return true;
	            	}
	            }
            }
        }
        
        return false;    	
    }
    
    public void addMark(NodeMark m) {
    	marks.add(m);
    	if(m instanceof SimpleMark) {
    		SimpleMark s = (SimpleMark)m;
    		if(s.getType() < 4)
    			Collections.sort(marks);
    	}
    }
    
    public void removeMark(NodeMark m) { marks.remove(m); }
    
    public Node getParent() { return parent; }
    public void setParent(Node n) { parent = n; }
    public String getComment() { return comment; }
    public void setComment(String c) { comment = c; }
    public int getSize() { return size; }
}
