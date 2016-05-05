/*
 * Created on Jul 4, 2005
 *
 */
package c2.fw.secure;

import java.io.File;

import javax.swing.UIManager;

import archstudio.Description;
import archstudio.VersionInfo;
import archstudio.comp.aem.AEMC2Component;
import archstudio.comp.aem.SecureAEMC2Component;
import archstudio.comp.bootstrapper.BootstrapperC2Component;
import archstudio.comp.shutdown.ShutdownC2Component;
import archstudio.comp.xarchtrans.XArchTransactionsC2Component;
import c2.fw.ArchitectureController;
import c2.fw.ArchitectureControllerFactory;
import c2.fw.ArchitectureEngine;
import c2.fw.Brick;
import c2.fw.BrickInterfaceIdPair;
import c2.fw.InitializationParameter;
import c2.fw.LocalArchitectureManager;
import c2.fw.OneQueuePerInterfaceMessageHandler;
import c2.fw.OneThreadPerBrickArchitectureEngine;
import c2.fw.SimpleArchitectureManager;
import c2.fw.SimpleIdentifier;
import c2.fw.SimpleWeld;
import c2.legacy.AbstractC2Brick;
import c2.legacy.conn.BusConnector;

public class SecureBootstrap{

	public static final String PRODUCT_NAME = "ArchStudio 3 Bootstrap Loader";
	public static final String PRODUCT_VERSION = "build " + VersionInfo.getVersion("[unofficial build]");
	
	public static final int ENGINE_REGULAR = 90;
	public static final int ENGINE_STEPPABLE = 100;
	private static int engineType;
	
	public static void main(String[] args){

		String xml = null;
		boolean generate = false;
		engineType = ENGINE_REGULAR;
		
		try{
			for(int i = 0; i < args.length; i++){
				if(args[i].startsWith("-")){
					if(args[i].equals("-generate")){
						generate = true;
					}
					else if(args[i].equals("-steppable")){
						engineType = ENGINE_STEPPABLE;
					}
					else if(args[i].equals("-systemlaf")){
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}
					else if(args[i].equals("-configurator")){
						archstudio.Configurator.main(args);
						return;
					}
					else{
						printHeader();
						printArgs();
						System.exit(5);
					}
				}
				else{
					if(xml != null){
						printHeader();
						printArgs();
						return;
					}
					else{
						xml = args[i];
					}
				}
			}
		}
		catch(Exception e){
			printHeader();
			printArgs();
			return;
		}
		
		if(generate){
			Description.main(new String[]{});
			return;
		}
		else{
			if(xml == null){
				printHeader();
				printArgs();
				System.exit(5);
			}
			printHeader();
			startSystem(xml);
		}
	}

	public static void printHeader(){
		System.out.println();
		System.out.println("---------------------------------------------------------------");
		System.out.println(PRODUCT_NAME + " (" + PRODUCT_VERSION + ")");
		System.out.println("(C)2001-2004 The Regents of the University of California.");
		System.out.println("All Rights Reserved Worldwide.");
		System.out.println();
	}		
	
	public static void printArgs(){
		System.err.println("Argument error.");
		System.err.println("ArchStudio 3 Bootstrap options:");
		System.err.println(" * Start a given architecture description:");
		System.err.println("   java archstudio.Bootstrap <file>");
		System.err.println();
		System.err.println(" * Generate the ArchStudio 3 architecture description to stdout:");
		System.err.println("   java archstudio.Bootstrap -generate");
		System.err.println();
		System.err.println(" * Start the ArchStudio 3 startup script generator:");
		System.err.println("   java archstudio.Bootstrap -configurator");
		System.err.println();
		System.err.println("Additional options:");
		System.err.println(" -steppable");
		System.err.println("    Start the architecture with a steppable engine.");
		System.err.println(" -systemlaf");
		System.err.println("    Start with the system's Look and Feel, rather than the Metal L&F.");
		
		System.err.println();
	}
	
	public static void startSystem(String fileNameOrURL){
		File f = new File(fileNameOrURL);
		InitializationParameter ip = null;
		if(f.exists()){
			ip = new InitializationParameter("startFile", fileNameOrURL);
		}
		else if(fileNameOrURL.startsWith("file:/") || fileNameOrURL.startsWith("http://")){
			ip = new InitializationParameter("startURL", fileNameOrURL);
		}
		else{
			printHeader();
			printArgs();
			System.exit(6);
		}
		
		InitializationParameter engineIp = null;
		
		SecureOneQueuePerInterfaceMessageHandler handler = new SecureOneQueuePerInterfaceMessageHandler();
		//SingleQueueMessageHandler handler = new SingleQueueMessageHandler();
		ArchitectureEngine engine = new OneThreadPerBrickArchitectureEngine();
		Class[] addlClasses = new Class[]{};
		if(engineType == ENGINE_REGULAR){
			engineIp = new InitializationParameter("engineType", "regular");
		}
		else if(engineType == ENGINE_STEPPABLE){
			engineIp = new InitializationParameter("engineType", "steppable");
		}
		
		//ThreadPoolArchitectureEngine engine = new ThreadPoolArchitectureEngine(10);
		// It seems unnecessary to have this SecureBootStrap, but if the situation rises, 
		// the difference than the normal archstuio.Bootstrap is
		// 1)The SecureOneQueuePerInterfaceMessageHandler above, 2) this 
		// SecureArchitectureManager, 3) the SecureAEMC2Component below
		SecureArchitectureManager manager = new SecureArchitectureManager();
		ArchitectureController controller = ArchitectureControllerFactory.createController(
			manager, handler, engine, addlClasses);

		//XArchADTC2Component archadt = new XArchADTC2Component(new SimpleIdentifier("XArchADT"));
		//BusConnector bus0 = new BusConnector(new SimpleIdentifier("Bus0"));
		XArchTransactionsC2Component archtrans = new XArchTransactionsC2Component(new SimpleIdentifier("BOOTSTRAP-XArchTrans"));
		BusConnector busT = new BusConnector(new SimpleIdentifier("BOOTSTRAP-BusT"));
		BusConnector busS = new BusConnector(new SimpleIdentifier("BOOTSTRAP-BusS"));
		SecureAEMC2Component aem = new SecureAEMC2Component(new SimpleIdentifier("BOOTSTRAP-AEM"));
		ShutdownC2Component sd = new ShutdownC2Component(new SimpleIdentifier("BOOTSTRAP-Shutdown"));
		BootstrapperC2Component bs = new BootstrapperC2Component(new SimpleIdentifier("BOOTSTRAP-Bootstrapper"), new InitializationParameter[]{ip, engineIp});
		
		//((LocalArchitectureManager)controller).addBrick(archadt);
		((LocalArchitectureManager)controller).addBrick(archtrans);
		((LocalArchitectureManager)controller).addBrick(aem);
		//((LocalArchitectureManager)controller).addBrick(bus0);
		((LocalArchitectureManager)controller).addBrick(busT);
		((LocalArchitectureManager)controller).addBrick(busS);
		((LocalArchitectureManager)controller).addBrick(sd);
		((LocalArchitectureManager)controller).addBrick(bs);

		//BrickInterfaceIdPair bus0Bottom = new BrickInterfaceIdPair(bus0.getIdentifier(), AbstractC2Brick.BOTTOM_INTERFACE_ID);
		//BrickInterfaceIdPair bus0Top = new BrickInterfaceIdPair(bus0.getIdentifier(), AbstractC2Brick.TOP_INTERFACE_ID);

		BrickInterfaceIdPair busTBottom = new BrickInterfaceIdPair(busT.getIdentifier(), AbstractC2Brick.BOTTOM_INTERFACE_ID);
		BrickInterfaceIdPair busTTop = new BrickInterfaceIdPair(busT.getIdentifier(), AbstractC2Brick.TOP_INTERFACE_ID);
		
		BrickInterfaceIdPair busSBottom = new BrickInterfaceIdPair(busS.getIdentifier(), AbstractC2Brick.BOTTOM_INTERFACE_ID);
		BrickInterfaceIdPair busSTop = new BrickInterfaceIdPair(busS.getIdentifier(), AbstractC2Brick.TOP_INTERFACE_ID);
		
		//BrickInterfaceIdPair archadtBottom = new BrickInterfaceIdPair(archadt.getIdentifier(), AbstractC2Brick.BOTTOM_INTERFACE_ID);
		//BrickInterfaceIdPair archadtTop = new BrickInterfaceIdPair(archadt.getIdentifier(), AbstractC2Brick.TOP_INTERFACE_ID);

		BrickInterfaceIdPair archtransBottom = new BrickInterfaceIdPair(archtrans.getIdentifier(), AbstractC2Brick.BOTTOM_INTERFACE_ID);
		BrickInterfaceIdPair archtransTop = new BrickInterfaceIdPair(archtrans.getIdentifier(), AbstractC2Brick.TOP_INTERFACE_ID);

		BrickInterfaceIdPair aemBottom = new BrickInterfaceIdPair(aem.getIdentifier(), AbstractC2Brick.BOTTOM_INTERFACE_ID);
		BrickInterfaceIdPair aemTop = new BrickInterfaceIdPair(aem.getIdentifier(), AbstractC2Brick.TOP_INTERFACE_ID);

		BrickInterfaceIdPair sdBottom = new BrickInterfaceIdPair(sd.getIdentifier(), AbstractC2Brick.BOTTOM_INTERFACE_ID);
		BrickInterfaceIdPair sdTop = new BrickInterfaceIdPair(sd.getIdentifier(), AbstractC2Brick.TOP_INTERFACE_ID);
		
		BrickInterfaceIdPair bsBottom = new BrickInterfaceIdPair(bs.getIdentifier(), AbstractC2Brick.BOTTOM_INTERFACE_ID);
		BrickInterfaceIdPair bsTop = new BrickInterfaceIdPair(bs.getIdentifier(), AbstractC2Brick.TOP_INTERFACE_ID);
		
		SimpleWeld[] welds = {
			//new SimpleWeld(archadtBottom, bus0Top),
			//new SimpleWeld(bus0Bottom, archtransTop),
			new SimpleWeld(archtransBottom, busTTop),
			new SimpleWeld(busTBottom, busSTop),
			new SimpleWeld(busTBottom, aemTop),
			new SimpleWeld(aemBottom, busSTop),
			new SimpleWeld(busSBottom, sdTop),
			new SimpleWeld(busSBottom, bsTop)
		};

		for(int i = 0; i < welds.length; i++){
			((LocalArchitectureManager)controller).addWeld(welds[i]);
		}
		
		try{
			controller.startEngine();
			controller.waitEngineState(ArchitectureEngine.ENGINESTATE_STARTED);
			controller.startAll();
			controller.waitStateAll(ArchitectureEngine.STATE_OPEN_RUNNING);
		}
		catch(InterruptedException e){
		}
		
		Brick[] bricks = ((LocalArchitectureManager)controller).getAllBricks();
		for(int i = 0; i < bricks.length; i++){
			controller.begin(bricks[i].getIdentifier());
		}
	}
}

