/*
* GoGrinder - a program to practice Go problems
* Copyright (c) 2004-2006 Tim Kington
* timkington@users.sourceforge.net
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
*/

package GoGrinder;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author  tkington
 */
public class GS {
    private GS() { /* This is a singleton */ }
    
    private static final int REVISION = 9;
    private static Rectangle probFrameBounds = new Rectangle(0, 0, 580, 603);
    private static Rectangle wgfFrameBounds = new Rectangle(0, 0, 580, 603);
    private static Selection selSets = new Selection();
    private static TagList tags = new TagList();
    private static boolean autoAdvance = true;
    private static boolean flip = true;
    private static boolean flipColors = true;
    private static boolean showGhost = true;
    private static boolean showWrongPath = false;
    private static boolean soundEnabled = true;
    private static boolean clickSoundEnabled = true;
    private static boolean checkForUpdates = true;
    private static boolean useProxy = false;
    private static String proxyHost = ""; //$NON-NLS-1$
    private static int proxyPort = 0;
    private static ProbCollection colls;
    private static boolean probState = true;
    private static int splitterPos = 381;
    private static LinkedList history;
    private static File saveDir;
    private static File sgfEditor;
    private static boolean rightClickAdvance = true;
    
    public static void loadSettings() throws Exception {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("grind.dat")); //$NON-NLS-1$
        
        int rev = in.readInt();
        if(rev > REVISION) {
            String msg = Messages.getString("err_data_file_newer"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(null, msg);
            Main.logSilent(new Exception(msg));
            System.exit(-1);
        }
        
        probFrameBounds = (Rectangle)in.readObject();
        selSets = (Selection)in.readObject();
        tags = (TagList)in.readObject();
        autoAdvance = in.readBoolean();
        flip = in.readBoolean();
        
        if(rev > 1) {
            flipColors = in.readBoolean();
            showGhost = in.readBoolean();
        }
        
        if(rev > 2)
            showWrongPath = in.readBoolean();
        
        if(rev > 3) {
            soundEnabled = in.readBoolean();
            clickSoundEnabled = in.readBoolean();
        }
        
        if(rev > 4) {
            checkForUpdates = in.readBoolean();
            useProxy = in.readBoolean();
            proxyHost = (String)in.readObject();
            proxyPort = in.readInt();
            
            if(useProxy) {
                Properties sysProps = System.getProperties(); 
                sysProps.put( "proxySet", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
                sysProps.put( "proxyHost", proxyHost); //$NON-NLS-1$
                sysProps.put( "proxyPort", String.valueOf(proxyPort)); //$NON-NLS-1$
            }
        }
        
        if(rev > 5) {
            probState = in.readBoolean();
            wgfFrameBounds = (Rectangle)in.readObject();
            splitterPos = in.readInt();
            history = (LinkedList)in.readObject();
        }
        
        if(rev > 6) {
        	saveDir = (File)in.readObject();
        }
        
        if(rev > 7) {
        	sgfEditor = (File)in.readObject();
        }
        
        if(rev > 8) {
        	rightClickAdvance = in.readBoolean();
        }
        
        in.close();
    }
    
    public static void saveSettings() throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("grind.dat")); //$NON-NLS-1$
        
        out.writeInt(REVISION);
        out.writeObject(probFrameBounds);
        out.writeObject(selSets);
        out.writeObject(tags);
        out.writeBoolean(autoAdvance);
        out.writeBoolean(flip);
        out.writeBoolean(flipColors);
        out.writeBoolean(showGhost);
        out.writeBoolean(showWrongPath);
        out.writeBoolean(soundEnabled);
        out.writeBoolean(clickSoundEnabled);
        out.writeBoolean(checkForUpdates);
        out.writeBoolean(useProxy);
        out.writeObject(proxyHost);
        out.writeInt(proxyPort);
        out.writeBoolean(probState);
        out.writeObject(wgfFrameBounds);
        out.writeInt(splitterPos);
        out.writeObject(history);
        out.writeObject(saveDir);
        out.writeObject(sgfEditor);
        out.writeBoolean(rightClickAdvance);
        
        out.close();
    }
    
    public static Rectangle getProbFrameBounds() { return probFrameBounds; }
    public static void setProbFrameBounds(Rectangle b) { probFrameBounds = b; }
    public static Selection getSelectedSets() { return selSets; }
    public static void setSelection(Selection s) { selSets = s; }
    public static boolean getAutoAdv() { return autoAdvance; }
    public static void setAutoAdv(boolean a) { autoAdvance = a; }
    public static boolean getFlip() { return flip; }
    public static void setFlip(boolean f) { flip = f; }
    public static boolean getFlipColors() { return flipColors; }
    public static void setFlipColors(boolean f) { flipColors = f; }
    public static boolean getShowGhost() { return showGhost; }
    public static void setShowGhost(boolean s) { showGhost = s; }
    public static boolean getShowWrongPath() { return showWrongPath; }
    public static void setShowWrongPath(boolean s) { showWrongPath = s; }
    public static boolean getSoundEnabled() { return soundEnabled; }
    public static void setSoundEnabled(boolean e) { soundEnabled = e; }
    public static boolean getClickSoundEnabled() { return clickSoundEnabled; }
    public static void setClickSoundEnabled(boolean e) { clickSoundEnabled = e; }
    public static TagList getTagList() { return tags; }
    public static ProbCollection getCollections() { return colls; }
    public static void setCollections(ProbCollection c) { colls = c; }
    public static boolean getCheckForUpdates() { return checkForUpdates; }
    public static void setCheckForUpdates(boolean c) { checkForUpdates = c; }
    public static boolean getUseProxy() { return useProxy; }
    public static void setUseProxy(boolean u) { useProxy = u; }
    public static String getProxyHost() { return proxyHost; }
    public static void setProxyHost(String h) { proxyHost = h; }
    public static int getProxyPort() { return proxyPort; }
    public static void setProxyPort(int p) { proxyPort = p; }
    public static boolean getProbState() { return probState; }
    public static void setProbState(boolean b) { probState = b; }
    public static void setWGFFrameBounds(Rectangle r) { wgfFrameBounds = r; }
    public static Rectangle getWGFFrameBounds() { return wgfFrameBounds; }
    public static void setSplitterPos(int p) { splitterPos = p; }
    public static int getSplitterPos() { return splitterPos; }
    public static void setHistory(LinkedList h) { history = h; }
    public static LinkedList getHistory() { return history; }
    public static void setSaveDir(File d) { saveDir = d; }
    public static File getSaveDir() { return saveDir; }
    public static void setSGFEditor(File e) { sgfEditor = e; }
    public static File getSGFEditor() { return sgfEditor; }
    public static boolean getRightClickAdvance() { return rightClickAdvance; }
    public static void setRightClickAdvance(boolean b) { rightClickAdvance = b; }
}
