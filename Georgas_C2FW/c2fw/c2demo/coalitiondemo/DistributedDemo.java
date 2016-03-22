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
package c2demo.coalitiondemo;

// C2-related packages
import c2.fw.*;
import c2.legacy.*;
import c2.legacy.conn.BusConnector;
import c2.conn.fred.*;

//Swing Stuff
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * This class is responsible for starting the program, initializing all
 * initial components and connectors.
 *
 * This is a distributed version of the coalition demo which
 * creates one US and one French Command and Control in separate
 * processes.  Each process has its own share button that, when selected,
 * adds new us-to-french or french-to-us filter and connects the filter
 * to the other country's information connector using FRED connectors.
 *
 * @author Kari Nies
 * @author Justin Erenkrantz
 * @author Eric Dashofy
 */
public class DistributedDemo {

    public static final String COUNTRY_US = "USA";
    public static final String COUNTRY_FRANCE = "FRANCE";

    private String currentCountry;
    private String[] fredHostString;

    private OneQueuePerInterfaceMessageHandler handler;
    private ArchitectureEngine engine;
    private SimpleArchitectureManager manager;
    private ArchitectureController controller;

    public DistributedDemo(String currentCountry,
                           String[] fredHostString,
                           String args[])
    {
        if (currentCountry.equals(COUNTRY_US)) {
            this.currentCountry = COUNTRY_US;
        } else if(currentCountry.equals(COUNTRY_FRANCE)){
            this.currentCountry = COUNTRY_FRANCE;
        } else {
            throw new IllegalArgumentException
                ("Invalid country: " + currentCountry);
        }
        this.fredHostString = fredHostString;

        Class[] classes;
        handler = new OneQueuePerInterfaceMessageHandler();
        engine  = new OneThreadPerBrickArchitectureEngine();
        manager = new SimpleArchitectureManager();
        classes = new Class[]{};

        controller = ArchitectureControllerFactory.createController
            (manager, handler, engine, classes);
    }

    /**
     * CommandAndControlArch class allows us to replicate this part
     * of the architecture for both the US and French Command and
     * Control systems.
     */
    class CommandAndControlArch {
        public FredConnector fredConn;
        public BusConnector infoConn;
        public USCommandAndControl usCommandAndControl;
        public FrenchCommandAndControl frCommandAndControl;
        public BusConnector displayConn;
        public UnknownArtist unknownArtist;
        public NeutralArtist neutralArtist;
        public FriendArtist friendArtist;
        public HostileArtist hostileArtist;
        public BusConnector artistConn;
        public FixedWingArtist fixedwingArtist;
        public BattleshipArtist battleshipArtist;
        public TroupsArtist troupsArtist;
        public AirDefenseMissileArtist admArtist;
        public CarrierArtist carrierArtist;
        public AWACSArtist awacsArtist;
        public OperationsArtist opsArtist;
        public BusConnector entityConn;
        public LandRetrieval landRetrieval;
        //public LocalLandRetrieval landRetrieval;
        public BusConnector screenConn;
        public VirtualScreen virtualScreen;

        public Identifier fredConnId;
        public Identifier infoConnId;
        public Identifier commandAndControlId;
        public Identifier displayConnId;
        public Identifier unknownArtistId;
        public Identifier neutralArtistId;
        public Identifier friendArtistId;
        public Identifier hostileArtistId;
        public Identifier artistConnId;
        public Identifier fixedwingArtistId;
        public Identifier battleshipArtistId;
        public Identifier troupsArtistId;
        public Identifier admArtistId;
        public Identifier carrierArtistId;
        public Identifier awacsArtistId;
        public Identifier opsArtistId;
        public Identifier entityConnId;
        public Identifier screenConnId;
        public Identifier landRetrievalId;
        public Identifier virtualScreenId;

        public CommandAndControlArch (String country,
                                      LocalArchitectureManager lam) {
            fredConnId =
                new SimpleIdentifier(country + " EXTERNAL PROCESS CONNECTOR");
            infoConnId  =
                new SimpleIdentifier(country + " INFO CONNECTOR");
            commandAndControlId =
                new SimpleIdentifier(country + " COMMAND AND CONTROL");
            displayConnId  =
                new SimpleIdentifier(country + " DISPLAY CONNECTOR");
            unknownArtistId  =
                new SimpleIdentifier(country + " UNKNOWN ARTIST");
            neutralArtistId  =
                new SimpleIdentifier(country + " NEUTRAL ARTIST");
            friendArtistId  =
                new SimpleIdentifier(country + " FRIEND ARTIST");
            hostileArtistId  =
                new SimpleIdentifier(country + " HOSTILE ARTIST");
            artistConnId  =
                new SimpleIdentifier(country + " ARTIST CONNECTOR");
            fixedwingArtistId =
                new SimpleIdentifier(country + " FIXEDWING ARTIST");
            battleshipArtistId  =
                new SimpleIdentifier(country + " BATTLESHIP ARTIST");
            troupsArtistId  =
                new SimpleIdentifier(country + " TROUPS ARTIST");
            carrierArtistId =
                new SimpleIdentifier(country + " CARRIER ARTIST");
            admArtistId =
                new SimpleIdentifier(country + " AIR DEF MISSILE ARTIST");
            awacsArtistId =
                new SimpleIdentifier(country + " AWACS ARTIST");
            opsArtistId =
                new SimpleIdentifier(country + " OPERATIONS ARTIST");
            entityConnId  =
                new SimpleIdentifier(country + " ENTITY CONNECTOR");
            landRetrievalId  =
                new SimpleIdentifier(country + " LAND RETRIEVAL");
            screenConnId  =
                new SimpleIdentifier(country + " SCREEN CONNECTOR");
            virtualScreenId  =
                new SimpleIdentifier(country + " VIRTUAL SCREEN");

            // *** create connectors
            InitializationParameter[]	params = new InitializationParameter
            	[fredHostString.length+1];
            for (int i = 0; i<fredHostString.length; i++) {
                params[i] = new InitializationParameter("host", fredHostString[i]);
            }
            params[fredHostString.length] = new InitializationParameter("groupName",
                    currentCountry + " FRED");
            fredConn = new FredConnector(fredConnId, params);
            /*
            fredConn = new FredConnector
                (fredConnId,
                 new InitializationParameter[]{
                     new InitializationParameter("host", fredHostString),
                     new InitializationParameter("groupName",
                                                 currentCountry + " FRED")
                 });
            */
            lam.addBrick(fredConn);

            infoConn = new BusConnector(infoConnId);
            lam.addBrick(infoConn);

            displayConn = new BusConnector(displayConnId);
            lam.addBrick(displayConn);

            artistConn = new BusConnector(artistConnId);
            lam.addBrick(artistConn);

            entityConn = new BusConnector(entityConnId);
            lam.addBrick(entityConn);

            screenConn = new BusConnector(screenConnId);
            lam.addBrick(screenConn);

            // *** create components
            if(currentCountry.equals(COUNTRY_US)){
                usCommandAndControl =
                    new USCommandAndControl(commandAndControlId);
                lam.addBrick(usCommandAndControl);
            }
            else if (currentCountry.equals(COUNTRY_FRANCE)) {
                frCommandAndControl =
                    new FrenchCommandAndControl(commandAndControlId);
                lam.addBrick(frCommandAndControl);
            }

            // affiliation artists
            unknownArtist = new UnknownArtist(unknownArtistId);
            lam.addBrick(unknownArtist);

            neutralArtist = new NeutralArtist(neutralArtistId);
            lam.addBrick(neutralArtist);

            friendArtist = new FriendArtist(friendArtistId);
            lam.addBrick(friendArtist);

            hostileArtist = new HostileArtist(hostileArtistId);
            lam.addBrick(hostileArtist);

            // entity artists
            fixedwingArtist = new FixedWingArtist(fixedwingArtistId);
            lam.addBrick(fixedwingArtist);

            battleshipArtist = new BattleshipArtist(battleshipArtistId);
            lam.addBrick(battleshipArtist);

            troupsArtist = new TroupsArtist(troupsArtistId);
            lam.addBrick(troupsArtist);

            carrierArtist = new CarrierArtist(carrierArtistId);
            lam.addBrick(carrierArtist);

            admArtist = new AirDefenseMissileArtist(admArtistId);
            lam.addBrick(admArtist);

            awacsArtist = new AWACSArtist(awacsArtistId);
            lam.addBrick(awacsArtist);

            opsArtist = new OperationsArtist(opsArtistId);
            lam.addBrick(opsArtist);

            // land retrieval
            landRetrieval = new LandRetrieval(landRetrievalId);
            //landRetrieval = new LocalLandRetrieval(landRetrievalId);
            lam.addBrick(landRetrieval);

            // virtual screen
            virtualScreen = new VirtualScreen(virtualScreenId, country);
            lam.addBrick(virtualScreen);

            // weld components and connectors
            lam.addWeld
                (new SimpleWeld
                 (new BrickInterfaceIdPair(fredConn.getIdentifier(),
                                           FredConnector.LOCAL_INTERFACE_ID),
                  new BrickInterfaceIdPair(infoConnId,
                                           AbstractC2Brick.TOP_INTERFACE_ID)
                  )
                 );

            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (infoConnId, commandAndControlId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (commandAndControlId, displayConnId));

            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (displayConnId, unknownArtistId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (displayConnId, neutralArtistId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (displayConnId, friendArtistId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (displayConnId, hostileArtistId));

            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (unknownArtistId, artistConnId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (neutralArtistId, artistConnId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (friendArtistId, artistConnId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (hostileArtistId, artistConnId));

            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (artistConnId, fixedwingArtistId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (artistConnId, battleshipArtistId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (artistConnId, troupsArtistId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (artistConnId, carrierArtistId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (artistConnId, admArtistId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (artistConnId, awacsArtistId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (artistConnId, opsArtistId));

            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (fixedwingArtistId, entityConnId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (battleshipArtistId, entityConnId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (troupsArtistId, entityConnId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (carrierArtistId, entityConnId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (admArtistId, entityConnId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (awacsArtistId, entityConnId));
            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (opsArtistId, entityConnId));

            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (entityConnId, screenConnId));

            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (landRetrievalId, screenConnId));

            lam.addWeld(c2.legacy.Utils.createC2Weld
                        (screenConnId, virtualScreenId));
        }
    }


    public void addUS2USFilter(Identifier usSensorConnId,
                               Identifier usInfoConn,
                               LocalArchitectureManager lam) {
        // add US to US filter
        Identifier u2uFilterId = new SimpleIdentifier
            ("US TO US FILTER");
        UStoUSFilter u2uFilter = new UStoUSFilter(u2uFilterId);
        lam.addBrick(u2uFilter);

        lam.addWeld(c2.legacy.Utils.createC2Weld(usSensorConnId, u2uFilterId));
        lam.addWeld(c2.legacy.Utils.createC2Weld(u2uFilterId, usInfoConn));
    }

    public void addFrench2FrenchFilter(Identifier frSensorConnId,
                                       Identifier frInfoConn,
                                       LocalArchitectureManager lam) {
        // add French to French filter
        Identifier f2fFilterId = new SimpleIdentifier
            ("FRENCH TO FRENCH FILTER");
        FrenchtoFrenchFilter f2fFilter = new FrenchtoFrenchFilter(f2fFilterId);
        lam.addBrick(f2fFilter);

        lam.addWeld(c2.legacy.Utils.createC2Weld(frSensorConnId, f2fFilterId));
        lam.addWeld(c2.legacy.Utils.createC2Weld(f2fFilterId, frInfoConn));
    }

    public void addUS2FrenchFilter(Identifier usSensorConnId,
                                   LocalArchitectureManager lam) {
        // add US to French filter
        Identifier u2fFilterId = new SimpleIdentifier
            ("US TO FRANCE FILTER");
        UStoFrenchFilter u2fFilter = new UStoFrenchFilter(u2fFilterId);
        lam.addBrick(u2fFilter);

        InitializationParameter[]	params = new InitializationParameter
    	[fredHostString.length+1];
	    for (int i = 0; i<fredHostString.length; i++) {
	        params[i] = new InitializationParameter("host", fredHostString[i]);
	    }
	    params[fredHostString.length] = new InitializationParameter("groupName",
	            COUNTRY_FRANCE + " FRED");
	    FredConnector localFredConnector =
	        new FredConnector
	        (new SimpleIdentifier("US TO FRANCE FRED CONNECTOR (US SIDE)"),
	         params);
        /*
        FredConnector localFredConnector =
            new FredConnector
            (new SimpleIdentifier("US TO FRANCE FRED CONNECTOR (US SIDE)"),
             new InitializationParameter[] {
                 new InitializationParameter("host", fredHostString),
                 new InitializationParameter("groupName", COUNTRY_FRANCE + " FRED")
             }
             );
        */
        lam.addBrick(localFredConnector);

        lam.addWeld(c2.legacy.Utils.createC2Weld(usSensorConnId, u2fFilterId));
        lam.addWeld(new SimpleWeld
                    (
                     new BrickInterfaceIdPair(u2fFilterId,
                                              AbstractC2Brick.BOTTOM_INTERFACE_ID),
                     new BrickInterfaceIdPair(localFredConnector.getIdentifier(),
                                              FredConnector.LOCAL_INTERFACE_ID)
                     )
                    );

        // make sure we start up these new components
        ArchitectureController controller = lam.getController();
        controller.startAll();
    }


    public void addFrench2USFilter(Identifier frSensorConnId,
                                   LocalArchitectureManager lam) {
        // add French to US filter
        Identifier f2uFilterId = new SimpleIdentifier
            ("FRENCH TO US FILTER");
        FrenchtoUSFilter f2uFilter = new FrenchtoUSFilter(f2uFilterId);
        lam.addBrick(f2uFilter);

        InitializationParameter[]	params = new InitializationParameter
    	[fredHostString.length+1];
	    for (int i = 0; i<fredHostString.length; i++) {
	        params[i] = new InitializationParameter("host", fredHostString[i]);
	    }
	    params[fredHostString.length] = new InitializationParameter("groupName",
	            COUNTRY_US + " FRED");
        FredConnector localFredConnector =
            new FredConnector
            (new SimpleIdentifier("FRANCE TO US FRED CONNECTOR (FRANCE SIDE)"),
                    params);
	    /*
        FredConnector localFredConnector =
            new FredConnector
            (new SimpleIdentifier("FRANCE TO US FRED CONNECTOR (FRANCE SIDE)"),
             new InitializationParameter[]{
                 new InitializationParameter("host", fredHostString),
                 new InitializationParameter("groupName", COUNTRY_US + " FRED")
             }
             );
        */
        lam.addBrick(localFredConnector);

        lam.addWeld(c2.legacy.Utils.createC2Weld(frSensorConnId, f2uFilterId));
        lam.addWeld(new SimpleWeld
                    (
                     new BrickInterfaceIdPair(f2uFilterId,
                                              AbstractC2Brick.BOTTOM_INTERFACE_ID),
                     new BrickInterfaceIdPair(localFredConnector.getIdentifier(),
                                              FredConnector.LOCAL_INTERFACE_ID)
                     )
                    );

        // make sure we start up these new components
        ArchitectureController controller = lam.getController();
        controller.startAll();
    }

    public void setup()
    {
        LocalArchitectureManager lam;
        ClockEventGenerator ceg;
        BusConnector clockConn;
        BusConnector sensorConn;

        Identifier cegId = new SimpleIdentifier("CLOCK EVENT GENERATOR");
        Identifier clockConnId  = new SimpleIdentifier("CLOCK CONNECTOR");
        Identifier sensorConnId = new SimpleIdentifier
            (currentCountry + " SENSOR CONNECTOR");

        lam = (LocalArchitectureManager)controller;

        ceg = new ClockEventGenerator(cegId, controller);
        lam.addBrick(ceg);

        clockConn = new BusConnector(clockConnId);
        lam.addBrick(clockConn);

        lam.addWeld(c2.legacy.Utils.createC2Weld(cegId, clockConnId));

        sensorConn = new BusConnector(sensorConnId);
        lam.addBrick(sensorConn);

        if(currentCountry.equals(COUNTRY_US)){
            USRadar usRadar;
            Identifier usRadarId = new SimpleIdentifier("US RADAR");

            usRadar = new USRadar(usRadarId);
            lam.addBrick(usRadar);

            // weld components to connectors
            lam.addWeld(c2.legacy.Utils.createC2Weld(clockConnId, usRadarId));
            lam.addWeld(c2.legacy.Utils.createC2Weld(usRadarId, sensorConnId));

            System.out.println("Creating USA C&C");
            CommandAndControlArch usCommandAndControl =
                new CommandAndControlArch("US", lam);
            // create and add filters
            addUS2USFilter(sensorConnId, usCommandAndControl.infoConnId, lam);
        }
        else if (currentCountry.equals(COUNTRY_FRANCE)) {
            FrenchRadar frRadar;
            FrenchAWACS frAWACS;
            Identifier frRadarId = new SimpleIdentifier("FRENCH RADAR");
            Identifier frAWACSId = new SimpleIdentifier("FRENCH AWACS");

            frRadar = new FrenchRadar(frRadarId);
            lam.addBrick(frRadar);
            frAWACS = new FrenchAWACS(frAWACSId);
            lam.addBrick(frAWACS);

            // weld components to connectors
            lam.addWeld(c2.legacy.Utils.createC2Weld(clockConnId, frRadarId));
            lam.addWeld(c2.legacy.Utils.createC2Weld(frRadarId, sensorConnId));
            lam.addWeld(c2.legacy.Utils.createC2Weld(clockConnId, frAWACSId));
            lam.addWeld(c2.legacy.Utils.createC2Weld(frAWACSId, sensorConnId));

            System.out.println("Creating French C&C");
            CommandAndControlArch frenchCommandAndControl =
                new CommandAndControlArch("FRENCH", lam);

            // create and add filters
            addFrench2FrenchFilter(sensorConnId,
                                   frenchCommandAndControl.infoConnId, lam);
        }

        new CoalitionButtonFrame(sensorConnId, lam);
    }


    class CoalitionButtonFrame{
        private JFrame frame;

        private Identifier sensorConnId;
        private LocalArchitectureManager lam;

        public CoalitionButtonFrame(Identifier sensorConnId,
                                    LocalArchitectureManager lam){

            this.sensorConnId = sensorConnId;
            this.lam = lam;

            frame = new JFrame(currentCountry + " Share Data");
            frame.getContentPane().setLayout(new BorderLayout());
            JButton makeCoalitionButton = new JButton(currentCountry +
                                                      " Share Data Now");
            makeCoalitionButton.addActionListener
                (new ActionListener() {
                        public void actionPerformed(ActionEvent evt){
                            doMakeCoalition();
                        }
                    }
                 );
            frame.getContentPane().add("Center", makeCoalitionButton);
            frame.setSize(300, 125);
            frame.setLocation(200, 200);
            frame.setVisible(true);
        }

        private void doMakeCoalition(){
            System.out.println("Making coalition now.");
            if (currentCountry.equals(COUNTRY_US)) {
                addUS2FrenchFilter(sensorConnId,lam);
            }
            else if (currentCountry.equals(COUNTRY_FRANCE)) {
                addFrench2USFilter(sensorConnId, lam);
            }
            System.out.println("Done making coalition.");
        }
    }

    public void start()
    {
        LocalArchitectureManager lam;
        lam = (LocalArchitectureManager)controller;

        try {
            System.err.println("Starting the engine.");
            controller.startEngine();
            System.err.println("Waiting for engine to start.");
            controller.waitEngineState(ArchitectureEngine.ENGINESTATE_STARTED);
            System.err.println("Engine started.");

            System.err.println("Starting all bricks.");
            controller.startAll();
            System.err.println("Waiting for all bricks to start.");
            controller.waitStateAll(ArchitectureEngine.STATE_OPEN_RUNNING);
            System.err.println("Bricks started.");
        }
        catch(InterruptedException ie) {
            ie.printStackTrace();
        }

        System.err.println("Beginning all bricks.");
        Brick[] bricks = lam.getAllBricks();
        for(int i = 0; i < bricks.length; i++){
            controller.begin(bricks[i].getIdentifier());
        }
        System.err.println("Done beginning all bricks.");
    }

    private static void argError(){
        System.err.println("Error.  Start with: ");
        System.err.println("  java " + DistributedDemo.class.getName() +
                           " US|France [fredHostString]");
    }


    public static void main(String[] args)
    {
        System.out.println("Starting demo!");

        if(args.length < 1){
            argError();
            return;
        }
        if(!args[0].equals("US")){
            if(!args[0].equals("France")){
                argError();
                return;
            }
        }

        String country = null;
        if(args[0].equals("US")){
            country = COUNTRY_US;
        }
        else if(args[0].equals("France")){
            country = COUNTRY_FRANCE;
        }

        //String hostString = "localhost/-.-.-.*";
        String[]	hostString = null;
        if(args.length >= 2){
            hostString = new String[args.length - 1];
            for (int i = 1; i<args.length; i++) {
                hostString[i-1] = args[i];
            }
        }
        else {
            hostString = new String[] {"localhost"};
        }

        DistributedDemo demo = new DistributedDemo(country, hostString, args);

        demo.setup();
        demo.start();
    }
}
