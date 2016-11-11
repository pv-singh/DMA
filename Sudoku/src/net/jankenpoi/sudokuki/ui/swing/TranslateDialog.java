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

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class TranslateDialog extends JDialog {

	private Frame parent;

	public TranslateDialog(Frame parent) {
		super(parent, true);
		this.parent = parent;
		initComponents();
		// setResizable(false);
		setTitle(gtxt("Translate this application"));
		pack();
	}

	private void initComponents() {
		URI sudokukiURI = null;
		try {
			sudokukiURI = new URI("http://sourceforge.net/projects/sudokuki/forums/forum/1801058");
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		JPanel panel = makeInfoPanel(sudokukiURI);



		Dimension parentDim = parent.getPreferredSize();
		Dimension dim = new Dimension();
		dim.setSize(parentDim.getHeight() * 1.75, parentDim.getWidth() * 1.25);
		add(panel);
		pack();
		setLocationRelativeTo(parent);
	}

	protected JPanel makeInfoPanel(final URI sudokukiURI) {
		JPanel panel = new JPanel(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel feedbackPanel = new JPanel(false);
		String feedbackStr = "<html>"
				+ "<table border=\"0\">"
				+ "<tr>"
				+ "</tr>"
				+ "<tr>"
				+ gtxt("You can easily translate Sudokuki into your own language!")+"<br/>"
				+"<br/>"
				+ "</tr>"
				+ "<tr>"
				+ gtxt("Propose your help and get information on how to proceed<br/> on the Translators Forum hosted by Sourceforge:")+"<br/>"
				+ "</tr>"
				+ "<tr>"
				+ "</tr>" + "<tr>" + "</tr>" + "</table>" + "</html>";
		JLabel label = new JLabel(feedbackStr);
		feedbackPanel.add(label);

		panel.add(feedbackPanel);

		JPanel linkPanel = new JPanel(false);
		JButton linkButton = new JButton();

		linkButton
				.setText("<HTML><FONT color=\"#000099\"><U>"+gtxt("Sudokuki Translators Forum")+"</U></FONT></HTML>");
		linkButton.setHorizontalAlignment(SwingConstants.CENTER);
		linkButton.setBorderPainted(false);
		linkButton.setOpaque(false);
		linkButton.setBackground(Color.WHITE);
		linkButton.setToolTipText(sudokukiURI.toString());
		linkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TranslateDialog.this.open(sudokukiURI);
			}
		});
		linkButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		linkPanel.add(linkButton);
		panel.add(linkPanel);

		return panel;
	}

	private void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(uri);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// TODO: error handling
		}
	}

}
