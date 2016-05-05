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

public class WellComponent extends c2.legacy.AbstractC2Brick
{
   protected Well my_well;
   protected KlaxSelectorTable kst;

    // category: constructor
   public WellComponent( String _name, int num_rows, int num_cols )
    {
        super( new SimpleIdentifier(_name) );
        create(_name, num_rows, num_cols);
    }


    public WellComponent( Identifier id )
    {
        super(id);
        create( id.toString(), 5, 5 );
    }


    private void create(String _name, int num_rows, int num_cols)
    {
        //super.create(_name, FIFOPort.classType());
        my_well = new Well(num_rows, num_cols);
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


    // category: c2 message processing
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


    // for each understood message, send both the operation
    // notification (for game logic components ) and a state
    // cast notification (for the artist)
    public synchronized void handleRequest( NamedPropertyMessage r )
    {
        String message_name = r.getName();
        NamedPropertyMessage n = null;
        boolean operation_completed = false;

        // handle message; always broadcast state
        if (message_name.equals(KlaxSelectorTable.AddTileSelector))
        {
            int loc;
            Tile t;

            loc = ((Integer)r.getParameter("location")).intValue();
            t = (Tile)r.getParameter("tile");
            add_tile(loc, t);

            Matrix m_old = my_well.get_well();
            Matrix m = (Matrix)m_old.clone();
            n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.StateCastSelector );
            n.addParameter ("state", m);
            sendDefault(n);

            operation_completed = true;
        }
        // to reduce message traffic, send a specific request to the
        // well to advance tiles or a request to anyone who can do so.
        // In the C++ implementation, this was a single request type
        else if (message_name.equals(KlaxSelectorTable.AdvanceWellTilesSelector) ||
               message_name.equalsIgnoreCase(KlaxSelectorTable.AdvanceTilesSelector))
        {
            int anyMoved = advance_tiles();
            if (anyMoved > 0)
            {
                Matrix m_old = my_well.get_well();
                Matrix m = (Matrix)m_old.clone();

                n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.StateCastSelector );
                n.addParameter ("state", m);
                sendDefault(n);

                operation_completed = true;
            }
        }
        else if (message_name.equals(KlaxSelectorTable.CollapseSelector))
        {
            collapse_tiles();
            n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.StateCastSelector );
            n.addParameter ("state", my_well.get_well().clone());
            sendDefault(n);

            operation_completed = true;
        }
        else if (message_name.equals(KlaxSelectorTable.RemoveHorizontalSelector))
        {
            int row, col, num_tiles;

            row = ((Integer)r.getParameter("row")).intValue();
            col = ((Integer)r.getParameter("column")).intValue();
            num_tiles = ((Integer)r.getParameter("num_tiles")).intValue();
            remove_horizontal(row, col, num_tiles);

            n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.StateCastSelector );
            n.addParameter ("state", my_well.get_well().clone());
            sendDefault(n);

            operation_completed = true;
        }
        else if (message_name.equals(KlaxSelectorTable.RemoveVerticalSelector))
        {
            int row, col, num_tiles;

            row = ((Integer)r.getParameter("row")).intValue();
            col = ((Integer)r.getParameter("column")).intValue();
            num_tiles = ((Integer)r.getParameter("num_tiles")).intValue();
            remove_vertical(row, col, num_tiles);

            n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.StateCastSelector );
            n.addParameter ("state", my_well.get_well().clone());
            sendDefault(n);

            operation_completed = true;
        }
        else if (message_name.equals(KlaxSelectorTable.RemoveDiagUpSelector))
        {
            int row, col, num_tiles;

            row = ((Integer)r.getParameter("row")).intValue();
            col = ((Integer)r.getParameter("column")).intValue();
            num_tiles = ((Integer)r.getParameter("num_tiles")).intValue();
            remove_diagonal_up (row, col, num_tiles);

            n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.StateCastSelector );
            n.addParameter ("state", my_well.get_well().clone());
            sendDefault(n);

            operation_completed = true;
        }
        else if (message_name.equals(KlaxSelectorTable.RemoveDiagDownSelector))
        {
            int row, col, num_tiles;

            row = ((Integer)r.getParameter("row")).intValue();
            col = ((Integer)r.getParameter("column")).intValue();
            num_tiles = ((Integer)r.getParameter("num_tiles")).intValue();
            remove_diagonal_down (row, col, num_tiles);

            n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.StateCastSelector );
            n.addParameter ("state", my_well.get_well().clone());
            sendDefault(n);

            operation_completed = true;
        }

        if (operation_completed)
        {
            // send the operation notification
            NamedPropertyMessage msg = (NamedPropertyMessage)r.duplicate();
            c2.legacy.Utils.tagAsC2Notification( msg );
            sendDefault(msg);
        }
    }


    // well ADT receives no notifications
    public synchronized void handleNotification( NamedPropertyMessage n )
    {
    }


   // category: accessors

   void add_tile(int location, Tile new_tile)
   {
      NamedPropertyMessage n;

      if (!my_well.add_tile(location, new_tile))
      {
         // well already filled in that column
         n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.WellFullSelector );
         n.addParameter ("location", new Integer(location));
         n.addParameter ("tile", new_tile);
         sendDefault(n);
      }
   }

   int advance_tiles ()
   {
      return (my_well.advance_tiles());
   }

   void collapse_tiles ()
   {
      my_well.collapse_tiles();
   }

   // right now, the remove methods send no notifications,
   // since everything is state cast
   void remove_horizontal(int row, int col, int num_tiles)
   {
      my_well.remove_horizontal (row, col, num_tiles);
   }

   void remove_vertical(int row, int col, int num_tiles)
   {
      my_well.remove_vertical (row, col, num_tiles);
   }

   void remove_diagonal_up(int row, int col, int num_tiles)
   {
      my_well.remove_diagonal_up (row, col, num_tiles);
   }

   void remove_diagonal_down(int row, int col, int num_tiles)
   {
      my_well.remove_diagonal_down (row, col, num_tiles);
   }
}

