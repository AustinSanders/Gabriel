package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class SwitchSDSComponent extends SimpleAWACSBrick{

	private String host;
	private String sds;
	private String[] connectionNames;
	
	private static final String[] RDMX1SDS1Connections =
	{
		"CTL1SDS1",
		"CTL2SDS1",
		"CTL3SDS1",
		"CTL4SDS1",
		"SCSIRDMX1",
		"SCSIRDMX1",
		"SCSIRDMX1",
		"SCSIRDMX1"
	};
	
	private static final String[] RDMX2SDS1Connections =
	{
		"CTL1SDS1",
		"CTL2SDS1",
		"CTL3SDS1",
		"CTL4SDS1",
		"SCSIRDMX2",
		"SCSIRDMX2",
		"SCSIRDMX2",
		"SCSIRDMX2"
	};
	
	private static final String[] STC1SDS1Connections =
	{
		"CTL1SDS1",
		"CTL2SDS1",
		"CTL3SDS1",
		"CTL4SDS1",
		"SCSISTC1",
		"SCSISTC1",
		"SCSISTC1",
		"SCSISTC1"
	};
	
	private static final String[] STC2SDS1Connections =
	{
		"CTL1SDS1",
		"CTL2SDS1",
		"CTL3SDS1",
		"CTL4SDS1",
		"SCSISTC2",
		"SCSISTC2",
		"SCSISTC2",
		"SCSISTC2"
	};
	
	public SwitchSDSComponent(IComponent iComp, String host, String sds){
		super(iComp);
		this.host = host;
		this.sds = sds;
		
		String hs = host + sds;
		if(hs.equals("RDMX1SDS1")){
			connectionNames = RDMX1SDS1Connections;
		}
		else if(hs.equals("RDMX2SDS1")){
			connectionNames = RDMX2SDS1Connections;
		}
		else if(hs.equals("STC1SDS1")){
			connectionNames = STC1SDS1Connections;
		}
		else if(hs.equals("STC2SDS1")){
			connectionNames = STC2SDS1Connections;
		}
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();
			
			if(payload.equals("SCSI" + host + " WRITE RMA1 " + sds)){
				workloadCPU(0.100d);
				sendAWACSMessage(connectionNames[0], "WRITE RMA1 " + sds);
			}
			else if(payload.equals("SCSI" + host + " WRITE RMA2 " + sds)){
				workloadCPU(0.100d);
				sendAWACSMessage(connectionNames[1], "WRITE RMA2 " + sds);
			}
			else if(payload.equals("SCSI" + host + " WRITE RMA3 " + sds)){
				workloadCPU(0.100d);
				sendAWACSMessage(connectionNames[2], "WRITE RMA3 " + sds);
			}
			else if(payload.equals("SCSI" + host + " WRITE RMA4 " + sds)){
				workloadCPU(0.100d);
				sendAWACSMessage(connectionNames[3], "WRITE RMA4 " + sds);
			}
			else if(payload.equals("CTL1" + host + " WRITE RMA1 " + sds + " Ack")){
				workloadCPU(0.100d);
				sendAWACSMessage(connectionNames[4], "WRITE RMA1 " + sds + " Ack");
			}
			else if(payload.equals("CTL2" + host + " WRITE RMA2 " + sds + " Ack")){
				workloadCPU(0.100d);
				sendAWACSMessage(connectionNames[5], "WRITE RMA2 " + sds + "Ack");
			}
			else if(payload.equals("CTL3" + host + " WRITE RMA3 " + sds + " Ack")){
				workloadCPU(0.100d);
				sendAWACSMessage(connectionNames[6], "WRITE RMA3 " + sds + "Ack");
			}
			else if(payload.equals("CTL4" + host + " WRITE RMA4 " + sds + " Ack")){
				workloadCPU(0.100d);
				sendAWACSMessage(connectionNames[7], "WRITE RMA4 " + sds + "Ack");
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

