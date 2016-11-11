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

package GoGrinder.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import GoGrinder.Main;
import GoGrinder.Messages;
import GoGrinder.WGFController;
import GoGrinder.ui.WGFFrame;

/**
 * @author tkington
 */
public class WGFOpenAction extends WGFAction {
	public WGFOpenAction(WGFFrame frame, WGFController controller) {
		super(frame, controller);
		putValue(NAME, Messages.getString("WGFOpenAction.Open")); //$NON-NLS-1$
		putValue(SHORT_DESCRIPTION, Messages.getString("open_file")); //$NON-NLS-1$
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		putValue(SMALL_ICON, Main.getIcon("Open16.gif", frame)); //$NON-NLS-1$
	}
	
	public void actionPerformed(ActionEvent evt) {
        String filename = Main.chooseFile(frame, Main.wgfFilter, Messages.getString("WGFFrame.OpenFile"), //$NON-NLS-1$
                                    false, Messages.getString("WGFFrame.Open")); //$NON-NLS-1$
        if(filename == null)
            return;

        try {
            controller.openFile(new File(filename));
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(frame, Messages.getString("WGFFrame.ErrorWhileReadingSeeGrindLog")); //$NON-NLS-1$
            Main.logSilent(e);
        }
	}
}
