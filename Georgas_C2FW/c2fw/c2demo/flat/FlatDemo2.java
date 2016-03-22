package c2demo.flat;

import edu.uci.ics.xarchutils.*;

public class FlatDemo2{
	
	public static void main(String[] args){
		XArchFlatImpl flat = new XArchFlatImpl();
		ObjRef xArch = flat.createXArch("urn:local-arch");
		ObjRef instanceContext = flat.createContext(xArch, "instance");
		ObjRef archInstances = flat.createElement(instanceContext, "archInstance");
		flat.add(xArch, "object", archInstances);
		
		ObjRef componentInstance1 = flat.create(instanceContext, "componentInstance");
		flat.set(componentInstance1, "id", "comp1");
		flat.add(archInstances, "componentInstance", componentInstance1);
		ObjRef description1 = flat.create(instanceContext, "description");
		flat.set(description1, "value", "Component!");
		flat.set(componentInstance1, "description", description1);
		
		String value = (String)flat.get(description1, "value");
		System.out.println("Desc = " + value);
		
		ObjRef clonedXArch = flat.cloneXArch(xArch, "urn:cloned-arch");
		
		System.out.println(flat.serialize(xArch));
		System.out.println(flat.serialize(clonedXArch));
	}


}

