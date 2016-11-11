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

package GoGrinder.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;

import GoGrinder.*;
import GoGrinder.tests.Test;
import GoGrinder.ui.actions.FakeAction;
import GoGrinder.ui.actions.InsertNodeAction;
import GoGrinder.ui.actions.InsertNodeCopyAction;
import GoGrinder.ui.actions.LocalMoveAction;
import GoGrinder.ui.actions.MoveAction;
import GoGrinder.ui.actions.NewAction;
import GoGrinder.ui.actions.RealAction;
import GoGrinder.ui.actions.WGFOpenAction;
import GoGrinder.ui.actions.WGFSaveAction;
import GoGrinder.ui.actions.WGFSaveAsAction;

/**
 *
 * @author  tkington
 */
public class WGFFrame extends JFrame {
    private WGFController wgfController;
    private GobanPanel goban;
    JEditorPane textPane;
    JSplitPane splitPane;
    JButton stepButton;
    
    JMenuBar teachMenu;
    JMenuBar editMenu;
    JToolBar toolbar;
    
    private Action newAction;
    private Action openAction;
    private Action saveAction;
    private Action saveAsAction;
    
    private Action fakeAction;
    private Action realAction;
    private Action moveAction;
    private Action localMoveAction;

    private Action insertNodeAction;
    private Action insertNodeCopyAction;
    
    private JCheckBoxMenuItem editModeMenuItem;
    private JComboBox toolCombo;
    private DefaultComboBoxModel toolComboModel;
    private ImageIcon grayTri;
    private JComboBox textModeCombo;
    
    /** Creates a new instance of WGFFrame */
    public WGFFrame() {
        super(Messages.getString("gg_title")); //$NON-NLS-1$
        
        URL iconURL = getClass().getResource("/GoGrinder/images/Icon.png"); //$NON-NLS-1$
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());
        
        Container cp = getContentPane();
        
        textPane = new JEditorPane("text/html",Messages.getString("WGFFrame.OpenAWGFFileToBegin")); //$NON-NLS-1$ //$NON-NLS-2$
        textPane.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textPane);
        
        wgfController = new WGFController(this);
        Test.controller = wgfController;
        textPane.addHyperlinkListener(wgfController);

        createActions();
        createToolBar();
        setJMenuBar(createMenuBar());
        
        enableActions();
        
        wgfController.setControls(textPane, stepButton);
        
        Box toolPanel = new Box(BoxLayout.X_AXIS);
        toolPanel.add(toolbar);
        toolPanel.add(Box.createHorizontalGlue());
        cp.add(toolPanel, BorderLayout.NORTH);
        
        Board board = new Board(19);
        goban = new GobanPanel(board);
        goban.turnOffGhost();
        wgfController.setPanel(goban);
        goban.setController(wgfController);
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                          goban,
                                          scrollPane);
        
        cp.add(splitPane, BorderLayout.CENTER);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	if(!wgfController.saveChanges())
            		return;
                Main.onExit();
            }
        });
        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setBounds(GS.getWGFFrameBounds());
        splitPane.setDividerLocation(GS.getSplitterPos());
    }
    
    private void createActions() {
    	newAction = new NewAction(this, wgfController);
    	openAction = new WGFOpenAction(this, wgfController);
    	saveAction = new WGFSaveAction(this, wgfController);
    	saveAsAction = new WGFSaveAsAction(this, wgfController);

    	fakeAction = new FakeAction(this, wgfController);
    	moveAction = new MoveAction(this, wgfController);
    	localMoveAction = new LocalMoveAction(this, wgfController);
    	realAction = new RealAction(this, wgfController);

    	insertNodeAction = new InsertNodeAction(this, wgfController);
    	insertNodeCopyAction = new InsertNodeCopyAction(this, wgfController);
    }
    
    private void createToolBar() {
        toolbar = new JToolBar();
        
        JButton b = new JButton(newAction);
        b.setText(""); //$NON-NLS-1$
        toolbar.add(b);
        
        b = new JButton(openAction);
        b.setText(""); //$NON-NLS-1$
        toolbar.add(b);
        
        b = new JButton(saveAction);
        b.setText(""); //$NON-NLS-1$
        toolbar.add(b);
        
        b = new JButton(saveAsAction);
        b.setText(""); //$NON-NLS-1$
        toolbar.add(b);
        
        toolbar.addSeparator();
        
        b = new JButton(Main.getIcon("Home16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("WGFFrame.Home")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                wgfController.home();
            }
        });
        toolbar.add(b);

        ActionListener prevListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                wgfController.prev();
            }
        };
        
        b = new JButton(Main.getIcon("Back16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("WGFFrame.Back")); //$NON-NLS-1$
        b.addActionListener(prevListener);
        toolbar.add(b);
        
        b.registerKeyboardAction(prevListener, 
                                 KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0),
                                 JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        b.registerKeyboardAction(prevListener, 
                                 KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0),
                                 JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        ActionListener nextListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                wgfController.next();
            }
        };
        
        b = new JButton(Main.getIcon("Forward16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("WGFFrame.NextPage")); //$NON-NLS-1$
        b.addActionListener(nextListener);
        toolbar.add(b);
        
        b.registerKeyboardAction(nextListener, 
                                 KeyStroke.getKeyStroke(KeyEvent.VK_X, 0),
                                 JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        b.registerKeyboardAction(nextListener, 
                                 KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0),
                                 JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        stepButton = new JButton(Messages.getString("WGFFrame.Step")); //$NON-NLS-1$
        stepButton.setToolTipText(Messages.getString("WGFFrame.Step")); //$NON-NLS-1$
        stepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                wgfController.step();
            }
        });
        toolbar.add(stepButton);
        
        toolbar.addSeparator();
        
        b = new JButton(moveAction); //$NON-NLS-1$
        b.setText(""); //$NON-NLS-1$
        toolbar.add(b);
        
        b = new JButton(fakeAction); //$NON-NLS-1$
        b.setText(""); //$NON-NLS-1$
        toolbar.add(b);
        
        b = new JButton(realAction); //$NON-NLS-1$
        b.setText(""); //$NON-NLS-1$
        toolbar.add(b);
        
        b = new JButton(localMoveAction);
        b.setText(""); //$NON-NLS-1$
        toolbar.add(b);
        
        
        ImageIcon tri = Main.getIcon("TriStone.png", this); //$NON-NLS-1$
        grayTri = new ImageIcon(GrayFilter.createDisabledImage(tri.getImage()));
        
        Object [] items = new Object[] {
        		tri,
        		Main.getIcon("CircleStone.png", this), //$NON-NLS-1$
        		Main.getIcon("SquareStone.png", this), //$NON-NLS-1$
        		Main.getIcon("XStone.png", this), //$NON-NLS-1$
        		Main.getIcon("AStone.png", this), //$NON-NLS-1$
        		Main.getIcon("OneStone.png", this), //$NON-NLS-1$
				Main.getIcon("LabelStone.png", this), //$NON-NLS-1$
				Main.getIcon("Territory.png", this)}; //$NON-NLS-1$
        toolCombo = new JComboBox(items);
        toolComboModel = (DefaultComboBoxModel)toolCombo.getModel();
        Dimension dim = toolCombo.getMinimumSize();
        toolCombo.setMaximumSize(new Dimension(dim.width, 100));
        toolCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(toolCombo.getSelectedIndex()) {
				case 0: wgfController.setEditTool(WGFController.TOOL_TRI); break;
				case 1: wgfController.setEditTool(WGFController.TOOL_CIR); break;
				case 2: wgfController.setEditTool(WGFController.TOOL_SQU); break;
				case 3: wgfController.setEditTool(WGFController.TOOL_X); break;
				case 4: wgfController.setEditTool(WGFController.TOOL_LETTER); break;
				case 5: wgfController.setEditTool(WGFController.TOOL_NUMBER); break;
				case 6: wgfController.setEditTool(WGFController.TOOL_LABEL); break;
				case 7: wgfController.setEditTool(WGFController.TOOL_TERRITORY); break;
				}
			}
		});
        toolbar.add(toolCombo);
        
        String [] modeChoices = {"View HTML", "Edit HTML", "Edit WGF"};   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        textModeCombo = new JComboBox(modeChoices);
        textModeCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int mode = -1;
				switch(textModeCombo.getSelectedIndex()) {
				case 0: mode = WGFController.TEXTMODE_VIEWHTML; break;
				case 1: mode = WGFController.TEXTMODE_EDITHTML; break;
				case 2: mode = WGFController.TEXTMODE_EDITWGF; break;
				}
				if(!wgfController.setTextPaneMode(mode))
					textModeCombo.setSelectedIndex(2);
			}
		});
        toolbar.add(textModeCombo);
        
        b = new JButton(insertNodeAction);
        toolbar.add(b);
        
        b = new JButton(insertNodeCopyAction);
        toolbar.add(b);
    }
    
    private JMenuBar createMenuBar() {
    	JMenuBar menuBar = new JMenuBar();
    	
    	JMenu menu = new JMenu(Messages.getString("WGFFrame.File")); //$NON-NLS-1$
    	
    	JMenuItem mi = new JMenuItem(newAction);
    	mi.setIcon(null);
    	menu.add(mi);
    	
    	mi = new JMenuItem(openAction);
    	mi.setIcon(null);
    	menu.add(mi);
    	
    	mi = new JMenuItem(saveAction);
    	mi.setIcon(null);
    	menu.add(mi);
    	
    	mi = new JMenuItem(saveAsAction);
    	mi.setIcon(null);
    	menu.add(mi);
    	
    	menuBar.add(menu);
        
    	menu = new JMenu(Messages.getString("WGFFrame.Mode")); //$NON-NLS-1$
    	
    	mi = new JMenuItem(Messages.getString("WGFFrame.ProblemMode")); //$NON-NLS-1$
        mi.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
            	if(!wgfController.saveChanges())
            		return;
                Main.switchState();
        	}
        });
        menu.add(mi);
    	
    	editModeMenuItem = new JCheckBoxMenuItem(Messages.getString("WGFFrame.EditMode")); //$NON-NLS-1$
        editModeMenuItem.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		wgfController.setEditMode(editModeMenuItem.isSelected());
        		enableActions();
        	}
        });
        menu.add(editModeMenuItem);
                
        menuBar.add(menu);
    	
    	return menuBar;
    }
    
    private void enableActions() {
    	boolean edit = wgfController.getMode() == WGFController.EDIT_MODE;
    	fakeAction.setEnabled(edit);
    	realAction.setEnabled(edit);
    	moveAction.setEnabled(edit);
    	localMoveAction.setEnabled(edit);
    	
    	boolean oldEnabled = toolCombo.isEnabled();
    	if(oldEnabled && !edit) {
    		toolComboModel.insertElementAt(grayTri, 0);
    		toolCombo.setSelectedIndex(0);
    	}
    	else if(edit && !oldEnabled) {
    		toolComboModel.removeElementAt(0);
    	}
    	toolCombo.setEnabled(edit);
    	
    	textModeCombo.setEnabled(edit);
    	insertNodeAction.setEnabled(edit);
    	insertNodeCopyAction.setEnabled(edit);
    	
    	stepButton.setEnabled(!edit);
    }
    
    public int getSplitterPos() { return splitPane.getDividerLocation(); }
    public void saveHistory() { wgfController.saveHistory(); }
    public void loadHistory() { wgfController.loadHistory(); }
}
