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
 * Class:           Tile
 * Author:          Kari Nies
 * Date Created:    May 1, 1997
 * Date Modified:   May 1, 1997
 * Description:     Chute Class for Klax game
 */


package c2demo.klax;


public class Chute extends Object {
	
	private Queue[] queues;
	private int numQueues;
	private int queueLen;
	private Tile spaceTile;
	
	/**
	 * constructor.  allocates and initializes the chute.  initially
	 * the chute is filled with space tiles.
	 */
	public Chute(int rows, int cols) {
		
		numQueues = cols;
		queueLen = rows;
		queues = new Queue[numQueues];
		spaceTile = new Tile(Tile.SpaceTile);
		
		// initialize chute to all spaces
		for (int i = 0; i < numQueues; ++i) {
			queues[i] = new Queue(queueLen);
			for (int j = 0; j < queueLen; ++j) {
				queues[i].enqueue(spaceTile);
			}
		}
	}
	
	/**
	 * returns the number of rows in the chute
	 */
	public int getNumRows() {
		return queueLen;
	}
	
	/**
	 * returns the number of columns in the chute
	 */
	public int getNumCols() {
		return numQueues;
	}
	
	public void clearAllChutes(){
		// initialize chute to all spaces
		for (int i = 0; i < numQueues; ++i) {
			queues[i] = new Queue(queueLen);
			for (int j = 0; j < queueLen; ++j) {
				queues[i].enqueue(spaceTile);
			}
		}
	}
	
	/**
	 * Places a new tile on the chute at the given location (column).
	 * Places space tiles at each remaining column.  This will
	 * cause a tile to be dropped from each column.  If a non-space
	 * tile is dropped,  returns the tile along with the column
	 * from which it dropped, otherwise returns null.
	 */
	public DroppedTile placeTile(int location, Tile newTile) {
		DroppedTile dt = null;
		int i;
		
		// dequeue tile from each queue.  this represents the
		// tiles that have fallen off the chute.  return a tile
		// pair if we find a non-space tile, otherwise return null.
		for (i = 0; i < numQueues; ++i) {
			Tile droppedTile = (Tile) queues[i].dequeue();
			if (!droppedTile.isSpace()) {
				// keep track of dropped tile, there will be at most one
				dt = new DroppedTile(i, droppedTile);
			}
		}
		// enqueue a the new tile to the queue at the given location
		// enqueue spaces onto the remaining queues
		if (!newTile.isSpace()) {
			for (i = 0; i < numQueues; ++i) {
				if (i==location) {
					queues[i].enqueue(newTile);
				} else {
					queues[i].enqueue(spaceTile);
				}
			}
		} else {
			for (i = 0; i < numQueues; ++i) {
				queues[i].enqueue(spaceTile);
			}
		}
		return dt;
	}
	
	
	/**
	 * Place a space tile at each column of the chute.  This will
	 * cause a tile to be dropped from each column. If a non-space
	 * tile is dropped,  returns the tile along with the column
	 * from which it dropped, otherwise returns null.
	 */
	public DroppedTile advanceTiles() {
		return placeTile(1, spaceTile);
	}
	
	
	public String toString ()
	{
		String return_string;
		
		return_string = "Chute: \n";
		for (int i = 0; i < numQueues; ++i) {
			return_string = return_string + queues[i].queueToString() + "\n";
		}
		return return_string;
	}
	
}
