package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class CKPTSTCComponent extends SimpleAWACSBrick{

	public CKPTSTCComponent(IComponent iComp){
		super(iComp);
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();
			System.out.println(getIdentifier() + " absorbed message: " + payload);
		}
	}
	
	public void sendStcpInitiatedCkpt(String dest, String destinationPlatform,
	String destinationProcess){
		
		double totalLength = 15000.0;
		double length = 1504.0;
		
		AWACSMessage am = new AWACSMessage();
		am.setPayload("Initiated CKPT");
		am.setSourcePlatform("STC" + getFTNumber());
		am.setSourceProcess("CKPT");
		am.setRoute("");
		am.setDestinationPlatform(destinationPlatform);
		am.setDestinationProcess(destinationProcess);
		am.setLength(1504.0);
		int blockCount = (int)((totalLength / am.getLength()) + .9999);
		for(int i = 0; i < blockCount; i++){
			AWACSMessage am2 = (AWACSMessage)am.duplicate();
			am2.setPayload(am2.getPayload() + " Block " + i);
			sendRawAWACSMessage(dest, am2);
		}
	}
	
	public void sendStcpFullCkpt(String dest, String destinationPlatform,
	String destinationProcess){
		
		//double totalLength = 600000.0;
		double totalLength = 60000.0;
		double length = 1504.0;
		
		AWACSMessage am = new AWACSMessage();
		am.setPayload("Full CKPT");
		am.setSourcePlatform("STC" + getFTNumber());
		am.setSourceProcess("CKPT");
		am.setRoute("");
		am.setDestinationPlatform(destinationPlatform);
		am.setDestinationProcess(destinationProcess);
		am.setLength(1504.0);
		int blockCount = (int)((totalLength / am.getLength()) + .9999);
		for(int i = 0; i < blockCount; i++){
			AWACSMessage am2 = (AWACSMessage)am.duplicate();
			am2.setPayload(am2.getPayload() + " Block " + i);
			sendRawAWACSMessage(dest, am2);
		}
	}
	
	public void tick(int timerCount){
		super.tick(timerCount);
		if(timerCount < 5){
			return;
		}
		int tc = ((timerCount - 5) % 10000);
		
		if(tc == 0){
			sendStcpFullCkpt("DSSTC1", "RDMX1", "RSS");
		}
		else if((tc >= 1000) && ((tc % 1000) == 0)){
			sendStcpInitiatedCkpt("DSSTC1", "RDMX1", "RSS");
		}
	}

}

