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
 * Description:     Tile class,  This class is not declared abstract
 *                  so that space tiles and empty tiles can be
 *                  constructed by the super class.
 */


package c2demo.klax;

import java.io.*;

public class Tile implements Cloneable, java.io.Serializable{

   private int tileId;

   public Tile (int id) {
      setId(id);
   }

   public Object clone() {
     try {
       return super.clone();
     } catch (CloneNotSupportedException e) {
       System.err.println(e.toString());
       e.printStackTrace();
       return null;
     }
   }

   public final static int EmptyTile = -1;  // -1 is always the empty tile
   public final static int SpaceTile = 0;   // 0 is always the space tile

   public String EmptyTileString = new String("empty");
   public String SpaceTileString = new String("space");


   /**
    * Overrides Object.toString,  Should be overidded by subclasses.
    */
   public String toString() {
      if (this.tileId == 0)
	 return SpaceTileString;
      else
	 if (this.tileId == -1)
	    return EmptyTileString;
	 else
	    throw new BadTileIdException();
   }

   /**
    * Returns the number of different tile kinds (excluding
    * space and empty.  Should be overidden by subclasses.
    */
   public int numberOfTileKinds () {
      return 0;
   }

   /**
    * Returns whether the tiles share the same tile id.
    * Note that two tiles of different subclasses may be
    * equal.
    */
   public boolean equal (Tile t) {
      return (tileId == t.getId());
   }

   /**
    * Returns the tile id.
    */
   final public int getId () {
      return tileId;
   }

   /**
    * Sets the tile id.  Makes sure that the range is correct
    * for the given subclass.
    */
   final public void setId (int id) {
      if ((id < -1) || (id > this.numberOfTileKinds()))
	 throw new BadTileIdException();
      tileId = id;
   }

   /**
    * Returns if the tile is a space
    */
   final public boolean isSpace () {
      return tileId == SpaceTile;
   }

   /**
    * Returns if the tile is empty
    */
   final public boolean isEmpty () {
      return tileId == EmptyTile;
   }

}
