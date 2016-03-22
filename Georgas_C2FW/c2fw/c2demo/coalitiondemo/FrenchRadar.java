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

/**
 * French Radar class.  Listens for Clock tick events and generates
 * entity events that simulate the tracking of an French AWACS aircraft.
 *
 * @author Kari Nies
 */
public class FrenchRadar extends AbstractC2Brick {

    private int awacsStartTick = 200;
    private Ellipse2D ellipse = new Ellipse2D.Double
        (500.0, 275.0, 50.0, 75.0);
    private PathIterator pi = ellipse.getPathIterator(new AffineTransform());
    private double flattening = 0.001;
    private FlatteningPathIterator fpi = new FlatteningPathIterator
        (pi, flattening);
    private double[] coord = new double[6];
    private double awacsXPos = 0.0;
    private double awacsYPos = 0.0;
    private int pathSegType;  

    private int batStartTick = 1000;
    private double batXStart = 492;
    private double batYStart = 398;
    private double batXPos = batXStart;
    private double batYPos = batYStart;

    private String frRadId = new String("Fr-Rad");
    private String awacsId;
    private String battleshipId;

    public FrenchRadar(Identifier id)
    {
        super(id);
        awacsId = EntityIdGenerator.getEntityId(frRadId);
        battleshipId = EntityIdGenerator.getEntityId(frRadId);
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

            NamedPropertyMessage awacsnpm, batnpm, received;
            received = (NamedPropertyMessage)m;
                
            if (received.getName().equals("clock")) {
                int tick;
                tick = received.getIntParameter("tick");
                if (tick % 200 == 0) {          
                    if (tick >= awacsStartTick) {
                        if (fpi.isDone()) {
                            pi = ellipse.getPathIterator
                                (new AffineTransform());
                            fpi = new FlatteningPathIterator(pi, flattening);
                        }
                        pathSegType = fpi.currentSegment(coord);
                        awacsXPos = coord[0];
                        awacsYPos = coord[1];
                        awacsnpm = new NamedPropertyMessage("entity");  
                        fpi.next();
                        awacsnpm.addParameter("type", EntityType.AWACS);
                        awacsnpm.addParameter("entityId", awacsId); 
                        awacsnpm.addParameter("country", Country.FRANCE);
                        awacsnpm.addParameter("locX", awacsXPos);
                        awacsnpm.addParameter("locY", awacsYPos);
                        awacsnpm.addParameter("locZ", 1.0);
                        sendNotification(awacsnpm);
                    } 
                }
                if (tick % 1000 == 0) {
                    if (tick >= batStartTick) {
                        batnpm = new NamedPropertyMessage("entity");         
                        batnpm.addParameter("tick", tick);
                        batnpm.addParameter("type", EntityType.BATTLESHIP);
                        batnpm.addParameter("entityId", battleshipId);
                        batnpm.addParameter("country", Country.FRANCE);
                        batnpm.addParameter("locX", batXPos);
                        batnpm.addParameter("locY", batYPos);
                        batnpm.addParameter("locZ", 1.0);
                        sendNotification(batnpm);
                    }
                }
            }
        }
    }
}
