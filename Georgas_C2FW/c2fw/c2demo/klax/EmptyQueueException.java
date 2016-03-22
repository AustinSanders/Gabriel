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
 * Class:           EmptyQueueException
 * Author:          Walter Korman
 * Date Created:    March 9, 1996
 * Date Modified:   March 9, 1996
 * Description:     Thrown when an attempt is made to work with an
 *                  empty Queue.
 *                  (c) 1996 by Walter Korman.  Free for public use as
 *                  long as this header remains unmodified.
 */

package c2demo.klax;

/**
 * Signals that the queue is empty.
 * @see Queue
 */
public
class EmptyQueueException extends RuntimeException {
    /**
     * Constructs a new EmptyQueueException with no detail message.
     * A detail message is a String that describes the exception.
     */
    public EmptyQueueException() {
    }
}

