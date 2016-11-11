package sdma.rtc.ui.icon;

public class ToolIcon {
	
	public String getIcon(String iconName){
		return this.getClass().getResource(iconName).toString().replace("file:", "");
	}

}
