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
 * Class:           BadTileIdException
 * Author:          Kari Nies
 * Date Created:    April 29, 1997
 * Date Modified:   April 29, 1997
 * Description:     Thrown when an attempt is made to work with a tile
 *                  whose id is out of bounds
 */

package c2demo.klax;

/**
 * Signals that a given TileId is out of bounds for a particular
 * tile subclass.
 */
public
class BadTileIdException extends RuntimeException {
    /**
     * Constructs a new BadTileIdException with no detail message.
     */
    public BadTileIdException() {
    }
}

