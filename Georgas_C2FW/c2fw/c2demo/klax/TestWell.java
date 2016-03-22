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

public class TestWell extends Object
{
   static public void main(String argv[])
   {
      int value = 10;
      Well w = new Well (5,5);
      ColorTile t = new ColorTile (1);

      System.out.println (w.toString());

      for (int i=0; i<5; i++)
         for (int j=0; j<5; j++)
         {
            if (value >= t.numberOfTileKinds())
               value = 0;
            t = new ColorTile (++value);
            w.add_tile (j,t);
            w.advance_tiles ();
         }

      System.out.println (w.toString());

      w.remove_horizontal (4,0,2);
      w.remove_vertical (2,3,2);
      System.out.println (w.toString());

      w.collapse_tiles ();
      System.out.println (w.toString());

      w.remove_diagonal_up (4,0,5);
      System.out.println (w.toString());

      w.collapse_tiles ();
      System.out.println (w.toString());

      w.remove_diagonal_down (2,1,3);
      System.out.println (w.toString());

      w.collapse_tiles ();
      System.out.println (w.toString());
   }
}
