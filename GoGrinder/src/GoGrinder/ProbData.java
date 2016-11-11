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
import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author  tkington
 */
public class ProbData implements Comparable {
    private File file;
    private int num;
    
    private ProbCollection parent;
    private int timesTried;
    private int timesRight;
    private long totalTime;
    private HashSet tags;
    private long lastTried;
    
    private boolean loaded;
    
    /** Creates a new instance of ProbData */
    public ProbData(ProbCollection parent, File f) {
        file = f;
        this.parent = parent;
        tags = new HashSet();
        lastTried = 0;
        loaded = false;
    }
    
    public ProbData() { /* Just for serialization */ }
    
    private void load() {
    	if(!loaded)
    		loadStats();
    }
    
    public void probDone(boolean right, long time) {
    	load();
    	
        timesTried++;
        if(right) {
            timesRight++;
            totalTime += time;
        }
        
        lastTried = System.currentTimeMillis();
        parent.probDone(right, time);
        saveStats();
    }
    
    public double getAvgTime() {
    	load();
    	
        if(timesRight == 0)
            return 60000;
        return (double)totalTime / timesRight;
    }
    
    private String getDatName() {
        String path = file.getPath();
        path = path.substring(0, path.length() - 4) + ".dat"; //$NON-NLS-1$
        int index = path.indexOf(Main.PROBLEM_DIR);
        path = path.substring(0, index) + Main.STATS_DIR + path.substring(index + Main.PROBLEM_DIR.length());
        return path;
    }

    private static final int STATREVISION = 1;
    public void loadStats() {
        try {
            File f = new File(getDatName());
            
            File parentFile = f.getParentFile();
            if(!parentFile.exists()) {
                if(!parentFile.mkdirs())
                    JOptionPane.showMessageDialog(null, Messages.getString("couldnt_create_dir") //$NON-NLS-1$
                                                        + " " + parentFile.getPath()); //$NON-NLS-1$
            }
            
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
            
            int rev = in.readInt();
            if(rev > STATREVISION) {
                String msg = Messages.getString("err_data_file_newer"); //$NON-NLS-1$
                JOptionPane.showMessageDialog(null, msg);
                Main.logSilent(new Exception(msg));
                System.exit(-1);
            }
            
            timesTried = in.readInt();
            timesRight = in.readInt();
            totalTime = in.readLong();
            tags = (HashSet)in.readObject();
            lastTried = in.readLong();
            in.close();
            
            loaded = true;
        }
        catch(FileNotFoundException e) {
            //  No stats for this problem yet
        	
        	//	Don't try to load file again
        	loaded = true;
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, Messages.getString("err_reading") + " " + getDatName()); //$NON-NLS-1$ //$NON-NLS-2$
            Main.logSilent(e);
        }
    }
    
    public void saveStats() {
    	load();
    	
        String filename = getDatName();
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeInt(STATREVISION);
            out.writeInt(timesTried);
            out.writeInt(timesRight);
            out.writeLong(totalTime);
            out.writeObject(tags);
            out.writeLong(lastTried);
            out.close();
        }
        catch(IOException e) {
            JOptionPane.showMessageDialog(null, Messages.getString("err_writing") + " " + filename); //$NON-NLS-1$ //$NON-NLS-2$
            Main.logSilent(e);
        }
    }
    
    public void export(ObjectOutputStream out) throws IOException {
    	load();
    	
        out.writeObject(file);
        out.writeLong(file.lastModified());
        out.writeObject(getSGF());
        out.writeObject(tags);
    }
    
    public void resetStats() {
    	load();
    	
        totalTime = timesTried = timesRight = 0;
        saveStats();
    }
    
    public String getSGF() {
    	load();
    	
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            
            int len = (int)file.length();
            StringBuffer data = new StringBuffer(len);
            String line;
            while((line = in.readLine()) != null) {
                data.append(line + "\n"); //$NON-NLS-1$
            }
            
            in.close();
            return data.toString();
        }
        catch(IOException e) {
            //  This will cause an exception in SGFParser that will be logged.
            return null;
        }
    }
    
    public void addTag(String t) {
    	load();
    	
        if(tags.add(t))
            saveStats();
    }
    
    public void removeTag(String t) {
    	load();
    	
        if(tags.remove(t))
            saveStats();
    }
    
    public HashSet getTags() {
    	load();
    	return tags;
    }
    
    public boolean matchesTags(ArrayList t, int matchType) {
    	load();
    	
        switch(matchType) {
            case Selection.MATCH_ALL:
                for(int i = 0; i < t.size(); i++) {
                    if(!tags.contains(t.get(i)))
                        return false;
                }
                return true;
            case Selection.MATCH_ANY:
                for(int i = 0; i < t.size(); i++) {
                    if(tags.contains(t.get(i)))
                        return true;
                }
                return false;
            case Selection.MATCH_NONE:
                for(int i = 0; i < t.size(); i++) {
                    if(tags.contains(t.get(i)))
                        return false;
                }
                return true;
        }
        
        throw new RuntimeException();
    }
    
    public void getSelectionStats(SelectionStats s) {
    	load();
    	
        s.addStats(timesTried, timesRight, totalTime);
    }

    //  This only saves the parent dir, the problem number, and filename so that 
    //  we can find this problem again
    private static final int REVISION = 1;
    public void readData(ObjectInput in) throws IOException, ClassNotFoundException {
    	//	No load here
        int rev = in.readInt();
        if(rev > REVISION) {
            String msg = Messages.getString("err_data_file_newer"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(null, msg);
            Main.logSilent(new Exception(msg));
            System.exit(-1);
        }
        
        file = (File)in.readObject();
        num = in.readInt();
    }
    
    public void writeData(ObjectOutput out) throws IOException {
    	//	No load here
        out.writeInt(REVISION);
        out.writeObject(file);
        out.writeInt(num);
    }
    
    public int compareTo(Object o) {
    	//	No load here - only compares file
        ProbData p = (ProbData)o;
        return file.compareTo(p.file);
    }

    public File getFile() {
    	//	No load here - file not in stats
    	return file;
    }
    
    public int getNum() {
    	//	No load here - num not in stats
    	return num;
    }
    
    public void setNum(int n) {
    	//	No load here - num not in stats
    	num = n;
    }
    
    public long getLastTried() {
    	load();
    	return lastTried;
    }
    
    public void setTags(HashSet tags) {
    	load();
    	this.tags.clear();
    	this.tags.addAll(tags);
    }
}
