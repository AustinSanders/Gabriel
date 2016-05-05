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

/**
 * Class:           ColorTile
 * Author:          Kari Nies
 * Date Created:    April 30, 1997
 * Date Modified:   April 30, 1997
 * Description:     Color tile class
 */


package c2demo.klax;

class ColorTile extends Tile {

   public final static int numColors = 8;

   public final static int red    = 1;
   public final static int blue   = 2;
   public final static int green  = 3;
   public final static int yellow = 4;
   public final static int cyan  = 5;
   public final static int magenta = 6;
   public final static int orange = 7;
   public final static int gray   = 8;

   protected static String[] colorArray =
      {"space", "red", "blue", "green", "yellow", "cyan", "magenta",
       "orange", "gray"};

   public ColorTile (int color) {
      super(color);
   }

   public String toString () {
      int id = this.getId();
      if (id <= 0)
	 return super.toString();
      else
         return colorArray[id];
   }

   public int numberOfTileKinds () {
      return numColors;
   }
}
