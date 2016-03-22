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

import c2.fw.Identifier;
import c2.fw.Message;
import c2.fw.NamedPropertyMessage;
import c2.legacy.AbstractC2Brick;

/**
 * Satellite class.  Listens for Clock tick events and generates
 * entity events that simulate the tracking of an aircraft carrier
 * and troups across a map.
 *
 * @author Kari Nies
 */
public class Satellite extends AbstractC2Brick {

    private int carStartTick = 1000;
    //orig
    private double carXStart = 20;
    private double carYStart = 580;
    // mod for screenshot
    //private double carXStart = 20;
    //private double carYStart = 150;
    private double carXPos = carXStart;
    private double carYPos = carYStart;

    private int trStartTick = 1000;
    // orig
    private double trXStart = 500;
    private double trYStart = 450;
    // mod for screenshot
    //private double trXStart = 155;
    //private double trYStart = 65;
    private double trXPos = trXStart;
    private double trYPos = trYStart;

    private int carrierId;
    private int troupsId;

    public Satellite(Identifier id)
    {
        super(id);
        carrierId = 3;//EntityIdGenerator.getEntityId();
        troupsId = 4;//EntityIdGenerator.getEntityId();
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

            NamedPropertyMessage tnpm, cnpm, received;
            received = (NamedPropertyMessage)m;

            if (received.getName().equals("clock")) {
                int tick;
                tick = received.getIntParameter("tick");
                if (tick % 1000 == 0) {
                    if (tick >= trStartTick) {
                        tnpm = new NamedPropertyMessage("entity");
                        tnpm.addParameter("tick", tick);
                        tnpm.addParameter("type", EntityType.TROUPS);
                        tnpm.addParameter("entityId", troupsId);
                        tnpm.addParameter("country", Country.USA);
                        tnpm.addParameter("locX", trXPos);
                        tnpm.addParameter("locY", trYPos);
                        tnpm.addParameter("locZ", 1.0);
                        tnpm.addParameter("heading", 0);
                        sendNotification(tnpm);
                    }
                    if (tick >= carStartTick) {
                        cnpm = new NamedPropertyMessage("entity");
                        cnpm.addParameter("tick", tick);
                        cnpm.addParameter("type", EntityType.CARRIER);
                        cnpm.addParameter("entityId", carrierId);
                        cnpm.addParameter("country", Country.FRANCE);
                        cnpm.addParameter("locX", carXPos);
                        cnpm.addParameter("locY", carYPos);
                        carYPos = carYPos - 1;
                        cnpm.addParameter("locZ", 1.0);
                        cnpm.addParameter("heading", 90);
                        sendNotification(cnpm);
                    }
                }
            }
        }
    }
}
