package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class MAINRDMXComponent extends SimpleAWACSBrick{

	public MAINRDMXComponent(IComponent iComp){
		super(iComp);
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();
			
			if(payload.equals("TMRRDMX1 Timer Event")){
				sendAWACSMessage("TMRRDMX1", "Timer Event Ack");
				workloadCPU(0.100d);
				sendAWACSMessage("AGRDMX1", "Release Hold 7");
			}
			else if(payload.equals("SHRDMX1 SIO 7")){
				sendAWACSMessage("SHRDMX1", "SIO 7 Ack");
				workloadCPU(0.100d);
			}
			else if(payload.equals("SHRDMX1 SIO 8")){
				sendAWACSMessage("SHRDMX1", "SIO 8 Ack");
				workloadCPU(0.100d);
				sendAWACSMessage("MTT1RDMX1", "SIO 8");
			}
			else if(payload.equals("SHRDMX1 SIO 9")){
				sendAWACSMessage("SHRDMX1", "SIO 9 Ack");
				workloadCPU(0.100d);
				sendAWACSMessage("MTT2RDMX1", "SIO 9");
			}
			else if(payload.equals("SHRDMX1 SIO 9")){
				sendAWACSMessage("SHRDMX1", "SIO 9 Ack");
				workloadCPU(0.100d);
				sendAWACSMessage("MTT2RDMX1", "SIO 9");
			}
			else if(payload.equals("AGRDMX1 IO END 7")){
				sendAWACSMessage("AGRDMX1", "IO END 7 Ack");
				workloadCPU(0.100d);
				sendAWACSMessage("AOCPCAU1", "IO END 7");
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

