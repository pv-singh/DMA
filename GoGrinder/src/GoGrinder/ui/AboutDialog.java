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

import GoGrinder.*;

/**
 *
 * @author  tkington
 */
public class AboutDialog extends javax.swing.JDialog {
    
    /** Creates new form AboutDialog */
    public AboutDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        verLabel.setText(Main.GRINDER_VERSION + " " + Messages.getString("java_ver") //$NON-NLS-1$ //$NON-NLS-2$
                         + " " + System.getProperty("java.version") + Messages.getString("rparen")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        jPanel2 = new javax.swing.JPanel();
        verLabel = new javax.swing.JLabel();
        jTextArea1 = new javax.swing.JTextArea();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Messages.getString("about_gg")); //$NON-NLS-1$
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.EmptyBorder(new java.awt.Insets(3, 3, 3, 3))));
        verLabel.setText(Main.GRINDER_VERSION);
        jPanel2.add(verLabel);

        jTextArea1.setBackground(new java.awt.Color(228, 234, 219));
        jTextArea1.setEditable(false);
        jTextArea1.setText(Messages.getString("copyright1") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
            + Messages.getString("copyright2") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
            + Messages.getString("copyright3") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
            + Messages.getString("copyright4") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
        jTextArea1.setAlignmentX(0.0F);
        jPanel2.add(jTextArea1);

        jTextArea2.setBackground(new java.awt.Color(228, 234, 219));
        jTextArea2.setEditable(false);
        jTextArea2.setLineWrap(true);
        jTextArea2.setText(Messages.getString("gpl1") //$NON-NLS-1$
            + "\n\n" + Messages.getString("gpl2") //$NON-NLS-1$ //$NON-NLS-2$
            + "\n\n" + Messages.getString("gpl3")); //$NON-NLS-1$ //$NON-NLS-2$
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setAlignmentX(0.0F);
        jTextArea2.setMinimumSize(new java.awt.Dimension(100, 153));
        jTextArea2.setPreferredSize(new java.awt.Dimension(100, 270));
        jPanel2.add(jTextArea2);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jPanel1.setAlignmentX(0.0F);
        jButton1.setText(Messages.getString("ok")); //$NON-NLS-1$
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onOK();
            }
        });

        jPanel1.add(jButton1);

        jPanel2.add(jPanel1);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GoGrinder/images/aboutchar.png"))); //$NON-NLS-1$
        jLabel1.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 3, 1, 3)));
        getContentPane().add(jLabel1, java.awt.BorderLayout.WEST);

        pack();
    }

    private void onOK() {
        setVisible(false);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new AboutDialog(new javax.swing.JFrame(), true).show();
    }
    
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JLabel verLabel;
}
