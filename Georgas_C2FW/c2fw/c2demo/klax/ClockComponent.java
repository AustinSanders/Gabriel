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
 * Class:           ClockComponent
 * Author:          Kari Nies
 * Date Created:    May 1, 1997
 * Date Modified:   May 1, 1997
 * Description:     Clock component for Klax game.  Unlike the c++
 *                  clock component, this component runs in its own
 *                  thread.  The clock can be suspended and resumed
 *                  and the interval between ticks can be queried and
 *                  modified.  The clock can generate a specified number
 *                  of minor ticks between major ticks.
 *
 * Notifications
 * -------------
 *    sends:    Tick
 *              MinorTick
 *              GetClockRate(interval=int)
 *              SetClockRate(interval=int)
 *    receives: none
 *
 * Requests
 * --------
 *    sends:    none
 *    receives: SuspendClock
 *              ResumeClock
 *              GetClockRate(interval=int)
 *              SetClockRate(interval=int)
 *
 */


package c2demo.klax;

import c2.fw.*;
import c2.legacy.*;
import java.io.*;


public class ClockComponent extends c2.legacy.AbstractC2Brick implements Runnable {

	protected boolean terminate = false;
	
   protected int deltaMilli;    // interval between ticks
   protected int numMinorTicks; // number of minor ticks between each major tick
   protected int curMinorTick;  // number of current minor ticks
   protected boolean clockOn;   // is clock running or suspended
   protected Thread t;          // clock component thread
   protected Semaphor clockSem = new Semaphor();
			        // semaphore used to suspend clock

   protected KlaxSelectorTable kst;  // table of klax messages


    // category: constructor
    /**
     * constructor creates and initializes the clock
     */
    public ClockComponent( String name, int interval, int minor )
    {
        super( new SimpleIdentifier(name) );
        create( name, interval, minor);
    }


    public ClockComponent( Identifier id )
    {
        super(id);
        create( id.toString(), 150, 3);
    }


    /**
     * initialzies the clock
     */
   private void create(String name, int interval, int minor)
   {
        //super.create(name, FIFOPort.classType());
        deltaMilli = interval;
        clockOn = true;
        numMinorTicks = minor;
        curMinorTick = 0;
        clockSem = new Semaphor();
    }


    public void destroy()
    {
    }


    // category: startup and cleanup
    public void init()
    {
    }


    public void begin()
    {
    }


    public void end()
    {
    }


    // category: threads
    /** init/start thread */
    public void start()
    {
        super.start();
        t = new Thread(this);
        clockOn = false;
        t.start();
    }


    /** stop thread */
    public void stop()
    {
        terminate = true;
        clockSem.set();
    }


    /** The run method is called when the thread is created.  It
     *  continually generates clock tick notifications at a specified
     *  interval.  It can be suspened and resumed by sending the
     *  component SuspendClock and ResumeClock requests respectively.
     *  The interval can be queried or modified by sending the component
     *  GetClockRate and SetClockRate requests respectively.
     */
    public void run()
    {
        NamedPropertyMessage tickNotification = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.Tick );
        NamedPropertyMessage minorNotification = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.MinorTick );
        while (true) {
        	if(terminate) return;
             // wait for the resumeClock if clock is suspended
             if (!clockOn)
             {
                 clockSem.get();
             }
           	if(terminate) return;
             if (curMinorTick >= numMinorTicks)
             {
                sendDefault(tickNotification);
                curMinorTick = 0;
            }
            else
            {
                sendDefault(minorNotification);
                ++curMinorTick;
            }
            try
            {
                Thread.sleep(deltaMilli);
            }
            catch (java.lang.InterruptedException e)
            {
                System.err.println("Caught InterruptedException: "
			       + e.getMessage());
            }
        }
    }


    // category: c2 message processing
    private void sendDefault( Message m )
    {
        if( Utils.isC2Notification(m) )
        {
            sendToAll( m, bottomIface );
        }
        else if( Utils.isC2Request(m) )
        {
            sendToAll( m, topIface );
        }
    }


    public synchronized void handle( Message m )
    {
        if( Utils.isC2Notification(m) )
        {
            handleNotification( (NamedPropertyMessage)m );
        }
        else
        {
            handleRequest( (NamedPropertyMessage)m );
        }
    }


    public synchronized void handleRequest( NamedPropertyMessage r )
    {
        String message_name = r.getName();
        NamedPropertyMessage n;

        if (message_name.equals(KlaxSelectorTable.SuspendClock))
        {
            clockOn = false;
            n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ClockSuspended );
            sendDefault(n);
        }
        else if (message_name.equals(KlaxSelectorTable.ResumeClock))
        {
            clockOn = true;
            clockSem.set();
        }
        else if (message_name.equals(KlaxSelectorTable.SetClockRate))
        {
            int newDelta = ((Integer)r.getParameter("interval")).intValue();
            if (newDelta > 0 && newDelta < 10000)
            {
                deltaMilli = newDelta;
                n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.GetClockRate );
                n.addParameter ("interval", new Integer(deltaMilli));
                sendDefault(n);
            }
        }
        else if (message_name.equals(KlaxSelectorTable.GetClockRate))
        {
             n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.GetClockRate );
             n.addParameter ("interval", new Integer(deltaMilli));
            sendDefault(n);
        }
   }

    /**  clock receives no notifications */
    public synchronized void handleNotification( NamedPropertyMessage n )
    {
    }
}
