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
public class USDescription extends Description {

    /**
     * Default constructor
     */
    public USDescription() {
        super();
    }

    /**
     * Register all of the connector types.
     * @param archTypes
     */
    public void createConnectorTypes(IArchTypes archTypes) {
        archTypes.addConnectorType(
            createConnectorType("USBusConnector_type",
                                "US Bus Connector Type",
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
            createConnectorType("USLocalFredConnector_type",
                                "Fred connector Type",
                                "c2.conn.fred.FredConnector",
                                new IInitializationParameter[]{
                                createInitializationParameter("host",
                                                          "127.0.0.1/-.-.-.-"),
                                createInitializationParameter("groupName",
                                                              "US" + " FRED")}
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
            addRulesComp(new String[]{"USR1","USR2"},
            createComponentType("USRadar_type", "US Radar Type",
                                "c2demo.coalitiondemo.USRadar", null))
        );

        archTypes.addComponentType(
            addRulesComp(new String[]{"USUSFil1","USUSFil2"},
            createComponentType("UStoUSFilter_type", "US to US Filter Type",
                                "c2demo.coalitiondemo.UStoUSFilter", null))
        );

        archTypes.addComponentType(
            addRulesComp(new String[]{"USCC1","USCC2","USCC3","USCC4","USCC5","USCC6","USCC7","USCC8","USCC9","USCC10","USCC11","USCC12","USCC13"},
            createComponentType("USCC_type", "US Command and Control Type",
                                "c2demo.coalitiondemo.USCommandAndControl",
                                null))
        );

        archTypes.addComponentType(
            addRulesComp(new String[]{"USUnAr0","USUnAr1","USUnAr2","USUnAr3","USUnAr4","USUnAr5"},
            createComponentType("USUnknownArtist_type", "Unknown Artist Type",
                                "c2demo.coalitiondemo.UnknownArtist",
                                null))
        );

        archTypes.addComponentType(
            addRulesComp(new String[]{"USNeuAr0","USNeuAr1","USNeuAr2","USNeuAr3","USNeuAr4","USNeuAr5"},
            createComponentType("USNeutralArtist_type", "Neutral Artist Type",
                                "c2demo.coalitiondemo.NeutralArtist",
                                null))
        );

        archTypes.addComponentType(
            addRulesComp(new String[]{"USFrAr0","USFrAr1","USFrAr2","USFrAr3","USFrAr4","USFrAr5"},
            createComponentType("USFriendArtist_type", "Friend Artist Type",
                                "c2demo.coalitiondemo.FriendArtist",
                                null))
        );

        archTypes.addComponentType(
            addRulesComp(new String[]{"USHosAr0","USHosAr1","USHosAr2","USHosAr3","USHosAr4","USHosAr5"},
            createComponentType("USHostileArtist_type", "Hostile Artist Type",
                                "c2demo.coalitiondemo.HostileArtist", null))
        );

        archTypes.addComponentType(
            addRulesComp(new String[]{"USFwAr1"},
            createComponentType("USFixedWingArtist_type",
                                "Fixed Wing Artist Type",
                                "c2demo.coalitiondemo.FixedWingArtist",
                                null))
        );

        archTypes.addComponentType(
            addRulesComp(new String[]{"USBSAr1"},
            createComponentType("USBattleshipArtist_type",
                                "Battleship Artist Type",
                                "c2demo.coalitiondemo.BattleshipArtist",
                                null))
        );

        archTypes.addComponentType(
            addRulesComp(new String[]{"FranceAwacs1"},
            createComponentType("FrenchAWACSArtist_type",
                                "AWACS Artist Type",
                                "c2demo.coalitiondemo.AWACSArtist",
                                null))
            );

        archTypes.addComponentType(
            addRulesComp(new String[]{"FranceADMAr1"},
            createComponentType("FrenchAirDefenseMissileArtist_type",
                                "Air Defense Missile Artist Type",
                                "c2demo.coalitiondemo.AirDefenseMissileArtist",
                                null))
            );

        archTypes.addComponentType(
            addRulesComp(new String[]{"USTrAr0"},
            createComponentType("USTroupsArtist_type", "Troups Artist Type",
                                "c2demo.coalitiondemo.TroupsArtist",
                                null))
        );
        archTypes.addComponentType(
            addRulesComp(new String[]{"USCAr1"},
            createComponentType("USCarrierArtist_type",
                                "Carrier Artist Type",
                                "c2demo.coalitiondemo.CarrierArtist",
                                null))
        );
        archTypes.addComponentType(
            addRulesComp(new String[]{"USOpAr1","USOpAr2","USOpAr3"},
            createComponentType("USOperationsArtist_type",
                                "Operations Artist Type",
                                "c2demo.coalitiondemo.OperationsArtist",
                                null))
            );
        archTypes.addComponentType(
            addRulesComp(new String[]{"USLR1"},
            createComponentType("USLandRetrieval_type",
                                "Land Retrieval Type",
                                "c2demo.coalitiondemo.LocalLandRetrieval",
                                null))
        );

        archTypes.addComponentType(
            createComponentType("USVirtualScreen_type",
                                "Virtual Screen Type",
                                "c2demo.coalitiondemo.VirtualScreen",
                                new IInitializationParameter[]{
                                createInitializationParameter("Country", "US")}
            ));
    }

    /**
     * Create components.
     */
    public void createComponents(IArchStructure archStructure) {
        //add components
        archStructure.addComponent(
            createComponent("Clock", "Clock Event Generator Component", "Clock_type")
        );
        archStructure.addComponent(
            createComponent("USRadar", "US Radar Component", "USRadar_type")
        );
        archStructure.addComponent(
            createComponent("UStoUSFilter", "UStoUS Filter Component", "UStoUSFilter_type")
        );
        archStructure.addComponent(
            createComponent("USCC", "US Command & Control Component", "USCC_type")
        );
        archStructure.addComponent(
            createComponent("USUnknownArtist", "Unknown Artist Component ", "USUnknownArtist_type")
        );

        archStructure.addComponent(
            createComponent("USNeutralArtist", "Neutral Artist Component", "USNeutralArtist_type")
        );
        archStructure.addComponent(
            createComponent("USFriendArtist", "Friend Artist Component", "USFriendArtist_type")
        );
        archStructure.addComponent(
            createComponent("USHostileArtist", "Hostile Artist Component", "USHostileArtist_type")
        );
        
        archStructure.addComponent(
            createComponent("USFixedWingArtist", "FixedWing Artist Component", "USFixedWingArtist_type")
        );
        archStructure.addComponent(
            createComponent("USBattleshipArtist", "Battleship Artist Component", "USBattleshipArtist_type")
        );
        archStructure.addComponent(
            createComponent("FrenchAWACSArtist", "AWACS Artist Component", "FrenchAWACSArtist_type")
            );
        
        archStructure.addComponent(
            createComponent("FrenchAirDefenseMissileArtist", "Air Defense Missile Artist Component", "FrenchAirDefenseMissileArtist_type")
            );
        archStructure.addComponent(
            createComponent("USTroupsArtist", "Troups Artist Component", "USTroupsArtist_type")
        );
        archStructure.addComponent(
            createComponent("USCarrierArtist", "Carrier Artist Component", "USCarrierArtist_type")
        );
        archStructure.addComponent(
            createComponent("USOperationsArtist", "Operations Artist Component", "USOperationsArtist_type")
            );
        archStructure.addComponent(
            createComponent("USLandRetrieval", "Land Retrieval Component", "USLandRetrieval_type")
        );
        archStructure.addComponent(
            createComponent("USVirtualScreen", "Virtual Screen Component", "USVirtualScreen_type")
        );
    }

    /**
     * Create the connectors in the architecture.
     */
    public void createConnectors(IArchStructure archStructure) {
        //add Connectors
        archStructure.addConnector(
            createConnector("ClockBus", "Clock Connector", "CommonBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("USRadarFilterBus", "Radar Filter Connector", "USBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("USFilterControlBus", "Filter and Command&Control Connector", "USBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("USDisplayBus", "Display Connector", "USBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("USEntityBus", "Entity Connector", "USBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("USScreenBus", "Screen Connector", "USBusConnector_type")
        );
        archStructure.addConnector(
            createConnector("USLandBus", "Land Connector", "USBusConnector_type")
        );
        
        archStructure.addConnector(
            createFredConnector("USLocalFredBus", "Local Fred Connector", "USLocalFredConnector_type")
            );
    }

    /**
     * Create the links in the architecture.
     */
    public void createLinks(IArchStructure archStructure) {
        archStructure.addLink(createLink("Clock", "ClockBus"));

        archStructure.addLink(createLink("ClockBus", "USRadar"));
        archStructure.addLink(createLink("USRadar", "USRadarFilterBus"));

        archStructure.addLink(createLink("USRadarFilterBus", "UStoUSFilter"));
        archStructure.addLink(createLink("UStoUSFilter", "USFilterControlBus"));

        archStructure.addLink(createLink("USFilterControlBus", "USCC"));
        archStructure.addLink(createLink("USCC", "USDisplayBus"));

        archStructure.addLink(createLink("USDisplayBus", "USUnknownArtist"));
        archStructure.addLink(createLink("USUnknownArtist", "USEntityBus"));

        archStructure.addLink(createLink("USDisplayBus", "USNeutralArtist"));
        archStructure.addLink(createLink("USNeutralArtist", "USEntityBus"));

        archStructure.addLink(createLink("USDisplayBus", "USFriendArtist"));
        archStructure.addLink(createLink("USFriendArtist", "USEntityBus"));

        archStructure.addLink(createLink("USDisplayBus", "USHostileArtist"));
        archStructure.addLink(createLink("USHostileArtist", "USEntityBus"));

        archStructure.addLink(createLink("USEntityBus", "USFixedWingArtist"));
        archStructure.addLink(createLink("USFixedWingArtist", "USScreenBus"));

        archStructure.addLink(createLink("USEntityBus", "USBattleshipArtist"));
        archStructure.addLink(createLink("USBattleshipArtist", "USScreenBus"));

        archStructure.addLink(createLink("USEntityBus", "FrenchAWACSArtist"));
        archStructure.addLink(createLink("FrenchAWACSArtist", "USScreenBus"));

        archStructure.addLink(createLink("USEntityBus",
                                         "FrenchAirDefenseMissileArtist"));
        archStructure.addLink(createLink("FrenchAirDefenseMissileArtist",
                                         "USScreenBus"));

        archStructure.addLink(createLink("USEntityBus", "USTroupsArtist"));
        archStructure.addLink(createLink("USTroupsArtist", "USScreenBus"));

        archStructure.addLink(createLink("USEntityBus", "USCarrierArtist"));
        archStructure.addLink(createLink("USCarrierArtist", "USScreenBus"));

        archStructure.addLink(createLink("USEntityBus", "USOperationsArtist"));
        archStructure.addLink(createLink("USOperationsArtist", "USScreenBus"));

        archStructure.addLink(createLink("USLandRetrieval", "USLandBus"));
        archStructure.addLink(createLink("USLandBus", "USVirtualScreen"));

        //Bus-to-Bus Links
        archStructure.addLink(createLink("USScreenBus", "USLandBus"));
        archStructure.addLink(createFredLink(null,
                                             "USFilterControlBus",
                                             "USLocalFredBus"));
    }

    /**
     * Create the rules in the architecture.
     */
    public void createRules(IArchStructure archStructure) {
        IRuleSpecification ruleSpecification =
            messages.createRuleSpecificationElement();
        ruleSpecification.setId("Rules for Coalition Forces");
        ruleSpecification.setDescription(createDescription("Coalition demo"));
        xArch.addObject(ruleSpecification);

        /* Define the messages for the rules. */
        //Generic
        INamedPropertyMessage eGenericN =
            createMessage("eGen-notif", "notification",
                          "entity notification", "1", "entity",
                          "c2.fw.NamedPropertyMessage");
        INamedPropertyMessage oGenericN =
            createMessage("oGen-notif", "notification",
                          "operation notification", "1", "operation",
                          "c2.fw.NamedPropertyMessage");
        INamedPropertyMessage mGenericN =
           createMessage("mGen-notif", "notification",
                         "map notification", "1", "map",
                         "c2.fw.NamedPropertyMessage");
        INamedPropertyMessage reGenericN =
            createMessage("reGen-notif", "notification",
                          "remove entity notification", "1", "rem-entity",
                          "c2.fw.NamedPropertyMessage");
        INamedPropertyMessage roGenericN =
            createMessage("roGen-notif", "notification", "remove operation notification", "1", "rem-operation", "c2.fw.NamedPropertyMessage");

        INamedPropertyMessage eDimAirN =
            createMessage("eDimAir-notif", "notification", "entity dimension air notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("dimension",BattleDimension.AIR.toString())});
        INamedPropertyMessage eDimGrndN =
            createMessage("eDimGrnd-notif", "notification", "entity dimension ground notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("dimension",BattleDimension.GROUND.toString())});
        INamedPropertyMessage eDimSeaN =
            createMessage("eDimSea-notif", "notification", "entity dimension sea notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("dimension",BattleDimension.SEA.toString())});
        INamedPropertyMessage eDimSubSurN =
            createMessage("eDimSubSur-notif", "notification", "entity dimension sub-surface notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("dimension",BattleDimension.SUBSURFACE.toString())});
        INamedPropertyMessage eSymCharN =
            createMessage("eSymChar-notif", "notification", "entity Sym char notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("symchar","symchar")});
        INamedPropertyMessage eSymColorN =
            createMessage("eSymColor-notif", "notification", "entity Sym Color notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("symcolor","color")});
        INamedPropertyMessage eSymFontFillCharN =
            createMessage("eSymFontFillChar-notif", "notification", "entity Sym Font and Fill Char notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("symfont","font"),createNamedProperty("fillchar","fillchar")});

        //Air Defense Missile Artist
        INamedPropertyMessage eAdmaN =
            createMessage("eAdma-notif", "notification",
              "entity type airdefensemissile notification", "1",
              "entity", "c2.fw.NamedPropertyMessage",
              new INamedProperty[]{
              createNamedProperty("type",
                                  EntityType.AIRDEFENSEMISSILE.toString())});
        //AWACS Artist
        INamedPropertyMessage eAwN =
            createMessage("eAw-notif", "notification",
                    "entity type awacs notification", "1",
                    "entity", "c2.fw.NamedPropertyMessage",
                    new INamedProperty[]{
                    createNamedProperty("type",
                                        EntityType.AWACS.toString())});

        //Battleship Artist
        INamedPropertyMessage eBsN =
            createMessage("eBs-notif", "notification",
                      "entity type battleship notification", "1",
                      "entity", "c2.fw.NamedPropertyMessage",
                      new INamedProperty[]{
                      createNamedProperty("type",
                                          EntityType.BATTLESHIP.toString())});
        //Carrier Artist
        INamedPropertyMessage eCarrierN =
            createMessage("eCar-notif", "notification",
                          "entity type carrier notification", "1", "entity",
                          "c2.fw.NamedPropertyMessage",
                          new INamedProperty[]{
                          createNamedProperty("type",
                                              EntityType.CARRIER.toString())});

        //ClockEvent Generator
        INamedPropertyMessage cClkN =
            createMessage("cClk-notif", "notification",
                          "clock type notification", "1", "clock",
                          "c2.fw.NamedPropertyMessage",
                          new INamedProperty[]{
                          createNamedProperty("tick","tickcount")});

        //Fixed Wing Artist
        INamedPropertyMessage eFwN =
            createMessage("eFw-notif", "notification",
                      "entity type fixed wing notification", "1", "entity",
                      "c2.fw.NamedPropertyMessage",
                      new INamedProperty[]{
                      createNamedProperty("type",
                                          EntityType.FIXEDWING.toString())});
        //Fixed Wing bomber Artist
        INamedPropertyMessage eFwbN =
            createMessage("eFwb-notif", "notification",
                  "entity type fixed wing bomber notification", "1",
                  "entity", "c2.fw.NamedPropertyMessage",
                  new INamedProperty[]{
                  createNamedProperty("type",
                                      EntityType.FIXEDWINGBOMBER.toString())});

        //Friend Artist
        INamedPropertyMessage eFrAfN =
            createMessage("eFrAf-notif", "notification",
                          "entity friend affiliation notification", "1",
                          "entity", "c2.fw.NamedPropertyMessage",
                          new INamedProperty[]{
                          createNamedProperty("affiliation",
                                              Affiliation.FRIEND.toString())});

        INamedPropertyMessage eFrTacN =
            createMessage("eFrTac-notif", "notification",
                          "entity friend tac color notification", "1",
                          "entity", "c2.fw.NamedPropertyMessage",
                          new INamedProperty[]{
                          createNamedProperty("taccolor","Friend Color")});
        //Hostile Artist
        INamedPropertyMessage eHosAfN =
            createMessage("eHosAf-notif", "notification", "entity hostile affiliation notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("affiliation",Affiliation.HOSTILE.toString())});
        INamedPropertyMessage eHosTacN =
            createMessage("eHosTac-notif", "notification", "entity hostile tac color notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("taccolor","Hostile Color")});

        //Land retrieval
        INamedPropertyMessage mLrN =
            createMessage("mLr-notif", "notification", "map land retrieval notification", "1", "map", "c2.fw.NamedPropertyMessage");

        //Neutral Artist
        INamedPropertyMessage eNeuAfN =
            createMessage("eNeuAf-notif", "notification", "entity neutral affiliation notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("affiliation",Affiliation.NEUTRAL.toString())});
        INamedPropertyMessage eNeuTacN =
            createMessage("eNeuTac-notif", "notification", "entity neutral tac color notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("taccolor","Neutral Color")});

        //Operations Artist
        INamedPropertyMessage oTacFN =
            createMessage("oTacF-notif", "notification", "operation tac font notification", "1", "operation", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("tacfont","Tac font")});

        //Troups Artist
        INamedPropertyMessage eTrN =
            createMessage("eTr-notif", "notification", "entity type troups notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("type",EntityType.TROUPS.toString())});

        //Unknown Artist
        INamedPropertyMessage eUnAfN =
            createMessage("eUnAf-notif", "notification", "entity unknown affiliation notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("affiliation",Affiliation.UNKNOWN.toString())});
        INamedPropertyMessage eUnTacN =
            createMessage("eUnTac-notif", "notification", "entity unknown tac color notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("taccolor","Unknown Color")});
        //US Command and Control
        INamedPropertyMessage eCounFraN =
            createMessage("eCounFra-notif", "notification", "entity country france notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("country",Country.FRANCE.toString())});
        INamedPropertyMessage eCounMexN =
            createMessage("eCounMex-notif", "notification", "entity country mexico notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("country",Country.MEXICO.toString())});
        INamedPropertyMessage eCounLebN =
            createMessage("eCounLeb-notif", "notification", "entity country lebanon notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("country",Country.LEBANON.toString())});
        INamedPropertyMessage eCounUSN =
            createMessage("eCounUS-notif", "notification", "entity country usa notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("country",Country.USA.toString())});
        INamedPropertyMessage eCounUnN =
            createMessage("eCounUn-notif", "notification", "entity country unknown notification", "1", "entity", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("country",Country.UNKNOWN.toString())});
        INamedPropertyMessage oFlightPathN =
            createMessage("oFP-notif", "notification", "operation type flightpath notification", "1", "operation", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("type",OperationType.FLIGHTPATH.toString())});
        INamedPropertyMessage oFlightPathR =
            createMessage("oFP-req", "request", "operation type flightpath request", "1", "operation", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("type",OperationType.FLIGHTPATH.toString())});
        INamedPropertyMessage oPredImpactN =
            createMessage("oPI-notif", "notification", "operation type predImpact notification", "1", "operation", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("type",OperationType.PREDIMPACT.toString())});
        INamedPropertyMessage oImpactN =
            createMessage("oI-notif", "notification", "operation type Impact notification", "1", "operation", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("type",OperationType.IMPACT.toString())});
        INamedPropertyMessage roAfN =
            createMessage("roAf-notif", "notification", "remove operation Friend affiliation notification", "1", "rem-operation", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("affiliation",Affiliation.FRIEND.toString())});
        INamedPropertyMessage oAfN =
            createMessage("oAf-notif", "notification", " operation Friend affiliation notification", "1", "operation", "c2.fw.NamedPropertyMessage",
        new INamedProperty[]{createNamedProperty("affiliation",Affiliation.FRIEND.toString())});
        //US Radar
        INamedPropertyMessage reCounN =
            createMessage("reCoun-notif", "notification",
                      "remove entity country notification", "1", "rem-entity",
                      "c2.fw.NamedPropertyMessage",
                      new INamedProperty[]{
                      createNamedProperty("country",Country.USA.toString())});

        //US to US Filter
        //Virtual Screen
        INamedPropertyMessage mR =
            createMessage("map-req", "notification", "map request", "1",
                          "map", "c2.fw.NamedPropertyMessage",
                          new INamedProperty[]{
                          createNamedProperty("action","request")});

        /* Whew.  Now, define the rules. */
        IProductionRule rules[] = {
            //AIR Defense Missile Artist
            createRule("FranceADMAr1", "eAdmaN>eAdmaN",
                       new INamedPropertyMessage[]{eAdmaN},
                       new INamedPropertyMessage[]{eAdmaN}, "MostRecent"),
            createRule("FranceADMAr2", "eAdmaN>eSymCharN",
                       new INamedPropertyMessage[]{eAdmaN},
                       new INamedPropertyMessage[]{eSymCharN}, "MostRecent"),

            //AWACS Artist
            createRule("FranceAwacs1", "eAwN>eAwN",
                       new INamedPropertyMessage[]{eAwN},
                       new INamedPropertyMessage[]{eAwN}, "MostRecent"),
            //Battleship Artist
            createRule("USBSAr1", "eBsN>eBsN",
                       new INamedPropertyMessage[]{eBsN},
                       new INamedPropertyMessage[]{eBsN}, "MostRecent"),
            //Carrier Artist
            createRule("USCAr1", "eCarrierN>eCarrierN",
                       new INamedPropertyMessage[]{eCarrierN},
                       new INamedPropertyMessage[]{eCarrierN}, "MostRecent"),
            //FixedWing Artist
            createRule("USFwAr1", "eFwN>eFwN",
                       new INamedPropertyMessage[]{eFwN},
                       new INamedPropertyMessage[]{eFwN}, "MostRecent"),
            //FixedWingBomber Artist
            createRule("USFwbAr1", "eFwbN>eFwbN",
                       new INamedPropertyMessage[]{eFwbN},
                       new INamedPropertyMessage[]{eFwbN}, "MostRecent"),
            //Friend Artist
            createRule("USFrAr0", "eFrAfN>eFrAfN",
                       new INamedPropertyMessage[]{eFrAfN},
                       new INamedPropertyMessage[]{eFrAfN}, "MostRecent"),

            createRule("USFrAr1", "eDimAirN>eDimAirN",
                       new INamedPropertyMessage[]{eDimAirN},
                       new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
            createRule("USFrAr2", "eDimGrndN>eDimGrndN",
                       new INamedPropertyMessage[]{eDimGrndN},
                       new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
            createRule("USFrAr3", "eDimSeaN>eDimSeaN",
                       new INamedPropertyMessage[]{eDimSeaN},
                       new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
            createRule("USFrAr4", "eDimSubSurN>eDimSubSurN",
                       new INamedPropertyMessage[]{eDimSubSurN},
                       new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
            createRule("USFrAr5", "oGenericN>oGenericN",
                       new INamedPropertyMessage[]{oGenericN},
                       new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
            //Hostile Artist
            createRule("USHosAr0", "eHosAfN>eHosAfN",
                       new INamedPropertyMessage[]{eHosAfN},
                       new INamedPropertyMessage[]{eHosAfN}, "MostRecent"),
            createRule("USHosAr1", "eDimAirN>eDimAirN",
                       new INamedPropertyMessage[]{eDimAirN},
                       new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
            createRule("USHosAr2", "eDimGrndN>eDimGrndN",
                       new INamedPropertyMessage[]{eDimGrndN},
                       new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
            createRule("USHosAr3", "eDimSeaN>eDimSeaN",
                       new INamedPropertyMessage[]{eDimSeaN},
                       new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
            createRule("USHosAr4", "eDimSubSurN>eDimSubSurN",
                       new INamedPropertyMessage[]{eDimSubSurN},
                       new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
            createRule("USHosAr5", "oGenericN>oGenericN",
                       new INamedPropertyMessage[]{oGenericN},
                       new INamedPropertyMessage[]{oGenericN}, "MostRecent"),

            //Land Retrieval
            createRule("USLR1", "mR>mLrN",
                       new INamedPropertyMessage[]{mR},
                       new INamedPropertyMessage[]{mLrN}, "MostRecent"),
            //Neutral Artist
            createRule("USNeuAr0", "eNeuAfN>eNeuAfN",
                       new INamedPropertyMessage[]{eNeuAfN},
                       new INamedPropertyMessage[]{eNeuAfN}, "MostRecent"),
            createRule("USNeuAr1", "eDimAirN>eDimAirN",
                       new INamedPropertyMessage[]{eDimAirN},
                       new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
            createRule("USNeuAr2", "eDimGrndN>eDimGrndN",
                       new INamedPropertyMessage[]{eDimGrndN},
                       new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
            createRule("USNeuAr3", "eDimSeaN>eDimSeaN",
                       new INamedPropertyMessage[]{eDimSeaN},
                       new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
            createRule("USNeuAr4", "eDimSubSurN>eDimSubSurN",
                       new INamedPropertyMessage[]{eDimSubSurN},
                       new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
            createRule("USNeuAr5", "oGenericN>oGenericN",
                       new INamedPropertyMessage[]{oGenericN},
                       new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
            //Operations Artist
            createRule("USOpAr1", "oGenericN>oGenericN",
                       new INamedPropertyMessage[]{oGenericN},
                       new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
            createRule("USOpAr2", "roGenericN>roGenericN",
                       new INamedPropertyMessage[]{roGenericN},
                       new INamedPropertyMessage[]{roGenericN}, "MostRecent"),
            createRule("USOpAr3", "reGenericN>reGenericN",
                       new INamedPropertyMessage[]{reGenericN},
                       new INamedPropertyMessage[]{reGenericN}, "MostRecent"),
            //TroupsArtist
            createRule("USTrAr0", "eTrN>eTrN",
                       new INamedPropertyMessage[]{eTrN},
                       new INamedPropertyMessage[]{eTrN}, "MostRecent"),
            //Unknown Artist
            createRule("USUnAr0", "eUnAfN>eUnAfN",
                       new INamedPropertyMessage[]{eUnAfN},
                       new INamedPropertyMessage[]{eUnAfN}, "MostRecent"),
            createRule("USUnAr1", "eDimAirN>eDimAirN",
                       new INamedPropertyMessage[]{eDimAirN},
                       new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
            createRule("USUnAr2", "eDimGrndN>eDimGrndN",
                       new INamedPropertyMessage[]{eDimGrndN},
                       new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
            createRule("USUnAr3", "eDimSeaN>eDimSeaN",
                       new INamedPropertyMessage[]{eDimSeaN},
                       new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
            createRule("USUnAr4", "eDimSubSurN>eDimSubSurN",
                       new INamedPropertyMessage[]{eDimSubSurN},
                       new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
            createRule("USUnAr5", "oGenericN>oGenericN",
                       new INamedPropertyMessage[]{oGenericN},
                       new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
            //US Command and control
            createRule("USCC1","eGenericN>eGenericN",
                       new INamedPropertyMessage[]{eGenericN},
                       new INamedPropertyMessage[]{eGenericN},"MostRecent"),
            createRule("USCC2",
                       "eCarrierN>oFlightPathN,oFlightPathR,oPredImpactN",
                       new INamedPropertyMessage[]{eCarrierN},
                       new INamedPropertyMessage[]{oFlightPathN,oFlightPathR,
                                                   oPredImpactN},
                       "MostRecent"),
            createRule("USCC3","eCounFraN>eFrAfN",
                       new INamedPropertyMessage[]{eCounFraN},
                       new INamedPropertyMessage[]{eFrAfN},"MostRecent"),
            createRule("USCC4","eCounMexN>eNeuAfN",
                       new INamedPropertyMessage[]{eCounMexN},
                       new INamedPropertyMessage[]{eNeuAfN},"MostRecent"),
            createRule("USCC5","eCounLebN>eHosAfN",
                       new INamedPropertyMessage[]{eCounLebN},
                       new INamedPropertyMessage[]{eHosAfN},"MostRecent"),
            createRule("USCC6","eCounUSN>eFrAfN",
                       new INamedPropertyMessage[]{eCounUSN},
                       new INamedPropertyMessage[]{eFrAfN},"MostRecent"),
            createRule("USCC7","eCounUnN>eUnAfN",
                       new INamedPropertyMessage[]{eCounUnN},
                       new INamedPropertyMessage[]{eUnAfN},"MostRecent"),
            createRule("USCC8","eFwN>roAfN,oAfN",
                       new INamedPropertyMessage[]{eFwN},
                       new INamedPropertyMessage[]{roAfN,oAfN},"MostRecent"),
            createRule("USCC9","eFwbN>eDimAirN",
                       new INamedPropertyMessage[]{eFwbN},
                       new INamedPropertyMessage[]{eDimAirN},"MostRecent"),
            createRule("USCC10","eBsN>eDimSeaN",
                       new INamedPropertyMessage[]{eBsN},
                       new INamedPropertyMessage[]{eDimSeaN},"MostRecent"),
            createRule("USCC11","eTrN>eDimGrndN",
                       new INamedPropertyMessage[]{eTrN},
                       new INamedPropertyMessage[]{eDimGrndN},"MostRecent"),
            createRule("USCC12","eAdmaN>oFlightPathN,oFlightPathR",
                       new INamedPropertyMessage[]{eAdmaN},
                       new INamedPropertyMessage[]{oFlightPathN,oFlightPathR},
                       "MostRecent"),
            createRule("USCC13","eAwN>eDimAirN",
                       new INamedPropertyMessage[]{eAwN},
                       new INamedPropertyMessage[]{eDimAirN},"MostRecent"),
            //US Radar
            createRule("USR1","cClkN>eFwN,reCounN",
                       new INamedPropertyMessage[]{cClkN},
                       new INamedPropertyMessage[]{eFwN,reCounN},"MostRecent"),
            createRule("USR2","cClkN>eCarrierN",
                       new INamedPropertyMessage[]{cClkN},
                       new INamedPropertyMessage[]{eCarrierN},"MostRecent"),
            //US to US Filter
            createRule("USUSFil1","eGenericN>eGenericN",
                       new INamedPropertyMessage[]{eGenericN},
                       new INamedPropertyMessage[]{eGenericN},"MostRecent"),
            createRule("USUSFil2","reGenericN>reGenericN",
                       new INamedPropertyMessage[]{reGenericN},
                       new INamedPropertyMessage[]{reGenericN},"MostRecent")
            //Virtual Screen
        };
        for (int i = 0; i < rules.length; i++)
            ruleSpecification.addRule(rules[i]);
    }

    public static void main(String[] args){
        /* Current released versions of Ant place spurious new lines
         * when using the Java taskdef with output parameter, so we
         * support printing to a file listed in args[0] to work properly.
         */
        Description d = new USDescription();
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
