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
import java.awt.geom.*;
import java.awt.Point;
import java.util.Vector;

/**
 * Radar class.  Listens for Clock tick events and generates
 * entity events that simulate the tracking of a aircraft carrier
 * and a fixedwing aircraft (F-18).
 *
 * @author Kari Nies
 */
public class USRadar extends AbstractC2Brick {

    private int fwStartTick = 1000;
    private double fwXStart = 217;
    private double fwYStart = 338;
    private double fwXPos = fwXStart;
    private double fwYPos = fwYStart;
    private Vector flightPath;
    private boolean inFlight = false;
    private Point fromPoint;
    private Point toPoint;
    private Point curPoint;
    private int pathIndex;
    private int maxIndex;
    private int distanceIncr = 2;
    private int distance;
    private int heading;

    private int carStartTick = 1000;
    private double carXStart = 217;
    private double carYStart = 338;
    private double carXPos = carXStart;
    private double carYPos = carYStart;

    private String usRadId = new String("US-Rad");
    private String fixedWingId;
    private String carrierId;

    public USRadar(Identifier id)
    {
        super(id);
        fixedWingId = EntityIdGenerator.getEntityId(usRadId);
        carrierId = EntityIdGenerator.getEntityId(usRadId);        
    }

    public void init()
    {
    }

    public void destroy()
    {
    }

    public void begin()
    {
    }

    public void end()
    {
    }

    public void handle(Message m)
    {
        if (m instanceof NamedPropertyMessage) {

            NamedPropertyMessage fwnpm, carnpm, received;
            received = (NamedPropertyMessage)m;

            if (received.getName().equals("clock")) {
                int tick;
                tick = received.getIntParameter("tick");

                if (tick % 500 == 0) {
                    if (tick >= fwStartTick) {
                        if (inFlight) {
                            // update fwXPos and fwYPos;
                            curPoint = GraphicsHelpers.calcPointOnLineAtDist
                                (fromPoint, toPoint, distance);
                            fwXPos = curPoint.getX();
                            fwYPos = curPoint.getY();
                            fwnpm = new NamedPropertyMessage("entity");  
                            fwnpm.addParameter("tick", tick);
                            fwnpm.addParameter("type", EntityType.FIXEDWING);
                            fwnpm.addParameter("entityId", fixedWingId); 
                            fwnpm.addParameter("country", Country.USA);
                            fwnpm.addParameter("heading", heading);
                            fwnpm.addParameter("locX", fwXPos);
                            fwnpm.addParameter("locY", fwYPos);
                            fwnpm.addParameter("locZ", 1.0);
                            fwnpm.addParameter("pathIdx", pathIndex);
                            sendNotification(fwnpm); 

                            double len = GraphicsHelpers.lineLen 
                                (fwXPos, fwYPos,
                                 toPoint.getX(), toPoint.getY());
                            if (len <= distanceIncr) {
                                // start new line segment
                                fwXPos = toPoint.getX();
                                fwYPos = toPoint.getY();
                                distance = distanceIncr;
                                pathIndex++;
                                if (pathIndex <= maxIndex) {
                                    fromPoint = toPoint;
                                    toPoint = (Point)flightPath.get(pathIndex);
                                    heading = GraphicsHelpers.calcHeading
                                        (fromPoint.x, fromPoint.y,
                                         toPoint.x, toPoint.y);
                                } else {
                                    inFlight = false;
                                    // flight complete, remove fixed wing       
                                    NamedPropertyMessage removeNPM;
                                    removeNPM = new NamedPropertyMessage
                                        ("rem-entity");
                                    removeNPM.addParameter
                                        ("entityId", fixedWingId);
                                    removeNPM.addParameter
                                        ("country", Country.USA);
                                    sendNotification(removeNPM);
                                }
                            } else {
                                distance = distance + distanceIncr;
                            }
                        }
                    } 
                }
                if (tick % 1000 == 0) {
                    if (tick >= carStartTick) {
                        carnpm = new NamedPropertyMessage("entity");         
                        carnpm.addParameter("tick", tick);
                        carnpm.addParameter("type", EntityType.CARRIER);
                        carnpm.addParameter("entityId", carrierId);
                        carnpm.addParameter("country", Country.USA);
                        carnpm.addParameter("locX", carXPos);
                        carnpm.addParameter("locY", carYPos);
                        carnpm.addParameter("locZ", 1.0);
                        sendNotification(carnpm);
                    }
                }
            } else if (received.getName().equals("operation")) {
                flightPath = (Vector)received.getParameter("taccoords");
                if (flightPath.size() > 1) {
                    if (!inFlight) { // do not reset index if already in flight
                        // start flixed wing moving along given flight path
                        inFlight = true;  
                        fromPoint = (Point)flightPath.get(0);
                        toPoint = (Point)flightPath.get(1);
                        pathIndex = 0;
                        fwXPos = fromPoint.getX();
                        fwYPos = fromPoint.getY();
                        distance = distanceIncr;
                        heading =GraphicsHelpers.calcHeading
                            (fromPoint.x, fromPoint.y,
                             toPoint.x, toPoint.y);
                    }
                    maxIndex = flightPath.size()-1;                   
                } else {
                    inFlight = false;
                }
            }
        }
    }
}
    
