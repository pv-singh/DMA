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
import GoGrinder.sgf.SGFParseException;

/**
 *
 * @author  tkington
 */
public abstract class Command {
    public abstract boolean doIt(Board b);
    public abstract void undoIt(Board b);
    public abstract Rectangle getBounds();
    public abstract void flip(int size, boolean flip, int rot, boolean color);
    public abstract void validatePoints(int size) throws SGFParseException;
    public abstract void toFileFormat(StringBuffer out);
    
    public static Command createCommand(String tag, ArrayList props) throws SGFParseException {
        if(tag.equals("B")) //$NON-NLS-1$
            return new MoveCommand(1, props);
        if(tag.equals("W")) //$NON-NLS-1$
            return new MoveCommand(-1, props);
        if(tag.equals("AB")) //$NON-NLS-1$
            return new AddCommand(1, props);
        if(tag.equals("AW")) //$NON-NLS-1$
            return new AddCommand(-1, props);
        //System.out.println("Unrecognized tag: " + tag);
        return null;
    }
}
