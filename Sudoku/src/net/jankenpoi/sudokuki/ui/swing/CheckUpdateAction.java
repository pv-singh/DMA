/*
 * Sudokuki - essential sudoku game
 * Copyright (C) 2007-2016 Sylvain Vedrenne
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jankenpoi.sudokuki.ui.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static net.jankenpoi.i18n.I18n.gtxt;

@SuppressWarnings("serial")
public class CheckUpdateAction extends AbstractAction {

        private Action openUpdateSiteAction;

        private JFrame frame;

        public CheckUpdateAction(JFrame frame, Action openUpdateSiteAction) {
                this.frame = frame;
                this.openUpdateSiteAction = openUpdateSiteAction;
                performSilentCheck();
        }

        public void actionPerformed(ActionEvent e) {
                CheckUpdateDialog dlg = new CheckUpdateDialog(frame, this);
                dlg.setVisible(true);
                int upToDateStatus = dlg.getResult();
                if (upToDateStatus == 0) {
                        JOptionPane.showMessageDialog(frame, "<html>"
                                        + "<table border=\"0\">" + "<tr>"
                                        + gtxt("This version of Sudokuki is up-to-date.") + "</tr>"
                                        + "</html>", "Sudokuki", JOptionPane.PLAIN_MESSAGE);
                } else if (upToDateStatus == 1) {
                        openUpdateSiteAction.setEnabled(true);
                        NewVersionFoundDialog nvDlg = new NewVersionFoundDialog(frame);
                        nvDlg.setVisible(true);
                } else if (upToDateStatus == -1) {
                        JOptionPane.showMessageDialog(frame, "<html>"
                                        + "<table border=\"0\">" + "<tr>"
                                        + gtxt("Unable to retrieve update information.<br/><br/>Please check on the following website<br/>if a new version of Sudokuki is available:<br/><br/>")
                                        + "http://sourceforge.net/projects/sudokuki/files/sudokuki"
                                        + "</tr>" + "</html>", "Sudokuki",
                                        JOptionPane.WARNING_MESSAGE);
                } else {
                        /*
                         * CheckUpdateAction.actionPerformed() CANCELLED");
                         */
                }
        }

        public void performSilentCheck() {
                CheckUpdateDialog dlg = new CheckUpdateDialog(frame, this);
                dlg.setVisible(false);
                // Here I'm supposing that the SwingWorker created by the
                // CheckUpdateDialog will live on and from its done() method will notify
                // this CheckUpdateAction when ready.
        }

        void notifyNewVersionFound() {
        openUpdateSiteAction.setEnabled(true);
        }

}
