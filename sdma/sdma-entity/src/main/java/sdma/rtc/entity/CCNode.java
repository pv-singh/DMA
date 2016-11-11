package sdma.rtc.entity;

import java.io.Serializable;

public class CCNode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2844677559213278887L;
	private String parent=null;
	private String child=null;
	
	public CCNode() {
		super();
	}
	
	public CCNode(CCNode ccNode){
		this.parent = ccNode.getParent();
		this.child = ccNode.getChild();
	}
	
	public CCNode(String parent, String child) {
		super();
		this.parent = parent;
		this.child = child;
	}
	
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getChild() {
		return child;
	}
	public void setChild(String child) {
		this.child = child;
	}
	
	
	
}
