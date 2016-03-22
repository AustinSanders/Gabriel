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


public class StatusComponent extends c2.legacy.AbstractC2Brick
{
   Status my_status = new Status ();
   protected KlaxSelectorTable kst;

    // category: constructors
    public StatusComponent( String _name )
    {
        super( new SimpleIdentifier(_name) );
        create( _name );
    }


    public StatusComponent( Identifier id )
    {
        super(id);
        create( id.toString() );
    }


    private void create( String _name )
    {
        //super.create(_name, FIFOPort.classType());
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


  void notifyCurrentScore(int score)
  {
     NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.CurrentScoreSelector );
     n.addParameter("score", new Integer (score));
     sendDefault(n);
  }

  void notifyNumberOfPlayers(int numPlayers)
  {
     NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.NumberOfPlayersSelector );
     n.addParameter("num_players", new Integer (numPlayers));
     sendDefault(n);
  }

  void notifyNumberOfLives(int numLives)
  {
     NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.NumberOfLivesSelector );
     n.addParameter("num_lives", new Integer (numLives));
     sendDefault(n);
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
        String message_name = r.getName();

        if (message_name.equals(KlaxSelectorTable.IncrementScoreSelector))
        {
            my_status.IncrementScore(((Integer)r.getParameter("delta")).intValue());
            notifyCurrentScore(my_status.GetCurrentScore());
        }
        else if (message_name.equals(KlaxSelectorTable.GetNumberOfLivesSelector))
            notifyNumberOfLives(my_status.GetNumberOfLives());
        else if (message_name.equals(KlaxSelectorTable.DecrementNumberOfLivesSelector))
        {
            my_status.DecrementNumberOfLives();
            notifyNumberOfLives(my_status.GetNumberOfLives());
        }
    }

    // handles no notifications
    public synchronized void handleNotification( NamedPropertyMessage n )
    {
    }
}

