package c2demo.klax;

import java.applet.*;
import java.util.*;
import c2.fw.*;
import c2.legacy.conn.BusConnector;
import c2demo.comp.*;
import c2demo.comp.graphics.*;


class SpellingKlaxVisualizationArchitecture
{
    public SpellingKlaxVisualizationArchitecture( String name )
    {
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

		Identifier ClockComponent1Id = new SimpleIdentifier("ClockComponent1");
		Identifier StatusComponent1Id = new SimpleIdentifier("StatusComponent1");
		Identifier RelativePositionLogic1Id = new SimpleIdentifier("RelativePositionLogic1");
		Identifier WellComponent1Id = new SimpleIdentifier("WellComponent1");
		Identifier PaletteComponent1Id = new SimpleIdentifier("PaletteComponent1");
		Identifier ChuteComponent1Id = new SimpleIdentifier("ChuteComponent1");
		Identifier MatchingLogic1Id = new SimpleIdentifier("MatchingLogic1");
		Identifier StatusLogic1Id = new SimpleIdentifier("StatusLogic1");
		Identifier NextLetterTileLogic1Id = new SimpleIdentifier("NextLetterTileLogic1");
		Identifier LetterTileArtist1Id = new SimpleIdentifier("LetterTileArtist1");
		Identifier ChuteArtist1Id = new SimpleIdentifier("ChuteArtist1");
		Identifier PaletteArtist1Id = new SimpleIdentifier("PaletteArtist1");
		Identifier WellArtist1Id = new SimpleIdentifier("WellArtist1");
		Identifier StatusArtist1Id = new SimpleIdentifier("StatusArtist1");
		Identifier LayoutManager1Id = new SimpleIdentifier("LayoutManager1");
		Identifier GraphicsBindingId = new SimpleIdentifier("GraphicsBinding");
		Identifier bus1Id = new SimpleIdentifier("bus1");
		Identifier bus2Id = new SimpleIdentifier("bus2");
		Identifier bus3Id = new SimpleIdentifier("bus3");
		Identifier bus4Id = new SimpleIdentifier("bus4");
		Identifier bus5Id = new SimpleIdentifier("bus5");
		Identifier bus6Id = new SimpleIdentifier("bus6");

		try
        {
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.ClockComponent"),
				ClockComponent1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.StatusComponent"),
				StatusComponent1Id);
			//controller.addBrick(new JavaClassBrickDescription("c2demo.klax.ClockComponent"),
			//	ClockComponent1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.RelativePositionLogic"),
				RelativePositionLogic1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.WellComponent"),
				WellComponent1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.PaletteComponent"),
				PaletteComponent1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.ChuteComponent"),
				ChuteComponent1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.SpellingMatchingLogic"),
				MatchingLogic1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.StatusLogic"),
				StatusLogic1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.NextLetterTileLogic"),
				NextLetterTileLogic1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.WellArtist"),
				WellArtist1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.PaletteArtist"),
				PaletteArtist1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.LetterTileArtist"),
				LetterTileArtist1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.ChuteArtist"),
				ChuteArtist1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.StatusArtist"),
				StatusArtist1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.klax.LayoutManager"),
				LayoutManager1Id);
			controller.addBrick(new JavaClassBrickDescription("c2demo.comp.graphics.GraphicsBinding"),
				GraphicsBindingId);
			controller.addBrick(new JavaClassBrickDescription("c2.legacy.conn.BusConnector"),
				bus1Id);
			controller.addBrick(new JavaClassBrickDescription("c2.legacy.conn.BusConnector"),
				bus2Id);
			controller.addBrick(new JavaClassBrickDescription("c2.legacy.conn.BusConnector"),
				bus3Id);
			controller.addBrick(new JavaClassBrickDescription("c2.legacy.conn.BusConnector"),
				bus4Id);
			controller.addBrick(new JavaClassBrickDescription("c2.legacy.conn.BusConnector"),
				bus5Id);
			controller.addBrick(new JavaClassBrickDescription("c2.legacy.conn.BusConnector"),
				bus6Id);
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}

		controller.addWeld(c2.legacy.Utils.createC2Weld(ClockComponent1Id, bus1Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(ChuteComponent1Id, bus1Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(PaletteComponent1Id, bus1Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(WellComponent1Id, bus1Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(StatusComponent1Id, bus1Id));

		controller.addWeld(c2.legacy.Utils.createC2Weld(bus1Id, NextLetterTileLogic1Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus1Id, RelativePositionLogic1Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus1Id, MatchingLogic1Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus1Id, bus2Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus1Id, bus3Id));

		controller.addWeld(c2.legacy.Utils.createC2Weld(MatchingLogic1Id, bus2Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(RelativePositionLogic1Id, bus2Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus2Id, StatusLogic1Id));

		controller.addWeld(c2.legacy.Utils.createC2Weld(bus3Id, WellArtist1Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus3Id, ChuteArtist1Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus3Id, PaletteArtist1Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus3Id, StatusArtist1Id));

		controller.addWeld(c2.legacy.Utils.createC2Weld(WellArtist1Id, bus4Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(ChuteArtist1Id, bus4Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(PaletteArtist1Id, bus4Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(StatusArtist1Id, bus4Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus4Id, bus5Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus4Id, LetterTileArtist1Id));

		controller.addWeld(c2.legacy.Utils.createC2Weld(LetterTileArtist1Id, bus5Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus5Id, LayoutManager1Id));

		controller.addWeld(c2.legacy.Utils.createC2Weld(LayoutManager1Id, bus6Id));
		controller.addWeld(c2.legacy.Utils.createC2Weld(bus6Id, GraphicsBindingId));

		try
        {
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
		catch(InterruptedException e)
        {
			e.printStackTrace();
			return;
		}

		controller.begin(bus1Id);
		controller.begin(bus2Id);
		controller.begin(bus3Id);
		controller.begin(bus4Id);
		controller.begin(bus5Id);
		controller.begin(bus6Id);

		controller.begin(ClockComponent1Id);
		controller.begin(StatusComponent1Id);
		controller.begin(RelativePositionLogic1Id);
		controller.begin(WellComponent1Id);
		controller.begin(PaletteComponent1Id);
		controller.begin(ChuteComponent1Id);
		controller.begin(MatchingLogic1Id);
		controller.begin(StatusLogic1Id);
		controller.begin(NextLetterTileLogic1Id);
		controller.begin(LetterTileArtist1Id);
		controller.begin(ChuteArtist1Id);
		controller.begin(PaletteArtist1Id);
		controller.begin(WellArtist1Id);
		controller.begin(StatusArtist1Id);
		controller.begin(LayoutManager1Id);
		controller.begin(GraphicsBindingId);

		/*
		try
        {
			while(true)
            {
				((SteppableArchitectureEngine)controller).step();
				try{
					Thread.sleep(50);
				}
				catch(InterruptedException e)
                {
				}
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
		*/
    }
}

public class SpellingKlaxVisualization extends Applet
{
	static public void main(String argv[])
	{
		SpellingKlaxVisualizationArchitecture sv_arch =
			new SpellingKlaxVisualizationArchitecture("SpellingKlaxVisualizationArchitecture");
	}

	public void init ()
	{
		SpellingKlaxVisualizationArchitecture sv_arch =
			new SpellingKlaxVisualizationArchitecture("SpellingKlaxVisualizationArchitecture");
	}
}
