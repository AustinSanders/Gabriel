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


/**
 * Radar class.  Listens for Clock tick events and generates
 * entity events that simulate the tracking of a fixed wing
 * aircraft and a battleship across a map.
 *
 * @author Kari Nies
 */
public class Radar extends AbstractC2Brick{

    private int fwStartTick = 200;
    private double fwXStart = 20;
    private double fwYStart = 20;
    private double fwXPos = fwXStart;
    private double fwYPos = fwYStart;

    private int bsStartTick = 1000;
    private double bsXStart = 50;
    private double bsYStart = 10;
    private double bsXPos = bsXStart;
    private double bsYPos = bsYStart;

    private int fixedWingId;
    private int battleshipId;

    public Radar(Identifier id)
    {
        super(id);
        fixedWingId = 1;//EntityIdGenerator.getEntityId();
        battleshipId = 2;//EntityIdGenerator.getEntityId();
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

            NamedPropertyMessage fwnpm, bsnpm, received;
            received = (NamedPropertyMessage)m;

            if (received.getName().equals("clock")) {
                int tick;
                tick = received.getIntParameter("tick");

                if (tick % 200 == 0) {
                    if (tick >= fwStartTick) {
                        fwnpm = new NamedPropertyMessage("entity");
                        fwnpm.addParameter("tick", tick);
                        fwnpm.addParameter("type", EntityType.FIXEDWING);
                        fwnpm.addParameter("entityId", fixedWingId);
                        fwnpm.addParameter("country", Country.USA);
                        fwnpm.addParameter("locX", fwXPos);
                        fwnpm.addParameter("locY", fwYPos);
                        fwXPos += 1;
                        fwYPos += 1;
                        fwnpm.addParameter("locZ", 1.0);
                        fwnpm.addParameter("heading", -45);
                        sendNotification(fwnpm);
                    }
                }
                if (tick % 1000 == 0) {
                    if (tick >= bsStartTick) {
                        bsnpm = new NamedPropertyMessage("entity");
                        bsnpm.addParameter("tick", tick);
                        bsnpm.addParameter("type", EntityType.BATTLESHIP);
                        bsnpm.addParameter("entityId", battleshipId);
                        bsnpm.addParameter("country", Country.UNKNOWN);
                        bsnpm.addParameter("locX", bsXPos);
                        bsnpm.addParameter("locY", bsYPos);
                        bsYPos += 1;
                        bsnpm.addParameter("locZ", 1.0);
                        bsnpm.addParameter("heading", -90);
                        sendNotification(bsnpm);
                    }
                }
            }
        }
    }
}

