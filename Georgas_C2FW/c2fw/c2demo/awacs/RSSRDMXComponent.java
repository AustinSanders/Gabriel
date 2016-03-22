package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class RSSRDMXComponent extends SimpleAWACSBrick{

	public RSSRDMXComponent(IComponent iComp){
		super(iComp);
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			if(originatedFrom(am, "DSRDMX1")){
				workloadCPU(0.100);
				System.out.println("AM.getSourceProcess = " + am.getSourceProcess());
				System.out.println("AM.getSourcePlatform = " + am.getSourcePlatform());
				
				if(am.getSourceProcess().equals("CKPT" + am.getSourcePlatform())){
					sendCkptScsiWrite("SCSIRDMX1", "WRITE RMA3 SDS2", am.getLength(),
						"SDS2", "CTLRMA3");
					sendCkptScsiWrite("SCSIRDMX1", "WRITE RMA4 SDS2", am.getLength(),
						"SDS2", "CTLRMA4");
				}
			}
			else{
				System.out.println(getIdentifier() + " absorbed message: " + am.getPayload());
			}			
		}
	}
	
	public void tick(int timerCount){
		super.tick(timerCount);
	}
	
	protected void sendCkptScsiWrite(String dest, String payload, double length,
		String destinationPlatform, String destinationProcess){
		
		AWACSMessage am = new AWACSMessage();
		am.setSourcePlatform("RDMX" + getFTNumber());
		am.setSourceProcess("RSS");
		am.setRoute("");
		am.setDestinationPlatform(destinationPlatform);
		am.setDestinationProcess(destinationProcess);
		am.setLength(length);
		
		sendRawAWACSMessage(dest, am);
	}
	
	/*
	public void send_ckpt_scsi_write(int c_x,
		int c_y,
		String info,
		double b_length,
		String d_platform,
		String d_thread)
	{
		String s_platform = "";
		String s_thread = "";
		Color s_color = Color.white;
		String b_route = "";
		s_platform = get_platform_name();
		s_thread = procname;
		buff_write(c_x,
			c_y,
			info,
			b_length,
			d_platform,
			d_thread,
			s_platform,
			s_thread,
			b_route,
			s_color);
		return;
	}
	*/
}

