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
 * Class:           Tile
 * Author:          Kari Nies
 * Date Created:    April 29, 1997
 * Date Modified:   April 29, 1997
 * Description:     DroppedTile class, represents a tile that has been
 *                  dropped from the chute.  Includes the tile and the
 *                  column from which it was dropped.
 */


package c2demo.klax;


public class DroppedTile {

   // tile that has been dropped from the chute
   protected static Tile droppedTile;
   // location (col) from which it was dropped
   protected static int location;

   /**
    * DroppedTile Constructor
    */
   public DroppedTile (int loc, Tile dtile) {
      location = loc;
      droppedTile = dtile;
   }

   /**
    * Overrides Object.toString
    */
   public String toString() {
      return "(" + location + "," + droppedTile + ")\n";
   }

   /**
    * Accesses the location
    */
    public int location ()
    {
       return location;
    }

   /**
    * Accesses the tile
    */
    public Tile tile ()
    {
       return droppedTile;
    }
}
