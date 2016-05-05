package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class DSRDMXComponent extends SimpleAWACSBrick{

	public DSRDMXComponent(IComponent iComp){
		super(iComp);
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();

			if(originatedFrom(am, "SWAFLAN1")){
				String destProcess = am.getDestinationProcess();
				if(destProcess.equals("MH")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("MHRDMX1", am);
				}
				else if(destProcess.equals("SM")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("SMRDMX1", am);
				}
				else if(destProcess.equals("SS")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("SSRDMX1", am);
				}
				else if(destProcess.equals("RSS")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("RSSRDMX1", am);
				}
				else if(destProcess.equals("MAP")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("MAPRDMX1", am);
				}
			}
			else if(originatedFrom(am, "SWAFLAN2")){
				String destProcess = am.getDestinationProcess();
				if(destProcess.equals("MH")){
					workloadCPUNIC1NIC2(0.100, 0.00, am.getLength());
					sendRawAWACSMessage("MHRDMX1", am);
				}
				else if(destProcess.equals("SM")){
					workloadCPUNIC1NIC2(0.100, 0.00, am.getLength());
					sendRawAWACSMessage("SMRDMX1", am);
				}
				else if(destProcess.equals("SS")){
					workloadCPUNIC1NIC2(0.100, 0.00, am.getLength());
					sendRawAWACSMessage("SSRDMX1", am);
				}
				else if(destProcess.equals("RSS")){
					workloadCPUNIC1NIC2(0.100, 0.00, am.getLength());
					sendRawAWACSMessage("RSSRDMX1", am);
				}
				else if(destProcess.equals("MAP")){
					workloadCPUNIC1NIC2(0.100, 0.00, am.getLength());
					sendRawAWACSMessage("MAPRDMX1", am);
				}
			}
			else if(
			originatedFrom(am, "MHRDMX1") || 
			originatedFrom(am, "SMRDMX1") ||
			originatedFrom(am, "SSRDMX1") ||
			originatedFrom(am, "RSSRDMX1") ||
			originatedFrom(am, "MAPRDMX1")){
				String route = am.getRoute();
				if((route == null) || (route == "")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("SWAFLAN1", am);
				}
				else if(route.equals("FLAN1")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("SWAFLAN1", am);
				}
				else if(route.equals("FLAN2")){
					workloadCPUNIC1NIC2(0.100, 0.00, am.getLength());
					sendRawAWACSMessage("SWAFLAN2", am);
				}
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

