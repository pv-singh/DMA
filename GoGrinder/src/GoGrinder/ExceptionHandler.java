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
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author  tkington
 */
public class ExceptionHandler {
    public void handle(Throwable t) {
        JOptionPane.showMessageDialog(null, Messages.getString("ex_occurred_see_grind_log")); //$NON-NLS-1$
        logSilent(t);
    }
    
    public void logSilent(Throwable t) {
        logSilent(t, null);
    }
    
    public void logSilent(Throwable t, String filename) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("grind.log", true)); //$NON-NLS-1$
            out.println(new Date() + ":"); //$NON-NLS-1$
            
            if(filename != null)
                out.println(Messages.getString("err_while_parsing_file") + " " + filename); //$NON-NLS-1$ //$NON-NLS-2$
            
            t.printStackTrace(out);
            out.close();
        }
        catch(IOException e) {
            System.out.println(Messages.getString("err_logging_ex")); //$NON-NLS-1$
            e.printStackTrace();
            t.printStackTrace();
            JOptionPane.showMessageDialog(null, Messages.getString("err_logging_ex")); //$NON-NLS-1$
        }
    }
}
