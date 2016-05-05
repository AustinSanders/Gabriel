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

class C2Rectangle extends C2GraphicsObject
{
   int _x;
   int _y;
   int _width;
   int _height;
   Color _fill;
   Color _border;
   protected String _id;

   public C2Rectangle (C2Viewport v, String id, int x, int y, int width,
                       int height, Color fill, Color border)
   {
      _x = x;
      _y = y;
      _width = width;
      _height = height;
      _fill = fill;
      _border = border;
      _id = id;

      paint_object(v);
      v.addGraphicsObject(this);
   }

   public C2Rectangle (C2Panel p, String id, int x, int y, int width,
                       int height, Color fill, Color border)
   {
      _x = x;
      _y = y;
      _width = width;
      _height = height;
      _fill = fill;
      _border = border;
      _id = id;

      paint_object(p);
      p.addGraphicsObject(this);
   }


   // this constructor will be implemented once we define patterns
   public C2Rectangle (C2Viewport v, int x, int y, int width,
                       int height, Color fill, Color border,
                       int pattern)
   {
   }

   public C2Rectangle (C2Panel p, int x, int y, int width,
                       int height, Color fill, Color border,
                       int pattern)
   {
   }

   public String getID()
   {
      return _id;
   }


   public void setID(String new_value)
   {
      _id = new_value;
   }


   public void setX (int new_value)
   {
      _x = new_value;
   }

   public void setY (int new_value)
   {
      _y = new_value;
   }


   public void setWidth (int new_value)
   {
      _width = new_value;
   }

   public void setHeight (int new_value)
   {
      _height = new_value;
   }



   public void setFill (Color new_value)
   {
      _fill = new_value;
   }


   public void setBorder (Color new_value)
   {
      _border = new_value;
   }

   public void paint_object (C2Viewport v)
   {
      Graphics g = v.getGraphics();
      g.setColor(_fill);
      g.fillRect (_x, _y, _width, _height);
      g.setColor(_border);
      g.drawRect (_x, _y, _width, _height);
   }

   public void paint_object (C2Panel p)
   {
      Graphics g = p.getGraphics();
      g.setColor(_fill);
      g.fillRect (_x, _y, _width, _height);
      g.setColor(_border);
      g.drawRect (_x, _y, _width, _height);
   }
}
