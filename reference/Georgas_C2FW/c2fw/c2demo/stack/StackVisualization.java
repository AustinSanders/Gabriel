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


package c2demo.stack;

import java.applet.*;
import java.lang.*;
import java.util.*;
import c2.fw.*;
import c2.legacy.conn.BusConnector;
import c2demo.comp.*;
import c2demo.comp.graphics.*;


class StackVisualizationArchitecture{
	
	public StackVisualizationArchitecture (String name){
		
		OneQueuePerInterfaceMessageHandler handler = new OneQueuePerInterfaceMessageHandler();
		//ThreadPoolArchitectureEngine engine = new ThreadPoolArchitectureEngine(1);
		OneThreadPerBrickArchitectureEngine engine = new OneThreadPerBrickArchitectureEngine();
		//OneThreadSteppableArchitectureEngine engine = new OneThreadSteppableArchitectureEngine();
		SimpleArchitectureManager manager = new SimpleArchitectureManager();

		System.out.println("Creating...");
		
		//ArchitectureController controller = ArchitectureControllerFactory.createController(
		//	manager, handler, engine);
		ArchitectureController controller = ArchitectureControllerFactory.createController(
			manager, handler, engine, new Class[]{SteppableArchitectureEngine.class});
		
		Identifier adtId = new SimpleIdentifier("StackADT");
		Identifier artistId = new SimpleIdentifier("StackArtist");
		Identifier artist2Id = new SimpleIdentifier("StackPieArtist");
		Identifier bindingId = new SimpleIdentifier("GraphicsBinding");
		Identifier main_busId = new SimpleIdentifier("MainBus");
		Identifier binding_busId = new SimpleIdentifier("BindingBus");
		
		try{
			//JavaNetBrickLoader jblNet1 = new JavaNetBrickLoader("http://erasmus.ics.uci.edu/testnet/");
			//JavaNetBrickLoader jblNet2 = new JavaNetBrickLoader("http://erasmus.ics.uci.edu/testnet2/");
			
			controller.addBrick(new JavaClassBrickDescription("c2demo.comp.StackADTThread"),
				adtId);
			controller.addBrick(new JavaClassBrickDescription("c2demo.comp.StackArtist"),
				artistId);
			controller.addBrick(new JavaClassBrickDescription("c2demo.comp.graphics.GraphicsBinding"),
				bindingId);
			controller.addBrick(new JavaClassBrickDescription("c2.legacy.conn.BusConnector"),
				main_busId);
			controller.addBrick(new JavaClassBrickDescription("c2.legacy.conn.BusConnector"),
				binding_busId);
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		controller.addWeld(c2.legacy.Utils.createC2Weld(adtId, main_busId));
		controller.addWeld(c2.legacy.Utils.createC2Weld(main_busId, artistId));
		controller.addWeld(c2.legacy.Utils.createC2Weld(artistId, binding_busId));
		controller.addWeld(c2.legacy.Utils.createC2Weld(binding_busId, bindingId));
		
		try{
			System.err.println("Starting the engine.");
			controller.startEngine();
			System.err.println("Waiting for engine to start.");
			controller.waitEngineState(ArchitectureEngine.ENGINESTATE_STARTED);
			System.err.println("Engine started.");

			c2.util.GUIEngineManager engMgr = new c2.util.GUIEngineManager(controller);

			System.err.println("Starting all bricks.");
			controller.startAll();
			System.err.println("Waiting for all bricks to start.");
			controller.waitStateAll(ArchitectureEngine.STATE_OPEN_RUNNING);
			System.err.println("Bricks started.");
		}
		catch(InterruptedException e){
			e.printStackTrace();
			return;
		}
		
		controller.begin(main_busId);
		controller.begin(binding_busId);

		controller.begin(adtId);
		controller.begin(artistId);
		controller.begin(bindingId);
	
		try{
			Thread.sleep(10000);
			System.err.println("Suspending StackADT");
			controller.suspend(adtId);
			System.err.println("Waiting for suspend");
			controller.waitState(adtId, ArchitectureEngine.STATE_OPEN_NOTRUNNING_SUSPENDED);
			System.err.println("Suspended.");
			Thread.sleep(10000);
			
			System.err.println("Resuming StackADT");
			controller.resume(adtId);
			System.err.println("Waiting for resume");
			controller.waitState(adtId, ArchitectureEngine.STATE_OPEN_RUNNING);
			System.err.println("Resumed.");

			Thread.sleep(10000);
			
			System.err.println("Suspending the architecture");
			controller.suspendAll();
			System.err.println("Waiting for suspend.");
			controller.waitStateAll(ArchitectureEngine.STATE_OPEN_NOTRUNNING_SUSPENDED);
			System.err.println("Suspended. Welding in new component");
			
			controller.addBrick(new JavaClassBrickDescription("c2demo.comp.StackPieArtist"),
				artist2Id);
			controller.addWeld(c2.legacy.Utils.createC2Weld(main_busId, artist2Id));
			controller.addWeld(c2.legacy.Utils.createC2Weld(artist2Id, binding_busId));

			Thread.sleep(1000);
			
			System.err.println("Starting new component.");
			controller.start(artist2Id);
			System.err.println("Waiting for start.");
			controller.waitState(artist2Id, ArchitectureEngine.STATE_OPEN_RUNNING);
			System.err.println("Beginning new component.");
			controller.begin(artist2Id);
			System.err.println("Resuming architecture.");
			controller.resumeAll();
			System.err.println("Waiting for resume architecture.");
			controller.waitStateAll(ArchitectureEngine.STATE_OPEN_RUNNING);
			System.err.println("Resumed.");
			
			/*
			while(true){
				((SteppableArchitectureEngine)controller).step();
				try{
					Thread.sleep(10);
				}
				catch(InterruptedException e){
				}
			}
			*/
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
			
	}
}

/**
 * StackVisualization class.<p>
 *
 * Implements a C2 architecture that provides two different
 * visualizations for a stack.  One visualization displays the entire
 * contents of the stack, while the other one displays only the value of the
 * top stack element and an arc from zero to <i>stack_top <b>mod</b> 360</i>
 * degrees. <p>
 *
 * Executes both as a standalone application and an applet.
 *
 * The architecture looks as follows:<p>
 *
 * <MAP NAME="StackVisualization"> <AREA SHAPE="RECT" COORDS="123,14,204,67" HREF="c2.comp.StackADTThread.html" > <AREA SHAPE="RECT" COORDS="204,149,285,203" HREF="c2.comp.StackPieArtist.html" > <AREA SHAPE="RECT" COORDS="42,149,123,203" HREF="c2.comp.StackArtist.html"> <AREA SHAPE="RECT" COORDS="123,284,204,338" HREF="c2.comp.graphics.GraphicsBinding.html"> <AREA SHAPE="RECT" COORDS="15,229,313,258" HREF="c2.framework.Connector.html" > <AREA SHAPE="RECT" COORDS="15,94,313,123" HREF="c2.framework.Connector.html" > </MAP>
 * <IMG SRC="StackVisualization.gif" border=0 ISMAP USEMAP="#StackVisualization">
 *
 * <p>
 * A textual equivalent of the above architecture is displayed once the
 * architecture is built.
 *
 * @see StackADTThread
 * @see StackArtist
 * @see StackPieArtist
 * @see GraphicsBinding
 *
 */

public class StackVisualization extends Applet
{
	static public void main(String argv[]) 
	{
		StackVisualizationArchitecture sv_arch =
			new StackVisualizationArchitecture("StackVisualizationArchitecture");
	}
	
	public void init ()
	{
		StackVisualizationArchitecture sv_arch =
			new StackVisualizationArchitecture("StackVisualizationArchitecture");
	}
}
