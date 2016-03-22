package c2demo.awacs;

import c2.fw.*;
import java.io.*;
import java.util.*;

public class CassiusEventGenerator implements MessageListener, TimerListener{

	protected int currentTickCount = 0;
	protected DataOutputStream dos;
	protected FileOutputStream fos;
	
	public CassiusEventGenerator(ArchitectureController controller){
		((c2.fw.Timer)controller).addTimerListener(this);
		((MessageProvider)controller).addMessageListener(this);
		try{
			fos = new FileOutputStream("awacs-cassius.txt");
			dos = new DataOutputStream(fos);
		}
		catch(IOException e){
			throw new RuntimeException("Cassius listener exploded.");
		}
	}
	
	public void writeEvent(int time, String src, String dest, String message){
		try{
			dos.writeBytes(time + "\\" + src + "\\" + dest + "\\" + message + "\n");
		}
		catch(IOException e){
			throw new RuntimeException("Cassius listener exploded.");
		}
	}
				
	public void messageSent(Message m){
		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();
			BrickInterfaceIdPair src = am.getSource();
			BrickInterfaceIdPair dest = am.getDestination();
			writeEvent(currentTickCount, src.getBrickIdentifier().toString(), dest.getBrickIdentifier().toString(), payload);
		}
	}

	public void tick(int tickCount){
		currentTickCount = tickCount;
	}

}

