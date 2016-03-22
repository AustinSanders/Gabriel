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

public class PaletteComponent extends c2.legacy.AbstractC2Brick
{
   protected KlaxSelectorTable kst;
   protected Palette my_palette;

    // category: constructor
    public PaletteComponent( String _name )
    {
        super( new SimpleIdentifier(_name) );
        create(_name);
    }


    public PaletteComponent( Identifier id )
    {
        super(id);
        create( id.toString() );
    }


    private void create( String _name )
    {
        //super.create(_name, FIFOPort.classType());
        my_palette = new Palette();
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


   // category: accessors
   public boolean AddTileToPalette(Tile NewTile)
   {
      return my_palette.AddTileToPalette(NewTile);
   }

   public Tile DropTileIntoWell()
   {
      return my_palette.DropTileIntoWell();
   }

   public int GetPaletteCapacity()
   {
      return my_palette.GetPaletteCapacity();
   }

   public void SetPaletteCapacity(int New_Capacity)
   {
      my_palette.SetPaletteCapacity(New_Capacity);
   }

   public int MovePalette(int direction)
   {
      return my_palette.MovePalette(direction);
   }

   public int GetPaletteLocation()
   {
      return my_palette.GetPalettePosition();
   }

   public void ResetPalette()
   {
      my_palette.ResetPalette();
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
        String message_name = r.getName();
        NamedPropertyMessage n = null;

        if (message_name.equals(KlaxSelectorTable.AddTileToPaletteSelector))
        {
            boolean Status;
            Tile new_tile;

            new_tile = (Tile)r.getParameter("tile");
            Status = AddTileToPalette(new_tile);

            if (Status)
            {
                n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.TileAddedToPaletteSelector );
                n.addParameter ("tile", new_tile);
                sendDefault(n);
            }
            else
            {
                n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.PaletteFullSelector );
                n.addParameter ("location", new Integer(GetPaletteLocation()));
                n.addParameter ("tile", new_tile);
                sendDefault(n);
            }
        }
        else if (message_name.equals(KlaxSelectorTable.DropPaletteTileSelector))
        {
            Tile the_tile = DropTileIntoWell();

            if (the_tile != null)
            {
                n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.PaletteTileDroppedSelector );
                n.addParameter ("location", new Integer(GetPaletteLocation()));
                n.addParameter ("tile", the_tile);
                sendDefault(n);
            }
            else
            {
                n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.PaletteEmptySelector );
                n.addParameter ("location", new Integer(GetPaletteLocation()));
                sendDefault(n);
            }
        }
        else if (message_name.equals(KlaxSelectorTable.MovePaletteSelector))
        {
            int direction, location;

            direction = ((Integer)r.getParameter("direction")).intValue();
            location = MovePalette(direction);

            n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.PaletteLocationSelector );
            n.addParameter ("location", new Integer(GetPaletteLocation()));
            sendDefault(n);
        }
        else if (message_name.equals(KlaxSelectorTable.GetPaletteLocationSelector))
        {
            n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.PaletteLocationSelector );
            n.addParameter ("location", new Integer(GetPaletteLocation()));
            sendDefault(n);
        }
        else if (message_name.equals(KlaxSelectorTable.GetPaletteCapacitySelector))
        {
            n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.PaletteCapacitySelector );
            n.addParameter ("capacity", new Integer(GetPaletteCapacity()));
            sendDefault(n);
        }
        else if (message_name.equals(KlaxSelectorTable.SetPaletteCapacitySelector))
        {
            int capacity;

            capacity = ((Integer)r.getParameter("capacity")).intValue();
            SetPaletteCapacity (capacity);

            // in case the set capacity was too large or too small and
            // needed to be reset, make sure to call GetPaletteCapacity
            n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.PaletteCapacitySelector );
            n.addParameter ("capacity", new Integer(GetPaletteCapacity()));
            sendDefault(n);
        }
        else if (message_name.equals(KlaxSelectorTable.ResetPaletteSelector))
        {
            ResetPalette();
        }
    }


    public synchronized void handleNotification( NamedPropertyMessage n )
    {
        // Palette ADT handles no notifications
    }
}
