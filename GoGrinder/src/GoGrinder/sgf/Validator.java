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

package GoGrinder.sgf;

import java.io.*;
import java.util.*;

import GoGrinder.Messages;

public class Validator {
    int numFiles;
    int numBad;
    
    public static void validate(String sgf) throws Exception {
        ArrayList recs = SGFParser.parse(sgf);
        if(recs.size() > 1)
            throw new Exception(Messages.getString("mult_probs")); //$NON-NLS-1$
        SGFNode root = (SGFNode)recs.get(0);

        String size = root.getProperty("SZ"); //$NON-NLS-1$
        int s;
        if(size != null)
            s = Integer.parseInt(size);
        s = 19;

        root.validatePoints(s);
        
        if(root.getBounds() == null)
            throw new Exception(Messages.getString("no_points")); //$NON-NLS-1$
    }
    
    void validate(File dir) {
    	System.out.println(dir.getPath());
        File [] files = dir.listFiles();
        for(int i = 0; i < files.length; i++) {
            File f = files[i];
            if(f.isDirectory())
                validate(f);
            else {
                try {
                    BufferedReader in = new BufferedReader(new FileReader(f));
                    String line;
                    StringBuffer sgf = new StringBuffer();
                    while((line = in.readLine()) != null)
                        sgf.append(line + "\n"); //$NON-NLS-1$
                    in.close();
                    
                    validate(sgf.toString());
                    
                    numFiles++;
                }
                catch(Exception e) {
                    numBad++;
                    System.out.println(Messages.getString("parsing") + " " + f.getPath()); //$NON-NLS-1$ //$NON-NLS-2$
                    try { Thread.sleep(50); } catch(InterruptedException ex) { /* */ }
                    e.printStackTrace();
                    try { Thread.sleep(50); } catch(InterruptedException ex) { /* */ }
                }
            }
        }
    }
    
    public static void main(String[] args) {
    	Validator v = new Validator();
        v.validate(new File("problems")); //$NON-NLS-1$
        System.out.println(v.numFiles + " valid files"); //$NON-NLS-1$
        System.out.println(v.numBad + " bad files"); //$NON-NLS-1$
    }
    
}
