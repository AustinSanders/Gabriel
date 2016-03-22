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
 * Operations Artist.  Listens for operation events and sets the font
 * before forwarding the message on.
 *
 * @author Kari Nies
 */
public class OperationsArtist extends AbstractC2Brick {

    public OperationsArtist(Identifier id)
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

            if (received.getName().equals("operation")) {
                    // set the font for the graphic  
                    npm = (NamedPropertyMessage)received.duplicate();
                    npm.addParameter("tacfont", SymbolFont.OPERATIONFONT);
                    sendNotification(npm);
            } else if ((received.getName().equals("rem-operation")) ||
                       (received.getName().equals("rem-entity"))) {
                // this is cheating..... allow all remove messages
                // to filter through here.  rem-entity should really
                // be handled in the entity artists...
                sendNotification(m);
            }
        }
    }
}
