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
import c2.legacy.*;

/**
 * GraphicsBinding class.<p>
 *
 * GraphicsBinding accepts notifications from C2 components above it and
 * reifies them as calls to Java AWT methods.  It also transforms
 * user-generated events into C2 requests, which are sent up an
 * architecture.<p>
 *
 * The binding currently supports manipulation of AWT frames, panels,
 * buttons, text fields, arcs, lines, ovals, rectangles, and text
 * strings.  The binding is extensible and is intended to support additional
 * AWT functionality. <p>
 *
 * <HR>
 * <h3>GraphicsBinding's C2 Message Interface</h3>
 *
 * GraphicsBinding's methods should not be invoked directly in a C2
 * architecture.  Instead, the binding's functionality is accessed via C2
 * notifications.  In response, the binding invokes Java AWT methods and
 * generates C2 requests, which it then sends to components above
 * it in the architecture.<p>
 *
 * NOTE: <i>Italicized message parameters have a default value and are
 * hence optional.</i><p>
 *
 * <pre>
 *     <b>Incoming Notifications</b>
 *         ApplicationTerminated ();
 *         ViewportCreated       (Integer x, Integer y,
 *                                Integer width, Integer height,
 *                                String title, String id,
 *                                String foreground, String background);
 *         ViewportDestroyed     (String id);
 *         ViewportCleared       (String id);
 *         PanelAdded            (Integer x, Integer y,
 *                                Integer width, Integer height,
 *                                String foreground, String background,
 *                                String id, String parent_id);
 *         PanelCleared          (String id);
 *         ButtonAdded           (Integer x, Integer y,
 *                                Integer width, Integer height,
 *                                String foreground, String background,
 *                                String label, String parent_id);
 *         TextFieldAdded        (Integer x, Integer y,
 *                                Integer width, Integer height,
 *                                String foreground, String background,
 *                                String label, <i>String value</i>,
 *                                String parent_id);
 *         TextFieldSet          (String label, String value,
 *                                String parent_id);
 *         TextFieldCleared      (String label, String parent_id);
 *         c2Grectangle          (Integer x, Integer y,
 *                                Integer width, Integer height,
 *                                String fill, String border,
 *                                String parent_id);
 *         c2Gline               (Integer x1, Integer y1,
 *                                Integer x2, Integer y2,
 *                                String color, String parent_id);
 *         c2Garc                (Integer x, Integer y,
 *                                Integer width, Integer height,
 *                                Integer angle1, Integer angle2,
 *                                String fill, String border,
 *                                String parent_id);
 *         c2Goval               (Integer x, Integer y,
 *                                Integer width, Integer height,
 *                                String fill, String border,
 *                                String parent_id);
 *         c2Gtext               (Integer x, Integer y,
 *                                String value, String font,
 *                                String color, <i>String style</i>,
 *                                <i>Integer size</i>, String parent_id);
 *     <b>Outgoing Requests</b>
 *         AcceptEvent           (String button, String parent_id);
 *         AcceptEvent           (String text_field, String value,
 *                                String parent_id);
 *         AcceptEvent           (String c2GkeyEvent, String parent_id);
 *         AcceptEvent           (Integer otherC2GkeyEvent, String parent_id);
 *     <b>Incoming Requests</b>
 *         none
 *     <b>Outgoing Notifications</b>
 *         none
 * </pre>
 *
 * @version
 */


public class GraphicsBinding extends c2.legacy.AbstractC2Brick{
	/** All the C2 Viewports maintained by the GraphicsBinding.
	 */
	protected Vector vports = new Vector ();
	/** All the C2 Panels maintained by the GraphicsBinding.
	 */
	protected Vector panels = new Vector ();

	// category: constructor
	public GraphicsBinding (String name){
		super(new SimpleIdentifier(name));
		//System.out.println("GraphicsBinding created: " + name);
	}

	public GraphicsBinding(Identifier id){
		super(id);
		//System.out.println("GraphicsBinding created: " + id);
	}

	public void init(){
	}

	public void begin(){
		//System.out.println("GraphicsBinding::begin");
	}

	public void end(){
	}

	public void destroy(){
	}

	//these two must be made public because of callbacks from C2 graphics components

	public void sendDefault(Message m){
		if(Utils.isC2Notification(m)){
			sendToAll(m, bottomIface);
		}
		else if(Utils.isC2Request(m)){
			sendToAll(m, topIface);
		}
	}



	/////////////////////////////////////
	// category: notification handling //
	/////////////////////////////////////

	// given a String representing a Color, return the Color
	private Color toColor (String c)
	{
		if (c.equals("black"))
			return Color.black;
		else if (c.equals("blue"))
			return Color.blue;
		else if (c.equals("cyan"))
			return Color.cyan;
		else if (c.equals("darkGray"))
			return Color.darkGray;
		else if (c.equals("gray"))
			return Color.gray;
		else if (c.equals("green"))
			return Color.green;
		else if (c.equals("lightGray"))
			return Color.lightGray;
		else if (c.equals("magenta"))
			return Color.magenta;
		else if (c.equals("orange"))
			return Color.orange;
		else if (c.equals("pink"))
			return Color.pink;
		else if (c.equals("red"))
			return Color.red;
		else if (c.equals("white"))
			return Color.white;
		else if (c.equals("yellow"))
			return Color.yellow;
		else return Color.white;  // white is the default
	}

	// given a viewport ID, it returns the viewport
	private Container containerByID (String id)
	{
		int i;
		boolean found = false;

		i=0;
		while ((i < vports.size()) && (!found))
		{
			// find the vieport with given id
			if (((C2Viewport)vports.elementAt(i)).getID().equals(id))
				found = true;
			else
				i++;
		}

		if (found)
			return (Container)vports.elementAt(i);
		else // the id belongs to a panel
		{
			i=0;
			while ((i < panels.size()) && (!found))
			{
				if (((C2Panel)panels.elementAt(i)).getID().equals(id))
					found = true;
				else
					i++;
			}
			return (Container)panels.elementAt(i);
		}
	}

	public synchronized void handle(Message m){
		//System.out.println("Got message! " + m);
		if(Utils.isC2Notification(m)){
			handleNotification((NamedPropertyMessage)m);
		}
		else if(Utils.isC2Request(m)){
			handleRequest((NamedPropertyMessage)m);
		}
	}

	/** Handles C2 notifications received from components above
	 * GraphicsBinding in an architecture by invoking AWT functions and
	 * issuing requests to above components in response to user events.
	 * @param n C2 notification.
	 */
	public synchronized void handleNotification(NamedPropertyMessage n){
		// handles messages from C2 components above

		//System.out.println("Got notification message! " + n);
		String message_name = n.getName();
        String obj_name = (String)n.getParameter("name");

		if (message_name.equalsIgnoreCase("ViewportCreated"))
		{
			//System.out.println("Got viewportcreated message! " + n);
			int x = ((Integer)n.getParameter("x")).intValue();
			int y = ((Integer)n.getParameter("y")).intValue();
			int width = ((Integer)n.getParameter("width")).intValue();
			int height = ((Integer)n.getParameter("height")).intValue();
			String title = (String)n.getParameter("title");
			String id = (String)n.getParameter("id");
			Color foreground = toColor((String)n.getParameter("foreground"));
			Color background = toColor((String)n.getParameter("background"));

			vports.addElement(createViewport (id, x, y, width, height, title,
				foreground, background, this));

		}
		else if (message_name.equalsIgnoreCase("ApplicationTerminated"))
		{
			C2Viewport v;

			while (!vports.isEmpty())
			{
				v = (C2Viewport)vports.firstElement();
				vports.removeElement(v);
				destroyViewport(v);
			}

			shutdown ();
		}
		else if (message_name.equalsIgnoreCase("ViewportDestroyed"))
		{
			try{
				C2Viewport v = (C2Viewport)containerByID((String)n.getParameter("id"));
				vports.removeElement(v);
				destroyViewport(v);
			}
			catch(ArrayIndexOutOfBoundsException aioobe){
				//This usually indicates that the viewport has already been
				//destroyed; just fail silently.
			}
		}
		else if (message_name.equalsIgnoreCase("ViewportCleared"))
		{
			C2Viewport v = (C2Viewport)containerByID((String)n.getParameter("id"));
			v.clearAll();
		}
		else if (message_name.equalsIgnoreCase("PanelAdded"))
		{
			int x = ((Integer)n.getParameter("x")).intValue();
			int y = ((Integer)n.getParameter("y")).intValue();
			int width = ((Integer)n.getParameter("width")).intValue();
			int height = ((Integer)n.getParameter("height")).intValue();
			Color foreground = toColor((String)n.getParameter("foreground"));
			Color background = toColor((String)n.getParameter("background"));
			String id = (String)n.getParameter("id");
			String parent_id = (String)n.getParameter("parent_id");

			C2Viewport v = (C2Viewport)containerByID(parent_id);
			panels.addElement(addPanel(id, v, x, y, width, height,
				foreground, background, this));
		}
		else if (message_name.equalsIgnoreCase("PanelCleared"))
		{
			C2Panel p = (C2Panel)containerByID((String)n.getParameter("id"));
			p.clearAll();
		}
        else if (message_name.equalsIgnoreCase("ObjectCreated"))
        {
            CreateObject(n);
        }
        else if (message_name.equalsIgnoreCase("ObjectDestroyed"))
        {
            boolean found = DestroyObject(n);
            if (!found)
                System.err.println ("Object to destroy not found!");
        }
        else if (message_name.equalsIgnoreCase("ObjectModified"))
        {
            boolean found = ModifyObject(n);
            if (!found)
                System.err.println ("Object to modify not found!");
        }
		else if (message_name.equalsIgnoreCase("ButtonAdded"))
		{
			int x = ((Integer)n.getParameter("x")).intValue();
			int y = ((Integer)n.getParameter("y")).intValue();
			int width = ((Integer)n.getParameter("width")).intValue();
			int height = ((Integer)n.getParameter("height")).intValue();
			Color foreground = toColor((String)n.getParameter("foreground"));
			Color background = toColor((String)n.getParameter("background"));
			String  id = (String)n.getParameter("label");
			String parent_id = (String)n.getParameter("parent_id");

			Container c = containerByID(parent_id);
			if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
				addButton((C2Viewport)c, x, y, width, height,
					id, foreground, background);
			else
				addButton((C2Panel)c, x, y, width, height,
					id, foreground, background);
		}
		else if (message_name.equalsIgnoreCase("TextFieldAdded"))
		{
			int x = ((Integer)n.getParameter("x")).intValue();
			int y = ((Integer)n.getParameter("y")).intValue();
			int width = ((Integer)n.getParameter("width")).intValue();
			int height = ((Integer)n.getParameter("height")).intValue();
			Color foreground = toColor((String)n.getParameter("foreground"));
			Color background = toColor((String)n.getParameter("background"));
			String id = (String)n.getParameter("label");
			String parent_id = (String)n.getParameter("parent_id");

			String value = (String)n.getParameter("value");
			if (value == null)
				value = "";

			Container c = containerByID(parent_id);
			if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
				addTextField (id, (C2Viewport)c, x, y, width, height,
					foreground, background, value);
			else
				addTextField (id, (C2Panel)c, x, y, width, height,
					foreground, background, value);
		}
		else if (message_name.equalsIgnoreCase("TextFieldSet"))
		{
			String id = (String)n.getParameter("label");
			String parent_id = (String)n.getParameter("parent_id");
			String value = (String)n.getParameter("value");

			C2TextField tf = null;
			Container c = containerByID(parent_id);
			if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
				tf = (C2TextField)((C2Viewport)c).findComponent(id);
			else
				tf = (C2TextField)((C2Panel)c).findComponent(id);

			setTextField (tf, value);
		}
		else if (message_name.equalsIgnoreCase("TextFieldCleared"))
		{
			String id = (String)n.getParameter("label");
			String parent_id = (String)n.getParameter("parent_id");

			C2TextField tf = null;
			Container c = containerByID(parent_id);

			if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
			{
				tf = (C2TextField)((C2Viewport)c).findComponent(id);
			}
			else
				tf = (C2TextField)((C2Panel)c).findComponent(id);

			eraseTextField (tf);
		}
		else if (message_name.equalsIgnoreCase("c2Grectangle"))
		{
			int x = ((Integer)n.getParameter("x")).intValue();
			int y = ((Integer)n.getParameter("y")).intValue();
			int width = ((Integer)n.getParameter("width")).intValue();
			int height = ((Integer)n.getParameter("height")).intValue();
			Color fill = toColor((String)n.getParameter("fill"));
			Color border = toColor((String)n.getParameter("border"));
			String parent_id = (String)n.getParameter("parent_id");

			Container c = containerByID(parent_id);
			if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
				rectangle ((C2Viewport)c, obj_name, x, y, width, height, fill, border);
			else
				rectangle ((C2Panel)c, obj_name, x, y, width, height, fill, border);
		}
		else if (message_name.equalsIgnoreCase("c2Gline"))
		{
			int x1 = ((Integer)n.getParameter("x1")).intValue();
			int y1 = ((Integer)n.getParameter("y1")).intValue();
			int x2 = ((Integer)n.getParameter("x2")).intValue();
			int y2 = ((Integer)n.getParameter("y2")).intValue();
			Color color = toColor((String)n.getParameter("color"));
			String parent_id = (String)n.getParameter("parent_id");

			Container c = containerByID(parent_id);
			if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
				line ((C2Viewport)c, obj_name, x1, y1, x2, y2, color);
			else
				line ((C2Panel)c, obj_name, x1, y1, x2, y2, color);
		}
		else if (message_name.equalsIgnoreCase("c2Garc"))
		{
			int x = ((Integer)n.getParameter("x")).intValue();
			int y = ((Integer)n.getParameter("y")).intValue();
			int width = ((Integer)n.getParameter("width")).intValue();
			int height = ((Integer)n.getParameter("height")).intValue();
			int angle1 = ((Integer)n.getParameter("angle1")).intValue();
			int angle2 = ((Integer)n.getParameter("angle2")).intValue();
			Color fill = toColor((String)n.getParameter("fill"));
			Color border = toColor((String)n.getParameter("border"));
			String parent_id = (String)n.getParameter("parent_id");

			Container c = containerByID(parent_id);
			if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
				arc ((C2Viewport)c, obj_name, x, y, width, height, angle1, angle2, fill, border);
			else
				arc ((C2Panel)c, obj_name, x, y, width, height, angle1, angle2, fill, border);
		}
		else if (message_name.equalsIgnoreCase("c2Goval"))
		{
			int x = ((Integer)n.getParameter("x")).intValue();
			int y = ((Integer)n.getParameter("y")).intValue();
			int width = ((Integer)n.getParameter("width")).intValue();
			int height = ((Integer)n.getParameter("height")).intValue();
			Color fill = toColor((String)n.getParameter("fill"));
			Color border = toColor((String)n.getParameter("border"));
			String parent_id = (String)n.getParameter("parent_id");

			Container c = containerByID(parent_id);
			if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
				oval ((C2Viewport)c, obj_name, x, y, width, height, fill, border);
			else
				oval ((C2Panel)c, obj_name, x, y, width, height, fill, border);
		}
		else if (message_name.equalsIgnoreCase("c2Gtext"))
		{
			int x = ((Integer)n.getParameter("x")).intValue();
			int y = ((Integer)n.getParameter("y")).intValue();
			String value = (String)n.getParameter("value");
			String font = (String)n.getParameter("font");
			Color color = toColor((String)n.getParameter("color"));
			String parent_id = (String)n.getParameter("parent_id");

			String style = (String)n.getParameter("style");
			if (style == null)
				style = "plain";

			int size;
			Integer integer_size = (Integer)n.getParameter("size");
			if (integer_size != null)
				size = integer_size.intValue();
			else // default size
				size = 12;

			Container c = containerByID(parent_id);
			if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
				text ((C2Viewport)c, obj_name, x, y, value, font, style, size, color);
			else
				text ((C2Panel)c, obj_name, x, y, value, font, style, size, color);
		}
		else {} // do nothing
	}


	/** Handles requests received from components below.  Assumes that
	 * GraphicsBinding is at the bottom of an architecture and
	 * receives no requests; hence, this is an empty method.
	 * @param r C2 request.
	 */
	public synchronized void handleRequest(NamedPropertyMessage r){
	}

	////////////////////////////
	// category: GUI creation //
	////////////////////////////

	/** Creates a C2 viewport, which is a Java AWT Frame in this case.
	 * @param id Viewport's unique identifier.
	 * @param x Viewport's x location.
	 * @param y Viewport's y location.
	 * @param width Viewport's width.
	 * @param height Viewport's height.
	 * @param title Viewport's title.
	 * @param foreground Viewport's foreground color.
	 * @param background Viewport's background color.
	 * @param gb Handle to this instance of the GraphicsBinding.
	 * @return The created C2 Viewport.
	 */
	public C2Viewport createViewport (String id, int x, int y, int width,
		int height, String title,
		Color foreground, Color background,
		GraphicsBinding gb)
	{
		C2Viewport v = new C2Viewport (id, x, y, width, height, title,
			foreground, background, gb);
		return v;
	}

	/** Destroys a C2 viewport.
	 * @param v Viewport to be destroyed.
	 */
	public void destroyViewport (C2Viewport v)
	{
		v.killViewport();
	}

	/** Clears a C2 viewport.
	 * @param v Viewport to be cleared.
	 */
	public void clearViewport (C2Viewport v)
	{
		v.clearAll();
	}

	/** Exits the current application.
	 */
	public void shutdown(){
		ShutdownArchMessage sam = new ShutdownArchMessage(0, ShutdownArchMessage.SHUTDOWN_NORMAL,
			"Normal Termination");
		sendArchMessage(sam);
	}

	/** Creates a C2 panel, which is a Java AWT Panel in this case.
	 * @param id Panel's unique identifier.
	 * @param x Panel's x location.
	 * @param y Panel's y location.
	 * @param width Panel's width.
	 * @param height Panel's height.
	 * @param foreground Panel's foreground color.
	 * @param background Panel's background color.
	 * @param gb Handle to this instance of the GraphicsBinding.
	 * @return The created C2 Panel.
	 */
	public C2Panel addPanel (String id, C2Viewport v, int x, int y, int width,
		int height, Color foreground, Color background,
		GraphicsBinding gb)
	{
		C2Panel p = new C2Panel (id, v, x, y, width, height, foreground,
			background, gb);
		return p;
	}

	/** Clears a C2 panel.
	 * @param p Panel to be cleared.
	 */
	public void clearPanel (C2Panel p)
	{
		p.clearAll();
	}

	/** Adds a button to a C2 viewport.
	 * @param v C2 Viewport to which the button is to be added.
	 * @param x Button's x location.
	 * @param y Button's y location.
	 * @param width Button's width.
	 * @param height Button's height.
	 * @param label Button's label.
	 * @param foreground Button's foreground color.
	 * @param background Button's background color.
	 * @return The added C2 Button.
	 */
	public C2Button addButton (C2Viewport v, int x, int y,
		int width, int height, String label,
		Color foreground, Color background)
	{
		C2Button b = new C2Button (v, x, y, width, height, label,
			foreground, background);
		return b;
	}

	/** Adds a button to a C2 panel.
	 * @param p C2 Panel to which the button is to be added.
	 * @param x Button's x location.
	 * @param y Button's y location.
	 * @param width Button's width.
	 * @param height Button's height.
	 * @param label Button's label.
	 * @param foreground Button's foreground color.
	 * @param background Button's background color.
	 * @return The added C2 Button.
	 */
	public C2Button addButton (C2Panel p, int x, int y, int width,
		int height, String label,
		Color foreground, Color background)
	{
		C2Button b = new C2Button (p, x, y, width, height, label,
			foreground, background);
		return b;
	}

	/** Retrieves the value of a text field.
	 * @param tf Text field whose value is accessed.
	 * @return The value of the text field.
	 */
	public String getTextField (C2TextField tf)
	{
		return tf.get();
	}

	/** Sets the value of a text field.
	 * @param tf Text field whose value is to be set.
	 * @param s New value for the text field.
	 */
	public void setTextField (C2TextField tf, String s)
	{
		tf.set(s);
	}

	/** Clears a text field.
	 * @param tf Text field to be cleared.
	 */
	public void eraseTextField (C2TextField tf)
	{
		tf.set("");
	}

	/** Adds a text field to a C2 viewport.
	 * @param id Text field's unique id.
	 * @param v C2 Vieport to which the text field is to be added.
	 * @param x Text field's x location.
	 * @param y Text field's y location.
	 * @param width Text field's width.
	 * @param height Text field's height.
	 * @param foreground Text field's foreground color.
	 * @param background Text field's background color.
	 * @param initial Text field's initial value.
	 * @return The added C2 Text Field.
	 */
	public C2TextField addTextField (String id, C2Viewport v, int x, int y,
		int width, int height, Color foreground,
		Color background, String initial)
	{
		C2TextField tf = new C2TextField (id, v, x, y, width, height,
			foreground, background, initial);
		return tf;
	}

	/** Adds a text field to a C2 Panel.
	 * @param id Text field's unique id.
	 * @param p C2 Panel to which the text field is to be added.
	 * @param x Text field's x location.
	 * @param y Text field's y location.
	 * @param width Text field's width.
	 * @param height Text field's height.
	 * @param foreground Text field's foreground color.
	 * @param background Text field's background color.
	 * @param initial Text field's initial value.
	 * @return The added C2 Text Field.
	 */
	public C2TextField addTextField (String id, C2Panel p, int x, int y,
		int width, int height, Color foreground,
		Color background, String initial)
	{
		C2TextField tf = new C2TextField (id, p, x, y, width, height,
			foreground, background, initial);
		return tf;
	}

	// category: drawing functions
	/** Draws a rectangle in a viewport.
	 * @param v C2 Viewport in which the rectangle is to be drawn.
	 * @param x Rectangle's x location.
	 * @param y Rectangle's y location.
	 * @param width Rectangle's width.
	 * @param height Rectangle's height.
	 * @param fill Rectangle's fill color.
	 * @param border Rectangle's border color.
	 */
	public void rectangle (C2Viewport v, String id, int x, int y, int width, int height,
		Color fill, Color border)
	{
		new C2Rectangle (v, id, x, y, width, height, fill, border);
	}

	/** Draws an oval in a viewport.
	 * @param v C2 Viewport in which the oval is to be drawn.
	 * @param x Oval's x location.
	 * @param y Oval's y location.
	 * @param width Oval's width.
	 * @param height Oval's height.
	 * @param fill Oval's fill color.
	 * @param border Oval's border color.
	 */
	public void oval (C2Viewport v, String id, int x, int y, int width, int height,
		Color fill, Color border)
	{
		new C2Oval (v, id, x, y, width, height, fill, border);
	}

	/** Draws an arc in a viewport.
	 * @param v C2 Viewport in which the arc is to be drawn.
	 * @param x Arc's x location.
	 * @param y Arc's y location.
	 * @param width Arc's width.
	 * @param height Arc's height.
	 * @param angle1 Arc's start angle.
	 * @param angle2 Arc's end angle.
	 * @param fill Arc's fill color.
	 * @param border Arc's border color.
	 */
	public void arc (C2Viewport v, String id, int x, int y, int width, int height,
		int angle1, int angle2,  Color fill, Color border)
	{
		new C2Arc (v, id, x, y, width, height, angle1, angle2, fill, border);
	}

	/** Draws a line in a viewport.
	 * @param v C2 Viewport in which the line is to be drawn.
	 * @param x1 Line's starting x location.
	 * @param y1 Line's starting y location.
	 * @param x2 Line's ending x location.
	 * @param y2 Line's ending y location.
	 * @param c Line's color.
	 */
	public void line (C2Viewport v, String id, int x1, int y1, int x2, int y2, Color c)
	{
		new C2Line (v, id, x1, y1, x2, y2, c);
	}

	/** Draws a text string in a viewport.
	 * @param v C2 Viewport in which the text string is to be drawn.
	 * @param x Text string's x location.
	 * @param y Text string's y location.
	 * @param message Text string's value.
	 * @param font Text string's font (e.g., helvetica).
	 * @param style Text string's style (e.g., bold).
	 * @param size Text string's size.
	 * @param c Text string's color.
	 */
	public void text (C2Viewport v, String id, int x, int y, String message, String font,
		String style, int size, Color c)
	{
		new C2Text (v, id, x, y, message, font, style, size, c);
	}

	// drawing graphics object on the panel
	/** Draws a rectangle in a panel.
	 * @param p C2 Panel in which the rectangle is to be drawn.
	 * @param x Rectangle's x location.
	 * @param y Rectangle's y location.
	 * @param width Rectangle's width.
	 * @param height Rectangle's height.
	 * @param fill Rectangle's fill color.
	 * @param border Rectangle's border color.
	 */
	public void rectangle (C2Panel p, String id, int x, int y, int width, int height,
		Color fill, Color border)
	{
		new C2Rectangle (p, id, x, y, width, height, fill, border);
	}

	/** Draws an oval in a panel.
	 * @param p C2 Panel in which the oval is to be drawn.
	 * @param x Oval's x location.
	 * @param y Oval's y location.
	 * @param width Oval's width.
	 * @param height Oval's height.
	 * @param fill Oval's fill color.
	 * @param border Oval's border color.
	 */
	public void oval (C2Panel p, String id, int x, int y, int width, int height,
		Color fill, Color border)
	{
		new C2Oval (p, id, x, y, width, height, fill, border);
	}

	/** Draws an arc in a panel.
	 * @param p C2 Panel in which the arc is to be drawn.
	 * @param x Arc's x location.
	 * @param y Arc's y location.
	 * @param width Arc's width.
	 * @param height Arc's height.
	 * @param angle1 Arc's start angle.
	 * @param angle2 Arc's end angle.
	 * @param fill Arc's fill color.
	 * @param border Arc's border color.
	 */
	public void arc (C2Panel p, String id, int x, int y, int width, int height,
		int angle1, int angle2,  Color fill, Color border)
	{
		new C2Arc (p, id, x, y, width, height, angle1, angle2, fill, border);
	}

	/** Draws a line in a panel.
	 * @param p C2 Panel in which the line is to be drawn.
	 * @param x1 Line's starting x location.
	 * @param y1 Line's starting y location.
	 * @param x2 Line's ending x location.
	 * @param y2 Line's ending y location.
	 * @param c Line's color.
	 */
	public void line (C2Panel p, String id, int x1, int y1, int x2, int y2, Color c)
	{
		new C2Line (p, id, x1, y1, x2, y2, c);
	}

	/** Draws a text string in a panel.
	 * @param p C2 Panel in which the text string is to be drawn.
	 * @param x Text string's x location.
	 * @param y Text string's y location.
	 * @param message Text string's value.
	 * @param font Text string's font (e.g., helvetica).
	 * @param style Text string's style (e.g., bold).
	 * @param size Text string's size.
	 * @param c Text string's color.
	 */
	public void text (C2Panel p, String id, int x, int y, String message, String font,
		String style, int size, Color c)
	{
		new C2Text (p, id, x, y, message, font, style, size, c);
	}

    protected void CreateObject( NamedPropertyMessage n )
    {
        String obj_type = (String)n.getParameter("type");
        String obj_name = (String)n.getParameter("name");

        if (obj_type.equalsIgnoreCase("arc"))
        {
            String name = (String)n.getParameter("name");
            int x = ((Integer)n.getParameter("x")).intValue();
            int y = ((Integer)n.getParameter("y")).intValue();
            int width = ((Integer)n.getParameter("width")).intValue();
            int height = ((Integer)n.getParameter("height")).intValue();
            int angle1 = ((Integer)n.getParameter("angle1")).intValue();
            int angle2 = ((Integer)n.getParameter("angle2")).intValue();
            Color fill = toColor((String)n.getParameter("fill"));
            Color border = toColor((String)n.getParameter("border"));
            String parent_id = (String)n.getParameter("parent_id");

            Container c = containerByID(parent_id);
            if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
                arc ((C2Viewport)c, obj_name, x, y, width, height, angle1, angle2, fill, border);
            else
                arc ((C2Panel)c, obj_name, x, y, width, height, angle1, angle2, fill, border);
        }
        else if (obj_type.equalsIgnoreCase("line"))
        {
            String name = (String)n.getParameter("name");
            int x1 = ((Integer)n.getParameter("x1")).intValue();
            int y1 = ((Integer)n.getParameter("y1")).intValue();
            int x2 = ((Integer)n.getParameter("x2")).intValue();
            int y2 = ((Integer)n.getParameter("y2")).intValue();
            Color color = toColor((String)n.getParameter("color"));
            String parent_id = (String)n.getParameter("parent_id");

            Container c = containerByID(parent_id);
            if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
                line ((C2Viewport)c, obj_name, x1, y1, x2, y2, color);
            else
                line ((C2Panel)c, obj_name, x1, y1, x2, y2, color);
        }
        else if (obj_type.equalsIgnoreCase("rectangle"))
        {
            String name = (String)n.getParameter("name");
            int x = ((Integer)n.getParameter("x")).intValue();
            int y = ((Integer)n.getParameter("y")).intValue();
            int width = ((Integer)n.getParameter("width")).intValue();
            int height = ((Integer)n.getParameter("height")).intValue();
            Color fill = toColor((String)n.getParameter("fill"));
            Color border = toColor((String)n.getParameter("border"));
            String parent_id = (String)n.getParameter("parent_id");

            Container c = containerByID(parent_id);
            if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
                rectangle ((C2Viewport)c, obj_name, x, y, width, height, fill, border);
            else
                rectangle ((C2Panel)c, obj_name, x, y, width, height, fill, border);
        }
        else if (obj_type.equalsIgnoreCase("oval"))
        {
            String name = (String)n.getParameter("name");
            int x = ((Integer)n.getParameter("x")).intValue();
            int y = ((Integer)n.getParameter("y")).intValue();
            int width = ((Integer)n.getParameter("width")).intValue();
            int height = ((Integer)n.getParameter("height")).intValue();
            Color fill = toColor((String)n.getParameter("fill"));
            Color border = toColor((String)n.getParameter("border"));
            String parent_id = (String)n.getParameter("parent_id");

            Container c = containerByID(parent_id);
            if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
                oval ((C2Viewport)c, obj_name, x, y, width, height, fill, border);
            else
            {
                oval ((C2Panel)c, obj_name, x, y, width, height, fill, border);
            }
        }
        else if (obj_type.equalsIgnoreCase("text"))
        {
            String name = (String)n.getParameter("name");
            int x = ((Integer)n.getParameter("x")).intValue();
            int y = ((Integer)n.getParameter("y")).intValue();
            String value = (String)n.getParameter("value");
            String font = (String)n.getParameter("font");
            Color color = toColor((String)n.getParameter("color"));
            String parent_id = (String)n.getParameter("parent_id");

            String style = (String)n.getParameter("style");
            if (style == null)
            style = "plain";

            int size;
            Integer integer_size = (Integer)n.getParameter("size");
            if (integer_size != null)
            size = integer_size.intValue();
            else // default size
            size = 12;

            Container c = containerByID(parent_id);
            if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
               text ((C2Viewport)c, obj_name, x, y, value, font, style, size, color);
            else
                text ((C2Panel)c, obj_name, x, y, value, font, style, size, color);
        }
    }

    protected boolean ModifyObject( NamedPropertyMessage n )
    {
        // find the container to which the object belongs, then
        // search the container for the given object, then
        // modify the object
        String parent_id = (String)n.getParameter("parent_id");
        Container c = containerByID(parent_id);

        if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
            return ModifyViewportObject ((C2Viewport)c, n);
        else
            return ModifyPanelObject ((C2Panel)c, n);
    }


    protected boolean DestroyObject( NamedPropertyMessage n )
    {
        // find the container to which the object belongs, then
        // search the container for the given object, then
        // destroy the object
        String parent_id = (String)n.getParameter("parent_id");
        Container c = containerByID(parent_id);

        if (c.getClass().getName().equals("c2demo.comp.graphics.C2Viewport"))
            return DestroyViewportObject ((C2Viewport)c, n);
        else
            return DestroyPanelObject ((C2Panel)c, n);
    }


    protected boolean ModifyPanelObject (C2Panel c, NamedPropertyMessage n)
    {
        // first paint the object in the background color, then modify
        // and repaint it

        boolean found = false;
        int count = 0;

        String name = (String)n.getParameter("name");
        while ((count < c.numGraphicsObjects()) && !found)
        {
            if (name.equals(c.getGraphicsObject(count).getID()))
                found = true;
            else
                count++;
        }
        if (!found)
            return false;
        else // object to be destroyed is at location count
        {
            Color bg = c.backgroundColor();
            C2GraphicsObject obj = c.getGraphicsObject(count);

            if (obj.getClass().getName().equals("c2demo.comp.graphics.C2Arc"))
            {
                ((C2Arc)obj).setFill(bg);
                ((C2Arc)obj).setBorder(bg);
                ((C2Arc)obj).paint_object(c);

                ((C2Arc)obj).setID((String)n.getParameter("name"));
                ((C2Arc)obj).setX(((Integer)n.getParameter("x")).intValue());
                ((C2Arc)obj).setY(((Integer)n.getParameter("y")).intValue());
                ((C2Arc)obj).setWidth(((Integer)n.getParameter("width")).intValue());
                ((C2Arc)obj).setHeight(((Integer)n.getParameter("height")).intValue());
                ((C2Arc)obj).setAngle1(((Integer)n.getParameter("angle1")).intValue());
                ((C2Arc)obj).setAngle2(((Integer)n.getParameter("angle2")).intValue());
                ((C2Arc)obj).setFill(toColor((String)n.getParameter("fill")));
                ((C2Arc)obj).setBorder(toColor((String)n.getParameter("border")));
                ((C2Arc)obj).paint_object(c);
            }
            else if (obj.getClass().getName().equals("c2demo.comp.graphics.C2Line"))
            {
                ((C2Line)obj).setColor(bg);
                ((C2Line)obj).paint_object(c);

                ((C2Line)obj).setID((String)n.getParameter("name"));
                ((C2Line)obj).setX1(((Integer)n.getParameter("x1")).intValue());
                ((C2Line)obj).setY1(((Integer)n.getParameter("y1")).intValue());
                ((C2Line)obj).setX2(((Integer)n.getParameter("x2")).intValue());
                ((C2Line)obj).setY2(((Integer)n.getParameter("y2")).intValue());
                ((C2Line)obj).setColor(toColor((String)n.getParameter("color")));
            }
            else if (obj.getClass().getName().equals("c2demo.comp.graphics.C2Oval"))
            {
                ((C2Oval)obj).setFill(bg);
                ((C2Oval)obj).setBorder(bg);
                ((C2Oval)obj).paint_object(c);
                ((C2Oval)obj).setID((String)n.getParameter("name"));
                ((C2Oval)obj).setX(((Integer)n.getParameter("x")).intValue());
                ((C2Oval)obj).setY(((Integer)n.getParameter("y")).intValue());
                ((C2Oval)obj).setWidth(((Integer)n.getParameter("width")).intValue());
                ((C2Oval)obj).setHeight(((Integer)n.getParameter("height")).intValue());
                ((C2Oval)obj).setFill(toColor((String)n.getParameter("fill")));
                ((C2Oval)obj).setBorder(toColor((String)n.getParameter("border")));
                ((C2Oval)obj).paint_object(c);
            }
            else if (obj.getClass().getName().equals("c2demo.comp.graphics.C2Rectangle"))
            {
                ((C2Rectangle)obj).setFill(bg);
                ((C2Rectangle)obj).setBorder(bg);
                ((C2Rectangle)obj).paint_object(c);

                ((C2Rectangle)obj).setID((String)n.getParameter("name"));
                ((C2Rectangle)obj).setX(((Integer)n.getParameter("x")).intValue());
                ((C2Rectangle)obj).setY(((Integer)n.getParameter("y")).intValue());
                ((C2Rectangle)obj).setWidth(((Integer)n.getParameter("width")).intValue());
                ((C2Rectangle)obj).setHeight(((Integer)n.getParameter("height")).intValue());
                ((C2Rectangle)obj).setFill(toColor((String)n.getParameter("fill")));
                ((C2Rectangle)obj).setBorder(toColor((String)n.getParameter("border")));
                ((C2Rectangle)obj).paint_object(c);
            }
            else if (obj.getClass().getName().equals("c2demo.comp.graphics.C2Text"))
            {
                ((C2Text)obj).setColor(bg);
                ((C2Text)obj).paint_object(c);

                ((C2Text)obj).setID((String)n.getParameter("name"));
                ((C2Text)obj).setX(((Integer)n.getParameter("x")).intValue());
                ((C2Text)obj).setY(((Integer)n.getParameter("y")).intValue());
                ((C2Text)obj).setValue((String)n.getParameter("value"));
                ((C2Text)obj).setFont((String)n.getParameter("font"));
                ((C2Text)obj).setColor(toColor((String)n.getParameter("color")));

                String style = (String)n.getParameter("style");
                if (style != null)
                    ((C2Text)obj).setStyle(style);
                else
                    ((C2Text)obj).setStyle("plain");

                int size;
                Integer integer_size = (Integer)n.getParameter("size");
                if (integer_size != null)
                    ((C2Text)obj).setSize(integer_size.intValue());
                else // default size
                    ((C2Text)obj).setSize(12);

                ((C2Text)obj).paint_object(c);
            }
            return true;
        }
    }


    protected boolean ModifyViewportObject( C2Viewport c, NamedPropertyMessage n )
    {
        // first paint the object in the background color, then modify
        // and repaint it

        boolean found = false;
        int count = 0;

        String name = (String)n.getParameter("name");
        while ((count < c.numGraphicsObjects()) && !found)
        {
            if (name.equals(c.getGraphicsObject(count).getID()))
                found = true;
            else
                count++;
        }

       if (!found)
            return false;
        else // object to be destroyed is at location count
        {
            Color bg = c.backgroundColor();
            C2GraphicsObject obj = c.getGraphicsObject(count);

            if (obj.getClass().getName().equals("c2demo.comp.graphics.C2Arc"))
            {
                ((C2Arc)obj).setFill(bg);
                ((C2Arc)obj).setBorder(bg);
                ((C2Arc)obj).paint_object(c);

                ((C2Arc)obj).setID((String)n.getParameter("name"));
                ((C2Arc)obj).setX(((Integer)n.getParameter("x")).intValue());
                ((C2Arc)obj).setY(((Integer)n.getParameter("y")).intValue());
                ((C2Arc)obj).setWidth(((Integer)n.getParameter("width")).intValue());
                ((C2Arc)obj).setHeight(((Integer)n.getParameter("height")).intValue());
                ((C2Arc)obj).setAngle1(((Integer)n.getParameter("angle1")).intValue());
                ((C2Arc)obj).setAngle2(((Integer)n.getParameter("angle2")).intValue());
                ((C2Arc)obj).setFill(toColor((String)n.getParameter("fill")));
                ((C2Arc)obj).setBorder(toColor((String)n.getParameter("border")));
                ((C2Arc)obj).paint_object(c);
            }
            else if (obj.getClass().getName().equals("c2demo.comp.graphics.C2Line"))
            {
                ((C2Line)obj).setColor(bg);
                ((C2Line)obj).paint_object(c);

                ((C2Line)obj).setID((String)n.getParameter("name"));
                ((C2Line)obj).setX1(((Integer)n.getParameter("x1")).intValue());
                ((C2Line)obj).setY1(((Integer)n.getParameter("y1")).intValue());
                ((C2Line)obj).setX2(((Integer)n.getParameter("x2")).intValue());
                ((C2Line)obj).setY2(((Integer)n.getParameter("y2")).intValue());
                ((C2Line)obj).setColor(toColor((String)n.getParameter("color")));
            }
            else if (obj.getClass().getName().equals("c2demo.comp.graphics.C2Oval"))
            {
                ((C2Oval)obj).setFill(bg);
                ((C2Oval)obj).setBorder(bg);
                ((C2Oval)obj).paint_object(c);

                ((C2Oval)obj).setID((String)n.getParameter("name"));
                ((C2Oval)obj).setX(((Integer)n.getParameter("x")).intValue());
                ((C2Oval)obj).setY(((Integer)n.getParameter("y")).intValue());
                ((C2Oval)obj).setWidth(((Integer)n.getParameter("width")).intValue());
                ((C2Oval)obj).setHeight(((Integer)n.getParameter("height")).intValue());
                ((C2Oval)obj).setFill(toColor((String)n.getParameter("fill")));
                ((C2Oval)obj).setBorder(toColor((String)n.getParameter("border")));
                ((C2Oval)obj).paint_object(c);
            }
            else if (obj.getClass().getName().equals("c2demo.comp.graphics.C2Rectangle"))
            {
                ((C2Rectangle)obj).setFill(bg);
                ((C2Rectangle)obj).setBorder(bg);
                ((C2Rectangle)obj).paint_object(c);

                ((C2Rectangle)obj).setID((String)n.getParameter("name"));
                ((C2Rectangle)obj).setX(((Integer)n.getParameter("x")).intValue());
                ((C2Rectangle)obj).setY(((Integer)n.getParameter("y")).intValue());
                ((C2Rectangle)obj).setWidth(((Integer)n.getParameter("width")).intValue()) ;
                ((C2Rectangle)obj).setHeight(((Integer)n.getParameter("height")).intValue());
                ((C2Rectangle)obj).setFill(toColor((String)n.getParameter("fill")));
                ((C2Rectangle)obj).setBorder(toColor((String)n.getParameter("border")));
                ((C2Rectangle)obj).paint_object(c);
            }
            else if (obj.getClass().getName().equals("c2demo.comp.graphics.C2Text"))
            {
                ((C2Text)obj).setColor(bg);
                ((C2Text)obj).paint_object(c);

                ((C2Text)obj).setID((String)n.getParameter("name"));
                ((C2Text)obj).setX(((Integer)n.getParameter("x")).intValue());
                ((C2Text)obj).setY(((Integer)n.getParameter("y")).intValue());
                ((C2Text)obj).setValue((String)n.getParameter("value"));
                ((C2Text)obj).setFont((String)n.getParameter("font"));
                ((C2Text)obj).setColor(toColor((String)n.getParameter("color")));
                ((C2Text)obj).paint_object(c);

                String style = (String)n.getParameter("style");
                if (style == null)
                    ((C2Text)obj).setStyle((String)n.getParameter("style"));
                else
                    ((C2Text)obj).setStyle("plain");

                int size;
                Integer integer_size = (Integer)n.getParameter("size");
                if (integer_size != null)
                    ((C2Text)obj).setSize(integer_size.intValue());
                else // default size
                    ((C2Text)obj).setSize(12);
            }
            return true;
        }
    }


    protected boolean DestroyPanelObject (C2Panel p, NamedPropertyMessage n)
    {
        String name = (String)n.getParameter("name");
        boolean found = false;
        int count = 0;

        while ((count < p.numGraphicsObjects()) && !found)
        {
            if (name.equals(p.getGraphicsObject(count).getID()))
                found = true;
            else
                count++;
        }

        if (!found)
            return false;
        else // object to be destroyed is at location count
        {
            p.removeGraphicsObject (count);
            return true;
        }
    }


    protected boolean DestroyViewportObject (C2Viewport v, NamedPropertyMessage n)
    {
        String name = (String)n.getParameter("name");
        boolean found = false;
        int count = 0;


        while ((count < v.numGraphicsObjects()) && !found)
        {
            if (name.equals(v.getGraphicsObject(count).getID()))
                found = true;
            else
                count++;
        }

        if (!found)
            return false;
        else // object to be destroyed is at location count
        {
            v.removeGraphicsObject (count);
            return true;
        }
    }

	//////////////////////
	// end GUI creation //
	//////////////////////
}
