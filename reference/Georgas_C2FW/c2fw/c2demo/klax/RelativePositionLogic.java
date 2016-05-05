/*
 * Copyright (c) 2000 Regents of the University of California.
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

package c2demo.klax;

import c2.fw.*;
import c2.legacy.*;
import java.io.*;

public class RelativePositionLogic extends c2.legacy.AbstractC2Brick
{
   protected int lastKnownPalettePos; // 1..5
   protected KlaxSelectorTable kst;

    // category: constructor
    public RelativePositionLogic( String _name )
    {
        super( new SimpleIdentifier(_name) );
        create( _name );
    }


    public RelativePositionLogic( Identifier id )
    {
        super(id);
        create( id.toString() );
    }


    private void create( String _name )
    {
        //super.create(_name, FIFOPort.classType());
        lastKnownPalettePos = 0;
    }


    public void destroy()
    {
    }


    // category: startup and cleanup
    public void init()
    {
    }


    public void begin()
    {
    }


    public void end()
    {
    }


  // category: reactions to notifications
  public void CatchOrDie(int column,  Tile t)
  {
     // expected to be called when a tile gets to the end of the chute
     // eventually this comparison could be more powerful
     // for example, multiple palettes
     if (column == lastKnownPalettePos)
     {
        // the palette caught it
        NamedPropertyMessage r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.AddTileToPaletteSelector );
        r.addParameter ("tile", t);
        sendDefault(r);
     }
     else // the tile fell to its death
     {
        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.MissedTileSelector );
        sendDefault(n);
     }

   }


  public void PlaceTileInWell(int column,  Tile t)
  {
     // expected to be called when the user wants to drop a tile
     NamedPropertyMessage r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.AddTileSelector );
     r.addParameter ("location", new Integer(column));
     r.addParameter ("tile", t);
     sendDefault(r);
  }


    // category: message handling
    private void sendDefault( Message m )
    {
        if( Utils.isC2Notification(m) )
        {
            sendToAll( m, bottomIface );
        }
        else if( Utils.isC2Request(m) )
        {
            sendToAll( m, topIface );
        }
    }


    public synchronized void handle( Message m )
    {
        if( Utils.isC2Notification(m) )
        {
            handleNotification( (NamedPropertyMessage)m );
        }
        else
        {
            handleRequest( (NamedPropertyMessage)m );
        }
    }


    public synchronized void handleRequest( NamedPropertyMessage r )
    {
       // handles no requests
    }


    public synchronized void handleNotification( NamedPropertyMessage n )
    {
        String message_name = n.getName();

        if (message_name.equals(KlaxSelectorTable.DropChuteTileSelector))
        {
            int location = ((Integer)n.getParameter("location")).intValue();
            Tile t = (Tile)n.getParameter("tile");
            CatchOrDie(location, t);
        }
        else if (message_name.equals(KlaxSelectorTable.PaletteTileDroppedSelector))
        {
            int location = ((Integer)n.getParameter("location")).intValue();
            Tile t = (Tile)n.getParameter("tile");
            PlaceTileInWell(location, t);
        }
        else if (message_name.equals(KlaxSelectorTable.PaletteLocationSelector))
        {
            int location = ((Integer)n.getParameter("location")).intValue();
            if (location >= 0 && location <= 4)
                lastKnownPalettePos = location;
            else
                System.err.println("posible error in palette statecast...");
        }
   }
}

