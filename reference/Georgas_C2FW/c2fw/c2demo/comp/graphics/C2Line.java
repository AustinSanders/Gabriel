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

class C2Line extends C2GraphicsObject
{
   int _x1;
   int _y1;
   int _x2;
   int _y2;
   Color _c;
   protected String _id;

   public C2Line (C2Viewport v, String id, int x1, int y1, int x2, int y2, Color c)
   {
      _x1 = x1;
      _y1 = y1;
      _x2 = x2;
      _y2 = y2;
      _c = c;
      _id = id;

      paint_object (v);
      v.addGraphicsObject(this);
   }

   public C2Line (C2Panel p, String id, int x1, int y1, int x2, int y2, Color c)
   {
      _x1 = x1;
      _y1 = y1;
      _x2 = x2;
      _y2 = y2;
      _c = c;
      _id = id;

      paint_object (p);
      p.addGraphicsObject(this);
   }

   public String getID()
   {
      return _id;
   }


   public void setID(String new_value)
   {
      _id = new_value;
   }


   public void setX1 (int new_value)
   {
      _x1 = new_value;
   }

   public void setY1 (int new_value)
   {
      _y1 = new_value;
   }

   public void setX2 (int new_value)
   {
      _x2 = new_value;
   }

   public void setY2 (int new_value)
   {
      _y2 = new_value;
   }

   public void setColor (Color new_value)
   {
      _c = new_value;
   }


   public void paint_object (C2Viewport v)
   {
      Graphics g = v.getGraphics();
      g.setColor(_c);
      g.drawLine (_x1, _y1, _x2, _y2);
   }

   public void paint_object (C2Panel p)
   {
      Graphics g = p.getGraphics();
      g.setColor(_c);
      g.drawLine (_x1, _y1, _x2, _y2);
   }
}
