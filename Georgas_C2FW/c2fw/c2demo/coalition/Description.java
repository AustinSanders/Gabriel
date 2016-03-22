package c2demo.coalition;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.instance.*;
import edu.uci.isr.xarch.types.*;
import edu.uci.isr.xarch.implementation.*;
import edu.uci.isr.xarch.variants.*;
import edu.uci.isr.xarch.javaimplementation.*;
import edu.uci.isr.xarch.javainitparams.*;
import edu.uci.isr.xarch.lookupimplementation.*;

import java.util.Arrays;

public class Description{
	private static IInstanceContext instance;
	private static ITypesContext types;
	private static IImplementationContext implementation;
	private static IVariantsContext variants;
	private static IJavaimplementationContext javaimplementation;
	private static IJavainitparamsContext javainitparams;
	private static ILookupimplementationContext lookupimplementation;
	
	public static void main(String[] args){
		System.out.println(getXml());
	}
	
	public static String getXml(){
		IXArchImplementation xArchImplementation = XArchUtils.getDefaultXArchImplementation();
		IXArch xArch = xArchImplementation.createXArch();

		instance = (IInstanceContext)xArchImplementation.createContext(xArch, "instance");
		types = (ITypesContext)xArchImplementation.createContext(xArch, "types");
		implementation = (IImplementationContext)xArchImplementation.createContext(xArch, "implementation");
		variants = (IVariantsContext)xArchImplementation.createContext(xArch, "variants");
		javaimplementation = (IJavaimplementationContext)xArchImplementation.createContext(xArch, "javaimplementation");
		lookupimplementation = (ILookupimplementationContext)xArchImplementation.createContext(xArch, "lookupimplementation");
		javainitparams = (IJavainitparamsContext)xArchImplementation.createContext(xArch, "javainitparams");
		
		IArchStructure archStructure = types.createArchStructureElement();
		archStructure.setId("CoalitionForces");
		archStructure.setDescription(createDescription("Coalition Forces Architecture"));
		
		IArchTypes archTypes = types.createArchTypesElement();
		
		xArch.addObject(archStructure);
		xArch.addObject(archTypes);
		
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
		ILookupImplementation lookupImplementation = lookupimplementation.createLookupImplementation();
		ILookupName lookupName = lookupimplementation.createLookupName();
		lookupName.setValue("IFACE_TOP");
		lookupImplementation.setName(lookupName);
		c2TopTypeImpl.addImplementation(lookupImplementation);
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
		ILookupImplementation lookupImplementation2 = lookupimplementation.createLookupImplementation();
		ILookupName lookupName2 = lookupimplementation.createLookupName();
		lookupName2.setValue("IFACE_BOTTOM");
		lookupImplementation2.setName(lookupName2);
		c2BottomTypeImpl.addImplementation(lookupImplementation2);
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
			createComponentType("Clock_type", "Clock Event Generator Type", "c2demo.coalition.ClockEventGenerator", null)
			);
		archTypes.addComponentType(
			createComponentType("Satellite_type", "Satellite Type", "c2demo.coalition.Satellite", null)
			);
		archTypes.addComponentType(
			createComponentType("Radar_type", "Radar Type", "c2demo.coalition.Radar", null)
			);
		archTypes.addComponentType(
			createComponentType("UStoUSFilter_type", "US to US Filter Type", "c2demo.coalition.UStoUSFilter",null)
			);
		archTypes.addComponentType(
			createComponentType("USCC_type", "US C and C Type", "c2demo.coalition.CommandAndControl", null)
			);
		archTypes.addComponentType(
			createComponentType("UnknownArtist_type", "Unknown Artist Type", "c2demo.coalition.UnknownArtist", null)
			);
		archTypes.addComponentType(
			createComponentType("NeutralArtist_type", "Neutral Artist Type", "c2demo.coalition.NeutralArtist", null)
			);
		archTypes.addComponentType(
			createComponentType("FriendArtist_type", "Friend Artist Type", "c2demo.coalition.FriendArtist", null)
			);
		archTypes.addComponentType(
			createComponentType("HostileArtist_type", "Hostile Artist Type", "c2demo.coalition.HostileArtist", null)
			);
		archTypes.addComponentType(
			createComponentType("FixedWingArtist_type", "Fixed Wing Artist Type", "c2demo.coalition.FixedWingArtist", null)
			);
		archTypes.addComponentType(
			createComponentType("BattleshipArtist_type", "Battleship Artist Type", "c2demo.coalition.BattleshipArtist", null)
			);
		archTypes.addComponentType(
			createComponentType("TroupsArtist_type", "Troups Artist Type", "c2demo.coalition.TroupsArtist", null)
			);
		archTypes.addComponentType(
			createComponentType("CarrierArtist_type", "Carrier Artist Type", "c2demo.coalition.CarrierArtist", null)
			);
		archTypes.addComponentType(
			createComponentType("LandRetrieval_type", "Land Retrieval Type", "c2demo.coalition.LandRetrieval", null)
			);
		
		archTypes.addComponentType(
			createComponentType("VirtualScreen_type", "Virtual Screen Type", "c2demo.coalition.VirtualScreen", 
			new IInitializationParameter[]{
				createInitializationParameter("Country", "US")}
			));
		
		//add components
		archStructure.addComponent(
			createComponent("Clock", "Clock Event Generator Component", "Clock_type")
			);
		archStructure.addComponent(
			createComponent("Satellite", "Satellite Component", "Satellite_type")
			);
		archStructure.addComponent(
			createComponent("Radar", "Radar Component", "Radar_type")
			);
		archStructure.addComponent(
			createComponent("UStoUSFilter", "UStoUS Filter Component", "UStoUSFilter_type")
			);
		archStructure.addComponent(
			createComponent("USCC", "USCC Component", "USCC_type")
			);
		archStructure.addComponent(
			createComponent("UnknownArtist", "Unknown Artist Component ", "UnknownArtist_type")
			);
		
		archStructure.addComponent(
			createComponent("NeutralArtist", "Neutral Artist Component", "NeutralArtist_type")
			);
		archStructure.addComponent(
			createComponent("FriendArtist", "Friend Artist Component", "FriendArtist_type")
			);
		archStructure.addComponent(
			createComponent("HostileArtist", "Hostile Artist Component", "HostileArtist_type")
			);
		
		archStructure.addComponent(
			createComponent("FixedWingArtist", "FixedWing Artist Component", "FixedWingArtist_type")
			);
		archStructure.addComponent(
			createComponent("BattleshipArtist", "Battleship Artist Component", "BattleshipArtist_type")
			);
		archStructure.addComponent(
			createComponent("TroupsArtist", "Troups Artist Component", "TroupsArtist_type")
			);
		archStructure.addComponent(
			createComponent("CarrierArtist", "Carrier Artist Type", "CarrierArtist_type")
			);
		archStructure.addComponent(
			createComponent("LandRetrieval", "Land Retrieval Component", "LandRetrieval_type")
			);
		archStructure.addComponent(
			createComponent("VirtualScreen", "Virtual Screen Component", "VirtualScreen_type")
			);
		
		//add connector type
		archTypes.addConnectorType(
			createConnectorType("BusConnector_type", "Bus Connector Type", "c2.legacy.conn.BusConnector")
			);
		
		//add Connectors
		archStructure.addConnector(
			createConnector("ClockBus", "Clock Connector", "BusConnector_type")
			);
		archStructure.addConnector(
			createConnector("ConnBus", "Connection Connector", "BusConnector_type")
			);
		archStructure.addConnector(
			createConnector("InformationBus", "Information Connector", "BusConnector_type")
			);
		archStructure.addConnector(
			createConnector("DisplayBus", "Display Connector", "BusConnector_type")
			);
		archStructure.addConnector(
			createConnector("EntityBus", "Entity Connector", "BusConnector_type")
			);
		archStructure.addConnector(
			createConnector("ArtistBus", "Artist Connector", "BusConnector_type")
			);
		archStructure.addConnector(
			createConnector("ScreenBus", "Screen Connector", "BusConnector_type")
			);
		archStructure.addConnector(
			createConnector("LandBus", "Land Connector", "BusConnector_type")
			);
		
		//add links
		archStructure.addLink(createLink("Clock", "ClockBus"));
		
		archStructure.addLink(createLink("ClockBus", "Satellite"));
		archStructure.addLink(createLink("Satellite", "ConnBus"));
		
		archStructure.addLink(createLink("ClockBus", "Radar"));
		archStructure.addLink(createLink("Radar", "ConnBus"));
		
		archStructure.addLink(createLink("ConnBus", "UStoUSFilter"));
		archStructure.addLink(createLink("UStoUSFilter", "InformationBus"));
		
		archStructure.addLink(createLink("InformationBus", "USCC"));
		archStructure.addLink(createLink("USCC", "DisplayBus"));
		
		archStructure.addLink(createLink("DisplayBus", "UnknownArtist"));
		archStructure.addLink(createLink("UnknownArtist", "ArtistBus"));
		
		archStructure.addLink(createLink("DisplayBus", "NeutralArtist"));
		archStructure.addLink(createLink("NeutralArtist", "ArtistBus"));
		
		archStructure.addLink(createLink("DisplayBus", "FriendArtist"));
		archStructure.addLink(createLink("FriendArtist", "ArtistBus"));
		
		archStructure.addLink(createLink("DisplayBus", "HostileArtist"));
		archStructure.addLink(createLink("HostileArtist", "ArtistBus"));
		
		archStructure.addLink(createLink("ArtistBus", "FixedWingArtist"));
		archStructure.addLink(createLink("FixedWingArtist", "EntityBus"));
		
		archStructure.addLink(createLink("ArtistBus", "BattleshipArtist"));
		archStructure.addLink(createLink("BattleshipArtist", "EntityBus"));
		
		archStructure.addLink(createLink("ArtistBus", "TroupsArtist"));
		archStructure.addLink(createLink("TroupsArtist", "EntityBus"));
		
		archStructure.addLink(createLink("ArtistBus", "CarrierArtist"));
		archStructure.addLink(createLink("CarrierArtist", "EntityBus"));
		
		archStructure.addLink(createLink("EntityBus", "ScreenBus"));
		
		archStructure.addLink(createLink("LandRetrieval", "ScreenBus"));
		
		archStructure.addLink(createLink("ScreenBus", "VirtualScreen"));
		
		
		//Bus-to-Bus Links
		//no bus-to-bus links in this architecture
		try{
			return xArchImplementation.serialize(xArch, null);
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
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
		IDescription dSig = types.createDescription();
		dSig.setValue(id + "_topSig");
		topSig.setDescription(dSig);
		IDirection topDirection = types.createDirection();
		topDirection.setValue("inout");
		topSig.setDirection(topDirection);
		
		IXMLLink topSigType = types.createXMLLink();
		topSigType.setType("simple");
		topSigType.setHref("#C2TopType");
		
		topSig.setType(topSigType);
		
		ISignature botSig = types.createSignature();
		dSig = types.createDescription();
		dSig.setValue(id + "_bottomSig");
		botSig.setDescription(dSig);
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
		IConnectorType ct0 = types.createConnectorType();
		IVariantConnectorType ct1 = variants.promoteToVariantConnectorType(ct0);
		IVariantConnectorTypeImpl ct = implementation.promoteToVariantConnectorTypeImpl(ct1);
		
		ct.setId(id);
		IDescription desc = types.createDescription();
		desc.setValue(description);
		ct.setDescription(desc);
		
		ISignature topSig = types.createSignature();
		IDescription dSig = types.createDescription();
		dSig.setValue(id + "_topSig");
		topSig.setDescription(dSig);
		IDirection topDirection = types.createDirection();
		topDirection.setValue("inout");
		topSig.setDirection(topDirection);
		
		IXMLLink topSigType = types.createXMLLink();
		topSigType.setType("simple");
		topSigType.setHref("#C2TopType");
		
		topSig.setType(topSigType);
		
		ISignature botSig = types.createSignature();
		dSig = types.createDescription();
		dSig.setValue(id + "_bottomSig");
		botSig.setDescription(dSig);
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
		IJavaClassFile javaClassFile = javaimplementation.createJavaClassFile();
		IJavaClassName jcn = javaimplementation.createJavaClassName();
		jcn.setValue(javaClassName);
		javaClassFile.setJavaClassName(jcn);
		javaImplementation.setMainClass(javaClassFile);
		
		ct.addImplementation(javaImplementation);
		
		return ct;
	}
}

