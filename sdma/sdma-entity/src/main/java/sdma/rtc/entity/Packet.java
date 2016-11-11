package sdma.rtc.entity;

import java.io.Serializable;
import java.util.Map;

/* This class represents packet that is used as base of 
 * communication between scs-ls-cloud tool and cloud server socket
 */
public class Packet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7605800324630852942L;
	
	/** Source Type of packet can be:
	 *  SCAT - Software Coupling Analysis Tool
	 *  CLSR   - Cloud Server
	 *  CLCR   - Cloud Cluster
	 *  **/
	
	// Source of the packet can be SCAT or CLSR or CLCR
	private Source source;
	/** Action of the message can be 
	list/save/update/delete of data 
	on destination socket */
	private Action action;
	/** Data can be object to post of on destination
	 * or response object from destination
	 * multiple data objects can be send received in form
	 * of Map */
	private Map<String,Object> data=null;
	
	public Packet() {
		super();
	}
	
	public Packet(Source source, Action action, Map<String, Object> data) {
		super();
		this.source = source;
		this.action = action;
		this.data = data;
	}
	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "Packet [getSource()=" + getSource() + ", getAction()="
				+ getAction() + ", getData()=" + getData() + "]";
	}	

}
