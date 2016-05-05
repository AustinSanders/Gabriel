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
package c2demo.coalition;

import c2.legacy.AbstractC2Brick;
import c2.fw.Identifier;
import c2.fw.Message;
import c2.fw.NamedPropertyMessage;


import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.MediaTracker;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This component is responsible to rendering and updating all of
 * the graphical entities for this demo including the topological
 * maps and the symbols for the entities that are being tracked.
 * This version has been adapted from Jusin Erenkrantz's original
 * implementaion.
 *
 * @author Kari Nies
 * @author Justin Erenkrantz
 */
public class VirtualScreen extends AbstractC2Brick {

    /**
	 * This class encompasses all of the data necessary to render
	 * a graphical symbol representing a warfighting entity.
	 */
    class SymbolGraphic {
        public EntityType entity;
        public Affiliation affiliation;
        public BattleDimension dimension;
        public int heading;
        public int x, y;
        public Font font;
        public Color color;
        public char fillChar, symChar;
        //** Indicates whether or not an entity is currently moving.
        //** Used to determine whether or not to draw a heading indicator.
        public boolean moving;

        public SymbolGraphic (EntityType entity,
                              Affiliation affiliation,
                              BattleDimension dimension,
                              int heading,
                              int x,
                              int y,
                              Font font,
                              Color color,
                              char fillChar,
                              char symChar,
                              boolean moving) {
            this.entity = entity;
            this.affiliation = affiliation;
            this.dimension = dimension;
            this.heading = heading;
            this.x = x;
            this.y = y;
            this.font = font;
            this.color = color;
            this.fillChar = fillChar;
            this.symChar = symChar;
            this.moving = moving;
        }

        public SymbolGraphic (NamedPropertyMessage npm) {
            entity = (EntityType)npm.getParameter("type");
            affiliation = (Affiliation)npm.getParameter("affiliation");
            dimension = (BattleDimension)npm.getParameter("dimension");
            heading = npm.getIntParameter("heading");
            x = (int)npm.getDoubleParameter("locX");
            y = (int)npm.getDoubleParameter("locY");
            font = (Font)npm.getParameter("symfont");
            color = (Color)npm.getParameter("symcolor");
            fillChar = ((Character)npm.getParameter("fillchar")).charValue();
            symChar = ((Character)npm.getParameter("symchar")).charValue();
            moving = false;
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
        private Map symbols;
        private MediaTracker mt;
        private int mapCount;

        /**
		 * constructor
		 */
        public GraphicPanel() {
            super();

            maps = new Vector();
            symbols = new HashMap();
            mt = new MediaTracker(this);

            mapCount = 0;
        }

        /**
		 * Adds a new map image to the list of map tiles.
		 */
        public void addImage(URL url, double x, double y) {
            MapGraphic mg;
            Image map;

            map = Toolkit.getDefaultToolkit().createImage(url);

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
		 * its position is updated, otherwise a new graphical symbol is
		 * created to represent it.
		 */
        public void updateSymbol(NamedPropertyMessage npm) {
            int entityId = npm.getIntParameter("entityId");
            Integer i = new Integer(entityId);
            SymbolGraphic sg = (SymbolGraphic)symbols.get(i);
            if (sg == null) {
                // symbol does not already exist, create and add it
                sg = new SymbolGraphic(npm);
                // oldsg will be null
                SymbolGraphic oldsg = (SymbolGraphic)symbols.put(i,sg);
            } else {
                // symbol exists, update its position and
                // add mark whether or not it is moving
                double newX = npm.getDoubleParameter("locX");
                double newY = npm.getDoubleParameter("locY");
                if ((sg.x != newX) || (sg.y != newY)){
                    sg.x = (int)newX;
                    sg.y = (int)newY;
                    sg.moving = true;
                } else {
                    sg.moving = false;
                }
            }
        }

        /**
		 * Returns the x,y coordinates of the end point of line starting
		 * at startx, starty in the direction of the given heading and of
		 * the given length.
		 */
        private Point getHeadingEndPoint (int startx, int starty,
                                          int heading, int length) {
            int x, y;
            x = startx + (int)(Math.cos(Math.toRadians(heading))* length);
            y = starty - (int)(Math.sin(Math.toRadians(heading))* length);
            return new Point(x,y);
        }

        /**
		 * Paints all of the graphical objects (maps and symbols).
		 */
        public synchronized void paintComponent(Graphics g) {
            //System.out.println("starting paintComponent.");
            super.paintComponent(g);

            ListIterator mi = maps.listIterator();
            MapGraphic mg;
            while (mi != null && mi.hasNext()) {
                mg = (MapGraphic)mi.next();
                g.drawImage(mg.image, mg.x, mg.y, this);
            }

            Iterator si = (symbols.values()).iterator();
            SymbolGraphic sg;
            while (si != null && si.hasNext()) {
                sg = (SymbolGraphic)si.next();
                //System.out.println("Painting a " + sg.entity.toString());
                g.setFont(sg.font);
                g.setColor(sg.color);
                g.drawString("" + sg.fillChar, sg.x, sg.y);
                g.setColor(Color.black);
                g.drawString("" + sg.symChar, sg.x, sg.y);

                if (sg.moving) {
                    // draw heading
                    FontMetrics fm = g.getFontMetrics(sg.font);
                    int startx = sg.x + (fm.charWidth(sg.symChar))/2;
                    int starty = (sg.y - (fm.getAscent())/2)+2;
                    int headingLength = fm.getAscent();
                    Point p = getHeadingEndPoint(startx, starty,
                                                 sg.heading, headingLength);
                    g.drawLine(startx, starty, p.x, p.y);
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

	public VirtualScreen(Identifier id){
		super(id);
		System.err.println("****Something bad happened.");
	}
	
	public VirtualScreen(Identifier id,
		c2.fw.InitializationParameter[] initParams) {
		super(id);
		this.country = initParams[0].getValue();
		//for (int i=0; i<initParams.length; i++){
			//this.country = initParams[i].getvalue();
		//}
		System.out.println("initialization parameter is :" + initParams[0].getValue());
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
        startX = 25.0;
        startY = 58.0;
        countX = 3.0;
        countY = 3.0;
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
                URL u;

                locX = npm.getDoubleParameter("longitude");
                locY = npm.getDoubleParameter("latitude");
                height = npm.getDoubleParameter("height");
                width =  npm.getDoubleParameter("width");
                o = npm.getParameter("imageURL");
                if (o instanceof String)
                {
                    try
                    {   u = new URL((String)o); }
                    catch (MalformedURLException mue)
                    {
                        mue.printStackTrace();
                        u = null;
                    }
                }
                else
                    throw new IllegalArgumentException();

                /* FIXME: Assuming United States area... */
                posX = locX - startX;
                posY = startY - locY;

                gp.addImage(u, posX * width, posY * height);
            }

            else if (npm.getName().equals("entity")) {
                gp.updateSymbol(npm);
                jf.update(jf.getGraphics());
            }
        }
    }
}
