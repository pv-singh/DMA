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

package GoGrinder.tests;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class StringChecker {
    int num;
    
    /** Creates a new instance of StringChecker */
    public StringChecker() throws Exception {        
        String cp = System.getProperty("java.class.path"); //$NON-NLS-1$
        String [] t = cp.split(";"); //$NON-NLS-1$
        
        String root = null;
        for(int i = 0; i < t.length; i++) {
            if(t[i].endsWith("gogrinder")) { //$NON-NLS-1$
                root = t[i];
                break;
            }
        }
        
        if(root == null) {
            System.err.println("Couldn't find root!"); //$NON-NLS-1$
            return;
        }
        
        System.err.println("Checking " + root + " for strings..."); //$NON-NLS-1$ //$NON-NLS-2$
        num = 0;
        
        checkDir(new File(root, "GoGrinder")); //$NON-NLS-1$
    }
    
    //	Check to make sure all property files contain
    //	the same set of strings
    public void checkDir(File dir) throws Exception {
    	ArrayList propFiles = new ArrayList();
    	ArrayList filenames = new ArrayList();
    	
    	File [] files = dir.listFiles();
    	for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if(f.getName().endsWith(".properties")) { //$NON-NLS-1$
				Properties p = new Properties();
				p.load(new FileInputStream(f));
				propFiles.add(p);
				filenames.add(f.getName());
			}
		}

    	HashMap [] missingProps = new HashMap[propFiles.size()];
    	for(int i = 0; i < missingProps.length; i++)
    		missingProps[i] = new HashMap();
    	
    	Properties p = (Properties)propFiles.get(0);
    	Iterator iter = p.keySet().iterator();
    	while(iter.hasNext()) {
    		String key = (String)iter.next();
    		for(int i = 1; i < propFiles.size(); i++) {
    			Properties prop = (Properties)propFiles.get(i);
    			String val = prop.getProperty(key);
    			if(val == null)
    				missingProps[i].put(key, ((Properties)propFiles.get(0)).get(key));
    			else prop.remove(key);
    		}
    	}
    	
    	for(int i = 0; i < missingProps.length; i++) {
    		if(missingProps[i].size() == 0)
    			continue;
    		
    		System.err.println("*** " + filenames.get(i) + ":");  //$NON-NLS-1$//$NON-NLS-2$
    		iter = missingProps[i].keySet().iterator();
    		while(iter.hasNext()) {
    			String key = (String)iter.next();
    			System.err.println(key + "=" + missingProps[i].get(key)); //$NON-NLS-1$
    		}
    	}
    	
    	for(int i = 1; i < propFiles.size(); i++) {
    		p = (Properties)propFiles.get(i);
    		iter = p.keySet().iterator();
    		while(iter.hasNext()) {
    			String key = (String)iter.next();
    			System.err.println("Key " + key + " is not in all files"); //$NON-NLS-1$ //$NON-NLS-2$
    		}
    	}
    }
    
    public static void main(String[] args) {
    	try {
    		new StringChecker();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
}
