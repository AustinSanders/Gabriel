package c2demo.nad;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.instance.*;
import edu.uci.isr.xarch.types.*;
import edu.uci.isr.xarch.implementation.*;
import edu.uci.isr.xarch.variants.*;
import edu.uci.isr.xarch.javaimplementation.*;
import edu.uci.isr.xarch.lookupimplementation.*;
import edu.uci.isr.xarch.javainitparams.*;

public class Description{

	private static IInstanceContext instance;
	private static ITypesContext types;
	private static IImplementationContext implementation;
	private static IVariantsContext variants;
	private static IJavaimplementationContext javaimplementation;
	private static IJavainitparamsContext javainitparams;
	private static ILookupimplementationContext lookupimplementation;
	
	private static boolean canTrackStealth = false;
	private static boolean canControlRadar = false;
	private static boolean secondDisplay = false;
	private static boolean includeReplacements = false;
	private static boolean includeMonitors = false;
	
	public static void main(String[] args){
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-stealth")){
				canTrackStealth = true;
			}
			if(args[i].equals("-radarcontrol")){
				canControlRadar = true;
			}
			if(args[i].equals("-2displays")){
				secondDisplay = true;
			}
			if(args[i].equals("-replacements")){
				includeReplacements = true;
			}
			if(args[i].equals("-monitors")){
				includeMonitors = true;
			}
		}
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
		archStructure.setId("NAD");
		archStructure.setDescription(createDescription("Notional AWACS Demo"));

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

		archTypes.addComponentType(
			createComponentType("RealWorldComponent_type", "RealWorld Component Type", "c2demo.nad.RealWorldC2Component")
		);
		
		archTypes.addComponentType(
			createComponentType("RadarComponent_type", "Radar Component Type", "c2demo.nad.RadarC2Component")
		);
		archTypes.addComponentType(
			createComponentType("BetterRadarComponent_type", "Better Radar Component Type", "c2demo.nad.RadarC2Component",
			new String[]{"canTrackStealth"}, new String[]{"true"})
		);
		
		archTypes.addComponentType(
			createComponentType("IFFComponent_type", "IFF Component Type", "c2demo.nad.IFFC2Component")
		);
		
		archTypes.addComponentType(
			createComponentType("AnnotationComponent_type", "Annotation Component Type", "c2demo.nad.AnnotationC2Component")
		);
		
		if(canControlRadar){
			archTypes.addComponentType(
				createComponentType("RadarControlComponent_type", "Radar Control Component Type", "c2demo.nad.RadarControllerComponent")
			);
		}
		archTypes.addComponentType(
			createComponentType("TrackFusionComponent_type", "Track Fusion Component Type", "c2demo.nad.TrackFusionC2Component")
		);
		
		archTypes.addComponentType(
			createComponentType("TrackArtistComponent_type", "Track Artist Component Type", "c2demo.nad.TrackArtistC2Component")
		);
		archTypes.addComponentType(
			createComponentType("MetadataArtistComponent_type", "Metadata Artist Component Type", "c2demo.nad.MetadataArtistC2Component")
		);
		
		archTypes.addComponentType(
			createComponentType("WorldComponent_type", "World Component Type", "c2demo.nad.WorldC2Component")
		);
		archTypes.addComponentType(
			createComponentType("ViewTransformer_type", "View Transformer Type", "c2demo.nad.ViewTransformerC2Component")
		);
		archTypes.addComponentType(
			createComponentType("RadarDisplay_type", "Radar Display Type", "c2demo.nad.RadarDisplayC2Component")
		);

		if(includeMonitors){
			archTypes.addComponentType(
				createComponentType("StatusMonitorComponent_type", "Status Monitor Component Type", "c2demo.nad.StatusMonitorC2Component")
			);
		}
		
		archTypes.addConnectorType(
			createConnectorType("BusConnector_type", "Bus Connector Type", "c2.legacy.conn.BusConnector")
		);
		
		archStructure.addComponent(
			createComponent("RealWorldComponent", "RealWorldComponent", "RealWorldComponent_type")
		);
		
		if(!canTrackStealth){
			archStructure.addComponent(
				createComponent("RadarComponent", "RadarComponent", "RadarComponent_type")
			);
		}
		else{
			archStructure.addComponent(
				createComponent("BetterRadarComponent", "BetterRadarComponent", "BetterRadarComponent_type")
			);
		}

		archStructure.addComponent(
			createComponent("IFFComponent", "IFFComponent", "IFFComponent_type")
		);
		
		if(includeReplacements){
			archStructure.addComponent(
				createComponent("ReplacementRadarComponent", "BetterRadarComponent", "BetterRadarComponent_type")
			);
			archStructure.addComponent(
				createComponent("ReplacementIFFComponent", "IFFComponent", "IFFComponent_type")
			);
		}
		
		archStructure.addComponent(
			createComponent("AnnotationComponent", "AnnotationComponent", "AnnotationComponent_type")
		);
		
		if(canControlRadar){
			archStructure.addComponent(
				createComponent("RadarControlComponent", "RadarControlComponent", "RadarControlComponent_type")
			);
		}
		archStructure.addComponent(
			createComponent("TrackFusionComponent", "TrackFusionComponent", "TrackFusionComponent_type")
		);
		
		archStructure.addComponent(
			createComponent("TrackArtistComponent", "TrackArtistComponent", "TrackArtistComponent_type")
		);
		archStructure.addComponent(
			createComponent("MetadataArtistComponent", "MetadataArtistComponent", "MetadataArtistComponent_type")
		);
		
		archStructure.addComponent(
			createComponent("WorldComponent", "WorldComponent", "WorldComponent_type")
		);
		archStructure.addComponent(
			createComponent("ViewTransformer1", "View Transformer 1", "ViewTransformer_type")
		);
		archStructure.addComponent(
			createComponent("RadarDisplay1", "Radar Display 1", "RadarDisplay_type")
		);
		
		if(secondDisplay){
			archStructure.addComponent(
				createComponent("ViewTransformer2", "View Transformer 2", "ViewTransformer_type")
			);
			archStructure.addComponent(
				createComponent("RadarDisplay2", "Radar Display 2", "RadarDisplay_type")
			);
		}

		if(includeMonitors){
			archStructure.addComponent(
				createComponent("StatusMonitorComponent", "Status Monitor Component", "StatusMonitorComponent_type")
			);
		}
		
		archStructure.addConnector(
			createConnector("RealWorldBus", "Real World Bus", "BusConnector_type")
		);
		
		archStructure.addConnector(
			createConnector("SensorBus", "Sensor Bus", "BusConnector_type")
		);
		
		if(includeReplacements){
			archStructure.addConnector(
				createConnector("BackupSensorBus", "Backup Sensor Bus", "BusConnector_type")
			);
		}
		
		archStructure.addConnector(
			createConnector("FusionBus", "Fusion Bus", "BusConnector_type")
		);
		archStructure.addConnector(
			createConnector("ArtistBus", "Artist Bus", "BusConnector_type")
		);
		archStructure.addConnector(
			createConnector("WorldBus", "WorldBus", "BusConnector_type")
		);
		archStructure.addConnector(
			createConnector("WS1Bus", "WS1Bus", "BusConnector_type")
		);
		if(secondDisplay){
			archStructure.addConnector(
				createConnector("WS2Bus", "WS2Bus", "BusConnector_type")
			);
		}

		if(includeMonitors){
			archStructure.addConnector(
				createConnector("StatusMonitorBus", "StatusMonitorBus", "BusConnector_type")
			);
		}
		
		archStructure.addLink(createLink("RealWorldComponent", "RealWorldBus"));
		
		archStructure.addLink(createLink("RealWorldBus", "IFFComponent"));
		archStructure.addLink(createLink("RealWorldBus", "AnnotationComponent"));
		
		if(!canTrackStealth){
			archStructure.addLink(createLink("RealWorldBus", "RadarComponent"));
			archStructure.addLink(createLink("RadarComponent", "SensorBus"));
		}
		else{
			archStructure.addLink(createLink("RealWorldBus", "BetterRadarComponent"));
			archStructure.addLink(createLink("BetterRadarComponent", "SensorBus"));
		}
		archStructure.addLink(createLink("IFFComponent", "SensorBus"));
		
		if(includeReplacements){
			archStructure.addLink(createLink("RealWorldBus", "ReplacementRadarComponent"));
			archStructure.addLink(createLink("ReplacementRadarComponent", "BackupSensorBus"));

			archStructure.addLink(createLink("RealWorldBus", "ReplacementIFFComponent"));
			archStructure.addLink(createLink("ReplacementIFFComponent", "BackupSensorBus"));

			archStructure.addLink(createLink("BackupSensorBus", "SensorBus"));
		}
		
		archStructure.addLink(createLink("AnnotationComponent", "SensorBus"));
		archStructure.addLink(createLink("SensorBus", "TrackFusionComponent"));
		if(canControlRadar){
			archStructure.addLink(createLink("SensorBus", "RadarControlComponent"));
		}
		archStructure.addLink(createLink("TrackFusionComponent", "FusionBus"));
		archStructure.addLink(createLink("FusionBus", "TrackArtistComponent"));
		archStructure.addLink(createLink("TrackArtistComponent", "ArtistBus"));
		archStructure.addLink(createLink("FusionBus", "MetadataArtistComponent"));
		archStructure.addLink(createLink("MetadataArtistComponent", "ArtistBus"));
		archStructure.addLink(createLink("ArtistBus", "WorldComponent"));
		
		archStructure.addLink(createLink("WorldComponent", "WorldBus"));
		archStructure.addLink(createLink("WorldBus", "ViewTransformer1"));
		archStructure.addLink(createLink("ViewTransformer1", "WS1Bus"));
		archStructure.addLink(createLink("WS1Bus", "RadarDisplay1"));

		if(secondDisplay){
			archStructure.addLink(createLink("WorldBus", "ViewTransformer2"));
			archStructure.addLink(createLink("ViewTransformer2", "WS2Bus"));
			archStructure.addLink(createLink("WS2Bus", "RadarDisplay2"));
		}
		
		if(includeMonitors){
			archStructure.addLink(createLink("SensorBus", "StatusMonitorBus"));
			archStructure.addLink(createLink("FusionBus", "StatusMonitorBus"));
			archStructure.addLink(createLink("StatusMonitorBus", "StatusMonitorComponent"));
		}
		
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
		IJavaClassFile javaClassFile = javaimplementation.createJavaClassFile();
		IJavaClassName jcn = javaimplementation.createJavaClassName();
		jcn.setValue(javaClassName);
		javaClassFile.setJavaClassName(jcn);
		javaImplementation.setMainClass(javaClassFile);
		
		ct.addImplementation(javaImplementation);
		
		return ct;
	}

	private static IComponentType createComponentType(String id, String description, String javaClassName,
		String[] paramNames, String[] paramValues){
		
		IVariantComponentTypeImpl ict = (IVariantComponentTypeImpl)createComponentType(id, description, javaClassName);
		IJavaImplementation ji = null;
		for(java.util.Iterator it = ict.getAllImplementations().iterator(); it.hasNext(); ){
			Object o = it.next();
			if(o instanceof IJavaImplementation){
				ji = (IJavaImplementation)o;
				break;
			}
		}
		IJavaClassFile mainClass = ji.getMainClass();
		IJavaClassFileParams mainClassParams = javainitparams.promoteToJavaClassFileParams(mainClass);
		for(int i = 0; i < paramNames.length; i++){
			IInitializationParameter ip = javainitparams.createInitializationParameter();
			ip.setName(paramNames[i]);
			ip.setValue(paramValues[i]);
			mainClassParams.addInitializationParameter(ip);
		}
		return ict;
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
		IJavaClassFile javaClassFile = javaimplementation.createJavaClassFile();
		IJavaClassName jcn = javaimplementation.createJavaClassName();
		jcn.setValue(javaClassName);
		javaClassFile.setJavaClassName(jcn);
		javaImplementation.setMainClass(javaClassFile);
		
		ct.addImplementation(javaImplementation);
		
		return ct;
	}
}