package sdma.rtc.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import sdma.rtc.entity.*;
import sdma.rtc.ui.util.CouplingListCellRenderer;

public class SenerioWindow extends JFrame implements Runnable, MouseListener,
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6622390368704387811L;
	private int BUFFER_SIZE = 200000;

	private byte[] dataBuffer = null;
	private String senerioHost = null;
	private Integer senerioPort = null;
	private DatagramSocket senerio = null;
	private Packet packet = null;
	private DatagramPacket dgp = null;
	private ByteArrayInputStream bis = null;
	private ObjectInput oin = null;
	private Thread chartSyncProcess = null;
	//private Map<String, Coupling> UPDC = null;
	private Map<String, Coupling> BPDC = null;
	private Integer chartCount = 0;

	
	private JPanel chartMainPanel = null;
	private JLabel classNameLb = null;
	private JLabel inCouplingLb = null;
	private JLabel outCouplingLb = null;
	private JButton clearBt = null;
	private JButton stopBt = null;
	private List<ClassTimeChart> chartCollection = null;
	private DefaultListModel<Coupling> listModel = null;
	private JList<Coupling> classCouplingList = null;
	private Map<String, Integer> classLocList = null;
	private Long totalLatency = 0L;
	private Integer totalMessages = 0;
	private boolean processStatus = true;
	private Long senerioStartTime = null;
	private Long senerioEndTime = 0l;
	private Long responseTimeStart = -1l;
	private Long responseTimeEnd = -1l;

	private final static int screenWidth = 1200;
	private final static int screenHeight = 750;

	public SenerioWindow(Integer portNumber, Map<String, Coupling> UPDC) {
		this.chartMainPanel = new JPanel();
		this.senerioPort = portNumber;
		this.chartCollection = new ArrayList<ClassTimeChart>();
		this.BPDC = cloneUPDC(UPDC);
		this.classLocList = new HashMap<String, Integer>();
		this.setLayout(null);

		classNameLb = new JLabel("Class Name");
		inCouplingLb = new JLabel("In Coupling");
		outCouplingLb = new JLabel("Out Coupling");

		stopBt = new JButton("Stop");
		clearBt = new JButton("Clear");

		listModel = new DefaultListModel<Coupling>();
		classCouplingList = new JList<Coupling>(listModel);
		classCouplingList.addMouseListener(this);

		this.setLayout(null);

		JScrollPane js = new JScrollPane();
		classCouplingList.setCellRenderer(new CouplingListCellRenderer());

		classNameLb.setBounds(50, 10, 120, 25);
		inCouplingLb.setBounds(700, 10, 120, 25);
		outCouplingLb.setBounds(940, 10, 120, 25);

		clearBt.setBounds(50, 200, 150, 30);
		clearBt.addActionListener(this);

		stopBt.setBounds(250, 200, 300, 30);
		stopBt.addActionListener(this);

		this.add(classNameLb);
		this.add(inCouplingLb);
		this.add(outCouplingLb);
		this.add(clearBt);
		this.add(stopBt);

		js.setViewportView(classCouplingList);
		js.setPreferredSize(new Dimension(200, 300));
		js.setBounds(50, 30, 1100, 150);

		this.add(js);

		JScrollPane js1 = new JScrollPane();
		chartMainPanel.setPreferredSize(new Dimension(1100, 10000));
		js1.setViewportView(chartMainPanel);
		js1.setBounds(50, 250, 1100, 420);

		this.add(js1);

		this.setVisible(true);

		this.setBounds((int) (getScreenSize().getWidth() - screenWidth) / 2,
				(int) (getScreenSize().getHeight() - screenHeight) / 2,
				screenWidth, screenHeight);

		chartSyncProcess = new Thread(this);
		chartSyncProcess.start();
	}

	private Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	private Map<String, Coupling> cloneUPDC(Map<String, Coupling> originUPDC) {
		Map<String, Coupling> newUPDC = new HashMap<String, Coupling>();
		Iterator<String> keys = originUPDC.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			try {
				newUPDC.put(key, (Coupling) originUPDC.get(key).clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return newUPDC;
	}

	public static void main(String[] args) {
		new SenerioWindow(8080, null);
	}

	public void run() {
		try {
			senerio = new DatagramSocket(senerioPort,
					InetAddress.getLocalHost());
			while (processStatus) {
				dataBuffer = new byte[BUFFER_SIZE];
				// initialize datagram packet
				dgp = new DatagramPacket(dataBuffer, dataBuffer.length);
				// when new packet received on socket
				senerio.receive(dgp);
				System.out.println("Received at senerio" + senerioPort);
				// store data in buffer
				dataBuffer = dgp.getData();
				// covert to byte stream
				bis = new ByteArrayInputStream(dataBuffer);
				// convert to object stream
				oin = new ObjectInputStream(bis);
				
				long currentLocalStart=System.nanoTime();
				
				// when object is not null
				packet = (Packet) oin.readObject();

				Map<String, Coupling> currUPDC = (Map<String, Coupling>) packet
						.getData().get("UPDC");
				System.out.println("Received at Senario...."+currUPDC.size());

				if (senerioStartTime == null)
					senerioStartTime = System.currentTimeMillis();
				
				if (responseTimeStart == -1l)
					responseTimeStart = System.currentTimeMillis();
				else if (responseTimeEnd == -1l)
					responseTimeEnd = System.currentTimeMillis();

				int messages = Integer.parseInt(packet.getData().get("TMS")
						+ "");
				Iterator<String> keys = currUPDC.keySet().iterator();
				Coupling coupling = null;
				System.out.println("Synching................");
				try {
					while (keys.hasNext()) {
						String clsName = keys.next();
						System.out.println("class"+clsName);
						coupling = currUPDC.get(clsName);
						coupling.setClassName(clsName);
						if (clsName != null) {
							if (classLocList.get(clsName) == null
									&& coupling != null
									&& coupling.getClassName() != null
									&& !coupling.getClassName().equals("")) {
								Coupling oldCouple = BPDC.get(clsName);
								if (oldCouple != null) {
									oldCouple.setClassName(clsName);
									coupling.setClassName(clsName);
									listModel.addElement(coupling.balanceCoupling(oldCouple));
								}else{
									coupling.setClassName(clsName);
									listModel
											.addElement(coupling);
								}
								classLocList.put(clsName, listModel.size() - 1);
								chartCollection
										.add(new ClassTimeChart(clsName));
							} else if (coupling != null
									&& coupling.getClassName() != null
									&& !coupling.getClassName().equals("")) {
								Coupling oldCouple = BPDC.get(clsName);
								if (oldCouple != null) {
									oldCouple.setClassName(clsName);
									listModel
											.set(classLocList.get(clsName),
													coupling.balanceCoupling(oldCouple));
								}else{
									listModel
									.set(classLocList.get(clsName),coupling);
								}
							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				classCouplingList.setModel(listModel);
				Enumeration<Coupling> enums = listModel.elements();
				int i = 0;
				while (enums.hasMoreElements()) {
					Coupling couple = enums.nextElement();
					chartCollection.get(i).update(couple);
					++i;
				}
				/*long currentLocalEnd=System.nanoTime();
				totalLatency = totalLatency+(Long.parseLong(packet.getData().get("LST")
						+ "")+((currentLocalEnd-currentLocalStart)/1000));
						*/
				if(messages>0){
				totalLatency = totalLatency+(Long.parseLong(packet.getData().get("LST")
						+ ""));
				}
				System.out.println("Total Latency Senario           "+totalLatency+"\n\n\n\n");
				totalMessages = totalMessages+1;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void mouseClicked(MouseEvent e) {
		int index = classCouplingList.getSelectedIndex();
		ClassTimeChart clsTime = chartCollection.get(index);
		clsTime.setBounds(0, chartCount * 400, 1000, 400);
		chartMainPanel.add(clsTime);
		chartCount = +1;
	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clearBt) {
			chartMainPanel.removeAll();
			chartCount = 0;
		} else if (e.getSource() == stopBt) {
			processStatus = false;
			senerioEndTime = System.currentTimeMillis();
			prepareStatics();
		}
	}

	private void prepareStatics() {
		Set<String> packages = new HashSet<String>();
		Set<String> methods = new HashSet<String>();
		Map<String, Integer> clsEC = new HashMap<String, Integer>();
		Map<String, Integer> mtlEC = new HashMap<String, Integer>();
		Map<String, Integer> mslEC = new HashMap<String, Integer>();
		Map<String, Integer> clsIC = new HashMap<String, Integer>();
		Map<String, Integer> mtlIC = new HashMap<String, Integer>();
		Map<String, Integer> mslIC = new HashMap<String, Integer>();

		int packsUsed = 0;
		int classesUsed = 0;
		int methodsCalled = 0;
		long avgLatency = 0;
		int executionTime = 0;
		int totalMsgs = 0;
		int i = 0;
		while (i < chartCollection.size()) {
			Coupling couple = chartCollection.get(i).getFinalCoupling();
			String name = couple.getClassName();
			if (name.lastIndexOf("/") > -1) {
				String pack = name.substring(0, name.lastIndexOf("/"));
				packages.add(pack);
			}
			clsEC.put(name, couple.getClassScope(2));
			mtlEC.put(name, couple.getMethodScope(2));
			mslEC.put(name, couple.getMessageScope(2));
			clsIC.put(name, couple.getClassScope(1));
			mtlIC.put(name, couple.getMethodScope(1));
			mslIC.put(name, couple.getMessageScope(1));
			if (couple.getOutCoupling() != null) {
				for (ClassCtx cls : couple.getOutCoupling()) {
					for (MethodCtx mts : cls.getMethodCtxs()) {
						methods.add(couple.getClassName()+"$"+cls.getName() + "$" + mts.getSignature());
					}
				}
			}
			System.out.println("Class " + couple.getClassName() + " "
					+ couple.getMessageScope(2));
			totalMsgs = totalMsgs + couple.getMessageScope(2);
			++i;
		}

		//System.out.println("Messqges " + totalMsgs);

		packsUsed = packages.size();
		classesUsed = chartCollection.size();
		methodsCalled = methods.size();
		avgLatency = totalLatency / (totalMsgs==0?1:totalMsgs);
		executionTime = (int) ((senerioEndTime - senerioStartTime) / 1000);
		
		System.out.println("On Stpop data");
		System.out.println("Total Latency "+totalLatency);
		System.out.println("Total Messages "+totalMessages);
		System.out.println("avgLatency "+avgLatency);
		
		new StaticsWindow(packsUsed, classesUsed, methodsCalled, totalMsgs,
				avgLatency, responseTimeEnd-responseTimeStart, executionTime, clsEC, mtlEC, mslEC,
				clsIC, mtlIC, mslIC);

	}
}
