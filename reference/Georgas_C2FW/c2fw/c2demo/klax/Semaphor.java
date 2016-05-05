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

package c2demo.klax;


/**
 * The semaphor controls access to a single shared resource.  Threads must
 * call get() before accessing the resource and call set() after using the
 * resource.
 * A thread can check to see if another thread is waiting on the semaphor
 * by calling poll().
 */
public class Semaphor extends Object {
  /** True if the semaphor is available, otherwise false. */
  protected boolean flag = false;
  /** True if another thread is blocking on the semaphor using get(),
   * otherwise false. */
  protected boolean waiting = false;

  /** Frees the semaphor.  If another thread is blocked on the semaphor
   * it becomes active and takes the semaphor. */
  public synchronized void set() {
	flag = true;
	notify();
  }

  /** Attempts to grabs the semaphor.  If the semaphor is available, the
   * semaphor is taken and the function returns.  If it is unavailable, the
   * calling thread blocks until it can grab the semaphor. */
  public synchronized void get() {
	while (flag == false) {
	  try {
		waiting = true;
		wait();
	  } catch (java.lang.InterruptedException e) {
	  } finally {
		waiting = false;
	  }
	}
	flag = false;
  }

  /** Returns true if any threads are blocked on the semaphor; ie are
   * blocked on the call to get() */
  public synchronized boolean poll() {
	return waiting;
  }
}

