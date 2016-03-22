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
import java.awt.Color;

import c2demo.coalitiondemo.*;

public class DisUSDescription{

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
			addRulesComp(new String[]{"USR1","USR2"},
			createComponentType("USRadar_type", "US Radar Type", "c2demo.coalitiondemo.USRadar"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"USUSFil1","USUSFil2"},
			createComponentType("UStoUSFilter_type", "US to US Filter Type", "c2demo.coalitiondemo.UStoUSFilter"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"USCC1","USCC2","USCC3","USCC4","USCC5","USCC6","USCC7","USCC8","USCC9","USCC10","USCC11","USCC12","USCC13"},
			createComponentType("USCC_type", "US Command and Control Type", "c2demo.coalitiondemo.USCommandAndControl"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"USUnAr0","USUnAr1","USUnAr2","USUnAr3","USUnAr4","USUnAr5"},
			createComponentType("USUnknownArtist_type", "Unknown Artist Type", "c2demo.coalitiondemo.UnknownArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"USNeuAr0","USNeuAr1","USNeuAr2","USNeuAr3","USNeuAr4","NeuAr5"},
			createComponentType("USNeutralArtist_type", "Neutral Artist Type", "c2demo.coalitiondemo.NeutralArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"USFrAr0","USFrAr1","USFrAr2","USFrAr3","USFrAr4","USFrAr5"},
			createComponentType("USFriendArtist_type", "Friend Artist Type", "c2demo.coalitiondemo.FriendArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"USHosAr0","USHosAr1","USHosAr2","USHosAr3","USHosAr4","USHosAr5"},
			createComponentType("USHostileArtist_type", "Hostile Artist Type", "c2demo.coalitiondemo.HostileArtist"))
			);
		
		archTypes.addComponentType(
			addRulesComp(new String[]{"USFwAr1"},
			createComponentType("USFixedWingArtist_type", "Fixed Wing Artist Type", "c2demo.coalitiondemo.FixedWingArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"USBSAr1"},
			createComponentType("USBattleshipArtist_type", "Battleship Artist Type", "c2demo.coalitiondemo.BattleshipArtist"))
			);
		
		archTypes.addComponentType(
			addRulesComp(new String[]{"FranceAwacs1"},
			createComponentType("FrenchAWACSArtist_type", "AWACS Artist Type", "c2demo.coalitiondemo.AWACSArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"FranceADMAr1"},
			createComponentType("FrenchAirDefenseMissileArtist_type", "Air Defense Missile Artist Type", "c2demo.coalitiondemo.AirDefenseMissileArtist"))
			);
		
		archTypes.addComponentType(
			addRulesComp(new String[]{"USTrAr0"},
			createComponentType("USTroupsArtist_type", "Troups Artist Type", "c2demo.coalitiondemo.TroupsArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"USCAr1"},
			createComponentType("USCarrierArtist_type", "Carrier Artist Type", "c2demo.coalitiondemo.CarrierArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"USOpAr1","USOpAr2","USOpAr3"},
			createComponentType("USOperationsArtist_type", "Operations Artist Type", "c2demo.coalitiondemo.OperationsArtist"))
			);
		archTypes.addComponentType(
			addRulesComp(new String[]{"USLR1"},
			createComponentType("USLandRetrieval_type", "Land Retrieval Type", "c2demo.coalitiondemo.LandRetrieval"))
			);
		
		archTypes.addComponentType(
			createComponentType("USVirtualScreen_type", "Virtual Screen Type", "c2demo.coalitiondemo.VirtualScreen", 
			new IInitializationParameter[]{
				createInitializationParameter("Country", "US")}
			));
		
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
		
		//add connector type
		archTypes.addConnectorType(
			createConnectorType("USBusConnector_type", "US Bus Connector Type", "c2.legacy.conn.BusConnector")
			);
		archTypes.addConnectorType(
			createConnectorType("CommonBusConnector_type", "Common Bus Connector Type", "c2.legacy.conn.BusConnector")
			);
		archTypes.addConnectorType(
			createFredConnectorType("USLocalFredConnector_type", "Fred connector Type", "c2.conn.fred.FredConnector",
			new IInitializationParameter[]{
				createInitializationParameter("host", "127.0.0.1/-.-.-.-"),
				createInitializationParameter("groupName", "US"+" FRED")}
			));
		
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
		
		//add links
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
		
		archStructure.addLink(createLink("USEntityBus", "FrenchAirDefenseMissileArtist"));
		archStructure.addLink(createLink("FrenchAirDefenseMissileArtist", "USScreenBus"));
		
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
		archStructure.addLink(createFredLink(null,"USFilterControlBus","USLocalFredBus"));
		
		//-------------------------------------------------------------------------------------
		//added for dynamic data exchange
		archTypes.addComponentType(
			createComponentType("UStoFrenchFilter_type", "US to French Filter Type", "c2demo.coalitiondemo.UStoFrenchFilter")
			);
	
		archTypes.addConnectorType(
			createFredConnectorType("USDisFredConnector_type", "Fred connector Type", "c2.conn.fred.FredConnector",
			new IInitializationParameter[]{
				createInitializationParameter("host", "127.0.0.1/-.-.-.-"),
				createInitializationParameter("groupName", "France"+" FRED")}
			));
		archStructure.addComponent(
			createComponent("UStoFrenchFilter", "US to French Filter Component", "UStoFrenchFilter_type")
			);
		archStructure.addConnector(
			createFredConnector("USDisFredBus", "Distributed Fred Connector", "USDisFredConnector_type")
			);
		archStructure.addLink(createLink("USRadarFilterBus", "UStoFrenchFilter"));
		archStructure.addLink(createFredLink("UStoFrenchFilter",null,"USDisFredBus"));		
		
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
		System.out.println (topId + bottomId + FredId);
		IPoint point1 = types.createPoint();
		IXMLLink anchor1 = instance.createXMLLink();
		
		if (bottomId != null){
			System.out.println ("the bottom id is :"+ bottomId);
			
			link.setId(FredId + "_to_" + bottomId);
			link.setDescription(createDescription(FredId + " to " + bottomId + " Link"));
			anchor1.setType("simple");
			anchor1.setHref("#" + bottomId + ".IFACE_TOP");
			point1.setAnchorOnInterface(anchor1);
		} else if (topId != null) {	
			System.out.println ("the top id is :"+ topId);
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
		String bottomName = "USLocalFredBus";
		String topName = "USDisFredBus";
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
	
	//US Command and Control
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
	
	INamedPropertyMessage oFlightPathN = createMessage("oFP-notif", "notification", "operation type flightpath notification", "1", "operation", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("type",OperationType.FLIGHTPATH.toString())});
	INamedPropertyMessage oFlightPathR = createMessage("oFP-req", "request", "operation type flightpath request", "1", "operation", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("type",OperationType.FLIGHTPATH.toString())});
	INamedPropertyMessage oPredImpactN = createMessage("oPI-notif", "notification", "operation type predImpact notification", "1", "operation", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("type",OperationType.PREDIMPACT.toString())});
	INamedPropertyMessage oImpactN = createMessage("oI-notif", "notification", "operation type Impact notification", "1", "operation", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("type",OperationType.IMPACT.toString())});
	
	INamedPropertyMessage roAfN = createMessage("roAf-notif", "notification", "remove operation Friend affiliation notification", "1", "rem-operation", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("affiliation",Affiliation.FRIEND.toString())});
	INamedPropertyMessage oAfN = createMessage("oAf-notif", "notification", " operation Friend affiliation notification", "1", "operation", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("affiliation",Affiliation.FRIEND.toString())});
	
	
	//US Radar
	INamedPropertyMessage reCounN = createMessage("reCoun-notif", "notification", "remove entity country notification", "1", "rem-entity", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("country",Country.USA.toString())});
	
	//US to US Filter
	
	//Virtual Screen
	INamedPropertyMessage mR = createMessage("map-req", "notification", "map request", "1", "map", "c2.fw.NamedPropertyMessage",
		new INamedProperty[]{createNamedProperty("action","request")});
	
	IProductionRule rules[] = {
		
		//AIR Defense Missile Artist
		createRule("FranceADMAr1", "eAdmaN>eAdmaN", new INamedPropertyMessage[]{eAdmaN}, new INamedPropertyMessage[]{eAdmaN}, "MostRecent"),
		createRule("FranceADMAr2", "eAdmaN>eSymCharN", new INamedPropertyMessage[]{eAdmaN}, new INamedPropertyMessage[]{eSymCharN}, "MostRecent"),
		
		//AWACS Artist
		createRule("FranceAwacs1", "eAwN>eAwN", new INamedPropertyMessage[]{eAwN}, new INamedPropertyMessage[]{eAwN}, "MostRecent"),
		
		//Battleship Artist
		createRule("USBSAr1", "eBsN>eBsN", new INamedPropertyMessage[]{eBsN}, new INamedPropertyMessage[]{eBsN}, "MostRecent"),
		
		//Carrier Artist
		createRule("USCAr1", "eCarrierN>eCarrierN", new INamedPropertyMessage[]{eCarrierN}, new INamedPropertyMessage[]{eCarrierN}, "MostRecent"),
		
		//FixedWing Artist
		createRule("USFwAr1", "eFwN>eFwN", new INamedPropertyMessage[]{eFwN}, new INamedPropertyMessage[]{eFwN}, "MostRecent"),
		
		//FixedWingBomber Artist
		createRule("USFwbAr1", "eFwbN>eFwbN", new INamedPropertyMessage[]{eFwbN}, new INamedPropertyMessage[]{eFwbN}, "MostRecent"),
		
		//Friend Artist
		createRule("USFrAr0", "eFrAfN>eFrAfN", new INamedPropertyMessage[]{eFrAfN}, new INamedPropertyMessage[]{eFrAfN}, "MostRecent"),
		
		createRule("USFrAr1", "eDimAirN>eDimAirN", new INamedPropertyMessage[]{eDimAirN}, new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
		createRule("USFrAr2", "eDimGrndN>eDimGrndN", new INamedPropertyMessage[]{eDimGrndN}, new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
		createRule("USFrAr3", "eDimSeaN>eDimSeaN", new INamedPropertyMessage[]{eDimSeaN}, new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
		createRule("USFrAr4", "eDimSubSurN>eDimSubSurN", new INamedPropertyMessage[]{eDimSubSurN}, new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
		
		createRule("USFrAr5", "oGenericN>oGenericN", new INamedPropertyMessage[]{oGenericN}, new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
		
		//Hostile Artist
		createRule("USHosAr0", "eHosAfN>eHosAfN", new INamedPropertyMessage[]{eHosAfN}, new INamedPropertyMessage[]{eHosAfN}, "MostRecent"),
		
		createRule("USHosAr1", "eDimAirN>eDimAirN", new INamedPropertyMessage[]{eDimAirN}, new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
		createRule("USHosAr2", "eDimGrndN>eDimGrndN", new INamedPropertyMessage[]{eDimGrndN}, new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
		createRule("USHosAr3", "eDimSeaN>eDimSeaN", new INamedPropertyMessage[]{eDimSeaN}, new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
		createRule("USHosAr4", "eDimSubSurN>eDimSubSurN", new INamedPropertyMessage[]{eDimSubSurN}, new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
		
		createRule("USHosAr5", "oGenericN>oGenericN", new INamedPropertyMessage[]{oGenericN}, new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
		
		//Land Retrieval
		createRule("USLR1", "mR>mLrN", new INamedPropertyMessage[]{mR}, new INamedPropertyMessage[]{mLrN}, "MostRecent"),
		
		//Neutral Artist
		createRule("USNeuAr0", "eNeuAfN>eNeuAfN", new INamedPropertyMessage[]{eNeuAfN}, new INamedPropertyMessage[]{eNeuAfN}, "MostRecent"),
		
		createRule("USNeuAr1", "eDimAirN>eDimAirN", new INamedPropertyMessage[]{eDimAirN}, new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
		createRule("USNeuAr2", "eDimGrndN>eDimGrndN", new INamedPropertyMessage[]{eDimGrndN}, new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
		createRule("USNeuAr3", "eDimSeaN>eDimSeaN", new INamedPropertyMessage[]{eDimSeaN}, new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
		createRule("USNeuAr4", "eDimSubSurN>eDimSubSurN", new INamedPropertyMessage[]{eDimSubSurN}, new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
		
		createRule("USNeuAr5", "oGenericN>oGenericN", new INamedPropertyMessage[]{oGenericN}, new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
		
		//Operations Artist
		createRule("USOpAr1", "oGenericN>oGenericN", new INamedPropertyMessage[]{oGenericN}, new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
		createRule("USOpAr2", "roGenericN>roGenericN", new INamedPropertyMessage[]{roGenericN}, new INamedPropertyMessage[]{roGenericN}, "MostRecent"),
		createRule("USOpAr3", "reGenericN>reGenericN", new INamedPropertyMessage[]{reGenericN}, new INamedPropertyMessage[]{reGenericN}, "MostRecent"),
		
		//TroupsArtist
		createRule("USTrAr0", "eTrN>eTrN", new INamedPropertyMessage[]{eTrN}, new INamedPropertyMessage[]{eTrN}, "MostRecent"),
		
		//Unknown Artist
		createRule("USUnAr0", "eUnAfN>eUnAfN", new INamedPropertyMessage[]{eUnAfN}, new INamedPropertyMessage[]{eUnAfN}, "MostRecent"),
		
		createRule("USUnAr1", "eDimAirN>eDimAirN", new INamedPropertyMessage[]{eDimAirN}, new INamedPropertyMessage[]{eDimAirN}, "MostRecent"),
		createRule("USUnAr2", "eDimGrndN>eDimGrndN", new INamedPropertyMessage[]{eDimGrndN}, new INamedPropertyMessage[]{eDimGrndN}, "MostRecent"),
		createRule("USUnAr3", "eDimSeaN>eDimSeaN", new INamedPropertyMessage[]{eDimSeaN}, new INamedPropertyMessage[]{eDimSeaN}, "MostRecent"),
		createRule("USUnAr4", "eDimSubSurN>eDimSubSurN", new INamedPropertyMessage[]{eDimSubSurN}, new INamedPropertyMessage[]{eDimSubSurN}, "MostRecent"),
		
		createRule("USUnAr5", "oGenericN>oGenericN", new INamedPropertyMessage[]{oGenericN}, new INamedPropertyMessage[]{oGenericN}, "MostRecent"),
		
		//US Command and control
		createRule("USCC1","eGenericN>eGenericN",new INamedPropertyMessage[]{eGenericN},new INamedPropertyMessage[]{eGenericN},"MostRecent"),
		createRule("USCC2","eCarrierN>oFlightPathN,oFlightPathR,oPredImpactN",new INamedPropertyMessage[]{eCarrierN},new INamedPropertyMessage[]{oFlightPathN,oFlightPathR,oPredImpactN},"MostRecent"),
		
		createRule("USCC3","eCounFraN>eFrAfN",new INamedPropertyMessage[]{eCounFraN},new INamedPropertyMessage[]{eFrAfN},"MostRecent"),
		createRule("USCC4","eCounMexN>eNeuAfN",new INamedPropertyMessage[]{eCounMexN},new INamedPropertyMessage[]{eNeuAfN},"MostRecent"),
		createRule("USCC5","eCounLebN>eHosAfN",new INamedPropertyMessage[]{eCounLebN},new INamedPropertyMessage[]{eHosAfN},"MostRecent"),
		createRule("USCC6","eCounUSN>eFrAfN",new INamedPropertyMessage[]{eCounUSN},new INamedPropertyMessage[]{eFrAfN},"MostRecent"),
		createRule("USCC7","eCounUnN>eUnAfN",new INamedPropertyMessage[]{eCounUnN},new INamedPropertyMessage[]{eUnAfN},"MostRecent"),
		
		createRule("USCC8","eFwN>roAfN,oAfN",new INamedPropertyMessage[]{eFwN},new INamedPropertyMessage[]{roAfN,oAfN},"MostRecent"),
		
		createRule("USCC9","eFwbN>eDimAirN",new INamedPropertyMessage[]{eFwbN},new INamedPropertyMessage[]{eDimAirN},"MostRecent"),
		createRule("USCC10","eBsN>eDimSeaN",new INamedPropertyMessage[]{eBsN},new INamedPropertyMessage[]{eDimSeaN},"MostRecent"),
		createRule("USCC11","eTrN>eDimGrndN",new INamedPropertyMessage[]{eTrN},new INamedPropertyMessage[]{eDimGrndN},"MostRecent"),
		createRule("USCC12","eAdmaN>oFlightPathN,oFlightPathR",new INamedPropertyMessage[]{eAdmaN},new INamedPropertyMessage[]{oFlightPathN,oFlightPathR},"MostRecent"),
		createRule("USCC13","eAwN>eDimAirN",new INamedPropertyMessage[]{eAwN},new INamedPropertyMessage[]{eDimAirN},"MostRecent"),
		
		//US Radar
		createRule("USR1","cClkN>eFwN,reCounN",new INamedPropertyMessage[]{cClkN},new INamedPropertyMessage[]{eFwN,reCounN},"MostRecent"),
		createRule("USR2","cClkN>eCarrierN",new INamedPropertyMessage[]{cClkN},new INamedPropertyMessage[]{eCarrierN},"MostRecent"),
		
		//US to US Filter
		createRule("USUSFil1","eGenericN>eGenericN",new INamedPropertyMessage[]{eGenericN},new INamedPropertyMessage[]{eGenericN},"MostRecent"),
		createRule("USUSFil2","reGenericN>reGenericN",new INamedPropertyMessage[]{reGenericN},new INamedPropertyMessage[]{reGenericN},"MostRecent")
			
		//Virtual Screen
			
	};
	
	return rules;
}

public static INamedProperty npCopy(INamedProperty npRef) {
	String name = (String)((IPropertyName)npRef.getName()).getValue();
	String value = (String)((IPropertyValue)npRef.getValue()).getValue();
	return createNamedProperty(name, value);        
}

}
