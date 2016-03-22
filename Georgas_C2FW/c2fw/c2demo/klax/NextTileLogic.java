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
import java.util.*;
import java.io.*;

public class NextTileLogic extends c2.legacy.AbstractC2Brick
{
	// category: representation
	protected int freq, state, variance;
	protected KlaxSelectorTable kst;
	
	protected boolean doHandleTicks = false;
	
	// category: constructors
	public NextTileLogic( String _name )
	{
		super( new SimpleIdentifier(_name) );
		create( _name );
	}
	
	
	public NextTileLogic( Identifier id )
	{
		super(id);
		create( id.toString() );
	}
	
	
	private void create( String _name )
	{
		//super.create(_name, FIFOPort.classType());
		freq = 6;
		state = 0;
		variance = 2;
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
		doHandleTicks = true;
	}
	
	
	public void end()
	{
		doHandleTicks = false;
		NamedPropertyMessage r = c2.legacy.Utils.createC2Request(KlaxSelectorTable.ClearAllChutesSelector);
		sendDefault(r);
	}
	
	
	protected int newTileTag()
	{
		Random rand = new Random();
		
		int num_tiles = ColorTile.numColors;
		int next_int = rand.nextInt();
		
		if (next_int < 0)
			next_int = -next_int;
		
		int newTileKind = (next_int % num_tiles)+1;
		
		return newTileKind;
	}
	
	
	// category: reactions to notifications
	public void HandleTick()
	{
		if(!doHandleTicks){
			return;
		}
		NamedPropertyMessage r;
		int rand_int;
		
		r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.AdvanceWellTilesSelector );
		sendDefault(r);
		
		state--;
		if (state <= 0)
		{
			Random rand = new Random();
			int left = 0;
			int right = 4;
			
			rand_int = rand.nextInt();
			if (rand_int < 0)
				rand_int = -rand_int;
			state = freq + (rand_int % variance);
			
			rand_int = rand.nextInt();
			if (rand_int < 0)
				rand_int = -rand_int;
			int column = (rand_int % (right-left+1)) + left;
			
			int tile_id = newTileTag();
			
			r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.PlaceTileSelector );
			r.addParameter("location", new Integer (column));
			r.addParameter("tile", new ColorTile(tile_id));
			sendDefault(r);
		}
		else
		{
			r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.AdvanceChuteTilesSelector );
			sendDefault(r);
		}
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
		
		if (message_name.equals(KlaxSelectorTable.Tick))
		{
			HandleTick();
		}
		else if (message_name.equals(KlaxSelectorTable.MinorTick))
		{
			NamedPropertyMessage r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.AdvanceWellTilesSelector );
			sendDefault(r);
		}
	}
}
