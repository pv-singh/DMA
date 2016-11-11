package sdma.rtc.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sdma.rtc.ui.icon.ToolIcon;
import sdma.rtc.ui.util.Configuration;


/**
 * @author harkomal Root window containing options: 1. Loading/Saving analysis
 *         configurations 2. Running analysis based on current loaded
 *         configuration
 */
public class Main extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5407523940829708491L;
	// screen size parameters
	private final static int screenWidth = 800;
	private final static int screenHeight = 500;

	// configuration option elements
	private JPanel configurePanel = null;
	private JButton configureButton = null;
	private JLabel configureText = null;

	// execute analysis option elements
	private JPanel analysisPanel = null;
	private JButton analysisButton = null;
	private JLabel executeText = null;

	private ConfigurationWindow configurationWindow = null;
	private AnalysisWindow analysisWindow = null;

	// current loaded/saved configuration to be used for analysis
	private static Configuration configuration = null;
	// collection of socket ports used by each new senerio
	private static List<Integer> senerioList = null;

	private static ToolIcon toolIcon = null;
	static {
		toolIcon = new ToolIcon();
	}

	private void initUI() {

		Container container = this.getContentPane();
		container.setLayout(null);

		// initialize configuration option panel
		configurePanel = new JPanel(null);
		configurePanel.setBounds(100, 100, 350, 300);

		configureText = new JLabel("Step 1 - Configuration");

		configureButton = new JButton(new ImageIcon(
				toolIcon.getIcon("config.png")));
		configureButton.setBounds(10, 40, 220, 200);
		configureButton.setMargin(new Insets(0, 0, 0, 0));
		configureButton.setPressedIcon(new ImageIcon(toolIcon
				.getIcon("config-click.png")));
		configureButton.setFocusPainted(false);
		configureButton.setContentAreaFilled(false);
		configureButton.setActionCommand("CONFIGURE_ANALYSIS_PROCESS");
		configureButton.addActionListener(this);
		configurePanel.add(configureButton);

		configureText.setBounds(0, 0, 300, 20);
		configurePanel.add(configureText);

		// initialize analysis option panel
		analysisPanel = new JPanel(null);
		analysisPanel.setBounds(450, 100, 350, 300);

		executeText = new JLabel("Step 2 - Capture and Analyse");

		analysisButton = new JButton(new ImageIcon(
				toolIcon.getIcon("execute.png")));
		analysisButton.setBounds(10, 40, 220, 200);
		analysisButton.setMargin(new Insets(0, 0, 0, 0));
		analysisButton.setPressedIcon(new ImageIcon(toolIcon
				.getIcon("execute-click.png")));
		analysisButton.setFocusPainted(false);
		analysisButton.setContentAreaFilled(false);
		analysisButton.setActionCommand("EXECUTE_ANALYSIS_PROCESS");
		analysisButton.addActionListener(this);
		analysisPanel.add(analysisButton);

		executeText.setBounds(0, 0, 300, 20);
		analysisPanel.add(executeText);

		// add configuration and execute panels in main window
		container.add(configurePanel);
		container.add(analysisPanel);

		// make window resizable disable
		this.setResizable(false);
		// make application exit on default close
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		// make application visible
		this.setVisible(true);
		// set location and size of window
		this.setBounds((int) (getScreenSize().getWidth() - screenWidth) / 2,
				(int) (getScreenSize().getHeight() - screenHeight) / 2,
				screenWidth, screenHeight);
	}

	public Main() {
		super("Software Dynamic Metrics Analysis (SDMA) - Tool");
		initUI();
		senerioList=new ArrayList<Integer>();
		configurationWindow = new ConfigurationWindow();
		analysisWindow = new AnalysisWindow();
	}

	public static void addSenerio(Integer port) {
		senerioList.add(port);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Properties properties = new Properties();
		if (command.equals("CONFIGURE_ANALYSIS_PROCESS")) {
			File f = new File("configurations.properties");
			//if environment properties are already save
			if (f.exists()) {
				InputStream in;
				try {
					in = new FileInputStream("configurations.properties");
					//load environment properties
					properties.load(in);
					in.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				//initialize environment properties in configuration window
 				configurationWindow.setScalsBaseDir(properties.getProperty("scalv_base_dir"));
				configurationWindow.setJdkHomeDir(properties.getProperty("jdk_base_dir"));
				//show configuration window
				configurationWindow.setVisible(true);
			} else {
				new ConfigEnvironment();
			}
		} else if (command.equals("EXECUTE_ANALYSIS_PROCESS")) {
			analysisWindow.setVisible(true);
		}
	}

	private Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	public static void setConfiguration(Configuration configuration) {
		Main.configuration = configuration;
	}

	public static List<Integer> getSenerioList() {
		return senerioList;
	}

	public static void main(String[] args) {
		new Main();
	}

}
