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

public class StatusLogic extends c2.legacy.AbstractC2Brick
{
   protected static final int horizontal = 0;
   protected static final int vertical = 1;
   protected static final int diagup = 2;
   protected static final int diagdown = 3;
   protected static final int scoring[] = {1000, 50, 5000, 5000};

   protected KlaxSelectorTable kst;
   protected String paletteName;
   protected int numOfTicksInRow;
   protected int bonusMultiplier;
   protected boolean ignore;

    // category: constructor
    public StatusLogic( String _name )
    {
        super( new SimpleIdentifier(_name) );
        create( _name );
    }


    public StatusLogic( Identifier id )
    {
        super(id);
        create( id.toString() );
    }


    private void create( String _name )
    {
        //super.create(_name, FIFOPort.classType());
        ignore          = false;
        numOfTicksInRow = 0;
        bonusMultiplier = 1;
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


   // category: commands
   protected int calculate_score(int direction, int number_of_tiles)
   {
      int adjusted_tiles = number_of_tiles - 2;
      return ((scoring[direction] * adjusted_tiles) * bonusMultiplier);
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
        // doesn't handle any requests
    }


    public synchronized void handleNotification( NamedPropertyMessage n )
    {
        String message_name = n.getName();

        if (message_name.equals(KlaxSelectorTable.Tick))
        {
            numOfTicksInRow++;
            if (numOfTicksInRow == 2)
            {
                bonusMultiplier = 1;
                numOfTicksInRow = 0;
            }
        }
        else if (message_name.equals(KlaxSelectorTable.CollapseSelector))
        {
            numOfTicksInRow = 0;
            bonusMultiplier = bonusMultiplier << 1;
        }
        else if (message_name.equals(KlaxSelectorTable.MissedTileSelector) ||
               message_name.equals(KlaxSelectorTable.PaletteFullSelector))
        {
            NamedPropertyMessage r1, r2;
            numOfTicksInRow = 0;

            r1 = c2.legacy.Utils.createC2Request( KlaxSelectorTable.DecrementNumberOfLivesSelector );
            sendDefault(r1);
            r2 = c2.legacy.Utils.createC2Request( KlaxSelectorTable.GetNumberOfLivesSelector );
            sendDefault(r2);
        }
        else if (message_name.equals(KlaxSelectorTable.WellFullSelector))
        {
            NamedPropertyMessage r;
            numOfTicksInRow = 0;

            int loc = ((Integer)n.getParameter("location")).intValue();
            Tile t = (Tile)n.getParameter("tile");
            r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.AddTileToPaletteSelector );
            r.addParameter ("tile", t);
            sendDefault(r);

            ignore = true;
        }
        else if (message_name.equals(KlaxSelectorTable.NumberOfLivesSelector))
        {
            int lives = ((Integer)n.getParameter("num_lives")).intValue();
            if (lives == 0)
            {
                NamedPropertyMessage r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.SuspendClock );
                sendDefault(r);
            }
        }
        else if (message_name.equals(KlaxSelectorTable.TileAddedToPaletteSelector))
        {
            if (!ignore)
            {
                NamedPropertyMessage r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.IncrementScoreSelector );
                r.addParameter ("delta", new Integer(5));
                sendDefault(r);
            }
            else
                ignore = false;
        }
        else if (message_name.equals(KlaxSelectorTable.RemoveHorizontalSelector))
        {
            int row = ((Integer)n.getParameter("row")).intValue();
            int col = ((Integer)n.getParameter("column")).intValue();
            int num_tiles = ((Integer)n.getParameter("num_tiles")).intValue();
            int score = calculate_score(horizontal, num_tiles);

            NamedPropertyMessage r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.IncrementScoreSelector );
            r.addParameter ("delta", new Integer(score));
            sendDefault(r);
        }
        else if (message_name.equals(KlaxSelectorTable.RemoveVerticalSelector))
        {
            int row = ((Integer)n.getParameter("row")).intValue();
            int col = ((Integer)n.getParameter("column")).intValue();
            int num_tiles = ((Integer)n.getParameter("num_tiles")).intValue();
            int score = calculate_score(vertical, num_tiles);

            NamedPropertyMessage r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.IncrementScoreSelector );
            r.addParameter ("delta", new Integer(score));
            sendDefault(r);
        }
        else if (message_name.equals(KlaxSelectorTable.RemoveDiagUpSelector))
        {
            int row = ((Integer)n.getParameter("row")).intValue();
            int col = ((Integer)n.getParameter("column")).intValue();
            int num_tiles = ((Integer)n.getParameter("num_tiles")).intValue();
            int score = calculate_score(diagup, num_tiles);

            NamedPropertyMessage r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.IncrementScoreSelector );
            r.addParameter ("delta", new Integer(score));
            sendDefault(r);
        }
        else if (message_name.equals(KlaxSelectorTable.RemoveDiagDownSelector))
        {
            int row = ((Integer)n.getParameter("row")).intValue();
            int col = ((Integer)n.getParameter("column")).intValue();
            int num_tiles = ((Integer)n.getParameter("num_tiles")).intValue();
            int score = calculate_score(diagdown, num_tiles);

            NamedPropertyMessage r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.IncrementScoreSelector );
            r.addParameter ("delta", new Integer(score));
            sendDefault(r);
        }
    }
}
