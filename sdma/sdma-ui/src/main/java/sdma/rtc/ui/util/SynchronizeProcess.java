package sdma.rtc.ui.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

import sdma.rtc.entity.*;
import sdma.rtc.ui.AnalysisWindow;

public class SynchronizeProcess extends Thread {
	private InetAddress ip = null;
	private Integer port = null;
	private Socket server = null;
	private Packet rpack = null;
	private Packet spack = null;
	private ObjectInputStream oin = null;
	private ObjectOutputStream out = null;
	private static int SYNC_INTERVAL = 2000;
	private long lastSyncTime = 0;
	private long currSyncTime = 0;
	
	private Long totalLatency=null;
	private Integer totalMsgs=null;

	public SynchronizeProcess(String host, Integer port) {
		try {
			this.ip = InetAddress.getByName(host);
			this.port = port;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {

			while (true) {
				currSyncTime = new Date().getTime();
				Thread.sleep(SYNC_INTERVAL);
				server = new Socket(ip, port);
				oin = new ObjectInputStream(server.getInputStream());
				oin.readObject();

				out = new ObjectOutputStream(server.getOutputStream());
				spack = new Packet();
				spack.setAction(Action.SYNC_UPD);
				out.writeObject(spack);

				rpack = (Packet) oin.readObject();
				
				oin.close();
				out.close();
				server.close();
				Map<String, Coupling> updc = (Map<String, Coupling>) rpack
						.getData().get("SUPD");

				totalLatency=Long.parseLong(rpack.getData().get("LST")+"");
				totalMsgs=Integer.parseInt(rpack.getData().get("TMS")+"");

				long localTime=new Date().getTime();
				totalLatency=lastSyncTime==0?totalLatency:(localTime-lastSyncTime)+totalLatency;
				System.out.println("Updating Senerio.................................\n\n\n\n\n");
				AnalysisWindow.updateSenerio(updc,totalLatency,totalMsgs);
				
				lastSyncTime=currSyncTime;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}