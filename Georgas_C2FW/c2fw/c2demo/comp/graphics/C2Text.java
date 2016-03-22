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

class C2Text extends C2GraphicsObject
{
   int _x;
   int _y;
   int _size;
   String _message;
   String _font;
   String _style;
   Color _c;
   protected String _id;

   public C2Text (C2Viewport v, String id, int x, int y, String message,
                  String font, String style, int size, Color c)
   {
       _id = id;
      displayString (v, x, y, message, font, style, size, c);
   }

   public C2Text (C2Panel p, String id, int x, int y, String message,
                  String font, String style, int size, Color c)
   {
      _id = id;
      displayString (p, x, y, message, font, style, size, c);
   }

   private void displayString (C2Viewport v, int x, int y,
                               String message, String font,
                               String style, int size, Color c)
   {
      _x = x;
      _y = y;
      _message = message;
      _font = font;
      _style = style;
      _size = size;
      _c = c;

      paint_object (v);
      v.addGraphicsObject(this);
   }

   public String getID()
   {
      return _id;
   }


   public void setID(String new_value)
   {
      _id = new_value;
   }


   public void setValue (String new_value)
   {
      _message = new_value;
   }

   public void setFont (String new_value)
   {
      _font = new_value;
   }

   public void setStyle (String new_value)
   {
      _style = new_value;
   }

   public void setX (int new_value)
   {
      _x = new_value;
   }

   public void setY (int new_value)
   {
      _y = new_value;
   }

   public void setSize (int new_value)
   {
      _size = new_value;
   }

   public void setColor (Color new_value)
   {
      _c = new_value;
   }

   private void displayString (C2Panel p, int x, int y,
                               String message, String font,
                               String style, int size, Color c)
   {
      _x = x;
      _y = y;
      _message = message;
      _font = font;
      _style = style;
      _size = size;
      _c = c;

      paint_object (p);
      p.addGraphicsObject(this);
   }

   public void paint_object (C2Viewport v)
   {
      Graphics g = v.getGraphics();

      if (_style.equalsIgnoreCase("plain"))
         g.setFont(new Font(_font, Font.PLAIN, _size));
      else if (_style.equalsIgnoreCase("bold"))
         g.setFont(new Font(_font, Font.BOLD, _size));
      else // style.equalsIgnoreCase("italic")
         g.setFont(new Font(_font, Font.ITALIC, _size));

      g.setColor(_c);
      g.drawString(_message, _x, _y);
   }

   public void paint_object (C2Panel p)
   {
      Graphics g = p.getGraphics();

      if (_style.equalsIgnoreCase("plain"))
         g.setFont(new Font(_font, Font.PLAIN, _size));
      else if (_style.equalsIgnoreCase("bold"))
         g.setFont(new Font(_font, Font.BOLD, _size));
      else // style.equalsIgnoreCase("italic")
         g.setFont(new Font(_font, Font.ITALIC, _size));

      g.setColor(_c);
      g.drawString(_message, _x, _y);
   }
}
