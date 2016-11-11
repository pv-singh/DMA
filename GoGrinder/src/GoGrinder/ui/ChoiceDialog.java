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

import java.awt.Rectangle;
import java.io.*;
import java.text.*;
import java.util.*;

import GoGrinder.Messages;

/**
 *
 * @author  tkington
 */
public class ChoiceDialog extends javax.swing.JDialog {
    public static final int YES_OPTION = 1;
    public static final int NO_OPTION = 2;
    public static final int YES_ALL_OPTION = 3;
    public static final int NO_ALL_OPTION = 4;
    
    private static final SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy, H:mm:ss aa"); //$NON-NLS-1$
    private static final DecimalFormat sizeFormat = new DecimalFormat();
    
    {
        sizeFormat.setMinimumFractionDigits(2);
        sizeFormat.setMaximumFractionDigits(2);
    }
    
    private int sel;
    
    /** Creates new form ConfirmDialog */
    public ChoiceDialog(java.awt.Frame parent, File f, long newTime, long newSize) {
        super(parent, true);
        
        initComponents();
        
        String fname = f.getPath();
        if(fname.startsWith("problems" + File.separatorChar)) //$NON-NLS-1$
            fname = fname.substring(9);
        filenameLabel.setText(Messages.getString("the_file") + " " + fname); //$NON-NLS-1$ //$NON-NLS-2$
        
        oldFileLabel.setText(sizeToStr(f.length()) + " "  //$NON-NLS-1$
                            + Messages.getString("modified_colon") + " " //$NON-NLS-1$ //$NON-NLS-2$
                            + df.format(new Date(f.lastModified())));
        newFileLabel.setText(sizeToStr(newSize) + " " //$NON-NLS-1$
                            + Messages.getString("modified_colon") + " " //$NON-NLS-1$ //$NON-NLS-2$
                            + df.format(new Date(newTime)));
        
        pack();
        Rectangle r = getBounds();
        System.out.println(r);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }
    
    String sizeToStr(long size) {
        double d = (double)size / 1024;
        return sizeFormat.format(d) + " " + Messages.getString("kilobytes"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void initComponents() {
        jPanel2 = new javax.swing.JPanel();
        filenameLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        oldFileLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        newFileLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Messages.getString("confirm_file_op")); //$NON-NLS-1$
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 5, 5, 5)));
        jPanel2.add(filenameLabel);

        jLabel2.setText(Messages.getString("already_exists_want_to_replace")); //$NON-NLS-1$
        jPanel2.add(jLabel2);

        jLabel3.setText(" "); //$NON-NLS-1$
        jPanel2.add(jLabel3);

        jPanel2.add(oldFileLabel);

        jLabel5.setText(" "); //$NON-NLS-1$
        jPanel2.add(jLabel5);

        jLabel6.setText(Messages.getString("with_this_one")); //$NON-NLS-1$
        jPanel2.add(jLabel6);

        jLabel7.setText(" "); //$NON-NLS-1$
        jPanel2.add(jLabel7);

        jPanel2.add(newFileLabel);

        jLabel9.setText(" "); //$NON-NLS-1$
        jPanel2.add(jLabel9);

        getContentPane().add(jPanel2);

        jPanel1.setAlignmentX(0.0F);
        jButton1.setText(Messages.getString("yes")); //$NON-NLS-1$
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onYes();
            }
        });

        jPanel1.add(jButton1);

        jButton2.setText(Messages.getString("no")); //$NON-NLS-1$
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onNo();
            }
        });

        jPanel1.add(jButton2);

        jButton3.setText(Messages.getString("yes_to_all")); //$NON-NLS-1$
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onYesAll();
            }
        });

        jPanel1.add(jButton3);

        jButton4.setText(Messages.getString("no_to_all")); //$NON-NLS-1$
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onNoAll();
            }
        });

        jPanel1.add(jButton4);

        getContentPane().add(jPanel1);
    }

    private void onNoAll() {
        sel = NO_ALL_OPTION;
        setVisible(false);
    }

    private void onYesAll() {
        sel = YES_ALL_OPTION;
        setVisible(false);
    }

    private void onNo() {
        sel = NO_OPTION;
        setVisible(false);
    }

    private void onYes() {
        sel = YES_OPTION;
        setVisible(false);
    }
        
    public int getSelection() { return sel; }
    
    private javax.swing.JLabel filenameLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel newFileLabel;
    private javax.swing.JLabel oldFileLabel;
}
