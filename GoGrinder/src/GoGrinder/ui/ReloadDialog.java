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
import javax.swing.*;
import javax.swing.border.*;

import GoGrinder.*;

public class ReloadDialog extends JDialog implements StatusListener {
    private JLabel statusMsg;
    
    /** Creates a new instance of ReloadDialog */
    private ReloadDialog(JFrame parent) {
        super(parent, true);
        init(parent);
    }
    
    private ReloadDialog(JDialog parent) {
        super(parent, true);
        init(parent);
    }

    private void init(Component parent) {
        statusMsg = new JLabel(Messages.getString("initializing")); //$NON-NLS-1$
        
        setUndecorated(true);
        
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        
        Border lineBorder = BorderFactory.createLineBorder(Color.black, 2);
        Border emptyBorder = BorderFactory.createEmptyBorder(0,2,0,2);
        statusMsg.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
        cp.add(statusMsg);
        
        pack();
        setSize(500, getSize().height);
        setLocationRelativeTo(parent);
        
        Thread t = new Thread() {
            public void run() {
                try { Thread.sleep(50); } catch(InterruptedException e) { /* */ }
                GS.setCollections(new ProbCollection(null, new File("problems"), ReloadDialog.this)); //$NON-NLS-1$
                setVisible(false);
            }
        };
        t.start();
        
        setVisible(true);
    }
    
    public void setStatus(String s) { statusMsg.setText(s); }
    
    public static void reloadProblems(JFrame parent) { new ReloadDialog(parent); }
    public static void reloadProblems(JDialog parent) { new ReloadDialog(parent); }
}
