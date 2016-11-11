package net.jankenpoi.sudokuki.ui.swing;

import static net.jankenpoi.i18n.I18n.gtxt;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.jankenpoi.i18n.I18n;
import net.jankenpoi.sudokuki.view.GridView;

@SuppressWarnings("serial")
public class SaveAsAction extends AbstractAction {
	private final GridView view;
	private final JFrame frame;

	SaveAsAction(JFrame frame, GridView view) {
		this.frame = frame;
		this.view = view;
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser() {

			@Override
			public File getSelectedFile() {
				File file = super.getSelectedFile();
				if (file == null) {
					return new File("myGrid.skg");
				}
				if (!"skg".equals(getExtension(file))) {
					file = new File(String.valueOf(file.getAbsolutePath())
							+ ".skg");
				}
				return file;
			}
		};

		fc.setDialogTitle(I18n.gtxt("Save as..."));
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				I18n.gtxt("Sudokuki grid files"), new String[] { "skg" });
		fc.setFileFilter(filter);
		int returnVal = fc.showSaveDialog(this.frame);

		if (returnVal != 0) {
			return;
		}
		File fileToSave = fc.getSelectedFile();
		if (fileToSave == null) {
			return;
		}
		fileToSave.delete();
		try {
			fileToSave.createNewFile();
		} catch (IOException e1) {
            JOptionPane.showMessageDialog(frame, "<html>"
                    + "<table border=\"0\">" + "<tr>"
                    + gtxt("Failed to save the grid<br/>at the selected location.") + "</tr>"
                    + "</html>", "Sudokuki", JOptionPane.ERROR_MESSAGE);
            return;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileToSave);
		} catch (FileNotFoundException e1) {
            JOptionPane.showMessageDialog(frame, "<html>"
                    + "<table border=\"0\">" + "<tr>"
                    + gtxt("Failed to save the grid<br/>at the selected location.") + "</tr>"
                    + "</html>", "Sudokuki", JOptionPane.ERROR_MESSAGE);
            return;
		}

		int[] cellInfos = this.view.getController().getCellInfosFromModel();
		for (int i = 0; i < cellInfos.length; i++) {
			byte lo = (byte) (cellInfos[i] & 0xFF);
			byte hi = (byte) ((cellInfos[i] & 0xFF00) >> 8);
			try {
				fos.write(lo);
				fos.write(hi);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		try {
			fos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static String getExtension(File file) {
		String ext = null;
		String s = file.getName();
		int i = s.lastIndexOf('.');

		if ((i > 0) && (i < s.length() - 1)) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}
