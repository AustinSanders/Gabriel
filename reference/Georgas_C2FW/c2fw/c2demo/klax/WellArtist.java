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

public class WellArtist extends c2.legacy.AbstractC2Brick
{
   protected KlaxSelectorTable kst;
   protected Matrix grid_rects;
   protected Vector grid_lines;
   protected int line_id;
   protected Matrix tile_objects;

   protected int num_rows;
   protected int num_cols;

   protected String well_viewport_name;
   protected int well_viewport_xpos;
   protected int well_viewport_ypos;
   protected String well_viewport_fg = "white";
   protected String well_viewport_bg = "darkGray";
   protected int grid_rect_width       = 50;
   protected int grid_rect_height      = 30;
   protected int tile_width_diff  = 4;
   protected int tile_height_diff = 4;
   protected int tile_width = grid_rect_width - tile_width_diff;
   protected int tile_height = grid_rect_height - tile_height_diff;
   protected int tile_xoffset = tile_width_diff/2;
   protected int tile_yoffset = tile_height_diff/2;


    // category: constructor
    public WellArtist( String _name, int _rows, int _cols, int xpos, int ypos )
    {
        super( new SimpleIdentifier(_name) );
        create(_name, _rows, _cols, xpos, ypos );
    }


    public WellArtist( Identifier id )
    {
        super(id);
        create(id.toString(),  5, 5, 10, 360);
    }


    private void create(String _name, int _rows, int _cols,
                      int xpos, int ypos)
    {
        num_rows = _rows;
        num_cols = _cols;
        grid_rects = new Matrix (num_rows, num_cols);
        grid_lines = new Vector ();
        line_id = 0;
        tile_objects = new Matrix (num_rows, num_cols);
        well_viewport_xpos = xpos;
        well_viewport_ypos = ypos;
    }


    public void destroy()
    {
    }


    public void init()
    {
    }


    // category: startup and cleanup
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


/*   public void create(String _name, int _rows, int _cols,
                      int xpos, int ypos)
   {
      super.create(_name, FIFOPort.classType());
      num_rows = _rows;
      num_cols = _cols;
      grid_rects = new Matrix (num_rows, num_cols);
      grid_lines = new Vector ();
      line_id = 0;
      tile_objects = new Matrix (num_rows, num_cols);
      well_viewport_xpos = xpos;
      well_viewport_ypos = ypos;
   }
   public void create(String name, Class portClass) {
      create(name,  5, 5, 10, 360);
   }
*/

    public synchronized void handleRequest( NamedPropertyMessage r )
    {
        // processes no requests
    }


    public synchronized void handleNotification( NamedPropertyMessage n )
    {
        String message_name = n.getName();

        Matrix m = (Matrix)n.getParameter("state");
        if (message_name.equals(KlaxSelectorTable.StateCastSelector))
            redisplay_well((Matrix)n.getParameter("state"));
    }


    protected void create_viewport()
    {
        well_viewport_name = getClass().getName() + "." +
                             getIdentifier().toString() + "." + "vport1";
        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ViewportCreatedSelector );
        n.addParameter ("x", new Integer(well_viewport_xpos));
        n.addParameter ("y", new Integer (well_viewport_ypos));
        n.addParameter ("width", new Integer (grid_rect_width*num_cols+1));
        n.addParameter ("height", new Integer (grid_rect_height*num_rows+1));
        n.addParameter ("foreground", well_viewport_fg);
        n.addParameter ("background", well_viewport_bg);
        n.addParameter ("title", well_viewport_name);
        n.addParameter ("id", well_viewport_name);
        sendDefault(n);
    }


    protected void draw_grid_rect( int row, int col )
    {
        int xpos = grid_rect_width * col;
        int ypos = grid_rect_height * row;

        // build new instance of a rectangle object
        String rectangle_name = getIdentifier().toString() + "grid" + row + col;

        Hashtable tile = new Hashtable();
        tile.put( "parent_id", well_viewport_name);
        tile.put( "name", rectangle_name);
        tile.put( "type", "rectangle");
        tile.put( "x", new Integer (xpos));
        tile.put( "y", new Integer (ypos));
        tile.put( "width", new Integer (grid_rect_width));
        tile.put( "height", new Integer (grid_rect_height));
        tile.put( "fill", "darkGray");
        tile.put( "border", "white");

        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectCreatedSelector );
        Enumeration e = tile.keys();
        while( e.hasMoreElements() )
        {
            String key = (String)e.nextElement();
            n.addParameter( key, tile.get(key) );
        }

        // keep track of this new object
        grid_rects.set_matrix_element(row, col, tile );

        sendDefault(n);
    }


    protected void draw_tile( int row, int col, Tile t )
    {
        int xpos = (grid_rect_width * col) + tile_xoffset;
        int ypos = (grid_rect_height * row) + tile_yoffset;

        // build new instance of a tile object
        String tile_name = getIdentifier().toString() + "tile" + row + col;

        Hashtable tile = new Hashtable();
        tile.put( "parent_id", well_viewport_name);
        tile.put( "name", tile_name);
        tile.put( "type", "tile");
        tile.put( "x", new Integer (xpos));
        tile.put( "y", new Integer (ypos));
        tile.put( "width", new Integer (tile_width));
        tile.put( "height", new Integer (tile_height));
        tile.put( "tile", t);
//      tile.put( "fill", color);
        tile.put( "border", "white"); // should be set by default

        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectCreatedSelector );
        Enumeration e = tile.keys();
        while( e.hasMoreElements() )
        {
            String key = (String)e.nextElement();
            n.addParameter( key, tile.get(key) );
        }

        tile_objects.set_matrix_element( row, col, tile );

        sendDefault(n);
    }


    protected void draw_grid_line( int x, int y, int deltax, int deltay )
    {
        // build new instance of a line object
        String line_name = getIdentifier().toString() + "line" + line_id;
        line_id++;

        Hashtable line = new Hashtable();
        line.put( "parent_id", well_viewport_name);
        line.put( "name", line_name);
        line.put( "type", "line");
        line.put( "x1", new Integer (x));
        line.put( "y1", new Integer (y));
        line.put( "x2", new Integer (x + deltax));
        line.put( "y2", new Integer (y + deltay));
        line.put( "color", "yellow");

        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectCreatedSelector );
        Enumeration e = line.keys();
        while( e.hasMoreElements() )
        {
            String key = (String)e.nextElement();
            n.addParameter( key, line.get(key) );
        }

        // keep track of this new object
        grid_lines.addElement( line );

        sendDefault(n);
    }


    protected void erase_tile( int row, int col )
    {
        Hashtable tile = (Hashtable)tile_objects.get_matrix_element(row, col);

        if( tile != null )
        {
            NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectDestroyedSelector );
            Enumeration e = tile.keys();
            while( e.hasMoreElements() )
            {
                String key = (String)e.nextElement();
                n.addParameter( key, tile.get(key) );
            }
            sendDefault(n);

            // delete tile object from tile_objects matrix
            tile_objects.remove_matrix_element(row, col);
        }
    }


    protected void draw_grid()
    {
        // draw rectangles
        for (int i = 0; i < num_rows; i++)
            for (int j = 0; j < num_cols; j++)
                draw_grid_rect(i,j);

        // draw colored U around well
        draw_grid_line (0, 0, 0, grid_rect_height * num_rows); // left
        draw_grid_line (num_cols * grid_rect_width, 0,         // right
                      0, grid_rect_height*num_rows);
        draw_grid_line (0, grid_rect_height * num_rows,        // base
                      grid_rect_width*num_cols, 0);
    }


    protected void erase_tiles()
    {
        for (int i = 0; i < num_rows; i++)
            for (int j = 0; j < num_cols; j++)
                // check if there's a tile and erase it
                if (tile_objects.get_matrix_element(i,j) != null)
                    erase_tile(i,j);
    }


    protected void draw_tiles( Well well_state )
    {
        for (int i = 0; i < num_rows; i++)
            for (int j = 0; j < num_cols; j++)
            {
                Tile t = well_state.get_tile(i,j);
                if (!t.isSpace())
                    draw_tile(i,j, t);
            }
    }


    protected void redisplay_well( Matrix well_state )
    {
        Tile oldTile;

        for (int i = 0; i < num_rows; i++)
        {
            for (int j = 0; j < num_cols; j++)
            {
                Tile t = (Tile) well_state.get_matrix_element(i,j);

                Hashtable tile_obj = (Hashtable)tile_objects.get_matrix_element(i,j);
                if( tile_obj != null)
                    oldTile = (Tile)tile_obj.get("tile");
                else
                    oldTile = new Tile(Tile.SpaceTile);

                if (oldTile.getId() != t.getId())
                {
                    erase_tile(i,j);
                    if (!t.isSpace())
                        draw_tile(i,j, t);
                }
            }
        }
    }
}
