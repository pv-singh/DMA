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

import javax.swing.KeyStroke;

import GoGrinder.Main;
import GoGrinder.Messages;
import GoGrinder.WGFController;
import GoGrinder.ui.WGFFrame;

/**
 * @author tkington
 */
public class NewAction extends WGFAction {
	public NewAction(WGFFrame frame, WGFController controller) {
		super(frame, controller);
		putValue(NAME, Messages.getString("NewAction.New")); //$NON-NLS-1$
		putValue(SHORT_DESCRIPTION, Messages.getString("NewAction.NewFile")); //$NON-NLS-1$
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		putValue(SMALL_ICON, Main.getIcon("New16.gif", frame)); //$NON-NLS-1$
	}
	
	public void actionPerformed(ActionEvent evt) {
		controller.onNewFile();
	}
}
