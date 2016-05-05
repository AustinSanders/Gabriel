/*
 * Copyright (c) 2001-2002 Regents of the University of California.
 * All rights reserved.
 *
 * This software was developed at the University of California, Irvine.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by the University of California, Irvine.  The name of the
 * University may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package c2demo.coalitiondemo;

import c2.legacy.AbstractC2Brick;
import c2.fw.Identifier;
import c2.fw.Message;
import c2.fw.NamedPropertyMessage;

import java.io.InputStream;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.MediaTracker;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * This component is responsible to rendering and updating all of 
 * the graphical entities for this demo including the topological
 * maps and the symbols for the entities that are being tracked.
 * This version has been adapted from Justin Erenkrantz's original
 * implementaion.
 *
 * @author Kari Nies
 * @author Justin Erenkrantz
 */
public class VirtualScreen extends AbstractC2Brick {

    private static final int defaultFontSize = 26;
    private static final int opFontSize = 28;

    /**
     * This class encompasses all of the data necessary to render
     * a graphical symbol representing a warfighting entity.
     */
    class SymbolGraphic {
        public EntityType entity;
        public Affiliation affiliation;
        public BattleDimension dimension;
        public int heading;
        public double x, y;
        public SymbolFont symbolFont;
        public Font font;
        public Color color;
        public char fillChar, symChar;
        // Indicates whether or not an entity is currently moving.
        // Used to determine whether or not to draw a heading indicator.
        public boolean moving;
        // Offset character so that it is roughly centered on x,y
        public int offsetX, offsetY;

        public SymbolGraphic (NamedPropertyMessage npm,
                              Graphics g) {
            entity = (EntityType)npm.getParameter("type");
            affiliation = (Affiliation)npm.getParameter("affiliation");
            dimension = (BattleDimension)npm.getParameter("dimension");
            if (npm.hasParameter("heading")) {
                heading = npm.getIntParameter("heading");
            } else {
                heading = 0;
            }
            x = npm.getDoubleParameter("locX");
            y = npm.getDoubleParameter("locY");
            symbolFont = (SymbolFont)npm.getParameter("symfont");
            color = ((AffiliationColor)npm.getParameter("symcolor")).getColor();
            fillChar = ((SymbolChar)npm.getParameter("fillchar")).getChar().charValue();
            symChar = ((SymbolChar)npm.getParameter("symchar")).getChar().charValue();
            moving = false;

            font = symbolFont.getFont(defaultFontSize);
            Point p = GraphicsHelpers.getCenterOffset(g, font, symChar);
            offsetX = -p.x;
            offsetY = p.y;
        }

    }


    /**
     * This class encompasses all of the data necessary to render
     * a tactical graphic representing a military operation.
     */
    class TacticalGraphic {
        public OperationType operation;
        public Affiliation affiliation;
        public Font font;
        public Color color;
        public Vector coords; 
        public int offsetX, offsetY;

        public TacticalGraphic (NamedPropertyMessage npm,
                                Graphics g) {
            operation = (OperationType)npm.getParameter("type");
            affiliation = (Affiliation)npm.getParameter("affiliation");
            font =
                ((SymbolFont)npm.getParameter("tacfont")).getFont(opFontSize);
            color = ((AffiliationColor)npm.getParameter("taccolor")).getColor();
            coords = (Vector)npm.getParameter("taccoords");
            Point p = GraphicsHelpers.getCenterOffset(g, font, '0');
            offsetX = -p.x;
            offsetY = p.y;
        }
    }


    /**
     * This class encompasses all of the data necessary to render
     * a single tile of the topological map.
     */
    class MapGraphic {
        public Image image;
        public int x, y;

        public MapGraphic(Image image, int x, int y) {
            this.image = image;
            this.x = x;
            this.y = y;
        }
    }


    /**
     * This class keeps track of and paints all graphical entities.
     */
    class GraphicPanel extends JPanel {

        //** a list of map tiles
        private List maps;
        //** a mapping of enity ids to symbol graphics
        private Map symbolGraphics;
        //** a mapping of operation ids to tactical graphics
        private Map tacticalGraphics;

        private MediaTracker mt;
        private int mapCount;

        /**
         * constructor
         */
        public GraphicPanel() {
            super();

            maps = new Vector();
            symbolGraphics = new HashMap();
            tacticalGraphics = new HashMap();
            mt = new MediaTracker(this);

            mapCount = 0;

            addMouseListener(new MouseInputAdapter(){
                    public void mouseClicked(MouseEvent e){
                        System.out.println("(" + e.getX() + "," + 
                                           e.getY() + ")");
                    }
                });
        }

        /**
         * Adds a new map image to the list of map tiles.
         */
        public void addMap(Image map, double x, double y) {
            MapGraphic mg;

            try {
                mt.addImage(map, mapCount);
                mt.waitForID(mapCount++);
            }
            catch (Exception e)
            {   e.printStackTrace();    }

            mg = new MapGraphic(map, (int)x, (int)y);
            maps.add(mg);
        }

        /**
         * Given an entity event message, checks if a graphical symbol
         * has already been created for the entity.  If it exisits, then
         * its position and heading is updated, otherwise a new graphical 
         * symbol is created to represent it.
         */
        public void updateSymbolGraphic(NamedPropertyMessage npm) {
            String entityId = (String)npm.getParameter("entityId");
            SymbolGraphic sg = (SymbolGraphic)symbolGraphics.get(entityId);
            if (sg == null) {
                // symbol does not already exist, create and add it
                sg = new SymbolGraphic(npm, super.getGraphics());
                symbolGraphics.put(entityId,sg);
            } else {
                // symbol exists, update its position and heading and 
                // mark whether or not it is moving
                double newX = npm.getDoubleParameter("locX");
                double newY = npm.getDoubleParameter("locY");
                // if given a heading, use it.  otherwise calc it.
                if (npm.hasParameter("heading")) {
                    sg.heading = npm.getIntParameter("heading");
                } else {
                    sg.heading = 
                        GraphicsHelpers.calcHeading(sg.x, sg.y, newX, newY);
                }
                if ((sg.x != newX) || (sg.y != newY)){
                    sg.x = newX;
                    sg.y = newY;
                    sg.moving = true;
                } else {
                    sg.moving = false;
                }
            }
        }

        /**
         * Given an operation event message, checks if a tactical graphic
         * has already been created for the operation.  If it exisits, then
         * its position is updated, otherwise a new tactical graphic is
         * created to represent it.
         */
        public void updateTacticalGraphic(NamedPropertyMessage npm) {
            String operationId = (String)npm.getParameter("operationId");
            TacticalGraphic tg = 
                (TacticalGraphic)tacticalGraphics.get(operationId);
            if (tg == null) {
                // symbol does not already exist, create and add it
                tg = new TacticalGraphic(npm, super.getGraphics());
                // oldsg will be null
                TacticalGraphic oldsg = 
                    (TacticalGraphic)tacticalGraphics.put(operationId, tg);
            } else {
                // tactical graphic exists, update its position coordinates
                Vector newCoords = (Vector)npm.getParameter("taccoords");
                tg.coords = newCoords;
            }
        }

        public void removeSymbolGraphic(NamedPropertyMessage npm) {
            String entityId = (String)npm.getParameter("entityId");
            symbolGraphics.remove(entityId);
        }

        public void removeTacticalGraphic(NamedPropertyMessage npm) {
            String operationId = (String)npm.getParameter("operationId");
            tacticalGraphics.remove(operationId);
        }


        /**
         * Paints all of the graphical objects (maps and symbols).
         */
        public void paintComponent(Graphics g) {
            //System.out.println("starting paintComponent.");    
            super.paintComponent(g);
            
            synchronized (maps) { 
                // draw map
                ListIterator mi = maps.listIterator();
                MapGraphic mg;
                while (mi != null && mi.hasNext()) {
                    mg = (MapGraphic)mi.next();
                    g.drawImage(mg.image, mg.x, mg.y, this);
                }
            }
            // draw symbol graphics
            Iterator sgi = (symbolGraphics.values()).iterator();
            SymbolGraphic sg;
            if (sgi != null) {
                while (sgi.hasNext()) {
                    ImageIcon ii;
                    sg = (SymbolGraphic)sgi.next();

                    g.setFont(sg.font);
                    g.setColor(sg.color);
                    g.drawString("" + sg.fillChar,
                                 (int)sg.x+sg.offsetX,
                                 (int)sg.y+sg.offsetY);
                    g.setColor(Color.black);
                    g.drawString("" + sg.symChar,
                                 (int)sg.x+sg.offsetX,
                                 (int)sg.y+sg.offsetY);

                    if (sg.moving) {
                        // draw heading
                        // x,y should roughly be the center of the character
                        int startx = (int)sg.x;
                        int starty = (int)sg.y+2; // slight correction
                        int headingLength = sg.offsetY*2;
                        Point p = GraphicsHelpers.getHeadingEndPoint
                            (startx, starty, sg.heading, headingLength);
                        g.drawLine(startx, starty, p.x, p.y);
                    }
                }
            }
            // draw tractical graphics
            Iterator tgi = (tacticalGraphics.values()).iterator();
            TacticalGraphic tg;
            if (tgi != null) {
                while (tgi.hasNext()) {
                    tg = (TacticalGraphic)tgi.next();
                    if (tg.operation == OperationType.PREDIMPACT){
                        // draw Predicted Impact Point
                        g.setFont(tg.font);
                        g.setColor(tg.color);
                        Point point = (Point)tg.coords.get(0);
                        g.drawString("*", (int)point.getX()+tg.offsetX, 
                                     (int)point.getY()+tg.offsetY);
                    } else if (tg.operation == OperationType.IMPACT){
                        // draw Predicted Impact Point
                        g.setFont(tg.font);
                        g.setColor(Color.black);
                        Point point = (Point)tg.coords.get(0);
                        g.drawString(".", (int)point.getX()+tg.offsetX, 
                                     (int)point.getY()+tg.offsetY);
                    } else if (tg.operation == OperationType.FLIGHTPATH) {
                        // draw Flight Path
                        Point offsetPoint = GraphicsHelpers.getCenterOffset
                            (g, tg.font, '0');
                        int acpRadius = (int)offsetPoint.x; //aproximate...
                        g.setFont(tg.font);
                        g.setColor(tg.color);
                        Point point = new Point(0,0);
                        Point prevPoint;
                        int pathLen = tg.coords.size();
                       
                        for (int i = 0; i < pathLen; i++) {
                            // draw each segment of the flight path
                            prevPoint = point;
                            point = (Point)tg.coords.get(i);
                            if (i > 0) {
                                // draw line, shave some pixels off each end
                                // so that the lines do not go through ACPs
                                int len = (int)GraphicsHelpers.lineLen
                                    (prevPoint,point);
                                Point p1, p2;
                                p1 = GraphicsHelpers.calcPointOnLineAtDist
                                    (prevPoint, point, acpRadius);
                                p2 = GraphicsHelpers.calcPointOnLineAtDist
                                    (prevPoint, point, len-acpRadius);
                                g.drawLine(p1.x, p1.y, p2.x, p2.y);
                                // do not draw last ACP if last point is
                                // same as the first
                                String s = Integer.toString(i);
                                if (i == pathLen-1) {
                                    if (!point.equals
                                        ((Point)tg.coords.get(0))){
                                        // convert i to a String, draw ACP
                                        g.drawString(s, point.x+tg.offsetX, 
                                                     point.y+tg.offsetY);
                                    }
                                } else {
                                    g.drawString(s, point.x+tg.offsetX, 
                                                 point.y+tg.offsetY);
                                }
                            }
                        }
                    }
                }
            }
        }  
    }


    public String country;

    private JFrame jf;
    private GraphicPanel gp;

    private double startX, startY, countX, countY;

    public VirtualScreen(Identifier id,
                         String country) {
        super(id);
        this.country = country;
    }

    public VirtualScreen(Identifier id,
                         c2.fw.InitializationParameter[] initParams) {
        super(id);
        this.country = initParams[0].getValue();
    }

    public void init() {
        System.out.println("Virtual Screen Starting!");
        jf = new JFrame(country + " Virtual Screen");
        jf.setSize(600, 600);

        gp = new GraphicPanel();
        gp.setBackground(Color.white);
        jf.getContentPane().add(gp);        
    }

    public void destroy() {
        System.out.println("Virtual Screen Dying!");
    }

    public void begin() {
        System.out.println("Virtual Screen Beginning!");
        startX = 95.0;
        startY = 58.0;
        countX = 3.0; // number of columns
        countY = 3.0; // number of rows
        for (double i = startX; i < startX + countX; i++)
        {
            for (double j = startY; j > startY - countY; j--)
            {
                // request a URL for each map tile
                NamedPropertyMessage npm;
                npm = new NamedPropertyMessage("map");
                npm.addParameter("action", "request");
                npm.addParameter("longitude", i);
                npm.addParameter("latitude", j);
                npm.addParameter("size", 200.0);
                sendRequest(npm);
            }
        }
       jf.setVisible(true);
    }

    public void end() {
        System.out.println("Virtual Screen Ending!");
        jf.setVisible(false);
    }

    public void handle(Message m) {
        //System.out.println("Virtual Screen received a message!");
        //System.out.println(m);
        if (m instanceof NamedPropertyMessage) {
            NamedPropertyMessage npm;
            npm = (NamedPropertyMessage)m;

            if (npm.getName().equals("map")) {
                double locX, locY, posX, posY, height, width;
                Object o;
                Image map = null;

                locX = npm.getDoubleParameter("longitude"); 
                locY = npm.getDoubleParameter("latitude"); 
                height = npm.getDoubleParameter("height");
                width =  npm.getDoubleParameter("width");

                if (npm.hasParameter("imageURL"))
                {
                    o = npm.getParameter("imageURL");
                    if (o instanceof String)
                    {
                        try
                        {
                            URL u = new URL((String)o);
                            map = Toolkit.getDefaultToolkit().createImage(u);
                        }
                        catch (MalformedURLException mue)
                        {
                            mue.printStackTrace();
                        }
                    }
                }
                if (npm.hasParameter("imageResource"))
                {
                    o = npm.getParameter("imageResource");
                    if (o instanceof String) {
                        String s = (String)o;
                        InputStream is =
                            VirtualScreen.class.getResourceAsStream(s);
                        if (is == null) {
                            is = VirtualScreen.class.getClassLoader().
                                getSystemResourceAsStream(s);
                        }

                        try {
                            map = ImageIO.read(is);
                        }
                        catch (java.io.IOException ie) {
                            ie.printStackTrace();
                        }
                    }
                }

                posX = locX - startX;
                posY = startY - locY;

                if (map != null) {
                    gp.addMap(map, posX * width, posY * height);
                }
            }

            else if (npm.getName().equals("entity")) {
                gp.updateSymbolGraphic(npm);
                jf.update(jf.getGraphics());
            }
            
            else if (npm.getName().equals("operation")) {
                gp.updateTacticalGraphic(npm);
                jf.update(jf.getGraphics());
            }
            else if (npm.getName().equals("rem-entity")) {
                gp.removeSymbolGraphic(npm);
                jf.update(jf.getGraphics());
            }
            
            else if (npm.getName().equals("rem-operation")) {
                gp.removeTacticalGraphic(npm);
                jf.update(jf.getGraphics());
            }
        }
    }
}
