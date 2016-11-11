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

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import GoGrinder.*;

/**
 *
 * @author  tkington
 */
public class HighScoreDialog extends JDialog {
    private static final String[] colNames = {Messages.getString("number_sign"), //$NON-NLS-1$
                                              Messages.getString("pct_right"), //$NON-NLS-1$
                                              Messages.getString("avg_time"), //$NON-NLS-1$
                                              Messages.getString("date")}; //$NON-NLS-1$
    private HighScore [] scores;
    private NumberFormat format;
    private SimpleDateFormat df;
    
    /** Creates a new instance of HighScoreDialog */
    public HighScoreDialog(JFrame owner, String collName, TreeSet scoreSet, HighScore newScore) {
        super(owner, true);
        setup(owner, collName, scoreSet, newScore);
    }
    
    public HighScoreDialog(JDialog owner, String collName, TreeSet scoreSet, HighScore newScore) {
        super(owner, true);
        setup(owner, collName, scoreSet, newScore);
    }
    
    private void setup(Window owner, String collName, TreeSet scoreSet, HighScore newScore)
    {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.scores = (HighScore [])scoreSet.toArray(new HighScore [scoreSet.size()]);
        
        setTitle(Messages.getString("high_scores")); //$NON-NLS-1$
        
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        
        cp.add(new JLabel(collName), BorderLayout.NORTH);
        
        JTable table = new JTable(new AbstractTableModel()
        {
            public String getColumnName(int col) { return colNames[col]; }
            public int getRowCount() { return scores.length; }
            public int getColumnCount() { return colNames.length; }
            public Object getValueAt(int row, int col) {
                switch(col) {
                    case 0: return String.valueOf(row + 1);
                    case 1:
                        int num = scores[row].getNumProbs();
                        int right = scores[row].getNumRight();
                        double pct = (double)right / num;
                        return format.format(pct * 100) + Messages.getString("pct_lparen")  //$NON-NLS-1$
                                + right + " " + Messages.getString("of") + " " + num  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                + Messages.getString("rparen"); //$NON-NLS-1$
                    case 2: return format.format(scores[row].getAvgTime() / 1000) + Messages.getString("seconds"); //$NON-NLS-1$
                    case 3: return df.format(scores[row].getDate());
                }
                return null;
            }
            public boolean isCellEditable(int row, int col) { return false; }
        });
        
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setMaxWidth(30);
        column = table.getColumnModel().getColumn(1);
        column.setMaxWidth(150);
        column = table.getColumnModel().getColumn(2);
        column.setMaxWidth(90);
        column = table.getColumnModel().getColumn(3);
        column.setMaxWidth(120);
        
        //  Select the new score, if present
        if(newScore != null) {
            int i;
            for(i = 0; i < scores.length; i++) {
                if(newScore.equals(scores[i]))
                    break;
            }
            ListSelectionModel lsm = table.getSelectionModel();
            lsm.setSelectionInterval(i, i);
        }
        
        JScrollPane sp = new JScrollPane(table);
        
        cp.add(sp, BorderLayout.CENTER);
        
        pack();
        setSize(400, getSize().height);
        setLocationRelativeTo(owner);
        
        format = DecimalFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        
        df = new SimpleDateFormat("MM/dd/yy kk:mmaa"); //$NON-NLS-1$
        
        setVisible(true);
    }
}
