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

package GoGrinder.ui;

import java.awt.event.*;
import javax.swing.*;

import GoGrinder.*;

/**
 *
 * @author  tkington
 */
public class TagCB extends JComboBox {
    DefaultComboBoxModel model;
    int numTags;
    TagListener listener;
    
    /** Creates a new instance of TagCB */
    public TagCB(TagListener l) {
        super(new DefaultComboBoxModel());
        
        fillCB();
        
        listener = l;
        
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSelChanged();
            }
        });
    }
    
    public void fillCB() {
        model = (DefaultComboBoxModel)getModel();
        model.removeAllElements();
        
        model.addElement(Messages.getString("apply_tag")); //$NON-NLS-1$
        
        String [] tags = GS.getTagList().getTags();
        for(int i = 0; i < tags.length; i++)
            model.addElement(tags[i]);
        
        model.addElement("----------"); //$NON-NLS-1$
        model.addElement(Messages.getString("new_tag")); //$NON-NLS-1$
        
        numTags = tags.length;
    }
    
    public void onSelChanged() {
        int i = getSelectedIndex();
        if(i == -1)
            return;
        
        if(i == 0 || i == numTags + 1) {
            setSelectedIndex(0);
            return;
        }
        
        if(i == numTags + 2) {
            String nt = JOptionPane.showInputDialog(this, Messages.getString("new_tag_q")); //$NON-NLS-1$
            if(nt == null)
                return;
            
            if(!GS.getTagList().addTag(nt)) {
                JOptionPane.showMessageDialog(this, Messages.getString("dup_tag")); //$NON-NLS-1$
                return;
            }
            
            boolean done = false;
            for(int j = 0; j < numTags; j++) {
                if(nt.compareTo((String)getItemAt(j + 1)) < 0) {
                    model.insertElementAt(nt, j + 1);
                    done = true;
                    break;
                }
            }
            
            if(!done)
                model.insertElementAt(nt, numTags + 1);
            
            setSelectedIndex(0);
            numTags++;
            
            listener.newTagCreated();
        }
        else {
            listener.addTag((String)getSelectedItem());
            setSelectedIndex(0);
        }
    }
}
