package sdma.rtc.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sdma.rtc.ui.icon.ToolIcon;

public class ClusterComponent extends JPanel{
	
	private JLabel clusterIcon=null;
	
	private JLabel compo1Lb=null;
	private JCheckBox compo1=null;
	
	private JLabel compo2Lb=null;
	private JCheckBox compo2=null;
	
	private JLabel compo3Lb=null;
	private JCheckBox compo3=null;
	
	private JLabel compo4Lb=null;
	private JCheckBox compo4=null;
	
	private JLabel compo5Lb=null;
	private JCheckBox compo5=null;
	
	private JLabel compo6Lb=null;
	private JCheckBox compo6=null;
	
	private JLabel compo7Lb=null;
	private JCheckBox compo7=null;
	
	private JLabel compo8Lb=null;
	private JCheckBox compo8=null;
	
	private static ToolIcon toolIcon=null;
	static{
		toolIcon=new ToolIcon();
	}
	public ClusterComponent(){
		this.setLayout(null);
		
		clusterIcon=new JLabel(new ImageIcon(toolIcon.getIcon("cluster.png")));
		clusterIcon.setEnabled(false);
		clusterIcon.setBounds(-20,0,152,152);
		
		compo1Lb=new JLabel("C1");
		compo2Lb=new JLabel("C2");
		compo3Lb=new JLabel("C3");
		compo4Lb=new JLabel("C4");
		compo5Lb=new JLabel("C5");
		compo6Lb=new JLabel("C6");
		compo7Lb=new JLabel("C7");
		compo8Lb=new JLabel("C8");
		
		compo1=new JCheckBox();
		compo2=new JCheckBox();
		compo3=new JCheckBox();
		compo4=new JCheckBox();
		compo5=new JCheckBox();
		compo6=new JCheckBox();
		compo7=new JCheckBox();
		compo8=new JCheckBox();
		
		compo1.setEnabled(false);
		compo1.setBounds(140,30,20,20);
		
		compo2.setEnabled(false);
		compo2.setBounds(140,53,20,20);
		
		compo3.setEnabled(false);
		compo3.setBounds(140,76,20,20);
		
		compo4.setEnabled(false);
		compo4.setBounds(140,99,20,20);
		
		compo5.setEnabled(false);
		compo5.setBounds(200,30,20,20);
		
		compo6.setEnabled(false);
		compo6.setBounds(200,53,20,20);
		
		compo7.setEnabled(false);
		compo7.setBounds(200,76,20,20);
		
		compo8.setEnabled(false);
		compo8.setBounds(200,99,20,20);
		
		compo1Lb.setBounds(120,30,20,20);
		compo2Lb.setBounds(120,53,20,20);
		compo3Lb.setBounds(120,76,20,20);
		compo4Lb.setBounds(120,99,20,20);
		compo5Lb.setBounds(180,30,20,20);
		compo6Lb.setBounds(180,53,20,20);
		compo7Lb.setBounds(180,76,20,20);
		compo8Lb.setBounds(180,99,20,20);
		
		this.add(clusterIcon);
		
		this.add(compo1Lb);
		this.add(compo2Lb);
		this.add(compo3Lb);
		this.add(compo4Lb);
		this.add(compo5Lb);
		this.add(compo6Lb);
		this.add(compo7Lb);
		this.add(compo8Lb);
		
		this.add(compo1);
		this.add(compo2);
		this.add(compo3);
		this.add(compo4);
		this.add(compo5);
		this.add(compo6);
		this.add(compo7);
		this.add(compo8);
	}
	
	public void activate(){
		clusterIcon.setEnabled(true);
		compo1.setEnabled(true);
		compo2.setEnabled(true);
		compo3.setEnabled(true);
		compo4.setEnabled(true);
		compo5.setEnabled(true);
		compo6.setEnabled(true);
		compo7.setEnabled(true);
		compo8.setEnabled(true);
		
	}
	
	public void deactivate(){
		clusterIcon.setEnabled(false);
		compo1.setEnabled(false);
		compo2.setEnabled(false);
		compo3.setEnabled(false);
		compo4.setEnabled(false);
		compo5.setEnabled(false);
		compo6.setEnabled(false);
		compo7.setEnabled(false);
		compo8.setEnabled(false);
		
		compo1.setSelected(false);
		compo2.setSelected(false);
		compo3.setSelected(false);
		compo4.setSelected(false);
		compo5.setSelected(false);
		compo6.setSelected(false);
		compo7.setSelected(false);
		compo8.setSelected(false);
	}
	
	public List<Integer> listAssignedComponents(){
		List<Integer> comps=new ArrayList<Integer>();
		if(compo1.isEnabled() && compo1.isSelected()){
			comps.add(1);
		}
		if(compo2.isEnabled() && compo2.isSelected()){
			comps.add(2);
		}
		if(compo3.isEnabled() && compo3.isSelected()){
			comps.add(3);
		}
		if(compo4.isEnabled() && compo4.isSelected()){
			comps.add(4);
		}
		if(compo5.isEnabled() && compo5.isSelected()){
			comps.add(5);
		}
		if(compo6.isEnabled() && compo6.isSelected()){
			comps.add(6);
		}
		if(compo7.isEnabled() && compo7.isSelected()){
			comps.add(7);
		}
		if(compo8.isEnabled() && compo8.isSelected()){
			comps.add(8);
		}
		
		return comps;
	}
	
	public boolean isActive(){
		return clusterIcon.isEnabled();
	}
	
}
