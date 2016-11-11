package sdma.rtc.entity;

import java.io.Serializable;
import java.util.List;

public class ClassCtx implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8999599152599191372L;
	
	private String name=null;
	private List<MethodCtx> methodCtxs=null;
	
	
	public ClassCtx() {
		super();
	}

	public ClassCtx(String name, List<MethodCtx> methodCtxs) {
		super();
		this.name = name;
		this.methodCtxs = methodCtxs;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<MethodCtx> getMethodCtxs() {
		return methodCtxs;
	}
	public void setMethodCtxs(List<MethodCtx> methodCtxs) {
		this.methodCtxs = methodCtxs;
	}
	
	

}
