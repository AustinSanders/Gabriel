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

public class ChuteComponent extends c2.legacy.AbstractC2Brick
{
	// representation
	protected Chute my_chute;
	protected KlaxSelectorTable kst;
	
	
	// category: constructor
	public ChuteComponent( String _name, int num_rows, int num_cols )
	{
		super( new SimpleIdentifier(_name) );
		create(_name, num_rows, num_cols);
	}
	
	
	public ChuteComponent( Identifier id )
	{
		super(id);
		create( id.toString(), 5, 5 );
	}
	
	
	private void create( String _name, int num_rows, int num_cols )
	{
		//super.create(_name, FIFOPort.classType());
		my_chute = new Chute(num_rows, num_cols);
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
	
	
	void notifyOfDroppedTile(DroppedTile dt)
	{
		NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.DropChuteTileSelector );
		n.addParameter ("location", new Integer(dt.location()));
		n.addParameter ("tile", dt.tile());
		sendDefault(n);
	}
	
	
	////////////////////////////////////////////////////////////////
	// category: accessors
	
	void place_tile(int location, Tile new_tile)
	{
		DroppedTile dt = my_chute.placeTile(location, new_tile);
		
		if (dt != null)
			notifyOfDroppedTile(dt);
	}
	
	void advance_tiles()
	{
		DroppedTile dt = my_chute.advanceTiles();
		
		if (dt != null)
			notifyOfDroppedTile(dt);
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
	
	
	public synchronized void handleRequest( NamedPropertyMessage r )
	{
		String message_name = r.getName();
		
		if (message_name.equals(KlaxSelectorTable.PlaceTileSelector))
		{
			int loc;
			
			loc = ((Integer)r.getParameter("location")).intValue();
			Tile t = ((Tile)r.getParameter("tile"));
			place_tile(loc, t) ;
			
			// same message is used to notify components below, so just
			// change the type to notification and resend
			NamedPropertyMessage n = (NamedPropertyMessage)r.duplicate();
			c2.legacy.Utils.tagAsC2Notification(n);
			sendDefault(n);
		}
		// to reduce message traffic, send a specific request to the
		// well to advance tiles or a request to anyone who can do so.
		// In the C++ implementation, this was a single request type
		else if (message_name.equals(KlaxSelectorTable.AdvanceChuteTilesSelector) ||
			message_name.equalsIgnoreCase(KlaxSelectorTable.AdvanceTilesSelector))
		{
			advance_tiles();
			
			// same message is used to notify components below, so just
			// change the type to notification and resend
			NamedPropertyMessage n = (NamedPropertyMessage)r.duplicate();
			c2.legacy.Utils.tagAsC2Notification(n);
			sendDefault(n);
		}
		else if(message_name.equals(KlaxSelectorTable.ClearAllChutesSelector)){
			my_chute.clearAllChutes();
			NamedPropertyMessage n = (NamedPropertyMessage)r.duplicate();
			c2.legacy.Utils.tagAsC2Notification(n);
			sendDefault(n);
		}
	}
	
	// chute ADT receives no notifications
	public synchronized void handleNotification( NamedPropertyMessage n )
	{
	}
}

