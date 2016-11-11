package sdma.rtc.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import sdma.rtc.ui.icon.ToolIcon;

public class ConfigEnvironment extends JFrame implements ActionListener {

	private JLabel scalsBaseDirLb = null;
	private JTextField scalsBaseDirTf = null;
	private JButton scalsBaseDirBt = null;

	private JLabel jdkBaseDirLb = null;
	private JTextField jdkBaseDirTf = null;
	private JButton jdkBaseDirBt = null;

	private JButton saveEnvironmentBt = null;

	private static ToolIcon toolIcon = null;
	static {
		toolIcon = new ToolIcon();
	}

	public ConfigEnvironment() {
		initUI();
	}

	private void initUI() {

		Container con = this.getContentPane();

		con.setLayout(null);

		scalsBaseDirLb = new JLabel("Tool Base Dir");
		scalsBaseDirTf = new JTextField();
		scalsBaseDirTf.setEditable(false);
		scalsBaseDirBt = new JButton(new ImageIcon(
				toolIcon.getIcon("browse.png")));
		scalsBaseDirBt.setBorder(null);
		scalsBaseDirBt.setMargin(new Insets(0, 0, 0, 0));
		scalsBaseDirBt.setFocusPainted(false);
		scalsBaseDirBt.setContentAreaFilled(false);
		scalsBaseDirBt.setActionCommand("BROWSE_BASE_DIR");
		scalsBaseDirBt.addActionListener(this);

		jdkBaseDirLb = new JLabel("JDK Home Dir");
		jdkBaseDirTf = new JTextField();
		jdkBaseDirTf.setEditable(false);
		jdkBaseDirBt = new JButton(
				new ImageIcon(toolIcon.getIcon("browse.png")));
		jdkBaseDirBt.setBorder(null);
		jdkBaseDirBt.setMargin(new Insets(0, 0, 0, 0));
		jdkBaseDirBt.setFocusPainted(false);
		jdkBaseDirBt.setContentAreaFilled(false);
		jdkBaseDirBt.setActionCommand("BROWSE_JDK_DIR");
		jdkBaseDirBt.addActionListener(this);

		saveEnvironmentBt = new JButton("Save");

		scalsBaseDirLb.setBounds(10, 6, 120, 25);
		scalsBaseDirTf.setBounds(120, 10, 380, 25);
		scalsBaseDirBt.setBounds(510, 4, 30, 30);

		jdkBaseDirLb.setBounds(10, 46, 120, 25);
		jdkBaseDirTf.setBounds(120, 50, 380, 25);
		jdkBaseDirBt.setBounds(510, 44, 30, 30);

		saveEnvironmentBt.setBounds(240, 90, 100, 25);
		saveEnvironmentBt.setActionCommand("SAVE_CONFIG");
		saveEnvironmentBt.addActionListener(this);

		con.add(scalsBaseDirLb);
		con.add(scalsBaseDirTf);
		con.add(scalsBaseDirBt);

		con.add(jdkBaseDirLb);
		con.add(jdkBaseDirTf);
		con.add(jdkBaseDirBt);

		con.add(saveEnvironmentBt);

		this.setBounds((int) (getScreenSize().getWidth() - 560) / 2,
				(int) (getScreenSize().getHeight() - 200) / 2, 560, 150);
		this.setVisible(true);
	}

	private Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public static void main(String[] args) {
		new ConfigEnvironment();
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if (action.equals("BROWSE_BASE_DIR")) {
			showHomeDirDialog();
		} else if (action.equals("BROWSE_JDK_DIR")) {
			showJdkDirDialog();
		} else {
			saveEnvironmentConfigs();
			preprateJP2Environment();
		}

	}

	private void saveEnvironmentConfigs() {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream("configurations.properties");
			prop.setProperty("scalv_base_dir", scalsBaseDirTf.getText());
			prop.setProperty("jdk_base_dir", jdkBaseDirTf.getText());
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
					this.dispose();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private void showHomeDirDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System
				.getProperty("user.home")));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			scalsBaseDirTf.setText(selectedFile.getAbsolutePath() + "/JP2A/");

		}
	}

	private void showJdkDirDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System
				.getProperty("user.home")));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			jdkBaseDirTf.setText(selectedFile.getAbsolutePath());

		}
	}
	
	private void preprateJP2Environment() {
		byte[] buf = new byte[1024];
		InputStream input = null;
		OutputStream output = null;
		int bytesRead;

		try {
			input = new FileInputStream(new File(this.getClass()
					.getResource("icon/cluster.png").toString().replace("file:", "")
					.replace("/bin", "")
					.replace("/target/classes", "")
					.replace("sdma/rtc/ui/icon/cluster.png", "")
					+ "resources.zip"));
			output = new FileOutputStream(scalsBaseDirTf.getText() + "resources.zip");
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
			unzip(scalsBaseDirTf.getText(), "resources.zip");
			} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				input.close();
				output.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void unzip(String dirPath, String fileName) throws IOException {
		File destDir = new File(dirPath);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(dirPath
				+ fileName));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = dirPath + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				extractFile(zipIn, filePath);
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}
	
	private void extractFile(ZipInputStream zipIn, String filePath)
			throws IOException {
		final int BUFFER_SIZE = 4096;
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

}
