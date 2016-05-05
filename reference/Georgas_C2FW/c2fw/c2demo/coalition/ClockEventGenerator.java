/*
 * Copyright (c) 2001-2002 Regents of the University of California.
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
package c2demo.coalition;

import c2.fw.ArchitectureController;
import c2.fw.NamedPropertyMessage;
import c2.fw.Message;
import c2.fw.Timer;
import c2.fw.TimerListener;
import c2.legacy.AbstractC2Brick;
import c2.fw.Identifier;


/** This class generates tick events. The timer listener filters
 *  out all but 1 in 100 ticks.
 *
 * @author Kari Nies
 */
public class ClockEventGenerator extends AbstractC2Brick
	implements TimerListener {
	
	private int currentTickCount;
	
	public ClockEventGenerator(Identifier id){
		super(id);
	}
	
	public ClockEventGenerator(Identifier id,
		ArchitectureController controller)
	{
		super(id);
		//((Timer)controller).addTimerListener(this);  // redundant
	}
	
	public void init()
	{
	}
	
	public void destroy()
	{
	}
	
	public void begin()
	{
		ClockThread clockThread = new ClockThread();
		clockThread.start();
	}
	
	public void end()
	{
	}
	
	public void handle(Message m)
	{
	}
	
	public void tick(int tickCount)
	{
		NamedPropertyMessage npm;
		currentTickCount = tickCount;
		
		if (currentTickCount != 0 && currentTickCount % 100 == 0) {
			npm = new NamedPropertyMessage("clock");
			npm.addParameter("tick", tickCount);
			sendNotification(npm);
		}
	}
	
	class ClockThread extends Thread{
		public void run(){
			int i = 0;
			while(true){
				try{
					i = i + 100;
					tick(i);
					Thread.sleep(200);
					//Thread.yield();
				}
				catch(InterruptedException e){}
			}
		}
	}
}
