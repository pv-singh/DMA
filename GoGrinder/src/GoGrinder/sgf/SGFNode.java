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

import GoGrinder.*;
import GoGrinder.command.*;

/**
 *
 * @author  tkington
 */
public class SGFNode extends Node {
    private boolean right;
    private boolean rightPath;
    private boolean fromFile;
    private Point pos;
    private int player;
    
    
    
    public SGFNode(SGFNode parent, int x, int y) {
        super(parent);
        fromFile = false;
        pos = new Point(x,y);
        player = parent.player * -1;
        cmds.add(new MoveCommand(player, pos));
        comment = ""; //$NON-NLS-1$
    }
    
    public SGFNode(String s, SGFNode parent) throws SGFParseException {
        super(parent);
        fromFile = true;
        
        parse(s);
    }
    
    public void parse(String s) throws SGFParseException {
    	super.parse(s);
    	
        Iterator iter = props.keySet().iterator();
        while(iter.hasNext()) {
            String tag = (String)iter.next();
            ArrayList propVals = (ArrayList)props.get(tag);
            
            if(tag.equals("B")) { //$NON-NLS-1$
                player = 1;
                pos = SGFUtils.stringToPoint((String)propVals.get(0));
            }
            else if(tag.equals("W")) { //$NON-NLS-1$
                player = -1;
                pos = SGFUtils.stringToPoint((String)propVals.get(0));
            }
            else if(tag.equals("AE")) { //$NON-NLS-1$
            	for(int i = 0; i < propVals.size(); i++) {
            		Point p = SGFUtils.stringToPoint((String)propVals.get(i));
            		for(int j = 0; j < cmds.size(); j++) {
            			Command c = (Command)cmds.get(j);
            			if(c instanceof AddCommand) {
            				AddCommand a = (AddCommand)c;
            				a.removePoint(p);
            			}
            		}
            	}
            }
        }
        
        if(comment != null) {
            right |= stripFromComment("RIGHT"); //$NON-NLS-1$
            right |= stripFromComment("CORRECT"); //$NON-NLS-1$
            stripFromComment("CHOICE"); //$NON-NLS-1$
        }
    }
    
    private boolean stripFromComment(String s) {
        int index = comment.indexOf(s);
        if(index == -1)
            return false;
        
        while(index != -1) {
            String beg = comment.substring(0, index);
            String end = comment.substring(index + s.length());
            if(index == 0)
                end = end.trim();
            
            comment = beg + end;
            index = comment.indexOf(s);
        }
        
        return true;
    }
    
    public boolean markPathsRIGHT() {
        boolean ret = false;
        
        if(right) {
            SGFNode n = this;
            while(n != null) {
                n.rightPath = true;
                n = (SGFNode)n.parent;
            }
            ret = true;
        }
        
        for(int i = 0; i < ch.size(); i++) {
            SGFNode n = (SGFNode)ch.get(i);
            ret |= n.markPathsRIGHT();
        }
        
        return ret;
    }
    
    public void markPathsWV() {
        if(parent == null) {
            rightPath = true;
        }
        else if(((SGFNode)parent).rightPath) {
            if(props.get("WV") == null) { //$NON-NLS-1$
                boolean foundTR = false;
                for(int i = 0; i < marks.size(); i++) {
                    NodeMark m = (NodeMark)marks.get(i);
                    if(m.getType() == NodeMark.TRI && pos != null 
                                && pos.equals(((SimpleMark)m).getPoint())) {
                            marks.remove(i);
                            foundTR = true;
                            break;
                    }
                }
                
                if(!foundTR) {
                    rightPath = true;
                    right = ch.isEmpty();
                }
            }
        }
        
        for(int i = 0; i < ch.size(); i++) {
            SGFNode n = (SGFNode)ch.get(i);
            n.markPathsWV();
        }
    }
    
    public Rectangle getBounds() {
        Rectangle r = null;
        for(int i = 0; i < cmds.size(); i++) {
            Command c = (Command)cmds.get(i);
            if(r == null)
                r = c.getBounds();
            else r = r.union(c.getBounds());
        }
        
        for(int i = 0; i < ch.size(); i++) {
            SGFNode n = (SGFNode)ch.get(i);
            Rectangle b = n.getBounds();
            if(b != null) {
                if(r == null)
                    r = n.getBounds();
                else r = r.union(b);
            }
        }
        
        for(int i = 0; i < marks.size(); i++) {
            NodeMark m = (NodeMark)marks.get(i);
            if(r == null)
            	r = m.getBounds();
            else r = r.union(m.getBounds());
        }
        
        return r;
    }
    
    public void getNavPts(ArrayList good, ArrayList bad) {
        for(int i = 0; i < ch.size(); i++) {
            SGFNode n = (SGFNode)ch.get(i);
            if(n.pos != null) {
                if(n.fromFile) {
                    if(n.rightPath)
                        good.add(n.pos);
                    else bad.add(n.pos);
                }
            }
        }
    }
    
    public void updateState(Board board) {
        super.updateState(board);
        
        ArrayList good = new ArrayList();
        ArrayList bad = new ArrayList();
        getNavPts(good, bad);
        board.setGoodMoves(good);
        board.setBadMoves(bad);
    }
    
    public int getNextPlayer() {
        for(int i = 0; i < ch.size(); i++) {
            SGFNode n = (SGFNode)ch.get(i);
            if(n.pos != null)
                return n.player;
        }
        return 1;
    }
    
    public SGFNode getNextNode(Point p) {
        for(int i = 0; i < ch.size(); i++) {
            SGFNode n = (SGFNode)ch.get(i);
            if(n.pos != null && n.pos.equals(p))
                return n;
        }
        return null;
    }
    
    public SGFNode getResponse() {
        int num = 0;
        for(int i = 0; i < ch.size(); i++) {
            SGFNode n = (SGFNode)ch.get(i);
            //  Don't leave the right path when responding
            //  Many problems from goproblems.com have this potential
            //  Also don't show nodes that were added by user in navsoln
            if(n.pos != null && n.fromFile && (n.rightPath || !rightPath))
                num++;
        }
        
        if(num == 0)
            return null;
        
        int r = rand.nextInt(num);
        
        num = 0;
        for(int i = 0; i < ch.size(); i++) {
            SGFNode n = (SGFNode)ch.get(i);
            if(n.pos != null && (n.rightPath || !rightPath)) {
                if(num == r)
                    return n;
                num++;
            }
        }
        
        throw new RuntimeException();
    }
    
    public void flip(int size, boolean flip, int rot, boolean color) {
        if(pos != null)
            SGFUtils.flipPoint(size, pos, flip, rot);
        
        for(int i = 0; i < cmds.size(); i++) {
            Command c = (Command)cmds.get(i);
            c.flip(size, flip, rot, color);
        }
        
        for(int i = 0; i < marks.size(); i++) {
            NodeMark m = (NodeMark)marks.get(i);
            m.flip(size, flip, rot);
        }
        
        for(int i = 0; i < ch.size(); i++) {
            SGFNode n = (SGFNode)ch.get(i);
            n.flip(size, flip, rot, color);
        }
        
        if(color) {
            player *= -1;
            if(comment != null)
                comment = flipComment(comment);
        }
    }
    
    public String flipComment(String s) {
        final String w = "white"; //$NON-NLS-1$
        final String b = "black"; //$NON-NLS-1$
        String r = ""; //$NON-NLS-1$
        
        for(int i = 0; i < s.length(); i++) {
            String sub = s.substring(i);
            if(sub.toLowerCase().startsWith("white")) { //$NON-NLS-1$
                for(int j = 0; j < 5; j++, i++) {
                    if(Character.isUpperCase(sub.charAt(j)))
                        r += Character.toUpperCase(b.charAt(j));
                    else r += b.charAt(j);
                }
            }
            else if(sub.toLowerCase().startsWith("black")) { //$NON-NLS-1$
                for(int j = 0; j < 5; j++, i++) {
                    if(Character.isUpperCase(sub.charAt(j)))
                        r += Character.toUpperCase(w.charAt(j));
                    else r += w.charAt(j);
                }
            }
            
            if(i < s.length())
                r += s.charAt(i);
        }
        
        return r;
    }
    
    public void validatePoints(int size) throws SGFParseException {
        if(pos != null)
            SGFUtils.checkPoint(pos, size);
        
        super.validatePoints(size);
    }
    
    public boolean hasChildMoves() {
        for(int i = 0; i < ch.size(); i++) {
            SGFNode n = (SGFNode)ch.get(i);
            if(n.pos != null)
                return true;
        }
        return false;
    }
    
    public boolean hasChildrenFromFile() {
        for(int i = 0; i < ch.size(); i++) {
            SGFNode n = (SGFNode)ch.get(i);
            if(n.fromFile)
                return true;
        }
        return false;
    }
    
    public boolean isRight() { return right; }
    public boolean isOnRightPath() { return rightPath; }
    public int getPlayer() { return player; }
    public void setPlayer(int p) { player = p; }
    public boolean isFromFile() { return fromFile; }
}
