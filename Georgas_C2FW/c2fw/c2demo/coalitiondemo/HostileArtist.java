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
 * Artist for entities whose affiliation is HOSTILE.  Listens for entity 
 * events whose affiliation is HOSTILE and detemines the frame and color
 * fill to be displayed as defined in MIL-STD 2525B. 
 * The frame is determined by selecting a MAPSYMBS font based
 * on both the the affiliation and the battle dimension and choosing
 * a special fill character within that font.
 *
 * @author Kari Nies
 */
public class HostileArtist extends AbstractC2Brick {


    public HostileArtist(Identifier id)
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

            Affiliation affiliation =
                (Affiliation)received.getParameter("affiliation");
            
            // only repond to messages regarding entities whose
            // affiliation is HOSTILE
            if (affiliation.equals(Affiliation.HOSTILE)) {
                // set the color, font, and fill char of the symbol   
                npm = (NamedPropertyMessage)received.duplicate();
                
                if (received.getName().equals("entity")) {
                    npm.addParameter("symcolor", AffiliationColor.HOSTILECOLOR);
                    npm.addParameter("fillchar", SymbolChar.FILLCHAR);
                    
                    BattleDimension dimension = 
                        (BattleDimension)received.getParameter("dimension");
                    
                    if (dimension.equals(BattleDimension.AIR))
                        npm.addParameter("symfont", SymbolFont.HOSTILEAIRFONT);
                    else if (dimension.equals(BattleDimension.GROUND))
                        npm.addParameter("symfont", SymbolFont.HOSTILELANDFONT);
                    else if ((dimension.equals(BattleDimension.SEA)) 
                             || (dimension.equals(BattleDimension.SUBSURFACE)))
                        npm.addParameter("symfont", SymbolFont.HOSTILESEAFONT);
                    else
                        throw new RuntimeException
                            ("HostileArtist:Unknown battle dimension.");
                } else if (received.getName().equals("operation")) {
                    npm.addParameter("taccolor", AffiliationColor.HOSTILECOLOR);
                } 
                sendNotification(npm);   
            }
        }
    }
}
