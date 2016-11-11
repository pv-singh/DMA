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
import javax.swing.tree.*;

import GoGrinder.ui.*;

/**
 *
 * @author  tkington
 */
public class ProbCollection extends DefaultMutableTreeNode {
    private static final long serialVersionUID = -4391979107075368395L;
    private static final int NUM_HIGH_SCORES = 10;
    
    private int size;
    private ArrayList probs;
    private File dir;
    private ProbCollection parent;
    
    private int numTried;
    private int numRight;
    private long totalTime;
    
    private int cursetNumTried;
    private int cursetNumRight;
    private long cursetTotalTime;
    private TreeSet highScores;
    
    /** Creates a new instance of ProbCollection */
    public ProbCollection() {
        //  This is just here for Serialization
    }
    
    public ProbCollection(ProbCollection parent, File dir, StatusListener status) {
        super(dir.getName());
        this.dir = dir;
        this.parent = parent;
        highScores = new TreeSet();
        
        status.setStatus(Messages.getString("loading") + " " + dir.getPath()); //$NON-NLS-1$ //$NON-NLS-2$
        
        loadStats();
        
        probs = new ArrayList();
        
        File [] files = dir.listFiles();
        for(int i = 0; i < files.length; i++) {
            if(files[i].isDirectory()) {
                ProbCollection child = new ProbCollection(this, files[i], status);
                add(child);
                size += child.getSize();
            }
            else if(files[i].getName().toLowerCase().endsWith(".sgf")) { //$NON-NLS-1$
                probs.add(new ProbData(this, files[i]));
                size++;
            }
        }
        
        Collections.sort(probs);
        for(int i = 0; i < probs.size(); i++) {
            ProbData p = (ProbData)probs.get(i);
            p.setNum(i);
        }
    }
    
    public void getSelectionStats(SelectionStats s, ArrayList tags, int matchType) {
        for(int i = 0; i < probs.size(); i++) {
            ProbData p = (ProbData)probs.get(i);
            if(p.matchesTags(tags, matchType))
                p.getSelectionStats(s);
        }
        
        for(int i = 0; i < getChildCount(); i++) {
            ProbCollection c = (ProbCollection)getChildAt(i);
            c.getSelectionStats(s, tags, matchType);
        }
    }
    
    public void getProblems(ArrayList problems, ArrayList tags, int matchType) {
        for(int i = 0; i < probs.size(); i++) {
            ProbData p = (ProbData)probs.get(i);
            if(p.matchesTags(tags, matchType))
                problems.add(p);
        }
        
        for(int i = 0; i < getChildCount(); i++) {
            ProbCollection c = (ProbCollection)getChildAt(i);
            c.getProblems(problems, tags, matchType);
        }
    }
    
    public void probDone(boolean right, long time) {
        numTried++;
        if(right) {
            numRight++;
            totalTime += time;
        }
        
        cursetNumTried++;
        if(right) {
            cursetNumRight++;
            cursetTotalTime += time;
        }
        
        saveStats();
        if(parent != null)
            parent.probDone(right, time);
    }
    
    public void startNewSet() {
        if(cursetNumTried == size && cursetNumTried != 0) {
            HighScore h = new HighScore(cursetNumRight, cursetNumTried, cursetTotalTime);
            HighScore worst = null;
            if(!highScores.isEmpty())
                worst = (HighScore)highScores.last();
            if(highScores.size() < NUM_HIGH_SCORES || h.compareTo(worst) < 0) {
                highScores.add(h);
                if(highScores.size() > NUM_HIGH_SCORES)
                    highScores.remove(worst);
                new HighScoreDialog(ProbFrame.inst, (String)getUserObject(), highScores, h);
            }
        }
        
        cursetNumTried = 0;
        cursetNumRight = 0;
        cursetTotalTime = 0;
        
        saveStats();
        
        for(int i = 0; i < getChildCount(); i++) {
            ProbCollection ch = (ProbCollection)getChildAt(i);
            ch.startNewSet();
        }
    }
    
    public ProbCollection findCollectionByDir(File d) {
        if(dir.equals(d))
            return this;
        
        for(int i = 0; i < getChildCount(); i++) {
            ProbCollection ch = (ProbCollection)getChildAt(i);
            ProbCollection ret = ch.findCollectionByDir(d);
            if(ret != null)
                return ret;
        }
        
        return null;
    }
    
    public ProbData findProblem(ProbData p) {
        File parentDir = p.getFile().getParentFile();
        ProbCollection c = findCollectionByDir(parentDir);
        
        if(c == null)
            return null;
        
        try {
            ProbData prob = c.getAt(p.getNum());
            if(prob.getFile().equals(p.getFile()))
                return prob;
            return null;
        }
        catch(IndexOutOfBoundsException e) {
            return null;
        }
    }
    
    private String getDatName() {
        String path = dir.getPath() + File.separator + "collection.dat"; //$NON-NLS-1$
        int index = path.indexOf(Main.PROBLEM_DIR);
        path = path.substring(0, index) + Main.STATS_DIR + path.substring(index + Main.PROBLEM_DIR.length());
        return path;
    }

    private static final int REVISION = 2;
    private void loadStats() {
        try {
            File f = new File(getDatName());
            
            File parentFile = f.getParentFile();
            if(!parentFile.exists()) {
                if(!parentFile.mkdirs()) {
                    JOptionPane.showMessageDialog(null, Messages.getString("couldnt_create_dir") //$NON-NLS-1$
                                                        + " " + parent.getPath()); //$NON-NLS-1$
                    throw new Exception(Messages.getString("couldnt_create_dir")  //$NON-NLS-1$
                                                    + " " + parent.getPath()); //$NON-NLS-1$
                }
            }
            
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
            
            int rev = in.readInt();
            if(rev > REVISION) {
                String msg = Messages.getString("err_data_file_newer"); //$NON-NLS-1$
                JOptionPane.showMessageDialog(null, msg);
                Main.logSilent(new Exception(msg));
                System.exit(-1);
            }
            
            numTried = in.readInt();
            numRight = in.readInt();
            totalTime = in.readLong();
            
            if(rev < 2) {
                cursetNumTried = in.readInt();
                cursetNumRight = in.readInt();
                cursetTotalTime = in.readLong();
            }
            
            highScores = (TreeSet)in.readObject();
            
            in.close();
        }
        catch(FileNotFoundException e) {
            //  Do nothing
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, Messages.getString("err_reading") + " " + getDatName()); //$NON-NLS-1$ //$NON-NLS-2$
            Main.logSilent(e);
        }
    }
    
    private void saveStats() {
        String filename = getDatName();
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeInt(REVISION);
            out.writeInt(numTried);
            out.writeInt(numRight);
            out.writeLong(totalTime);
            out.writeObject(highScores);
            out.close();
            
            if(parent != null)
                parent.saveStats();
        }
        catch(IOException e) {
            JOptionPane.showMessageDialog(null, Messages.getString("err_writing") + " " + filename); //$NON-NLS-1$ //$NON-NLS-2$
            Main.logSilent(e);
        }
    }
    
    public void deleteStats() {
        File f = new File(getDatName());
        if(f.exists()) {
            if(!f.delete())
                JOptionPane.showMessageDialog(null, Messages.getString("err_deleting") + " " + f.getPath()); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    public boolean equals(Object o) {
        ProbCollection c = (ProbCollection)o;
        return dir.equals(c.dir) && size == c.size;
    }
    
    private void removeStats(int tr, int ri, long time) {
        numTried -= tr;
        numRight -= ri;
        totalTime -= time;
        
        if(parent != null)
            parent.removeStats(tr, ri, time);
    }
    
    private void resetStats(HashSet colls, ProgressDialog d) {
        colls.add(this);
        if(getChildCount() == 0)
            removeStats(numTried, numRight, totalTime);
        else {
            for(int i = 0; i < getChildCount(); i++) {
                ProbCollection c = (ProbCollection)getChildAt(i);
                c.resetStats(colls, d);
            }
        }
        
        for(int i = 0; i < probs.size(); i++) {
            ProbData p = (ProbData)probs.get(i);
            p.resetStats();
            d.bump();
        }
    }
    
    public void resetStats(ProgressDialog d) {
        HashSet colls = new HashSet();
        resetStats(colls, d);
        
        Iterator it = colls.iterator();
        while(it.hasNext()) {
            ProbCollection c = (ProbCollection)it.next();
            c.deleteStats();
        }
    }
    
    public void addTag(String t, ProgressDialog d) {
        for(int i = 0; i < getChildCount(); i++) {
            ProbCollection c = (ProbCollection)getChildAt(i);
            c.addTag(t, d);
        }
        
        for(int i = 0; i < probs.size(); i++) {
            ProbData p = (ProbData)probs.get(i);
            p.addTag(t);
            d.bump();
        }
    }
    
    public void removeTags(ArrayList tags, ProgressDialog d) {
        for(int i = 0; i < getChildCount(); i++) {
            ProbCollection c = (ProbCollection)getChildAt(i);
            c.removeTags(tags, d);
        }
        
        for(int i = 0; i < probs.size(); i++) {
            ProbData p = (ProbData)probs.get(i);
            for(int j = 0; j < tags.size(); j++)
                p.removeTag((String)tags.get(j));
            d.bump();
        }
    }
    
    public int getSize() { return size; }
    public ProbData getAt(int i) { return (ProbData)probs.get(i); }
    public File getDir() { return dir; }
    public int getCursetNumTried() { return cursetNumTried; }
    public void setCursetNumTried(int n) { cursetNumTried = n; }
    public int getCursetNumRight() { return cursetNumRight; }
    public void setCursetNumRight(int n) { cursetNumRight = n; }
    public long getCursetTotalTime() { return cursetTotalTime; }
    public void setCursetTotalTime(long t) { cursetTotalTime = t; }
    public TreeSet getHighScores() { return highScores; }
    public int hashCode() { return dir.hashCode(); }
}
