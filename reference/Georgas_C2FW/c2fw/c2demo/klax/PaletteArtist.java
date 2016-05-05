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

public class PaletteArtist extends c2.legacy.AbstractC2Brick
{

   protected KlaxSelectorTable kst;
   protected Stack tile_objects;
   protected Hashtable palette;
   protected Hashtable palette_label;

   protected int num_cols;
   protected int current_col;

   protected String palette_viewport_name;
   protected int palette_viewport_xpos;
   protected int palette_viewport_ypos;
   protected String palette_viewport_fg = "white";
   protected String palette_viewport_bg = "darkGray";

   protected int grid_width       = 50;
   protected int grid_height      = 30;

   protected int left_direction   = -1;
   protected int right_direction  = 1;

   protected int tile_width       = 25;
   protected int tile_height      = 15;
   protected int tile_xoffset     = tile_width/2;
   protected int tile_yoffset     = tile_height;

   protected int palette_width    = 50;
   protected int palette_height   = 20;
   protected int palette_center   = palette_width/2;



    // category: constructors
    public PaletteArtist( String _name, int _cols, int xpos, int ypos)
    {
        super( new SimpleIdentifier(_name) );
        create( _name, _cols, xpos, ypos );
    }


    public PaletteArtist( Identifier id )
    {
        super(id);
        create( id.toString(), 5, 10, 210);
    }


    private void create(String _name, int _cols, int xpos, int ypos)
    {
        //super.create(_name, FIFOPort.classType());
        current_col = 0;
        num_cols = _cols;
        palette_viewport_xpos = xpos;
        palette_viewport_ypos = ypos;
        tile_objects = new Stack ();
    }


    public void destroy()
    {
    }


    public void init()
    {
    }


    public void begin()
    {
        create_viewport ();
        create_palette();
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
        String message_name = r.getName();
        if (message_name.equals(KlaxSelectorTable.AcceptEventSelector))
        {
            String key = (String)r.getParameter("c2GkeyEvent");
            if (key != null)
            {
                if (key.equals(",-"))
                {
                    NamedPropertyMessage new_r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.DropPaletteTileSelector );
                    sendDefault(new_r);
                }
                else if (key.equals("<-"))
                {
                    // negative direction for left
                    NamedPropertyMessage new_r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.MovePaletteSelector );
                    new_r.addParameter ("direction", new Integer (-1));
                    sendDefault(new_r);
                }
                else if (key.equals("->"))
                {
                    // positive direction for right
                    NamedPropertyMessage new_r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.MovePaletteSelector );
                    new_r.addParameter ("direction", new Integer (1));
                    sendDefault(new_r);
                }
                else if (key.equals("end"))
                {
                    // end key terminates the application
                    NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( "ApplicationTerminated" );
                    sendDefault(n);
                }
            }
        }
    }


    public synchronized void handleNotification( NamedPropertyMessage n )
    {
        String message_name = n.getName();

        if (message_name.equals(KlaxSelectorTable.TileAddedToPaletteSelector))
        {
            Tile t = (Tile)n.getParameter("tile");
            Hashtable new_tile = draw_tile(t);
            tile_objects.push(new_tile);

            NamedPropertyMessage new_n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectCreatedSelector );
            Enumeration e = new_tile.keys();
            while( e.hasMoreElements() )
            {
                String key = (String)e.nextElement();
                new_n.addParameter( key, new_tile.get(key) );
            }
            sendDefault(new_n);
        }
        else if (message_name.equals(KlaxSelectorTable.PaletteTileDroppedSelector))
        {
            Hashtable tile = (Hashtable)tile_objects.pop();
            erase_tile(tile);
        }
        else if (message_name.equals(KlaxSelectorTable.PaletteLocationSelector))
        {
            int loc = ((Integer)n.getParameter("location")).intValue();
            if (current_col != loc)
            {
                move_to_column(loc);
                current_col = loc;
            }
        }
    }


    protected void create_viewport()
    {
        palette_viewport_name = getClass().getName() + "." +
                              getIdentifier().toString() + "." + "vport1";
        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ViewportCreatedSelector );
        n.addParameter ("x", new Integer(palette_viewport_xpos));
        n.addParameter ("y", new Integer (palette_viewport_ypos));

        // expressions needed for below values
        n.addParameter ("width", new Integer (251));
        n.addParameter ("height", new Integer (151));
        n.addParameter ("foreground", palette_viewport_fg);
        n.addParameter ("background", palette_viewport_bg);
        n.addParameter ("title", palette_viewport_name);
        n.addParameter ("id", palette_viewport_name);
        sendDefault(n);
    }


    protected void create_palette()
    {
        String palette_name = getIdentifier().toString() + "palette";

        palette = new Hashtable ();
        palette.put ("parent_id", palette_viewport_name);
        palette.put ("name", palette_name);
        palette.put ("type", "rectangle");
        palette.put ("x", new Integer (0));
        palette.put ("y", new Integer (6 * tile_height));
        palette.put ("width", new Integer (palette_width));
        palette.put ("height", new Integer (palette_height));
        palette.put ("fill", "blue");
        palette.put ("border", "black");

        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectCreatedSelector );
        Enumeration e = palette.keys();
        while( e.hasMoreElements() )
        {
            String key = (String)e.nextElement();
            n.addParameter( key, palette.get(key) );
        }
        sendDefault(n);
    }


    protected void erase_tile(Hashtable tile)
    {
        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectDestroyedSelector );
        Enumeration e = tile.keys();
        while( e.hasMoreElements() )
        {
            String key = (String)e.nextElement();
            n.addParameter( key, tile.get(key) );
        }
        sendDefault(n);
    }


    protected void move_to_column(int loc)
    {
        int palette_x = loc * grid_width;
        int current_center = palette_x + palette_center;
        int tile_x = current_center - tile_xoffset;

        Hashtable tile;

        // change the location of each element on the tile stack
        for (int i = 0; i < tile_objects.size(); i++)
        {
            tile = (Hashtable)tile_objects.elementAt(i);
            tile.put ("x", new Integer (tile_x));

            NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectModifiedSelector );
            Enumeration e = tile.keys();
            while( e.hasMoreElements() )
            {
                String key = (String)e.nextElement();
                n.addParameter( key, tile.get(key) );
            }
            sendDefault(n);
        }

        palette.put ("x", new Integer (palette_x));
        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectModifiedSelector );
        Enumeration e = palette.keys();
        while( e.hasMoreElements() )
        {
            String key = (String)e.nextElement();
            n.addParameter( key, palette.get(key) );
        }
        sendDefault(n);
    }


    protected Hashtable draw_tile (Tile t)
    {
        int tile_pos = (5 - tile_objects.size());
        int tile_y = tile_height * tile_pos;

        int current_center = (((Integer)palette.get("x")).intValue() +
                           palette_center);
        int tile_x = current_center - tile_xoffset;

        // build new instance of a tile object
        String tile_name = getIdentifier().toString() + "PaletteArtistTile" +
                         tile_objects.size();

        Hashtable tile = new Hashtable ();
        tile.put ("parent_id", palette_viewport_name);
        tile.put ("name", tile_name);
        tile.put ("type", "tile");
        tile.put ("x", new Integer (tile_x));
        tile.put ("y", new Integer (tile_y));
        tile.put ("width", new Integer (tile_width));
        tile.put ("height", new Integer (tile_height));
        tile.put ("tile", t);
        //      tile.put ("fill", color);
        tile.put ("border", "white"); // should be set by default

        return tile;
    }
}
