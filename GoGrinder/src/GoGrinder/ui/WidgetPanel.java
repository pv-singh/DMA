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
import java.util.*;
import java.text.*;
import javax.swing.*;

import GoGrinder.*;

/**
 *
 * @author  tkington
 */
public class WidgetPanel extends javax.swing.JPanel implements TagListener {
    static NumberFormat format;
    
    {
        format = DecimalFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
    }
    
    private SGFController sgfController;
    private DefaultListModel listModel;
    private ImageIcon blackIcon;
    private ImageIcon whiteIcon;
        
    /** Creates new form WidgetPanel */
    public WidgetPanel(SGFController c) {
        sgfController = c;
        listModel = new DefaultListModel();
        blackIcon = new ImageIcon(GobanPanel.blacks[0]);
        whiteIcon = new ImageIcon(GobanPanel.whites[0][0]);
        
        initComponents();
        
        int width = Math.max(navButton.getPreferredSize().width, 
                             tagCB.getPreferredSize().width) + 15;
        setPreferredSize(new Dimension(width, getPreferredSize().height));
    }
    
    public void setProblem(ProbData p) {
        fillTagList(p);
    }
    
    private void fillTagList(ProbData p) {
        listModel.clear();
        HashSet tags = p.getTags();
        String [] t = (String[])tags.toArray(new String[tags.size()]);
        Arrays.sort(t);
        
        for(int i = 0; i < t.length; i++)
            listModel.addElement(t[i]);
    }
    
    public void fillTagCB() {
        ((TagCB)tagCB).fillCB();
    }
    
    public void setToPlay(int p) {
        if(p == 1) {
            toPlayLabel.setIcon(blackIcon);
            toPlayLabel.setText(Messages.getString("black_to_play")); //$NON-NLS-1$
        }
        else {
            toPlayLabel.setIcon(whiteIcon);
            toPlayLabel.setText(Messages.getString("white_to_play")); //$NON-NLS-1$
        }
    }
    
    public void setSolved(boolean trueResult, boolean dispResult) {
        if(dispResult) {
            if(trueResult) {
                solvedLabel.setForeground(new Color(0xdb, 0xa3, 0));
                solvedLabel.setText(Messages.getString("solved")); //$NON-NLS-1$
            }
            else {
                solvedLabel.setForeground(Color.black);
                solvedLabel.setText(Messages.getString("solved_no_credit")); //$NON-NLS-1$
            }
        }
        else {
            solvedLabel.setForeground(Color.red);
            solvedLabel.setText(Messages.getString("failed")); //$NON-NLS-1$
        }
    }
    
    public void clearSolved() {
        solvedLabel.setForeground(Color.black);
        solvedLabel.setText(Messages.getString("unsolved")); //$NON-NLS-1$
    }
    
    public void setPctRight(double p) {
        if(Double.isNaN(p))
            p = 0;
        pctRightLabel.setText(format.format(p * 100) + Messages.getString("pct_correct")); //$NON-NLS-1$
    }
    
    public void setNumText(String s) { numLabel.setText(s); }
    
    private void initComponents() {
        jPanel2 = new javax.swing.JPanel();
        toPlayLabel = new javax.swing.JLabel();
        solvedLabel = new javax.swing.JLabel();
        restartButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        navButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        numLabel = new javax.swing.JLabel();
        pctRightLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        tagCB = new TagCB(this);
        jScrollPane1 = new javax.swing.JScrollPane();
        tagJList = new JList(listModel);
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(3, 3, 3, 3)));
        jPanel2.setLayout(new java.awt.GridLayout(0, 1));

        jPanel2.setAlignmentX(0.0F);
        jPanel2.add(toPlayLabel);

        solvedLabel.setFont(new java.awt.Font("MS Sans Serif", 1, 14)); //$NON-NLS-1$
        solvedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel2.add(solvedLabel);

        restartButton.setText(Messages.getString("restart")); //$NON-NLS-1$
        restartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRestart();
            }
        });

        jPanel2.add(restartButton);

        backButton.setText(Messages.getString("back")); //$NON-NLS-1$
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onBack();
            }
        });

        jPanel2.add(backButton);

        navButton.setText(Messages.getString("nav_soln")); //$NON-NLS-1$
        navButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onNavigate();
            }
        });

        jPanel2.add(navButton);

        add(jPanel2);

        jPanel1.setLayout(new java.awt.GridLayout(0, 1));

        jPanel1.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(Messages.getString("statistics")), new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 3, 0, 3)))); //$NON-NLS-1$
        jPanel1.setAlignmentX(0.0F);
        jPanel1.add(numLabel);

        jPanel1.add(pctRightLabel);

        add(jPanel1);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(Messages.getString("tags")), new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 3, 3, 3)))); //$NON-NLS-1$
        tagCB.setAlignmentX(0.0F);
        jPanel4.add(tagCB);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setAlignmentX(0.0F);
        tagJList.setAlignmentX(0.0F);
        jScrollPane1.setViewportView(tagJList);

        jPanel4.add(jScrollPane1);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.X_AXIS));

        jPanel3.setAlignmentX(0.0F);
        jPanel3.add(Box.createHorizontalGlue());
        jButton1.setText(Messages.getString("remove")); //$NON-NLS-1$
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRemoveTag();
            }
        });

        jPanel3.add(jButton1);

        jPanel4.add(jPanel3);

        add(jPanel4);

    }

    private void onRemoveTag() {
        ProbData p = GS.getSelectedSets().getCurProb();
        int [] sel = tagJList.getSelectedIndices();
        for(int i = 0; i < sel.length; i++)
            p.removeTag((String)listModel.getElementAt(sel[i]));
        fillTagList(p);
    }

    private void onNavigate() {
        sgfController.navigateSolution();
        navButton.setEnabled(false);
    }

    private void onBack() {
        sgfController.goBack();
    }

    private void onRestart() {
        sgfController.restart();
        navButton.setEnabled(true);
    }

    public void addTag(String t) {
        ProbData p = GS.getSelectedSets().getCurProb();
        p.addTag(t); 
        fillTagList(p);
    }
    
    public void enableNavButton(boolean e) { navButton.setEnabled(e); }
    
    public void newTagCreated() { /* Don't care about this event */ }
    
    private javax.swing.JButton backButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton navButton;
    private javax.swing.JLabel numLabel;
    private javax.swing.JLabel pctRightLabel;
    private javax.swing.JButton restartButton;
    private javax.swing.JLabel solvedLabel;
    private javax.swing.JComboBox tagCB;
    private javax.swing.JList tagJList;
    private javax.swing.JLabel toPlayLabel;
}
