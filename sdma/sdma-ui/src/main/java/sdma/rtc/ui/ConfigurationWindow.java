package sdma.rtc.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import sdma.rtc.entity.Action;
import sdma.rtc.entity.Packet;
import sdma.rtc.entity.Source;
import sdma.rtc.server.Server;
import sdma.rtc.ui.icon.ToolIcon;
import sdma.rtc.ui.util.Configuration;

public class ConfigurationWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = -7048237649167992890L;

	private final static int screenWidth = 1000;
	private final static int screenHeight = 630;

	private JLabel serverConfigLb = null;
	private JTextField serverConfigTf = null;
	
	// application path elements
	private JLabel applicationPathLb = null;
	private JTextField applicationPathTf = null;
	private JButton applicationPathBt = null;

	// application supportive libs path
	private JLabel supportLibsPathLb = null;
	private JList supportLibsPathLst = null;
	private DefaultListModel supportiveLibsModel = null;
	private JButton supportLibsPathBt = null;
	private JScrollPane supportiveLibsSP = null;

	// application packages elements
	private JLabel applicationPackgsLb = null;
	private JSpinner spinner = null;
	private JList applicationPackgsLst = null;
	private DefaultListModel applicationPackgsModel = null;
	private JScrollPane applicationLibsSP = null;
	private JButton applicationPacksBut = null;

	// save configuration as defined
	private JButton saveConfigurationBut = null;

	private JPanel topPanel = null;
	private JPanel bottomPanel = null;

	private static List<ClusterComponent> clusters = null;
	private JButton pingClustersBut = null;

	private static Integer SUPPORTED_CLUSTERS = 4;
	private String scalsBaseDir = null;
	private String jdkHomeDir = null;
	private List<String> applicationPacks = null;
	private Server server = null;

	private static Integer BUFFER_SIZE = 200000;

	private static ToolIcon toolIcon = null;
	static {
		toolIcon = new ToolIcon();
		clusters = new ArrayList<ClusterComponent>();
	}

	private void addCluster(ClusterComponent cluster, int count) {
		cluster.setBounds(10 + (225 * count), 50, 240, 130);
		JLabel clusterLb = new JLabel("Gear " + (count + 1));
		clusterLb.setBounds(35 + (count * 225), 180, 250, 25);
		bottomPanel.add(cluster);
		bottomPanel.add(clusterLb);
		clusters.add(cluster);
	}

	public ConfigurationWindow() {
		super("Configuration Window");
		this.scalsBaseDir = scalsBaseDir;
		this.jdkHomeDir = jdkHomeDir;

		Container container = this.getContentPane();
		container.setLayout(null);

		serverConfigLb = new JLabel("Select Cloud");
		serverConfigTf = new JTextField();
		

		applicationPathLb = new JLabel("Application (.jar)");
		applicationPathTf = new JTextField();
		applicationPathBt = new JButton(new ImageIcon(
				toolIcon.getIcon("browse.png")));

		supportLibsPathLb = new JLabel("Supportive Libs (.jars)");
		supportLibsPathLst = new JList();
		supportLibsPathBt = new JButton(new ImageIcon(
				toolIcon.getIcon("browse.png")));

		applicationPackgsLb = new JLabel("Application Packages");
		SpinnerListModel monthModel = new SpinnerListModel(new String[] { "1",
				"2", "3", "4", "5", "6", "7", "8" });
		spinner = new JSpinner(monthModel);
		applicationPackgsModel = new DefaultListModel();
		applicationPackgsLst = new JList(applicationPackgsModel);

		pingClustersBut = new JButton("Ping Cluster Gears");

		topPanel = new JPanel(null);
		topPanel.setBounds(20, 30, 950, 200);

		bottomPanel = new JPanel(null);
		bottomPanel.setBounds(20, 250, 950, 230);

		ClusterComponent cluster = null;
		for (int i = 0; i < SUPPORTED_CLUSTERS; ++i) {
			addCluster(new ClusterComponent(), i);
		}

		pingClustersBut.setBounds(27, 10, 180, 30);
		pingClustersBut.setActionCommand("PING_CLUSTERS");
		pingClustersBut.addActionListener(this);
		bottomPanel.add(pingClustersBut);

		serverConfigLb.setBounds(10, 10, 160, 22);
		serverConfigTf.setBounds(180, 10, 300, 22);
		
		topPanel.add(serverConfigLb);
		topPanel.add(serverConfigTf);
	
		applicationPathLb.setBounds(10, 55, 160, 22);
		applicationPathTf.setBounds(180, 57, 290, 22);
		applicationPathTf.setEditable(false);
		applicationPathBt.setBounds(480, 50, 30, 30);
		applicationPathBt.setBorder(null);
		applicationPathBt.setMargin(new Insets(0, 0, 0, 0));
		applicationPathBt.setFocusPainted(false);
		applicationPathBt.setContentAreaFilled(false);
		applicationPathBt.setActionCommand("MAIN_APP");
		applicationPathBt.addActionListener(this);

		topPanel.add(applicationPathLb);
		topPanel.add(applicationPathTf);
		topPanel.add(applicationPathBt);

		supportLibsPathLb.setBounds(10, 98, 160, 22);

		supportLibsPathBt.setBounds(480, 96, 30, 30);
		supportLibsPathBt.setBorder(null);
		supportLibsPathBt.setMargin(new Insets(0, 0, 0, 0));
		supportLibsPathBt.setFocusPainted(false);
		supportLibsPathBt.setContentAreaFilled(false);
		supportLibsPathBt.setActionCommand("SUPPORT_LIBS");
		supportLibsPathBt.addActionListener(this);

		supportiveLibsModel = new DefaultListModel();
		supportLibsPathLst.setModel(supportiveLibsModel);

		supportiveLibsSP = new JScrollPane();
		supportiveLibsSP.setViewportView(supportLibsPathLst);
		supportiveLibsSP.setBounds(180, 102, 290, 80);

		applicationPackgsLb.setBounds(550, 7, 200, 22);
		spinner.setBounds(830, 7, 40, 24);

		applicationLibsSP = new JScrollPane();
		applicationLibsSP.setViewportView(applicationPackgsLst);
		applicationLibsSP.setBounds(550, 38, 380, 145);

		applicationPacksBut = new JButton(new ImageIcon(
				toolIcon.getIcon("save.png")));
		applicationPacksBut.setBounds(880, 7, 40, 25);
		applicationPacksBut.setActionCommand("UPDATE_PACKAGE_COMPONENT");
		applicationPacksBut.addActionListener(this);

		saveConfigurationBut = new JButton("Update Configs");
		saveConfigurationBut.setBounds(430, 510, 160, 40);
		saveConfigurationBut.setActionCommand("UPDATE_CONFIGURATIONS");
		saveConfigurationBut.addActionListener(this);

		topPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED));
		bottomPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED));
		topPanel.add(supportLibsPathLb);
		topPanel.add(supportiveLibsSP);
		topPanel.add(supportLibsPathBt);

		topPanel.add(applicationPackgsLb);
		topPanel.add(spinner);
		topPanel.add(applicationPacksBut);
		topPanel.add(applicationLibsSP);

		container.add(saveConfigurationBut);

		container.add(topPanel);
		container.add(bottomPanel);

		refreshServerConfigs();

		this.setResizable(false);

		this.setBounds((int) (getScreenSize().getWidth() - screenWidth) / 2,
				(int) (getScreenSize().getHeight() - screenHeight) / 2,
				screenWidth, screenHeight);
	}

	private void refreshServerConfigs() {
		Properties prop = new Properties();
		InputStream in;
		if (new File("server-location.properties").exists()) {
			try {
				in = new FileInputStream("server-location.properties");
				prop.load(in);
				Iterator<Object> it = prop.keySet().iterator();
				String key = null;
				serverConfigTf.removeAll();
				while (it.hasNext()) {
					key = it.next().toString();
					serverConfigTf.setText(prop.get(key).toString());
				}
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void mainApplicationDialog() {
		// create a file chooser dialog
		JFileChooser fileChooser = new JFileChooser();

		// set user home as initial base dir
		fileChooser.setCurrentDirectory(new File(System
				.getProperty("user.home")));
		// allow choose only files
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		// filter for file extensions only jars
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"Java Archive", "jar"));
		// show and handle dialog
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			applicationPathTf.setText(selectedFile.getAbsolutePath());
			// load all the packages of selected application jar
			List<String> packs = loadPackages(selectedFile.getAbsolutePath());
			// add them to list of packages for component grouping
			for (String pack : packs) {
				applicationPackgsModel.addElement("[]" + pack);
			}
			applicationPackgsLst.setModel(applicationPackgsModel);
		}
	}

	private List<String> loadPackages(String jarFilePath) {
		applicationPacks = new ArrayList<String>();
		Set<String> packs = new HashSet<String>();
		try {
			// create an executable command shell script in base dir
			File commandFile = new File(scalsBaseDir + "command.sh");
			commandFile.setExecutable(true);

			// write command to list jar packages in shell script
			FileOutputStream outputStream = new FileOutputStream(commandFile);
			outputStream.write(("jar tf " + jarFilePath).getBytes());
			outputStream.close();

			// create a java process for command
			ProcessBuilder pb = new ProcessBuilder("./command.sh");
			pb.directory(new File(scalsBaseDir));
			pb.redirectErrorStream(true);

			// start the process to list packages
			Process process = pb.start();
			System.out.println("Reading");
			// read each output line one by one for package names
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				// exclude META-INF dirs
				if (line.indexOf(".") == -1) {
					if (line.indexOf("META-INF") == -1) {
						packs.add(line);
					}
				} else {
					if (line.indexOf("MANIFEST.MF") == -1) {
						if (line.indexOf("META-INF") == -1) {
							if (line.lastIndexOf("/") > -1) {
								packs.add(line.substring(0,
										line.lastIndexOf("/")));
							} else {
								packs.add(line);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Iterator<String> it = packs.iterator();
		while (it.hasNext()) {
			applicationPacks.add(it.next());
		}
		return applicationPacks;
	}

	private void supportiveLibsDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System
				.getProperty("user.home")));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"Java Archive", "jar"));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			supportiveLibsModel = new DefaultListModel<String>();
			File[] selectedFile = fileChooser.getSelectedFiles();
			for (File f : selectedFile) {
				supportiveLibsModel.addElement(f.getAbsolutePath());
			}
			supportLibsPathLst.setModel(supportiveLibsModel);
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("NEW_SERVER_CONFIG")) {
			// create new server configuration
			new ConfigServerWindow();
		} else if (command.equals("MAIN_APP")) {
			// load main application and packages
			mainApplicationDialog();
		} else if (command.equals("SUPPORT_LIBS")) {
			// load supportive libs
			supportiveLibsDialog();
		} else if (command.equals("UPDATE_PACKAGE_COMPONENT")) {
			int[] selIndex = applicationPackgsLst.getSelectedIndices();
			applicationPackgsModel = (DefaultListModel) applicationPackgsLst
					.getModel();
			for (int i = 0; i < selIndex.length; ++i) {
				String elem = applicationPackgsModel.getElementAt(selIndex[i])
						.toString();
				elem = "[" + spinner.getValue()
						+ elem.substring(elem.indexOf("]"));
				applicationPackgsModel.setElementAt(elem, selIndex[i]);
			}
			applicationPackgsLst.setModel(applicationPackgsModel);
			int curVal = Integer.parseInt(spinner.getValue().toString());
			spinner.setValue(curVal == 8 ? "1" : (curVal + 1) + "");
		} else if (command.equals("PING_CLUSTERS")) {
			
				try{
				Socket serverDS = null;
				ObjectOutputStream out = null;
				ObjectInputStream oin = null;

				String serverConfig = serverConfigTf
						.getText();
				if (server != null) {
					server.stop();
					server = null;
				}
				if (serverConfig != null && !serverConfig.equals("")) {
					String[] configComps = serverConfig.split(" ");
					if (configComps.length <= 2) {
						System.out.println("No cluster defined.");
					} else {
						if (configComps.length % 2 == 1) {
							System.out
									.println("Clusters are not defined properly.");
						} else {
							List<String> clusterConfigs = new ArrayList<String>();
							for (int i = 2; i < configComps.length; i = i + 2) {
								clusterConfigs.add(configComps[i] + " "
										+ configComps[i + 1]);
							}
							server = new Server(configComps[0],
									Integer.parseInt(configComps[1]),
									clusterConfigs);
						}
					}
				
			
					serverDS = new Socket(
							InetAddress.getByName(configComps[0]),
							Integer.parseInt(configComps[1]));
					serverDS.setSoTimeout(20000);

					Packet packet = new Packet();
					packet.setAction(Action.LIST_AC);
					packet.setSource(Source.SCAT);

					oin = new ObjectInputStream(serverDS.getInputStream());
					oin.readObject();

					out = new ObjectOutputStream(serverDS.getOutputStream());
					out.writeObject(packet);

					packet = (Packet) oin.readObject();
					List<Integer> clusters = (List<Integer>) packet.getData()
							.get("ACTIVE_CLUSTERS");
					populateActiveClusters(clusters);

					oin.close();
					out.close();
					serverDS.close();
				}

			} catch (SocketException e1) {
				e1.printStackTrace();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}

		} else if (command.equals("UPDATE_CONFIGURATIONS")) {

			// get application jar path
			String appplication = applicationPathTf.getText();
			// collection to store supportive jars and application packages
			List<String> supportJars = new ArrayList<String>();
			Map<Integer, List<String>> compoPacks = new HashMap<Integer, List<String>>();

			// store supportive jar paths in local collection
			DefaultListModel<String> model = (DefaultListModel<String>) supportLibsPathLst
					.getModel();
			for (int i = 0; i < model.size(); ++i) {
				supportJars.add(model.getElementAt(i));
			}

			// store component packages in local collection
			model = (DefaultListModel<String>) applicationPackgsLst.getModel();
			List<String> packs = null;
			for (int i = 0; i < model.size(); ++i) {
				int extractComp = Integer.parseInt(model.getElementAt(i)
						.charAt(1) + "");
				String pack = model.getElementAt(i).substring(3);
				packs = compoPacks.get(extractComp);
				if (packs == null) {
					packs = new ArrayList<String>();
				}
				packs.add(pack);
				System.out.println("comp" + extractComp + "package"
						+ packs.size());
				compoPacks.put(extractComp, packs);
			}

			// collection to store each cluster components
			Map<Integer, List<Integer>> clusterComponents = new HashMap<Integer, List<Integer>>();
			int clCount = 1;
			for (ClusterComponent clusterComp : clusters) {
				if (clusterComp.isActive()) {
					clusterComponents.put(clCount,
							clusterComp.listAssignedComponents());
				}
				++clCount;
			}
			String[] location = serverConfigTf.getText().split(" ");
			if (location.length <= 2) {
				System.out.println("No cluster defined.");
			} else {
				if (location.length % 2 == 1) {
					System.out.println("Gears are not defined properly.");
				} else {
					List<String> clusterConfigs = new ArrayList<String>();
					for (int i = 2; i < location.length; i = i + 2) {
						clusterConfigs.add(location[0] + " " + location[1]);
					}
					Main.setConfiguration(new Configuration(scalsBaseDir, -1,
							jdkHomeDir, appplication, supportJars,
							applicationPacks, compoPacks, clusterComponents,
							location[0], Integer.parseInt(location[1]),
							clusterConfigs));
				}
			}

		}

	}

	private void populateActiveClusters(List<Integer> activeClusters) {

		// deactivate all clusters
		for (ClusterComponent cl : clusters) {
			cl.deactivate();
		}

		for (Integer i : activeClusters) {
			clusters.get(i - 1).activate();
		}
	}

	private Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	class ConfigServerWindow extends JFrame implements ActionListener {

		// config name for cluster
		private JLabel clusterConfigNameLb = null;
		private JTextField clusterConfigNameTf = null;

		// configuration for cluster socket parameters
		private JLabel clusterHostLb = null;
		private JTextField clusterHostTf = null;
		private JLabel clusterPortLb = null;
		private JTextField clusterPortTf = null;

		private JButton saveConfigBt = null;

		public ConfigServerWindow() {

			super("Server Location Configuration");

			initUI();

			this.setBounds((int) (getScreenSize().getWidth() - 450) / 2,
					(int) (getScreenSize().getHeight() - 300) / 2, 450, 220);
			this.setVisible(true);
		}

		private void initUI() {

			Container con = this.getContentPane();
			con.setLayout(null);

			clusterConfigNameLb = new JLabel("Config Name");
			clusterConfigNameTf = new JTextField();

			clusterConfigNameLb.setBounds(10, 10, 130, 25);
			clusterConfigNameTf.setBounds(160, 10, 120, 25);

			clusterHostLb = new JLabel("Host");
			clusterHostTf = new JTextField();

			clusterHostLb.setBounds(10, 45, 130, 25);
			clusterHostTf.setBounds(160, 45, 200, 25);

			clusterPortLb = new JLabel("Port");
			clusterPortTf = new JTextField();

			clusterPortLb.setBounds(10, 80, 130, 25);
			clusterPortTf.setBounds(160, 80, 120, 25);

			saveConfigBt = new JButton("Save");
			saveConfigBt.setBounds(160, 120, 80, 25);
			saveConfigBt.addActionListener(this);

			con.add(clusterConfigNameLb);
			con.add(clusterConfigNameTf);

			con.add(clusterHostLb);
			con.add(clusterHostTf);
			con.add(clusterPortLb);
			con.add(clusterPortTf);
			con.add(saveConfigBt);

		}

		private Dimension getScreenSize() {
			return Toolkit.getDefaultToolkit().getScreenSize();
		}

		private void updaServerConfigs() {
			Properties prop = new Properties();
			File configFile = new File("server-location.properties");
			OutputStream output = null;
			if (configFile.exists()) {

				InputStream in;
				try {
					in = new FileInputStream("server-location.properties");
					prop.load(in);
					in.close();
					if (prop.get(clusterConfigNameTf.getText()) != null) {
						prop.setProperty(
								clusterConfigNameTf.getText(),
								clusterHostTf.getText() + " "
										+ clusterPortTf.getText());
					} else {
						prop.put(
								clusterConfigNameTf.getText(),
								clusterHostTf.getText() + " "
										+ clusterPortTf.getText());
					}
					output = new FileOutputStream("server-location.properties");
					prop.store(output, null);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
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
			} else {

				try {
					output = new FileOutputStream("server-location.properties");
					prop.setProperty(
							clusterConfigNameTf.getText(),
							clusterHostTf.getText() + " "
									+ clusterPortTf.getText());
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
			refreshServerConfigs();
		}

		public void actionPerformed(ActionEvent e) {
			updaServerConfigs();
		}

	}

	private String getLocationProperty(String loc) {
		Properties prop = new Properties();
		File configFile = new File("server-location.properties");
		OutputStream output = null;
		if (configFile.exists()) {

			InputStream in;
			try {
				in = new FileInputStream("server-location.properties");
				prop.load(in);
				in.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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
			return prop.getProperty(loc);
		} else {
			return null;
		}
	}

	public String getScalsBaseDir() {
		return scalsBaseDir;
	}

	public void setScalsBaseDir(String scalsBaseDir) {
		this.scalsBaseDir = scalsBaseDir;
	}

	public String getJdkHomeDir() {
		return jdkHomeDir;
	}

	public void setJdkHomeDir(String jdkHomeDir) {
		this.jdkHomeDir = jdkHomeDir;
	}
}