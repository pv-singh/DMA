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

import GoGrinder.Task;

public class ProgressDialog extends javax.swing.JDialog {
    Task task;
    boolean succeeded;
    
    /** Creates new form ProgressDialog */
    public ProgressDialog(java.awt.Frame parent, String s, Task t) {
        super(parent, true);
        init(parent, s, t);
    }
    
    public ProgressDialog(java.awt.Dialog parent, String s, Task t) {
        super(parent, true);
        init(parent, s, t);
    }
        
    public void init(Component parent, String s, Task t) {
        task = t;
        succeeded = true;
        this.setUndecorated(true);
        
        initComponents();
        
        label.setText(s);
        progBar.setMinimum(0);
        progBar.setValue(0);
        progBar.setStringPainted(true);
        
        task.setProgressDialog(this);
        
        pack();
        Dimension size = getSize();
        setSize(Math.max(200, size.width), size.height);
        setLocationRelativeTo(parent);
        
        task.start();
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }
    
    public void setMaximum(int max) { progBar.setMaximum(max); }
    public void bump() { setProgress(progBar.getValue() + 1); }
    public void setProgress(int val) { progBar.setValue(val); }
    public boolean getSucceeded() { return succeeded; }
    public void setSucceeded(boolean s) { succeeded = s; }
    
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        label = new javax.swing.JLabel();
        progBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2), new javax.swing.border.EmptyBorder(new java.awt.Insets(2, 2, 2, 2))));
        jPanel1.add(label, java.awt.BorderLayout.NORTH);

        jPanel1.add(progBar, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }
    
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label;
    private javax.swing.JProgressBar progBar;
}
