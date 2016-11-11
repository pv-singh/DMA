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
public class Selection implements Externalizable {
    private static final long serialVersionUID = 4665920713920515042L;
    
    public static final String [] ORDERS = {Messages.getString("order_number"),  //$NON-NLS-1$
                                            Messages.getString("order_random"), //$NON-NLS-1$
                                            Messages.getString("order_easiest"), //$NON-NLS-1$
                                            Messages.getString("order_hardest"), //$NON-NLS-1$
                                            Messages.getString("order_least_recent")}; //$NON-NLS-1$
    public static final String [] MATCHTYPES = {Messages.getString("match_all_tags"), //$NON-NLS-1$
                                                Messages.getString("match_any_tags"), //$NON-NLS-1$
                                                Messages.getString("match_no_tags")}; //$NON-NLS-1$
    public static final int MATCH_ALL = 0;
    public static final int MATCH_ANY = 1;
    public static final int MATCH_NONE = 2;
    
    private ArrayList sets;
    private ArrayList problems;
    private ArrayList tags;
    private boolean [] tried;
    
    private int numSolved;
    private int numRight;
    private int curIndex;
    private ProbData curProb;
    private long probStartTime;
    private int order;
    private int matchType;
    
    /** Creates a new instance of Selection */
    public Selection() {
        curIndex = -1;
        curProb = null;
        problems = new ArrayList();
        selectProblems(new ArrayList(), new ArrayList(), 0, 0);
    }
    
    public void selectProblems(ArrayList sets, ArrayList tags, int order, int matchType) {
        this.sets = sets;
        this.tags = tags;
        this.order = order;
        this.matchType = matchType;
        
        numSolved = 0;
        numRight = 0;
        problems.clear();
        
        for(int i = 0; i < sets.size(); i++) {
            ProbCollection c = (ProbCollection)sets.get(i);
            c.getProblems(problems, tags, matchType);
            c.startNewSet();
        }
        
        sortProblems(order);
        tried = new boolean[problems.size()];
        
        curIndex = -1;
        curProb = null;
    }
    
    public void sortProblems(int sortOrder) {
        switch(sortOrder) {
            case 0: // In Order
                return;
            case 1: // Random Order
                ArrayList newProb = new ArrayList(problems.size());
                boolean [] used = new boolean[problems.size()];
                
                for(int i = 0; i < used.length; i++) {
                    int n = Main.rand.nextInt(used.length - i);
                    int num = 0;
                    for(int j = 0; j < used.length; j++) {
                        if(!used[j]) {
                            if(n == num) {
                                newProb.add(problems.get(j));
                                used[j] = true;
                            }
                            num++;
                        }
                    }
                }
                
                problems = newProb;
                break;
            case 2: // Easiest
                Collections.sort(problems, new Comparator() {
                   public int compare(Object a, Object b) {
                       ProbData p = (ProbData)a;
                       ProbData s = (ProbData)b;
                       double t1 = p.getAvgTime();
		       double t2 = s.getAvgTime();
		       if(t1 < t2)
                           return -1;
                       else if(t1 > t2)
                           return 1;
                       return 0;
                   }
                });
                break;
            case 3: // Hardest
                Collections.sort(problems, new Comparator() {
                   public int compare(Object a, Object b) {
                       ProbData p = (ProbData)a;
                       ProbData s = (ProbData)b;
                       double t1 = p.getAvgTime();
		       double t2 = s.getAvgTime();
		       if(t1 < t2)
                           return 1;
                       else if(t1 > t2)
                           return -1;
                       return 0;
                   }
                });
                break;
            case 4: // Least Recent
                Collections.sort(problems, new Comparator() {
                   public int compare(Object a, Object b) {
                       long t1 = ((ProbData)a).getLastTried();
                       long t2 = ((ProbData)b).getLastTried();
                       if(t1 < t2)
                           return -1;
                       else if(t1 > t2)
                           return 1;
                       return 0;
                   }
                });
                break;
            default: throw new RuntimeException();
        }
    }
    
    public ProbData getNextProblem() {
        curIndex++;
        
        if(curIndex == problems.size()) {
            for(int i = 0; i < sets.size(); i++) {
                ProbCollection c = (ProbCollection)sets.get(i);
                c.startNewSet();
            }
            
            numSolved = 0;
            numRight = 0;
            curIndex = 0;
            Arrays.fill(tried, false);
        }
        
        curProb = (ProbData)problems.get(curIndex);
        resetProblemTime();
        return curProb;
    }
    
    public ProbData getPrevProblem() {
        curIndex--;
        if(curIndex == -1)
            curIndex = problems.size() - 1;
        
        curProb = (ProbData)problems.get(curIndex);
        resetProblemTime();
        return curProb;
    }
    
    public ProbData findProblem(String sub) {
        ProbData p = null;
        int i = curIndex;
        
        while(true) {
            i++;
            if(i == problems.size())
                i = 0;
            
            if(i == curIndex)
                return null;
            
            p = (ProbData)problems.get(i);
            if(p.getFile().getName().indexOf(sub) != -1)
                break;
        }
        
        curIndex = i;
        curProb = p;
        resetProblemTime();
        return p;
    }
    
    public void probDone(boolean isRight) {
        if(!tried[curIndex]) {
            curProb.probDone(isRight, System.currentTimeMillis() - probStartTime);
            numSolved++;
            if(isRight)
                numRight++;
            tried[curIndex] = true;
        }
    }
    
    public int getNumSolved() { return numSolved; }
    public int getNumRight() { return numRight; }
    public int getNumSelected() { return problems.size(); }
    public int getCurIndex() { return curIndex; }
    public ProbData getCurProb() { return curProb; }
    public boolean isEmpty() { return problems.isEmpty(); }
    public ArrayList getSelectedSets() { return sets; }
    public ArrayList getSelectedTags() { return tags; }
    public int getOrder() { return order; }
    public int getMatchType() { return matchType; }
    
    private static final int REVISION = 10002;
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int rev = in.readInt();
        if(rev > REVISION) {
            String msg = Messages.getString("err_data_file_newer"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(null, msg);
            Main.logSilent(new Exception(msg));
            System.exit(-1);
        }
        
        if(rev >= 10001)
            numSolved = in.readInt();
        else numSolved = rev;
        
        numRight = in.readInt();
        curIndex = in.readInt();
        order = in.readInt();
        matchType = in.readInt();
        
        if(rev < 10001)
            in.readLong();
        
        ProbCollection colls = GS.getCollections();
        
        boolean missing = false;
        int numSets = in.readInt();
        sets = new ArrayList();
        for(int i = 0; i < numSets; i++) {
            File dir = (File)in.readObject();
            
            ProbCollection c = colls.findCollectionByDir(dir);
            
            if(rev > 10001) {
                int numTried = in.readInt();
                int numCorrect = in.readInt();
                long totTime = in.readLong();
                
                if(c != null) {
                    c.setCursetNumTried(numTried);
                    c.setCursetNumRight(numCorrect);
                    c.setCursetTotalTime(totTime);
                }
            }
            
            if(c != null)
                sets.add(c);
            else missing = true;
        }
        
        int numProbs = in.readInt();
        problems = new ArrayList(numProbs);
        ProbData p = new ProbData();
        for(int i = 0; i < numProbs; i++) {
            p.readData(in);
            ProbData prob = colls.findProblem(p);
            if(prob != null)
                problems.add(prob);
            else missing = true;
        }
        
        tags = (ArrayList)in.readObject();
        if(rev >= 10001)
            tried = (boolean[])in.readObject();
        else tried = new boolean[problems.size()];
        
        if(missing) {
            numSolved = numRight = 0;
            curIndex = -1;
            Arrays.fill(tried, false);
            
            JOptionPane.showMessageDialog(null, Messages.getString("some_probs_missing")); //$NON-NLS-1$
        }
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(REVISION);
        out.writeInt(numSolved);
        out.writeInt(numRight);
        out.writeInt(curIndex - 1);
        out.writeInt(order);
        out.writeInt(matchType);
        
        out.writeInt(sets.size());
        for(int i = 0; i < sets.size(); i++) {
            ProbCollection c = (ProbCollection)sets.get(i);
            out.writeObject(c.getDir());
            out.writeInt(c.getCursetNumTried());
            out.writeInt(c.getCursetNumRight());
            out.writeLong(c.getCursetTotalTime());
        }
        
        out.writeInt(problems.size());
        for(int i = 0; i < problems.size(); i++) {
            ProbData p = (ProbData)problems.get(i);
            p.writeData(out);
        }
        
        out.writeObject(tags);
        out.writeObject(tried);
    }
    
    public void resetProblemTime() { probStartTime = System.currentTimeMillis(); }

	public String getCurrentProblemPath() {
		if(curProb == null)
			return null;
		return curProb.getFile().getAbsolutePath();
	}
}
