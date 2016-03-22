package c2demo.klax;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.instance.*;
import edu.uci.isr.xarch.types.*;
import edu.uci.isr.xarch.implementation.*;
import edu.uci.isr.xarch.variants.*;
import edu.uci.isr.xarch.javaimplementation.*;
import edu.uci.isr.xarch.lookupimplementation.*;

public class Description{

	public static final int REGULAR_KLAX = 100;
	public static final int SPELLING_KLAX = 200;
	
	private static int klaxType = REGULAR_KLAX;
	
	private static IInstanceContext instance;
	private static ITypesContext types;
	private static IImplementationContext implementation;
	private static IVariantsContext variants;
	private static IJavaimplementationContext javaimplementation;
	private static ILookupimplementationContext lookupimplementation;
	
	public static void main(String[] args){
		if(args.length == 0){
			System.out.println(getXml());
		}
		else if(args[0].equals("REGULAR")){
			System.out.println(getXml());
		}
		else if(args[0].equals("SPELLING")){
			klaxType = SPELLING_KLAX;
			System.out.println(getXml());
		}
		else{
			System.out.println("Arg error.");
		}
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
		
		IArchStructure archStructure = types.createArchStructureElement();
		archStructure.setId("Klax");
		if(klaxType == REGULAR_KLAX){
			archStructure.setDescription(createDescription("Klax (Regular)"));
		}
		else if(klaxType == SPELLING_KLAX){
			archStructure.setDescription(createDescription("Klax (Spelling)"));
		}			
		
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
			createComponentType("ClockComponent_type", "Clock Component Type", "c2demo.klax.ClockComponent")
		);
		archTypes.addComponentType(
			createComponentType("StatusComponent_type", "Status Component Type", "c2demo.klax.StatusComponent")
		);
		archTypes.addComponentType(
			createComponentType("RelativePositionLogic_type", "Relative Position Logic Type", "c2demo.klax.RelativePositionLogic")
		);
		archTypes.addComponentType(
			createComponentType("WellComponent_type", "Well Component Type", "c2demo.klax.WellComponent")
		);
		archTypes.addComponentType(
			createComponentType("PaletteComponent_type", "Palette Component Type", "c2demo.klax.PaletteComponent")
		);
		archTypes.addComponentType(
			createComponentType("ChuteComponent_type", "Chute Component Type", "c2demo.klax.ChuteComponent")
		);
		
		if(klaxType == REGULAR_KLAX){
			archTypes.addComponentType(
				createComponentType("MatchingLogic_type", "Matching Logic Type", "c2demo.klax.MatchingLogic")
			);
		}
		else if(klaxType == SPELLING_KLAX){
			archTypes.addComponentType(
				createComponentType("SpellingMatchingLogic_type", "SpellingMatching Logic Type", "c2demo.klax.SpellingMatchingLogic")
			);
		}
		
		archTypes.addComponentType(
			createComponentType("StatusLogic_type", "Status Logic Type", "c2demo.klax.StatusLogic")
		);
		if(klaxType == REGULAR_KLAX){
			archTypes.addComponentType(
				createComponentType("NextTileLogic_type", "Next Tile Logic Type", "c2demo.klax.NextTileLogic")
			);
		}
		else if(klaxType == SPELLING_KLAX){
			archTypes.addComponentType(
				createComponentType("NextLetterTileLogic_type", "Next Letter Tile Logic Type", "c2demo.klax.NextLetterTileLogic")
			);
		}
		
		archTypes.addComponentType(
			createComponentType("WellArtist_type", "Well Artist Type", "c2demo.klax.WellArtist")
		);
		archTypes.addComponentType(
			createComponentType("PaletteArtist_type", "Palette Artist Type", "c2demo.klax.PaletteArtist")
		);
		if(klaxType == REGULAR_KLAX){
			archTypes.addComponentType(
				createComponentType("TileArtist_type", "Tile Artist Type", "c2demo.klax.TileArtist")
			);
		}
		else if(klaxType == SPELLING_KLAX){
			archTypes.addComponentType(
				createComponentType("LetterTileArtist_type", "Letter Tile Artist Type", "c2demo.klax.LetterTileArtist")
			);
		}
		
		archTypes.addComponentType(
			createComponentType("ChuteArtist_type", "Chute Artist Type", "c2demo.klax.ChuteArtist")
		);
		archTypes.addComponentType(
			createComponentType("StatusArtist_type", "Status Artist Type", "c2demo.klax.StatusArtist")
		);
		archTypes.addComponentType(
			createComponentType("LayoutManager_type", "Layout Manager Type", "c2demo.klax.LayoutManager")
		);
		archTypes.addComponentType(
			createComponentType("GraphicsBinding_type", "Graphics Binding Type", "c2demo.comp.graphics.GraphicsBinding")
		);
		archTypes.addConnectorType(
			createConnectorType("BusConnector_type", "Bus Connector Type", "c2.legacy.conn.BusConnector")
		);
		
		archStructure.addComponent(
			createComponent("ClockComponent", "ClockComponent", "ClockComponent_type")
		);
		archStructure.addComponent(
			createComponent("StatusComponent", "StatusComponent", "StatusComponent_type")
		);
		archStructure.addComponent(
			createComponent("RelativePositionLogic", "RelativePositionLogic", "RelativePositionLogic_type")
		);
		archStructure.addComponent(
			createComponent("WellComponent", "WellComponent", "WellComponent_type")
		);
		archStructure.addComponent(
			createComponent("PaletteComponent", "PaletteComponent", "PaletteComponent_type")
		);
		archStructure.addComponent(
			createComponent("ChuteComponent", "ChuteComponent", "ChuteComponent_type")
		);
		if(klaxType == REGULAR_KLAX){
			archStructure.addComponent(
				createComponent("MatchingLogic", "MatchingLogic", "MatchingLogic_type")
			);
		}
		else if(klaxType == SPELLING_KLAX){
			archStructure.addComponent(
				createComponent("SpellingMatchingLogic", "SpellingMatchingLogic", "SpellingMatchingLogic_type")
			);
		}			
		archStructure.addComponent(
			createComponent("StatusLogic", "StatusLogic", "StatusLogic_type")
		);
		if(klaxType == REGULAR_KLAX){
			archStructure.addComponent(
				createComponent("NextTileLogic", "NextTileLogic", "NextTileLogic_type")
			);
		}
		else if(klaxType == SPELLING_KLAX){
			archStructure.addComponent(
				createComponent("NextLetterTileLogic", "NextLetterTileLogic", "NextLetterTileLogic_type")
			);
		}			
		archStructure.addComponent(
			createComponent("WellArtist", "WellArtist", "WellArtist_type")
		);
		archStructure.addComponent(
			createComponent("PaletteArtist", "PaletteArtist", "PaletteArtist_type")
		);
		if(klaxType == REGULAR_KLAX){
			archStructure.addComponent(
				createComponent("TileArtist", "TileArtist", "TileArtist_type")
			);
		}
		else if(klaxType == SPELLING_KLAX){
			archStructure.addComponent(
				createComponent("LetterTileArtist", "LetterTileArtist", "LetterTileArtist_type")
			);
		}			
		archStructure.addComponent(
			createComponent("ChuteArtist", "ChuteArtist", "ChuteArtist_type")
		);
		archStructure.addComponent(
			createComponent("StatusArtist", "StatusArtist", "StatusArtist_type")
		);
		archStructure.addComponent(
			createComponent("LayoutManager", "LayoutManager", "LayoutManager_type")
		);
		archStructure.addComponent(
			createComponent("GraphicsBinding", "GraphicsBinding", "GraphicsBinding_type")
		);

		archStructure.addConnector(
			createConnector("Bus1", "Bus1", "BusConnector_type")
		);
		archStructure.addConnector(
			createConnector("Bus2", "Bus2", "BusConnector_type")
		);
		archStructure.addConnector(
			createConnector("Bus3", "Bus3", "BusConnector_type")
		);
		archStructure.addConnector(
			createConnector("Bus4", "Bus4", "BusConnector_type")
		);
		archStructure.addConnector(
			createConnector("Bus5", "Bus5", "BusConnector_type")
		);
		archStructure.addConnector(
			createConnector("Bus6", "Bus6", "BusConnector_type")
		);

		archStructure.addLink(createLink("ClockComponent", "Bus1"));
		archStructure.addLink(createLink("ChuteComponent", "Bus1"));
		archStructure.addLink(createLink("PaletteComponent", "Bus1"));
		archStructure.addLink(createLink("WellComponent", "Bus1"));
		archStructure.addLink(createLink("StatusComponent", "Bus1"));
		
		if(klaxType == REGULAR_KLAX){
			archStructure.addLink(createLink("Bus1", "NextTileLogic"));
		}
		else if(klaxType == SPELLING_KLAX){
			archStructure.addLink(createLink("Bus1", "NextLetterTileLogic"));
		}
		
		archStructure.addLink(createLink("Bus1", "RelativePositionLogic"));
		archStructure.addLink(createLink("Bus1", "Bus2"));
		archStructure.addLink(createLink("Bus1", "Bus3"));
		
		if(klaxType == REGULAR_KLAX){
			archStructure.addLink(createLink("Bus1", "MatchingLogic"));
			archStructure.addLink(createLink("MatchingLogic", "Bus2"));
		}
		else if(klaxType == SPELLING_KLAX){
			archStructure.addLink(createLink("Bus1", "SpellingMatchingLogic"));
			archStructure.addLink(createLink("SpellingMatchingLogic", "Bus2"));
		}
		
		archStructure.addLink(createLink("RelativePositionLogic", "Bus2"));
		archStructure.addLink(createLink("Bus2", "StatusLogic"));

		archStructure.addLink(createLink("Bus3", "WellArtist"));
		archStructure.addLink(createLink("Bus3", "ChuteArtist"));
		archStructure.addLink(createLink("Bus3", "PaletteArtist"));
		archStructure.addLink(createLink("Bus3", "StatusArtist"));

		archStructure.addLink(createLink("WellArtist", "Bus4"));
		archStructure.addLink(createLink("ChuteArtist", "Bus4"));
		archStructure.addLink(createLink("PaletteArtist", "Bus4"));
		archStructure.addLink(createLink("StatusArtist", "Bus4"));
		archStructure.addLink(createLink("Bus4", "Bus5"));

		if(klaxType == REGULAR_KLAX){
			archStructure.addLink(createLink("Bus4", "TileArtist"));
			archStructure.addLink(createLink("TileArtist", "Bus5"));
		}
		else if(klaxType == SPELLING_KLAX){
			archStructure.addLink(createLink("Bus4", "LetterTileArtist"));
			archStructure.addLink(createLink("LetterTileArtist", "Bus5"));
		}			

		archStructure.addLink(createLink("Bus5", "LayoutManager"));
		
		archStructure.addLink(createLink("LayoutManager", "Bus6"));
		archStructure.addLink(createLink("Bus6", "GraphicsBinding"));
		
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
	
		IXMLLink topIfaceSig = types.createXMLLink();
		topIfaceSig.setType("simple");
		topIfaceSig.setHref("#" + typeId + "_topSig");
		topIface.setSignature(topIfaceSig);

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
	
		IXMLLink bottomIfaceSig = types.createXMLLink();
		bottomIfaceSig.setType("simple");
		bottomIfaceSig.setHref("#" + typeId + "_bottomSig");
		bottomIface.setSignature(bottomIfaceSig);

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
	
		IXMLLink topIfaceSig = types.createXMLLink();
		topIfaceSig.setType("simple");
		topIfaceSig.setHref("#" + typeId + "_topSig");
		topIface.setSignature(topIfaceSig);

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
	
		IXMLLink bottomIfaceSig = types.createXMLLink();
		bottomIfaceSig.setType("simple");
		bottomIfaceSig.setHref("#" + typeId + "_bottomSig");
		bottomIface.setSignature(bottomIfaceSig);

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
		IDescription dSig = types.createDescription();
		dSig.setValue(id + "_topSig");
		topSig.setDescription(dSig);
		IDirection topDirection = types.createDirection();
		topDirection.setValue("inout");
		topSig.setId(id + "_topSig");
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
		botSig.setId(id + "_bottomSig");
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
		topSig.setId(id + "_topSig");
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
		botSig.setId(id + "_bottomSig");
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