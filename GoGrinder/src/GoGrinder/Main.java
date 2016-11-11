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
import java.net.*;
import java.util.*;
import java.applet.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import GoGrinder.ui.*;
import GoGrinder.ui.SplashScreen;

import com.Ostermiller.util.Browser;

/**
 *
 * @author  tkington
 */
public class Main {
    public static final int EXPORTREVISION = 1;
    public static String GRINDER_VERSION;
    public static final int NUMERIC_VERSION = 114;
    public static final String VERSION_STRING = "Version 1.14"; //$NON-NLS-1$
    public static final String NUM_VER_STRING = "1.14"; //$NON-NLS-1$
    public static final String STATS_DIR = "stats"; //$NON-NLS-1$
    public static final String PROBLEM_DIR = "problems"; //$NON-NLS-1$
	public static final String LOCALE_FILE = "grind.locale"; //$NON-NLS-1$

    public static final Color BGCOLOR = new Color(228, 234, 219);
    public static final Color SELCOLOR = new Color(197, 201, 189);
    public static final Random rand = new Random();
    private static final ExceptionHandler handler = new ExceptionHandler();
    
    public static ProbFrame probFrame;
    public static WGFFrame wgfFrame;
    
    public static JFileChooser chooser;
    public static GGFileFilter gxpFilter;
    public static GGFileFilter ggsFilter;
    public static GGFileFilter wgfFilter;
    
    public static AudioClip clickSound;
    public static AudioClip rightSound;
    public static AudioClip wrongSound;
    
    /** Creates a new instance of Main */
    public Main(boolean useSplashImage) {
        System.setProperty("sun.awt.exception.handler", "GoGrinder.ExceptionHandler"); //$NON-NLS-1$ //$NON-NLS-2$
        GRINDER_VERSION = Messages.getString("gg_version")  //$NON-NLS-1$
				+ " " + NUM_VER_STRING; //$NON-NLS-1$
        
        Browser.init();
        
        chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);
        
        SplashScreen splash = new SplashScreen(probFrame, useSplashImage);
        
        /*Runtime run = Runtime.getRuntime();
        System.out.println(run.totalMemory() - run.freeMemory());*/
        File probDir = new File("problems"); //$NON-NLS-1$
        if(!probDir.exists()) {
            JOptionPane.showMessageDialog(probFrame, Messages.getString("couldnt_find_probdir") //$NON-NLS-1$
                                              + "\n" + Messages.getString("curdir_incorrect")); //$NON-NLS-1$ //$NON-NLS-2$
            System.exit(-1);
        }
        GS.setCollections(new ProbCollection(null, probDir, splash));
        //System.out.println(run.totalMemory() - run.freeMemory());
        
        try {
            splash.setStatus(Messages.getString("loading_settings")); //$NON-NLS-1$
            GS.loadSettings();
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(probFrame, Messages.getString("err_loading_settings")); //$NON-NLS-1$
            if(!(e instanceof FileNotFoundException))
                log(e);
        }

        File saveDir = GS.getSaveDir();
        if(saveDir != null && saveDir.exists())
        	chooser.setCurrentDirectory(saveDir);
        
        probFrame = new ProbFrame(splash);
        wgfFrame = new WGFFrame();
        
        if(GS.getCheckForUpdates()) {
            splash.setStatus(Messages.getString("update_checking")); //$NON-NLS-1$
            checkForUpdates();
        }
        
        gxpFilter = new GGFileFilter(Messages.getString("gg_export_files"), ".gxp"); //$NON-NLS-1$ //$NON-NLS-2$
        ggsFilter = new GGFileFilter(Messages.getString("gg_state_files"), ".ggs"); //$NON-NLS-1$ //$NON-NLS-2$
        wgfFilter = new GGFileFilter(Messages.getString("Main.WGFFiles"), ".wgf"); //$NON-NLS-1$ //$NON-NLS-2$
        
        //splash.setStatus(Messages.getString("Main.LoadingSounds")); //$NON-NLS-1$
        if(GS.getClickSoundEnabled() || GS.getSoundEnabled())
            loadSounds(this);
        
        //splash.setStatus(Messages.getString("Main.LoadingWGFHistory")); //$NON-NLS-1$
        wgfFrame.loadHistory();
        
        splash.setVisible(false);
        splash.dispose();
        
        probFrame.selectFirstProb();
        
        if(GS.getProbState())
            probFrame.setVisible(true);
        else wgfFrame.setVisible(true);
    }
    
    public static void onExit() {
        try {
            GS.setProbFrameBounds(probFrame.getBounds());
            GS.setWGFFrameBounds(wgfFrame.getBounds());
            GS.setSplitterPos(wgfFrame.getSplitterPos());
            GS.setSaveDir(chooser.getCurrentDirectory());
            wgfFrame.saveHistory();
            GS.saveSettings();
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, Messages.getString("err_saving_settings")); //$NON-NLS-1$
            Main.log(e);
        }
        
        System.exit(0);
    }
    
    public static void switchState() {
        boolean probState = GS.getProbState();
        probState = !probState;
        GS.setProbState(probState);
        
        if(probState)
            GS.getSelectedSets().resetProblemTime();
        
        probFrame.setVisible(probState);
        wgfFrame.setVisible(!probState);
    }

    public static ImageIcon getIcon(String imageName, Object o) {
        String imagePath = "GoGrinder/images/" + imageName; //$NON-NLS-1$
        ClassLoader cl = o.getClass().getClassLoader();
        URL url = cl.getResource(imagePath);
        return new ImageIcon(url);
    }
    
    public static String chooseFile(Component owner, GGFileFilter filter, String title,
                                    boolean save, String buttonText) {
        chooser.setDialogTitle(title);
        chooser.resetChoosableFileFilters();
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setApproveButtonText(buttonText);
        
        int ret;
        if(save)
            ret = chooser.showSaveDialog(owner);
        else ret = chooser.showOpenDialog(owner);
        
        if(ret != JFileChooser.APPROVE_OPTION)
            return null;
        
        File f = chooser.getSelectedFile();
        if(save) {
            if(f.exists()) {
                int choice = JOptionPane.showConfirmDialog(owner,
                                              f.getName() + " " + Messages.getString("already_exists_overwrite"), //$NON-NLS-1$ //$NON-NLS-2$
                                              null, JOptionPane.YES_NO_OPTION);
                if(choice == JOptionPane.NO_OPTION)
                    return null;
            }
        }
        else if(!f.exists()) {
            JOptionPane.showMessageDialog(owner, f.getName() + " " + Messages.getString("does_not_exist")); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
        
        if(!f.getName().toLowerCase().endsWith(filter.getExt()))
            return f.getPath() + filter.getExt();
        return f.getPath();
    }
    
    private void checkForUpdates() {
        try {
            URL urladdress = new URL ("http://gogrinder.sourceforge.net/version.txt"); //$NON-NLS-1$
            HttpURLConnection conn = (HttpURLConnection)urladdress.openConnection();
            conn.connect();
            int responseCode = conn.getResponseCode();
            if(responseCode == 200)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                int ver = Integer.parseInt(in.readLine());
                if(ver > NUMERIC_VERSION) {
                	String changes = ""; //$NON-NLS-1$
                	String line;
                	while((line = in.readLine()) != null) {
                		if(line.startsWith(VERSION_STRING))
                			break;
                		changes += line + "\n"; //$NON-NLS-1$
                	}
                	
                	NewVersionDialog d = new NewVersionDialog(changes);
                	if(d.getNewVersion()) {
                        Browser.displayURLinNew("http://sourceforge.net/project/showfiles.php?group_id=115946"); //$NON-NLS-1$
                        System.exit(0);
                    }
                }
            }
            else JOptionPane.showMessageDialog(null, Messages.getString("update_check_err_num") + " " + responseCode); //$NON-NLS-1$ //$NON-NLS-2$

            conn.disconnect();
        }
        catch(IOException e) {
            JOptionPane.showMessageDialog(null, Messages.getString("update_check_failed")); //$NON-NLS-1$
            logSilent(e);
        }
    }
    
    private static void loadSounds(Object obj) {
        clickSound = Applet.newAudioClip(obj.getClass().getResource("/GoGrinder/sounds/click.wav")); //$NON-NLS-1$
        rightSound = Applet.newAudioClip(obj.getClass().getResource("/GoGrinder/sounds/right.wav")); //$NON-NLS-1$
        wrongSound = Applet.newAudioClip(obj.getClass().getResource("/GoGrinder/sounds/wrong.wav")); //$NON-NLS-1$
    }
    
    public static void log(Throwable t) { handler.handle(t); }
    public static void logSilent(Throwable t) { handler.logSilent(t); }
    public static void logSilent(Throwable t, String filename) { handler.logSilent(t, filename); }
    
    static void setColors() {
        UIManager.put("Button.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("CheckBox.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("CheckBoxMenuItem.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("ComboBox.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("ComboBox.disabledBackground", BGCOLOR); //$NON-NLS-1$
        UIManager.put("Menu.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("MenuBar.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("MenuItem.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("OptionPane.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("Panel.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("ScrollBar.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("ScrollBar.thumb", BGCOLOR); //$NON-NLS-1$
        UIManager.put("Table.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("TableHeader.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("ToggleButton.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("ToolBar.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("Viewport.background", BGCOLOR); //$NON-NLS-1$
        UIManager.put("control", BGCOLOR); //$NON-NLS-1$
        
        UIManager.put("Button.select", SELCOLOR); //$NON-NLS-1$
        UIManager.put("ToggleButton.select", SELCOLOR); //$NON-NLS-1$
    }
    
    static class GGFileFilter extends FileFilter {
        private String desc;
        private String ext;
        
        GGFileFilter(String desc, String ext) {
            this.desc = desc;
            this.ext = ext;
        }
        
        public String getDescription() { return desc; }
        public String getExt() { return ext; }
        
        public boolean accept(File f) {
            if(f.isDirectory()) return true;
            return f.getName().toLowerCase().endsWith(ext);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String [] args) {
        String version = System.getProperty("java.version"); //$NON-NLS-1$
        
        try {
            StringTokenizer tok = new StringTokenizer(version, "._-"); //$NON-NLS-1$
            int major = Integer.parseInt(tok.nextToken());
            int minor = Integer.parseInt(tok.nextToken());
            int sub = Integer.parseInt(tok.nextToken());

            boolean badver = false;
            if(major == 1) {
                if(minor < 4)
                    badver = true;
                else if(minor == 4) {
                    if(sub < 2)
                        badver = true;
                }
            }

            if(badver) {
                System.out.println(Messages.getString("java_142_required")); //$NON-NLS-1$
                System.out.println(Messages.getString("you_are_running_ver") + " " + version); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }
        }
        catch(Exception e) {
            System.out.println(Messages.getString("warn_couldnt_parse_ver") + " " + version); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println(Messages.getString("java_142_required")); //$NON-NLS-1$
        }
        
        try {
            setColors();
            
            boolean nosplash = false;
            String lang = null;
            for(int i = 0; i < args.length; i++) {
            	if(args[i].equals("nosplash")) //$NON-NLS-1$
            		nosplash = true;
            	else if(args[i].startsWith("defaultLanguage")) { //$NON-NLS-1$
            		int index = args[i].indexOf("="); //$NON-NLS-1$
            		if(index == -1) {
            			System.out.println("Correct format is defaultLanguage=en"); //$NON-NLS-1$
            			System.exit(-1);
            		}
            		lang = args[i].substring(index + 1);
            	}
            }
            
            if(lang != null) {
            	setDefaultLang(lang);
            }
            
            new Main(!nosplash);
        }
        catch(Exception e) {
            log(e);
            System.exit(-1);
        }
    }
	
	public static void setDefaultLang(String lang) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(LOCALE_FILE));
			out.println(lang);
			out.close();
		}
		catch(IOException e) {
			System.out.println("Error saving default locale to " + LOCALE_FILE); //$NON-NLS-1$
		}
	}
    
    public static void showPrefs(JFrame parent) {
        new SettingsDialog(parent);
        if(clickSound == null && (GS.getSoundEnabled() || GS.getClickSoundEnabled()))
            loadSounds(parent);
    }
}
