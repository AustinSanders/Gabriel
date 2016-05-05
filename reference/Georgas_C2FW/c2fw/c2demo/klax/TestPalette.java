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

import java.util.*;
import java.io.*;

public class TestPalette extends Object
{
   static public void main(String argv[])
   {
      ColorTile t;
      Palette p = new Palette ();
      System.out.println (p.toString());

      t = new ColorTile (1);
      p.AddTileToPalette (t);
      t = new ColorTile (2);
      p.AddTileToPalette (t);
      t = new ColorTile (3);
      p.AddTileToPalette (t);
      t = new ColorTile (4);
      p.AddTileToPalette (t);
      t = new ColorTile (-1);
      p.AddTileToPalette (t);
      System.out.println (p.toString());

      t = new ColorTile (5);
      p.AddTileToPalette (t);
      System.out.println (p.toString());

      t = (ColorTile)p.DropTileIntoWell ();
      System.out.println ("Dropped Tile: " + t.toString() + "\n");
      System.out.println (p.toString());

      t = new ColorTile (5);
      p.AddTileToPalette (t);
      System.out.println (p.toString());

      System.out.println ("Number of Tiles: " + p.HowManyTiles());
      System.out.println ("Palette Capacity: " + p.GetPaletteCapacity());

      System.out.println ("Palette Position: " + p.GetPalettePosition());
      System.out.println ("Palette Position: " + p.MovePalette(1));
      System.out.println ("Palette Position: " + p.MovePalette(5));
      System.out.println ("Palette Position: " + p.MovePalette(-10));

      p.ResetPalette();
      System.out.println (p.toString());
   }
}
