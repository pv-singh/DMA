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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import GoGrinder.Messages;

/**
 * @author tkington
 */
public class NewVersionDialog extends JDialog {
	private boolean download = false;
	
	public NewVersionDialog(String changes) {
		super((JFrame)null, true);
		setTitle(Messages.getString("update_available")); //$NON-NLS-1$
		
		Container cp = getContentPane();
		
		cp.add(new JScrollPane(new JTextArea(changes)), BorderLayout.CENTER);

		Box buttonPanel = new Box(BoxLayout.X_AXIS);
		
		buttonPanel.add(new JLabel(Messages.getString("update_new_version_available"))); //$NON-NLS-1$
		
		buttonPanel.add(Box.createHorizontalGlue());
		
		JButton b = new JButton(Messages.getString("yes")); //$NON-NLS-1$
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				download = true;
				setVisible(false);
			}
		});
		buttonPanel.add(b);
		
		b = new JButton(Messages.getString("no")); //$NON-NLS-1$
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				download = false;
				setVisible(false);
			}
		});
		buttonPanel.add(b);
		
		cp.add(buttonPanel, BorderLayout.SOUTH);
		
		setSize(475, 500);
        setLocationRelativeTo(null);
        setVisible(true);
	}
	
	public boolean getNewVersion() { return download; }
}
