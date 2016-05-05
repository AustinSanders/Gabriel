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
 * Class:           AlphaTile
 * Author:          Kari Nies
 * Date Created:    May 2, 1997
 * Date Modified:   May 2, 1997
 * Description:     Alphabet tile class
 */

package c2demo.klax;

class AlphaTile extends Tile {

   private static int numCharacters = 26;

   public AlphaTile (char c) {
      super((int)c - 96);
   }

   public String toString () {
      int id = this.getId();
      if (id <= 0)
         return super.toString();
      else {
         char c = (char)(id + 96);
         return new String("[" + c + "     ]");
      }
   }

   public int numberOfTileKinds () {
      return numCharacters;
   }
}
