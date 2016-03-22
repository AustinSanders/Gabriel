/*
   Copyright (c) 1995, 1996 Regents of the University of California.
   All rights reserved.

   This software was developed by the Arcadia project
   at the University of California, Irvine.

   Redistribution and use in source and binary forms are permitted
   provided that the above copyright notice and this paragraph are
   duplicated in all such forms and that any documentation,
   advertising materials, and other materials related to such
   distribution and use acknowledge that the software was developed
   by the University of California, Irvine.  The name of the
   University may not be used to endorse or promote products derived
   from this software without specific prior written permission.
   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
   IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
   WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
*/


package c2demo.comp.graphics;

import java.lang.*;
import java.util.*;
import java.awt.*;
import c2demo.comp.graphics.*;

class C2TextField extends TextField
{
   protected String _id;

   // adding a TextField to a viewport
   public C2TextField (String id, C2Viewport v, int x, int y, int width,
                       int height, Color foreground, Color background,
                       String initial)
   {
      v.add(this);
      setText(initial);
      setLocation(x, y);
      setSize(width, height);
      setBackground(background);
      setForeground(foreground);
			v.repaint();

      _id = id;
   }

   // adding a TextField to a panel
   public C2TextField (String id, C2Panel p, int x, int y, int width,
                       int height, Color foreground, Color background,
                       String initial)
   {
      setText(initial);
      setLocation(x, y);
      setSize(width, height);
      setBackground(background);
      setForeground(foreground);
      p.add(this);

      _id = id;
   }

   public String getID()
   {
      return _id;
   }

   public String get ()
   {
      return getText();
   }

   public void set (String s)
   {
      setText(s);
   }
}
