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

/**
 * StackADTThread class.<p>
 *
 * Implements a stack abstract date type (ADT) in its own 
 * thread of control.  It accepts requests for modifying and accessing the 
 * stack from components in a C2 architecture below it, and notifies such 
 * components of its internal state changes. <p>
 *
 * <HR>
 * <h3>StackADTThread's C2 Message Interface</h3>
 *
 * StackADTThread's methods should not be invoked directly in a C2
 * architecture.  Instead, the ADT's functionality is accessed via C2
 * requests. In response to those requests, the ADT emits notifications of
 * its internal state changes.<p>
 *
 * <pre>
 *    <b>Incoming Notifications</b>
 *        none
 *    <b>Outgoing Requests</b>
 *        none
 *    <b>Incoming Requests</b>
 *        push (Object value);
 *        pop  ();
 *        top  ();
 *        get_state ();
 *    <b>Outgoing Notifications</b>
 *        pushed (Object value);
 *        popped (Object value);
 *        top    (Object value);
 *        state  (Stack stack);
 *        empty  ();
 * </pre>
 */
public class StackADTThread extends c2.legacy.AbstractC2Brick{
	/** Stack ADT.
	 */
	protected Stack stack = new Stack();
	
	// category: constructor
	public StackADTThread(String name){
		super(new SimpleIdentifier(name));
	}
	
	public StackADTThread(Identifier id){
		super(id);
	}
	
	public void init(){
	}
	
	public void begin(){
	}
	
	public void end(){
	}
	
	public void destroy(){
	}
	
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
	
	// category: message passing implementation
	
	/** Handles C2 requests received from components below 
	 * StackADTThread in an architecture.  Manipulates the ADT and 
	 * notifies components below of the results of operations on 
	 * the stack and/or the state of the stack.
	 * @param r C2 request.
	 */
	public synchronized void handleRequest(NamedPropertyMessage r){
			
		String message_name;
		Object param_value;
		NamedPropertyMessage n = null;
		
		message_name = r.getName();
		
		// handle message
		if (message_name.equalsIgnoreCase("push"))
		{
			param_value = r.getParameter ("value");
			stack.push (param_value);
			
			// generate a notification
			n = c2.legacy.Utils.createC2Notification("pushed");
			n.addParameter ("value", param_value);
			sendDefault(n);  
		}
		else if (message_name.equalsIgnoreCase("pop"))
		{
			try 
			{ 
				param_value = stack.pop (); 
				n = c2.legacy.Utils.createC2Notification("popped");
				n.addParameter ("value", param_value);
			}
			catch (java.util.EmptyStackException e)
			{
				n = c2.legacy.Utils.createC2Notification("empty");
			}
			finally 
			{
				sendDefault(n);  
			}
		}
		else if (message_name.equalsIgnoreCase("top"))
		{
			try 
			{ 
				param_value = stack.peek ();
				n = c2.legacy.Utils.createC2Notification("top");
				n.addParameter ("value", param_value);
			}
			catch (java.util.EmptyStackException e)
			{
				n = c2.legacy.Utils.createC2Notification("empty");
			}
			finally 
			{
				sendDefault(n);
			}
		}
		else if (message_name.equalsIgnoreCase("get_state"))
		{
			if (stack.empty())
				n = c2.legacy.Utils.createC2Notification("empty");
			else
			{
				n = c2.legacy.Utils.createC2Notification("state");
				n.addParameter ("stack", stack.clone());
			}
			sendDefault(n);
		}
	}
	
	/** Handles notifications received from components above.  Assumes that
	 * StackADTThread is at the top of an architecture and
	 * receives no notifications; hence, this is an empty method.
	 * @param n C2 notification.
	 */
	
	public synchronized void handleNotification(NamedPropertyMessage n){
	}
}
