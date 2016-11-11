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
public class TagList implements Externalizable {
    private static final long serialVersionUID = 483521160770524344L;
    
    HashSet tags;
    
    /** Creates a new instance of TagList */
    public TagList() {
        tags = new HashSet();
        tags.add(Messages.getString("best_move")); //$NON-NLS-1$
        tags.add(Messages.getString("endgame")); //$NON-NLS-1$
        tags.add(Messages.getString("fuseki")); //$NON-NLS-1$
        tags.add(Messages.getString("favorites")); //$NON-NLS-1$
        tags.add(Messages.getString("joseki")); //$NON-NLS-1$
        tags.add(Messages.getString("life_and_death")); //$NON-NLS-1$
        tags.add(Messages.getString("tesuji")); //$NON-NLS-1$
    }
    
    public boolean addTag(String tag) {
        if(tags.contains(tag))
            return false;
        
        tags.add(tag);
        return true;
    }
    
    public void addTags(HashSet t) { tags.addAll(t); }
    
    public void removeTags(ArrayList t) {
        for(int i = 0; i < t.size(); i++)
            tags.remove(t.get(i));
    }
    
    public String [] getTags() {
        String [] t = (String[])tags.toArray(new String[tags.size()]);
        Arrays.sort(t);
        return t;
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
        
        tags = (HashSet)in.readObject();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(REVISION);
        out.writeObject(tags);
    }
}
