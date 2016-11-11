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

package GoGrinder.command;

import java.awt.*;
import java.util.*;

import GoGrinder.Board;
import GoGrinder.sgf.*;

/**
 *
 * @author  tkington
 */
public class CompositeCommand extends Command {
    private ArrayList cmds;
    
    /** Creates a new instance of CompositeCommand */
    public CompositeCommand() {
        cmds = new ArrayList();
    }
    
    public CompositeCommand(ArrayList cmds) {
        this.cmds = cmds;
    }
    
    public void add(Command c) { cmds.add(c); }
    public Command get(int i) { return (Command)cmds.get(i); }
    
    public boolean doIt(Board b) {
        for(int i = 0; i < cmds.size(); i++) {
            Command c = (Command)cmds.get(i);
            c.doIt(b);
        }
        return true;
    }
    
    public void undoIt(Board b) {
        for(int i = cmds.size() - 1; i >= 0; i--) {
            Command c = (Command)cmds.get(i);
            c.undoIt(b);
        }
    }
    
    public Rectangle getBounds() {
        Rectangle ret = null;
        for(int i = 0; i < cmds.size(); i++) {
            Command c = (Command)cmds.get(i);
            if(ret == null)
                ret = c.getBounds();
            else ret = ret.union(c.getBounds());
        }
        return ret;
    }
    
    public void flip(int size, boolean flip, int rot, boolean color) {
        for(int i = 0; i < cmds.size(); i++) {
            Command c = (Command)cmds.get(i);
            c.flip(size, flip, rot, color);
        }
    }
    
    public void validatePoints(int size) throws SGFParseException {
        for(int i = 0; i < cmds.size(); i++) {
            Command c = (Command)cmds.get(i);
            c.validatePoints(size);
        }
    }
    
    public void toFileFormat(StringBuffer out) { /* */ }
}
