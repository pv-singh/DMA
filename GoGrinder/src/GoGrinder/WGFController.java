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

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import GoGrinder.sgf.*;
import GoGrinder.tests.Test;
import GoGrinder.ui.WGFFrame;
import GoGrinder.command.*;
import GoGrinder.editor.EditTool;
import GoGrinder.editor.FakeTool;
import GoGrinder.editor.LabelTool;
import GoGrinder.editor.LetterTool;
import GoGrinder.editor.LocalMoveTool;
import GoGrinder.editor.MarkTool;
import GoGrinder.editor.MoveTool;
import GoGrinder.editor.NumberTool;
import GoGrinder.editor.RealTool;
import GoGrinder.editor.TerritoryTool;

/**
 *
 * @author  tkington
 */
public class WGFController extends Controller implements HyperlinkListener {
	public static final int TEACH_MODE = 0;
	public static final int EDIT_MODE = 1;

	public static final int TEXTMODE_VIEWHTML = 0;
	public static final int TEXTMODE_EDITHTML = 1;
	public static final int TEXTMODE_EDITWGF = 2;

	public static EditTool TOOL_REAL;
	public static EditTool TOOL_FAKE;
	public static EditTool TOOL_MOVE;
	public static EditTool TOOL_LOCALMOVE;
	public static EditTool TOOL_TRI;
	public static EditTool TOOL_CIR;
	public static EditTool TOOL_SQU;
	public static EditTool TOOL_X;
	public static EditTool TOOL_LETTER;
	public static EditTool TOOL_NUMBER;
	public static EditTool TOOL_LABEL;
	public static EditTool TOOL_TERRITORY;

	private WGFFrame mainFrame;
    private int mode;
    private File curFile;
    private WGFNode curNode;
    private ArrayList nodes;
    private JEditorPane textPane;
    private JButton stepButton;
    private HashMap nodeMap;
    private Stack undoStack;
    private ArrayList globalReasons;

    private ArrayList stepCommands;
    private int stepNum;
    
    private boolean isDirty;
    private EditTool editTool;
    private int textPaneMode;
    
    public WGFController(WGFFrame mainFrame) {
    	this.mainFrame = mainFrame;
    	isDirty = false;
    	mode = TEACH_MODE;
        undoStack = new Stack();

        TOOL_FAKE = new FakeTool(this);
        TOOL_REAL = new RealTool(this);
        TOOL_MOVE = new MoveTool(this);
        TOOL_LOCALMOVE = new LocalMoveTool(this);
        TOOL_TRI = new MarkTool(this, NodeMark.TRI);
        TOOL_CIR = new MarkTool(this, NodeMark.CIR);
        TOOL_SQU = new MarkTool(this, NodeMark.SQU);
        TOOL_X = new MarkTool(this, NodeMark.X);
        TOOL_LETTER = new LetterTool(this);
        TOOL_NUMBER = new NumberTool(this);
        TOOL_LABEL = new LabelTool(this);
        TOOL_TERRITORY = new TerritoryTool(this);
        
        editTool = TOOL_FAKE;
    }
    
    public void setControls(JEditorPane textPane, JButton stepButton) {
        this.textPane = textPane;
        this.stepButton = stepButton;
    }
    
    public void onNewFile() {
    	if(!saveChanges())
    		return;

    	String str = JOptionPane.showInputDialog(mainFrame, Messages.getString("WGFController.BoardSize")); //$NON-NLS-1$
    	int size = 0;
    	try {
    		size = Integer.parseInt(str);
    	}
    	catch(NumberFormatException e) {
    		return;
    	}
    	if(size < 5 || size > 19) {
    		JOptionPane.showMessageDialog(mainFrame, Messages.getString("WGFController.LegalSizes5To19")); //$NON-NLS-1$
    		return;
    	}
    		
    	setFile(null);
    	
    	nodes = new ArrayList();
    	
    	nodeMap = new HashMap();
    	undoStack = new Stack();
    	
    	try {
    		curNode = new WGFNode("SZ[" + size //$NON-NLS-1$
    							+ "]C[<html></html>]", null); //$NON-NLS-1$
    	}
    	catch(SGFParseException e) { Main.log(e); }
    	
    	nodes.add(curNode);
    	globalReasons = null;
    	
    	curNode.registerNode(nodeMap);
    	
    	createBoard(-1);
    	
    	curNode.execute(board);
    	fillTextPane();
    	
    	panel.hideGhost();
    	
    	setDirty(false);
    }
    
    public void openFile(File f) throws SGFParseException, IOException {
    	if(!saveChanges())
    		return;
    	
        setFile(f);
        
        nodes = WGFParser.parse(f);
        
        nodeMap = new HashMap();
        undoStack = new Stack();
        
        curNode = (WGFNode)nodes.get(0);
        globalReasons = curNode.getReasons();

        for(int i = 0; i < nodes.size(); i++)
            ((WGFNode)nodes.get(i)).registerNode(nodeMap);
        
        createBoard(-1);
        
        curNode.execute(board);
        fillTextPane();
        
        panel.hideGhost();
        
        setDirty(false);
    }
    
    public boolean saveChanges() {
    	if(textPaneMode != TEXTMODE_VIEWHTML) {
    		if(!setTextPaneMode(TEXTMODE_VIEWHTML)) {
    			JOptionPane.showMessageDialog(mainFrame, Messages.getString("WGFController.SaveFailed")); //$NON-NLS-1$
    			return false;
    		}
    	}
    	
    	if(isDirty) {
    		String filename = Messages.getString("WGFController.TheCurrentFile"); //$NON-NLS-1$
    		if(curFile != null)
    			filename = curFile.getName();
    		
    		int choice = JOptionPane.showConfirmDialog(mainFrame, filename
    				+ Messages.getString("WGFController.HasChangedSaveChanges"), Messages.getString("WGFController.ConfirmSave"), //$NON-NLS-1$ //$NON-NLS-2$
					JOptionPane.YES_NO_CANCEL_OPTION);
    		if(choice == JOptionPane.CANCEL_OPTION)
    			return false;
    		if(choice == JOptionPane.YES_OPTION) {
    			if(!saveFile(curFile))
    				return false;
    		}
    		
    		setDirty(false);
    	}
    	
    	return true;
    }
    
    private void createBoard(int oldSize) {
        int size = curNode.getSize();
        if(size == 0) {
            if(oldSize != -1)
                size = oldSize;
            else size = 19;
        }

        setBoard(new Board(size));
    }
    
    public void setBoard(Board b) {
        board = b;
        panel.setBoard(board);
        board.setPanel(panel);

        int size = board.getSize();
        panel.setSize(new Rectangle(size, size), null);
    }
    
    public void mouseClicked(int x, int y, int modifiers) {
    	switch(mode) {
    	case TEACH_MODE: normalMouseClicked(x, y, modifiers); break;
    	case EDIT_MODE: editMouseClicked(x, y, modifiers); break;
    	}
    }
    
    public void mouseWheelMoved(int numClicks) { /* */ }
    
    private void normalMouseClicked(int x, int y, int modifiers) {
        Test t = curNode.getTest();
        if(t == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        t.handleClick(board, curNode, globalReasons, x, y, modifiers);
    }
    
    private void editMouseClicked(int x, int y, int modifiers) {
    	if(editTool.mouseClicked(x, y, modifiers))
    		setDirty(true);
    	else Toolkit.getDefaultToolkit().beep();
    }
    
    public void home() {
    	if(!updateNodeFromTextPane())
    		return;
    	
        undoStack = new Stack();
        
        curNode = (WGFNode)nodes.get(0);
        
        createBoard(-1);
        
        curNode.execute(board);
        fillTextPane();
    }
    
    public void prev() {
    	if(!updateNodeFromTextPane())
    		return;
    	
        if(undoStack.isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        UndoEntry e = (UndoEntry)undoStack.pop();
        if(e.board == null) {
            for(int i = 0; i < e.numUndos; i++) {
                curNode = (WGFNode)curNode.getParent();
                board.undoLast();
            }
            
            curNode.updateState(board);
            fillTextPane();
        }
        else {
            board = e.board;
            setBoard(board);
            curNode = e.prevNode;
            curNode.updateState(board);
            fillTextPane();
        }
        
        enableStep();
    }
    
    public void next() {
    	if(!updateNodeFromTextPane())
    		return;
    	
        //  If we need to show the answer to a test before
        //  moving on, do so
        if(curNode.hasAnswer()) {
        	curNode.showAnswer(board);
            return;
        }
        
        String nextLink = curNode.getNextLink();
        if(nextLink == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        WGFNode next;
        if(nextLink.equals("NEXTNODE")) //$NON-NLS-1$
            next = (WGFNode)curNode.getFirstChild();
        else next = (WGFNode)nodeMap.get(nextLink);
        
        goToNode(next, true);
    }
    
    public void backToParent() {
        UndoEntry e = (UndoEntry)undoStack.peek();
        
        curNode = (WGFNode)curNode.getParent();
        board.undoLast();

        e.numUndos--;
        if(e.numUndos == 0)
            undoStack.pop();

        curNode.updateState(board);
        fillTextPane();
    }
    
    public void goToNode(WGFNode next, boolean updateState) {
    	if(next == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        if(next == curNode.getFirstChild()) {
            undoStack.push(new UndoEntry(1, null, null, null));
            curNode = next;
            curNode.execute(board);
            if(updateState)
            	fillTextPane();
        }
        else if(isDescendant(curNode, next)) {
            int num = 0;
            while(curNode != next) {
                curNode = (WGFNode)curNode.getFirstChild();
                curNode.execute(board);
                if(updateState)
                	fillTextPane();
                num++;
            }

            undoStack.push(new UndoEntry(num, null, null, null));
        }
        else if(isDescendant(next, curNode)) {
            while(curNode != next)
                backToParent();
        }
        else {
            //  Next node is unrelated
            undoStack.push(new UndoEntry(-1, curNode, next, board));
            curNode = next;
            createBoard(board.getSize());
            curNode.execute(board);
            if(updateState)
            	fillTextPane();
        }
        
        enableStep();
    }
    
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            String desc = e.getDescription();
            
            if(desc.equals("NEXTNODE")) { //$NON-NLS-1$
                next();
                return;
            }
            
            WGFNode next = null;
            
            String [] t = desc.split("\\:"); //$NON-NLS-1$
            switch(t.length) {
                case 1:
                    next = (WGFNode)nodeMap.get(t[0]);
                    break;
                case 2:
                    next = (WGFNode)nodeMap.get(t[1]);
                    break;
                default:
                    Toolkit.getDefaultToolkit().beep();
                    return;
            }
            
            if(!updateNodeFromTextPane())
            	return;
            goToNode(next, true);
        }
    }
    
    public void enableStep() {
        stepCommands = curNode.getStepCommands(board);
        
        if(stepCommands != null && stepCommands.size() > 1) {
            stepNum = stepCommands.size();
            if(mode != EDIT_MODE)
            	stepButton.setEnabled(true);
        }
        else {
            stepNum = -1;
            stepCommands = null;
            stepButton.setEnabled(false);
        }
    }
    
    public void step() {
        if(stepNum == stepCommands.size()) {
            for(int i = stepNum - 1; i > 0; i--)
                board.executeTempCommand((Command)stepCommands.get(i));
            stepNum = 1;
        }
        else {
            board.undoTempCommand();
            stepNum++;
        }
    }
    
    public void saveHistory() {
        LinkedList hist = new LinkedList();
        while(!undoStack.isEmpty()) {
            UndoEntry e = (UndoEntry)undoStack.pop();
            if(e.numUndos != -1)
                hist.addFirst(new Integer(e.numUndos));
            else hist.addFirst(e.nextNode.getName());
        }
        
        hist.addFirst(curFile);
        
        GS.setHistory(hist);
    }
    
    public void loadHistory() {
        LinkedList hist = GS.getHistory();
        if(hist == null)
            return;
        
        File f = (File)hist.removeFirst();
        if(f == null)
        	return;
        
        try {
            openFile(f);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(mainFrame, Messages.getString("WGFController.ErrWhileReadingFile") //$NON-NLS-1$
                                                + f.getName() + Messages.getString("WGFController.SeeGrindLog")); //$NON-NLS-1$
            Main.logSilent(e);
        }
        
        while(!hist.isEmpty()) {
            Object o = hist.removeFirst();
            if(o instanceof Integer) {
                int n = ((Integer)o).intValue();
                WGFNode node = curNode;
                for(int i = 0; i < n; i++) {
                    node = (WGFNode)node.getFirstChild();
                }
                goToNode(node, false);
            }
            else {
                String name = (String)o;
                WGFNode node = (WGFNode)nodeMap.get(name);
                goToNode(node, false);
            }
        }
        curNode.updateState(board);
        fillTextPane();
        enableStep();
    }
    
    private boolean isDescendant(WGFNode parent, WGFNode d) {
        while(d != null && d != parent)
            d = (WGFNode)d.getParent();

        return d == parent;
    }
    
    public boolean saveFile() {
    	if(curFile == null) 
    		return saveFileAs();
    	return saveFile(curFile);
    }
    
    public boolean saveFileAs() {
		String filename = Main.chooseFile(mainFrame, Main.wgfFilter, Messages
				.getString("WGFFrame.SaveFile"), //$NON-NLS-1$
				true, Messages.getString("WGFFrame.Save")); //$NON-NLS-1$
		if (filename == null)
			return false;
		
		curFile = new File(filename);
    	
    	return saveFile(curFile);
    }

    private boolean saveFile(File f) {
    	if(textPaneMode != TEXTMODE_VIEWHTML) {
    		if(!setTextPaneMode(TEXTMODE_VIEWHTML)) {
    			JOptionPane.showMessageDialog(mainFrame, Messages.getString("WGFController.SaveFailed")); //$NON-NLS-1$
    			return false;
    		}
    	}
    	
    	try {
	        PrintWriter out = new PrintWriter(new FileOutputStream(f));
	        
	        for(int i = 0; i < nodes.size(); i++) {
	        	out.println("("); //$NON-NLS-1$
	        	WGFNode node = (WGFNode)nodes.get(i);
	        	StringBuffer s = new StringBuffer();
	        	node.toFileFormat(s);
	        	out.print(s);
	        	out.println(")"); //$NON-NLS-1$
	        }
	    	
	        out.close();
	        
	        setFile(f);
	        setDirty(false);
	        
	        return true;
    	}
        catch(Exception e) {
            JOptionPane.showMessageDialog(mainFrame,
            		Messages.getString("WGFFrame.ErrSavingSeeGrindLog")); //$NON-NLS-1$
            Main.logSilent(e);
            return false;
        }
    }
    
    public int getMode() { return mode; }
    
    public void setEditMode(boolean m) {
    	mode = m ? EDIT_MODE : TEACH_MODE;
    	if(m)
    		board.undoTempCommands();
    	else enableStep();
    }
    
    public void insertNode() {
    	WGFNode node = null;
    	try {
    		node = new WGFNode("", null); //$NON-NLS-1$
    	}
    	catch(SGFParseException e) {
    		Main.log(e);
    	}
    	insertNode(node);
    }
    
    public void insertNodeCopy() {
    	insertNode(curNode.createCopy());
    }
    
    private void insertNode(WGFNode node) {
    	if(!updateNodeFromTextPane())
    		return;
    	curNode.insertNodeAfter(node);
    	goToNode(node, true);
    	setDirty(true);
    }
    
    public boolean hasAddCommand(int x, int y, int color) {
    	return curNode.hasAddCommand(x, y, color);
    }
    
    public void addAddCommand(int x, int y, int color) {
    	board.undoLast();
    	curNode.addAddCommand(x, y, color);
    	curNode.execute(board);
    }
    
    public void removeAddCommand(int x, int y, int color) {
    	board.undoLast();
    	curNode.removeAddCommand(x, y, color);
    	curNode.execute(board);
    }
    
    public boolean hasCommand(Command c) {
    	return curNode.hasCommand(c);
    }
    
    public void addCommand(Command c) {
    	board.undoLast();
    	curNode.addCommand(c);
    	curNode.execute(board);
    	enableStep();
    }
    
    public void removeCommand(Command c) {
    	board.undoLast();
    	curNode.removeCommand(c);
    	curNode.execute(board);
    	enableStep();
    }
    
    public SimpleMark getSimpleMark(int x, int y, int type) {
    	return curNode.getSimpleMark(x, y, type);
    }
    
    public boolean hasStoneAt(int x, int y) {
    	if(board.getAt(x, y) != 0)
    		return true;
    	
    	return curNode.hasStoneMarkAt(x, y);
    }
    
    public void addMark(NodeMark m) {
    	curNode.addMark(m);
    	board.addMark(m);
    	setDirty(true);
    	enableStep();
    }
    
    public void removeMark(NodeMark m) {
    	curNode.removeMark(m);
    	board.removeMark(m);
    	setDirty(true);
    	enableStep();
    }
    
    public String getNextLabel() {
    	return curNode.getNextLabel();
    }
    
    public String getNextNumberLabel() {
    	return curNode.getNextNumberLabel();
    }
    
    public boolean setTextPaneMode(int mode) {
    	if(!updateNodeFromTextPane())
    		return false;
    	
    	textPaneMode = mode;
    	if(mode != TEXTMODE_VIEWHTML)
    		setDirty(true);
    	
    	String contentType = null;
    	switch(mode) {
    	case TEXTMODE_VIEWHTML:
    		contentType = "text/html"; //$NON-NLS-1$
    		break;
    	case TEXTMODE_EDITHTML:
    		contentType = "text/plain"; //$NON-NLS-1$
    		break;
    	case TEXTMODE_EDITWGF:
    		contentType = "text/plain"; //$NON-NLS-1$
    		break;
    	}
    	
    	textPane.setContentType(contentType);
    	textPane.setEditable(mode != TEXTMODE_VIEWHTML);
    	
    	fillTextPane();
    	return true;
    }
    
    private void fillTextPane() {
    	String text = null;
    	
    	switch(textPaneMode) {
    	case TEXTMODE_VIEWHTML:
    	case TEXTMODE_EDITHTML:
    		text = curNode.getComment();
    		break;
    	case TEXTMODE_EDITWGF:
    		StringBuffer wgf = new StringBuffer();
    		curNode.getLocalWGF(wgf);
    		text = wgf.toString();
    		break;
    	}
    	
    	textPane.setText(text);
    	textPane.setCaretPosition(0);
    }
    
    private boolean updateNodeFromTextPane() {
		switch(textPaneMode) {
		case TEXTMODE_EDITHTML:
			curNode.setComment(textPane.getText());
			break;
		case TEXTMODE_EDITWGF:
			//	Remove node from map in case name changes
			String name = curNode.getName();
			if(name != null)
				nodeMap.remove(name.toUpperCase());
			
	    	board.undoLast();
	    	
			boolean badWGF = false;
	    	try {
	    		curNode.parse(textPane.getText());
	    	}
	    	catch(SGFParseException e) {
	    		JOptionPane.showMessageDialog(mainFrame,
	    				Messages.getString("WGFController.ErrorParsingNodeSeeGrindLog")); //$NON-NLS-1$
	    		Main.logSilent(e);
	    		badWGF = true;
	    	}
	    	
	    	name = curNode.getName();
	    	if(name != null)
	    		nodeMap.put(name.toUpperCase(), curNode);
	    	
	    	curNode.execute(board);
	    	enableStep();
	    	
	    	if(badWGF)
	    		return false;
			break;
		}
		
		return true;
	}
    
    private void setDirty(boolean dirty) {
    	isDirty = dirty;
    	setTitle();
    }
    
    private void setFile(File f) {
    	curFile = f;
    	setTitle();
    }
    
    private void setTitle() {
    	String title = Messages.getString("WGFController.GoGrinderDash"); //$NON-NLS-1$
    	
    	if(curFile != null)
    		title += curFile.getName();
    	else title += Messages.getString("WGFController.Untitled"); //$NON-NLS-1$
    	
    	if(isDirty)
    		title += "*"; //$NON-NLS-1$
    	mainFrame.setTitle(title);
    }
    
    public void setEditTool(EditTool editTool) { this.editTool = editTool; }
    public JEditorPane getTextPane() { return textPane; }
	
	static class UndoEntry {
        //  If the user went forward, num contains the number of moves forward,
        //  and board and node are null.  If not, the user followed a hyperlink.
        Board board;
        WGFNode prevNode;
        WGFNode nextNode;
        int numUndos;
        
        UndoEntry(int num, WGFNode prevNode, WGFNode nextNode, Board b) {
            numUndos = num;
            this.prevNode = prevNode;
            this.nextNode = nextNode;
            board = b;
        }
    }
}
