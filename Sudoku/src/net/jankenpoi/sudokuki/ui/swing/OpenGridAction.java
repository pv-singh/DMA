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

import static net.jankenpoi.i18n.I18n.gtxt;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.jankenpoi.sudokuki.model.GridModel;
import net.jankenpoi.sudokuki.view.GridView;

@SuppressWarnings("serial")
public class OpenGridAction extends AbstractAction {

	private final GridView view;

	private final JFrame frame;
	
	OpenGridAction(JFrame frame, GridView view) {
		this.frame = frame;
		this.view = view;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		final JFileChooser fc = new JFileChooser();
		
		fc.setDialogTitle(gtxt("Open grid..."));
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new FileFilter() {
			
			public String getExtension(File f) {
		        String ext = null;
		        String s = f.getName();
		        int i = s.lastIndexOf('.');

		        if (i > 0 &&  i < s.length() - 1) {
		            ext = s.substring(i+1).toLowerCase();
		        }
		        return ext;
		    }
			
			@Override
			public String getDescription() {
				return gtxt("Sudokuki grid files");
			}
			
			@Override
			public boolean accept(File f) {
				String extension = getExtension(f);
				if (f.isDirectory() || "skg".equals(extension)) {
					return true;
				}
				return false;
			}
		});
		int returnVal = fc.showOpenDialog(frame);
		
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File fileToOpen = fc.getSelectedFile();
		if (fileToOpen == null) {
			return;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileToOpen);
		} catch (FileNotFoundException e1) {
		}
		if (fis == null) {
            JOptionPane.showMessageDialog(frame, "<html>"
                    + "<table border=\"0\">" + "<tr>"
                    + "File not found:" + "</tr>"
                    + "<tr>" + fileToOpen + "</tr>"
                    + "</html>", "Sudokuki", JOptionPane.PLAIN_MESSAGE);
            return;
		}
		
		short[] externalCellInfos = new short[81];
		try {
			for (int i = 0; i < 81; i++) {
				int lo = fis.read();
				int hi = fis.read();
				short together = (short) (hi << 8 | lo);

				if ((together & ~(GridModel.FLAG_CELL_READ_ONLY
						| GridModel.FLAG_GRID_COMPLETE
						| GridModel.MASK_CELL_MEMOS | GridModel.MASK_CELL_VALUES)) != 0) {
					throw new IOException("Invalid cell info");
				}
				if (9 < (together & GridModel.MASK_CELL_VALUES)) {
					throw new IOException("Cell value out of range");
				}
				externalCellInfos[i] = together;
			}
			view.getController().notifyResetGridFromShorts(externalCellInfos);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(frame, "<html>"
					+ "<table border=\"0\">" + "<tr>"
					+ gtxt("This file is not a Sudokuki grid.") + "</tr>"
					+ "</html>", "Sudokuki", JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				fis.close();
			} catch (IOException e1) {
				System.err.println("An error occured upon FileInputStream close()");
			}
		}
	}
		
}
