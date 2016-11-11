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
import javax.swing.border.*;

import GoGrinder.*;

public class SplashScreen extends JWindow implements StatusListener
{
    private JLabel statusMsg;
    private Cursor oldCursor;

    public SplashScreen(JFrame parent, boolean useImage) {
        super(parent);

        statusMsg = new JLabel(Messages.getString("initializing")); //$NON-NLS-1$
        
        getContentPane().setLayout(new BorderLayout());
        
        Box panel = new Box(BoxLayout.X_AXIS);
        panel.setBorder(new LineBorder(Color.black, 2));
        
        if(useImage) {
            panel.add(new JLabel(
                  new javax.swing.ImageIcon(
                  getClass().getResource("/GoGrinder/images/SplashChar.png")))); //$NON-NLS-1$
        }
        
        Box rPanel = new Box(BoxLayout.Y_AXIS);
        
        if(useImage) {
            rPanel.add(new JLabel(
                  new javax.swing.ImageIcon(
                  getClass().getResource("/GoGrinder/images/SplashText.png")))); //$NON-NLS-1$
        }
        
        Border emptyBorder = BorderFactory.createEmptyBorder(0,2,0,2);
        statusMsg.setBorder(emptyBorder);
        rPanel.add(statusMsg, BorderLayout.SOUTH);
        
        panel.add(rPanel);

        getContentPane().add(panel, BorderLayout.CENTER);
        
        pack();
        if(!useImage)
            setSize(500, getSize().height);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void setVisible(boolean v) {
        if(v) {
            oldCursor = getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        else setCursor(oldCursor);
        
        super.setVisible(v);
    }

    public void setStatus(String msg)
    {
      statusMsg.setText(msg);
    }
}
