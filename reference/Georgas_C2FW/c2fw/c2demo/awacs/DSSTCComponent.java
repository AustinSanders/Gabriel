package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class DSSTCComponent extends SimpleAWACSBrick{

	public DSSTCComponent(IComponent iComp){
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
					sendRawAWACSMessage("MHSTC" + getFTNumber(), am);
				}
				else if(destProcess.equals("SM")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("SMSTC" + getFTNumber(), am);
				}
				else if(destProcess.equals("TIN")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("TINSTC" + getFTNumber(), am);
				}
				else if(destProcess.equals("TOUT")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("TOUTSTC" + getFTNumber(), am);
				}
				else if(destProcess.equals("CKPT")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("CKPTSTC" + getFTNumber(), am);
				}
			}
			else if(originatedFrom(am, "SWAFLAN2")){
				String destProcess = am.getDestinationProcess();
				if(destProcess.equals("MH")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("MHSTC" + getFTNumber(), am);
				}
				else if(destProcess.equals("SM")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("SMSTC" + getFTNumber(), am);
				}
				else if(destProcess.equals("TIN")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("TINSTC" + getFTNumber(), am);
				}
				else if(destProcess.equals("TOUT")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("TOUTSTC" + getFTNumber(), am);
				}
				else if(destProcess.equals("CKPT")){
					workloadCPUNIC1NIC2(0.100, am.getLength(), 0.00);
					sendRawAWACSMessage("CKPTSTC" + getFTNumber(), am);
				}
			}
			else if(
			originatedFrom(am, "MHSTC" + getFTNumber()) || 
			originatedFrom(am, "SMSTC" + getFTNumber()) ||
			originatedFrom(am, "TINSTC" + getFTNumber()) ||
			originatedFrom(am, "TOUTSTC" + getFTNumber()) ||
			originatedFrom(am, "CKPTSTC" + getFTNumber())){
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

