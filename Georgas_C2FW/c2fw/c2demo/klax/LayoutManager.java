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

public class LayoutManager extends c2.legacy.AbstractC2Brick
{
	
	protected KlaxSelectorTable kst;
	protected int winHeight, winWidth;
	protected String winTitle;
	
	protected int layout_window_x = 0;
	protected int layout_window_y = 0;
	protected String layout_window_fg = "white";
	protected String layout_window_bg = "darkGray";
	
	// category: constructor
	public LayoutManager( String _name, String _title, int height, int width )
	{
		super( new SimpleIdentifier(_name) );
		create(_name, _title, height, width);
	}
	
	
	public LayoutManager( Identifier id )
	{
		super(id);
		create( id.toString(), "KLAX!!!", 660, 275);
	}
	
	
	private void create( String _name, String _title, int height, int width )
	{
		//super.create(_name, FIFOPort.classType());
		winTitle = _title;
		winHeight = height;
		winWidth = width;
	}
	
	// category: startup and cleanup
	public void init()
	{
	}
	
	public void destroy()
	{
	}
	
	private boolean doneBeginning = false;
	
	public void begin()
	{
		createWindow();
		doneBeginning = true;
	}
	
	
	public void end()
	{
		destroyWindow();
	}
	
	public void start(){
		super.start();
		//createWindow();
	}
	
	public void stop(){
		super.stop();
		//destroyWindow();
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
		// forwards all requests - but only once, since each is sent
		// as a key event both from the panel and the overall viewport
		if (!((String)r.getParameter("parent_id")).equals(getIdentifier().toString()))
			sendDefault(r);
	}
	
	
	public synchronized void handleNotification( NamedPropertyMessage n )
	{
		while(!doneBeginning){
			try{Thread.sleep(100);} catch(InterruptedException ie){}
		}
		
		String message_name = n.getName();
		String message_type = (String)n.getParameter("type");
		if (message_name.equals(KlaxSelectorTable.ViewportCreatedSelector))
		{
			//System.out.println("handling vcs notification " + n);
			// change the viewport to a panel
			n.setName(KlaxSelectorTable.PanelAddedSelector);
			
			// panel belongs to the overall window
			n.addParameter ("parent_id", getIdentifier().toString());
			
			// forward the message
			sendDefault(n);
		}
		else if ((message_name.equals(KlaxSelectorTable.ObjectModifiedSelector)) ||
			(message_name.equals(KlaxSelectorTable.ObjectCreatedSelector)) ||
			(message_name.equals(KlaxSelectorTable.ObjectDestroyedSelector)))
		{
			// only send converted tiles on
			if (!message_type.equals("tile")){
				//System.out.println("Sending on destroyed message.");
				sendDefault(n);
			}
		}
		else sendDefault(n);
	}
	
	
	protected void createWindow()
	{
		NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ViewportCreatedSelector );
		n.addParameter ("x", new Integer(layout_window_x));
		n.addParameter ("y", new Integer (layout_window_y));
		n.addParameter ("width", new Integer (winWidth));
		n.addParameter ("height", new Integer (winHeight));
		n.addParameter ("foreground", layout_window_fg);
		n.addParameter ("background", layout_window_bg);
		n.addParameter ("title", winTitle);
		n.addParameter ("id", getIdentifier().toString());
		//System.out.println("***Sending viewport created message!!!");
		sendDefault(n);
	}
	
	void destroyWindow()
	{
		NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ViewportDestroyedSelector );
		n.addParameter ("id", getIdentifier().toString());
		sendDefault(n);
	}
}
