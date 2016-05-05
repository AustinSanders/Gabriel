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

/**
 * Class:           Queue
 * Author:          Kari Nies, modified source from Walter Korman
 * Date Created:    April 29, 1997
 * Date Modified:   April 29, 1997
 * Description:     A First-In-First-Out(FIFO) queue of objects.
 *                  Based on the java.util.Stack class by Jonathan Payne.
 *                  (c) 1996 by Walter Korman.  Free for public use as
 *                  long as this header remains unmodified.
 */

package c2demo.klax;

import java.util.*;

public class Queue extends Vector {
	
	private int queueMax;
	
	/**
	 * Queue constructor
	 */
	public Queue (int capacity) {
		super(capacity);
		queueMax = capacity;
	}
	
	/**
	 * Puts an item into the queue.
	 * @param item the item to be put into the queue.
	 */
	public Object enqueue(Object item) {
		addElement(item);
		
		return item;
	}
	
	/**
	 * Gets an item from the front of the queue.
	 * @exception EmptyQueueException If the queue is empty.
	 */
	public Object dequeue() {
		Object  obj;
		
		obj = peek();
		removeElementAt(0);
		
		return obj;
	}
	
	/**
	 * Clears all items from the queue.
	 */
	public void clearQueue(){
		removeAllElements();
	}
	
	/**
	 * Peeks at the front of the queue.
	 * @exception EmptyQueueException If the queue is empty.
	 */
	public Object peek() {
		int     len = size();
		
		if (len == 0)
			throw new EmptyQueueException();
		return elementAt(0);
	}
	
	/**
	 * Returns true if the queue is empty.
	 */
	public boolean empty() {
		return isEmpty();
	}
	
	/**
	 * Cannot override Vector.toString
	 */
	public String queueToString() {
		StringBuffer buf = new StringBuffer();
		java.util.Enumeration e = elements();
		buf.append("[");
		
		for (int i = 0 ; i <= queueMax-1 ; i++) {
			String s = e.nextElement().toString();
			buf.append(s);
			if (i < queueMax-1) {
				buf.append(", ");
			}
		}
		buf.append("]");
		return buf.toString();
	}
	
}
