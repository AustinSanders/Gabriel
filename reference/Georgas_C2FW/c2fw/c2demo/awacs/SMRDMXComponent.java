package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class SMRDMXComponent extends SimpleAWACSBrick{

	public SMRDMXComponent(IComponent iComp){
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

	public void sendHeartbeat(String dest, String payload, String destinationPlatform,
		String destinationProcess, String route){
		
		AWACSMessage am = new AWACSMessage();
		am.setPayload(payload);
		am.setSourcePlatform("RDMX" + getFTNumber());
		am.setSourceProcess("SS");
		am.setRoute(route);
		am.setDestinationPlatform(destinationPlatform);
		am.setDestinationProcess(destinationProcess);
		am.setLength(208.0);
		
		sendRawAWACSMessage(dest, am);
	}
	
	public void tick(int timerCount){
		super.tick(timerCount);
		if(timerCount < 5){
			return;
		}
		int tc = ((timerCount - 5) % 42) + 1;
		if(tc == 1){
			sendHeartbeat("DSRDMX1", "STC1 Heartbeat", "STC1", "SM", "");
		}
		else if(tc == 2){
			sendHeartbeat("DSRDMX1", "STC2 Heartbeat", "STC2", "SM", "");
		}
		else if((tc >= 3) && (tc <= 18)){
			int wscNum = tc - 2;
			sendHeartbeat("DSRDMX1", "WSC" + wscNum + " Heartbeat", "WSC" + wscNum, "SM", "");
		}
		else if(tc == 19){
			sendHeartbeat("DSRDMX1", "AMC1 Heartbeat", "AMC1", "SM", "");
		}
		else if(tc == 20){
			sendHeartbeat("DSRDMX1", "AMC2 Heartbeat", "AMC2", "SM", "");
		}
		else if(tc == 21){
			sendHeartbeat("DSRDMX1", "RDMX2 Heartbeat", "RDMX2", "SM", "");
		}
		else if(tc == 22){
			sendHeartbeat("DSRDMX1", "STC1 Heartbeat", "STC1", "SM", "FLAN2");
		}
		else if(tc == 23){
			sendHeartbeat("DSRDMX1", "STC2 Heartbeat", "STC2", "SM", "FLAN2");
		}
		else if((tc >= 24) && (tc <= 39)){
			int wscNum = tc - 23;
			sendHeartbeat("DSRDMX1", "WSC" + wscNum + " Heartbeat", "WSC" + wscNum, "SM", "FLAN2");
		}
		else if(tc == 40){
			sendHeartbeat("DSRDMX1", "AMC1 Heartbeat", "AMC1", "SM", "FLAN2");
		}
		else if(tc == 41){
			sendHeartbeat("DSRDMX1", "AMC2 Heartbeat", "AMC2", "SM", "FLAN2");
		}
		else if(tc == 42){
			sendHeartbeat("DSRDMX1", "RDMX2 Heartbeat", "RDMX2", "SM", "FLAN2");
		}
	}

}

