package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class SHRDMXComponent extends SimpleAWACSBrick{

	public SHRDMXComponent(IComponent iComp){
		super(iComp);
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();
			
			if(payload.equals("AOCPCAU1 SIO 7")){
				sendAWACSMessage("AOCPCAU1", "SIO 7 Ack");
				workloadCPU(0.100d);
				sendAWACSMessage("MAINRDMX1", "SIO 7");
			}
			else if(payload.equals("AOCPCAU1 SIO 8")){
				sendAWACSMessage("AOCPCAU1", "SIO 8 Ack");
				workloadCPU(0.100d);
				sendAWACSMessage("MAINRDMX1", "SIO 8");
			}
			else if(payload.equals("AOCPCAU1 SIO 9")){
				sendAWACSMessage("AOCPCAU1", "SIO 9 Ack");
				workloadCPU(0.100d);
				sendAWACSMessage("MAINRDMX1", "SIO 9");
			}
			else if(payload.equals("AOCPCAU1 SIO A")){
				sendAWACSMessage("AOCPCAU1", "SIO A Ack");
				workloadCPU(0.100d);
				sendAWACSMessage("MAINRDMX1", "SIO A");
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

