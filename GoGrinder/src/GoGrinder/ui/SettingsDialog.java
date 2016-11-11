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
import java.io.File;

import GoGrinder.*;

/**
 *
 * @author  Tim
 */
public class SettingsDialog extends javax.swing.JDialog {
    private JCheckBox useProxyCB;
    private JTextField proxyHostTF;
    private JTextField proxyPortTF;
    private JLabel hostLB;
    private JLabel portLB;
    private JTextField sgfTF;
    private JCheckBox rightAdvCB;
    
    /** Creates new form SettingsDialog */
    public SettingsDialog(java.awt.Frame parent) {
        super(parent, true);
        setTitle(Messages.getString("gg_settings")); //$NON-NLS-1$
        Container cp = getContentPane();

        topPanel = new Box(BoxLayout.Y_AXIS);
        
        autoAdvCB = new javax.swing.JCheckBox();
        autoAdvCB.setText(Messages.getString("auto_adv_to_next_prob")); //$NON-NLS-1$
        topPanel.add(autoAdvCB);
        
        rightAdvCB = new JCheckBox();
        rightAdvCB.setText(Messages.getString("SettingsDialog.AdvanceOnRightClick")); //$NON-NLS-1$
        topPanel.add(rightAdvCB);

        flipCB = new javax.swing.JCheckBox();
        flipCB.setText(Messages.getString("rand_rot_flip")); //$NON-NLS-1$
        topPanel.add(flipCB);

        flipColorsCB = new javax.swing.JCheckBox();
        flipColorsCB.setText(Messages.getString("rand_swap_colors")); //$NON-NLS-1$
        topPanel.add(flipColorsCB);

        showGhostCB = new javax.swing.JCheckBox();
        showGhostCB.setText(Messages.getString("show_ghost")); //$NON-NLS-1$
        topPanel.add(showGhostCB);

        showWrongPathCB = new javax.swing.JCheckBox();
        showWrongPathCB.setText(Messages.getString("disp_failed_early")); //$NON-NLS-1$
        topPanel.add(showWrongPathCB);

        clickSoundCB = new javax.swing.JCheckBox();
        clickSoundCB.setText(Messages.getString("enable_stone_sound")); //$NON-NLS-1$
        topPanel.add(clickSoundCB);

        soundCB = new javax.swing.JCheckBox();
        soundCB.setText(Messages.getString("enable_right_wrong_sound")); //$NON-NLS-1$
        topPanel.add(soundCB);

        topPanel.add(Box.createVerticalStrut(10));
        
        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalStrut(5));
        box.add(new JLabel(Messages.getString("SettingsDialog.SGFEditor"))); //$NON-NLS-1$
        sgfTF = new JTextField();
        box.add(sgfTF);
        JButton browse = new JButton(Messages.getString("SettingsDialog.Browse")); //$NON-NLS-1$
        browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onBrowse();
			}
		});
        box.add(browse);
        box.add(Box.createHorizontalStrut(10));
        topPanel.add(box);
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        topPanel.add(Box.createVerticalStrut(5));
        
        checkUpdateCB = new javax.swing.JCheckBox();
        checkUpdateCB.setText(Messages.getString("check_version_at_startup")); //$NON-NLS-1$
        checkUpdateCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableProxyFields();
            }
        });
        topPanel.add(checkUpdateCB);

        buttonPanel = new javax.swing.JPanel();
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton = new javax.swing.JButton();
        okButton.setText(Messages.getString("ok")); //$NON-NLS-1$
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onOK();
            }
        });

        buttonPanel.add(okButton);
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        JPanel proxyPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        useProxyCB = new JCheckBox(Messages.getString("proxy_use_proxy")); //$NON-NLS-1$
        useProxyCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableProxyFields();
            }
        });
        proxyPanel.add(useProxyCB, c);
        
        hostLB = new JLabel(Messages.getString("proxy_host")); //$NON-NLS-1$
        c.gridwidth = 1;
        c.gridy = 1;
        c.insets = new Insets(0, 10, 0, 0);
        proxyPanel.add(hostLB, c);
        
        proxyHostTF = new JTextField(15);
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        proxyPanel.add(proxyHostTF, c);
        
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0, 10, 0, 0);
        portLB = new JLabel(Messages.getString("proxy_port")); //$NON-NLS-1$
        proxyPanel.add(portLB, c);
        
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        proxyPortTF = new JTextField(15);
        proxyPanel.add(proxyPortTF, c);
        
        cp.add(topPanel, java.awt.BorderLayout.NORTH);
        
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(Messages.getString("proxy_settings"))); //$NON-NLS-1$
        panel.add(proxyPanel);
        
        cp.add(panel, BorderLayout.CENTER);
        
        cp.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        
        autoAdvCB.setSelected(GS.getAutoAdv());
        rightAdvCB.setSelected(GS.getRightClickAdvance());
        flipCB.setSelected(GS.getFlip());
        flipColorsCB.setSelected(GS.getFlipColors());
        showGhostCB.setSelected(GS.getShowGhost());
        showWrongPathCB.setSelected(GS.getShowWrongPath());
        clickSoundCB.setSelected(GS.getClickSoundEnabled());
        soundCB.setSelected(GS.getSoundEnabled());
        checkUpdateCB.setSelected(GS.getCheckForUpdates());
        useProxyCB.setSelected(GS.getUseProxy());
        proxyHostTF.setText(GS.getProxyHost());
        proxyPortTF.setText(String.valueOf(GS.getProxyPort()));
        File sgf = GS.getSGFEditor();
        if(sgf != null)
        	sgfTF.setText(sgf.getAbsolutePath());

        enableProxyFields();
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void onBrowse() {
    	JFileChooser choose = new JFileChooser();
    	choose.setDialogTitle(Messages.getString("SettingsDialog.SelectSGFEditor")); //$NON-NLS-1$
    	if(choose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
    		sgfTF.setText(choose.getSelectedFile().getAbsolutePath());
    	}
    }
    
    private void enableProxyFields() {
        boolean check = checkUpdateCB.isSelected();
        boolean use = useProxyCB.isSelected();
        useProxyCB.setEnabled(check);
        hostLB.setEnabled(check && use);
        portLB.setEnabled(check && use);
        proxyHostTF.setEnabled(check && use);
        proxyPortTF.setEnabled(check && use);
    }

    private void onOK() {
        boolean use = useProxyCB.isSelected();
        if(use) {
            int port = 0;
            try {
                port = Integer.parseInt(proxyPortTF.getText());
            }
            catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(this, Messages.getString("invalid_port")); //$NON-NLS-1$
                return;
            }
            
            GS.setProxyPort(port);
            GS.setProxyHost(proxyHostTF.getText());
        }
        
        GS.setUseProxy(use);
        GS.setAutoAdv(autoAdvCB.isSelected());
        GS.setRightClickAdvance(rightAdvCB.isSelected());
        GS.setFlip(flipCB.isSelected());
        GS.setFlipColors(flipColorsCB.isSelected());
        GS.setShowGhost(showGhostCB.isSelected());
        GS.setShowWrongPath(showWrongPathCB.isSelected());
        GS.setClickSoundEnabled(clickSoundCB.isSelected());
        GS.setSoundEnabled(soundCB.isSelected());
        GS.setCheckForUpdates(checkUpdateCB.isSelected());
        String sgf = sgfTF.getText();
        if(sgf.length() > 0)
        	GS.setSGFEditor(new File(sgf));

        setVisible(false);
    }

    private javax.swing.JCheckBox autoAdvCB;
    private javax.swing.JCheckBox checkUpdateCB;
    private javax.swing.JCheckBox clickSoundCB;
    private javax.swing.JCheckBox flipCB;
    private javax.swing.JCheckBox flipColorsCB;
    private javax.swing.JButton okButton;
    private Box topPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JCheckBox showGhostCB;
    private javax.swing.JCheckBox showWrongPathCB;
    private javax.swing.JCheckBox soundCB;

}
