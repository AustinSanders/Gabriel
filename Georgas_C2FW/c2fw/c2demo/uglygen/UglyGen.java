package c2demo.uglygen;

import edu.uci.ics.xarchutils.*;
import edu.uci.ics.xadlutils.*;

public class UglyGen{
	static int counter = 100;
	
	protected XArchFlatInterface xarch;
	protected ObjRef xArchRef;
	protected ObjRef typesContextRef;
	protected ObjRef archTypesRef;
	
	public static void main(String[] args){
		UglyGen ug = new UglyGen();
		System.out.println(ug.generateUglyDocument());
	}
	
	public String generateUglyDocument(){
		xarch = new XArchFlatImpl();
		xArchRef = xarch.createXArch("urn:ugly");
		typesContextRef = xarch.createContext(xArchRef, "types");
		archTypesRef = xarch.createElement(typesContextRef, "archTypes");
		xarch.add(xArchRef, "object", archTypesRef);
		
		ObjRef topArchStructureRef = generateUglyStructure(3, 3);
		XadlUtils.setDescription(xarch, topArchStructureRef, "Top Structure");
		xarch.add(xArchRef, "object", topArchStructureRef);
		
		String stringRep = xarch.serialize(xArchRef);
		return stringRep;
	}
	
	public ObjRef generateUglyStructure(int breadth, int levelsLeft){
		if(levelsLeft == 0){
			return null;
		}
		
		ObjRef archStructureRef = xarch.createElement(typesContextRef, "archStructure");
		String archStructureID = generateUID();
		xarch.set(archStructureRef, "id", archStructureID);
		XadlUtils.setDescription(xarch, archStructureRef, "ArchStructure [" + archStructureID + "]");
		
		ObjRef[] componentRefs = new ObjRef[breadth];
		for(int i = 0; i < breadth; i++){
			//Create a component type
			ObjRef componentTypeRef = xarch.create(typesContextRef, "componentType");
			String componentTypeID = generateUID();
			xarch.set(componentTypeRef, "id", componentTypeID);
			XadlUtils.setDescription(xarch, componentTypeRef, "Component Type " + i);
			
			//Create in sig
			ObjRef inSignatureRef = xarch.create(typesContextRef, "signature");
			String inSignatureID = generateUID();
			xarch.set(inSignatureRef, "id", inSignatureID);
			XadlUtils.setDescription(xarch, inSignatureRef, "In Signature");
			XadlUtils.setDirection(xarch, inSignatureRef, "in");
			xarch.add(componentTypeRef, "signature", inSignatureRef);
			
			//Create out sig
			ObjRef outSignatureRef = xarch.create(typesContextRef, "signature");
			String outSignatureID = generateUID();
			xarch.set(outSignatureRef, "id", outSignatureID);
			XadlUtils.setDescription(xarch, outSignatureRef, "Out Signature");
			XadlUtils.setDirection(xarch, outSignatureRef, "out");
			xarch.add(componentTypeRef, "signature", outSignatureRef);
			
			//Okay, now we have a component type with an in and an out.
			//Let's try to recursively create + hookup some substructure.
			ObjRef innerArchStructureRef = generateUglyStructure(breadth, levelsLeft - 1);
			if(innerArchStructureRef != null){
				//Add generated archStructure to document
				xarch.add(xArchRef, "object", innerArchStructureRef);
				
				String innerArchStructureID = XadlUtils.getID(xarch, innerArchStructureRef);
				
				//Map inner component pipeline to signatures on outer type
				ObjRef[] innerComponentRefs = xarch.getAll(innerArchStructureRef, "component");
				
				ObjRef leftComp = innerComponentRefs[0];
				ObjRef leftCompInInterface = getInterface(leftComp, "in");
				String leftCompInInterfaceID = XadlUtils.getID(xarch, leftCompInInterface);

				ObjRef leftSIMRef = xarch.create(typesContextRef, "signatureInterfaceMapping");
				XadlUtils.setXLink(xarch, leftSIMRef, "outerSignature", inSignatureID);
				XadlUtils.setXLink(xarch, leftSIMRef, "innerInterface", leftCompInInterfaceID);
				
				ObjRef rightComp = innerComponentRefs[innerComponentRefs.length - 1];
				ObjRef rightCompOutInterface = getInterface(rightComp, "out");
				String rightCompOutInterfaceID = XadlUtils.getID(xarch, rightCompOutInterface);
				
				ObjRef rightSIMRef = xarch.create(typesContextRef, "signatureInterfaceMapping");
				XadlUtils.setXLink(xarch, rightSIMRef, "outerSignature", outSignatureID);
				XadlUtils.setXLink(xarch, rightSIMRef, "innerInterface", rightCompOutInterfaceID);
				
				//Hookup substructure
				ObjRef subArchitectureRef = xarch.create(typesContextRef, "subArchitecture");
				XadlUtils.setXLink(xarch, subArchitectureRef, "archStructure", innerArchStructureID);
				xarch.add(subArchitectureRef, "signatureInterfaceMapping", leftSIMRef);
				xarch.add(subArchitectureRef, "signatureInterfaceMapping", rightSIMRef);
				
				//Add subarchitecture to component type.
				xarch.set(componentTypeRef, "subArchitecture", subArchitectureRef);
			}
			
			//Add component type to document
			xarch.add(archTypesRef, "componentType", componentTypeRef);
			
			//Now we've created the component with a couple signatures,
			//as well as some substructure (if we're not at the bottom of the stack).
			//Let's create the component instance.
			
			ObjRef componentRef = xarch.create(typesContextRef, "component");
			componentRefs[i] = componentRef;
			String componentID = generateUID();
			xarch.set(componentRef, "id", componentID);
			XadlUtils.setDescription(xarch, componentRef, "Component " + i);
			
			//Create in iface
			ObjRef inInterfaceRef = xarch.create(typesContextRef, "interface");
			String inInterfaceID = generateUID();
			xarch.set(inInterfaceRef, "id", inInterfaceID);
			XadlUtils.setDescription(xarch, inInterfaceRef, "In Interface");
			XadlUtils.setDirection(xarch, inInterfaceRef, "in");
			xarch.add(componentRef, "interface", inInterfaceRef);
			XadlUtils.setXLink(xarch, inInterfaceRef, "signature", inSignatureID);
			
			//Create out iface
			ObjRef outInterfaceRef = xarch.create(typesContextRef, "interface");
			String outInterfaceID = generateUID();
			xarch.set(outInterfaceRef, "id", outInterfaceID);
			XadlUtils.setDescription(xarch, outInterfaceRef, "Out Interface");
			XadlUtils.setDirection(xarch, outInterfaceRef, "out");
			xarch.add(componentRef, "interface", outInterfaceRef);
			XadlUtils.setXLink(xarch, outInterfaceRef, "signature", outSignatureID);
			
			//Hookup component to type
			XadlUtils.setXLink(xarch, componentRef, "type", componentTypeID);

			//Add component to structure
			xarch.add(archStructureRef, "component", componentRef);
			
			//Create a link to the last component if there is one
			if(i > 0){
				ObjRef outComponentRef = componentRefs[i - 1];
				ObjRef outComponentInterfaceRef = getInterface(outComponentRef, "out");
				String outComponentInterfaceID = XadlUtils.getID(xarch, outComponentInterfaceRef);
				ObjRef inComponentRef = componentRefs[i];
				ObjRef inComponentInterfaceRef = getInterface(inComponentRef, "in");
				String inComponentInterfaceID = XadlUtils.getID(xarch, inComponentInterfaceRef);
				
				ObjRef linkRef = xarch.create(typesContextRef, "link");
				String linkID = generateUID();
				xarch.set(linkRef, "id", linkID);
				XadlUtils.setDescription(xarch, linkRef, "Link [" + linkID + "]");
				
				ObjRef point1Ref = xarch.create(typesContextRef, "point");
				XadlUtils.setXLink(xarch, point1Ref, "anchorOnInterface", outComponentInterfaceID);
				xarch.add(linkRef, "point", point1Ref);
				
				ObjRef point2Ref = xarch.create(typesContextRef, "point");
				XadlUtils.setXLink(xarch, point2Ref, "anchorOnInterface", inComponentInterfaceID);
				xarch.add(linkRef, "point", point2Ref);
				
				xarch.add(archStructureRef, "link", linkRef);
			}
		}
		
		return archStructureRef;
	}
	
	public ObjRef getInterface(ObjRef componentRef, String directionToMatch){
		ObjRef[] interfaceRefs = xarch.getAll(componentRef, "interface");
		for(int i = 0; i < interfaceRefs.length; i++){
			String direction = XadlUtils.getDirection(xarch, interfaceRefs[i]);
			if(direction.equals(directionToMatch)){
				return interfaceRefs[i];
			}
		}
		return null;
	}
	
	public String generateUID(){
		return "id" + counter++;
	}
	

}
