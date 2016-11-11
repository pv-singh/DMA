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
import javax.swing.*;

import GoGrinder.ui.*;
import GoGrinder.sgf.*;

/**
 *
 * @author  tkington
 */
public class FileUtils {
    private static javax.swing.filechooser.FileFilter sgfFilter;
    private static HashMap genreMap;
    
    private JFileChooser chooser;
    
    /** Creates a new instance of Utils */
    public FileUtils(JFileChooser ch) {
        init();
        chooser = ch;
    }
    
    
    public void splitFile() {
        chooser.setDialogTitle(Messages.getString("select_sgf_file")); //$NON-NLS-1$
        chooser.resetChoosableFileFilters();
        chooser.setFileFilter(sgfFilter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setApproveButtonText(Messages.getString("split")); //$NON-NLS-1$
        int ret = chooser.showOpenDialog(ProbFrame.inst);
        if(ret != JFileChooser.APPROVE_OPTION)
            return;
        
        File f = chooser.getSelectedFile();
        
        File dir = chooseProblemDir();
        if(dir == null)
            return;
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            
            int len = (int)f.length();
            StringBuffer data = new StringBuffer(len);
            String line;
            while((line = in.readLine()) != null) {
                data.append(line);
            }
            
            in.close();
            
            SGFParser.split(dir, data.toString());
            JOptionPane.showMessageDialog(ProbFrame.inst, Messages.getString("split_complete")); //$NON-NLS-1$
        }
        catch(Exception e) {
            String msg = e.getMessage();
            if(msg.length() > 50)
                msg = msg.substring(0, 50) + Messages.getString("dot_dot_dot"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(ProbFrame.inst, Messages.getString("err_parsing_sgf") + "\n" + msg); //$NON-NLS-1$ //$NON-NLS-2$
            Main.logSilent(e);
        }
        
        ReloadDialog.reloadProblems(ProbFrame.inst);
    }
    
    private File chooseProblemDir() {
        File curDir = chooser.getCurrentDirectory();
        chooser.setCurrentDirectory(new File("problems")); //$NON-NLS-1$
        chooser.setDialogTitle(Messages.getString("select_dir_for_new_probs")); //$NON-NLS-1$
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setApproveButtonText(Messages.getString("save")); //$NON-NLS-1$
        int ret = chooser.showOpenDialog(ProbFrame.inst);
        chooser.setCurrentDirectory(curDir);
        if(ret != JFileChooser.APPROVE_OPTION)
            return null;
        File dir = chooser.getSelectedFile();
        
        //  Make sure we're inside the problems directory
        File probsDir = new File("problems"); //$NON-NLS-1$
        probsDir = new File(probsDir.getAbsolutePath());
        File f = dir;
        while(f != null && !f.equals(probsDir))
            f = f.getParentFile();
        
        if(f == null || !f.equals(probsDir)) {
            JOptionPane.showMessageDialog(ProbFrame.inst, Messages.getString("dest_must_be_in_probs_dir")); //$NON-NLS-1$
            return null;
        }
        
        return dir;
    }
    
    private void init() {
        if(genreMap != null)
            return;
        
        genreMap = new HashMap();
        genreMap.put("life and death", Messages.getString("life_and_death")); //$NON-NLS-1$ //$NON-NLS-2$
        genreMap.put("joseki", Messages.getString("joseki")); //$NON-NLS-1$ //$NON-NLS-2$
        genreMap.put("tesuji", Messages.getString("tesuji")); //$NON-NLS-1$ //$NON-NLS-2$
        genreMap.put("endgame", Messages.getString("endgame")); //$NON-NLS-1$ //$NON-NLS-2$
        genreMap.put("best move", Messages.getString("best_move")); //$NON-NLS-1$ //$NON-NLS-2$
        genreMap.put("fuseki", Messages.getString("fuseki")); //$NON-NLS-1$ //$NON-NLS-2$
        
        sgfFilter = new javax.swing.filechooser.FileFilter() {
            public String getDescription() { return Messages.getString("sgf_files"); } //$NON-NLS-1$
            public boolean accept(File f) {
                if(f.isDirectory()) return true;
                return f.getName().toLowerCase().endsWith(".sgf"); //$NON-NLS-1$
            }
        };
    }
}
