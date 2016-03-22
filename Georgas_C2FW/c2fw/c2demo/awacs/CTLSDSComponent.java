package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class CTLSDSComponent extends SimpleAWACSBrick{

	private String rma;
	private String sds;
	
	
	public CTLSDSComponent(IComponent iComp, String rma, String sds){
		super(iComp);
		this.rma = rma;
		this.sds = sds;
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();
			
			if(payload.equals("RDMX1" + sds + " WRITE " + rma + " " + sds)){
				workloadCPUDisk(0.0, 1.0);
				sendAWACSMessage("RDMX1SDS1", "WRITE " + rma + " " + sds + " Ack");
			}
			else if(payload.equals("RDMX2" + sds + " WRITE " + rma + " " + sds)){
				workloadCPUDisk(0.0, 1.0);
				sendAWACSMessage("RDMX2SDS1", "WRITE " + rma + " " + sds + " Ack");
			}
			else if(payload.equals("STC1" + sds + " WRITE " + rma + " " + sds)){
				workloadCPUDisk(0.0, 1.0);
				sendAWACSMessage("STC1SDS1", "WRITE " + rma + " " + sds + " Ack");
			}
			else if(payload.equals("STC2" + sds + " WRITE " + rma + " " + sds)){
				workloadCPUDisk(0.0, 1.0);
				sendAWACSMessage("STC2SDS1", "WRITE " + rma + " " + sds + " Ack");
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

