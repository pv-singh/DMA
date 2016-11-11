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

import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.geom.*;
import java.applet.*;

import GoGrinder.*;
import GoGrinder.sgf.*;

/**
 *
 * @author  tkington
 */
public class GobanPanel extends JPanel
{
    static final int NUM_WHITE_IMAGES = 12;
    static final int MIN_TILE_SIZE = 20;
    static final int MAX_TILE_SIZE = 48;
    
    public static final int EMPTY = 0;
    public static final int CROSS = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int TOP = 4;
    public static final int BOTTOM = 5;
    public static final int LL = 6;
    public static final int LR = 7;
    public static final int UL = 8;
    public static final int UR = 9;
    public static final int HOSHI = 10;
    
    static final Point [] HOSHI9 = new Point [] {new Point(2, 2), new Point(2, 6), new Point(6, 2), new Point(6, 6)};
    static final Point [] HOSHI13 = new Point [] {new Point(3, 3), new Point(3, 9), new Point(9,  3),
                                                  new Point(9, 9), new Point(6, 6)};
    static final Point [] HOSHI19 = new Point [] {new Point(3, 3), new Point(3, 9), new Point(3, 15),
                                                  new Point(9, 3), new Point(9, 9), new Point(9, 15),
                                                  new Point(15, 3), new Point(15, 9), new Point(15, 15)};
                                                  
    static final int [][] XC2 = new int[19][19];
    static final int [][] XC20 = new int[19][19];                   
    static final int [][] XC22 = new int[19][19]; 
    static final int [][] XC23 = new int[19][19]; 
    static final int [][] XC24 = new int[19][19]; 
    static final int [][] XC3 = new int[19][19];
    static final int [][] XC32 = new int[19][19];  
    static final int [][] XC40 = new int[19][19]; 
    static final int [][] XC41 = new int[19][19]; 
    static final int [][] XC42 = new int[19][19]; 
    static final int [][] XC43 = new int[19][19]; 
    static final int [][] XC44 = new int[19][19]; 
    static final int [][] XC45 = new int[19][19]; 
    static final int [][] XC6 = new int[19][19];
    static final int [][] XC60 = new int[19][19];
    
    static {
        XCSetup();
    }
    
    Image black, white[], blackGhost, whiteGhost;
    static Image blacks[], whites[][], blackGhosts[], whiteGhosts[];
    static Image kaya;
    
    static AudioClip pok;
    
    private Board board;
    private Controller controller;
    private Font font;
    
    private int tileSize;
    private int halfSize;
    
    private int ghostX = -1;
    private int ghostY = -1;
    private int ghostColor = 1;
    private boolean hideGhost = false;
    
    private boolean navMode;
    
    private int[][] whichWhite;
    private Point [] hoshi;
    
    private Rectangle boardBounds;
    private Dimension winBounds;
    private int xOffset;
    private int yOffset;
    private int stoneOff;
    
    /** Creates a new instance of GobanPanel */
    public GobanPanel(Board b)
    {
        setBoard(b);
        
        font = new Font("SansSerif", Font.BOLD, 24); //$NON-NLS-1$
        
        whichWhite = new int[19][19];
        for(int i = 0; i < 19; i++) {
            for(int j = 0; j < 19; j++) {
                whichWhite[i][j] = Main.rand.nextInt(NUM_WHITE_IMAGES);
            }
        }
        
        try {
            loadImages();
        }
        catch(IOException e) {
            JOptionPane.showMessageDialog(null, Messages.getString("err_loading_images")); //$NON-NLS-1$
            Main.logSilent(e);
            System.exit(-1);
        }
        
        setLayout(null);
        setMinimumSize(new Dimension(MIN_TILE_SIZE * 19, MIN_TILE_SIZE * 19));
        setPreferredSize(new Dimension(MIN_TILE_SIZE * 19, MIN_TILE_SIZE * 19));
        
        setSize(new Rectangle(0, 0, 19, 19), new Dimension(517, 517));
        
        addMouseListener(new MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                onClick(e);
            }
            
            public void mouseExited(MouseEvent e) {
				int x = (ghostX - boardBounds.x) * tileSize + xOffset;
				int y = (ghostY - boardBounds.y) * tileSize + yOffset;
		
				ghostX = ghostY = -1;

                repaint(0, x, y, tileSize, tileSize);
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                onMouseMoved(e);
            }
        });
        
        addMouseWheelListener(new MouseWheelListener() {
        	public void mouseWheelMoved(MouseWheelEvent e) {
        		onMouseWheelMoved(e);
        	}
        });
        
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                setSize(null, getSize());
            }
        });
    }
    
    public void setBoard(Board b) {
        board = b;
        switch(board.getSize()) {
            case 9: hoshi = HOSHI9; break;
            case 13: hoshi = HOSHI13; break;
            case 19: hoshi = HOSHI19; break;
            default: hoshi = null; break;
        }
    }
    
    public void setSize(Rectangle bBounds, Dimension wBounds) {
        if(wBounds == null)
            wBounds = winBounds;
        
        if(bBounds == null)
            bBounds = boardBounds;
        else {
            //  Leave one row of space around problem
            if(bBounds.x > 0) {
                bBounds.x--;
                bBounds.width++;
            }
            if(bBounds.width < board.getSize() - bBounds.x)
                bBounds.width++;
            if(bBounds.y > 0) {
                bBounds.y--;
                bBounds.height++;
            }
            if(bBounds.height < board.getSize() - bBounds.y)
                bBounds.height++;
        }
        
        //  If stones are near the edge, show the edge
        if(bBounds.x < 4) {
            bBounds.width += bBounds.x;
            bBounds.x = 0;
        }
        if(bBounds.x + bBounds.width > board.getSize() - 5)
            bBounds.width = board.getSize() - bBounds.x;
        if(bBounds.y < 4) {
            bBounds.height += bBounds.y;
            bBounds.y = 0;
        }
        if(bBounds.y + bBounds.height > board.getSize() - 5)
            bBounds.height = board.getSize() - bBounds.y;
        
        int bSize = Math.max(bBounds.height, bBounds.width);
        
        int pixSize = Math.min(wBounds.width, wBounds.height);
        bSize = Math.min(board.getSize(), Math.max(bSize, pixSize / MAX_TILE_SIZE));
        
        //  Adjust bounds if bSize overridden
        if(bBounds.width < bSize) {
            bBounds.x = Math.max(0, bBounds.x + bBounds.width / 2 - bSize / 2);
            bBounds.width = bSize;
            bBounds.x = Math.min(bBounds.x, board.getSize() - bBounds.width);
        }
        if(bBounds.height < bSize) {
            bBounds.y = Math.max(0, bBounds.y + bBounds.height / 2 - bSize / 2);
            bBounds.height = bSize;
            bBounds.y = Math.min(bBounds.y, board.getSize() - bBounds.height);
        }
        
        boardBounds = bBounds;
        winBounds = wBounds;
        
        tileSize = Math.min(MAX_TILE_SIZE + 5, pixSize / bSize);
        halfSize = tileSize / 2;
        
        int w = boardBounds.width * tileSize;
        int h = boardBounds.height * tileSize;
        xOffset = (wBounds.width - w) / 2;
        yOffset = (wBounds.height - h) / 2;
        
        stoneOff = Math.max(0, (tileSize - MAX_TILE_SIZE) / 2);
        
        float fontSize;
        if(tileSize > 30)
            fontSize = 24.0f;
        else if(tileSize > 25)
            fontSize = 18.0f;
        else fontSize = 14.0f;
        if(font.getSize() != fontSize)
            font = font.deriveFont(fontSize);
        
        int tileNum = Math.min(Math.max(tileSize - MIN_TILE_SIZE, 0), blacks.length - 1);
        black = blacks[tileNum];
        white = whites[tileNum];
        blackGhost = blackGhosts[tileNum];
        whiteGhost = whiteGhosts[tileNum];
        
        repaint();
    }
    
    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING ,
                                              RenderingHints.VALUE_ANTIALIAS_ON ));
        
        g.setFont(font);
        
        int w = boardBounds.width * tileSize;
        int h = boardBounds.height * tileSize;
        Dimension panelSize = getSize();
        
        g.setColor(Main.BGCOLOR);
        g.fillRect(0, 0, panelSize.width, yOffset);
        g.fillRect(0, yOffset, xOffset, panelSize.height - yOffset);
        g.fillRect(xOffset, yOffset + h, panelSize.width - xOffset, panelSize.height - h - yOffset);
        g.fillRect(xOffset + w, yOffset, panelSize.width - w - xOffset, h);
        g.setColor(Color.black);
        
        g.translate(xOffset, yOffset);
        
        g.drawImage(kaya, 0, 0, w, h, 0, 0, w, h, null);
        g.drawRect(0, 0, w, h);
                
        //  Grey squares go on before anything else
        ArrayList marks = board.getMarks();
        for(int i = 0; i < marks.size(); i++) {
            NodeMark m = (NodeMark)marks.get(i);
            if(m.getType() != NodeMark.GREYSQ)
                continue;

            Point p2 = ((SimpleMark)m).getPoint();

            int x = p2.x - boardBounds.x;
            int y = p2.y - boardBounds.y;
            g.setColor(Color.lightGray);
            g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
        }

        drawGrid(g);
        
        for(int i = 0; i < marks.size(); i++) {
            NodeMark m = (NodeMark)marks.get(i);
            if(m.getType() == NodeMark.LINE) {
                LineMark line = (LineMark)m;
                Point p1 = line.getP1();
                Point p2 = line.getP2();
                int x1 = p1.x - boardBounds.x;
                int y1 = p1.y - boardBounds.y;
                int x2 = p2.x - boardBounds.x;
                int y2 = p2.y - boardBounds.y;
                g.setColor(line.getColor());
                g2d.setStroke(line.getStroke());
                g.drawLine(x1 * tileSize + halfSize, y1 * tileSize + halfSize,
                           x2 * tileSize + halfSize, y2 * tileSize + halfSize);
            }
        }
        
        for(int i = 0; i < boardBounds.width; i++)
        {
            for(int j = 0; j < boardBounds.height; j++)
            {
                int x = i + boardBounds.x;
                int y = j + boardBounds.y;
                if(!board.isEmptyAt(x, y)) {
                    if(board.getAt(x,y) == 1)
                        g.drawImage(black, i * tileSize + stoneOff, j * tileSize + stoneOff, null);
                    else if(board.getAt(x,y) == -1)
                        g.drawImage(white[whichWhite[i][j]], i * tileSize + stoneOff, j * tileSize + stoneOff, null);
                }
            }
        }
        
        Point p = new Point();
        Color oldColor;
        g2d.setStroke(new BasicStroke(tileSize < 25 ? 2 : 3));
        for(int i = 0; i < marks.size(); i++) {
            NodeMark mk = (NodeMark)marks.get(i);
            if(mk instanceof SimpleMark) {
                SimpleMark m = (SimpleMark)mk;
                Point p2 = m.getPoint();

                if(board.isBlackAt(p2.x, p2.y))
                    g.setColor(Color.WHITE);
                else g.setColor(Color.BLACK);

                p.x = p2.x - boardBounds.x;
                p.y = p2.y - boardBounds.y;

                int x, y, size;
                switch(m.getType()) {
                    case NodeMark.CIR:
                    case NodeMark.GREENCIR:
                    case NodeMark.REDCIR:
                        oldColor = g.getColor();
                        if(m.getType() == NodeMark.GREENCIR)
                            g.setColor(Color.green);
                        else if(m.getType() == NodeMark.REDCIR)
                            g.setColor(Color.red);

                        x = (int)((p.x + 0.22) * tileSize);
                        y = (int)((p.y + 0.18) * tileSize);
                        size = (int)(tileSize * 0.6);
                        g.drawOval(x, y, size, size);

                        g.setColor(oldColor);
                        break;
                    case NodeMark.SQU:
                        Rectangle2D.Double r = new Rectangle2D.Double((p.x + 0.22) * tileSize,
                                                                       (p.y + 0.22) * tileSize,
                                                                       tileSize * 0.56, tileSize * 0.56);
                        g2d.draw(r);
                        break;
                    case NodeMark.X:
                        Line2D.Double line = new Line2D.Double((p.x + 0.22) * tileSize,
                                                               (p.y + 0.22) * tileSize,
                                                               (p.x + 0.78) * tileSize,
                                                               (p.y + 0.78) * tileSize);
                        g2d.draw(line);
                        line.setLine((p.x + 0.22) * tileSize, (p.y + 0.78) * tileSize,
                                     (p.x + 0.78) * tileSize, (p.y + 0.22) * tileSize);
                        g2d.draw(line);
                        break;
                    case NodeMark.TRI:
                    case NodeMark.GREENTRI:
                    case NodeMark.REDTRI:
                        oldColor = g.getColor();
                        if(m.getType() == NodeMark.GREENTRI)
                            g.setColor(Color.green);
                        else if(m.getType() == NodeMark.REDTRI)
                            g.setColor(Color.red);

                        int x1 = p.x * tileSize + halfSize;
                        int y1 = (int)((p.y + 0.07) * tileSize);
                        int x2 = (int)((p.x + 0.15) * tileSize);
                        int y2 = (int)((p.y + 0.72) * tileSize);
                        int x3 = (int)((p.x + 0.85) * tileSize);
                        g.drawLine(x1, y1, x2, y2);
                        g.drawLine(x2, y2, x3, y2);
                        g.drawLine(x3, y2, x1, y1);

                        g.setColor(oldColor);
                        break;
                    case NodeMark.FAKEB:
                    case NodeMark.LOCALB:
                        g.drawImage(black, p.x * tileSize + stoneOff, p.y * tileSize + stoneOff, null);
                        break;
                    case NodeMark.FAKEW:
                    case NodeMark.LOCALW:
                        g.drawImage(white[whichWhite[p.x][p.y]], p.x * tileSize + stoneOff, p.y * tileSize + stoneOff, null);
                        break;
                    case NodeMark.TERRW:
                        g.setColor(Color.white);
                    case NodeMark.TERRB:
                        x = p.x * tileSize + halfSize;
                        y = p.y * tileSize + halfSize;
                        if(tileSize < 27)
                            g.fillOval(x - 3, y - 3, 7, 7);
                        else g.fillOval(x - 5, y - 5, 11, 11);
                        break;
                    case NodeMark.GROUSE:
                        break;
                    case NodeMark.GREYSTONE:
                        oldColor = g.getColor();
                        g.setColor(Color.gray);
                        x = p.x * tileSize;
                        y = p.y * tileSize;
                        g.fillOval(x + 2, y + 2, tileSize - 4, tileSize - 4);
                        g.setColor(oldColor);
                        break;
                }
            }
        }
        
        g.setColor(Color.black);
        FontMetrics met = g.getFontMetrics();
        int dy = halfSize + met.getAscent() / 2 - (2 * tileSize / 16);
        for(int i = 0; i < marks.size(); i++) {
            NodeMark m = (NodeMark)marks.get(i);
            if(m.getType() == NodeMark.LABEL) {
                NodeLabel lab = (NodeLabel)m;
                Point p2 = lab.getPoint();

                if(board.isBlackAt(p2.x, p2.y))
                    g.setColor(Color.WHITE);
                else g.setColor(Color.BLACK);

                p.x = p2.x - boardBounds.x;
                p.y = p2.y - boardBounds.y;
                int dx = halfSize - met.stringWidth(lab.getText()) / 2;
                g.drawString(lab.getText(), p.x * tileSize + dx, p.y * tileSize + dy);
            }
        }
        
        int right = boardBounds.x + boardBounds.width;
        int bottom = boardBounds.y + boardBounds.height;
        if(ghostX >= boardBounds.x && ghostX < right &&
                ghostY >= boardBounds.y && ghostY < bottom &&
                board.getAt(ghostX,ghostY) == 0)
            g.drawImage((ghostColor == 1) ? blackGhost : whiteGhost,
                        (ghostX - boardBounds.x) * tileSize,
                        (ghostY - boardBounds.y) * tileSize,
                        null);
        
        if(navMode) {
            ArrayList good = board.getGoodMoves();
            for(int i = 0; i < good.size(); i++) {
                p.x = ((Point)good.get(i)).x - boardBounds.x;
                p.y = ((Point)good.get(i)).y - boardBounds.y;

                int x = (int)((p.x + 0.5) * tileSize - 3);
                int y = (int)((p.y + 0.5) * tileSize - 3);
                g.setColor(Color.green);
                g.fillOval(x, y, 7, 7);  
            }

            ArrayList bad = board.getBadMoves();
            for(int i = 0; i < bad.size(); i++) {
                p.x = ((Point)bad.get(i)).x - boardBounds.x;
                p.y = ((Point)bad.get(i)).y - boardBounds.y;

                int x = (int)((p.x + 0.5) * tileSize - 3);
                int y = (int)((p.y + 0.5) * tileSize - 3);
                g.setColor(Color.red);
                g.fillOval(x, y, 7, 7);  
            }
        }
    }
    
    private void drawGrid(Graphics g) {
    	int gridType = board.getGridType();
        g.setColor(Color.black);
        if(gridType == -1) {
            for(int i = 0; i < boardBounds.width; i++) {
                int left = i * tileSize;
                int right = left + tileSize - 1;

                for(int j = 0; j < boardBounds.height; j++) {
                    int bi = i + boardBounds.x;
                    int bj = j + boardBounds.y;

                    if(board.getAt(bi, bj) == 0 &&
                            board.hasLabelAt(i + boardBounds.x, j + boardBounds.y))
                        continue;

                    int x = i * tileSize + halfSize;
                    int y = j * tileSize + halfSize;
                    int top = j * tileSize;
                    int bottom = top + tileSize - 1;

                    if(bi > 0)
                        g.drawLine(left, y, x, y);
                    if(bi < board.getSize() - 1)
                        g.drawLine(x, y, right, y);
                    if(bj > 0)
                        g.drawLine(x, top, x, y);
                    if(bj < board.getSize() - 1)
                        g.drawLine(x, y, x, bottom);
                }
            }

            if(hoshi != null) {
                for(int i = 0; i < hoshi.length; i++) {
                    Point p = hoshi[i];
                    int x = p.x - boardBounds.x;
                    int y = p.y - boardBounds.y;
                    if(x >= 0 && x < boardBounds.width && y >= 0 && y < boardBounds.height) {
                        if(board.hasLabelAt(p.x, p.y))
                            continue;

                        int left = x * tileSize + halfSize - 2;
                        int top = y * tileSize + halfSize - 2;
                        g.fillRect(left, top, 5, 5);
                    }
                }
            }
        }
        else {
            int [][] grid = getGrid(gridType);
            outer:
            for(int i = 0; i < boardBounds.width; i++) {
                int left = i * tileSize;
                int right = left + tileSize - 1;

                for(int j = 0; j < boardBounds.height; j++) {
                    int bi = i + boardBounds.x;
                    int bj = j + boardBounds.y;

                    if(board.getAt(bi, bj) == 0 &&
                            board.hasLabelAt(i + boardBounds.x, j + boardBounds.y))
                        continue;

                    int x = i * tileSize + halfSize;
                    int y = j * tileSize + halfSize;
                    int top = j * tileSize;
                    int bottom = top + tileSize - 1;
                    
                    switch(grid[i][j]) {
                        case CROSS:
                            g.drawLine(left, y, right, y);
                            g.drawLine(x, top, x, bottom);
                            break;
                        case LEFT:
                            g.drawLine(x, y, right, y);
                            g.drawLine(x, top, x, bottom);
                            break;
                        case RIGHT:
                            g.drawLine(left, y, x, y);
                            g.drawLine(x, top, x, bottom);
                            break;
                        case TOP:
                            g.drawLine(left, y, right, y);
                            g.drawLine(x, y, x, bottom);
                            break;
                        case BOTTOM:
                            g.drawLine(left, y, right, y);
                            g.drawLine(x, top, x, y);
                            break;
                        case UL:
                            g.drawLine(x, y, right, y);
                            g.drawLine(x, y, x, bottom);
                            break;
                        case UR:
                            g.drawLine(left, y, x, y);
                            g.drawLine(x, y, x, bottom);
                            break;
                        case LL:
                            g.drawLine(x, y, right, y);
                            g.drawLine(x, top, x, y);
                            break;
                        case LR:
                            g.drawLine(left, y, x, y);
                            g.drawLine(x, top, x, y);
                            break;
                        case HOSHI:
                            g.drawLine(left, y, right, y);
                            g.drawLine(x, top, x, bottom);
                            g.fillRect(x - 2, y - 2, 5, 5);
                            break;
                    }
                }
            }
        }
    }
    
    private int [][] getGrid(int type) {
        switch(type) {
            case 2: return XC2;
            case 20: return XC20;
            case 22: return XC22;
            case 23: return XC23;
            case 24: return XC24;
            case 3: return XC3;
            case 32: return XC32;
            case 40: return XC40;
            case 41: return XC41;
            case 42: return XC42;
            case 43: return XC43;
            case 44: return XC44;
            case 45: return XC45;
            case 6: return XC6;
            case 60: return XC60;
        }
        return null;
    }
    
    public void onClick(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        
        if(x < 0 || x >= board.getSize() * tileSize + xOffset ||
           y < 0 || y >= board.getSize() * tileSize + yOffset )
            return;
        
        x = (x - xOffset) / tileSize + boardBounds.x;
        y = (y - yOffset) / tileSize + boardBounds.y;
        
        controller.mouseClicked(x, y, e.getModifiers());
    }
    
    public void onMouseMoved(MouseEvent e) {
        if(hideGhost || !GS.getShowGhost())
            return;
        
        int x = e.getX();
        int y = e.getY();
        
        if(ghostX != -1 &&
            (x < 0 || x >= board.getSize() * tileSize + xOffset ||
             y < 0 || y >= board.getSize() * tileSize + yOffset)) {
		    //  Clear ghost 
		    int oldGhostX = (ghostX - boardBounds.x) * tileSize + xOffset;
		    int oldGhostY = (ghostY - boardBounds.y) * tileSize + yOffset;
		    
		    ghostX = ghostY = -1;
	            
            repaint(0, oldGhostX, oldGhostY, tileSize, tileSize);

            return;
		}
        
        x = (x - xOffset) / tileSize + boardBounds.x;
        y = (y - yOffset) / tileSize + boardBounds.y;
        
        if(x != ghostX || y != ghostY) {

            int oldGhostX = (ghostX - boardBounds.x) * tileSize + xOffset;
            int oldGhostY = (ghostY - boardBounds.y) * tileSize + yOffset;

            ghostX = x;
            ghostY = y;

            //  Clear old stone
            repaint(0, oldGhostX, oldGhostY, tileSize, tileSize);

            //  Repaint new ghost stone
            repaint(0, (ghostX - boardBounds.x) * tileSize + xOffset, 
                       (ghostY - boardBounds.y) * tileSize + yOffset, 
                        tileSize, tileSize);
        }
    }
    
    public void onMouseWheelMoved(MouseWheelEvent e) {
    	controller.mouseWheelMoved(e.getWheelRotation());
    }
    
    private Image loadImage(String imagePath)
    {
        ClassLoader cl = this.getClass().getClassLoader();
        URL url = cl.getResource(imagePath);
        if (url != null)  {
            ImageIcon icon = new ImageIcon(url);
            return icon.getImage();
        }
        
        return null;
    }
    
    public void loadImages() throws IOException
    {
        //  Only do this the first time a GobanPanel is created
        if(blacks != null)
            return;
        
        MediaTracker tracker = new MediaTracker(this);
        
        int id = 0;
        
        kaya = loadImage("GoGrinder/images/Kaya.png"); //$NON-NLS-1$
        tracker.addImage(kaya, id++);
        
        int numTiles = MAX_TILE_SIZE - MIN_TILE_SIZE + 1;
        blacks = new Image[numTiles];
        whites = new Image[numTiles][NUM_WHITE_IMAGES];
        blackGhosts = new Image[numTiles];
        whiteGhosts = new Image[numTiles];
        
        for(int i = MIN_TILE_SIZE; i <= MAX_TILE_SIZE; i++) {
            int num = i - MIN_TILE_SIZE;
            blacks[num] = loadImage("GoGrinder/images/BlackStone" + i + ".png"); //$NON-NLS-1$ //$NON-NLS-2$
            tracker.addImage(blacks[num], id++);

            for(int j = 0; j < NUM_WHITE_IMAGES; j++) {
                whites[num][j] = loadImage("GoGrinder/images/WhiteStone" + i + "-" + j + ".png"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                tracker.addImage(whites[num][j], id++);
            }

            blackGhosts[num] = createImage(new FilteredImageSource(blacks[num].getSource(), new GhostFilter()));
            tracker.addImage(blackGhosts[num], id++);

            whiteGhosts[num] = createImage(new FilteredImageSource(whites[num][0].getSource(), new GhostFilter()));
            tracker.addImage(whiteGhosts[num], id++);
        }
        
        try
        {
            tracker.waitForAll();
        }
        catch(InterruptedException e)
        {
            throw new IOException(Messages.getString("err_loading_images")); //$NON-NLS-1$
        }
    }
    
    class GhostFilter extends RGBImageFilter {
        public int filterRGB(int x, int y, int rgb) {
            if (((x + y) & 1) == 1)
                rgb &= 0x00ffffff;
            return rgb;
        }
    }
    
    public void repaint(int x, int y) {
        int a = (x - boardBounds.x) * tileSize + xOffset;
        int b = (y - boardBounds.y) * tileSize + yOffset;
        repaint(a, b, tileSize, tileSize);
    }
    
    public void setController(Controller c) { controller = c; }
    public void setNavMode(boolean m) { navMode = m; repaint(); }
    public void setGhostColor(int c) { ghostColor = c; }
    public void hideGhost() { ghostX = ghostY = -1; }
    public void turnOffGhost() { hideGhost(); hideGhost = true; }
    
    private static void XCSetup() {
        int [] to = {0, 10};
        for(int k = 0; k < 2; k++) {
            XC2[0][to[k]] = UL;
            XC2[18][to[k]] = UR;
            for(int i = 1; i < 18; i++) {
                XC2[i][to[k]] = TOP;
                for(int j = 1; j < 8; j++) {
                    XC2[i][to[k] + j] = CROSS;
                }
            }
            
            for(int i = 1; i < 8; i++) {
                XC2[0][to[k] + i] = LEFT;
                XC2[18][to[k] + i] = RIGHT;
            }
            
            XC2[3][to[k] + 3] =
                XC2[9][to[k] + 3] =
                XC2[15][to[k] + 3] = HOSHI;
        }
        
        for(int k = 0; k < 2; k++) {
            XC20[to[k]][0] = UL;
            XC20[to[k]][18] = LL;
            for(int i = 1; i < 18; i++) {
                XC20[to[k]][i] = LEFT;
                for(int j = 1; j < 8; j++) {
                    XC20[to[k] + j][i] = CROSS;
                }
            }
            
            for(int i = 1; i < 8; i++) {
                XC20[to[k] + i][0] = TOP;
                XC20[to[k] + i][18] = BOTTOM;
            }
            
            XC20[to[k] + 3][3] = 
                XC20[to[k] + 3][9] =
                XC20[to[k] + 3][15] = HOSHI;
        }
        
        to[0] = 1;
        to[1] = 11;
        for(int k = 0; k < 2; k++) {
            XC22[to[k] + 7][0] = UR;
            XC22[to[k] + 7][18] = LR;
            for(int i = 1; i < 18; i++) {
                XC22[to[k] + 7][i] = RIGHT;
                for(int j = 0; j < 7; j++) {
                    XC22[to[k] + j][i] = CROSS;
                }
            }
            
            for(int i = 0; i < 7; i++) {
                XC22[to[k] + i][0] = TOP;
                XC22[to[k] + i][18] = BOTTOM;
            }
            
            XC22[to[k] + 4][3] =
                XC22[to[k] + 4][9] =
                XC22[to[k] + 4][15] = HOSHI;
        }
        
        for(int k = 0; k < 2; k++) {
            XC23[0][to[k] + 7] = LL;
            XC23[18][to[k] + 7] = LR;
            for(int i = 1; i < 18; i++) {
                XC23[i][to[k] + 7] = BOTTOM;
                for(int j = 0; j < 7; j++) {
                    XC23[i][to[k] + j] = CROSS;
                }
            }
            
            for(int i = 0; i < 7; i++) {
                XC23[0][to[k] + i] = LEFT;
                XC23[18][to[k] + i] = RIGHT;
            }
            
            XC23[3][to[k] + 4] =
                XC23[9][to[k] + 4] =
                XC23[15][to[k] + 4] = HOSHI;
        }
        
        XC24[0][0] = XC24[8][0] = UL;
        XC24[0][18] = XC24[8][18] = LL;
        for(int i = 1; i < 18; i++)
            XC24[0][i] = XC24[8][i] = LEFT;
        for(int i = 1; i < 10; i++) {
            XC24[i + 8][0] = TOP;
            XC24[i + 8][18] = BOTTOM;
            for(int j = 1; j < 18; j++) {
                XC24[i + 8][j] = CROSS;
                if(i < 7)
                    XC24[i][j] = CROSS;
            }
            if(i < 7) {
                XC24[i][0] = TOP;
                XC24[i][18] = BOTTOM;
            }
        }
        XC24[3][3] = XC24[3][9] = XC24[3][15] = HOSHI;
        XC24[11][3] = XC24[11][9] = XC24[11][15] = HOSHI;
        
        int [] tho = {0, 7, 13};
        for(int k = 0; k < 3; k++) {
            XC3[0][tho[k]] = UL;
            XC3[18][tho[k]] = UR;
            for(int i = 1; i < 18; i++) {
                XC3[i][tho[k]] = TOP;
                for(int j = 1; j < 5; j++) {
                    XC3[i][tho[k] + j] = CROSS;
                }
            }
            
            for(int i = 1; i < 5; i++) {
                XC3[0][tho[k] + i] = LEFT;
                XC3[18][tho[k] + i] = RIGHT;
            }
            
            XC3[3][tho[k] + 3] =
                XC3[9][tho[k] + 3] =
                XC3[15][tho[k] + 3] = HOSHI;
        }
        
        XC32[0][0] = XC32[0][13] = UL;
        XC32[18][0] = XC32[18][13] = UR;
        for(int i = 1; i < 18; i++) {
            if(i == 9)
                continue;
            XC32[i][0] = XC32[i][13] = TOP;
            for(int j = 1; j < 12; j++) {
                XC32[i][j] = CROSS;
                if(j < 5)
                    XC32[i][j + 13] = CROSS;
            }
        }
        for(int i = 1; i < 12; i++) {
            XC32[0][i] = LEFT;
            XC32[18][i] = RIGHT;
            if(i < 5) {
                XC32[0][i + 13] = LEFT;
                XC32[18][i + 13] = RIGHT;
            }
        }
        XC32[9][13] = TOP;
        for(int i = 1; i < 5; i++) {
            XC32[9][i + 13] = CROSS;
        }
        XC32[3][3] = XC32[15][3] = XC32[3][9] = XC32[15][9] = HOSHI;
        XC32[3][16] = XC32[9][16] = XC32[15][16] = HOSHI;
        
        int [][] fo = {{0, 0}, {10, 0}, {0, 10}, {10, 10}};
        for(int k = 0; k < 4; k++) {
            XC40[fo[k][0]][fo[k][1]] = UL;
            for(int i = 1; i < 8; i++) {
                XC40[fo[k][0] + i][fo[k][1]] = TOP;
                XC40[fo[k][0]][fo[k][1] + i] = LEFT;
                for(int j = 1; j < 8; j++) {
                    XC40[fo[k][0] + i][fo[k][1] + j] = CROSS;
                }
            }
            XC40[fo[k][0] + 3][fo[k][1] + 3] = HOSHI;
        }
        
        fo[0][0] = fo[2][0] = 1;
        for(int k = 0; k < 4; k++) {
            for(int i = 0; i < 8; i++) {
                XC41[fo[k][0] + i][fo[k][1]] = TOP;
                for(int j = 1; j < 8; j++) {
                    XC41[fo[k][0] + i][fo[k][1] + j] = CROSS;
                }
            }
        }
        
        fo[1][0] = fo[3][0] = 11;
        for(int k = 0; k < 4; k++) {
            XC42[fo[k][0] + 7][fo[k][1]] = UR;
            for(int i = 0; i < 7; i++) {
                XC42[fo[k][0] + i][fo[k][1]] = TOP;
                XC42[fo[k][0] + 7][fo[k][1] + i + 1] = RIGHT;
                for(int j = 1; j < 8; j++) {
                    XC42[fo[k][0] + i][fo[k][1] + j] = CROSS;
                }
            }
            XC42[fo[k][0] + 4][fo[k][1] + 3] = HOSHI;
        }
        
        fo[0][1] = fo[1][1] = 1;
        for(int k = 0; k < 4; k++) {
            for(int i = 0; i < 7; i++) {
                for(int j = 0; j < 8; j++) {
                    XC43[fo[k][0] + i][fo[k][1] + j] = CROSS;
                }
            }
            
            for(int i = 0; i < 8; i++) {
                XC43[fo[k][0] + 7][fo[k][1] + i] = RIGHT;
            }
        }
        
        fo[2][1] = fo[3][1] = 11;
        for(int k = 0; k < 4; k++) {
            XC44[fo[k][0] + 7][fo[k][1] + 7] = LR;
            for(int i = 0; i < 7; i++) {
                XC44[fo[k][0] + i][fo[k][1] + 7] = BOTTOM;
                XC44[fo[k][0] + 7][fo[k][1] + i] = RIGHT;
                for(int j = 0; j < 7; j++) {
                    XC44[fo[k][0] + i][fo[k][1] + j] = CROSS;
                }
            }
            XC44[fo[k][0] + 4][fo[k][1] + 4] = HOSHI;
        }
        
        fo[1][0] = fo[3][0] = 10;
        for(int k = 0; k < 4; k++) {
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 7; j++) {
                    XC45[fo[k][0] + i][fo[k][1] + j] = CROSS;
                }
                XC45[fo[k][0] + i][fo[k][1] + 7] = BOTTOM;
            }
        }
        
        int [][] so = {{1, 0}, {7, 0}, {13, 0}, {1, 10}, {7, 10}, {13, 10}};
        for(int k = 0; k < 6; k++) {
            for(int i = 0; i < 5; i++) {
                XC60[so[k][0] + i][so[k][1]] = TOP;
                for(int j = 1; j < 8; j++) {
                    XC60[so[k][0] + i][so[k][1] + j] = CROSS;
                }
            }
        }
        
        so[0][0] = so[3][0] = 0;
        for(int k = 0; k < 6; k++) {
            XC6[so[k][0]][so[k][1]] = UL;
            for(int i = 1; i < 5; i++) {
                XC6[so[k][0] + i][so[k][1]] = TOP;
                for(int j = 1; j < 8; j++) {
                    XC6[so[k][0] + i][so[k][1] + j] = CROSS;
                }
            }
            
            for(int j = 1; j < 8; j++) {
                XC6[so[k][0]][so[k][1] + j] = LEFT;
            }
            
            XC6[so[k][0] + 3][so[k][1] + 3] = HOSHI;
        }
    }
}
