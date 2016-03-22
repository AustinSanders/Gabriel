package c2demo.bulk;

import edu.uci.ics.xarchutils.*;

public class TestBulkQueryEngine{

	public static void main(String[] args){
		XArchFlatInterface xarch = new XArchFlatImpl();
		try{
			ObjRef xArchRef = xarch.parseFromFile("archstudio.xml");
			ObjRef typesContextRef = xarch.createContext(xArchRef, "types");
			XArchBulkQuery bq = new XArchBulkQuery(xArchRef);
			bq.addQueryPath("archStructure*/component*/description/value");
			bq.addQueryPath("archStructure*/connector*/description/value");
			
			System.out.println(bq);
			
			XArchBulkQueryResults qr = ((XArchFlatImpl)xarch).bulkQuery(bq);
			System.out.println(qr);
			
			XArchFlatQueryInterface xarchBulk = new XArchBulkQueryResultProxy(xarch, qr);
			ObjRef[] archStructureRefs = xarchBulk.getAllElements(typesContextRef, "archStructure", xArchRef);
			//System.out.println(xarchBulk.toString());
			ObjRef[] componentRefs = xarchBulk.getAll(archStructureRefs[0], "component");
			//System.out.println(componentRefs.length);
			//if(true) return;
			for(int i = 0; i < componentRefs.length; i++){
				System.out.println(componentRefs[i]);
				//System.out.println("componentRefs[i] type = " + xarch.getType(componentRefs[i]));
				ObjRef descriptionRef = (ObjRef)xarchBulk.get(componentRefs[i], "description");
				System.out.println(descriptionRef);
				String description = (String)xarchBulk.get(descriptionRef, "value");
				System.out.println(description);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
	}

}
