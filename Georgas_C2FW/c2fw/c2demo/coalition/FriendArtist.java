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

import java.awt.Color;
import java.awt.Font;
import c2.legacy.AbstractC2Brick;
import c2.fw.Identifier;
import c2.fw.Message;
import c2.fw.NamedPropertyMessage;


/**
 * Artist for entities whose affiliation is FRIEND.  Listens for entity
 * events whose affiliation is FRIEND and detemines the frame and color
 * fill to be displayed as defined in MIL-STD 2525B.
 * The frame is determined by selecting a MAPSYMBS font based
 * on both the the affiliation and the battle dimension and choosing
 * a special fill character within that font.
 *
 * @author Kari Nies
 */
public class FriendArtist extends AbstractC2Brick {

    private final Color friendColor = new Color (128, 224, 255);
    private final Character fillChar = new Character('\uF040');

    // FRIEND fonts
    private final int defaultFontSize = 24;
    private final Font friendAirFont =
        new Font("MapSym-FR-Air-APP6a", Font.PLAIN, defaultFontSize);
    private final Font friendLandFont =
        new Font("MapSym-FR-Land-APP6a", Font.PLAIN, defaultFontSize);
    private final Font friendSeaFont =
        new Font("MapSym-FR-Sea-APP6a", Font.PLAIN, defaultFontSize);


    public FriendArtist(Identifier id)
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
            // affiliation is FRIEND
            if (affiliation.equals(Affiliation.FRIEND)) {
                // set the color, font, and fill char of the symbol
                npm = (NamedPropertyMessage)received.duplicate();

                npm.addParameter("symcolor", friendColor);
                npm.addParameter("fillchar", fillChar);

                BattleDimension dimension =
                    (BattleDimension)received.getParameter("dimension");

                if (dimension.equals(BattleDimension.AIR))
                    npm.addParameter("symfont", friendAirFont);
                else if (dimension.equals(BattleDimension.GROUND))
                    npm.addParameter("symfont", friendLandFont);
                else if ((dimension.equals(BattleDimension.SEA))
                         || (dimension.equals(BattleDimension.SUBSURFACE)))
                    npm.addParameter("symfont", friendSeaFont);
                else
                    throw new RuntimeException
                         ("FriendArtist:Unknown battle dimension.");

                sendNotification(npm);
            }
        }
    }
}
