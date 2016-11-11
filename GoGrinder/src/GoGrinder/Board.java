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

package GoGrinder;

import java.awt.*;
import java.util.*;

import GoGrinder.ui.GobanPanel;
import GoGrinder.command.*;
import GoGrinder.sgf.*;

/**
 *
 * @author tkington
 */
public class Board
{
    static int [][] offsets = {{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
    
    private int [][] board;
    private int size;
    private ArrayList marks = new ArrayList();
    private UndoController undo;
    private GobanPanel panel;
    
    private Point ko = new Point(-1, -1);
    
    private ArrayList goodMoves = new ArrayList();
    private ArrayList badMoves = new ArrayList();
    
    private int numTempCommands = 0;
    
    private int gridType = -1;
    private boolean ignoreMoves;
    
    public Board(int s)
    {
        size = s;
        board = new int[size][size];
        undo = new UndoController();
    }
    
    public Board(Board b) {
        size = b.size;
        board = new int[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                board[i][j] = b.board[i][j];
            }
        }
    }
    
    public void setPanel(GobanPanel p) { panel = p; }
    
    public void executeCommand(Command c) {
        undo.doCommand(c, this);
        panel.repaint();
    }
    
    public void undoLast() {
        undoTempCommands();
        
        undo.undo(this);
        panel.repaint();
    }
    
    public void executeTempCommand(Command c) {
        undo.doCommand(c, this);
        numTempCommands++;
        panel.repaint();
    }
    
    public void undoTempCommand() {
        undo.undo(this);
        numTempCommands--;
        panel.repaint();
    }
    
    public void undoTempCommands() {
        for(int i = 0; i < numTempCommands; i++)
            undo.undo(this);
        numTempCommands = 0;
    }
    
    public boolean isLegalMove(int color, int x, int y) {
        if(x < 0 || x >= size || y < 0 || y >= size || board[x][y] != 0)
            return false;
        
        if(x == ko.x && y == ko.y)
            return false;
        
        try {
            board[x][y] = color;
            
            if(countLiberties(x, y) > 0)
                return true;
            
            for(int i = 0; i < offsets.length; i++) {
                int nx = x + offsets[i][0];
                int ny = y + offsets[i][1];
                if(nx < 0 || nx >= size || ny < 0 || ny >= size)
                    continue;
                
                if(board[nx][ny] == color * -1 && countLiberties(nx, ny) == 0)
                    return true;
            }
            
            return false;
        }
        finally {
            board[x][y] = 0;
        }
    }
    
    public void move(int color, int x, int y, ArrayList removedStones) {
        if(x < 0 || x >= size || y < 0 || y >= size)
            return;
        
        board[x][y] = color;
        
        for(int i = 0; i < offsets.length; i++) {
            int nx = x + offsets[i][0];
            int ny = y + offsets[i][1];
            if(nx < 0 || nx >= size || ny < 0 || ny >= size)
                continue;
            
            if(board[nx][ny] == color * -1 && countLiberties(nx, ny) == 0) {
                removeGroup(nx, ny, removedStones);
            }
        }
        
        if(removedStones.size() == 1 && isAlone(x, y) && countLiberties(x, y) == 1) {
            setKoPoint(x, y);
        }
        else ko.x = -1;
    }
    
    private void removeGroup(int x, int y, ArrayList removedStones) {
        int color = board[x][y];
        
        LinkedList q = new LinkedList();
        q.add(new Point(x, y));
        
        while(!q.isEmpty()) {
            Point p = (Point)q.removeFirst();
            
            for(int i = 0; i < offsets.length; i++) {
                int nx = p.x + offsets[i][0];
                int ny = p.y + offsets[i][1];
                if(nx < 0 || nx >= size || ny < 0 || ny >= size)
                    continue;
                
                if(board[nx][ny] == color)
                    q.addLast(new Point(nx, ny));
            }
            
            removedStones.add(new Point(p.x, p.y));
            board[p.x][p.y] = 0;
        }
    }
    
    public boolean isAlone(int x, int y) {
        int color = board[x][y];
        
        for(int i = 0; i < offsets.length; i++) {
            int nx = x + offsets[i][0];
            int ny = y + offsets[i][1];
            if(nx < 0 || nx >= size || ny < 0 || ny >= size)
                continue;
            
            if(board[nx][ny] == color)
                return false;
        }
        
        return true;
    }
    
    public void setKoPoint(int x, int y) {
        for(int i = 0; i < offsets.length; i++) {
            int nx = x + offsets[i][0];
            int ny = y + offsets[i][1];
            if(nx < 0 || nx >= size || ny < 0 || ny >= size)
                continue;
            
            if(board[nx][ny] == 0) {
                ko.x = nx;
                ko.y = ny;
                return;
            }       
        }
        
        Main.log(new Exception());
    }
    
    public int countLiberties(int x, int y) {
        int color = board[x][y];
        boolean [][] used = new boolean[size][size];
        
        LinkedList q = new LinkedList();
        q.add(new Point(x, y));
        
        int num = 0;
        while(!q.isEmpty()) {
            Point p = (Point)q.removeFirst();
            used[p.x][p.y] = true;
            
            for(int i = 0; i < offsets.length; i++) {
                int nx = p.x + offsets[i][0];
                int ny = p.y + offsets[i][1];
                if(nx < 0 || nx >= size || ny < 0 || ny >= size)
                    continue;
                
                if(board[nx][ny] == color && !used[nx][ny])
                    q.addLast(new Point(nx, ny));
                else if(board[nx][ny] == 0 && !used[nx][ny]) {
                    num++;
                    used[nx][ny] = true;
                }
            }
        }
        
        return num;
    }
    
    public int remove(int x, int y) {
        int old = board[x][y];
        board[x][y] = 0;
        return old;
    }
    
    public void addMark(NodeMark m) {
        marks.add(m);
        m.invalidatePanel(panel);
    	
        if(m instanceof SimpleMark) {
    		SimpleMark s = (SimpleMark)m;
    		if(s.getType() < 4)
    			Collections.sort(marks);
    	}
    }
    
    public void removeMark(NodeMark m) {
        if(marks.remove(m))
            m.invalidatePanel(panel);
    }
    
    public boolean hasLabelAt(int x, int y) {
        for(int k = 0; k < marks.size(); k++) {
            NodeMark m = (NodeMark)marks.get(k);
            if(m.getType() == NodeMark.LABEL) {
                NodeLabel lab = (NodeLabel)m;
                Point p = lab.getPoint();
                if(p.x == x && p.y == y)
                    return true;
            }
        }
        return false;
    }
    
    public boolean isEmptyAt(int x, int y) {
        for(int i = 0; i < marks.size(); i++) {
            NodeMark m = (NodeMark)marks.get(i);
            if(!(m instanceof SimpleMark))
                continue;
            
            Point p = ((SimpleMark)m).getPoint();
            if(p.x == x && p.y == y && m.getType() == NodeMark.EMPTY)
                return true;
        }
        return false;
    }
    
    public boolean isBlackAt(int x, int y) {
        if(board[x][y] == 1 && !ignoreMoves)
            return true;
        
        for(int i = 0; i < marks.size(); i++) {
            NodeMark m = (NodeMark)marks.get(i);
            if(!(m instanceof SimpleMark))
                continue;
            
            Point p = ((SimpleMark)m).getPoint();
            if(p.x == x && p.y == y) {
                switch(m.getType()) {
                    case NodeMark.FAKEB:
                    case NodeMark.LOCALB:
                        return true;
                }
            }
        }
        
        return false;
    }
    
    public int getAt(int x, int y) {
        if(ignoreMoves)
            return 0;
        return board[x][y];
    }
    
    public void addStone(int p, int x, int y) { board[x][y] = p; }
    
    public int getSize() { return size; }
    public void setGoodMoves(ArrayList g) { goodMoves = g; }
    public ArrayList getGoodMoves() { return goodMoves; }
    public void setBadMoves(ArrayList b) { badMoves = b; }
    public ArrayList getBadMoves() { return badMoves; }
    public void setMarks(ArrayList m) { marks = (ArrayList)m.clone(); }
    public ArrayList getMarks() { return marks; }
    public Point getKoPoint() { return ko; }
    public void setKoPoint(Point p) { ko.x = p.x; ko.y = p.y; }
    public void setGridType(int n) { gridType = n; }
    public int getGridType() { return gridType; }
    public void setIgnoreMoves(boolean b) { ignoreMoves = b; }
}
