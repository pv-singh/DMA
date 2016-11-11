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
import java.io.*;
import java.util.*;
import java.util.zip.*;

import GoGrinder.*;

import com.Ostermiller.util.Browser;

/**
 *
 * @author  tkington
 */
public class ProbFrame extends JFrame {
    public static ProbFrame inst;
    
    private FileUtils fileUtils;
    private GobanPanel goban;
    private WidgetPanel wp;
    private JLabel statusBar;
    private JToggleButton advanceButton;
    private long lastNextTime = 0;
    
    private SGFController sgfController;
    
    public ProbFrame(SplashScreen splash) {
        super(Messages.getString("gg_title")); //$NON-NLS-1$
        inst = this;
        
        URL iconURL = getClass().getResource("/GoGrinder/images/Icon.png"); //$NON-NLS-1$
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());
        
        Container cp = getContentPane();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        
        Box toolPanel = new Box(BoxLayout.X_AXIS);
        toolPanel.add(createToolbar());
        toolPanel.add(Box.createHorizontalGlue());
        cp.add(toolPanel);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        
        sgfController = new SGFController();
        
        splash.setStatus(Messages.getString("loading_images")); //$NON-NLS-1$
        Board board = new Board(19);
        goban = new GobanPanel(board);
        
        topPanel.add(goban, BorderLayout.CENTER);
        
        JPanel eastPanel = new JPanel(new BorderLayout());
        
        wp = new WidgetPanel(sgfController);
        eastPanel.add(wp, BorderLayout.NORTH);
        
        topPanel.add(eastPanel, BorderLayout.EAST);
        
        cp.add(topPanel);
        
        board.setPanel(goban);
        sgfController.setPanel(goban);
        sgfController.setBoard(board);
        sgfController.setWidgetPanel(wp);
        
        goban.setController(sgfController);
        
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea,
                                                 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                if(d.height < 90)
                    return new Dimension(d.width, 90);
                return d;
            }
            public Dimension getMinimumSize() {
                return new Dimension(0, 90);
            }
        };
        cp.add(scrollPane);
        
        Box statusBox = new Box(BoxLayout.X_AXIS);
        statusBar = new JLabel(" "); //$NON-NLS-1$
        statusBox.add(statusBar);
        statusBox.add(Box.createHorizontalGlue());
        cp.add(statusBox);
        sgfController.setStatusBar(statusBar);
        
        sgfController.setCommentArea(textArea);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Main.onExit();
            }
        });
        
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                boolean resize = false;
                int h = getHeight();
                int w = getWidth();
                
                Dimension gSize = goban.getMinimumSize();
                Dimension wpSize = wp.getPreferredSize();
                
                int minWidth = gSize.width + wpSize.width + 30;
                if(w < minWidth) {
                    w = minWidth;
                    resize = true;
                }
                
                int minHeight = gSize.height + 225;
                if(h < minHeight) {
                    h = minHeight;
                    resize = true;
                }
                
                if(resize)
                    setSize(w, h);
            }
        });
        
        setBounds(GS.getProbFrameBounds());
        
        fileUtils = new FileUtils(Main.chooser);
    }
    
    public void selectFirstProb() {   
        if(GS.getSelectedSets().isEmpty())
            onSelectProbs();
        else sgfController.nextProblem();
    }
    
    public JToolBar createToolbar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        
        JButton b = new JButton(Main.getIcon("Zoom16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("select_problems")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSelectProbs();
            }
        });
        bar.add(b);
        
        /*b = new JButton("mode"); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onMode();
            }
        });
        bar.add(b);*/
        
        b = new JButton(Main.getIcon("Open16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("open_file")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOpen();
            }
        });
        bar.add(b);
        
        b = new JButton(Main.getIcon("Save16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("save_progress")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });
        bar.add(b);
        
        b = new JButton(Main.getIcon("Edit16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("ProbFrame.EditCurrentProb")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEdit();
            }
        });
        bar.add(b);
        
        bar.addSeparator();

        ActionListener prevListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sgfController.prevProblem();
            }
        };
        
        b = new JButton(Main.getIcon("Back16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("prev_problem")); //$NON-NLS-1$
        b.addActionListener(prevListener);
        bar.add(b);
        
        b.registerKeyboardAction(prevListener, 
                                 KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0),
                                 JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        b.registerKeyboardAction(prevListener, 
                                 KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0),
                                 JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        ActionListener nextListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	long now = System.currentTimeMillis();
            	if(now - lastNextTime >= 350) {
            		lastNextTime = now;
            		sgfController.nextProblem();
            	}
            }
        };
        
        b = new JButton(Main.getIcon("Forward16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("next_problem")); //$NON-NLS-1$
        b.addActionListener(nextListener);
        bar.add(b);
        
        b.registerKeyboardAction(nextListener, 
                                 KeyStroke.getKeyStroke(KeyEvent.VK_X, 0),
                                 JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        b.registerKeyboardAction(nextListener, 
                                 KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0),
                                 JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        b = new JButton(Main.getIcon("Find16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("find_problem")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onFind();
            }
        });
        bar.add(b);
        
        bar.addSeparator();
        
        advanceButton = new JToggleButton(Main.getIcon("Play16.gif", this)); //$NON-NLS-1$
        advanceButton.setToolTipText(Messages.getString("auto_advance")); //$NON-NLS-1$
        advanceButton.setSelected(GS.getAutoAdv());
        advanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GS.setAutoAdv(!GS.getAutoAdv());
            }
        });
        bar.add(advanceButton);
        
        b = new JButton(Main.getIcon("Preferences16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("preferences")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.showPrefs(ProbFrame.this);
                advanceButton.setSelected(GS.getAutoAdv());
            }
        });
        bar.add(b);
        
        bar.addSeparator();
        
        b = new JButton(Main.getIcon("Copy16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("split_sgf_file")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileUtils.splitFile();
            }
        });
        bar.add(b);
        
        b = new JButton(Main.getIcon("Import16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("import_problems")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onImport();
            }
        });
        bar.add(b);
        
        /*b = new JButton(Main.getIcon("Export16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("extract_problems")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ProgressDialog p = new ProgressDialog(ProbFrame.this, Messages.getString("extracting_problems"), new Task() { //$NON-NLS-1$
                    public void doTask() {
                        fileUtils.extractProblems(progDlg);
                    }
                });
                wp.fillTagCB();

                if(p.getSucceeded())
                    ReloadDialog.reloadProblems(ProbFrame.this);
            }
        });
        bar.add(b);*/
        
        bar.addSeparator();
        
        b = new JButton(Main.getIcon("About16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("about_gg")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AboutDialog d = new AboutDialog(ProbFrame.this, true);
                d.setVisible(true);
            }
        });
        bar.add(b);
        
        b = new JButton(Main.getIcon("Help16.gif", this)); //$NON-NLS-1$
        b.setToolTipText(Messages.getString("help")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File f = new File("docs" + File.separator + "docs.html"); //$NON-NLS-1$ //$NON-NLS-2$
                String url = null;
                try {
                    url = f.toURL().toString();
                    Browser.displayURL(url);
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(ProbFrame.this, Messages.getString("couldnt_display_url") + " " + url + //$NON-NLS-1$ //$NON-NLS-2$
                                                  "\n" + Messages.getString("see_grind_log")); //$NON-NLS-1$ //$NON-NLS-2$
                    Main.logSilent(ex);
                }
            }
        });
        bar.add(b);
        
        return bar;
    }
    
    public void onMode() {
        Main.switchState();
    }
    
    public void onFind() {
        String sub = JOptionPane.showInputDialog(this, Messages.getString("enter_part_of_filename")); //$NON-NLS-1$
        if(sub == null)
            return;
        
        if(!sgfController.findProblem(sub))
            JOptionPane.showMessageDialog(this, Messages.getString("no_matching_probs_found")); //$NON-NLS-1$
    }
    
    private void onEdit() {
    	File editor = GS.getSGFEditor();
    	if(editor == null) {
    		JOptionPane.showMessageDialog(this, Messages.getString("ProbFrame.FirstCfgEditor")); //$NON-NLS-1$
    		return;
    	}
    	
    	if(!editor.exists()) {
    		JOptionPane.showMessageDialog(this, Messages.getString("ProbFrame.TheSGFEditor") + editor.getAbsolutePath() + Messages.getString("ProbFrame.DoesNotExist")); //$NON-NLS-1$ //$NON-NLS-2$
    		return;
    	}
    	
    	File dir = editor.getParentFile();
    	String [] args = new String[2];
    	args[0] = editor.getAbsolutePath();
    	args[1] = sgfController.getCurrentProblemPath();
    	
    	try {
    		Runtime.getRuntime().exec(args, null, dir);
    	}
    	catch(IOException e) {
    		JOptionPane.showMessageDialog(this, Messages.getString("ProbFrame.ErrorLaunchingEditor")); //$NON-NLS-1$
    		Main.logSilent(e);
    	}
    }
    
    private static final int SAVEREVISION = 1;
    public void onOpen() {
        String filename = Main.chooseFile(this, Main.ggsFilter, Messages.getString("open_file"), //$NON-NLS-1$
                                    false, Messages.getString("open")); //$NON-NLS-1$
        if(filename == null)
            return;
        
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));

            int rev = in.readInt();
            if(rev > SAVEREVISION) {
                JOptionPane.showMessageDialog(null, Messages.getString("err_data_file_newer")); //$NON-NLS-1$
                return;
            }

            Selection sel = (Selection)in.readObject();
            in.close();
            
            if(sel.isEmpty())
                return;
        
            GS.setSelection(sel);
            sgfController.nextProblem();
        }
        catch(Exception e) {
            Main.log(e);
        }
    }
    
    public void onSave() {
        String filename = Main.chooseFile(this, Main.ggsFilter, Messages.getString("save_state"), //$NON-NLS-1$
                                    true, Messages.getString("save")); //$NON-NLS-1$
        if(filename == null)
            return;
        
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeInt(SAVEREVISION);
            out.writeObject(GS.getSelectedSets());
            out.close();
        }
        catch(Exception e) {
            Main.log(e);
        }
    }
    
    public void onImport() {
        ProgressDialog p = new ProgressDialog(this, Messages.getString("importing_problems"), new Task() { //$NON-NLS-1$
            public void doTask() {
                doImport(progDlg);
            }
        });
        
        if(p.getSucceeded())
            ReloadDialog.reloadProblems(this);
    }
    
    public void doImport(ProgressDialog progDlg) {
        String filename = Main.chooseFile(this, Main.gxpFilter, Messages.getString("import_problems"), //$NON-NLS-1$
                                    false, Messages.getString("import")); //$NON-NLS-1$
        if(filename == null) {
            progDlg.setSucceeded(false);
            return;
        }
        
        try {
            ZipInputStream zin = new ZipInputStream(
                                 new BufferedInputStream(
                                 new FileInputStream(filename)));
            zin.getNextEntry();
            
            ObjectInputStream in = new ObjectInputStream(zin);

            int rev = in.readInt();
            if(rev > Main.EXPORTREVISION) {
                JOptionPane.showMessageDialog(null, Messages.getString("err_data_file_newer")); //$NON-NLS-1$
                Main.log(new Exception());
                return;
            }
         
            int num = in.readInt();
            progDlg.setMaximum(num);
            
            boolean choseAll = false;
            boolean choseYes = false;
            
            outer:
            for(int i = 0; i < num; i++) {
                progDlg.bump();
                
                File f = (File)in.readObject();
                long impFileTime = in.readLong();
                String sgf = (String)in.readObject();
                HashSet tags = (HashSet)in.readObject();
                
                if(f.exists() && f.lastModified() != impFileTime) {
                    if(choseAll) {
                        if(!choseYes)
                            continue;
                    }
                    else {
                        ChoiceDialog c = new ChoiceDialog(this, f, impFileTime, sgf.length());
                        int ch = c.getSelection();
                        switch(ch) {
                            case ChoiceDialog.YES_OPTION:
                                break;
                            case ChoiceDialog.NO_OPTION:
                                continue;
                            case ChoiceDialog.YES_ALL_OPTION:
                                choseAll = choseYes = true;
                                break;
                            case ChoiceDialog.NO_ALL_OPTION:
                                choseAll = true;
                                choseYes = false;
                                continue;
                        }
                    }
                }
                else {
                    File dir = f.getParentFile();
                    if(!dir.exists()) {
                        if(!dir.mkdirs()) {
                            JOptionPane.showMessageDialog(this, Messages.getString("couldnt_create_dir") + " " + dir.getPath()); //$NON-NLS-1$ //$NON-NLS-2$
                            return;
                        }
                    }
                }
                
                if(!f.exists() || f.lastModified() != impFileTime) {
                    PrintWriter out = new PrintWriter(new FileWriter(f));
                    out.println(sgf);
                    out.close();
                    
                    f.setLastModified(impFileTime);
                }
                
                //  Merge stats, replacing tags
                ProbData p = new ProbData(null, f);
                p.loadStats();
                p.setTags(tags);
                p.saveStats();
                
                GS.getTagList().addTags(tags);
            }
            
            in.close();
        }
        catch(Exception e) {
            Main.log(e);
        }
    }
    
    public void onSelectProbs() {
        SelectionDialog d;
        Selection selSets = GS.getSelectedSets();
        do {
            d = new SelectionDialog(this, GS.getCollections(),
            selSets.getSelectedSets(),
            selSets.getSelectedTags(),
            selSets.getOrder(),
            selSets.getMatchType());
        } while(d.getNumSelected() == 0 && selSets.isEmpty());
        
        wp.fillTagCB();
        
        if(!d.wasCancelled()) {
            selSets.selectProblems(d.getSelectedSets(), d.getSelectedTags(), d.getOrder(), d.getMatchType());
            sgfController.nextProblem();
        }
    }
}
