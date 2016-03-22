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

class C2Button extends Button
{
   protected String id;

   // adding a button to a viewport
   public C2Button (C2Viewport v, int x, int y, int width, int height,
                   String label, Color foreground, Color background)
   {
      v.add(this);
      setLabel(label);
      setSize(width, height);
      setBackground (background);
      setForeground(foreground);
      setLocation(x, y);

      id = label;
   }

   // adding a button to a panel
   public C2Button (C2Panel p, int x, int y, int width, int height,
                   String label, Color foreground, Color background)
   {
      setLabel(label);
      setSize(width, height);
      setBackground(background);
      setForeground(foreground);
      setLocation(x, y);
      p.add(this);

      id = label;
   }

   public String getID()
   {
      return id;
   }
}
