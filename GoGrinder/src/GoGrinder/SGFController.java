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
import java.awt.event.*;
import javax.swing.*;

import GoGrinder.ui.*;
import GoGrinder.sgf.*;

/**
 *
 * @author  tkington
 */
public class SGFController extends Controller {
    public static final int PLAY = 0;
    public static final int NAV_SOLN = 1;
    
    private SGFNode root;
    private SGFNode curNode;
    private WidgetPanel widgetPanel;
    private JTextArea commentArea;
    private JLabel statusBar;
    private int mode;
    private int playerColor;
    private int curColor;
    
    private boolean enabled = true;
    
    private boolean hasResult;
    private boolean result;
    private boolean hasPlayedSound;
    
    public boolean startProblem(ProbData prob) {
        String filename = prob.getFile().getPath().substring(9);
        statusBar.setText(filename);
        
        try {
            ArrayList recs = SGFParser.parse(prob.getSGF());
            if(recs.size() > 1)
                JOptionPane.showMessageDialog(null, prob.getFile().getPath()
                                            + " " + Messages.getString("contains_mult_probs")); //$NON-NLS-1$ //$NON-NLS-2$
            root = (SGFNode)recs.get(0);
            
            int size = root.getSize();
            if(size != 0)
                board = new Board(size);
            else board = new Board(19);
            
            root.validatePoints(board.getSize());
        }
        catch(OutOfMemoryError e) {
            JOptionPane.showMessageDialog(null, Messages.getString("out_of_memory") + " " + filename); //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        }
        catch(SGFParseException e) {
            String msg = e.getMessage();
            if(msg.length() > 50)
                msg = msg.substring(0, 50) + Messages.getString("dot_dot_dot"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(panel, Messages.getString("err_parsing_sgf_skipping") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                                                + Messages.getString("file_colon") + " " + filename + "\n" + msg); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            Main.logSilent(e, filename);
            return false;
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(panel, Messages.getString("err_parsing_sgf_skipping") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                                                + Messages.getString("file_colon") + " " + filename); //$NON-NLS-1$ //$NON-NLS-2$
            Main.logSilent(e, filename);
            return false;
        }

        panel.setBoard(board);
        board.setPanel(panel);

        if(!root.markPathsRIGHT())
            root.markPathsWV();

        boolean doFlip = GS.getFlip();
        boolean doFlipColors = GS.getFlipColors();
        
        int n = Main.rand.nextInt(4);
        boolean flip = ((n & 1) == 1) && doFlip;
        boolean flipC = ((n & 2) == 2) && doFlipColors;
        int rot = doFlip ? Main.rand.nextInt(4) : 0;
        
        root.flip(board.getSize(), flip, rot, flipC);
        
        Rectangle bounds = root.getBounds();
        //  GobanPanel expects 1-based width & height
        bounds.width++;
        bounds.height++;
        panel.setSize(bounds, null);
        
        //  Process nodes until we come to one with moves as children
        root.execute(board);
        root.updateState(board);
        commentArea.setText(root.getComment());
        while(!root.hasChildMoves()) {
            SGFNode newRoot = (SGFNode)root.getFirstChild();
            if(newRoot == null)
                break;
            
            root = newRoot;
            root.setParent(null);
            root.execute(board);
            root.updateState(board);
            commentArea.setText(root.getComment());
        }
        
        setCurColor(playerColor = root.getNextPlayer());
        root.setPlayer(playerColor * -1);
        hasResult = false;
        result = false;
        hasPlayedSound = false;
        
        curNode = root;
        
        panel.hideGhost();
        return true;
    }
    
    public void mouseClicked(int x, int y, int modifiers)
    {
        if((modifiers & MouseEvent.BUTTON3_MASK) != 0) {
        	if(GS.getRightClickAdvance())
        		nextProblem();
            return;
        }
        
        switch(mode)
        {
            case PLAY:
                playMouseClicked(x, y);
                break;
            case NAV_SOLN:
                navSolnMouseClicked(x, y);
                break;
        }
    }
    
    public void mouseWheelMoved(int numClicks) {
    	numClicks *= -1;
    	for(int i = 0; i < numClicks; i++)
    		goBack();
    }
    
    private void playMouseClicked(int x, int y) {
        if(!enabled)
            return;
        
        if(!board.isLegalMove(curColor, x, y)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        SGFNode next = curNode.getNextNode(new Point(x,y));
        
        if(next == null) {
            next = new SGFNode(curNode, x, y);
            curNode.addChild(next);
        }
        
        doNode(next);
        
        SGFNode resp = next.getResponse();
        if(resp != null) {
            enabled = false;
            SwingUtilities.invokeLater(new Responder(resp));
        }
    }
    
    private void doNode(SGFNode n) {
        if(n.isRight()) {
            setResult(true);
        }
        else if(!n.isOnRightPath()) {
            setResult(false);
        }
        
        if(GS.getClickSoundEnabled())
            Main.clickSound.play();
        
        if(n.isRight()) {
            widgetPanel.setSolved(result, true);
            
            if(!hasPlayedSound && GS.getSoundEnabled()) {
                Main.rightSound.play();
                hasPlayedSound = true;
            }
        }
        else {
            if(GS.getShowWrongPath()) {
                if(!n.isOnRightPath()) {
                    widgetPanel.setSolved(result, false);
                    if(!hasPlayedSound && GS.getSoundEnabled()) {
                        Main.wrongSound.play();
                        hasPlayedSound = true;
                    }
                }
                else widgetPanel.clearSolved();
            }
            else {
                if(!n.hasChildrenFromFile()) {
                    widgetPanel.setSolved(result, false);
                    if(!hasPlayedSound && GS.getSoundEnabled()) {
                        Main.wrongSound.play();
                        hasPlayedSound = true;
                    }
                }
                else widgetPanel.clearSolved();
            }
        }

        n.execute(board);
        n.updateState(board);
        commentArea.setText(n.getComment());
        curNode = n;
        setCurColor(curColor *= -1);
        
        if(GS.getAutoAdv() && n.isRight() && mode == PLAY) {
            enabled = false;
            SwingUtilities.invokeLater(new AutoAdvancer());
        }
    }
    
    private void navSolnMouseClicked(int x, int y) {
        SGFNode next = curNode.getNextNode(new Point(x,y));
        
        if(next == null) {
            if(!board.isLegalMove(curNode.getPlayer() * -1, x, y)) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            next = new SGFNode(curNode, x, y);
            curNode.addChild(next);
        }
        
        doNode(next);
    }
    
    public void goBack()
    {
        do {
            SGFNode parent = (SGFNode)curNode.getParent();
            if(parent == null) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            board.undoLast();
            curNode = parent;
            setCurColor(curColor *= -1);
            curNode.updateState(board);
            commentArea.setText(curNode.getComment());
            
            if(GS.getShowWrongPath()) {
                if(!curNode.isOnRightPath())
                    widgetPanel.setSolved(result, false);
                else widgetPanel.clearSolved();
            }
            else widgetPanel.clearSolved();
            
            if(mode == NAV_SOLN || !curNode.isFromFile())
                break;
        } while(curNode.getPlayer() == playerColor);
    }
    
    public void restart()
    {
        mode = PLAY;
        panel.setNavMode(false);
        widgetPanel.clearSolved();
        SGFNode parent;
        while((parent = (SGFNode)curNode.getParent()) != null) {
            board.undoLast();
            curNode = parent;
            setCurColor(curColor *= -1);
            curNode.updateState(board);
            commentArea.setText(curNode.getComment());
        }
    }
    
    public void navigateSolution() {
        mode = NAV_SOLN;
        panel.setNavMode(true);
        setResult(false);
        
        widgetPanel.clearSolved();
        SGFNode parent;
        while((parent = (SGFNode)curNode.getParent()) != null) {
            board.undoLast();
            curNode = parent;
            setCurColor(curColor *= -1);
            curNode.updateState(board);
            commentArea.setText(curNode.getComment());
        }
    }
    
    public void setCommentArea(JTextArea a) { commentArea = a; }
    public void setWidgetPanel(WidgetPanel p) { widgetPanel = p; }
    public void setStatusBar(JLabel s) { statusBar = s; }
    
    public void nextProblem() {
        mode = PLAY;
        panel.setNavMode(false);
        widgetPanel.clearSolved();
        widgetPanel.enableNavButton(true);
        
        Selection selSets = GS.getSelectedSets();
        ProbData p = selSets.getNextProblem();
        
        while(!startProblem(p))
            p = selSets.getNextProblem();
        
        widgetPanel.setProblem(p);
        widgetPanel.setNumText(selSets.getCurIndex() + " " + Messages.getString("of")  //$NON-NLS-1$ //$NON-NLS-2$
                                + " " + selSets.getNumSelected()); //$NON-NLS-1$
        widgetPanel.setPctRight((double)selSets.getNumRight() / selSets.getNumSolved());
    }
    
    public void prevProblem() {
        mode = PLAY;
        panel.setNavMode(false);
        widgetPanel.clearSolved();
        widgetPanel.enableNavButton(true);
        
        Selection selSets = GS.getSelectedSets();
        ProbData p = selSets.getPrevProblem();
        
        while(!startProblem(p))
            p = selSets.getPrevProblem();
        
        widgetPanel.setProblem(p);
        widgetPanel.setNumText(selSets.getCurIndex() + " " + Messages.getString("of")  //$NON-NLS-1$ //$NON-NLS-2$
                                + " " + selSets.getNumSelected()); //$NON-NLS-1$
        widgetPanel.setPctRight((double)selSets.getNumRight() / selSets.getNumSolved());
    }
    
    public boolean findProblem(String sub) {
        Selection selSets = GS.getSelectedSets();
        ProbData old = selSets.getCurProb();
        
        ProbData p = selSets.findProblem(sub);
        if(p == null)
            return false;
        
        mode = PLAY;
        panel.setNavMode(false);
        widgetPanel.clearSolved();
        widgetPanel.enableNavButton(true);
        
        if(!startProblem(p)) {
            p = old;
            startProblem(p);
        }
        
        widgetPanel.setProblem(p);
        widgetPanel.setNumText(selSets.getCurIndex() + " " + Messages.getString("of")  //$NON-NLS-1$ //$NON-NLS-2$
                                + " " + selSets.getNumSelected()); //$NON-NLS-1$
        widgetPanel.setPctRight((double)selSets.getNumRight() / selSets.getNumSolved());
        
        return true;
    }
    
    public void setResult(boolean r) {
        if(!hasResult) {
            result = r;
            hasResult = true;
            GS.getSelectedSets().probDone(r);
        }
    }
    
    private void setCurColor(int c) {
        curColor = c;
        widgetPanel.setToPlay(curColor);
        panel.setGhostColor(curColor);
    }
    
    public String getCurrentProblemPath() { return GS.getSelectedSets().getCurrentProblemPath(); }
    
    class Responder implements Runnable{
        SGFNode node;
        
        Responder(SGFNode n) { this.node = n; }
        
        public void run() {
            try { Thread.sleep(250); } catch(Exception e) { /* */ }
            doNode(node);
            enabled = true;
        }
    }
    
    class AutoAdvancer implements Runnable {
        public void run() {
            try { Thread.sleep(500); } catch(Exception e) { /* */ }
            enabled = true;
            nextProblem();
        }
    }
}
