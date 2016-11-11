package sdma.rtc.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Coupling implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1374640288510985495L;

	private List<ClassCtx> inCoupling = null;
	private List<ClassCtx> outCoupling = null;
	private String className = null;

	public Coupling(List<ClassCtx> inCoupling, List<ClassCtx> outCoupling,
			String className) {
		super();
		this.inCoupling = inCoupling;
		this.outCoupling = outCoupling;
		this.className = className;
	}

	public Coupling balanceCoupling(Coupling oldCoupling) {

		Coupling balanceCoupling = null;
		List<MethodCtx> balMethods = null;
		List<ClassCtx> balClasses = null;
		if (oldCoupling != null) {
			/*System.out
					.println("---------------------------------------------------");
			System.out.println("Class : " + oldCoupling.getClassName());
			System.out.println("----Out Coupling---");
			System.out.println("Class Level : " + oldCoupling.getClassScope(2));
			System.out.println("Method Level : "
					+ oldCoupling.getMethodScope(2));
			System.out.println("Message Level : "
					+ oldCoupling.getMessageScope(2));
			System.out.println("----In Coupling---");
			System.out.println("Class Level : " + oldCoupling.getClassScope(1));
			System.out.println("Method Level : "
					+ oldCoupling.getMethodScope(1));
			System.out.println("Message Level : "
					+ oldCoupling.getMessageScope(1));
			System.out
					.println("---------------------------------------------------");

			System.out
					.println("---------------------------------------------------");
			System.out.println("Class : " + this.getClassName());
			System.out.println("----Out Coupling---");
			System.out.println("Class Level : " + this.getClassScope(2));
			System.out.println("Method Level : " + this.getMethodScope(2));
			System.out.println("Message Level : " + this.getMessageScope(2));
			System.out.println("----In Coupling---");
			System.out.println("Class Level : " + this.getClassScope(1));
			System.out.println("Method Level : " + this.getMethodScope(1));
			System.out.println("Message Level : " + this.getMessageScope(1));
			System.out
					.println("---------------------------------------------------");*/
			balanceCoupling = new Coupling();
			balanceCoupling.setClassName(oldCoupling.getClassName());
			// for in coupling
			if (oldCoupling.getInCoupling() != null) {
				List<ClassCtx> clsOldCoupling = oldCoupling.getInCoupling();
				int cs = clsOldCoupling.size();
				int c1 = 0;

				balClasses = new ArrayList<ClassCtx>();
				if (inCoupling != null) {
				for (ClassCtx ncls : inCoupling) {
					balMethods = new ArrayList<MethodCtx>();
					if (c1 < cs) {
						int mc = clsOldCoupling.get(c1).getMethodCtxs().size();
						int c2 = 0;
						for (MethodCtx nmt : ncls.getMethodCtxs()) {
							MethodCtx methodCtx = null;
							if (c2 < mc) {
								int cd = 0;
								if ((cd = nmt.getCallCount()
										- clsOldCoupling.get(c1)
												.getMethodCtxs().get(c2)
												.getCallCount()) > 0) {
									methodCtx = new MethodCtx(
											nmt.getSignature(), cd);
								}
							} else {
								methodCtx = new MethodCtx(nmt.getSignature(),
										nmt.getCallCount());
							}
							if (methodCtx != null)
								balMethods.add(methodCtx);
							++c2;
						}

					} else {
						balMethods = ncls.getMethodCtxs();
					}

					if (balMethods.size() > 0) {
						balClasses
								.add(new ClassCtx(ncls.getName(), balMethods));
					}
					++c1;
				}
				balanceCoupling.setInCoupling(balClasses);
				}else{
					balanceCoupling.setInCoupling(new ArrayList<ClassCtx>());
				}
			} else {
				balanceCoupling.setInCoupling(inCoupling);
			}

			// for out coupling
			if (oldCoupling.getOutCoupling() != null) {
				List<ClassCtx> clsOldCoupling = oldCoupling.getOutCoupling();
				int cs = clsOldCoupling.size();
				int c1 = 0;

				balClasses = new ArrayList<ClassCtx>();
				if (outCoupling != null) {
					for (ClassCtx ncls : outCoupling) {
						balMethods = new ArrayList<MethodCtx>();
						if (c1 < cs) {
							int mc = clsOldCoupling.get(c1).getMethodCtxs()
									.size();
							int c2 = 0;
							for (MethodCtx nmt : ncls.getMethodCtxs()) {
								MethodCtx methodCtx = null;
								if (c2 < mc) {
									int cd = 0;
									if ((cd = nmt.getCallCount()
											- clsOldCoupling.get(c1)
													.getMethodCtxs().get(c2)
													.getCallCount()) > 0) {
										methodCtx = new MethodCtx(
												nmt.getSignature(), cd);
									}
								} else {
									methodCtx = new MethodCtx(
											nmt.getSignature(),
											nmt.getCallCount());
								}
								if (methodCtx != null)
									balMethods.add(methodCtx);
								++c2;
							}

						} else {
							balMethods = ncls.getMethodCtxs();
						}
						if (balMethods.size() > 0) {
							balClasses.add(new ClassCtx(ncls.getName(),
									balMethods));
						}
						++c1;
					}
					balanceCoupling.setOutCoupling(balClasses);
				} else {
					balanceCoupling.setOutCoupling(new ArrayList<ClassCtx>());
				}

			} else {
				balanceCoupling.setOutCoupling(outCoupling);
			}
			
			if(balanceCoupling.getMessageScope(1)==0 && balanceCoupling.getMessageScope(2)==0){
				balanceCoupling=this;
			}

		} else {
			balanceCoupling = this;
		}

		/*if (balanceCoupling != null) {
			System.out
					.println("------------------Difference------------------------");
			System.out.println("Balance\nclass "
					+ balanceCoupling.getClassName());
			System.out.println("cs " + balanceCoupling.getClassScope(1) + " "
					+ balanceCoupling.getClassScope(2));
			System.out.println("mts " + balanceCoupling.getMethodScope(1) + " "
					+ balanceCoupling.getMethodScope(2));
			System.out.println("mss " + balanceCoupling.getMessageScope(1)
					+ " " + balanceCoupling.getMessageScope(2));
		}*/
		return balanceCoupling;
	}
	
	
	public Coupling barBalanceCoupling(Coupling oldCoupling) {

		Coupling balanceCoupling = null;
		List<MethodCtx> balMethods = null;
		List<ClassCtx> balClasses = null;
		if (oldCoupling != null) {
			System.out
					.println("----------------Bar Balance Coupling-----------------------------------");
			System.out.println("Class : " + oldCoupling.getClassName());
			System.out.println("----Out Coupling---");
			System.out.println("Class Level : " + oldCoupling.getClassScope(2));
			System.out.println("Method Level : "
					+ oldCoupling.getMethodScope(2));
			System.out.println("Message Level : "
					+ oldCoupling.getMessageScope(2));
			System.out.println("----In Coupling---");
			System.out.println("Class Level : " + oldCoupling.getClassScope(1));
			System.out.println("Method Level : "
					+ oldCoupling.getMethodScope(1));
			System.out.println("Message Level : "
					+ oldCoupling.getMessageScope(1));
			System.out
					.println("---------------------------------------------------");

			System.out
					.println("---------------------------------------------------");
			System.out.println("Class : " + this.getClassName());
			System.out.println("----Out Coupling---");
			System.out.println("Class Level : " + this.getClassScope(2));
			System.out.println("Method Level : " + this.getMethodScope(2));
			System.out.println("Message Level : " + this.getMessageScope(2));
			System.out.println("----In Coupling---");
			System.out.println("Class Level : " + this.getClassScope(1));
			System.out.println("Method Level : " + this.getMethodScope(1));
			System.out.println("Message Level : " + this.getMessageScope(1));
			System.out
					.println("---------------------------------------------------");
			balanceCoupling = new Coupling();
			balanceCoupling.setClassName(oldCoupling.getClassName());
			// for in coupling
			if (oldCoupling.getInCoupling() != null) {
				List<ClassCtx> clsOldCoupling = oldCoupling.getInCoupling();
				int cs = clsOldCoupling.size();
				int c1 = 0;

				balClasses = new ArrayList<ClassCtx>();
				if (inCoupling != null) {
				for (ClassCtx ncls : inCoupling) {
					balMethods = new ArrayList<MethodCtx>();
					if (c1 < cs) {
						int mc = clsOldCoupling.get(c1).getMethodCtxs().size();
						int c2 = 0;
						for (MethodCtx nmt : ncls.getMethodCtxs()) {
							MethodCtx methodCtx = null;
							if (c2 < mc) {
								int cd = 0;
								if ((cd = nmt.getCallCount()
										- clsOldCoupling.get(c1)
												.getMethodCtxs().get(c2)
												.getCallCount()) > 0) {
									methodCtx = new MethodCtx(
											nmt.getSignature(), cd);
								}
							} else {
								methodCtx = new MethodCtx(nmt.getSignature(),
										nmt.getCallCount());
							}
							if (methodCtx != null)
								balMethods.add(methodCtx);
							++c2;
						}

					} else {
						balMethods = ncls.getMethodCtxs();
					}

					if (balMethods.size() > 0) {
						balClasses
								.add(new ClassCtx(ncls.getName(), balMethods));
					}
					++c1;
				}
				balanceCoupling.setInCoupling(balClasses);
				}else{
					balanceCoupling.setInCoupling(new ArrayList<ClassCtx>());
				}
			} else {
				balanceCoupling.setInCoupling(inCoupling);
			}

			// for out coupling
			if (oldCoupling.getOutCoupling() != null) {
				List<ClassCtx> clsOldCoupling = oldCoupling.getOutCoupling();
				int cs = clsOldCoupling.size();
				int c1 = 0;

				balClasses = new ArrayList<ClassCtx>();
				if (outCoupling != null) {
					for (ClassCtx ncls : outCoupling) {
						balMethods = new ArrayList<MethodCtx>();
						if (c1 < cs) {
							int mc = clsOldCoupling.get(c1).getMethodCtxs()
									.size();
							int c2 = 0;
							for (MethodCtx nmt : ncls.getMethodCtxs()) {
								MethodCtx methodCtx = null;
								if (c2 < mc) {
									int cd = 0;
									if ((cd = nmt.getCallCount()
											- clsOldCoupling.get(c1)
													.getMethodCtxs().get(c2)
													.getCallCount()) > 0) {
										methodCtx = new MethodCtx(
												nmt.getSignature(), cd);
									}
								} else {
									methodCtx = new MethodCtx(
											nmt.getSignature(),
											nmt.getCallCount());
								}
								if (methodCtx != null)
									balMethods.add(methodCtx);
								++c2;
							}

						} else {
							balMethods = ncls.getMethodCtxs();
						}
						if (balMethods.size() > 0) {
							balClasses.add(new ClassCtx(ncls.getName(),
									balMethods));
						}
						++c1;
					}
					balanceCoupling.setOutCoupling(balClasses);
				} else {
					balanceCoupling.setOutCoupling(new ArrayList<ClassCtx>());
				}

			} else {
				balanceCoupling.setOutCoupling(outCoupling);
			}
		} else {
			balanceCoupling = this;
		}

		if (balanceCoupling != null) {
			System.out
					.println("------------------Difference------------------------");
			System.out.println("Balance\nclass "
					+ balanceCoupling.getClassName());
			System.out.println("cs " + balanceCoupling.getClassScope(1) + " "
					+ balanceCoupling.getClassScope(2));
			System.out.println("mts " + balanceCoupling.getMethodScope(1) + " "
					+ balanceCoupling.getMethodScope(2));
			System.out.println("mss " + balanceCoupling.getMessageScope(1)
					+ " " + balanceCoupling.getMessageScope(2));
		}
		return balanceCoupling;
	}

	public Coupling() {
		super();
		inCoupling = new ArrayList<ClassCtx>();
		outCoupling = new ArrayList<ClassCtx>();
	}

	public List<ClassCtx> getInCoupling() {
		return inCoupling;
	}

	public void setInCoupling(List<ClassCtx> inCoupling) {
		this.inCoupling = inCoupling;
	}

	public List<ClassCtx> getOutCoupling() {
		return outCoupling;
	}

	public void setOutCoupling(List<ClassCtx> outCoupling) {
		this.outCoupling = outCoupling;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Integer getClassScope(int inOutCoupling) {

		switch (inOutCoupling) {
		case 1:
			return inCoupling != null ? inCoupling.size() : 0;
		case 2:
			return outCoupling != null ? outCoupling.size() : 0;
		default:
			return 0;
		}

	}

	public Integer getMethodScope(int inOutCoupling) {

		switch (inOutCoupling) {
		case 1:
			if (inCoupling != null) {
				int mCalls = 0;
				for (ClassCtx cls : inCoupling) {
					mCalls += cls.getMethodCtxs() != null ? cls.getMethodCtxs()
							.size() : 0;
				}
				return mCalls;
			}
			return 0;
		case 2:
			if (outCoupling != null) {
				int mCalls = 0;
				for (ClassCtx cls : outCoupling) {
					mCalls += cls.getMethodCtxs() != null ? cls.getMethodCtxs()
							.size() : 0;
				}
				return mCalls;
			}
			return 0;
		default:
			return 0;
		}

	}

	public Integer getMessageScope(int inOutCoupling) {

		switch (inOutCoupling) {
		case 1:
			if (inCoupling != null) {
				int msCalls = 0;
				for (ClassCtx cls : inCoupling) {
					if (cls.getMethodCtxs() != null) {
						for (MethodCtx mls : cls.getMethodCtxs()) {
							msCalls += mls.getCallCount();
						}
					}

				}
				return msCalls;
			}
			return 0;
		case 2:
			if (outCoupling != null) {
				int msCalls = 0;
				for (ClassCtx cls : outCoupling) {
					if (cls.getMethodCtxs() != null) {
						for (MethodCtx mls : cls.getMethodCtxs()) {
							msCalls += mls.getCallCount();
						}
					}

				}
				return msCalls;
			}
			return 0;
		default:
			return 0;
		}

	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
