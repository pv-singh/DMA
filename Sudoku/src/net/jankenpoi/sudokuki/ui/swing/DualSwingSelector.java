package net.jankenpoi.sudokuki.ui.swing;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.jankenpoi.sudokuki.ui.MemosSelector;
import net.jankenpoi.sudokuki.ui.Selector;

public class DualSwingSelector implements Selector, MemosSelector {

	private int digit = -1;
	
	private byte[] memos = null;
	
	public DualSwingSelector(boolean valuePickerOnTop, JFrame parent,
			JPanel panel, int invokedX, int invokedY, byte previousValue,
			Byte[] previousMemos) {
		
		DualSelectionDialog dlg = new DualSelectionDialog(valuePickerOnTop, parent, previousValue, previousMemos);
		int width = dlg.getWidth();
		int height = dlg.getHeight();
		
		int leftLimit = (int)panel.getLocationOnScreen().getX();
		int rightLimit = leftLimit + panel.getWidth() - width;
		int upperLimit = (int)panel.getLocationOnScreen().getY();
		int lowerLimit = upperLimit + panel.getHeight() - height;
		
		int x = leftLimit + invokedX - width/2;
		x = Math.max(leftLimit, x);
		x = Math.min(rightLimit, x);
		
		int y = upperLimit + invokedY - height/2;
		y = Math.max(upperLimit, y);
		y = Math.min(lowerLimit, y);
		
		dlg.setLocation(x, y);
		
		dlg.setVisible(true);
		
		digit = dlg.getClickedDigit();
		
		memos = dlg.getSelectedMemos();
	}

	public int retrieveNumber() {
		return digit;
	}

	public byte[] retrieveMemos() {
		return memos;
	}

}
