package sdma.rtc.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import sdma.rtc.entity.*;
import sdma.rtc.ui.icon.ToolIcon;
import sdma.rtc.ui.util.InstrumentProcess;
import sdma.rtc.ui.util.SynchronizeProcess;

public class AnalysisWindow extends JFrame implements ActionListener {
	// screen dimensions
	private final static int screenWidth = 1200;
	private final static int screenHeight = 650;

	// elements for analysis granualarity level
	private JLabel granLevel = null;
	private JRadioButton csGrRb = null;
	private JRadioButton mdGrRb = null;
	private JRadioButton msGrRb = null;

	private ButtonGroup bg = null;

	// synchronization interval elements
	private JLabel syncInterval = null;
	private JComboBox<String> syncIntervalCb = null;

	// start/stop analysis elements
	private JButton startAnalysis = null;
	private JButton stopAnalysis = null;

	// create analysis for new senerio
	private JButton newSenerioBut = null;

	// top and bottom section panels
	private JPanel topPanel = null;
	private static JPanel bottomPanel = null;

	// Thread for instrumentation data posting
	private InstrumentProcess instrumentProcess = null;
	private SynchronizeProcess synchronizeProcess = null;

	private SenerioWindow senerioWindow = null;

	public static Map<String, Coupling> OADS = null;

	public static int BUFFER_SIZE = 200000;

	private static ChartPanel chartPanel2 = null;
	private static DefaultCategoryDataset dataset2 = null;
	private static int totalClassCount = 0;
	private static ToolIcon toolIcon = null;
	static {
		toolIcon = new ToolIcon();
	}

	public AnalysisWindow() {

		Container con = this.getContentPane();
		con.setLayout(null);

		topPanel = new JPanel(null);
		bottomPanel = new JPanel(null);

		granLevel = new JLabel("Granuality Level");

		csGrRb = new JRadioButton("Class Level");
		mdGrRb = new JRadioButton("Method Level");
		msGrRb = new JRadioButton("Message Level");

		syncInterval = new JLabel("Synchronize Interval");
		String[] intervalLevels = new String[] { "1", "2", "5", "10", "20",
				"50" };
		syncIntervalCb = new JComboBox<String>(intervalLevels);

		csGrRb.setSelected(true);

		bg = new ButtonGroup();

		bg.add(csGrRb);
		bg.add(mdGrRb);
		bg.add(msGrRb);

		granLevel.setBounds(15, 5, 150, 25);

		csGrRb.setBounds(10, 45, 130, 25);
		mdGrRb.setBounds(10, 70, 130, 25);
		msGrRb.setBounds(10, 95, 130, 25);

		syncInterval.setBounds(15, 160, 180, 25);
		syncIntervalCb.setBounds(15, 195, 60, 25);
		JLabel syncCbLb = new JLabel("Seconds");
		syncCbLb.setBounds(90, 195, 70, 25);

		startAnalysis = new JButton(
				new ImageIcon(toolIcon.getIcon("start.png")));
		startAnalysis.setBounds(270, 5, 55, 55);
		startAnalysis.setMargin(new Insets(0, 0, 0, 0));
		startAnalysis.setFocusPainted(false);
		startAnalysis.setBorder(null);
		startAnalysis.setContentAreaFilled(false);
		startAnalysis.addActionListener(this);
		startAnalysis.setActionCommand("START_APP_ANALYSIS");

		stopAnalysis = new JButton(new ImageIcon(toolIcon.getIcon("stop.png")));
		stopAnalysis.setBounds(470, 5, 55, 55);
		stopAnalysis.setMargin(new Insets(0, 0, 0, 0));
		stopAnalysis.setFocusPainted(false);
		stopAnalysis.setBorder(null);
		stopAnalysis.setContentAreaFilled(false);
		stopAnalysis.setEnabled(false);

		newSenerioBut = new JButton(new ImageIcon(
				toolIcon.getIcon("start-analysis.png")));
		newSenerioBut.setMargin(new Insets(0, 0, 0, 0));
		newSenerioBut.setFocusPainted(false);
		newSenerioBut.setBorder(null);
		newSenerioBut.setContentAreaFilled(false);
		newSenerioBut.addActionListener(this);
		newSenerioBut.setEnabled(false);
		newSenerioBut.setActionCommand("CREATE_SENERIO");
		newSenerioBut.setBounds(650, 5, 55, 55);

		topPanel.setBounds(50, 10, 1100, 70);

		topPanel.add(startAnalysis);
		topPanel.add(stopAnalysis);
		topPanel.add(newSenerioBut);

		topPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED));
		bottomPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED));

		dataset2 = new DefaultCategoryDataset();
		chartPanel2 = new ChartPanel(createChart2(dataset2));
		chartPanel2.setBounds(10, 0, 2000, 200);
		bottomPanel.add(chartPanel2);

		JScrollPane js1 = new JScrollPane();
		bottomPanel.setPreferredSize(new Dimension(2000, 300));
		js1.setViewportView(bottomPanel);
		js1.setBounds(50, 100, 1100, 480);

		con.add(topPanel);
		con.add(js1);

		this.setResizable(false);

		this.setBounds((int) (getScreenSize().getWidth() - screenWidth) / 2,
				(int) (getScreenSize().getHeight() - screenHeight) / 2,
				screenWidth, screenHeight);
	}

	private JFreeChart createChart2(final CategoryDataset dataset) {
		JFreeChart barChart = ChartFactory.createBarChart("Overall Analysis",
				"Classes", "Coupling", dataset, PlotOrientation.HORIZONTAL,
				true, true, false);
		BarRenderer renderer = (BarRenderer) barChart.getCategoryPlot()
				.getRenderer();
		renderer.setItemMargin(0.1);

		CategoryPlot plot = barChart.getCategoryPlot();
		CategoryAxis axis = plot.getDomainAxis();
		Font font = new Font("Dialog", Font.PLAIN, 5);

		ValueAxis axis1 = plot.getRangeAxis();

		axis.setTickLabelFont(font);
		axis1.setTickLabelFont(font);
		return barChart;

	}

	private Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("START_APP_ANALYSIS")) {
			OADS = new HashMap<String, Coupling>();
			try {
				exportInstrumentScope();
				exportConfigProps();
				copyJarFile(new JarFile(new File(Main.getConfiguration()
						.getApplicationPath())), new File(Main
						.getConfiguration().getScalsBaseDir() + "JP2/"));
				generateRunScript();
				shareClusterLoad();
				instrumentProcess = new InstrumentProcess();
				instrumentProcess.start();
				synchronizeProcess = new SynchronizeProcess(Main
						.getConfiguration().getServerHost(), Main
						.getConfiguration().getServerPort());
				synchronizeProcess.start();
				stopAnalysis.setEnabled(true);
				newSenerioBut.setEnabled(true);
				startAnalysis.setEnabled(false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getActionCommand().equals("CREATE_SENERIO")) {
			int senerioPort = getFreePort();
			senerioWindow = new SenerioWindow(senerioPort, OADS);
			Main.addSenerio(senerioPort);
		}
	}

	private int getFreePort() {
		ServerSocket tempServer;
		int port = -1;
		try {
			tempServer = new ServerSocket(0);
			port = tempServer.getLocalPort();
			System.out.println(port);
			tempServer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return port;
	}

	public static void main(String[] args) {
		AnalysisWindow w = new AnalysisWindow();
		w.setVisible(true);
	}

	private String shareClusterLoad() {
		Socket server = null;
		ObjectInputStream oin = null;
		ObjectOutputStream out = null;
		InetAddress ip = null;
		Packet spack = null;
		Packet rpack = null;
		try {

			ip = InetAddress.getByName(Main.getConfiguration().getServerHost());
			server = new Socket(ip, Main.getConfiguration().getServerPort());

			oin = new ObjectInputStream(server.getInputStream());
			oin.readObject();

			out = new ObjectOutputStream(server.getOutputStream());

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("CLUSTER_LOAD", Main.getConfiguration()
					.generateClusterLoad());
			spack = new Packet();
			spack.setAction(Action.INIT_CL);
			spack.setSource(Source.SCAT);
			spack.setData(data);
			out.writeObject(spack);

			rpack = (Packet) oin.readObject();
			oin.close();
			out.close();
			server.close();
			return rpack.getData().get("STATUS").toString();
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return "ERROR";
	}

	public static void updateSenerio(Map<String, Coupling> UPDC,
			Long totalLatency, Integer totalMsgs) {
		long startTime = System.currentTimeMillis();
		DatagramPacket dgp = null;
		byte[] dataBuffer = new byte[BUFFER_SIZE];
		DatagramSocket senerioDS = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream out = null;
		InetAddress ip = null;
		Packet packet = null;
		Iterator<Integer> it = Main.getSenerioList().iterator();
		try {
			long endTime = System.currentTimeMillis();
			while (it.hasNext()) {
				int port = Integer.parseInt(it.next().toString());
				System.out.println("Sending to port..."+port);
				senerioDS = new DatagramSocket();
				bos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(bos);
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("UPDC", UPDC);
				data.put("LST", (endTime - startTime) + totalLatency);
				data.put("TMS", totalMsgs);
				ip = InetAddress.getLocalHost();
				packet = new Packet();
				packet.setData(data);
				out.writeObject(packet);
				dataBuffer = bos.toByteArray();
				dgp = new DatagramPacket(dataBuffer, dataBuffer.length, ip,
						port);
				senerioDS.send(dgp);
			}

			System.out.println("Size UPDC\n\n\n"+UPDC.size());

			Iterator<String> keys = UPDC.keySet().iterator();
			Coupling coupling = null;
			try {
				while (keys.hasNext()) {
					System.out.println("map ....next");
					String clsName = keys.next();
					System.out.println("calss ...."+clsName);
					coupling = OADS.get(clsName);
					System.out.println("coupling ...."+coupling+"\n\n\n");
					if (clsName != null) {
						if (coupling == null) {
							coupling = UPDC.get(clsName);
							coupling.setClassName(clsName);
							System.out.println("Added Value ...."+coupling.getMessageScope(1)+" Import_Coupling\n\n\n");
							dataset2.addValue(coupling.getMessageScope(1),
									"Import_Coupling", refineClass(clsName));
							dataset2.addValue(coupling.getMessageScope(2),
									"Export_Coupling", refineClass(clsName));
							totalClassCount = totalClassCount + 1;
							if (totalClassCount * 20 > chartPanel2.getSize()
									.getHeight()) {
								chartPanel2
										.setSize(new Dimension(2000,
												(int) chartPanel2.getSize()
														.getHeight() + 200));
								bottomPanel.setPreferredSize(new Dimension(
										2000,
										(int) bottomPanel.getPreferredSize()
												.getHeight() + 200));
							}
						} else if (coupling != null
								&& coupling.getClassName() != null
								&& !coupling.getClassName().equals("")) {
							Coupling oldCouple = OADS.get(clsName);
							if (oldCouple != null) {
								oldCouple.setClassName(clsName);
								dataset2.setValue(coupling.getMessageScope(1),
										"Import_Coupling", refineClass(clsName));
								dataset2.setValue(coupling.getMessageScope(2),
										"Export_Coupling", refineClass(clsName));
							}
						}
					}

				}
				OADS.putAll(UPDC);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String refineClass(String rawClassName) {
		String actualClassName = null;
		actualClassName = rawClassName.substring(1, rawClassName.length() - 1);
		return actualClassName;
	}

	private void generateRunScript() {
		FileOutputStream outputStream;
		File commandFile = new File(Main.getConfiguration().getScalsBaseDir()
				+ "JP2/runAgent.sh");
		commandFile.setExecutable(true);
		File input = new File(this.getClass().getResource("icon/cluster.png")
				.toString().replace("file:", "").replace("/bin", "")
				.replace("/target/classes", "")
				.replace("sdma/rtc/ui/icon/cluster.png", "")
				+ "agent.sh");
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(input)));
			outputStream = new FileOutputStream(commandFile);
			while ((line = br.readLine()) != null) {

				String supportiveJars = "";
				List<String> supportJars = Main.getConfiguration()
						.getSupportLibPaths();
				if (supportJars != null && supportJars.size() > 0)
					for (String jar : supportJars) {
						supportiveJars += ":lib/"
								+ jar.substring(jar.lastIndexOf("/") + 1);
					}

				outputStream.write(line
						.replace("$SUPPORT_JARS", supportiveJars)
						.replace(
								"$APP_JAR",
								Main.getConfiguration()
										.getApplicationPath()
										.substring(
												Main.getConfiguration()
														.getApplicationPath()
														.lastIndexOf("/") + 1))
						.getBytes());
				outputStream.write("\n".getBytes());

			}
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void copyJarFile(JarFile jarFile, File destDir) throws IOException {
		String fileName = jarFile.getName();
		String fileNameLastPart = fileName.substring(fileName
				.lastIndexOf(File.separator));
		File destFile = new File(destDir, fileNameLastPart);

		JarOutputStream jos = new JarOutputStream(
				new FileOutputStream(destFile));
		Enumeration<JarEntry> entries = jarFile.entries();
		Map<String, String> mp = new HashMap<String, String>();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (mp.get(entry.getName()) == null) {
				InputStream is = jarFile.getInputStream(entry);

				// jos.putNextEntry(entry);
				// create a new entry to avoid ZipException: invalid entry
				// compressed size

				jos.putNextEntry(new JarEntry(entry.getName()));
				byte[] buffer = new byte[4096];
				int bytesRead = 0;
				while ((bytesRead = is.read(buffer)) != -1) {
					jos.write(buffer, 0, bytesRead);

				}
				is.close();
				jos.flush();
				jos.closeEntry();
				mp.put(entry.getName(), "Y");
			}

		}
		jos.close();
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

	private void exportConfigProps() throws IOException {
		Properties prop = new Properties();
		OutputStream output = null;

		try {

			output = new FileOutputStream(Main.getConfiguration()
					.getScalsBaseDir() + "configurations.properties");
			prop.setProperty("servername", Main.getConfiguration()
					.getServerHost());
			prop.setProperty("serverport", Main.getConfiguration()
					.getServerPort() + "");
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private void exportInstrumentScope() throws IOException {
		Map<String, List<String>> componentPackages = new HashMap<String, List<String>>();
		OutputStream out = new FileOutputStream(new File(Main
				.getConfiguration().getScalsBaseDir()
				+ "/JP2/conf/instrument_scope.txt"));
		List<String> packs = null;
		for (String pack : Main.getConfiguration().getApplicationPackgs()) {
			// String packName = pack.substring(3);
			out.write((pack + "\n").getBytes());
			/*
			 * if ((packs = componentPackages.get(pack.charAt(1) + "")) == null)
			 * packs = new ArrayList<String>(); packs.add(packName);
			 * componentPackages.put(pack.charAt(1) + "", packs);
			 */
		}
		out.close();
	}

}