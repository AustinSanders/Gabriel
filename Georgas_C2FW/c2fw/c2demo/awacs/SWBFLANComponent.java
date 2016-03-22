package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class SWBFLANComponent extends SimpleAWACSBrick{

	public SWBFLANComponent(IComponent iComp){
		super(iComp);
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();

			String destinationPlatform = am.getDestinationPlatform();
			if((destinationPlatform == null) || (destinationPlatform.equals(""))){
			}
			else if(destinationPlatform.startsWith("WSC")){
				String destPlatNumString = destinationPlatform.substring(3);
				int wscNum = Integer.parseInt(destPlatNumString);
				if((wscNum >= 1) && (wscNum <= 13)){
					workloadAB1AB2(0.00, am.getLength());
					sendRawAWACSMessage("DSWSC" + wscNum, am);
				}
				if((wscNum >= 14) && (wscNum <= 16)){
					workloadAB1AB2(0.00, am.getLength());
					sendRawAWACSMessage("SWAFLAN" + getFTNumber(), am);
				}
			}
			else if(destinationPlatform.equals("RDMX1")){
				workloadAB1AB2(0.00, am.getLength());
				sendRawAWACSMessage("SWAFLAN" + getFTNumber(), am);
			}
			else if(destinationPlatform.equals("RDMX2")){
				workloadAB1AB2(0.00, am.getLength());
				sendRawAWACSMessage("SWAFLAN" + getFTNumber(), am);
			}
			else if(destinationPlatform.equals("AMC1")){
				workloadAB1AB2(0.00, am.getLength());
				sendRawAWACSMessage("DSAMC1", am);
			}
			else if(destinationPlatform.equals("AMC2")){
				workloadAB1AB2(0.00, am.getLength());
				sendRawAWACSMessage("SWAFLAN"  + getFTNumber(), am);
			}
			else if(destinationPlatform.equals("STC1")){
				workloadAB1AB2(0.00, am.getLength());
				sendRawAWACSMessage("SWAFLAN"  + getFTNumber(), am);
			}
			else if(destinationPlatform.equals("STC2")){
				workloadAB1AB2(0.00, am.getLength());
				sendRawAWACSMessage("SWAFLAN"  + getFTNumber(), am);
			}
			else{
				System.out.println(getIdentifier() + " absorbed message: " + payload);
			}			
		}
	}
	
	public void tick(int timerCount){
		super.tick(timerCount);
	}

}

