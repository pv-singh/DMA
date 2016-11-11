package sdma.rtc.entity;

import java.io.Serializable;

/** **
 * @author harkomal
 * This enumeration defines the valid packet source types
 */
public enum Source implements Serializable{
	
	SCAT, //Software Coupling Analysis Tool
	CLSR, //Cloud Server
	CLCR; //Cloud Cluster
	
	private static final long serialVersionUID = -6129356268469694615L;
	
}
