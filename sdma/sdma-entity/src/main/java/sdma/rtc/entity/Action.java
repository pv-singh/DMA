package sdma.rtc.entity;

import java.io.Serializable;

public enum Action implements Serializable{
	LIST_AC, //list all the active clusters ready for analysis
	READY,   //send to cluster if ready for communication
	INIT_CL,
	PROFILE_NODE,
	SYNC_UPD,
	CLR_AD;
	private static final long serialVersionUID = -6129356268469694616L;
}
