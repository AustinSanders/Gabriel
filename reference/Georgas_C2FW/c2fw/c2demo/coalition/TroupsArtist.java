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
 * Troups Artist.  Listens for entity events of
 * entity type TROUPS and determines the icon to be displayed
 * as defined in MIL-STD 2525B.
 * The icon is determined by selecting a character within
 * a MAPSYMBS font.
 *
 * @author Kari Nies
 */
public class TroupsArtist extends AbstractC2Brick {

    public TroupsArtist(Identifier id)
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

            if (received.getName().equals("entity")) {
                EntityType entity =
                    (EntityType)received.getParameter("type");

                // only repond to messages regarding entities whose
                // entity type is TROUPS
                if (entity.equals(EntityType.TROUPS)) {
                    // set the character for the  symbol
                    npm = (NamedPropertyMessage)received.duplicate();
                    npm.addParameter("symchar",
                                     new Character('\uF04D'));
                    sendNotification(npm);
                }
            }
        }
    }
}
