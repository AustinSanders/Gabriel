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
import c2.fw.*;

class C2Viewport extends Frame
{
   protected Vector graphics_objects = new Vector();
   protected Color back_color;
   protected String id;
   protected GraphicsBinding gb;

	protected int width;
	protected int height;

   public C2Viewport (String vport_id, int x, int y, int width, int height,
                      String title, Color foreground, Color background,
                      GraphicsBinding binding)
   {
      super(title);

      setBackground (background);
      setForeground (foreground);
      setLayout (null);  // default layout; may change if needed
      setLocation(x, y);
			this.width = width;
			this.height = height;
			setSize(width, height);
      pack();
      setVisible(true);

      id = vport_id;
      back_color = background;
      gb = binding;
   }

	//These four functions work around an...ahemmm....inconsistency
	//in certain JVM's where pack() will reduce a window to 0,0 if
	//you don't specify :)

	public Dimension getMinimumSize(){
		return minimumSize();
	}

	public Dimension minimumSize(){
		return new Dimension(width, height);
	}

	public Dimension getPreferredSize(){
		return minimumSize();
	}

	public Dimension preferredSize(){
		return new Dimension(width, height);
	}

   public void killViewport ()
   {
      setVisible(false);
      dispose();
   }

   public String getID()
   {
      return id;
   }

   public Color backgroundColor ()
   {
      return back_color;
   }

   public int numGraphicsObjects ()
   {
      return graphics_objects.size();
   }

   public C2GraphicsObject getGraphicsObject (int location)
   {
      return (C2GraphicsObject)graphics_objects.elementAt (location);
   }

   public void removeGraphicsObject (C2GraphicsObject e)
   {
      graphics_objects.removeElement(e);
      Graphics g = getGraphics ();
      Rectangle r = getBounds();
      g.setColor (back_color);
      g.fillRect (0, 0, r.width, r.height);
      paint (g);
   }

   public void removeGraphicsObject (int position)
   {
      graphics_objects.removeElementAt(position);
      Graphics g = getGraphics ();
      Rectangle r = getBounds();
      g.setColor (back_color);
      g.fillRect (0, 0, r.width, r.height);
      paint (g);
   }

   public void addGraphicsObject (C2GraphicsObject e)
   {
      graphics_objects.addElement(e);
   }

   public void clearAll()
   {
      Graphics g = getGraphics ();
      graphics_objects.removeAllElements();
      removeAll();
      Rectangle r = getBounds();
      g.setColor (back_color);
      g.fillRect (0, 0, r.width, r.height);
   }

   public java.awt.Component findComponent (String id)
   {
      java.awt.Component[] comps = getComponents();
      int num_comps = getComponentCount();

      int i=0;
      boolean found = false;

      while ((i < num_comps) && (!found))
      {
         if (comps[i].getClass().getName().equals("c2demo.comp.graphics.C2Button"))
         {
            if (id.equals(((C2Button)comps[i]).getID()))
               found = true;
            else
               i++;
         }
         else if (comps[i].getClass().getName().equals("c2demo.comp.graphics.C2TextField"))
         {
            if (id.equals(((C2TextField)comps[i]).getID()))
               found = true;
            else
               i++;
         }
         else if (comps[i].getClass().getName().equals("c2demo.comp.graphics.C2Panel"))
         {
            if (id.equals(((C2Panel)comps[i]).getID()))
               found = true;
            else
               i++;
         }
      }

      return comps[i];
   }

   public void paint (Graphics g)
   {
      for (int i=0; i < graphics_objects.size(); i++)
          ((C2GraphicsObject)graphics_objects.elementAt(i)).paint_object(this);
   }

   /////////////////////
   // handling events //
   /////////////////////
   public boolean handleEvent (Event e)
   {
      if (e.id == Event.WINDOW_DESTROY){
        //System.exit(0);
				gb.shutdown();
			}
      return super.handleEvent(e);
   }

   public boolean action (Event e, Object arg)
   {
      NamedPropertyMessage req;

      if (e.target.getClass().getName().equals("c2demo.comp.graphics.C2Button"))
      // a button has been clicked.  Find the parent and handle the click
      {
         req = c2.legacy.Utils.createC2Request("AcceptEvent");
         req.addParameter ("button", ((C2Button)e.target).getLabel());

         Container c = ((C2Button)e.target).getParent();
         if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
            req.addParameter ("parent_id", ((C2Viewport)c).getID());
         else
            req.addParameter ("parent_id", ((C2Panel)c).getID());

         gb.sendDefault(req);
         return true;
      }
      else if (e.target.getClass().getName().equals("c2demo.comp.graphics.C2TextField"))
      // text has been entered into a text field.
      {
         req = c2.legacy.Utils.createC2Request("AcceptEvent");
         req.addParameter ("text_field", ((C2TextField)e.target).getID());

         Container c = ((C2TextField)e.target).getParent();
         if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
            req.addParameter ("parent_id", ((C2Viewport)c).getID());
         else
            req.addParameter ("parent_id", ((C2Panel)c).getID());

         req.addParameter ("value",  gb.getTextField((C2TextField)e.target));

         gb.sendDefault(req);
         return true;
      }
      else return false;
   }

   public boolean keyDown (Event e, int key)
   {
      NamedPropertyMessage req = c2.legacy.Utils.createC2Request("AcceptEvent");
      // handle original C++ implementation cluge
      if (key == Event.LEFT)
         req.addParameter("c2GkeyEvent", "<-");
      if (key == Event.RIGHT)
         req.addParameter("c2GkeyEvent", "->");
      if (key == Event.UP)
         req.addParameter("c2GkeyEvent", "-^");
      if (key == Event.DOWN)
         req.addParameter("c2GkeyEvent", ",-");
      else if (key == Event.END)
         req.addParameter("c2GkeyEvent", "end");
      else // all other keys
         req.addParameter ("otherC2GkeyEvent", new Integer(key));

      req.addParameter ("parent_id", this.getID());

      gb.sendDefault(req);
      return false;
   }

}
