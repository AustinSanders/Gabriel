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

/**
 * Frech AWACS class.  Listens for Clock tick events and generates
 * entity events that simulate the tracking of French troups and
 * a Lebonese air defense missile.
 *
 * @author Kari Nies
 */
public class FrenchAWACS extends AbstractC2Brick {

    private int trStartTick = 1000;
    private double trXStart = 533;
    private double trYStart = 307;
    private double trXPos = trXStart;
    private double trYPos = trYStart;

    private int admStartTick = 1000;
    private double admXStart = 529;
    private double admYStart = 329;
    private double admXPos = admXStart;
    private double admYPos = admYStart;

    private String frAWACSId = new String("Fr-AWACS");
    private String troupsId;
    private String admissileId;

    public FrenchAWACS(Identifier id)
    {
        super(id);
        troupsId = EntityIdGenerator.getEntityId(frAWACSId);
        admissileId = EntityIdGenerator.getEntityId(frAWACSId);
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
                        sendNotification(tnpm);
                    } 
                    if (tick >= admStartTick) {
                        tnpm = new NamedPropertyMessage("entity");          
                        tnpm.addParameter("tick", tick);
                        tnpm.addParameter("type", EntityType.AIRDEFENSEMISSILE);
                        tnpm.addParameter("entityId", admissileId);
                        tnpm.addParameter("country", Country.LEBANON);
                        tnpm.addParameter("locX", admXPos);
                        tnpm.addParameter("locY", admYPos);
                        tnpm.addParameter("locZ", 1.0);
                        sendNotification(tnpm);
                    } 
                }
            }
        }
    }
}
