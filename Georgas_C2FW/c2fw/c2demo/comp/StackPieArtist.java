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


package c2demo.comp;

import java.lang.*;
import java.util.*;
import c2.fw.*;
import c2.legacy.*;
import c2demo.comp.graphics.*;


/**
 * StackPieArtist class.<p>
 *
 * Implements a graphical visualization of a stack.  Maintains an abstract
 * model of the top of the stack and an arc from zero to <i>stack_top
 * <b>mod</b> 360</i> degrees. Also maintains buttons to push
 * elements onto the stack, pop them off the stack, and access the top
 * element.  A text field allows a user to specify the value to be pushed,
 * and a quit button to exit the application to which the Artist belongs.<p>
 *
 * The Artist issues stack manipulation requests to a stack ADT above it in
 * a C2 architecture and notifications to components (if any) below
 * it.  In turn, it receives notifications from the stack ADT and requests
 * from a graphics server and acts accordingly.<p>
 *
 * <HR>
 * <h3>StackPieArtist's C2 Message Interface</h3>
 *
 * StackPieArtist's methods should not be invoked directly in a C2
 * architecture.  Instead, the Artist's functionality is accessed via C2
 * messages (requests and notifications). In response to those messages,
 * the Artist emits requests to a stack ADT above it and sends notifications
 * of its internal state changes down.<p>
 *
 * <pre>
 *    <b>Incoming Notifications</b>
 *        pushed                (Integer value);
 *        popped                (Integer value);
 *        top                   (Integer value);
 *        empty                 ();
 *    <b>Outgoing Requests</b>
 *        push                  (Integer value);
 *        pop                   ();
 *        top                   ();
 *    <b>Incoming Requests</b>
 *        AcceptEvent           (String button, String parent_id);
 *        AcceptEvent           (String text_field, String value,
 *                               String parent_id);
 *    <b>Outgoing Notifications</b>
 *        ViewportCreated       (Integer x, Integer y,
 *                               Integer width, Integer height,
 *                               String title, String id,
 *                               String foreground, String background);
 *        ApplicationTerminated ();
 *        PanelAdded            (Integer x, Integer y,
 *                               Integer width, Integer height,
 *                               String foreground, String background,
 *                               String id, String parent_id);
 *        PanelCleared          (String id);
 *        ButtonAdded           (Integer x, Integer y,
 *                               Integer width, Integer height,
 *                               String foreground, String background,
 *                               String label, String parent_id);
 *        TextFieldAdded        (Integer x, Integer y,
 *                               Integer width, Integer height,
 *                               String foreground, String background,
 *                               String label, String parent_id);
 *        TextFieldSet          (String label, String value,
 *                               String parent_id);
 *        TextFieldCleared      (String label, String parent_id);
 *        c2Gtext               (Integer x, Integer y,
 *                               String value, String font,
 *                               String color, String style,
 *                               Integer size, String parent_id);
 *        c2Garc                (Integer x, Integer y,
 *                               Integer width, Integer height,
 *                               Integer angle1, Integer angle2,
 *                               String fill, String border,
 *                               String parent_id);
 *
 * </pre>
 */
public class StackPieArtist extends c2.legacy.AbstractC2Brick
{
	/** Viewport in which the contents of the stack and widgets for its
	 * manipulation are to be displayed.
	 */
	protected String vport;
	/** Panel in which the contents of the stack are to be displayed.
	 */
	protected String panel;
	/** Button for pushing elements onto the stack.
	 */
	protected String push_button;
	/** Button for popping elements off the stack.
	 */
	protected String pop_button;
	/** Button for returning the top stack element.
	 */
	protected String top_button;
	/** Button for exiting the application.
	 */
	protected String quit_button;
	/** Button for entering the value to be pushed onto the stack and viewing
	 * the top stack element.
	 */
	protected String txt_field;
	/** Integer value of the text field.
	 */
	protected Integer tf_value = new Integer(0);
	
	// category: constructor
	public StackPieArtist (String name){
		super(new SimpleIdentifier(name));
	}
	
	public StackPieArtist(Identifier id){
		super(id);
	}
	
	public void init(){
	}
	
	/** At startup, creates an abstract viewport, panel, quit, push, pop,
	 * and top buttons, and a text field, and emits notifications of these
	 * changes to any listening components below.  It also requests the current
	 * state of the stack, in the case that the component is added to an already
	 * running architecture.
	 */
	public void begin(){
		//super.init ();
		
		String id;
		NamedPropertyMessage new_n;
		
		// generate a notification
		vport = getClass().getName() + "." + getIdentifier().toString() + "." + "vport1";
		new_n = c2.legacy.Utils.createC2Notification("ViewportCreated");
		new_n.addParameter ("x", new Integer(500));
		new_n.addParameter ("y", new Integer (10));
		new_n.addParameter ("width", new Integer (380));
		new_n.addParameter ("height", new Integer (300));
		new_n.addParameter ("foreground", "black");
		new_n.addParameter ("background", "white");
		new_n.addParameter ("title", vport);
		new_n.addParameter ("id", vport);
		sendDefault(new_n);  
		
		new_n = c2.legacy.Utils.createC2Notification("c2Gtext");
		new_n.addParameter ("x", new Integer(210));
		new_n.addParameter ("y", new Integer(70));
		new_n.addParameter ("value", "Stack Top");
		new_n.addParameter ("font", "Helvetica");
		new_n.addParameter ("style", "bold");
		new_n.addParameter ("color", "black");
		new_n.addParameter ("size", new Integer (30));
		new_n.addParameter ("parent_id", vport);
		sendDefault(new_n);
		
		
		panel = vport + "." + "panel1";
		new_n = c2.legacy.Utils.createC2Notification("PanelAdded");
		new_n.addParameter ("x", new Integer(200));
		new_n.addParameter ("y", new Integer (80));
		new_n.addParameter ("width", new Integer (150));
		new_n.addParameter ("height", new Integer (80));
		new_n.addParameter ("foreground", "black");
		new_n.addParameter ("background", "yellow");
		new_n.addParameter ("id", panel);
		new_n.addParameter ("parent_id", vport);
		sendDefault(new_n);  
		
		quit_button = "Quit";
		new_n = c2.legacy.Utils.createC2Notification("ButtonAdded");
		new_n.addParameter ("x", new Integer (30));
		new_n.addParameter ("y", new Integer (55));
		new_n.addParameter ("width", new Integer (120));
		new_n.addParameter ("height", new Integer (90));
		new_n.addParameter ("foreground", "white");
		new_n.addParameter ("background", "black");
		new_n.addParameter ("label", quit_button);
		new_n.addParameter ("parent_id", vport);
		sendDefault(new_n);
		
		push_button = "Push";
		new_n = c2.legacy.Utils.createC2Notification("ButtonAdded");
		new_n.addParameter ("x", new Integer (70));
		new_n.addParameter ("y", new Integer (180));
		new_n.addParameter ("width", new Integer (100));
		new_n.addParameter ("height", new Integer (60));
		new_n.addParameter ("foreground", "black");
		new_n.addParameter ("background", "green");
		new_n.addParameter ("label", push_button);
		new_n.addParameter ("parent_id", vport);
		sendDefault(new_n);  
		
		pop_button = "Pop";
		new_n = c2.legacy.Utils.createC2Notification("ButtonAdded");
		new_n.addParameter ("x", new Integer (210));
		new_n.addParameter ("y", new Integer (180));
		new_n.addParameter ("width", new Integer (100));
		new_n.addParameter ("height", new Integer (60));
		new_n.addParameter ("foreground", "black");
		new_n.addParameter ("background", "green");
		new_n.addParameter ("label", pop_button);
		new_n.addParameter ("parent_id", vport);
		sendDefault(new_n);  
		
		txt_field = "TextField1";
		new_n = c2.legacy.Utils.createC2Notification("TextFieldAdded");
		new_n.addParameter ("x", new Integer (30));
		new_n.addParameter ("y", new Integer (260));
		new_n.addParameter ("width", new Integer (320));
		new_n.addParameter ("height", new Integer (30));
		new_n.addParameter ("foreground", "black");
		new_n.addParameter ("background", "cyan");
		new_n.addParameter ("label", txt_field);
		new_n.addParameter ("parent_id", vport);
		sendDefault(new_n);  
		
		NamedPropertyMessage r = c2.legacy.Utils.createC2Request("top");
		sendDefault(r);
		
	}
	
	public void end(){
	}
	
	public void destroy(){
	}
	
	// category: message passing implementation
	
	private void sendDefault(Message m){
		if(Utils.isC2Notification(m)){
			sendToAll(m, bottomIface);
		}
		else if(Utils.isC2Request(m)){
			sendToAll(m, topIface);
		}
	}
	
	public synchronized void handle(Message m){
		if(Utils.isC2Notification(m)){
			handleNotification((NamedPropertyMessage)m);
		}
		else if(Utils.isC2Request(m)){
			handleRequest((NamedPropertyMessage)m);
		}
	}
	
	/** Handles requests resulting from a user's manipulation of stack's
	 * depiction.  Depending on the request, it either sends stack manipulation
	 * requests to the ADT, records the value of the text field, or sends a
	 * <i>ApplicationTerminated</i> notification to the graphics binding (when the
	 * quit button is pressed).
	 *
	 * @param r Request to be handled.
	 */
	
	public synchronized void handleRequest(NamedPropertyMessage r){
		if (r.getName().equalsIgnoreCase("AcceptEvent")){
			String parent_id = (String)r.getParameter("parent_id");
			if ((parent_id.equalsIgnoreCase(vport)) ||
				(parent_id.equalsIgnoreCase(panel))) 
					handleUserInput (r); 
		}
	}
	
	void handleUserInput(NamedPropertyMessage r){
		NamedPropertyMessage new_r;
		NamedPropertyMessage new_n;
		String value;
		String label;
		
		label = (String)r.getParameter("button");
		
		if (label != null) // this is a button event
		{
			if (label.equalsIgnoreCase(quit_button))
			{
				// generate a notification
				new_n = c2.legacy.Utils.createC2Notification("ApplicationTerminated");
				sendDefault(new_n);
			}
			if (label.equalsIgnoreCase(push_button))
			{
				// generate a request
				new_r = c2.legacy.Utils.createC2Request("push");
				new_r.addParameter ("value", tf_value);
				sendDefault(new_r);
			}
			else if(label.equalsIgnoreCase(pop_button))
			{
				// generate a request
				new_r = c2.legacy.Utils.createC2Request("pop");
				sendDefault(new_r);
			}
		}
		else // this is a text field event
		{
			label = (String)r.getParameter("text_field");
			
			if (label != null)
			{
				tf_value = Integer.valueOf((String)r.getParameter("value"));
			}
		}
	}
	
	/** Handles notifications from the stack ADT.  If the ADT notifies that the
	 * stack has been pushed or popped, the top of the stack is requested.
	 * When the ADT sends a notification with the stack's top value, that value 
	 * is recorded in the Artist's abstract text field and an abstract arc whose
	 * size corresponds to the top value is created; the Artist sends a 
	 * notification of this state change down.
	 *
	 * @param n Notification to be handled.
	 */
	
	public synchronized void handleNotification(NamedPropertyMessage n){
		String message_name;
		String param_value;
		Vector param_vector;
		NamedPropertyMessage new_n;
		NamedPropertyMessage new_r;
		int size;
		
		message_name = n.getName();
		
		// handle message
		if (message_name.equalsIgnoreCase("pushed"))
		{
			param_value = ((Integer)n.getParameter("value")).toString();
			
			// generate a notification
			new_n = c2.legacy.Utils.createC2Notification("TextFieldCleared");
			new_n.addParameter ("label", txt_field);
			new_n.addParameter ("parent_id", vport);
			sendDefault(new_n);  
			
			// generate request to send the top of the stack
			new_r = c2.legacy.Utils.createC2Request("top");
			sendDefault(new_r);
		}
		else if (message_name.equalsIgnoreCase("popped"))
		{
			tf_value = ((Integer)n.getParameter("value"));
			param_value = tf_value.toString();
			
			
			// generate a notification
			new_n = c2.legacy.Utils.createC2Notification("TextFieldCleared");
			new_n.addParameter ("label", txt_field);
			new_n.addParameter ("parent_id", vport);
			sendDefault(new_n);  
			
			new_n = c2.legacy.Utils.createC2Notification("TextFieldSet");
			new_n.addParameter ("label", txt_field);
			new_n.addParameter ("value", param_value);
			new_n.addParameter ("parent_id", vport);
			sendDefault(new_n);  
			
			// generate request to send the top of the stack
			new_r = c2.legacy.Utils.createC2Request("top");
			sendDefault(new_r);
		}
		else if (message_name.equalsIgnoreCase("top"))
		{
			tf_value = ((Integer)n.getParameter("value"));
			param_value = tf_value.toString();
			
			// clear the panel
			new_n = c2.legacy.Utils.createC2Notification("PanelCleared");
			new_n.addParameter ("id", panel);
			sendDefault(new_n);
			
			// generate a notification
			new_n = c2.legacy.Utils.createC2Notification("c2Gtext");
			new_n.addParameter ("x", new Integer(75 - param_value.length()*5));
			new_n.addParameter ("y", new Integer (20));
			new_n.addParameter ("value", param_value);
			new_n.addParameter ("font", "Helvetica");
			new_n.addParameter ("style", "bold");
			new_n.addParameter ("color", "blue");
			new_n.addParameter ("size", new Integer (17));
			new_n.addParameter ("parent_id", panel);
			sendDefault(new_n);
			
			// display arc of size equal to the top of stack mod 360 degrees
			Integer tf_mod = new Integer (tf_value.intValue()%360);
			new_n = c2.legacy.Utils.createC2Notification("c2Garc");
			new_n.addParameter ("x", new Integer (50));
			new_n.addParameter ("y", new Integer (25));
			new_n.addParameter ("width", new Integer (50));
			new_n.addParameter ("height", new Integer (50));
			new_n.addParameter ("angle1", new Integer (0));
			new_n.addParameter ("angle2", tf_mod);
			new_n.addParameter ("fill", "blue");
			new_n.addParameter ("border", "black");
			new_n.addParameter ("parent_id", panel);
			sendDefault(new_n);
			
		}
		else if (message_name.equalsIgnoreCase("empty"))
		{
			new_n = c2.legacy.Utils.createC2Notification("PanelCleared");
			new_n.addParameter ("id", panel);
			sendDefault(new_n);
		}
	}
	
}
