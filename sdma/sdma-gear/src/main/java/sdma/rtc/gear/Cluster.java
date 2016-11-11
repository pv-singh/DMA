package sdma.rtc.gear;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sdma.rtc.entity.Action;
import sdma.rtc.entity.CCNode;
import sdma.rtc.entity.ClassCtx;
import sdma.rtc.entity.Coupling;
import sdma.rtc.entity.MethodCtx;
import sdma.rtc.entity.Packet;
import sdma.rtc.entity.Source;

/**
 * @author harkomal
 * 
 *         This is the client cluster to process incoming node packet data and
 *         send back to server on sync request. Each time sync data is request
 *         by server cluster UPD - un-processed data on cluster will be clear to
 *         zero.
 * 
 */
public class Cluster {

	// Datagram socket address and port
	private Integer clusterPort = null;
	private InetAddress clusterAddress = null;

	// map to store un-processed data - later it will be stored on memcache
	private Map<String, Coupling> UPDS = null;

	// map to store overall analysis data - later it will be store on memcache
	private Map<String, Coupling> OADS = new HashMap<String, Coupling>();

	// Thread to read node packets received from server
	private PacketReader packetReader = null;
	// packets for read and write data on socket
	private Packet rpack = null;
	private Packet spack = null;

	// supportive elements for parent and child nodes
	private String parent = null;
	private String parentClass = null;
	private String parentMethod = null;

	private String child = null;
	private String childClass = null;
	private String childMethod = null;

	// datagram socket for cluster to read data on
	private ServerSocket cluster = null;

	// supprtive elemenets for socket communication
	private ObjectInputStream oin = null;
	private ObjectOutput out = null;
	
	private Long lastSyncTime=null;

	// start a new cluster with a port
	public Cluster(String host,Integer port) throws UnknownHostException {
		//cluster configs
		clusterAddress=InetAddress.getByName(host);
		// clusterAddress = InetAddress.getByName("127.0.0.1");
		clusterPort = port;
		
		// initialize maps to store un-processed and overall data
		UPDS = new HashMap<String, Coupling>();
		OADS = new HashMap<String, Coupling>();
		
		// initialize and start thread to read data on socket
		packetReader = new PacketReader();
		packetReader.start();
	}

	class PacketReader extends Thread {

		public PacketReader() {
			try {
				
				// one time initialize datagram socket
				cluster = new ServerSocket(clusterPort, 1,
						clusterAddress);
				// logging
				System.out.println("Cluster started on port : "
						+ clusterPort + " and host :"
						+ clusterAddress.getHostName());
			} catch (Exception e) {
				e.getMessage();
			}
		}

		@Override
		public void run() {
			Map<String, Object> response = new HashMap<String, Object>();
			Long pStart=null;
			while (true) {
				try {

					Socket clSocket = cluster.accept();
					out = new ObjectOutputStream(clSocket.getOutputStream());
					
					Packet packet = new Packet();
					packet.setAction(Action.READY);

					out.writeObject(packet);
					out.flush();
					oin = new ObjectInputStream(clSocket.getInputStream());
					
					rpack = (Packet) oin.readObject();
					
					// cluster READY for analysis

					// if node data is received - process and store calling node
					// information
					if (rpack.getAction() == Action.PROFILE_NODE) {
						spack=new Packet();
						spack.setAction(Action.SYNC_UPD);
						spack.setSource(Source.CLCR);
						response.clear();
						response.put("SUPD", UPDS);
						response.put("LST", lastSyncTime==null?0:lastSyncTime);
						spack.setData(response);
						out.writeObject(spack);
						out.flush();
						UPDS.clear();
						out.close();
						oin.close();
						clSocket.close();
						
						//nano time
						pStart=System.nanoTime();
						processPacket((List<CCNode>) rpack.getData().get("NODE"));
						lastSyncTime=(System.nanoTime()-pStart)/1000;
					}else if (rpack.getAction() == Action.READY) {
						out.close();
						oin.close();
						clSocket.close();
					}else if (rpack.getAction() == Action.CLR_AD) {
						// clear data for each new analysis
						UPDS.clear();
						OADS.clear();
						out.close();
						oin.close();
						clSocket.close();
					}
					
				} catch (ClassNotFoundException e) {
					e.getMessage();
				} catch (IOException e) {
					e.getMessage();
				}
			}
		}

		// processing incoming node data and store
		private void processPacket(List<CCNode> nodes) {

			for(CCNode node:nodes){
			boolean classFound = false;
			boolean methodFound = false;
			int classLoc = -1;
			int methodLoc = -1;

			// support elementd for out-coupling and in-coupling
			Coupling outCoupling = null;
			Coupling inCoupling = null;

			parentClass = null;
			parentMethod = null;
			childClass = null;
			childMethod = null;

			// get parent and child nodes from packet node
			parent = node.getParent();
			child = node.getChild();

			if (parent != null) {
				// extract parent class and method
				parentClass = parent.split("\\.")[0];
				parentMethod = parent.split("\\.")[1];
			}
			if (child != null) {
				// extract parent class and child
				childClass = child.split("\\.")[0];
				childMethod = child.split("\\.")[1];
			}

			// if parent class is not null
			if (parentClass != null) {
				// if out coupling exist in over all data
				if (OADS.get(parentClass) != null) {
					// get out coupled classes
					if ((outCoupling = OADS.get(parentClass)) != null) {

						// reset all local variables
						classLoc = -1;
						classFound = false;
						methodFound = false;

						// get out coupling classes
						if (outCoupling.getOutCoupling() != null) {

							// for each class update coupling based on input
							// node
							for (ClassCtx en : outCoupling.getOutCoupling()) {
								++classLoc;

								// if child class exist in out coupled classes
								if (en.getName() != null
										&& en.getName().equals(childClass)) {
									// mark flag and set location
									classFound = true;
									methodLoc = -1;
									// for each method exist class
									for (MethodCtx men : en.getMethodCtxs()) {
										methodLoc++;
										// if method signature matches with
										// child class method
										if (men.getSignature() != null
												&& men.getSignature().equals(
														childMethod)) {
											methodFound = true;
											break;
										}
									}
									break;
								}
							}
						} else {
							outCoupling = new Coupling();
						}
						if(parentClass.endsWith("command/Command")){
							System.out.println("Inside Block 5");
						}
						// if same method with same signature and class found
						// already
						if (classFound && methodFound) {

							// increment the method call count
							outCoupling
									.getOutCoupling()
									.get(classLoc)
									.getMethodCtxs()
									.set(methodLoc,
											outCoupling.getOutCoupling()
													.get(classLoc)
													.getMethodCtxs()
													.get(methodLoc).increment());

							// if class found but method with same signature not
							// exist
						} else if (classFound) {
							// add child node method for class that is found in
							// out coupling
							List<MethodCtx> lis = new ArrayList<MethodCtx>();
							lis.add(new MethodCtx(childMethod, 1));
							// set methods of corresponding out coupling class
							outCoupling.getOutCoupling().get(classLoc)
									.setMethodCtxs(lis);

							// if neither class nor method found in out coupling
						} else {
							// add child node method in out coupling
							List<MethodCtx> lis = new ArrayList<MethodCtx>();
							lis.add(new MethodCtx(childMethod, 1));
							// add a new child node class in out coupling
							ClassCtx classCtx = new ClassCtx(childClass, lis);

							// initialize out coupling class when found null
							if (outCoupling.getOutCoupling() == null)
								outCoupling
										.setOutCoupling(new ArrayList<ClassCtx>());

							// add class to out coupling list
							outCoupling.getOutCoupling().add(classCtx);
						}
						if(parentClass.endsWith("command/Command")){
							System.out.println("Inside Block 4");
						}
						// set out coupling for parent class
						OADS.put(parentClass, outCoupling);
						UPDS.put(parentClass, outCoupling);
					}else{
						outCoupling = new Coupling();
						// add child node method in out coupling
						List<MethodCtx> lis = new ArrayList<MethodCtx>();
						lis.add(new MethodCtx(childMethod, 1));
						// add a new child node class in out coupling
						ClassCtx classCtx = new ClassCtx(childClass, lis);

						// initialize out coupling class when found null
						if (outCoupling.getOutCoupling() == null)
							outCoupling
									.setOutCoupling(new ArrayList<ClassCtx>());

						// add class to out coupling list
						outCoupling.getOutCoupling().add(classCtx);
						if(parentClass.endsWith("command/Command")){
							System.out.println("Inside Block 3");
						}
						// set out coupling for parent class
						OADS.put(parentClass, outCoupling);
						UPDS.put(parentClass, outCoupling);
					}
				} else {
					// initialize out coupling and add out coupling method and
					// class for it
					outCoupling = new Coupling(null, new ArrayList<ClassCtx>(),
							parentClass);

					List<MethodCtx> lis = new ArrayList<MethodCtx>();
					lis.add(new MethodCtx(childMethod, 1));

					ClassCtx classCtx = new ClassCtx(childClass, lis);

					// initialize out coupling class list
					outCoupling.setOutCoupling(new ArrayList<ClassCtx>());

					// add node class coupling
					outCoupling.getOutCoupling().add(classCtx);
					if(parentClass.endsWith("command/Command")){
						System.out.println("Inside Block 2");
					}
					// set out coupling for parent class
					OADS.put(parentClass, outCoupling);
					UPDS.put(parentClass, outCoupling);
				}

				// same process as followed for out coupling here is for in
				// coupling

				// if in coupling exist in over all data
				if (OADS.get(childClass) != null) {
					if ((inCoupling = OADS.get(childClass)) != null) {
						// reset
						classLoc = -1;
						classFound = false;
						methodFound = false;
						if (inCoupling.getInCoupling() != null) {
							for (ClassCtx en : inCoupling.getInCoupling()) {
								++classLoc;
								if (en.getName() != null
										&& en.getName().equals(parentClass)) {
									classFound = true;
									methodLoc = -1;
									for (MethodCtx men : en.getMethodCtxs()) {
										methodLoc++;
										if (men.getSignature() != null
												&& men.getSignature().equals(
														parentMethod)) {
											methodFound = true;
											break;
										}
									}
									break;
								}
							}
						} else {
							inCoupling = new Coupling();
						}
						if (classFound && methodFound) {
							inCoupling
									.getInCoupling()
									.get(classLoc)
									.getMethodCtxs()
									.set(methodLoc,
											inCoupling.getInCoupling()
													.get(classLoc)
													.getMethodCtxs()
													.get(methodLoc).increment());
						} else if (classFound) {

							List<MethodCtx> lis = new ArrayList<MethodCtx>();
							lis.add(new MethodCtx(parentMethod, 1));

							inCoupling.getInCoupling().get(classLoc)
									.setMethodCtxs(lis);

						} else {

							List<MethodCtx> lis = new ArrayList<MethodCtx>();
							lis.add(new MethodCtx(parentMethod, 1));

							ClassCtx classCtx = new ClassCtx(parentClass, lis);

							inCoupling.setInCoupling(new ArrayList<ClassCtx>());
							inCoupling.getInCoupling().add(classCtx);
						}

						OADS.put(childClass, inCoupling);
						UPDS.put(childClass, inCoupling);
					}else{
						inCoupling = new Coupling();
						List<MethodCtx> lis = new ArrayList<MethodCtx>();
						lis.add(new MethodCtx(parentMethod, 1));

						ClassCtx classCtx = new ClassCtx(parentClass, lis);

						inCoupling.setInCoupling(new ArrayList<ClassCtx>());
						inCoupling.getInCoupling().add(classCtx);
						OADS.put(childClass, inCoupling);
						UPDS.put(childClass, inCoupling);
					}
				} else {

					inCoupling = new Coupling(new ArrayList<ClassCtx>(), null,
							childClass);

					List<MethodCtx> lis = new ArrayList<MethodCtx>();
					lis.add(new MethodCtx(parentMethod, 1));

					ClassCtx classCtx = new ClassCtx(parentClass, lis);

					inCoupling.setInCoupling(new ArrayList<ClassCtx>());
					inCoupling.getInCoupling().add(classCtx);

					OADS.put(childClass, inCoupling);
					UPDS.put(childClass, inCoupling);

				}

			}
			}
		}
	}

	public static void main(String[] args) throws NumberFormatException, UnknownHostException {
		new Cluster(args[0],Integer.parseInt(args[1]));
	}

}
