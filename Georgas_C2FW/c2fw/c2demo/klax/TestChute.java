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

import java.util.Vector;

public class TestChute {

   static public void main(String argv[]) {
      DroppedTile dt;
      int i;

      Chute klaxChute = new Chute(5,5);
      System.out.println (klaxChute.toString());

      for (i = 0; i < 5; i++) {
         dt = klaxChute.placeTile(i, new ColorTile(ColorTile.red));
         if (dt != null)
            System.out.println (dt.toString());
         System.out.println (klaxChute.toString());
      }

      for (i = 0; i < 5; i++) {
         dt = klaxChute.advanceTiles();
         if (dt != null)
            System.out.println (dt.toString());
         System.out.println (klaxChute.toString());
      }
   }
}
