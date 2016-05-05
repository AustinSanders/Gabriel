package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class SCSIRDMXComponent extends SimpleAWACSBrick{

	public SCSIRDMXComponent(IComponent iComp){
		super(iComp);
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();

			if(payload.equals("RDMX1SDS1 WRITE RMA1 SDS1 Ack")){
				sendAWACSMessage("MTT1RDMX1", "WRITE RMA1 SDS1 Ack");
				workloadCPUSCSI(0.100d, 0.100d);
			}
			else if(payload.equals("RDMX1SDS1 WRITE RMA2 SDS1 Ack")){
				sendAWACSMessage("MTT2RDMX1", "WRITE RMA2 SDS1 Ack");
				workloadCPUSCSI(0.100d, 0.100d);
			}
			else if(payload.equals("RDMX1SDS1 WRITE RMA3 SDS1 Ack")){
				sendAWACSMessage("MTT3RDMX1", "WRITE RMA3 SDS1 Ack");
				workloadCPUSCSI(0.100d, 0.100d);
			}
			else if(payload.equals("RDMX1SDS2 WRITE RMA3 SDS2 Ack")){
				sendAWACSMessage("RSSRDMX1", "WRITE RMA3 SDS2 Ack");
				workloadCPUSCSI(0.100d, 0.100d);
			}
			else if(payload.equals("RDMX1SDS2 WRITE RMA4 SDS2 Ack")){
				sendAWACSMessage("RSSRDMX2", "WRITE RMA4 SDS2 Ack");
				workloadCPUSCSI(0.100d, 0.100d);
			}
			else if(payload.equals("MTT1RDMX1 WRITE RMA1 SDS1")){
				sendAWACSMessage("RDMX1SDS1", "WRITE RMA1 SDS1");
				workloadCPUSCSI(0.100d, 0.100d);
			}
			else if(payload.equals("MTT2RDMX1 WRITE RMA2 SDS1")){
				sendAWACSMessage("RDMX1SDS1", "WRITE RMA2 SDS1");
				workloadCPUSCSI(0.100d, 0.100d);
			}
			else if(payload.equals("MTT3RDMX1 WRITE RMA3 SDS1")){
				sendAWACSMessage("RDMX1SDS1", "WRITE RMA3 SDS1");
				workloadCPUSCSI(0.100d, 0.100d);
			}
			else{
				if(originatedFrom(am, "RSSRDMX1")){
					String destPlatform = am.getDestinationPlatform();
					if(destPlatform.equals("SDS1")){
						workloadCPUSCSI(0.100, am.getLength());
						AWACSMessage am2 = (AWACSMessage)am.duplicate();
						am2.setPayload("SCSIRDMX1 " + am2.getPayload());
						sendRawAWACSMessage("RDMX1SDS1", am2);
					}
					else if(destPlatform.equals("SDS2")){
						workloadCPUSCSI(0.100, am.getLength());
						AWACSMessage am2 = (AWACSMessage)am.duplicate();
						am2.setPayload("SCSIRDMX1 " + am2.getPayload());
						sendRawAWACSMessage("RDMX1SDS2", am2);
					}
				}
				else if(originatedFrom(am, "MAPRDMX1")){
					sendAWACSMessage("MAPRDMX1", "MAP Ack");
					workloadCPUSCSI(0.100, 0.100);
					//Forward message to RDMX1SDS1
					sendRawAWACSMessage("RDMX1SDS1", am);
				}
				else{
					System.out.println(getIdentifier() + " absorbed message: " + payload);
				}			
			}
		}
	}
	
	public void tick(int timerCount){
		super.tick(timerCount);
	}

}

