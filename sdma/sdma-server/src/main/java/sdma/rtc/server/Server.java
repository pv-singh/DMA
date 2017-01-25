package sdma.rtc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sdma.rtc.entity.*;

public class Server {

	// Datagram socket port for server cluster
	private Integer serverPort = null;
	private InetAddress serverAddress = null;

	// cluster load - packages for each active cluster
	private Map<String, Integer> clusterLoad = null;

	// datagram socket for datagram transfer
	private ServerSocket socket = null;

	// Thread to read datagram packet data on socket
	private DataPacketReader dataPacketReader = null;

	private NodeSender nodeSender = null;

	// list of indexes of active clusters
	private List<Integer> activeClusters = null;

	// available cluster hosts
	private String[] clusterHosts = null;
	// available cluster ports
	private Integer[] clusterProfilePorts = null;

	private List<List<CCNode>> clusterNodes = null;

	// map to store un-processed data - later it will be stored on memcache
	private Map<String, Coupling> UPDS = null;

	// if list containing nodes is already used by another
	// thread then current thread will be blocked for some time
	private boolean blockAccess = false;

	// total latency from the time last data sync
	private Long totalLatency = 0l;
	// total message from the time last data sync
	private Integer totalMsgs = 0;

	// start a server on specified port with configures cluster configurations
	public Server(String host, Integer port, List<String> clusters) {

		// if configuration file is supplied
	//	if (clusters != null && clusters.size() > 0) {

			try {

				// initialize server configuration
				serverPort = port;
				serverAddress = InetAddress.getByName(host);

				clusterNodes = new ArrayList<List<CCNode>>();
				clusterNodes.add(new ArrayList<CCNode>());
				UPDS = new HashMap<String, Coupling>();

				// read file to extract cluster configuration
				clusterHosts = new String[10];
				clusterProfilePorts = new Integer[10];

				int i = 0;
				System.out
						.println("Following clusters will be used for analysis support:");
				for (String line : clusters) {
					System.out.println(line);
					String[] parts = line.split(" ");
					clusterHosts[i] = parts[0];
					clusterProfilePorts[i] = Integer.parseInt(parts[1]);
					++i;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

	//	} else {
	//		System.out
	//				.println("Please provide cluster-config.txt to list supportive clusters");
	//		System.exit(0);
	//	}
		// initialize setup server
		dataPacketReader = new DataPacketReader();
		dataPacketReader.start();

		 nodeSender = new NodeSender();
		 nodeSender.start();
	}

	// thread to read packets form analysis application
	class DataPacketReader extends Thread {

		// elements for socket communication
		private ObjectInputStream oin = null;
		private ObjectOutputStream out = null;
		private String ppn = null;
		private String cpn = null;

		//
		private int outBoundCluster = -1;
		private int inBoundCluster = -1;

		public DataPacketReader() {
			try {
				// one time initialize datagram socket
				socket = new ServerSocket(serverPort, 1, serverAddress);
				socket.setReuseAddress(true);
				System.out.println("Server started on port : " + serverPort
						+ " and host :" + serverAddress.getHostName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// method to list indexes of active clusters from specified
		// clusters in configuration file
		private List<Integer> listActiveClusters() {

			// collection to store active cluster indexes
			List<Integer> clusters = new ArrayList<Integer>();

			// elements to support communication
			Packet packet = null;
			Socket clusterDS = null;
			ObjectInputStream oin = null;
			ObjectOutputStream oos = null;
			String isRead = null;
			clusterNodes.clear();
			try {
				
				// iterate for each cluster config
				for (int i = 0; i < clusterHosts.length; ++i) {
					isRead = "NO";
					System.out.println("command "+clusterHosts[i]);
					if (clusterHosts[i] != null) {
						System.out.println("command "+clusterHosts[i]);
						
						// create a new socket and set timeout to 2 seconds
						clusterDS = new Socket(clusterHosts[i],
								clusterProfilePorts[i]);
						clusterDS.setSoTimeout(10000);
						System.out.println(clusterHosts[i] + " "
								+ clusterProfilePorts[i]);
						// create a new request packet to check cluster ready
						// state
						packet = new Packet();
						packet.setAction(Action.READY);
						packet.setSource(Source.CLSR);
						try {
							oin = new ObjectInputStream(
									clusterDS.getInputStream());
							oin.readObject();
							oos = new ObjectOutputStream(
									clusterDS.getOutputStream());
							oos.writeObject(packet);
							isRead = "YES";
						} catch (Exception e) {
							e.printStackTrace();
							isRead = "NO";
						}
						// receive the packet from stream and close socket
						if (isRead.equals("YES")) {
							clusterNodes.add(new ArrayList<CCNode>());
							// add cluster index to the list of active clusters
							clusters.add(i + 1);
						}
						if (oin != null)
							oin.close();
						clusterDS.close();
					}

				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return clusters;
		}

		private void clearClusterAnalysis() {

			// elements to support communication
			Packet packet = null;
			Socket clusterDS = null;
			ObjectInputStream oin = null;
			ObjectOutput oos = null;

			try {
				// iterate for each cluster config
				for (int i = 0; i < clusterHosts.length; ++i) {
					if (clusterHosts[i] != null) {

						// create a new socket and set timeout to 2 seconds
						clusterDS = new Socket(clusterHosts[i],
								clusterProfilePorts[i]);
						clusterDS.setSoTimeout(10000);

						// create a new request packet to check cluster ready
						// state
						packet = new Packet();
						packet.setAction(Action.CLR_AD);
						packet.setSource(Source.CLSR);

						oin = new ObjectInputStream(clusterDS.getInputStream());
						oin.readObject();

						oos = new ObjectOutputStream(
								clusterDS.getOutputStream());
						oos.writeObject(packet);
						oos.flush();

						oos.close();
						clusterDS.close();
					}
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			CCNode ccNode = null;
			Map<String, Object> response = new HashMap<String, Object>();
			try {
				while (true) {

					Socket clSocket = socket.accept();
					out = new ObjectOutputStream(clSocket.getOutputStream());

					Packet packet = new Packet();
					packet.setAction(Action.READY);

					out.writeObject(packet);
					out.flush();
					oin = new ObjectInputStream(clSocket.getInputStream());

					packet = (Packet) oin.readObject();
					// if request is to list active clusters
					if (packet.getAction() == Action.LIST_AC) {
						// list activve clusters by pinging each cluster
						
						activeClusters = listActiveClusters();
						response.clear();

						response.put("ACTIVE_CLUSTERS", activeClusters);

						Packet spack = new Packet();
						spack.setAction(Action.LIST_AC);
						spack.setData(response);

						out.writeObject(spack);
						out.flush();
						out.close();
						oin.close();
						clSocket.close();

						// if request is to initialize cluster load
					} else if (packet.getAction() == Action.INIT_CL) {
						if (packet.getData().get("CLUSTER_LOAD") != null) {

							// initialize the cluster load for new analysis
							clusterLoad = (Map<String, Integer>) packet
									.getData().get("CLUSTER_LOAD");

							clearClusterAnalysis();

							response.clear();
							response.put("STATUS", "OK");

							Packet spack = new Packet();
							spack.setAction(Action.INIT_CL);
							spack.setSource(Source.CLSR);
							spack.setData(response);

							out.writeObject(spack);
							out.flush();

							out.close();
							oin.close();
							clSocket.close();

						}

						// process node profile request
					} else if (packet.getAction() == Action.PROFILE_NODE) {
						if (packet.getData().get("NODE") != null) {
							ccNode = (CCNode) packet.getData().get("NODE");
							addNode(0, ccNode);
						}
						out.close();
						oin.close();
						clSocket.close();

						/*
						 * if (packet.getData().get("NODE") != null) { ccNode =
						 * (CCNode) packet.getData().get("NODE"); if
						 * (ccNode.getParent() != null &&
						 * !ccNode.getParent().equals("")) { ppn =
						 * extractPackage(ccNode.getParent()); } else { ppn =
						 * null; }
						 * 
						 * if (ccNode.getChild() != null &&
						 * !ccNode.getChild().equals("")) { cpn =
						 * extractPackage(ccNode.getChild()); } else { cpn =
						 * null; } if (ppn != null) { if (clusterLoad.get(ppn)
						 * != null) { outBoundCluster = clusterLoad.get(ppn) -
						 * 1; // new NodeSender(outBoundCluster, //
						 * ccNode).start();; addNode(outBoundCluster, ccNode); }
						 * }
						 * 
						 * if (clusterLoad.get(cpn) != null) { inBoundCluster =
						 * clusterLoad.get(cpn) - 1; // new
						 * NodeSender(inBoundCluster, // ccNode).start();;
						 * addNode(inBoundCluster, ccNode); }
						 * 
						 * response.clear(); response.put("STATUS", "OK");
						 * 
						 * }
						 */

					} else if (packet.getAction() == Action.SYNC_UPD) {
						System.out.println("Sysnch Date\n\n\n\n");
						
						response.clear();
						response.put("SUPD", UPDS);
						response.put("LST", totalLatency);
						response.put("TMS", totalMsgs);

						Packet spack = new Packet();

						spack.setAction(Action.SYNC_UPD);
						spack.setSource(Source.CLSR);
						spack.setData(response);

						out.writeObject(spack);
						out.flush();
						System.out.println("Sync Data Sent " + UPDS.size());
						// UPDS.clear();

						// reset latency and message after data sync
						totalLatency = 0l;
						totalMsgs = 0;

						out.close();
						oin.close();
						clSocket.close();
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		private void addNode(int cluster, CCNode ccNode) {
			System.out.println(clusterNodes.get(0).size() + "Size");
			/*
			 * while (blockAccess) { try { Thread.sleep(10); } catch
			 * (InterruptedException e) { e.printStackTrace(); } }
			 */
			// blockAccess=true;
			clusterNodes.get(cluster).add(ccNode);
			// blockAccess=false;
		}

		private String extractPackage(String node) {
			String part = node.split("\\.")[0];
			if (part.contains("/")) {
				return part.substring(0, part.lastIndexOf("/") + 1).replace(
						"L", "");
			} else {
				return "";
			}
		}
	}

	public static void main(String[] args) {
		new Server(args[0], Integer.parseInt(args[1]), null);
	}

	class NodeSender extends Thread {

		public NodeSender() {

		}

		private Map<String, Coupling> clubAllClusterUPD(
				List<Map<String, Coupling>> UPCS) {
			Map<String, Coupling> overAllUPC = new HashMap<String, Coupling>();
			String key = null;
			for (Map<String, Coupling> upc : UPCS) {
				if (upc != null) {
					Iterator<String> keys = upc.keySet().iterator();
					while (keys.hasNext()) {
						key = keys.next();
						overAllUPC.put(key, upc.get(key));
					}
				}
			}
			return overAllUPC;
		}

		public void run() {

			Map<String, Object> response = new HashMap<String, Object>();
			Socket clusterDS = null;
			Packet packet = null;
			ObjectOutputStream out = null;
			ObjectInputStream oin = null;
			long maxTime = 0;
			while (true) {
				maxTime = 0;
				try {
					List<List<CCNode>> upns = currentClusterNodes();
					if (activeClusters != null) {
						List<Map<String, Coupling>> clusterUPDS = new ArrayList<Map<String, Coupling>>();
						for (int i = 0; i < activeClusters.size(); ++i) {
							// add messages
							totalMsgs = +upns.get(i).size();

							clusterDS = new Socket(clusterHosts[i],
									clusterProfilePorts[i]);
							oin = new ObjectInputStream(
									clusterDS.getInputStream());
							oin.readObject();
							clusterDS.setSoTimeout(5000);
							packet = new Packet();
							packet.setAction(Action.PROFILE_NODE);
							response.put("NODE", upns.get(i));
							packet.setData(response);
							out = new ObjectOutputStream(
									clusterDS.getOutputStream());
							out.writeObject(packet);
							out.flush();

							Packet rpack = (Packet) oin.readObject();
							@SuppressWarnings("unchecked")
							Map<String, Coupling> currUPDS = (Map<String, Coupling>) rpack
									.getData().get("SUPD");
							long gearTime = Long.parseLong(rpack.getData().get(
									"LST")
									+ "");
							if (gearTime > maxTime) {
								maxTime = gearTime;
							}
							clusterUPDS.add(currUPDS);
							oin.close();
							out.close();
							clusterDS.close();

						}
						UPDS.putAll(clubAllClusterUPD(clusterUPDS));
						Thread.sleep(500);
					}
					totalLatency = +maxTime;
					//System.out.println("Latency  " + totalLatency);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized List<List<CCNode>> currentClusterNodes() {
		List<List<CCNode>> oldNode = new ArrayList<List<CCNode>>();
		List<CCNode> list = null;
		for (int i = 0; i < clusterNodes.size(); ++i) {
			List<CCNode> old = clusterNodes.get(i);
			list = new ArrayList<CCNode>();
			for (int j = 0; j < old.size(); ++j) {
				list.add(new CCNode(old.get(j)));

			}
			oldNode.add(list);
			clusterNodes.get(i).clear();
		}
		return oldNode;
	}

	public void stop() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
