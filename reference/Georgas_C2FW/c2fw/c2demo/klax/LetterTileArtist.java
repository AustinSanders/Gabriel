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


public class LetterTileArtist extends c2.legacy.AbstractC2Brick
{
	KlaxSelectorTable kst;
	boolean forward_messages;
	
	// category: constructors
	public LetterTileArtist( String _name, boolean forward_flag )
	{
		super( new SimpleIdentifier(_name) );
		forward_messages = forward_flag;
	}
	
	
	public LetterTileArtist( Identifier id )
	{
		super(id);
	}
	
	
	public void destroy()
	{
	}
	
	
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
		//System.out.println("LetterTileArtist got a message: " + m);
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
		if( forward_messages )
		{
			// generate a request
			NamedPropertyMessage new_r = c2.legacy.Utils.createC2Request(r.getName());
			sendDefault( new_r );
		}
	}
	
	
	public synchronized void handleNotification( NamedPropertyMessage n )
	{
		String message_name = n.getName();
		
		if ((message_name.equals(KlaxSelectorTable.ObjectCreatedSelector)) ||
			(message_name.equals (KlaxSelectorTable.ObjectModifiedSelector)) ||
			(message_name.equals(KlaxSelectorTable.ObjectDestroyedSelector)))
		{
			//if(message_name.equals(kst.ObjectDestroyedSelector))
				//System.out.println("Got object destroyed selector letter.");
			String obj_type = (String)n.getParameter("type");
			if (obj_type.equals("tile"))
			{
				mapTile(n);
				NamedPropertyMessage new_n = (NamedPropertyMessage)n.duplicate();
				Tile t = (Tile)n.getParameter("tile");
				//System.out.println("t is : " + t);
				//System.out.println("t.class is: " + t.getClass());
				if (t.isEmpty()) {
					new_n.addParameter("type", "oval");
					new_n.addParameter("fill", "white");
					sendDefault( new_n );
				} else if (t instanceof LetterTile) {
					// shift the letter to display properly
					int newX = ((Integer)new_n.getParameter("x")).intValue() +
						((Integer)new_n.getParameter("width")).intValue()
						/ 5 * 2;
					int newY = ((Integer)new_n.getParameter("y")).intValue() +
						((Integer)new_n.getParameter("height")).intValue()
						/ 3 * 2;
					
					new_n.addParameter ("x", new Integer(newX));
					new_n.addParameter ("y", new Integer(newY));
					new_n.addParameter("type", "text");
					new_n.addParameter("value", t.toString());
					new_n.addParameter("font", "Helvetica");
					new_n.addParameter("color", "white");
					new_n.addParameter("style", "bold");
					new_n.addParameter("size", new Integer (14));
					sendDefault( new_n );
				}
				else if(t instanceof ColorTile)
				{
	        new_n.addParameter("type", "oval");
	        String new_name;
	        new_name = (String)n.getParameter("name");
	        new_name += "_mapped";
	        n.addParameter("name", new_name);
					new_n.addParameter("fill", t.toString());
					sendDefault(new_n);
				}
			}
			else {
				if (forward_messages)
					sendDefault( n );
			}
		}
		else
			if (forward_messages)
				sendDefault( n );
	}
	
	
	protected void mapTile( NamedPropertyMessage n )
	{
		mapName( n );
		mapType( n );
	}
	
	
	protected void mapType( NamedPropertyMessage n )
	{
	}
	
	
	protected void mapName( NamedPropertyMessage n )
	{
		String new_name;
		new_name = (String)n.getParameter("name");
		new_name += "_mapped";
		// delete the old entry?
		n.addParameter("name", new_name);
	}
}
