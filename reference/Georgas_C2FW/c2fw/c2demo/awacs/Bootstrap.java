package c2demo.awacs;

import java.io.*;
import java.util.*;
import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.instance.*;
import edu.uci.isr.xarch.types.*;

public class Bootstrap{

	public static void printArgs(){
		System.err.println("Arg Error.  Usage is:");
		System.err.println("java c2demo.awacs.Bootstrap [-file xADLfile] [-cassius] [-visio]");
		System.exit(1);
	}
	
	public static void main(String[] args){
		
		String filename = "awacs.xml";
		boolean doCassius = false;
		boolean doVisio = false;
		
		for(int i = 0; i < args.length; i++){
			if(args[i].toLowerCase().equals("-file")){
				if((i + 1) == (args.length)){
					printArgs();
				}
				i++;
				filename = args[i];
			}
			else if(args[i].toLowerCase().equals("-cassius")){
				doCassius = true;
			}
			else if(args[i].toLowerCase().equals("-visio")){
				doVisio = true;
			}
			else{
				printArgs();
			}
		}
		
		IXArchImplementation xArchImplementation = XArchUtils.getDefaultXArchImplementation();
		File f = new File(filename);
		if(!f.exists()){
			System.err.println("AWACS architecture file not found.");
			return;
		}
		if(!f.canRead()){
			System.err.println("Can't read AWACS architecture file.");
			return;
		}
		IXArch xArch = null;
		try{
			FileReader fr = new FileReader(f);
			xArch = xArchImplementation.parse(fr);
		}
		catch(IOException e){
			e.printStackTrace();
			return;
		}
		catch(Exception e2){
			e2.printStackTrace();
			return;
		}

		OneQueuePerInterfaceMessageHandler handler = new OneQueuePerInterfaceMessageHandler();
		//ThreadPoolArchitectureEngine engine = new ThreadPoolArchitectureEngine(1);
		//OneThreadPerBrickArchitectureEngine engine = new OneThreadPerBrickArchitectureEngine();
		OneThreadSteppableArchitectureEngine engine = new OneThreadSteppableArchitectureEngine();
		SimpleArchitectureManager manager = new SimpleArchitectureManager();
		ArchitectureController controller = ArchitectureControllerFactory.createController(
			manager, handler, engine, new Class[]{SteppableArchitectureEngine.class});
		
		TypesContext typesContext = new TypesContext(xArch);
		IArchStructure archStructure = typesContext.getArchStructure(xArch);
		
		Hashtable interfaceMap = new Hashtable();
		
		Collection compColl = archStructure.getAllComponents();
		for(Iterator it = compColl.iterator(); it.hasNext(); ){
			IComponent iComp = (IComponent)it.next();
			
			AbstractAWACSBrick sab;
			//if(iComp.getId().startsWith("TMRRDMX")){
			if(iComp.getId().equals("TMRRDMX1")){
				//System.out.println("Creating TMRRDMX1");
				sab = new TMRRDMXComponent(iComp);
			}
			else if(iComp.getId().equals("MAINRDMX1")){
				//System.out.println("Creating MAINRDMX1");
				sab = new MAINRDMXComponent(iComp);
			}
			else if(iComp.getId().equals("AGRDMX1")){
				sab = new AGRDMXComponent(iComp);
			}
			else if(iComp.getId().equals("AMCPRDMX1")){
				sab = new AMCPRDMXComponent(iComp);
			}
			else if(iComp.getId().equals("SHRDMX1")){
				sab = new SHRDMXComponent(iComp);
			}
			else if(iComp.getId().equals("AOCPCAU1")){
				sab = new AOCPCAUComponent(iComp);
			}
			else if(iComp.getId().equals("MTT1RDMX1")){
				sab = new MTT1RDMXComponent(iComp);
			}
			else if(iComp.getId().equals("MTT2RDMX1")){
				sab = new MTT2RDMXComponent(iComp);
			}
			else if(iComp.getId().equals("MTT3RDMX1")){
				sab = new MTT3RDMXComponent(iComp);
			}
			else if(iComp.getId().equals("RDMX1SDS1")){
				sab = new SwitchSDSComponent(iComp, "RDMX1", "SDS1");
			}
			else if(iComp.getId().equals("RDMX2SDS1")){
				sab = new SwitchSDSComponent(iComp, "RDMX2", "SDS1");
			}
			else if(iComp.getId().equals("STC1SDS1")){
				sab = new SwitchSDSComponent(iComp, "STC1", "SDS1");
			}
			else if(iComp.getId().equals("STC2SDS1")){
				sab = new SwitchSDSComponent(iComp, "STC2", "SDS1");
			}
			else if(iComp.getId().equals("CTL1SDS1")){
				sab = new CTLSDSComponent(iComp, "RMA1", "SDS1");
			}
			else if(iComp.getId().equals("CTL2SDS1")){
				sab = new CTLSDSComponent(iComp, "RMA2", "SDS1");
			}
			else if(iComp.getId().equals("CTL3SDS1")){
				sab = new CTLSDSComponent(iComp, "RMA3", "SDS1");
			}
			else if(iComp.getId().equals("CTL4SDS1")){
				sab = new CTLSDSComponent(iComp, "RMA4", "SDS1");
			}
			else if(iComp.getId().equals("RDMX1SDS2")){
				sab = new SwitchSDSComponent(iComp, "RDMX1", "SDS2");
			}
			else if(iComp.getId().equals("RDMX2SDS2")){
				sab = new SwitchSDSComponent(iComp, "RDMX2", "SDS2");
			}
			else if(iComp.getId().equals("STC1SDS2")){
				sab = new SwitchSDSComponent(iComp, "STC1", "SDS2");
			}
			else if(iComp.getId().equals("STC2SDS2")){
				sab = new SwitchSDSComponent(iComp, "STC2", "SDS2");
			}
			else if(iComp.getId().equals("CTL1SDS2")){
				sab = new CTLSDSComponent(iComp, "RMA1", "SDS2");
			}
			else if(iComp.getId().equals("CTL2SDS2")){
				sab = new CTLSDSComponent(iComp, "RMA2", "SDS2");
			}
			else if(iComp.getId().equals("CTL3SDS2")){
				sab = new CTLSDSComponent(iComp, "RMA3", "SDS2");
			}
			else if(iComp.getId().equals("CTL4SDS2")){
				sab = new CTLSDSComponent(iComp, "RMA4", "SDS2");
			}
			else if(iComp.getId().equals("SCSIRDMX1")){
				sab = new SCSIRDMXComponent(iComp);
			}
			else if(iComp.getId().equals("RSSRDMX1")){
				sab = new RSSRDMXComponent(iComp);
			}
			else if(iComp.getId().equals("SSRDMX1")){
				sab = new SSRDMXComponent(iComp);
			}
			else if(iComp.getId().equals("SMRDMX1")){
				sab = new SMRDMXComponent(iComp);
			}
			else if(iComp.getId().equals("DSRDMX1")){
				sab = new DSRDMXComponent(iComp);
			}
			else if(iComp.getId().startsWith("SWAFLAN")){
				sab = new SWAFLANComponent(iComp);
			}
			else if(iComp.getId().startsWith("SWBFLAN")){
				sab = new SWBFLANComponent(iComp);
			}
			else if(iComp.getId().equals("CKPTSTC1")){
				sab = new CKPTSTCComponent(iComp);
			}
			else if(iComp.getId().startsWith("DSAMC")){
				sab = new DSAMCComponent(iComp);
			}
			else if(iComp.getId().startsWith("SMAMC")){
				sab = new SMWSCComponent(iComp);	//use workstation SM
			}
			else if(iComp.getId().startsWith("DSSTC")){
				sab = new DSSTCComponent(iComp);
			}
			else if(iComp.getId().startsWith("SMWSC")){
				sab = new SMWSCComponent(iComp);
			}
			else if(iComp.getId().startsWith("SMSTC")){
				sab = new SMWSCComponent(iComp);	//use workstation SM
			}
			else if(iComp.getId().startsWith("DSWSC")){
				sab = new DSWSCComponent(iComp);	//use workstation SM
			}
			else{
				sab = new SimpleAWACSBrick(iComp);
			}
				
			
			Interface[] ifaces = sab.getAllInterfaces();
			for(int i = 0; i < ifaces.length; i++){
				interfaceMap.put(ifaces[i].getIdentifier(), ifaces[i]);
			}
			
			((LocalArchitectureManager)controller).addBrick(sab);
		}
		
		Collection connColl = archStructure.getAllConnectors();
		for(Iterator it = connColl.iterator(); it.hasNext(); ){
			IConnector iConn = (IConnector)it.next();
			AWACSConnector sab = new AWACSConnector(iConn);
			
			Interface[] ifaces = sab.getAllInterfaces();
			for(int i = 0; i < ifaces.length; i++){
				interfaceMap.put(ifaces[i].getIdentifier(), ifaces[i]);
			}
			
			((LocalArchitectureManager)controller).addBrick(sab);
		}
		
		Collection linkColl = archStructure.getAllLinks();
		for(Iterator it = linkColl.iterator(); it.hasNext(); ){
			ILink iLink = (ILink)it.next();

			Collection pointColl = iLink.getAllPoints();
			Iterator it2 = pointColl.iterator();

			IPoint iPoint1 = (IPoint)it2.next();
			IPoint iPoint2 = (IPoint)it2.next();
			IXMLLink anchor1 = iPoint1.getAnchorOnInterface();
			IXMLLink anchor2 = iPoint2.getAnchorOnInterface();
			String href1 = anchor1.getHref();
			String href2 = anchor2.getHref();
			
			String id1 = href1.trim().substring(1);
			String id2 = href2.trim().substring(1);
			
			Interface iface1 = (Interface)interfaceMap.get(new SimpleIdentifier(id1));
			Interface iface2 = (Interface)interfaceMap.get(new SimpleIdentifier(id2));
			
			BrickInterfaceIdPair pair1 = new BrickInterfaceIdPair(iface1.getBrick().getIdentifier(), iface1.getIdentifier());
			BrickInterfaceIdPair pair2 = new BrickInterfaceIdPair(iface2.getBrick().getIdentifier(), iface2.getIdentifier());
			
			SimpleWeld weld = new SimpleWeld(pair1, pair2);
			((LocalArchitectureManager)controller).addWeld(weld);
		}	
		
		if(doCassius){
			CassiusEventGenerator ceg = new CassiusEventGenerator(controller);
		}
		if(doVisio){
			VisioMessageAnimator vma = new VisioMessageAnimator(controller);
		}
		
		try{
			System.err.println("Starting the engine.");
			controller.startEngine();
			System.err.println("Waiting for engine to start.");
			controller.waitEngineState(ArchitectureEngine.ENGINESTATE_STARTED);
			System.err.println("Engine started.");
	
			System.err.println("Starting all bricks.");
			controller.startAll();
			System.err.println("Waiting for all bricks to start.");
			controller.waitStateAll(ArchitectureEngine.STATE_OPEN_RUNNING);
			System.err.println("Bricks started.");
		}
		catch(InterruptedException e){
		}
		
		Brick[] bricks = ((LocalArchitectureManager)controller).getAllBricks();
		for(int i = 0; i < bricks.length; i++){
			controller.begin(bricks[i].getIdentifier());
		}
		
		new c2.util.GUISteppableEngineManager((SteppableArchitectureEngine)controller);
		
		/*
		for(int i = 0; i < 23; i++){
			System.out.println("Stepping");
			((SteppableArchitectureEngine)controller).step();
		}
		*/
		
		//System.exit(0);

	}
			
			

}

