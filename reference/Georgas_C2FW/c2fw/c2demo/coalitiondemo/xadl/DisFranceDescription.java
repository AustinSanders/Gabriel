package c2demo.coalitiondemo.xadl;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.instance.*;
import edu.uci.isr.xarch.types.*;
import edu.uci.isr.xarch.implementation.*;
import edu.uci.isr.xarch.variants.*;
import edu.uci.isr.xarch.javaimplementation.*;
import edu.uci.isr.xarch.javainitparams.*;
import java.util.Arrays;

import edu.uci.isr.xarch.versions.*;
import edu.uci.isr.xarch.messages.*;
import java.io.*;
import java.util.Collection;
import java.util.Iterator;

import c2demo.coalitiondemo.*;

public class DisFranceDescription{

	private static InstanceContext instance;
	private static TypesContext types;
	private static ImplementationContext implementation;
	private static VariantsContext variants;
	private static JavaimplementationContext javaimplementation;
	private static JavainitparamsContext javainitparams;

	//new
	private static VersionsContext versions;
	private static MessagesContext messages;
	
	public static void main(String[] args){
		System.out.println(getXml());
	}
	
	public static String getXml(){
		IXArch xArch = XArchUtils.createXArch();
		instance = new InstanceContext(xArch);
		types = new TypesContext(xArch);
		implementation = new ImplementationContext(xArch);
		variants = new VariantsContext(xArch);
		javaimplementation = new JavaimplementationContext(xArch);
		javainitparams = new JavainitparamsContext(xArch);
		
		//new
		versions = new VersionsContext(xArch);
		messages = new MessagesContext(xArch);
		
		IArchStructure archStructure = types.createArchStructureElement();
		archStructure.setId("CoalitionForces");
		archStructure.setDescription(createDescription("Coalition Forces Architecture"));
		
		IArchTypes archTypes = types.createArchTypesElement();
		
		xArch.addObject(archStructure);
		xArch.addObject(archTypes);
		
		//new
		IRuleSpecification ruleSpecification = messages.createRuleSpecificationElement();
		ruleSpecification.setId("Rules for Coalition Forces");
		ruleSpecification.setDescription(createDescription("Coalition demo"));
		xArch.addObject(ruleSpecification);
		IProductionRule rules[] = getRuleSpecification();
		for (int i = 0; i < rules.length; i++ )
			ruleSpecification.addRule(rules[i]);
		
		//for Fred Connectors
		IInterfaceType c2LocalType = types.createInterfaceType();
		IInterfaceTypeImpl c2LocalTypeImpl = implementation.promoteToInterfaceTypeImpl(c2LocalType); 
		c2LocalTypeImpl.setId("C2LocalType");
		c2LocalTypeImpl.setDescription(createDescription("C2 Local Interface"));
		archTypes.addInterfaceType(c2LocalTypeImpl);
		
		IInterfaceType c2TopType = types.createInterfaceType();
		IInterfaceTypeImpl c2TopTypeImpl = implementation.promoteToInterfaceTypeImpl(c2TopType); 
		c2TopTypeImpl.setId("C2TopType");
		c2TopTypeImpl.setDescription(createDescription("C2 Top Interface"));
		IJavaImplementation javaImplementation = javaimplementation.createJavaImplementation();
		IJavaClassFile javaClassFile = javaimplementation.createJavaClassFile();
		IJavaClassName jcn = javaimplementation.createJavaClassName();
		jcn.setValue("c2.fw.SimpleInterface");
		javaClassFile.setJavaClassName(jcn);
		javaImplementation.setMainClass(javaClassFile);
		c2TopTypeImpl.addImplementation(javaImplementation);
		archTypes.addInterfaceType(c2TopTypeImpl);
		
		IInterfaceType c2BottomType = types.createInterfaceType();
		IInterfaceTypeImpl c2BottomTypeImpl = implementation.promoteToInterfaceTypeImpl(c2BottomType); 
		c2BottomTypeImpl.setId("C2BottomType");
		c2BottomTypeImpl.setDescription(createDescription("C2 Bottom Interface"));
		javaImplementation = javaimplementation.createJavaImplementation();
		javaClassFile = javaimplementation.createJavaClassFile();
		jcn = javaimplementation.createJavaClassName();
		jcn.setValue("c2.fw.SimpleInterface");
		javaClassFile.setJavaClassName(jcn);
		javaImplementation.setMainClass(javaClassFile);
		c2BottomTypeImpl.addImplementation(javaImplementation);
		archTypes.addInterfaceType(c2BottomTypeImpl);
		
		//add component types
		//another way to do it
		//IInitializationParameter[] clockComponentInitParamsList = new IInitializationParameter[2];
		//clockComponentInitParamsList[0] = createInitializationParameter("A", "B");
		//clockComponentInitParamsList[1] = createInitializationParameter("B", "C");
		//
		//archTypes.addComponentType(
		//	createComponentType("ClockComponent_type", "Clock Component Type", "c2demo.klax.ClockComponent",clockComponentInitParamList));
		
		
		archTypes.addComponentType(
			createComponentType("Clock_type", "Clock Event Generator Type", "c2demo.coalitiondemo.ClockEventGenerator", null)
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FAw1"},
			createComponentType("FrenchAWACS_type", "French AWACS Type", "c2demo.coalitiondemo.FrenchAWACS"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrR1"},
			createComponentType("FrenchRadar_type", "French Radar Type", "c2demo.coalitiondemo.FrenchRadar"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrFrFil1"},
			createComponentType("FrenchtoFrenchFilter_type", "French to French Filter Type", "c2demo.coalitiondemo.FrenchtoFrenchFilter"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchCC1","FrenchCC2","FrenchCC3","FrenchCC4","FrenchCC5","FrenchCC6","FrenchCC7","FrenchCC8","FrenchCC9","FrenchCC10","FrenchCC11","FrenchCC12"},
			createComponentType("FrenchCC_type", "French Command and Control Type", "c2demo.coalitiondemo.FrenchCommandAndControl"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchUnAr0","FrenchUnAr1","FrenchUnAr2","FrenchUnAr3","FrenchUnAr4","FrenchUnAr5"},
			createComponentType("FrenchUnknownArtist_type", "Unknown Artist Type", "c2demo.coalitiondemo.UnknownArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchNeuAr0","FrenchNeuAr1","FrenchNeuAr2","FrenchNeuAr3","FrenchNeuAr4","FrenchNeuAr5"},
			createComponentType("FrenchNeutralArtist_type", "Neutral Artist Type", "c2demo.coalitiondemo.NeutralArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchFrAr0","FrenchFrAr1","FrenchFrAr2","FrenchFrAr3","FrenchFrAr4","FrenchFrAr5"},
			createComponentType("FrenchFriendArtist_type", "Friend Artist Type", "c2demo.coalitiondemo.FriendArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchHosAr0","FrenchHosAr1","FrenchHosAr2","FrenchHosAr3","FrenchHosAr4","FrenchHosAr5"},
			createComponentType("FrenchHostileArtist_type", "Hostile Artist Type", "c2demo.coalitiondemo.HostileArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchFwAr1"},
			createComponentType("USFixedWingArtist_type", "Fixed Wing Artist Type", "c2demo.coalitiondemo.FixedWingArtist"))
			);
		archTypes.addComponentType(
			createComponentType("FrenchBattleshipArtist_type", "Battleship Artist Type", "c2demo.coalitiondemo.BattleshipArtist")
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchAwacs1"},
			createComponentType("FrenchAWACSArtist_type", "AWACS Artist Type", "c2demo.coalitiondemo.AWACSArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchADMAr1"},
			createComponentType("FrenchAirDefenseMissileArtist_type", "Air Defense Missile Artist Type", "c2demo.coalitiondemo.AirDefenseMissileArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchTrAr0"},
			createComponentType("FrenchTroupsArtist_type", "Troups Artist Type", "c2demo.coalitiondemo.TroupsArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchCAr1"},
			createComponentType("USCarrierArtist_type", "Carrier Artist Type", "c2demo.coalitiondemo.CarrierArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchOpAr1","FrenchOpAr2","FrenchOpAr3"},
			createComponentType("FrenchOperationsArtist_type", "Operations Artist Type", "c2demo.coalitiondemo.OperationsArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FrenchLR1"},
			createComponentType("FrenchLandRetrieval_type", "Land Retrieval Type", "c2demo.coalitiondemo.LandRetrieval"))
			);
		
		archTypes.addComponentType(
			createComponentType("FrenchVirtualScreen_type", "Virtual Screen Type", "c2demo.coalitiondemo.VirtualScreen", 
			new IInitializationParameter[]{
				createInitializationParameter("Country", "France")}
			));
		
		//add components
		archStructure.addComponent(
			createComponent("Clock", "Clock Event Generator Component", "Clock_type")
			);
		archStructure.addComponent(
			createComponent("FrenchRadar", "French Radar Component", "FrenchRadar_type")
			);
		archStructure.addComponent(
			createComponent("FrenchAWACS", "French AWACS Component", "FrenchAWACS_type")
			);
		archStructure.addComponent(
			createComponent("FrenchtoFrenchFilter", "FrenchtoFrench Filter Component", "FrenchtoFrenchFilter_type")
			);
		archStructure.addComponent(
			createComponent("FrenchCC", "French Command & Control Component", "FrenchCC_type")
			);
		archStructure.addComponent(
			createComponent("FrenchUnknownArtist", "Unknown Artist Component ", "FrenchUnknownArtist_type")
			);
		
		archStructure.addComponent(
			createComponent("FrenchNeutralArtist", "Neutral Artist Component", "FrenchNeutralArtist_type")
			);
		archStructure.addComponent(
			createComponent("FrenchFriendArtist", "Friend Artist Component", "FrenchFriendArtist_type")
			);
		archStructure.addComponent(
			createComponent("FrenchHostileArtist", "Hostile Artist Component", "FrenchHostileArtist_type")
			);
		
		archStructure.addComponent(
			createComponent("USFixedWingArtist", "FixedWing Artist Component", "USFixedWingArtist_type")
			);
		archStructure.addComponent(
			createComponent("FrenchBattleshipArtist", "Battleship Artist Component", "FrenchBattleshipArtist_type")
			);
		
		archStructure.addComponent(
			createComponent("FrenchAWACSArtist", "AWACS Artist Component", "FrenchAWACSArtist_type")
			);
		
		archStructure.addComponent(
			createComponent("FrenchAirDefenseMissileArtist", "Air Defense Missile Artist Component", "FrenchAirDefenseMissileArtist_type")
			);
		archStructure.addComponent(
			createComponent("FrenchTroupsArtist", "Troups Artist Component", "FrenchTroupsArtist_type")
			);
		archStructure.addComponent(
			createComponent("USCarrierArtist", "Carrier Artist Component", "USCarrierArtist_type")
			);
		archStructure.addComponent(
			createComponent("FrenchOperationsArtist", "Operations Artist Component", "FrenchOperationsArtist_type")
			);
		archStructure.addComponent(
			createComponent("FrenchLandRetrieval", "Land Retrieval Component", "FrenchLandRetrieval_type")
			);
		archStructure.addComponent(
			createComponent("FrenchVirtualScreen", "Virtual Screen Component", "FrenchVirtualScreen_type")
			);
		
		//add connector type
		archTypes.addConnectorType(
			createConnectorType("FrenchBusConnector_type", "Bus Connector Type", "c2.legacy.conn.BusConnector")
			);
		archTypes.addConnectorType(
			createConnectorType("CommonBusConnector_type", "Common Bus Connector Type", "c2.legacy.conn.BusConnector")
			);
		archTypes.addConnectorType(
			createFredConnectorType("FrenchLocalFredConnector_type", "Fred connector Type", "c2.conn.fred.FredConnector",
			new IInitializationParameter[]{
				createInitializationParameter("host", "127.0.0.1/-.-.-.-"),
				createInitializationParameter("groupName", "France"+" FRED")}
			));
		
		//add Connectors
		archStructure.addConnector(
			createConnector("ClockBus", "Clock Connector", "CommonBusConnector_type")
			);
		archStructure.addConnector(
			createConnector("FrenchRadarFilterBus", "Radar Filter Connector", "FrenchBusConnector_type")
			);
		archStructure.addConnector(
			createConnector("FrenchFilterControlBus", "Filter and Command&Control Connector", "FrenchBusConnector_type")
			);
		archStructure.addConnector(
			createConnector("FrenchDisplayBus", "Display Connector", "FrenchBusConnector_type")
			);
		archStructure.addConnector(
			createConnector("FrenchEntityBus", "Entity Connector", "FrenchBusConnector_type")
			);
		archStructure.addConnector(
			createConnector("FrenchScreenBus", "Screen Connector", "FrenchBusConnector_type")
			);
		archStructure.addConnector(
			createConnector("FrenchLandBus", "Land Connector", "FrenchBusConnector_type")
			);
		archStructure.addConnector(
			createFredConnector("FrenchLocalFredBus", "Local Fred Connector", "FrenchLocalFredConnector_type")
			);
		
		
		//add links
		archStructure.addLink(createLink("Clock", "ClockBus"));
		
		archStructure.addLink(createLink("ClockBus", "FrenchRadar"));
		archStructure.addLink(createLink("FrenchRadar", "FrenchRadarFilterBus"));
		
		archStructure.addLink(createLink("ClockBus", "FrenchAWACS"));
		archStructure.addLink(createLink("FrenchAWACS", "FrenchRadarFilterBus"));
		
		archStructure.addLink(createLink("FrenchRadarFilterBus", "FrenchtoFrenchFilter"));
		archStructure.addLink(createLink("FrenchtoFrenchFilter", "FrenchFilterControlBus"));
		
		archStructure.addLink(createLink("FrenchFilterControlBus", "FrenchCC"));
		archStructure.addLink(createLink("FrenchCC", "FrenchDisplayBus"));
		
		archStructure.addLink(createLink("FrenchDisplayBus", "FrenchUnknownArtist"));
		archStructure.addLink(createLink("FrenchUnknownArtist", "FrenchEntityBus"));
		
		archStructure.addLink(createLink("FrenchDisplayBus", "FrenchNeutralArtist"));
		archStructure.addLink(createLink("FrenchNeutralArtist", "FrenchEntityBus"));
		
		archStructure.addLink(createLink("FrenchDisplayBus", "FrenchFriendArtist"));
		archStructure.addLink(createLink("FrenchFriendArtist", "FrenchEntityBus"));
		
		archStructure.addLink(createLink("FrenchDisplayBus", "FrenchHostileArtist"));
		archStructure.addLink(createLink("FrenchHostileArtist", "FrenchEntityBus"));
		
		archStructure.addLink(createLink("FrenchEntityBus", "USFixedWingArtist"));
		archStructure.addLink(createLink("USFixedWingArtist", "FrenchScreenBus"));
		
		archStructure.addLink(createLink("FrenchEntityBus", "FrenchBattleshipArtist"));
		archStructure.addLink(createLink("FrenchBattleshipArtist", "FrenchScreenBus"));
		
		archStructure.addLink(createLink("FrenchEntityBus", "FrenchAWACSArtist"));
		archStructure.addLink(createLink("FrenchAWACSArtist", "FrenchScreenBus"));
		
		archStructure.addLink(createLink("FrenchEntityBus", "FrenchAirDefenseMissileArtist"));
		archStructure.addLink(createLink("FrenchAirDefenseMissileArtist", "FrenchScreenBus"));
		
		archStructure.addLink(createLink("FrenchEntityBus", "FrenchTroupsArtist"));
		archStructure.addLink(createLink("FrenchTroupsArtist", "FrenchScreenBus"));
		
		archStructure.addLink(createLink("FrenchEntityBus", "USCarrierArtist"));
		archStructure.addLink(createLink("USCarrierArtist", "FrenchScreenBus"));
		
		archStructure.addLink(createLink("FrenchEntityBus", "FrenchOperationsArtist"));
		archStructure.addLink(createLink("FrenchOperationsArtist", "FrenchScreenBus"));
		
		archStructure.addLink(createLink("FrenchLandRetrieval", "FrenchLandBus"));
		archStructure.addLink(createLink("FrenchLandBus", "FrenchVirtualScreen"));
		
		//Bus-to-Bus Links
		archStructure.addLink(createLink("FrenchScreenBus", "FrenchLandBus"));
		archStructure.addLink(createFredLink(null,"FrenchFilterControlBus","FrenchLocalFredBus"));		
		
		
		//-------------------------------------------------------------------------------------
		//added for dynamic data exchange
		archTypes.addComponentType(
			createComponentType("FrenchtoUSFilter_type", "French to US Filter Type", "c2demo.coalitiondemo.FrenchtoUSFilter")
			);
		
		archTypes.addConnectorType(
			createFredConnectorType("FrenchDisFredConnector_type", "Fred connector Type", "c2.conn.fred.FredConnector",
			new IInitializationParameter[]{
				createInitializationParameter("host", "127.0.0.1/-.-.-.-"),
				createInitializationParameter("groupName", "US"+" FRED")}
			));
		
		archStructure.addComponent(
			createComponent("FrenchtoUSFilter", "French to US Filter Component", "FrenchtoUSFilter_type")
			);
		archStructure.addConnector(
			createFredConnector("FrenchDisFredBus", "Distributed Fred Connector", "FrenchDisFredConnector_type")
			);
		archStructure.addLink(createLink("FrenchRadarFilterBus", "FrenchtoUSFilter"));
		archStructure.addLink(createFredLink("FrenchtoUSFilter",null,"FrenchDisFredBus"));	
		
		//-------------------------------------------------------------------------------------
		
				
		return XArchUtils.getPrettyXmlRepresentation(xArch);
	}
	
	private static IDescription createDescription(String value){
		IDescription desc = types.createDescription();
		desc.setValue(value);
		return desc;
	}

	private static ILink createLink(String topId, String bottomId){
		ILink link = types.createLink();
		link.setId(topId + "_to_" + bottomId);
		link.setDescription(createDescription(topId + " to " + bottomId + " Link"));
		
		IPoint point1 = types.createPoint();
		IXMLLink anchor1 = instance.createXMLLink();
		anchor1.setType("simple");
		anchor1.setHref("#" + topId + ".IFACE_BOTTOM");
		point1.setAnchorOnInterface(anchor1);
		
		IPoint point2 = types.createPoint();
		IXMLLink anchor2 = instance.createXMLLink();
		anchor2.setType("simple");
		anchor2.setHref("#" + bottomId + ".IFACE_TOP");
		point2.setAnchorOnInterface(anchor2);
		
		link.addPoint(point1);
		link.addPoint(point2);
		return link;
	}
	
	private static ILink createFredLink(String topId, String bottomId, String FredId){
		
		ILink link = types.createLink();
		
		IPoint point1 = types.createPoint();
		IXMLLink anchor1 = instance.createXMLLink();
		
		if (bottomId != null){
			link.setId(FredId + "_to_" + bottomId);
			link.setDescription(createDescription(FredId + " to " + bottomId + " Link"));
			anchor1.setType("simple");
			anchor1.setHref("#" + bottomId + ".IFACE_TOP");
			point1.setAnchorOnInterface(anchor1);
		} else if (topId != null) {	
			link.setId(topId + "_to_" + FredId);
			link.setDescription(createDescription(topId + " to " + FredId + " Link"));
			anchor1.setType("simple");
			anchor1.setHref("#" + topId + ".IFACE_BOTTOM");
			point1.setAnchorOnInterface(anchor1);
		}	
		
		IPoint point2 = types.createPoint();
		IXMLLink anchor2 = instance.createXMLLink();
		anchor2.setType("simple");
		anchor2.setHref("#" + FredId + ".IFACE_LOCAL");
		point2.setAnchorOnInterface(anchor2);
		
		link.addPoint(point1);
		link.addPoint(point2);
		return link;
	}
	
	
	private static IInitializationParameter createInitializationParameter(String name, String value){
		IInitializationParameter ip = javainitparams.createInitializationParameter();
		ip.setName(name);
		ip.setValue(value);
		return ip;
	}
	
	private static IComponent createComponent(String id, String description, String typeId){
		IComponent c = types.createComponent();
		
		c.setId(id);
		IDescription desc = types.createDescription();
		desc.setValue(description);
		c.setDescription(desc);
		
		IInterface topIface = types.createInterface();
		topIface.setId(id + ".IFACE_TOP");
		topIface.setDescription(createDescription(description + " Top Interface"));
		IDirection topDirection = types.createDirection();
		topDirection.setValue("inout");
		topIface.setDirection(topDirection);
	
		IXMLLink topIfaceType = types.createXMLLink();
		topIfaceType.setType("simple");
		topIfaceType.setHref("#C2TopType");
		
		topIface.setType(topIfaceType);
		
		IInterface bottomIface = types.createInterface();
		bottomIface.setId(id + ".IFACE_BOTTOM");
		bottomIface.setDescription(createDescription(description + " Bottom Interface"));
		IDirection bottomDirection = types.createDirection();
		bottomDirection.setValue("inout");
		bottomIface.setDirection(bottomDirection);
	
		IXMLLink bottomIfaceType = types.createXMLLink();
		bottomIfaceType.setType("simple");
		bottomIfaceType.setHref("#C2BottomType");
		
		bottomIface.setType(bottomIfaceType);
		
		c.addInterface(topIface);
		c.addInterface(bottomIface);

		IXMLLink cType = types.createXMLLink();
		cType .setType("simple");
		cType .setHref("#" + typeId);
		c.setType(cType);
		return c;
	}
	
	private static IConnector createConnector(String id, String description, String typeId){
		IConnector c = types.createConnector();
		
		c.setId(id);
		IDescription desc = types.createDescription();
		desc.setValue(description);
		c.setDescription(desc);
		
		IInterface topIface = types.createInterface();
		topIface.setId(id + ".IFACE_TOP");
		topIface.setDescription(createDescription(description + " Top Interface"));
		IDirection topDirection = types.createDirection();
		topDirection.setValue("inout");
		topIface.setDirection(topDirection);
	
		IXMLLink topIfaceType = types.createXMLLink();
		topIfaceType.setType("simple");
		topIfaceType.setHref("#C2TopType");
		
		topIface.setType(topIfaceType);
		
		IInterface bottomIface = types.createInterface();
		bottomIface.setId(id + ".IFACE_BOTTOM");
		bottomIface.setDescription(createDescription(description + " Bottom Interface"));
		IDirection bottomDirection = types.createDirection();
		bottomDirection.setValue("inout");
		bottomIface.setDirection(bottomDirection);
	
		IXMLLink bottomIfaceType = types.createXMLLink();
		bottomIfaceType.setType("simple");
		bottomIfaceType.setHref("#C2BottomType");
		
		bottomIface.setType(bottomIfaceType);
		
		c.addInterface(topIface);
		c.addInterface(bottomIface);

		IXMLLink cType = types.createXMLLink();
		cType.setType("simple");
		cType.setHref("#" + typeId);
		c.setType(cType);
		return c;
	}
	
	private static IConnector createFredConnector(String id, String description, String typeId){
		/*IConnector c = types.createConnector();
		
		c.setId(id);
		IDescription desc = types.createDescription();
		desc.setValue(description);
		c.setDescription(desc);
		
		IInterface topIface = types.createInterface();
		topIface.setId(id + ".IFACE_LOCAL");
		topIface.setDescription(createDescription(description + " Top Interface"));
		IDirection topDirection = types.createDirection();
		topDirection.setValue("inout");
		topIface.setDirection(topDirection);
		
		IXMLLink topIfaceType = types.createXMLLink();
		topIfaceType.setType("simple");
		topIfaceType.setHref("#C2TopType");
		
		topIface.setType(topIfaceType);
		
		IInterface bottomIface = types.createInterface();
		bottomIface.setId(id + ".IFACE_BOTTOM");
		bottomIface.setDescription(createDescription(description + " Bottom Interface"));
		IDirection bottomDirection = types.createDirection();
		bottomDirection.setValue("inout");
		bottomIface.setDirection(bottomDirection);
		
		IXMLLink bottomIfaceType = types.createXMLLink();
		bottomIfaceType.setType("simple");
		bottomIfaceType.setHref("#C2BottomType");
		
		bottomIface.setType(bottomIfaceType);
		
		c.addInterface(topIface);
		//c.addInterface(bottomIface);
		
		IXMLLink cType = types.createXMLLink();
		cType.setType("simple");
		cType.setHref("#" + typeId);
		c.setType(cType);
		return c;*/
		IConnector c = types.createConnector();
		
		c.setId(id);
		IDescription desc = types.createDescription();
		desc.setValue(description);
		c.setDescription(desc);
		
		IInterface localIface = types.createInterface();
		localIface.setId(id + ".IFACE_LOCAL");
		localIface.setDescription(createDescription(description + " Local Interface"));
		IDirection fredDirection = types.createDirection();
		fredDirection.setValue("inout");
		localIface.setDirection(fredDirection);
		
		IXMLLink localIfaceType = types.createXMLLink();
		localIfaceType.setType("simple");
		
		// this is absolute hack....find out another way to do this!!
		String bottomName = "FrenchLocalFredBus";
		String topName = "FrenchDisFredBus";
		if (id.equals(bottomName)){
			localIfaceType.setHref("#C2LocalType");
		} else if (id.equals(topName)){ 
			localIfaceType.setHref("#C2LocalType");
		}
		
		localIface.setType(localIfaceType);
		
		c.addInterface(localIface);
		IXMLLink cType = types.createXMLLink();
		cType.setType("simple");
		cType.setHref("#" + typeId);
		c.setType(cType);
		return c;
	}
	
	private static IComponentType createComponentType(String id, String description, String javaClassName){
		return createComponentType(id, description, javaClassName, new IInitializationParameter[0]);
	}
	
	private static IComponentType createComponentType(String id, String description, String javaClassName, IInitializationParameter[] params){
		IComponentType ct0 = types.createComponentType();
		IVariantComponentType ct1 = variants.promoteToVariantComponentType(ct0);
		IVariantComponentTypeImpl ct = implementation.promoteToVariantComponentTypeImpl(ct1);
		
		ct.setId(id);
		IDescription desc = types.createDescription();
		desc.setValue(description);
		ct.setDescription(desc);
		
		ISignature topSig = types.createSignature();
		IDirection topDirection = types.createDirection();
		topDirection.setValue("inout");
		topSig.setDirection(topDirection);
		
		IXMLLink topSigType = types.createXMLLink();
		topSigType.setType("simple");
		topSigType.setHref("#C2TopType");
		
		topSig.setType(topSigType);
		
		ISignature botSig = types.createSignature();
		IDirection botDirection = types.createDirection();
		botDirection.setValue("inout");
		botSig.setDirection(botDirection);
		
		IXMLLink botSigType = types.createXMLLink();
		botSigType.setType("simple");
		botSigType.setHref("#C2BottomType");
		botSig.setType(botSigType);
		
		ct.addSignature(topSig);
		ct.addSignature(botSig);
		
		IJavaImplementation javaImplementation = javaimplementation.createJavaImplementation();
		//IJavaClassFile javaClassFile = javaimplementation.createJavaClassFile();
		
		//added for initialization parameters
		IJavaClassFile javaClassFileParams;
		
		if(params != null){
			javaClassFileParams = javainitparams.createJavaClassFileParams();
			((IJavaClassFileParams)javaClassFileParams).addInitializationParameters(Arrays.asList(params));
		}
		else{
			javaClassFileParams = javaimplementation.createJavaClassFile();
		}

		IJavaClassName jcn = javaimplementation.createJavaClassName();
		jcn.setValue(javaClassName);
		javaClassFileParams.setJavaClassName(jcn);
		javaImplementation.setMainClass(javaClassFileParams);
		ct.addImplementation(javaImplementation);
		
		//if (!cfInitParam.isEmpty()){
		//for (int i=0; i< cfInitParam.length; i++){
			//IInitializationParameter cfParam = javainitparams.createInitializationParameter();
			//cfParam.setName(cfInitParam[i].getName());
			//cfParam.setValue(cfInitParam[i].getValue());
			//javaClassFile.addInitializationParameter(cfParam);
		//}
		//}
		
		return ct;
	}

	private static IConnectorType createConnectorType(String id, String description, String javaClassName){
		return createConnectorType(id, description, javaClassName, new IInitializationParameter[0]);
	}
	
	private static IConnectorType createConnectorType(String id, String description, String javaClassName,IInitializationParameter[] connparams){
		IConnectorType ct0 = types.createConnectorType();
		IVariantConnectorType ct1 = variants.promoteToVariantConnectorType(ct0);
		IVariantConnectorTypeImpl ct = implementation.promoteToVariantConnectorTypeImpl(ct1);
		
		ct.setId(id);
		IDescription desc = types.createDescription();
		desc.setValue(description);
		ct.setDescription(desc);
		
		ISignature topSig = types.createSignature();
		IDirection topDirection = types.createDirection();
		topDirection.setValue("inout");
		topSig.setDirection(topDirection);
		
		IXMLLink topSigType = types.createXMLLink();
		topSigType.setType("simple");
		topSigType.setHref("#C2TopType");
		
		topSig.setType(topSigType);
		
		ISignature botSig = types.createSignature();
		IDirection botDirection = types.createDirection();
		botDirection.setValue("inout");
		botSig.setDirection(botDirection);
		
		IXMLLink botSigType = types.createXMLLink();
		botSigType.setType("simple");
		botSigType.setHref("#C2BottomType");
		botSig.setType(botSigType);
		
		ct.addSignature(topSig);
		ct.addSignature(botSig);
		
		//added for initialization parameters
		IJavaClassFile javaClassFileParams;
		
		if(connparams != null){
			javaClassFileParams = javainitparams.createJavaClassFileParams();
			((IJavaClassFileParams)javaClassFileParams).addInitializationParameters(Arrays.asList(connparams));
		}
		else{
			javaClassFileParams = javaimplementation.createJavaClassFile();
		}
		
		IJavaImplementation javaImplementation = javaimplementation.createJavaImplementation();
		
		IJavaClassName jcn = javaimplementation.createJavaClassName();
		jcn.setValue(javaClassName);
		javaClassFileParams.setJavaClassName(jcn);
		javaImplementation.setMainClass(javaClassFileParams);
		ct.addImplementation(javaImplementation);
		
		return ct;
	}
	
	private static IConnectorType createFredConnectorType(String id, String description, String javaClassName,IInitializationParameter[] connparams){
		IConnectorType ct0 = types.createConnectorType();
		IVariantConnectorType ct1 = variants.promoteToVariantConnectorType(ct0);
		IVariantConnectorTypeImpl ct = implementation.promoteToVariantConnectorTypeImpl(ct1);
		
		ct.setId(id);
		IDescription desc = types.createDescription();
		desc.setValue(description);
		ct.setDescription(desc);
		
		ISignature topSig = types.createSignature();
		IDirection topDirection = types.createDirection();
		topDirection.setValue("inout");
		topSig.setDirection(topDirection);
		
		IXMLLink topSigType = types.createXMLLink();
		topSigType.setType("simple");
		topSigType.setHref("#C2LocalType");
		
		topSig.setType(topSigType);
		
		ISignature botSig = types.createSignature();
		IDirection botDirection = types.createDirection();
		botDirection.setValue("inout");
		botSig.setDirection(botDirection);
		
		IXMLLink botSigType = types.createXMLLink();
		botSigType.setType("simple");
		botSigType.setHref("#C2LocalType");
		botSig.setType(botSigType);
		
		ct.addSignature(topSig);
		ct.addSignature(botSig);
		
		//added for initialization parameters
		IJavaClassFile javaClassFileParams;
		
		if(connparams != null){
			javaClassFileParams = javainitparams.createJavaClassFileParams();
			((IJavaClassFileParams)javaClassFileParams).addInitializationParameters(Arrays.asList(connparams));
		}
		else{
			javaClassFileParams = javaimplementation.createJavaClassFile();
		}
		
		IJavaImplementation javaImplementation = javaimplementation.createJavaImplementation();
		
		IJavaClassName jcn = javaimplementation.createJavaClassName();
		jcn.setValue(javaClassName);
		javaClassFileParams.setJavaClassName(jcn);
		javaImplementation.setMainClass(javaClassFileParams);
		ct.addImplementation(javaImplementation);
		
		return ct;
	}
	
	//new
	private static IComponentType addRulesComp(String ruleNames[], IComponentType ct0) {
		IVariantComponentTypeImplVers ct1 = versions.promoteToVariantComponentTypeImplVers((IVariantComponentTypeImpl)ct0);
		IVariantComponentTypeImplVersSpec ct = messages.promoteToVariantComponentTypeImplVersSpec(ct1);
		ct.setMessageCausalitySpecification(getMCS(ruleNames));
		return ct;
	}
	
	//new
	private static IConnectorType addRulesConn(String ruleNames[], IConnectorType ct0) {
		IVariantConnectorTypeImplVers ct1 = versions.promoteToVariantConnectorTypeImplVers((IVariantConnectorTypeImpl)ct0);
		IVariantConnectorTypeImplVersSpec ct = messages.promoteToVariantConnectorTypeImplVersSpec(ct1);
		ct.setMessageCausalitySpecification(getMCS(ruleNames));
		return ct;
	}
	
	//new
	private static IMessageCausalitySpecification getMCS(String ruleNames[]) {
		IMessageCausalitySpecification mcsRef = messages.createMessageCausalitySpecification();
		for (int i = 0; i < ruleNames.length; i++){
			IXMLLink ruleRef = types.createXMLLink();
			ruleRef.setType("simple");
			ruleRef.setHref("#"+ruleNames[i]);
			mcsRef.addRule(ruleRef);
		}
		return mcsRef;
	}
	
	//new
	private static INamedProperty createNamedProperty(String name, String value) {
		INamedProperty npRef = messages.createNamedProperty();
		IPropertyName nameRef = messages.createPropertyName();
		nameRef.setValue(name);
		IPropertyValue valueRef = messages.createPropertyValue();
		valueRef.setValue(value);
		
		npRef.setName(nameRef);
		npRef.setValue(valueRef);
		return npRef;
	}
	
	//new
//	IPropertyName iname = np.getName();
//	String name = (String)iname.getValue();
//	IPropertyValue ivalue = np.getValue();
//	String value = (String)ivalue.getValue();
//	return createNamedProperty(name, value);        

//}
//new
private static INamedPropertyMessage createMessage(String id, String kind, String description, String count, String name, String type) {
	INamedPropertyMessage messageRef = messages.createNamedPropertyMessage();
	IDescription descriptionRef = createDescription(description);
	ICount countRef = messages.createCount();
	countRef.setValue(count);
	IMessageName nameRef = messages.createMessageName();
	nameRef.setValue(name);
	IMessageType typeRef = messages.createMessageType();
	typeRef.setValue(type);
	
	messageRef.setId(id);
	messageRef.setDescription(descriptionRef);
	messageRef.setKind(kind);
	messageRef.setCount(countRef);
	messageRef.setName(nameRef);
	messageRef.setType(typeRef);
	return messageRef;
}

//new
private static INamedPropertyMessage createMessage(String id, String kind, String description, String count, String name, String type, INamedProperty np[]) {
	INamedPropertyMessage npmRef = createMessage(id, kind, description, count, name, type);
	for (int i = 0; i < np.length; i++ )
		npmRef.addNamedProperty(np[i]);
	return npmRef;
}

//new
private static INamedPropertyMessage msgCopy(INamedPropertyMessage messageRef) {
	String id = (String)messageRef.getId();
	IDescription idescription = messageRef.getDescription();
	String description = (String)idescription.getValue();
	String kind = (String)messageRef.getKind();
	ICount icount = messageRef.getCount();
	String count = (String)icount.getValue();
	IMessageName iname = messageRef.getName();
	String name = (String)iname.getValue();
	IMessageType itype = messageRef.getType();
	String type = (String)itype.getValue();
	Collection npRef = messageRef.getAllNamedPropertys();
	INamedProperty namedProperties[] = new INamedProperty[npRef.size()];
	int i = 0;
	for (Iterator it = npRef.iterator(); it.hasNext(); ) {
		namedProperties[i++] = npCopy((INamedProperty)(it.next()));
	}
	
	return createMessage(id, kind, description, count, name, type, namedProperties);
}

//new
private static IProductionRule createRule(String id, String description, INamedPropertyMessage recvMsg[], INamedPropertyMessage sendMsg[], String causeTime) {
	IProductionRule ruleRef = messages.createProductionRule();
	IDescription descriptionRef = createDescription(description);
	ICauseTime causeTimeRef = messages.createCauseTime();
	causeTimeRef.setValue(causeTime);
	
	ruleRef.setId(id);
	ruleRef.setDescription(descriptionRef);
	for (int i = 0; i < recvMsg.length; i++)
		ruleRef.addReceiveMessage(msgCopy(recvMsg[i]));
	for (int i = 0; i < sendMsg.length; i++)
		ruleRef.addSendMessage(msgCopy(sendMsg[i]));
	ruleRef.setCauseTime(causeTimeRef);
	return ruleRef;
}

//new
private static IProductionRule[] getRuleSpecification() {
	
	//Generic
	INamedPropertyMessage eGenericN = createMessage("eGen-notif", "notification", "entity notification", "1", "entity", "c2.fw.NamedPropertyMessage");
	INamedPropertyMessage oGenericN = createMessage("oGen-notif", "notification", "operation notification", "1", "operation", "c2.fw.NamedPropertyMessage");
	INamedPropertyMessage mGenericN = createMessage("mGen-notif", "notification", "map notification", "1", "map", "c2.fw.NamedPropertyMessage");
	INamedPropertyMessage reGenericN = createMessage("reGen-notif", "notification", "remove entity notification", "1", "rem-entity", "c2.fw.NamedPropertyMessage");
	INamedPropertyMessage roGenericN = createMessage("roGen-notif", "notification", "remove operation notification", "1", "rem-operation", "c2.fw.NamedPropertyMessage");
	
	INamedPropertyMessage eDimAirN = createMessage("eDimAir-notif", "notification", "entity dimension air notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("dimension",BattleDimension.AIR.toString())});
	INamedPropertyMessage eDimGrndN = createMessage("eDimGrnd-notif", "notification", "entity dimension ground notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("dimension",BattleDimension.GROUND.toString())});
	INamedPropertyMessage eDimSeaN = createMessage("eDimSea-notif", "notification", "entity dimension sea notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("dimension",BattleDimension.SEA.toString())});
	INamedPropertyMessage eDimSubSurN = createMessage("eDimSubSur-notif", "notification", "entity dimension sub-surface notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("dimension",BattleDimension.SUBSURFACE.toString())});
	
	INamedPropertyMessage eSymCharN = createMessage("eSymChar-notif", "notification", "entity Sym char notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("symchar","symchar")});
	INamedPropertyMessage eSymColorN = createMessage("eSymColor-notif", "notification", "entity Sym Color notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("symcolor","color")});
	INamedPropertyMessage eSymFontFillCharN = createMessage("eSymFontFillChar-notif", "notification", "entity Sym Font and Fill Char notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("symfont","font"),createNamedProperty("fillchar","fillchar")});
	
	//Air Defense Missile Artist
	INamedPropertyMessage eAdmaN = createMessage("eAdma-notif", "notification", "entity type airdefensemissile notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("type",EntityType.AIRDEFENSEMISSILE.toString())});
	
	//AWACS Artist
	INamedPropertyMessage eAwN = createMessage("eAw-notif", "notification", "entity type awacs notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("type",EntityType.AWACS.toString())});
	
	//Battleship Artist
	INamedPropertyMessage eBsN = createMessage("eBs-notif", "notification", "entity type battleship notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("type",EntityType.BATTLESHIP.toString())});
	
	//Carrier Artist
	INamedPropertyMessage eCarrierN = createMessage("eCar-notif", "notification", "entity type carrier notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("type",EntityType.CARRIER.toString())});
	
	//ClockEvent Generator
	INamedPropertyMessage cClkN = createMessage("cClk-notif", "notification", "clock type notification", "1", "clock", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("tick","tickcount")});
	
	//Fixed Wing Artist
	INamedPropertyMessage eFwN = createMessage("eFw-notif", "notification", "entity type fixed wing notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("type",EntityType.FIXEDWING.toString())});
	
	//Fixed Wing bomber Artist
	INamedPropertyMessage eFwbN = createMessage("eFwb-notif", "notification", "entity type fixed wing bomber notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("type",EntityType.FIXEDWINGBOMBER.toString())});
	
	//French Command and Control
	INamedPropertyMessage eCounFraN = createMessage("eCounFra-notif", "notification", "entity country france notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("country",Country.FRANCE.toString())});
	INamedPropertyMessage eCounMexN = createMessage("eCounMex-notif", "notification", "entity country mexico notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("country",Country.MEXICO.toString())});
	INamedPropertyMessage eCounLebN = createMessage("eCounLeb-notif", "notification", "entity country lebanon notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("country",Country.LEBANON.toString())});
	INamedPropertyMessage eCounUSN = createMessage("eCounUS-notif", "notification", "entity country usa notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("country",Country.USA.toString())});
	INamedPropertyMessage eCounUnN = createMessage("eCounUn-notif", "notification", "entity country unknown notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("country",Country.UNKNOWN.toString())});
	
	//French to French Filter
	
	//Friend Artist
	INamedPropertyMessage eFrAfN = createMessage("eFrAf-notif", "notification", "entity friend affiliation notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("affiliation",Affiliation.FRIEND.toString())});
	INamedPropertyMessage eFrTacN = createMessage("eFrTac-notif", "notification", "entity friend tac color notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("taccolor","Friend Color")});
	
	//Hostile Artist
	INamedPropertyMessage eHosAfN = createMessage("eHosAf-notif", "notification", "entity hostile affiliation notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("affiliation",Affiliation.HOSTILE.toString())});
	INamedPropertyMessage eHosTacN = createMessage("eHosTac-notif", "notification", "entity hostile tac color notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("taccolor","Hostile Color")});
	
	//Land retrieval
	INamedPropertyMessage mLrN = createMessage("mLr-notif", "notification", "map land retrieval notification", "1", "map", "c2.fw.NamedPropertyMessage");
	
	//Neutral Artist
	INamedPropertyMessage eNeuAfN = createMessage("eNeuAf-notif", "notification", "entity neutral affiliation notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("affiliation",Affiliation.NEUTRAL.toString())});
	INamedPropertyMessage eNeuTacN = createMessage("eNeuTac-notif", "notification", "entity neutral tac color notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("taccolor","Neutral Color")});
	
	//Operations Artist
	INamedPropertyMessage oTacFN = createMessage("oTacF-notif", "notification", "operation tac font notification", "1", "operation", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("tacfont","Tac font")});
	
	//Troups Artist
	INamedPropertyMessage eTrN = createMessage("eTr-notif", "notification", "entity type troups notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("type",EntityType.TROUPS.toString())});
	
	//Unknown Artist
	INamedPropertyMessage eUnAfN = createMessage("eUnAf-notif", "notification", "entity unknown affiliation notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("affiliation",Affiliation.UNKNOWN.toString())});
	INamedPropertyMessage eUnTacN = createMessage("eUnTac-notif", "notification", "entity unknown tac color notification", "1", "entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("taccolor","Unknown Color")});
	
	//Virtual Screen
	INamedPropertyMessage mR = createMessage("map-req", "notification", "map request", "1", "map", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("action","request")});
	
	IProductionRule rules[] = {
		
		//AIR Defense Missile Artist
		createRule("FrenchADMAr1", "eAdmaN>eAdmaN", new INamedPropertyMessage[]{eAdmaN}, new INamedPropertyMessage[]{eAdmaN}, "MostRecent"),
		createRule("FrenchADMAr2", "eAdmaN>eSymCharN", new INamedPropertyMessage[]{eAdmaN}, new INamedPropertyMessage[]{eSymCharN}, "MostRecent"),
		
		//AWACS Artist
		createRule("FrenchAwacs1", "eAwN>eAwN", new INamedPropertyMessage[]{eAwN}, new INamedPropertyMessage[]{eAwN}, "MostRecent"),
		
		//Carrier Artist
		createRule("FrenchCAr1", "eCarrierN>eCarrierN", new INamedPropertyMessage[]{eCarrierN}, new INamedPropertyMessage[]{eCarrierN}, "MostRecent"),
		
		//FixedWing Artist
		createRule("FrenchFwAr1", "eFwN>eFwN", new INamedPropertyMessage[]{eFwN}, new INamedPropertyMessage[]{eFwN}, "MostRecent"),
		
		//FixedWingBomber Artist
		createRule("FrenchFwbAr1", "eFwbN>eFwbN", new INamedPropertyMessage[]{eFwbN}, new INamedPropertyMessage[]{eFwbN}, "MostRecent"),
		
		//French Command and Control
		createRule("FrenchCC1","eCounFraN>eFrAfN",new INamedPropertyMessage[]{eCounFraN},new INamedPropertyMessage[]{eFrAfN},"MostRecent"),
		createRule("FrenchCC2","eCounMexN>eNeuAfN",new INamedPropertyMessage[]{eCounMexN},new INamedPropertyMessage[]{eNeuAfN},"MostRecent"),
		createRule("FrenchCC3","eCounLebN>eHosAfN",new INamedPropertyMessage[]{eCounLebN},new INamedPropertyMessage[]{eHosAfN},"MostRecent"),
		createRule("FrenchCC4","eCounUSN>eFrAfN",new INamedPropertyMessage[]{eCounUSN},new INamedPropertyMessage[]{eFrAfN},"MostRecent"),
		createRule("FrenchCC5","eCounUnN>eUnAfN",new INamedPropertyMessage[]{eCounUnN},new INamedPropertyMessage[]{eUnAfN},"MostRecent"),
		
		createRule("FrenchCC6","eFwbN>eDimAirN",new INamedPropertyMessage[]{eFwbN},new INamedPropertyMessage[]{eDimAirN},"MostRecent"),
		createRule("FrenchCC7","eBsN>eDimSeaN",new INamedPropertyMessage[]{eBsN},new INamedPropertyMessage[]{eDimSeaN},"MostRecent"),
		createRule("FrenchCC8","eTrN>eDimGrndN",new INamedPropertyMessage[]{eTrN},new INamedPropertyMessage[]{eDimGrndN},"MostRecent"),
		createRule("FrenchCC9","eAdmaN>eDimGrndN",new INamedPropertyMessage[]{eAdmaN},new INamedPropertyMessage[]{eDimGrndN},"MostRecent"),
		createRule("FrenchCC10","eFwN>eDimAirN",new INamedPropertyMessage[]{eFwN},new INamedPropertyMessage[]{eDimAirN},"MostRecent"),
		createRule("FrenchCC11","eCarrierN>eDimSeaN",new INamedPropertyMessage[]{eCarrierN},new INamedPropertyMessage[]{eDimSeaN},"MostRecent"),
		createRule("FrenchCC12","eAwN>eDimAirN",new INamedPropertyMessage[]{eAwN},new INamedPropertyMessage[]{eDimAirN},"MostRecent"),
		
		//French AWACS
		createRule("FAw1","cClkN>eTrN,eAdmaN",new INamedPropertyMessage[]{cClkN},new INamedPropertyMessage[]{eTrN,eAdmaN},"MostRecent"),
		
		//French Radar
		createRule("FrR1","cClkN>eBsN,eAwN",new INamedPropertyMessage[]{cClkN},new INamedPropertyMessage[]{eBsN,eAwN},"MostRecent"),
		
		//French to French Filter
		createRule("FrFrFil1","eGenericN>eGenericN",new INamedPropertyMessage[]{eGenericN},new INamedPropertyMessage[]{eGenericN},"MostRecent"),
		
		//Friend Artist
		createRule("FrenchFrAr0", "eFrAfN>eFrAfN", new INamedPropertyMessage[]{eFrAfN}, new INamedPropertyMessage[]{eFrAfN}, "MostRecent"),
		
		createRule("FrenchFrAr1", "eDimAirN>eDimAirN", new INamedPropertyMessage[]{eDimAirN}, new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
		createRule("FrenchFrAr2", "eDimGrndN>eDimGrndN", new INamedPropertyMessage[]{eDimGrndN}, new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
		createRule("FrenchFrAr3", "eDimSeaN>eDimSeaN", new INamedPropertyMessage[]{eDimSeaN}, new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
		createRule("FrenchFrAr4", "eDimSubSurN>eDimSubSurN", new INamedPropertyMessage[]{eDimSubSurN}, new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
		
		createRule("FrenchFrAr5", "oGenericN>oGenericN", new INamedPropertyMessage[]{oGenericN}, new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
		
		//Hostile Artist
		createRule("FrenchHosAr0", "eHosAfN>eHosAfN", new INamedPropertyMessage[]{eHosAfN}, new INamedPropertyMessage[]{eHosAfN}, "MostRecent"),
		
		createRule("FrenchHosAr1", "eDimAirN>eDimAirN", new INamedPropertyMessage[]{eDimAirN}, new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
		createRule("FrenchHosAr2", "eDimGrndN>eDimGrndN", new INamedPropertyMessage[]{eDimGrndN}, new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
		createRule("FrenchHosAr3", "eDimSeaN>eDimSeaN", new INamedPropertyMessage[]{eDimSeaN}, new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
		createRule("FrenchHosAr4", "eDimSubSurN>eDimSubSurN", new INamedPropertyMessage[]{eDimSubSurN}, new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
		
		createRule("FrenchHosAr5", "oGenericN>oGenericN", new INamedPropertyMessage[]{oGenericN}, new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
		
		//Land Retrieval
		createRule("FrenchLR1", "mR>mLrN", new INamedPropertyMessage[]{mR}, new INamedPropertyMessage[]{mLrN}, "MostRecent"),
		
		//Neutral Artist
		createRule("FrenchNeuAr0", "eNeuAfN>eNeuAfN", new INamedPropertyMessage[]{eNeuAfN}, new INamedPropertyMessage[]{eNeuAfN}, "MostRecent"),
		
		createRule("FrenchNeuAr1", "eDimAirN>eDimAirN", new INamedPropertyMessage[]{eDimAirN}, new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
		createRule("FrenchNeuAr2", "eDimGrndN>eDimGrndN", new INamedPropertyMessage[]{eDimGrndN}, new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
		createRule("FrenchNeuAr3", "eDimSeaN>eDimSeaN", new INamedPropertyMessage[]{eDimSeaN}, new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
		createRule("FrenchNeuAr4", "eDimSubSurN>eDimSubSurN", new INamedPropertyMessage[]{eDimSubSurN}, new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
		
		createRule("FrenchNeuAr5", "oGenericN>oGenericN", new INamedPropertyMessage[]{oGenericN}, new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
		
		//Operations Artist
		createRule("FrenchOpAr1", "oGenericN>oGenericN", new INamedPropertyMessage[]{oGenericN}, new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
		createRule("FrenchOpAr2", "roGenericN>roGenericN", new INamedPropertyMessage[]{roGenericN}, new INamedPropertyMessage[]{roGenericN}, "MostRecent"),
		createRule("FrenchOpAr3", "reGenericN>reGenericN", new INamedPropertyMessage[]{reGenericN}, new INamedPropertyMessage[]{reGenericN}, "MostRecent"),
		
		//TroupsArtist
		createRule("FrenchTrAr0", "eTrN>eTrN", new INamedPropertyMessage[]{eTrN}, new INamedPropertyMessage[]{eTrN}, "MostRecent"),
		
		//Unknown Artist
		createRule("FrenchUnAr0", "eUnAfN>eUnAfN", new INamedPropertyMessage[]{eUnAfN}, new INamedPropertyMessage[]{eUnAfN}, "MostRecent"),
		
		createRule("FrenchUnAr1", "eDimAirN>eDimAirN", new INamedPropertyMessage[]{eDimAirN}, new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
		createRule("FrenchUnAr2", "eDimGrndN>eDimGrndN", new INamedPropertyMessage[]{eDimGrndN}, new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
		createRule("FrenchUnAr3", "eDimSeaN>eDimSeaN", new INamedPropertyMessage[]{eDimSeaN}, new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
		createRule("FrenchUnAr4", "eDimSubSurN>eDimSubSurN", new INamedPropertyMessage[]{eDimSubSurN}, new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
		
		createRule("FrenchUnAr5", "oGenericN>oGenericN", new INamedPropertyMessage[]{oGenericN}, new INamedPropertyMessage[]{oGenericN}, "MostRecent")
			
	};
	
	return rules;
}
//new
//public static ObjRef npCopy(ObjRef npRef) {
	//String name = (String)xArch.get((ObjRef)xArch.get(npRef, "Name"), "Value");
	//String value = (String)xArch.get((ObjRef)xArch.get(npRef, "Value"), "Value");
	//return createNamedProperty(name, value);        
//}

public static INamedProperty npCopy(INamedProperty npRef) {
	String name = (String)((IPropertyName)npRef.getName()).getValue();
	String value = (String)((IPropertyValue)npRef.getValue()).getValue();
	return createNamedProperty(name, value);        
}

}
