/*
 * Copyright (c) 2003 Regents of the University of California.
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
package c2demo.coalitiondemo.xadl;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.instance.*;
import edu.uci.isr.xarch.types.*;
import edu.uci.isr.xarch.implementation.*;
import edu.uci.isr.xarch.variants.*;
import edu.uci.isr.xarch.javaimplementation.*;
import edu.uci.isr.xarch.javainitparams.*;
import edu.uci.isr.xarch.boolguard.*;
import edu.uci.isr.xarch.versions.*;
import edu.uci.isr.xarch.messages.*;
import java.util.Arrays;
import java.io.FileOutputStream;
import java.io.PrintStream;

import c2demo.coalitiondemo.*;

/**
 * This class will emit the XML format of the architecture that can be
 * edited with ArchStudio or used to bootstrap the project.
 */
public class FranceDescription extends Description {

    /**
     * Default constructor
     */
    public FranceDescription() {
        super();
    }

    /**
     * Register all of the connector types.
     * @param archTypes
     */
    public void createConnectorTypes(IArchTypes archTypes) {
        archTypes.addConnectorType(
            createConnectorType("FrenchBusConnector_type",
                                "French Bus Connector Type",
                                "c2.legacy.conn.BusConnector",
                                null)
            );
        archTypes.addConnectorType(
            createConnectorType("CommonBusConnector_type",
                                "Common Bus Connector Type",
                                "c2.legacy.conn.BusConnector",
                                null)
            );
        archTypes.addConnectorType(
            createConnectorType("FrenchLocalFredConnector_type",
                                "Fred connector Type",
                                "c2.conn.fred.FredConnector",
                                new IInitializationParameter[]{
                                createInitializationParameter("host",
                                                          "127.0.0.1/-.-.-.-"),
                                createInitializationParameter("groupName",
                                                          "France" + " FRED")}
            ));
    }

    /**
     * Register all of the component types.
     * @param archTypes
     */
    public void createComponentTypes(IArchTypes archTypes) {
        archTypes.addComponentType(
            createComponentType("Clock_type", "Clock Event Generator Type",
                                "c2demo.coalitiondemo.ClockEventGenerator",
                                null)
        );

        archTypes.addComponentType(
            createComponentType("FrenchAWACS_type", "French AWACS Type",
                                "c2demo.coalitiondemo.FrenchAWACS", null)
            );
        archTypes.addComponentType(
            createComponentType("FrenchRadar_type", "French Radar Type",
                                "c2demo.coalitiondemo.FrenchRadar", null)
        );
        archTypes.addComponentType(
            createComponentType("FrenchtoFrenchFilter_type",
                                "French to French Filter Type",
                                "c2demo.coalitiondemo.FrenchtoFrenchFilter",
                                null)
        );

        archTypes.addComponentType(
            createComponentType("FrenchCC_type",
                                "French Command and Control Type",
                                "c2demo.coalitiondemo.FrenchCommandAndControl",
                                null)
        );
        archTypes.addComponentType(
            createComponentType("FrenchUnknownArtist_type",
                                "Unknown Artist Type",
                                "c2demo.coalitiondemo.UnknownArtist", null)
        );
        archTypes.addComponentType(
            createComponentType("FrenchNeutralArtist_type",
                                "Neutral Artist Type",
                                "c2demo.coalitiondemo.NeutralArtist", null)
        );
        archTypes.addComponentType(
            createComponentType("FrenchFriendArtist_type",
                                "Friend Artist Type",
                                "c2demo.coalitiondemo.FriendArtist", null)
        );
        archTypes.addComponentType(
            createComponentType("FrenchHostileArtist_type",
                                "Hostile Artist Type",
                                "c2demo.coalitiondemo.HostileArtist",
                                null)
        );
        archTypes.addComponentType(
            createComponentType("USFixedWingArtist_type",
                                "Fixed Wing Artist Type",
                                "c2demo.coalitiondemo.FixedWingArtist",
                                null)
        );
        archTypes.addComponentType(
            createComponentType("FrenchBattleshipArtist_type",
                                "Battleship Artist Type",
                                "c2demo.coalitiondemo.BattleshipArtist",
                                null)
        );
        archTypes.addComponentType(
            createComponentType("FrenchAWACSArtist_type",
                                "AWACS Artist Type",
                                "c2demo.coalitiondemo.AWACSArtist", null)
        );
        archTypes.addComponentType(
            createComponentType("FrenchAirDefenseMissileArtist_type",
                                "Air Defense Missile Artist Type",
                                "c2demo.coalitiondemo.AirDefenseMissileArtist",
                                null)
        );
        archTypes.addComponentType(
            createComponentType("FrenchTroupsArtist_type",
                                "Troups Artist Type",
                                "c2demo.coalitiondemo.TroupsArtist", null)
        );
        archTypes.addComponentType(
            createComponentType("USCarrierArtist_type",
                                "Carrier Artist Type",
                                "c2demo.coalitiondemo.CarrierArtist", null)
        );
        archTypes.addComponentType(
            createComponentType("FrenchOperationsArtist_type",
                                "Operations Artist Type",
                                "c2demo.coalitiondemo.OperationsArtist", null)
        );
        archTypes.addComponentType(
            createComponentType("FrenchLandRetrieval_type",
                                "Land Retrieval Type",
                                "c2demo.coalitiondemo.LocalLandRetrieval", null)
        );

        archTypes.addComponentType(
            createComponentType("FrenchVirtualScreen_type",
                                "Virtual Screen Type",
                                "c2demo.coalitiondemo.VirtualScreen",
                                new IInitializationParameter[]{
                                createInitializationParameter("Country",
                                                              "France")})
        );
    }

    /**
     * Create components.
     */
    public void createComponents(IArchStructure archStructure) {
        //add components
        archStructure.addComponent(
            createComponent("Clock", "Clock Event Generator Component",
                            "Clock_type")
        );
        archStructure.addComponent(
            createComponent("FrenchRadar", "French Radar Component",
                            "FrenchRadar_type")
        );
        archStructure.addComponent(
            createComponent("FrenchAWACS", "French AWACS Component",
                            "FrenchAWACS_type")
            );
        archStructure.addComponent(
            createComponent("FrenchtoFrenchFilter",
                            "FrenchtoFrench Filter Component",
                            "FrenchtoFrenchFilter_type")
        );
        archStructure.addComponent(
            createComponent("FrenchCC", "French Command & Control Component",
                            "FrenchCC_type")
        );
        archStructure.addComponent(
            createComponent("FrenchUnknownArtist",
                            "Unknown Artist Component ",
                            "FrenchUnknownArtist_type")
        );

        archStructure.addComponent(
            createComponent("FrenchNeutralArtist",
                            "Neutral Artist Component",
                            "FrenchNeutralArtist_type")
        );
        archStructure.addComponent(
            createComponent("FrenchFriendArtist",
                            "Friend Artist Component",
                            "FrenchFriendArtist_type")
        );
        archStructure.addComponent(
            createComponent("FrenchHostileArtist",
                            "Hostile Artist Component",
                            "FrenchHostileArtist_type")
        );

        archStructure.addComponent(
            createComponent("USFixedWingArtist",
                            "FixedWing Artist Component",
                            "USFixedWingArtist_type")
        );
        archStructure.addComponent(
            createComponent("FrenchBattleshipArtist",
                            "Battleship Artist Component",
                            "FrenchBattleshipArtist_type")
        );

        archStructure.addComponent(
            createComponent("FrenchAWACSArtist", "AWACS Artist Component",
                            "FrenchAWACSArtist_type")
            );

        archStructure.addComponent(
            createComponent("FrenchAirDefenseMissileArtist",
                            "Air Defense Missile Artist Component",
                            "FrenchAirDefenseMissileArtist_type")
            );
        archStructure.addComponent(
            createComponent("FrenchTroupsArtist",
                            "Troups Artist Component",
                            "FrenchTroupsArtist_type")
        );
        archStructure.addComponent(
            createComponent("USCarrierArtist", "Carrier Artist Component",
                            "USCarrierArtist_type")
        );
        archStructure.addComponent(
            createComponent("FrenchOperationsArtist",
                            "Operations Artist Component",
                            "FrenchOperationsArtist_type")
            );
        archStructure.addComponent(
            createComponent("FrenchLandRetrieval",
                            "Land Retrieval Component",
                            "FrenchLandRetrieval_type")
        );
        archStructure.addComponent(
            createComponent("FrenchVirtualScreen",
                            "Virtual Screen Component",
                            "FrenchVirtualScreen_type")
        );
    }

    /**
     * Create the connectors in the architecture.
     */
    public void createConnectors(IArchStructure archStructure) {
        //add Connectors
        archStructure.addConnector(
            createConnector("ClockBus", "Clock Connector",
                            "CommonBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("FrenchRadarFilterBus", "Radar Filter Connector",
                            "FrenchBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("FrenchFilterControlBus",
                            "Filter and Command&Control Connector",
                            "FrenchBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("FrenchDisplayBus", "Display Connector",
                            "FrenchBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("FrenchEntityBus", "Entity Connector",
                            "FrenchBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("FrenchScreenBus", "Screen Connector",
                            "FrenchBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("FrenchLandBus", "Land Connector",
                            "FrenchBusConnector_type")
        );
        archStructure.addConnector(
            createFredConnector("FrenchLocalFredBus", "Local Fred Connector",
                                "FrenchLocalFredConnector_type")
        );
    }

    /**
     * Create the links in the architecture.
     */
    public void createLinks(IArchStructure archStructure) {
        //add links
        archStructure.addLink(createLink("Clock", "ClockBus"));

        archStructure.addLink(createLink("ClockBus", "FrenchRadar"));
        archStructure.addLink(createLink("FrenchRadar",
                                         "FrenchRadarFilterBus"));

        archStructure.addLink(createLink("ClockBus", "FrenchAWACS"));
        archStructure.addLink(createLink("FrenchAWACS",
                                         "FrenchRadarFilterBus"));
        archStructure.addLink(createLink("FrenchRadarFilterBus",
                                         "FrenchtoFrenchFilter"));

        archStructure.addLink(createLink("FrenchtoFrenchFilter",
                                         "FrenchFilterControlBus"));
        archStructure.addLink(createLink("FrenchFilterControlBus", "FrenchCC"));
        archStructure.addLink(createLink("FrenchCC", "FrenchDisplayBus"));

        archStructure.addLink(createLink("FrenchDisplayBus",
                                         "FrenchUnknownArtist"));
        archStructure.addLink(createLink("FrenchUnknownArtist",
                                         "FrenchEntityBus"));
        archStructure.addLink(createLink("FrenchDisplayBus",
                                         "FrenchNeutralArtist"));
        archStructure.addLink(createLink("FrenchNeutralArtist",
                                         "FrenchEntityBus"));
        archStructure.addLink(createLink("FrenchDisplayBus",
                                         "FrenchFriendArtist"));
        archStructure.addLink(createLink("FrenchFriendArtist",
                                         "FrenchEntityBus"));
        archStructure.addLink(createLink("FrenchDisplayBus",
                                         "FrenchHostileArtist"));
        archStructure.addLink(createLink("FrenchHostileArtist",
                                         "FrenchEntityBus"));

        archStructure.addLink(createLink("FrenchEntityBus",
                                         "USFixedWingArtist"));
        archStructure.addLink(createLink("USFixedWingArtist",
                                         "FrenchScreenBus"));

        archStructure.addLink(createLink("FrenchEntityBus",
                                         "FrenchBattleshipArtist"));
        archStructure.addLink(createLink("FrenchBattleshipArtist",
                                         "FrenchScreenBus"));

        archStructure.addLink(createLink("FrenchEntityBus",
                                         "FrenchAWACSArtist"));
        archStructure.addLink(createLink("FrenchAWACSArtist",
                                         "FrenchScreenBus"));

        archStructure.addLink(createLink("FrenchEntityBus",
                                         "FrenchAirDefenseMissileArtist"));
        archStructure.addLink(createLink("FrenchAirDefenseMissileArtist",
                                         "FrenchScreenBus"));

        archStructure.addLink(createLink("FrenchEntityBus",
                                         "FrenchTroupsArtist"));
        archStructure.addLink(createLink("FrenchTroupsArtist",
                                         "FrenchScreenBus"));

        archStructure.addLink(createLink("FrenchEntityBus",
                                         "USCarrierArtist"));
        archStructure.addLink(createLink("USCarrierArtist",
                                         "FrenchScreenBus"));

        archStructure.addLink(createLink("FrenchEntityBus",
                                         "FrenchOperationsArtist"));
        archStructure.addLink(createLink("FrenchOperationsArtist",
                                         "FrenchScreenBus"));

        archStructure.addLink(createLink("FrenchLandRetrieval",
                                         "FrenchLandBus"));
        archStructure.addLink(createLink("FrenchLandBus",
                                         "FrenchVirtualScreen"));
        //Bus-to-Bus Links
        archStructure.addLink(createLink("FrenchScreenBus", "FrenchLandBus"));
        archStructure.addLink(createFredLink(null,"FrenchFilterControlBus",
                                             "FrenchLocalFredBus"));
    }

    /**
     * Create the rules in the architecture.
     */
    public void createRules(IArchStructure archStructure) {
    }

    public static void main(String[] args){
        /* Current released versions of Ant place spurious new lines
         * when using the Java taskdef with output parameter, so we
         * support printing to a file listed in args[0] to work properly.
         */
        Description d = new FranceDescription();
        d.createArchitecture("Coalition Forces");

        if (args.length > 0) {
            try {
                FileOutputStream fos = new FileOutputStream(args[0]);
                PrintStream ps = new PrintStream(fos);
                ps.println(d.getXml());
            }
            catch (java.io.FileNotFoundException fnfe) {
                System.err.println(fnfe);
            }
        }
        else {
            System.out.println(d.getXml());
        }
    }

}
