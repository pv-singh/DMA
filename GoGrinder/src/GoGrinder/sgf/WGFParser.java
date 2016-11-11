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

import GoGrinder.*;

/**
 *
 * @author  tkington
 */
public class WGFParser {
    // takes a string and returns a string pair: the first token encountered and the rest of the string
    // we have to watch for crap like AW[dg][hg] too, (is this standard Smart Game Format?)
    static StringPair grabToken(String s) throws SGFParseException {
        int i;
        s = s.trim();
        String tok = ""; //$NON-NLS-1$
        int start = 0, end = 0;
        if (s.charAt(0) == '(') {
            // gotta find matching parenthesis
            int dpth = 1;
            start = 1;
            for (i=1;i<s.length();i++) {
                if (s.charAt(i) == '[') {// find matching bracket or throw
                    int bdepth = 1;
                    while(true) {
                        if(s.charAt(i) == '[')
                            bdepth++;
                        else if(s.charAt(i) == ']') {
                            bdepth--;
                            break;
                        }
                        i++;
                    }
                }
                if (s.charAt(i) == '(') dpth++;
                if (s.charAt(i) == ')') dpth--;
                if (dpth == 0)
                    break;
            }
            if (dpth != 0)
                throw new SGFParseException(Messages.getString("illegal_token_bad_depth") + " " + s); //$NON-NLS-1$ //$NON-NLS-2$
            end = i;
        }
        else if (s.charAt(0) == ';') {
            int dpth = 0;
            start = 1;
            for (i=1;i<s.length();i++) {
                if (s.charAt(i) == '[') {// find matching bracket or throw
                    int bdepth = 0;
                    while(true) {
                        if(s.charAt(i) == '[')
                            bdepth++;
                        else if(s.charAt(i) == ']') {
                            bdepth--;
                            if(bdepth == 0)
                                break;
                        }
                        i++;
                    }
                }
                if ((s.charAt(i) == '(' || s.charAt(i) == ';'))
                    break;
            }
            if (dpth != 0)
                throw new SGFParseException(Messages.getString("illegal_sem_token") + " " + s); //$NON-NLS-1$ //$NON-NLS-2$
            end = i;
        }
        else throw new SGFParseException(Messages.getString("illegal_start_of_token") //$NON-NLS-1$
                                    + " " + s.charAt(0) + " " + Messages.getString("in") + " " + s); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        
        tok = s.substring(start, end);
        tok = tok.trim();
        s = s.substring(end, s.length());
        s = s.trim();
        if (s.length() > 0 && s.charAt(0) == ')')
            s = s.substring(1);
        return new StringPair(tok, s);
    }
    
    public static WGFNode parseNode(String sgf, WGFNode parent) throws SGFParseException {
        StringPair tok = grabToken(sgf);
        WGFNode node = new WGFNode(tok.a, parent);
        if (tok.b.length() > 0) {
            if (tok.b.charAt(0) == ';')
                node.addChild(parseNode(tok.b, node));
            else {
                while (tok.b.length() > 0) {
                    tok = grabToken(tok.b);
                    node.addChild(parseNode(tok.a, node));
                }
            }
        }
        return node;
    }
    
    private static String trimComments(String sgf) throws SGFParseException {
        int i;
        outer:
        for(i = 0; i < sgf.length(); i++) {
            if(sgf.charAt(i) == '(') {
                for(int j = i + 1; j < sgf.length(); j++) {
                    char c = sgf.charAt(j);
                    if(c == ';')
                        break outer;
                    if(!Character.isWhitespace(c))
                        continue outer;
                }
            }
        }
        if(i == sgf.length())
            throw new SGFParseException(Messages.getString("no_lparen_found")); //$NON-NLS-1$
        return sgf.substring(i);
    }
    
    private static ArrayList parse(String sgf) throws SGFParseException {
        ArrayList ret = new ArrayList();
        sgf = sgf.trim();
        sgf = trimComments(sgf);
        
        StringPair tok = grabToken(sgf);
        while(true) {
            ret.add(parseNode(tok.a, null));
            
            if(tok.b.length() == 0)
                break;
            
            tok = grabToken(tok.b);
        }
        
        return ret;
    }
    
    public static ArrayList parse(File f) throws SGFParseException, IOException {
        BufferedReader in = new BufferedReader(new FileReader(f));
        String line;
        StringBuffer sgf = new StringBuffer();
        boolean inComment = false;
        while((line = in.readLine()) != null) {
            if(inComment) {
                int n = 0;
                while(n < line.length() && line.charAt(n) == ' ')
                    n++;
                
                if(n > 0) {
                    line = line.substring(n);
                    for(int i = 0; i < n; i++) {
                        line = "&nbsp;" + line; //$NON-NLS-1$
                    }
                }
            }
            else line = line.trim();
            
            if(line.startsWith("; //")) //$NON-NLS-1$
                line = ";"; //$NON-NLS-1$
            else if(line.startsWith("( //")) //$NON-NLS-1$
                line = "("; //$NON-NLS-1$
            else if(line.startsWith(") //")) //$NON-NLS-1$
                line = ")"; //$NON-NLS-1$
            else {
                if(line.startsWith("C[") || line.startsWith("XS[")) //$NON-NLS-1$ //$NON-NLS-2$
                    inComment = true;
                if(inComment && line.endsWith("]")) //$NON-NLS-1$
                        inComment = false;
                if(inComment)
                    line += "\n"; //$NON-NLS-1$
            }
            sgf.append(line);
        }
        in.close();

        return parse(sgf.toString());
    }
    
    static class StringPair {
        String a;
        String b;
        StringPair(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }
    
    public static void main(String [] args) throws Exception {
        parse(new File("problems\\test\\contact.wgf")); //$NON-NLS-1$
        parse(new File("problems\\test\\test.wgf")); //$NON-NLS-1$
        parse(new File("problems\\test\\intro.wgf")); //$NON-NLS-1$
        parse(new File("problems\\test\\basic.wgf")); //$NON-NLS-1$
        parse(new File("problems\\test\\sector.wgf")); //$NON-NLS-1$
    }
}
