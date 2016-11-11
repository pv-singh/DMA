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
public class HighScore implements Comparable, Externalizable {
    private int numRight;
    private int numProbs;
    private double avgTime;
    private Date date;
    
    public HighScore() { /* Just for serialization */ }
    
    public HighScore(int right, int num, long totTime) {
        numRight = right;
        numProbs = num;
        avgTime = (double)totTime / num;
        date = new Date();
    }
    
    public boolean equals(Object o) {
        HighScore h = (HighScore)o;
        if(!date.equals(h.date))
            return false;
        if(numRight != h.numRight)
            return false;
        if(numProbs != h.numProbs)
            return false;
        if(avgTime != h.avgTime)
            return false;
        return true;
    }
    
    public int compareTo(Object o) {
        HighScore s = (HighScore)o;
        double p1 = (double)numRight / numProbs;
        double p2 = (double)s.numRight / s.numProbs;
        if(p1 > p2)
            return -1;
        else if(p2 > p1)
            return 1;
        
        if(avgTime < s.avgTime)
            return -1;
        else if(s.avgTime < avgTime)
            return 1;
        
        return date.compareTo(s.date);
    }
    
    private static final int REVISION = 1;
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int rev = in.readInt();
        if(rev > REVISION) {
            String msg = Messages.getString("err_data_file_newer"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(null, msg);
            Main.logSilent(new Exception(msg));
            System.exit(-1);
        }
        
        numRight = in.readInt();
        numProbs = in.readInt();
        avgTime = in.readDouble();
        date = (Date)in.readObject();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(REVISION);
        out.writeInt(numRight);
        out.writeInt(numProbs);
        out.writeDouble(avgTime);
        out.writeObject(date);
    }

    public int getNumProbs() { return numProbs; }
    public int getNumRight() { return numRight; }
    public double getAvgTime() { return avgTime; }
    public Date getDate() { return date; }
}
