package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class AGRDMXComponent extends SimpleAWACSBrick{

	public AGRDMXComponent(IComponent iComp){
		super(iComp);
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();
			
			if(payload.equals("MAINRDMX1 Release Hold 7")){
				sendAWACSMessage("MAINRDMX1", "Release Hold 7 Ack");
				workloadCPU(0.100d);
				sendAWACSMessage("AMCPRDMX1", "History Output");
			}
			else if(payload.equals("AMCPRDMX1 History Output Ack")){
				sendAWACSMessage("MAINRDMX1", "IO END 7");
				workloadCPU(0.100d);
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

