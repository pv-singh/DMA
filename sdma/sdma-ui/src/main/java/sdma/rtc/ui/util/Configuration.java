package sdma.rtc.ui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
	private final static int MAX_CLUSTER_SUPPORT = 3;
	// base directory of tool to save configurations and other supportive libs
	private String scalsBaseDir = null;
	// time interval in milliseconds to request cloud server for updated
	// analysis data
	private Integer syncInterval = null;
	// JDK home path to directory one level higher than 'bin'
	private String jdkHome = null;
	// path of the java/jar application to be analysed
	private String applicationPath = null;
	// path of the jar libraries that will support main application (optional)
	private List<String> supportLibPaths = null;
	// list of all the packages/sub-packages that are part of application
	private List<String> applicationPackgs = null;
	// packages that are grouped/configured as a single component
	private Map<Integer, List<String>> componentPackgs = null;
	// components to be handled on each of the available cluster
	private Map<Integer, List<Integer>> clusterComponents = null;
	
	//server host and port
	private String serverHost=null;
	private Integer serverPort=null;

	private List<String> clusters = null;
	
	// generate the cluster wise load of application packages
	// all the classes included in particular package will be considered
	// as part of package and will be handled on same cluster to which
	// package is assigned
	public Map<String, Integer> generateClusterLoad() {
		Map<String, Integer> clusterLoad = null;
		List<Integer> components = null;
		List<String> componentPkgs = null;
		if (componentPackgs != null && componentPackgs.size() > 0
				&& clusterComponents != null && clusterComponents.size() > 0) {
			clusterLoad = new HashMap<String, Integer>();
			// iterate for each cluster components
			for (int i = 1; i <= MAX_CLUSTER_SUPPORT; ++i) {
				//if at least one component is assigned to cluster
				if ((components = clusterComponents.get(i)) != null) {
					//iterate for components of selected cluster
					for (Integer component : components) {
						//if at least one package is assigned to component
						if ((componentPkgs = componentPackgs.get(component)) != null) {
							// iterate for eack package of selected component
							for (String pack : componentPkgs) {
								System.out.println("Pack "+pack +" cluster"+i);
								//if it is a valid package
								if (pack != null && !pack.equals(""))
									clusterLoad.put(pack, i);
							}
						}
					}
				}
			}
		}

		return clusterLoad;
	}

	public Configuration() {
		super();
	}

	public Configuration(String scalsBaseDir, Integer syncInterval,
			String jdkHome, String applicationPath,
			List<String> supportLibPaths, List<String> applicationPackgs,
			Map<Integer, List<String>> componentPackgs,
			Map<Integer, List<Integer>> clusterComponents,String serverHost,Integer serverPort,List<String> clusters) {
		super();
		this.scalsBaseDir = scalsBaseDir;
		this.syncInterval = syncInterval;
		this.jdkHome = jdkHome;
		this.applicationPath = applicationPath;
		this.supportLibPaths = supportLibPaths;
		this.applicationPackgs = applicationPackgs;
		this.componentPackgs = componentPackgs;
		this.clusterComponents = clusterComponents;
		this.serverHost=serverHost;
		this.serverPort=serverPort;
		this.clusters=clusters;
	}

	public String getScalsBaseDir() {
		return scalsBaseDir;
	}

	public void setScalsBaseDir(String scalsBaseDir) {
		this.scalsBaseDir = scalsBaseDir;
	}

	public Integer getSyncInterval() {
		return syncInterval;
	}

	public void setSyncInterval(Integer syncInterval) {
		this.syncInterval = syncInterval;
	}

	public String getJdkHome() {
		return jdkHome;
	}

	public void setJdkHome(String jdkHome) {
		this.jdkHome = jdkHome;
	}

	public String getApplicationPath() {
		return applicationPath;
	}

	public void setApplicationPath(String applicationPath) {
		this.applicationPath = applicationPath;
	}

	public List<String> getSupportLibPaths() {
		return supportLibPaths;
	}

	public void setSupportLibPaths(List<String> supportLibPaths) {
		this.supportLibPaths = supportLibPaths;
	}

	public List<String> getApplicationPackgs() {
		return applicationPackgs;
	}

	public void setApplicationPackgs(List<String> applicationPackgs) {
		this.applicationPackgs = applicationPackgs;
	}

	public Map<Integer, List<String>> getComponentPackgs() {
		return componentPackgs;
	}

	public void setComponentPackgs(Map<Integer, List<String>> componentPackgs) {
		this.componentPackgs = componentPackgs;
	}

	public Map<Integer, List<Integer>> getClusterComponents() {
		return clusterComponents;
	}

	public void setClusterComponents(
			Map<Integer, List<Integer>> clusterComponents) {
		this.clusterComponents = clusterComponents;
	}
	
	

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}
	
	public List<String> getClusters() {
		return clusters;
	}

	public void setClusters(List<String> clusters) {
		this.clusters = clusters;
	}

	@Override
	public String toString() {
		return "Configuration [getScalsBaseDir()=" + getScalsBaseDir()
				+ ", getSyncInterval()=" + getSyncInterval()
				+ ", getJdkHome()=" + getJdkHome() + ", getApplicationPath()="
				+ getApplicationPath() + ", getSupportLibPaths()="
				+ getSupportLibPaths() + ", getApplicationPackgs()="
				+ getApplicationPackgs() + ", getComponentPackgs()="
				+ getComponentPackgs() + ", getClusterComponents()="
				+ getClusterComponents() + "]";
	}

}
