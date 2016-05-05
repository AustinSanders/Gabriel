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


public class ChuteArtist extends c2.legacy.AbstractC2Brick
{
	protected KlaxSelectorTable kst;
	protected Vector grid_objects;
	protected int line_id;
	protected int tile_id;
	protected Matrix tile_objects;
	
	int num_rows;
	int num_cols;
	
	protected String chute_viewport_name;
	protected int chute_viewport_xpos;
	protected int chute_viewport_ypos;
	protected String chute_viewport_fg = "white";
	protected String chute_viewport_bg = "darkGray";
	protected int grid_rect_width       = 50;
	protected int grid_rect_height      = 30;
	protected int tile_width_diff  = 4;
	protected int tile_height_diff = 4;
	protected int tile_width = grid_rect_width - tile_width_diff;
	protected int tile_height = grid_rect_height - tile_height_diff;
	protected int tile_xoffset = tile_width_diff/2;
	protected int tile_yoffset = tile_height_diff/2;
	
	
	// category: constructor
	public ChuteArtist( String _name, int _rows, int _cols, int xpos, int ypos )
	{
		super( new SimpleIdentifier(_name) );
		create( _name, _rows, _cols, xpos, ypos );
	}
	
	
	public ChuteArtist( Identifier id )
	{
		super(id);
		create( id.toString(), 5, 5, 10, 30);
	}
	
	
	private void create( String _name, int _rows, int _cols, int xpos, int ypos )
	{
		//super.create(_name, FIFOPort.classType());
		num_rows = _rows;
		num_cols = _cols;
		grid_objects = new Vector ();
		line_id = 0;
		tile_id = 0;
		tile_objects = new Matrix (num_rows, num_cols);
		chute_viewport_xpos = xpos;
		chute_viewport_ypos = ypos;
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
		create_viewport ();
		draw_grid ();
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
	
	
	public synchronized void handleRequest( NamedPropertyMessage r )
	{
		// processes no requests
	}
	
	
	public synchronized void handleNotification( NamedPropertyMessage n )
	{
		String message_name = n.getName();
		
		if (message_name.equals(KlaxSelectorTable.PlaceTileSelector))
		{
			int loc = ((Integer)n.getParameter("location")).intValue();
			Tile t  = ((Tile)n.getParameter("tile"));
			place_tile(loc, t);
		}
		else if (message_name.equals(KlaxSelectorTable.AdvanceChuteTilesSelector))
		{
			advance_tiles();
		}
		else if(message_name.equals(KlaxSelectorTable.ClearAllChutesSelector)){
			//System.out.println("Got clear all chutes selector.");
			for(int i = 0; i < num_rows; i++){
				for(int j = 0; j < num_cols; j++){
					erase_tile(i, j);
				}
			}
		}
	}
	
	
	protected void create_viewport ()
	{
		chute_viewport_name = getClass().getName() + "." +
			getIdentifier().toString() + "." + "vport1";
		NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ViewportCreatedSelector );
		n.addParameter ("x", new Integer(chute_viewport_xpos));
		n.addParameter ("y", new Integer (chute_viewport_ypos));
		n.addParameter ("width", new Integer (grid_rect_width*num_cols+1));
		n.addParameter ("height", new Integer (grid_rect_height*num_rows+1));
		n.addParameter ("foreground", chute_viewport_fg);
		n.addParameter ("background", chute_viewport_bg);
		n.addParameter ("title", chute_viewport_name);
		n.addParameter ("id", chute_viewport_name);
		sendDefault(n);
	}
	
	
	protected void draw_grid_line (int x, int y,
		int deltax, int deltay)
	{
		// build new instance of a line object
		String line_name = getIdentifier().toString() + "line" + line_id;
		line_id++;
		
		Hashtable line = new Hashtable ();
		line.put ("parent_id", chute_viewport_name);
		line.put ("name", line_name);
		line.put ("type", "line");
		line.put ("x1", new Integer (x));
		line.put ("y1", new Integer (y));
		line.put ("x2", new Integer (x + deltax));
		line.put ("y2", new Integer (y + deltay));
		line.put ("color", "white");
		
		// keep track of this new object
		grid_objects.addElement (line);
		
		NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectCreatedSelector );
		Enumeration e = line.keys();
		while( e.hasMoreElements() )
		{
			String key = (String)e.nextElement();
			n.addParameter( key, line.get(key) );
		}
		sendDefault(n);
	}
	
	
	
	protected void draw_tile (int row, int col, Tile t)
	{
		int xpos = (grid_rect_width * col) + tile_xoffset;
		int ypos = (grid_rect_height * row) + tile_yoffset;
		
		// build new instance of a tile object
		String tile_name = getIdentifier().toString() + "tile" + tile_id;
		tile_id++;
		
		Hashtable tile = new Hashtable ();
		tile.put ("parent_id", chute_viewport_name);
		tile.put ("name", tile_name);
		tile.put ("type", "tile");
		tile.put ("x", new Integer (xpos));
		tile.put ("y", new Integer (ypos));
		tile.put ("width", new Integer (tile_width));
		tile.put ("height", new Integer (tile_height));
		tile.put ("tile", t);
		tile.put ("border", "white"); // should be set by default
		
		tile_objects.set_matrix_element(row, col, tile);
		
		NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectCreatedSelector );
		Enumeration e = tile.keys();
		while( e.hasMoreElements() )
		{
			String key = (String)e.nextElement();
			n.addParameter( key, tile.get(key) );
		}
		sendDefault(n);
	}
	
	
	void drop_tile (int row, int col)
	{
		// retrieve tile object from matrix
		Hashtable tile_obj = (Hashtable)tile_objects.get_matrix_element(row, col);
		
		// modify tile's y pos
		int new_ypos = (grid_rect_height * (row+1)) + tile_yoffset;
		tile_obj.put ("y", new Integer (new_ypos));
		
		NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectModifiedSelector );
		Enumeration e = tile_obj.keys();
		while( e.hasMoreElements() )
		{
			String key = (String)e.nextElement();
			n.addParameter( key, tile_obj.get(key) );
		}
		sendDefault(n);
		
		// modify position in tile_objects matrix
		tile_objects.set_matrix_element(row+1, col, tile_obj);
		tile_objects.remove_matrix_element(row, col);
	}
	
	
	
	protected void erase_tile (int row, int col)
	{
		Hashtable tile = (Hashtable)tile_objects.get_matrix_element(row, col);
		
		if (tile != null)
		{
			//System.out.println("found a tile!");
			NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectDestroyedSelector );
			Enumeration e = tile.keys();
			while( e.hasMoreElements() )
			{
				String key = (String)e.nextElement();
				n.addParameter( key, tile.get(key) );
			}
			//System.out.println("Sending object destroy message: " + n);
			sendDefault(n);
			
			// delete tile object from tile_objects matrix
			tile_objects.remove_matrix_element(row, col);
		}
	}
	
	
	protected void draw_grid ()
	{
		// vertical lines
		for (int c = 0; c <= num_cols; c++)
			draw_grid_line (c*grid_rect_width, 0,
			0, grid_rect_height*num_rows);
		
		// horizontal lines
		draw_grid_line (0, 0, grid_rect_width*num_cols, 0);
		draw_grid_line (0, grid_rect_height*num_rows,
			grid_rect_width*num_cols, 0);
	}
	
	
	protected void shift_tiles_at_col(int col)
	{
		// if last element is a tile, erase it
		if (tile_objects.get_matrix_element(num_rows-1, col) != null)
			erase_tile(num_rows-1, col);
		
		// drop any existing tiles in bottom up order
		for (int i = num_rows-2; i >= 0; i--)
			if (tile_objects.get_matrix_element(i, col) != null)
				drop_tile(i, col);
	}
	
	
	
	protected void place_tile(int location, Tile tile)
	{
		Tile t = (Tile) tile.clone();
		advance_tiles();
		draw_tile(0, location, t);
	}
	
	
	protected void advance_tiles()
	{
		for (int j = 0; j < num_cols; j++)
			shift_tiles_at_col(j);
	}
}
