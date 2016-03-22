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
 * Command and Control component.  Listens for entity events and
 * determines the affiliation based on the country of origin, and
 * also determines the battle dimension based on the entity type.
 *
 * @author Kari Nies
 */
public class CommandAndControl extends AbstractC2Brick{


    public CommandAndControl(Identifier id)
    {
        super(id);
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
            NamedPropertyMessage npm, received;

            received = (NamedPropertyMessage)m;
            npm = (NamedPropertyMessage)received.duplicate();

            // determine affiliation from country
            Country country = (Country)received.getParameter("country");

            if (country.equals(Country.USA))
                npm.addParameter("affiliation", Affiliation.FRIEND);
            else if (country.equals(Country.MEXICO))
                npm.addParameter("affiliation", Affiliation.NEUTRAL);
            else if (country.equals(Country.FRANCE))
                npm.addParameter("affiliation", Affiliation.FRIEND);
            else if (country.equals(Country.UNKNOWN))
                npm.addParameter("affiliation", Affiliation.UNKNOWN);
            else
                throw new RuntimeException("C&C:Unknown country.");

            // determine battle dimension from entity type
            EntityType entity = (EntityType)received.getParameter("type");

            if (entity.equals(EntityType.FIXEDWING))
                npm.addParameter("dimension", BattleDimension.AIR);
            else if (entity.equals(EntityType.FIXEDWINGBOMBER))
                npm.addParameter("dimension", BattleDimension.AIR);
            else if (entity.equals(EntityType.BATTLESHIP))
                npm.addParameter("dimension", BattleDimension.SEA);
            else if (entity.equals(EntityType.CARRIER))
                npm.addParameter("dimension", BattleDimension.SEA);
            else if (entity.equals(EntityType.TROUPS))
                npm.addParameter("dimension", BattleDimension.GROUND);
            else
                throw new RuntimeException("C&C:Unknown entity type.");

            sendNotification(npm);
        }
    }

}
