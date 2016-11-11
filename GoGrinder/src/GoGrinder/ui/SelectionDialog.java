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

import java.io.*;
import java.awt.*;
import java.util.zip.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import GoGrinder.*;

/**
 *
 * @author  tkington
 */
public class SelectionDialog extends JDialog implements TreeSelectionListener, TagListener {
    static NumberFormat format;
    
    {
        format = DecimalFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
    }
    
    private JTree tree;
    private DefaultTreeModel model;
    private ProbCollection probs;
    private ArrayList selectedSets;
    private ArrayList selectedTags;
    private int numSelected;
    private boolean cancelled;
    private int order;
    private int matchType;
    
    private JLabel countLabel;
    private JLabel pctRight;
    private JLabel avgTime;
    
    private DefaultListModel listModel;
    private JList tagList;
    
    private TagCB tagCB;
    private JComboBox orderCB;
    private JComboBox matchCB;
    private JButton hsButton;
    
    /** Creates a new instance of SelectionDialog */
    public SelectionDialog(JFrame owner, ProbCollection probs, ArrayList selSets, ArrayList selTags, int order, int matchType) {
        super(owner, Messages.getString("current_sel"), true); //$NON-NLS-1$
        
        this.probs = probs;
        selectedSets = selSets;
        selectedTags = selTags;
        cancelled = false;
        numSelected = 0;
        
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        
        Box tlPanel = new Box(BoxLayout.Y_AXIS);
        tlPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        tlPanel.add(new JLabel(Messages.getString("collections"))); //$NON-NLS-1$
        
        model = new DefaultTreeModel(probs);
        tree = new JTree(model);
        tree.addTreeSelectionListener(this);
        
        JScrollPane sp = new JScrollPane(tree);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        tlPanel.add(sp);
        
        Box butPanel = new Box(BoxLayout.X_AXIS);
        butPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        butPanel.add(Box.createHorizontalGlue());
        
        tagCB = new TagCB(this);
        butPanel.add(tagCB);
        
        JButton reset = new JButton(Messages.getString("reset_stats")); //$NON-NLS-1$
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onResetStats();
            }
        });
        butPanel.add(reset);
        
        hsButton = new JButton(Messages.getString("high_scores")); //$NON-NLS-1$
        hsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onHighScores();
            }
        });
        butPanel.add(hsButton);
        
        tlPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        tlPanel.add(butPanel);
        
        JPanel trPanel = new JPanel(new BorderLayout());
        trPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        
        trPanel.add(new JLabel(Messages.getString("tags")), BorderLayout.NORTH); //$NON-NLS-1$
        
        listModel = new DefaultListModel();
        tagList = new JList(listModel);
        tagList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = tagList.getSelectionModel();
                if(lsm.getMinSelectionIndex() == 0 && lsm.getMaxSelectionIndex() != 0)
                    lsm.setSelectionInterval(0, 0);
                updateStats();
            }
        });
        
        fillTagList();
        
        sp = new JScrollPane(tagList);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        trPanel.add(sp, BorderLayout.CENTER);
        
        Box sortPanel = new Box(BoxLayout.Y_AXIS);
        sortPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        
        Box delPanel = new Box(BoxLayout.X_AXIS);
        delPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton b = new JButton(Messages.getString("delete")); //$NON-NLS-1$
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDeleteTags();
            }
        });
        delPanel.add(Box.createHorizontalGlue());
        delPanel.add(b);
        sortPanel.add(delPanel);
        
        sortPanel.add(new JLabel(Messages.getString("match"))); //$NON-NLS-1$
        matchCB = new JComboBox(Selection.MATCHTYPES);
        matchCB.setAlignmentX(Component.LEFT_ALIGNMENT);
        matchCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateStats();
            }
        });
        sortPanel.add(matchCB);
        
        sortPanel.add(new JLabel(Messages.getString("sort_order"))); //$NON-NLS-1$
        orderCB = new JComboBox(Selection.ORDERS);
        orderCB.setAlignmentX(Component.LEFT_ALIGNMENT);
        sortPanel.add(orderCB);
        
        trPanel.add(sortPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("select_problems_colon"))); //$NON-NLS-1$
        topPanel.add(tlPanel, BorderLayout.CENTER);
        topPanel.add(trPanel, BorderLayout.EAST);
        
        Box leftStats = new Box(BoxLayout.Y_AXIS);
        Box rightStats = new Box(BoxLayout.Y_AXIS);
        
        leftStats.add(new JLabel(Messages.getString("num_selected"))); //$NON-NLS-1$
        
        countLabel = new JLabel(Messages.getString("zero_of") + " " + probs.getSize()); //$NON-NLS-1$ //$NON-NLS-2$
        rightStats.add(countLabel);
        
        leftStats.add(new JLabel(Messages.getString("pct_right_colon"))); //$NON-NLS-1$
        
        pctRight = new JLabel();
        rightStats.add(pctRight);
        
        leftStats.add(new JLabel(Messages.getString("avg_time_colon"))); //$NON-NLS-1$
        
        avgTime = new JLabel();
        rightStats.add(avgTime);
        
        JPanel statPanel = new JPanel();
        statPanel.add(leftStats);
        statPanel.add(rightStats);
        
        JPanel brPanel = new JPanel(new BorderLayout());
        
        JButton exp = new JButton(Messages.getString("export_dot_dot_dot")); //$NON-NLS-1$
        exp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExport();
            }
        });
        brPanel.add(exp, BorderLayout.NORTH);
        
        Box bottomPanel = new Box(BoxLayout.X_AXIS);
        bottomPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("current_sel_colon"))); //$NON-NLS-1$
        
        JPanel dumb = new JPanel(new BorderLayout());
        dumb.add(brPanel, BorderLayout.WEST);
        
        bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        bottomPanel.add(statPanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        bottomPanel.add(dumb);
        
        Box buttonPanel = new Box(BoxLayout.X_AXIS);
        buttonPanel.add(Box.createHorizontalGlue());
        
        JButton okButton = new JButton(Messages.getString("ok")); //$NON-NLS-1$
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonPanel.add(okButton);
        
        JButton cancelButton = new JButton(Messages.getString("cancel")); //$NON-NLS-1$
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        buttonPanel.add(cancelButton);
        
        Box southPanel = new Box(BoxLayout.Y_AXIS);
        southPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        southPanel.add(bottomPanel);
        southPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        southPanel.add(buttonPanel);
        
        cp.add(topPanel, BorderLayout.CENTER);
        cp.add(southPanel, BorderLayout.SOUTH);
        
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        
        TreePath [] paths = new TreePath[selectedSets.size()];
        for(int i = 0; i < selectedSets.size(); i++) {
            ProbCollection c = (ProbCollection)selectedSets.get(i);
            paths[i] = new TreePath(c.getPath());
        }
        tree.setSelectionPaths(paths);
        
        if(selectedTags.isEmpty())
            tagList.setSelectedIndex(0);
        else {
            int [] sel = new int[selectedTags.size()];
            int index = 0;
            for(int i = 0; i < sel.length; i++) {
                for(int j = 0; j < listModel.size(); j++) {
                    if(listModel.get(j).equals(selectedTags.get(i))) {
                        sel[index++] = j;
                        break;
                    }
                }
            }
            tagList.setSelectedIndices(sel);
        }
        
        orderCB.setSelectedIndex(order);
        matchCB.setSelectedIndex(matchType);
        
        pack();
        setSize(getSize().width, 500);
        setLocationRelativeTo(owner);
        setVisible(true);
    }
    
    public void fillTagList() {
        listModel.clear();
        listModel.addElement(Messages.getString("none")); //$NON-NLS-1$
        String [] tags = GS.getTagList().getTags();
        for(int i = 0; i < tags.length; i++)
            listModel.addElement(tags[i]);
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        TreePath[] paths = tree.getSelectionPaths();
        
        if(paths != null) {
            for(int i = 0; i < paths.length; i++) {
                for(int j = 0; j < paths.length; j++) {
                    if(i == j)
                        continue;

                    if(paths[i].isDescendant(paths[j]))
                        tree.removeSelectionPath(paths[j]);
                }
            }
        }
        
        updateStats();
    }
    
    public void updateStats() {
        SelectionStats s = getSelectionStats();
        numSelected = s.getNum();
        
        countLabel.setText(s.getNum() + " " + Messages.getString("of") + " " + probs.getSize()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        int tried = s.getNumTried();
        int right = s.getNumRight();
        long time = s.getTotalTime();
        if(tried == 0)
            pctRight.setText(Messages.getString("none_right")); //$NON-NLS-1$
        else pctRight.setText(format.format((double)right / tried * 100)
                              + Messages.getString("pct_right_paren") + right  //$NON-NLS-1$
                              + " " + Messages.getString("of") + " " + tried //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                              + Messages.getString("rparen")); //$NON-NLS-1$
            
        if(right == 0)
            avgTime.setText(Messages.getString("avg_time_zero")); //$NON-NLS-1$
        else avgTime.setText(format.format((double) time / right / 1000)
                            + Messages.getString("seconds")); //$NON-NLS-1$
        
        TreePath[] paths = tree.getSelectionPaths();
        if(paths != null && paths.length == 1)
            hsButton.setEnabled(true);
        else hsButton.setEnabled(false);
    }
    
    private SelectionStats getSelectionStats() {
        ArrayList tags = new ArrayList();
        ListSelectionModel lsm = tagList.getSelectionModel();
        for(int i = lsm.getMinSelectionIndex(); i <= lsm.getMaxSelectionIndex(); i++) {
            if(lsm.isSelectedIndex(i)) {
                //  If NONE is selected, no tags
                if(i == 0)
                    break;
                
                tags.add(listModel.getElementAt(i));
            }
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SelectionStats s = new SelectionStats();
        TreePath[] paths = tree.getSelectionPaths();
        if(paths != null) {
            for(int i = 0; i < paths.length; i++) {
                ProbCollection c = (ProbCollection)paths[i].getLastPathComponent();
                c.getSelectionStats(s, tags, matchCB.getSelectedIndex());
            }
        }
        
        setCursor(null);
        
        return s;
    }
    
    public void onOK() {
        TreePath[] paths = tree.getSelectionPaths();
        
        if(numSelected == 0 || paths == null) {
            JOptionPane.showMessageDialog(this, Messages.getString("no_probs_match_colls_and_tags")); //$NON-NLS-1$
            return;
        }
        
        selectedSets.clear();
        for(int i = 0; i < paths.length; i++) {
            ProbCollection c = (ProbCollection)paths[i].getLastPathComponent();
            selectedSets.add(c);
        }
        
        selectedTags.clear();
        int [] sel = tagList.getSelectedIndices();
        for(int i = 0; i < sel.length; i++) {
            if(sel[i] == 0)
                break;
            
            selectedTags.add(listModel.getElementAt(sel[i]));
        }
        
        order = orderCB.getSelectedIndex();
        matchType = matchCB.getSelectedIndex();
        
        setVisible(false);
        dispose();
    }
    
    public void onCancel() {
        setVisible(false);
        dispose();
        cancelled = true;
    }
    
    public void onResetStats() {
        if(JOptionPane.showConfirmDialog(this, Messages.getString("reset_stats_for_sel"), //$NON-NLS-1$
                                         null, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            return;
        
        Cursor cur = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        new ProgressDialog(this, Messages.getString("resetting_stats"), new Task() { //$NON-NLS-1$
            public void doTask() {
                resetStats(progDlg);
            }
        });
        
        updateStats();
        
        setCursor(cur);
    }
    
    public void resetStats(ProgressDialog progDlg) {
        TreePath[] paths = tree.getSelectionPaths();
        if(paths == null) {
            JOptionPane.showMessageDialog(SelectionDialog.this, Messages.getString("no_sets_selected")); //$NON-NLS-1$
            return;
        }

        int num = 0;
        for(int i = 0; i < paths.length; i++) {
            ProbCollection c = (ProbCollection)paths[i].getLastPathComponent();
            num += c.getSize();
        }
        progDlg.setMaximum(num);

        for(int i = 0; i < paths.length; i++) {
            ProbCollection c = (ProbCollection)paths[i].getLastPathComponent();
            c.resetStats(progDlg);
        }
    }
    
    public void addTag(final String t) {
        if(JOptionPane.showConfirmDialog(this, Messages.getString("apply_the_tag") + t  //$NON-NLS-1$
                                               + Messages.getString("to_sel_colls"), //$NON-NLS-1$
                                         null, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            return;
        
        Cursor cur = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        new ProgressDialog(this, Messages.getString("applying") + t + Messages.getString("tag"), new Task() { //$NON-NLS-1$ //$NON-NLS-2$
            public void doTask() {
                addTag(t, progDlg);
            }
        });
        
        setCursor(cur);
        
        updateStats();
    }
    
    private void addTag(String t, ProgressDialog progDlg) {
        TreePath[] paths = tree.getSelectionPaths();
        if(paths == null) {
            JOptionPane.showMessageDialog(SelectionDialog.this, Messages.getString("no_sets_selected")); //$NON-NLS-1$
            return;
        }

        int numProbs = 0;
        for(int i = 0; i < paths.length; i++) {
            ProbCollection c = (ProbCollection)paths[i].getLastPathComponent();
            numProbs += c.getSize();
        }
        progDlg.setMaximum(numProbs);

        for(int i = 0; i < paths.length; i++) {
            ProbCollection c = (ProbCollection)paths[i].getLastPathComponent();
            c.addTag(t, progDlg);
        }       
    }
    
    public void onDeleteTags() {
        if(JOptionPane.showConfirmDialog(this, Messages.getString("perm_del_tag"), //$NON-NLS-1$
                                         null, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            return;
        
        new ProgressDialog(this, Messages.getString("removing_tags"), new Task() { //$NON-NLS-1$
            public void doTask() {
                deleteTags(progDlg);
            }
        });
    }
    
    private void deleteTags(ProgressDialog progDlg) {   
        int [] sel = tagList.getSelectedIndices();
        if(sel.length == 1 && sel[0] == 0) {
            JOptionPane.showMessageDialog(this, Messages.getString("no_tags_selected")); //$NON-NLS-1$
            return;
        }
        
        ArrayList tags = new ArrayList();
        for(int i = 0; i < sel.length; i++) {
            if(sel[i] == 0)
                continue;
            
            tags.add(listModel.getElementAt(sel[i]));
        }
        
        ProbCollection c = GS.getCollections();
        progDlg.setMaximum(c.getSize());
        c.removeTags(tags, progDlg);
        
        TagList list = GS.getTagList();
        list.removeTags(tags);
        
        fillTagList();
        tagCB.fillCB();
    }
    
    public void onHighScores() {
        TreePath[] paths = tree.getSelectionPaths();
        if(paths == null)
            return;
        
        ProbCollection c = (ProbCollection)paths[0].getLastPathComponent();
        new HighScoreDialog(this, (String)c.getUserObject(), c.getHighScores(), null);
    }
    
    public void onExport() {
        Cursor cur = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        new ProgressDialog(this, Messages.getString("exporting_probs"), new Task() { //$NON-NLS-1$
            public void doTask() {
                doExport(progDlg);
            }
        });
        
        setCursor(cur);
    }
    
    private void doExport(ProgressDialog progDlg) {
        TreePath[] paths = tree.getSelectionPaths();
        if(paths == null) {
            JOptionPane.showMessageDialog(SelectionDialog.this,
                        Messages.getString("no_probs_match_colls_and_tags")); //$NON-NLS-1$
            return;
        }

        String filename = Main.chooseFile(SelectionDialog.this, Main.gxpFilter,
                                    Messages.getString("export_problems"), true, //$NON-NLS-1$
                                    Messages.getString("export")); //$NON-NLS-1$
        if(filename == null)
            return;

        try {
            ZipOutputStream zout = new ZipOutputStream(
                                   new BufferedOutputStream(
                                   new FileOutputStream(filename)));
            zout.putNextEntry(new ZipEntry("Exported Data")); //$NON-NLS-1$

            ObjectOutputStream out = new ObjectOutputStream(zout);

            ArrayList tags = new ArrayList();
            int [] sel = tagList.getSelectedIndices();
            for(int i = 0; i < sel.length; i++) {
                if(sel[i] == 0)
                    break;

                tags.add(listModel.getElementAt(sel[i]));
            }

            int matchType = matchCB.getSelectedIndex();

            ArrayList probs = new ArrayList();
            for(int i = 0; i < paths.length; i++) {
                ProbCollection c = (ProbCollection)paths[i].getLastPathComponent();
                c.getProblems(probs, tags, matchType);
            }

            progDlg.setMaximum(probs.size());

            out.writeInt(Main.EXPORTREVISION);
            out.writeInt(probs.size());
            for(int i = 0; i < probs.size(); i++) {
                ((ProbData)probs.get(i)).export(out);
                progDlg.bump();
            }

            out.close();
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, Messages.getString("ex_while_exporting")); //$NON-NLS-1$
            Main.logSilent(e);
        }     
    }
    
    public int getNumSelected() { return numSelected; }
    public ArrayList getSelectedSets() { return selectedSets; }
    public ArrayList getSelectedTags() { return selectedTags; }
    public void newTagCreated() { fillTagList(); }
    public boolean wasCancelled() { return cancelled; }
    public int getOrder() { return order; }
    public int getMatchType() { return matchType; }
}
