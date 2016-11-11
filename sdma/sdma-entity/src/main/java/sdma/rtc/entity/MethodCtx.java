package sdma.rtc.entity;

import java.io.Serializable;

import sdma.rtc.entity.MethodCtx;

public class MethodCtx implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -943555891452854946L;
	
	private String signature=null;
	private Integer callCount=null;
	
	public MethodCtx() {
		super();
	}
	
	public MethodCtx(String signature, Integer callCount) {
		super();
		this.signature = signature;
		this.callCount = callCount;
	}
	
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public Integer getCallCount() {
		return callCount;
	}
	public void setCallCount(Integer callCount) {
		this.callCount = callCount;
	}
	
	public MethodCtx increment(){
		this.callCount++;
		return this;
	}

}
