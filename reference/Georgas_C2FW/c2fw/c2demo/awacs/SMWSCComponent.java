package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class SMWSCComponent extends SimpleAWACSBrick{

	private int currentTime;
	private double timeRDMX1 = 0.0;
	private double timeRDMX2 = 0.0;
	private double timeSTC1 = 0.0;
	private double timeSTC2 = 0.0;

	public SMWSCComponent(IComponent iComp){
		super(iComp);
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();
			String destinationPlatform = am.getDestinationPlatform();
			String destinationProcess = am.getDestinationProcess();
			
			if((destinationProcess == null) || (destinationProcess.equals(""))){
			}
			else if(destinationProcess.startsWith("SM")){
				if(payload.equals(destinationPlatform + " Heartbeat")){
					workloadCPU(0.100);
					String sourcePlatform = am.getSourcePlatform();
					if(sourcePlatform.equals("RDMX1")){
						timeRDMX1 = currentTime;
					}
					else if(sourcePlatform.equals("RDMX2")){
						timeRDMX2 = currentTime;
					}
					else if(sourcePlatform.equals("STC1")){
						timeSTC1 = currentTime;
					}
					else if(sourcePlatform.equals("STC2")){
						timeSTC2 = currentTime;
					}
				}
				else if(payload.equals(destinationPlatform + " SM Wrap")){
					workloadCPU(0.100);
					sendSMWrapReply(
						"DS" + getPlatformName(),  
						destinationPlatform +
						" SM Wrap Reply"+
						" RDMX1 "+ timeRDMX1 +
						" RDMX2 "+ timeRDMX2 +
						" STC1 " + timeSTC1  +
						" STC2 " + timeSTC2  ,
						am.getSourcePlatform(),
						"SS",
						am.getRoute()
					);
				}
			}
			else if(destinationProcess.equals("MH")){
				workloadCPU(0.100);
			}
				
			System.out.println(getIdentifier() + " absorbed message: " + payload);
		}
	}

	protected String getPlatformName(){
		return getIdentifier().toString().substring(2);
	}
		
	public void sendSMWrapReply(String dest, String payload, String destinationPlatform,
		String destinationProcess, String route){
		
		AWACSMessage am = new AWACSMessage();
		am.setPayload(payload);
		
		am.setSourcePlatform(getPlatformName());
		am.setSourceProcess("SM");
		am.setRoute(route);
		am.setDestinationPlatform(destinationPlatform);
		am.setDestinationProcess(destinationProcess);
		am.setLength(240.0);
		
		sendRawAWACSMessage(dest, am);
	}

	public void tick(int timerCount){
		super.tick(timerCount);
		currentTime = timerCount;
	}

}

