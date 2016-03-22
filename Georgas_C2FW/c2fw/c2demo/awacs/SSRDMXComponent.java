package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public class SSRDMXComponent extends SimpleAWACSBrick{

	public SSRDMXComponent(IComponent iComp){
		super(iComp);
	}

	public void handle(Message m){
		super.handle(m);
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();
			System.out.println(getIdentifier() + " absorbed message: " + payload);
		}
	}

	public void sendSMWrap(String dest, String payload, String destinationPlatform,
		String destinationProcess, String route){
		
		AWACSMessage am = new AWACSMessage();
		am.setPayload(payload);
		am.setSourcePlatform("RDMX" + getFTNumber());
		am.setSourceProcess("SS");
		am.setRoute(route);
		am.setDestinationPlatform(destinationPlatform);
		am.setDestinationProcess(destinationProcess);
		am.setLength(208.0);
		
		sendRawAWACSMessage(dest, am);
	}
	/*
	public void send_smwrap(int c_x,
		int c_y,
		String info,
		String d_platform,
		String d_thread,
		String b_route)
	{
		double b_length = 208.0;
		String s_platform = "";
		String s_thread = "";
		Color s_color = Color.green;
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
	public void tick(int timerCount){
		super.tick(timerCount);
		if(timerCount < 45){
			return;
		}
		int tc = ((timerCount - 45) % 42) + 1;
		if((tc >= 1) && (tc <= 16)){
			int wsNum = tc;
			sendSMWrap("DSRDMX1", "WSC" + wsNum + " SM Wrap", "WSC" + wsNum, "SM", "");
		}
		else if((tc >= 22) && (tc <= 37)){
			int wsNum = tc - 21;
			sendSMWrap("DSRDMX1", "WSC" + wsNum + " SM Wrap", "WSC" + wsNum, "SM", "FLAN2");
		}
		else{
			switch(tc){
				case 17:
					sendSMWrap("DSRDMX1", "STC1 SM Wrap", "STC1", "SM", "");
					break;
				case 18:
					sendSMWrap("DSRDMX1", "STC2 SM Wrap", "STC2", "SM", "");
					break;
				case 19:
					sendSMWrap("DSRDMX1", "AMC1 SM Wrap", "AMC1", "SM", "");
					break;
				case 20:
					sendSMWrap("DSRDMX1", "AMC2 SM Wrap", "AMC2", "SM", "");
					break;
				case 21:
					sendSMWrap("DSRDMX1", "RDMX2 SM Wrap", "RDMX2", "SM", "");
					break;
				
				case 38:
					sendSMWrap("DSRDMX1", "STC1 SM Wrap", "STC1", "SM", "FLAN2");
					break;
				case 39:
					sendSMWrap("DSRDMX1", "STC2 SM Wrap", "STC2", "SM", "FLAN2");
					break;
				case 40:
					sendSMWrap("DSRDMX1", "AMC1 SM Wrap", "AMC1", "SM", "FLAN2");
					break;
				case 41:
					sendSMWrap("DSRDMX1", "AMC2 SM Wrap", "AMC2", "SM", "FLAN2");
					break;
				case 42:
					sendSMWrap("DSRDMX1", "RDMX2 SM Wrap", "RDMX2", "SM", "FLAN2");
					break;
			}
		}
	}

}

