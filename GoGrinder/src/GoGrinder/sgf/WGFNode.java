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

import java.awt.*;
import java.util.*;

import GoGrinder.*;
import GoGrinder.tests.*;
import GoGrinder.command.*;

/**
 *
 * @author  tkington
 */
public class WGFNode extends Node {
    private String nextLink;
    private String name;
    private ArrayList reasons;
    private Test test;
    private ArrayList testResults;
    private int gridType = -1;
    private boolean ignoreMoves;
    private SimpleMark lastMoveMark;
    
    /** Creates a new instance of WGFNode */
    public WGFNode(String wgf, WGFNode parent) throws SGFParseException {
        super(parent);
        
        parse(wgf);
    }
    
    public void parse(String wgf) throws SGFParseException {
    	name = null;
    	reasons = null;
    	nextLink = null;
    	test = null;
    	testResults = null;
    	gridType = -1;
    	ignoreMoves = false;
    	
    	super.parse(wgf);
    	
		try {
        	name = getProperty("N"); //$NON-NLS-1$
            
            if(comment == null)
                comment = ""; //$NON-NLS-1$

            boolean converting = !comment.startsWith("<html"); //$NON-NLS-1$
            	
            ArrayList list = (ArrayList)props.get("XS"); //$NON-NLS-1$
            if(list != null) {
                reasons = new ArrayList();
                for(int i = 0; i < list.size(); i++) {
                    String s = (String)list.get(i);
                    int index = s.indexOf(":"); //$NON-NLS-1$
                    String num = s.substring(0, index);
                    reasons.add(new Reason(Integer.parseInt(num),
                    						s.substring(index + 1),
											converting));
                }
            }
            
            if(converting)
            	processCommentAndLinks();
            else {
                nextLink = getProperty("NEXT"); //$NON-NLS-1$
                if(nextLink == null)
                	nextLink = "NEXTNODE"; //$NON-NLS-1$
            }

            list = (ArrayList)props.get("YA"); //$NON-NLS-1$
            if(list != null) 
                test = Test.create("YA", list); //$NON-NLS-1$

            list = (ArrayList)props.get("YN"); //$NON-NLS-1$
            if(list != null) 
                test = Test.create("YN", list); //$NON-NLS-1$

            list = (ArrayList)props.get("YO"); //$NON-NLS-1$
            if(list != null) 
                test = Test.create("YO", list); //$NON-NLS-1$

            list = (ArrayList)props.get("YS"); //$NON-NLS-1$
            if(list != null) 
                test = Test.create("YS", list); //$NON-NLS-1$

            list = (ArrayList)props.get("XT"); //$NON-NLS-1$
            if(list != null) {
                testResults = new ArrayList();
                for(int i = 0; i < list.size(); i++) {
                    testResults.add(new TestResult(SGFUtils.stringToPoint((String)list.get(i)), TestResult.TRI));
                }
            }

            list = (ArrayList)props.get("XU"); //$NON-NLS-1$
            if(list != null) {
                if(testResults == null)
                    testResults = new ArrayList();
                for(int i = 0; i < list.size(); i++) {
                    testResults.add(new TestResult(SGFUtils.stringToPoint((String)list.get(i)), TestResult.CIR));
                }
            }

            String xc = getProperty("XC"); //$NON-NLS-1$
            if(xc != null)
                gridType = Integer.parseInt(xc);
            
            String xi = getProperty("XI"); //$NON-NLS-1$
            if(xi != null)
                ignoreMoves = true;

            props.clear();
        }
        catch(Exception e) {
            throw new SGFParseException(e.getMessage() + " sgf=" + wgf); //$NON-NLS-1$
        }
	}

	private void processCommentAndLinks() throws SGFParseException {
		if(comment.startsWith("^")) { //$NON-NLS-1$
		    int index = comment.indexOf('\n');
		    if(index != -1)
		        comment = "<center>" + comment.substring(1, index) //$NON-NLS-1$
		                + "</center>" + comment.substring(index + 1); //$NON-NLS-1$
		}

		if(name != null && name.charAt(0) != '.')
		    comment = "<center><b>" + name + "</b></center>\n" + comment; //$NON-NLS-1$ //$NON-NLS-2$

		int index = comment.indexOf("\n^"); //$NON-NLS-1$
		while(index != -1) {
		    int index2 = comment.indexOf('\n', index + 1);
		    int index3 = index2 + 1;
		    if(index2 == -1) {
		        index3 = index2;
		        index2 = comment.length();
		    }

		    //  Add <center> tags, remove \n on either side
		    comment = comment.substring(0, index) + "<center>" //$NON-NLS-1$
		            + comment.substring(index + 2, index2) + "</center>" //$NON-NLS-1$
		            + comment.substring(index3);

		    index = comment.indexOf("\n^"); //$NON-NLS-1$
		}

		comment = comment.replaceAll("\n", "<br>\n"); //$NON-NLS-1$ //$NON-NLS-2$

		comment = "<html>\n" + comment + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$

		ArrayList list = (ArrayList)props.get("YG"); //$NON-NLS-1$
		if(list != null) {
		    for(int i = 0; i < list.size(); i++)
		        replaceLink((String)list.get(i), true);
		}

		nextLink = getProperty("YF"); //$NON-NLS-1$
		if(nextLink != null) {
		    nextLink = nextLink.toUpperCase();
		    replaceLink(nextLink, false);
		}
		else {
		    nextLink = "NEXTNODE"; //$NON-NLS-1$
		    replaceLink("NEXTNODE", false); //$NON-NLS-1$
		}
	}

	public void updateState(Board board) {
        super.updateState(board);
        
        board.setGridType(gridType);
        board.setIgnoreMoves(ignoreMoves);
        
        if(test != null)
            test.init();
        
        if(testResults != null) {
            WGFNode parent = (WGFNode)getParent();
            AllTest test = (AllTest)parent.getTest();
            
            for(int i = 0; i < testResults.size(); i++) {
                TestResult a = (TestResult)testResults.get(i);
                int type;
                boolean right = test.gotAnswer(a.p);
                
                switch(a.type) {
                    case TestResult.TRI:
                        if(right)
                            type = NodeMark.GREENTRI;
                        else type = NodeMark.REDTRI;
                        break;
                    case TestResult.CIR:
                        if(right)
                            type = NodeMark.GREENCIR;
                        else type = NodeMark.REDCIR;
                        break;
                    default:
                        throw new RuntimeException();
                }
                board.addMark(new SimpleMark(type, a.p));
            }
        }
    }
    
    public ArrayList getStepCommands(Board board) {
    	if(lastMoveMark != null) {
    		board.removeMark(lastMoveMark);
    		lastMoveMark = null;
    	}
    	
        ArrayList ret = new ArrayList();
        for(int i = 0; i < cmds.size(); i++) {
            Command c = (Command)cmds.get(i);
            if(c instanceof MoveCommand) {
                Point p = ((MoveCommand)c).getPoint();
                
                CompositeCommand comp = new CompositeCommand();
                comp.add(new RemoveCommand((MoveCommand)c));

                NodeMark mark = getMarkAt(p, false);
                if(mark != null)
                	comp.add(new RemoveMarkCommand(mark));
                
                ret.add(comp);
            }
        }
        
        if(!ret.isEmpty()) {
            CompositeCommand c = (CompositeCommand)ret.get(ret.size() - 1);
            RemoveCommand last = (RemoveCommand)c.get(0);
            if(getMarkAt(last.getPoint(), false) == null) {
	            lastMoveMark = new SimpleMark(NodeMark.CIR, last.getPoint());
	            board.addMark(lastMoveMark);
	            c.add(new RemoveMarkCommand(lastMoveMark));
            }
            return ret;
        }
        
        for(int i = 0; i < marks.size(); i++) {
            NodeMark m = (NodeMark)marks.get(i);
            int type = m.getType();
            if(type == NodeMark.LOCALB || type == NodeMark.LOCALW) {
                CompositeCommand comp = new CompositeCommand();
                comp.add(new RemoveMarkCommand(m));
                
                NodeMark mark = getMarkAt(((SimpleMark)m).getPoint(), true);
                if(mark != null)
                    comp.add(new RemoveMarkCommand(mark));
                
                ret.add(comp);
            }
        }
        
        if(!ret.isEmpty())
            return ret;
        
        return null;
    }
    
    private void replaceLink(String linkDest, boolean mustExist) throws SGFParseException {
        linkDest = linkDest.toUpperCase();
        
        int linkNum = -1;
        int index = linkDest.indexOf(':');
        if(index > 0) {
        	try {
	            linkNum = Integer.parseInt(linkDest.substring(0, index)) / 10;
	            if(linkNum > 0 && reasons != null) {
	                boolean found = false;
	                for(int i = 0; i < reasons.size(); i++) {
	                    Reason r = (Reason)reasons.get(i);
	                    if(r.getNum() == linkNum) {
	                        r.replaceLink(linkDest.substring(index));
	                        found = true;
	                        break;
	                    }
	                }
	                if(found)
	                    return;
	            }
        	}
        	catch(NumberFormatException e) { /* do nothing - must not be a numbered link */ }
        }
        
        int pos1 = comment.indexOf('_');
        int pos2 = comment.indexOf('_', pos1 + 1);
        if(pos1 != -1 || pos2 != -1) {
            String linkText = comment.substring(pos1 + 1, pos2);
            comment = comment.substring(0, pos1) + "<a href=\"" //$NON-NLS-1$
                    + linkDest + "\">" + linkText + "</a>" //$NON-NLS-1$ //$NON-NLS-2$
                    + comment.substring(pos2 + 1);
            return;
        }
        
        if(reasons != null) {
            for(int i = 0; i < reasons.size(); i++) {
                Reason r = (Reason)reasons.get(i);
                if(r.replaceLink(linkDest))
                    return;
            }
        }

        if(mustExist) {
            /*printProp("C");
            printProp("YF");
            printProp("YG");
            printProp("XS");
            System.out.println();*/

            throw new SGFParseException(Messages.getString("WGFNode.ErrorParsingLinks")); //$NON-NLS-1$
        }
    }
    
    public boolean hasAnswer() {
    	if(test == null)
    		return false;
    	return test.hasAnswer();
    }
    
    public void showAnswer(Board b) {
        test.showAnswer(b, this);
    }
    
    public void registerNode(HashMap map) {
        if(name != null)
            map.put(name.toUpperCase(), this);
        for(int i = 0; i < ch.size(); i++) {
            ((WGFNode)ch.get(i)).registerNode(map);
        }
    }
    
    public void insertNodeAfter(WGFNode node) {
    	WGFNode firstChild = null;
    	if(ch.size() > 0)
    		firstChild = (WGFNode)ch.get(0);
    	
    	if(firstChild != null) {
    		ch.remove(0);
    		node.addChild(firstChild);
    		firstChild.setParent(node);
    	}
    	
    	node.setParent(this);
    	ch.add(node);
    }
    
    public WGFNode createCopy() {
    	WGFNode node = null;
    	try {
    		node = new WGFNode("", null); //$NON-NLS-1$
    	}
    	catch(SGFParseException e) { Main.log(e); }
    	
    	node.gridType = gridType;
    	node.ignoreMoves = ignoreMoves;
    	node.size = size;
    	
    	for (Iterator iter = marks.iterator(); iter.hasNext();) {
			NodeMark m = (NodeMark) iter.next();
			node.marks.add(m.clone());
		}
    	
    	return node;
    }
    
    public void getLocalWGF(StringBuffer out) {
    	if(name != null)
    		out.append("  N[" + name + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$
    	
    	if(size != 0)
    		out.append("  SZ[" + size + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$
    	
    	if(reasons != null) {
	    	for (Iterator iter = reasons.iterator(); iter.hasNext();) {
				Reason r = (Reason) iter.next();
				r.toFileFormat(out);
			}
    	}
    	
    	SGFUtils.printMarks(marks, out, true);
    	
    	if(test != null)
    		test.toFileFormat(out);
    	
    	for (Iterator iter = cmds.iterator(); iter.hasNext();) {
			Command cmd = (Command) iter.next();
			cmd.toFileFormat(out);
		}
    	
    	if(testResults != null) {
    		Collections.sort(testResults);
    		int type = -1;
    		out.append("  "); //$NON-NLS-1$
    		for (Iterator iter = testResults.iterator(); iter.hasNext();) {
				TestResult t = (TestResult) iter.next();
				if(t.type != type) {
					if(type != -1) {
						out.append("\n  "); //$NON-NLS-1$
						switch(t.type) {
						case TestResult.CIR: out.append("XU"); break; //$NON-NLS-1$
						case TestResult.TRI: out.append("XT"); break; //$NON-NLS-1$
						}
					}
					type = t.type;
				}
				out.append("[" + SGFUtils.pointToString(t.p) + "]");  //$NON-NLS-1$//$NON-NLS-2$
			}
    		out.append("\n"); //$NON-NLS-1$
    	}
    	
    	if(gridType != -1)
    		out.append("  XC[" + gridType + "]\n");  //$NON-NLS-1$//$NON-NLS-2$
    	
    	if(ignoreMoves)
    		out.append("  XI[1]\n"); //$NON-NLS-1$
    	
    	out.append("  C[" + comment + "]\n");  //$NON-NLS-1$//$NON-NLS-2$
    	
    	if(!nextLink.equals("NEXTNODE")) //$NON-NLS-1$
    		out.append("  NEXT[" + nextLink + "]\n");  //$NON-NLS-1$//$NON-NLS-2$
    	
    	out.append("\n"); //$NON-NLS-1$
    }
    
    public void toFileFormat(StringBuffer out) {
    	out.append(";\n"); //$NON-NLS-1$
    	
    	getLocalWGF(out);
    	
        for(int i = 0; i < ch.size(); i++) {
            ((WGFNode)ch.get(i)).toFileFormat(out);
        }
    }
    
    public String getName() { return name; }
    public String getNextLink() { return nextLink; }
    public Test getTest() { return test; }
    public ArrayList getReasons() { return reasons; }
    
    private static class TestResult implements Comparable {
        public static final int TRI = 0;
        public static final int CIR = 1;
        
        Point p;
        int type;
        
        TestResult(Point p, int type) {
            this.p = p;
            this.type = type;
        }
        
        public int compareTo(Object o) {
        	TestResult r = (TestResult)o;
        	if(type < r.type)
        		return -1;
        	if(type > r.type)
        		return 1;
        	return 0;
        }
    }
}
