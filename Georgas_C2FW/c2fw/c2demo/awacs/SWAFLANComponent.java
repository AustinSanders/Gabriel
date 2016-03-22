package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class SWAFLANComponent extends SimpleAWACSBrick{

	public SWAFLANComponent(IComponent iComp){
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
					workloadAB1AB2(am.getLength(), 0.00);
					sendRawAWACSMessage("SWBFLAN" + getFTNumber(), am);
				}
				if((wscNum >= 14) && (wscNum <= 16)){
					workloadAB1AB2(am.getLength(), 0.00);
					sendRawAWACSMessage("DSWSC" + wscNum, am);
				}
			}
			else if(destinationPlatform.equals("RDMX1")){
				workloadAB1AB2(am.getLength(),0.000);
				sendRawAWACSMessage("DSRDMX1", am);
			}
			else if(destinationPlatform.equals("RDMX2")){
				workloadAB1AB2(am.getLength(),0.000);
				sendRawAWACSMessage("DSRDMX2", am);
			}
			else if(destinationPlatform.equals("AMC1")){
				workloadAB1AB2(am.getLength(),0.000);
				sendRawAWACSMessage("SWBFLAN" + getFTNumber(), am);
			}
			else if(destinationPlatform.equals("AMC2")){
				workloadAB1AB2(am.getLength(),0.000);
				sendRawAWACSMessage("DSAMC2", am);
			}
			else if(destinationPlatform.equals("STC1")){
				workloadAB1AB2(am.getLength(),0.000);
				sendRawAWACSMessage("DSSTC1", am);
			}
			else if(destinationPlatform.equals("STC2")){
				workloadAB1AB2(am.getLength(),0.000);
				sendRawAWACSMessage("DSSTC2", am);
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

