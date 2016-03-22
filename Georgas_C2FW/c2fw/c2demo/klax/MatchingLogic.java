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


public class MatchingLogic extends c2.legacy.AbstractC2Brick
{
   protected static final int shortest = 3;
   protected Matrix tiles = null;
   protected int nrows, ncols;
   protected int[] deltaRowTable = { -1, -1, 0, 1, 1, 1, 0, -1 };
   protected int[] deltaColTable = { 0, 1, 1, 1, 0, -1, -1, -1 };
   protected boolean matchesHappened; // true if already had some
                                      // matches on this Tick
   protected KlaxSelectorTable kst;

   // flags for if a certain tile is used in each direction
   protected boolean used[][] = new boolean[10][10];


    // category: constructor
    public MatchingLogic( String _name )
    {
        super( new SimpleIdentifier(_name) );
        create(_name);
    }


    public MatchingLogic( Identifier id )
    {
        super(id);
        create( id.toString() );
    }


    private void create( String _name )
    {
        matchesHappened = false;
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



   // category: operations
   protected void foundMatch (String msg_name, int row, int col, int len)
   {
      NamedPropertyMessage r = c2.legacy.Utils.createC2Request( msg_name );
      r.addParameter ("row", new Integer (row));
      r.addParameter ("column", new Integer (col));
      r.addParameter ("num_tiles", new Integer (len));
      sendDefault(r);
   }


   protected void clearUsed()
   {
      for ( int r = 0; r < nrows; ++r)
         for ( int c = 0; c < ncols; ++c)
            used[r][c] = false; // mark everything unused
   }

   protected int longestMatchingSequence (int row, int col, int dir)
   {
      int len = 0;
      int curRow = row;
      int curCol = col;
      int deltarow = deltaRowTable[dir];
      int deltacol = deltaColTable[dir];

      Tile tile = (Tile)tiles.get_matrix_element(row, col);
      if (tile.isSpace() || tile.isEmpty())
         return 0;
      if (used[row][col])
         return 0;  // return 0 if already counted

      while (curRow >= 0 && curCol >= 0 &&
             curRow < nrows && curCol < ncols)
      {
         if (!tile.equal((Tile)tiles.get_matrix_element(curRow, curCol)))
            return len;
         used[curRow][curCol] = true;
         curRow += deltarow;
         curCol += deltacol;
         len++;
      }

      return len;
   }

   // category: matching search
   public void checkMatch(String msg_name, int row, int col, int dir)
   {
      if (tiles == null)
         System.err.println ("no tiles!");

      int len = longestMatchingSequence(row, col, dir);
      if (len >= shortest)
      {
         foundMatch(msg_name, row, col, len);
         matchesHappened = true;
      }
   }


   public void lookForMatches()
   {
      if (tiles == null)
         System.err.println ("no tiles!");

      nrows = tiles.get_matrix_row_size();
      ncols = tiles.get_matrix_col_size();
      int r, c;

      clearUsed();

      for (r = 0; r < nrows; ++r)
         for ( c = 0; c < ncols; ++c)
           checkMatch(KlaxSelectorTable.RemoveHorizontalSelector, r, c, 2);

      clearUsed();
      for ( r = 0; r < nrows; ++r)
         for ( c = 0; c < ncols; ++c)
            checkMatch(KlaxSelectorTable.RemoveDiagDownSelector, r, c, 3);

      clearUsed();
      for ( r = 0; r < nrows; ++r)
         for ( c = 0; c < ncols; ++c)
            checkMatch(KlaxSelectorTable.RemoveVerticalSelector, r, c, 4);

      clearUsed();
      for ( r = nrows - 1 ; r >= 0; --r)
         for ( c = 0; c < ncols; ++c)
            checkMatch(KlaxSelectorTable.RemoveDiagUpSelector, r, c, 1);
   }


   protected void removeTilesInMidAir()
   {
      int r, c, r2;
      for ( r = 0; r < nrows; ++r)
         for ( c = 0; c < ncols; ++c)
         {
            Tile t = (Tile)tiles.get_matrix_element(r, c);
            if (t.isSpace())
               for (r2 = r; r2 >= 0; --r2)
                  tiles.set_matrix_element(r2, c, t);
         }
   }


   // category: state cast receiving
   void acceptNewState(Matrix newTiles)
   {
      tiles = (Matrix)newTiles.clone();
      removeTilesInMidAir();
      lookForMatches();
   }


    // category: dispatching
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

        if (message_name.equals(KlaxSelectorTable.StateCastSelector))
        {
            Matrix m = (Matrix)n.getParameter("state");
            acceptNewState(m);
        }
        else if (message_name.equals(KlaxSelectorTable.Tick))
        {
            if (matchesHappened)
            {
                NamedPropertyMessage r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.CollapseSelector );
                sendDefault(r);
                matchesHappened = false;
            }
        }
    }
}
