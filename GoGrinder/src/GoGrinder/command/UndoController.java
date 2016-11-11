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

import GoGrinder.Board;
import java.util.*;
import java.awt.Toolkit;

public class UndoController
{
    private LinkedList undoStack;   //  Commands that can be undone

    /** Creates new UndoController */
    public UndoController()
    {
        undoStack = new LinkedList();
    }

    public void clear()
    {
        undoStack.clear();
    }

    public boolean doCommand(Command cmd, Board b)
    {
        //  Execute command
        boolean success = cmd.doIt(b);

        //  If successful, it can be undone
        if(success)
            undoStack.addFirst(cmd);
        return success;
    }

    public void undo(Board b)
    {
        int size = undoStack.size();

        //  If there's something to undo, undo it
        if(size > 0)
        {
            Command c = (Command)undoStack.removeFirst();
            c.undoIt(b);
        }
        else Toolkit.getDefaultToolkit().beep();
    }
}
