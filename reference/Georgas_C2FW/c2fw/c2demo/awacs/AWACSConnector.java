package c2demo.awacs;

import c2.fw.*;

public class AWACSConnector extends SimpleAWACSBrick implements Connector{

	private Interface ifaceA = null;
	private Interface ifaceB = null;
	
	private BrickInterfaceIdPair connectedOnA = null;
	private BrickInterfaceIdPair connectedOnB = null;

	public AWACSConnector(edu.uci.isr.xarch.types.IConnector conn){
		super(conn);
	}
		
	private void initBrick(){
		Interface[] ifaces = getAllInterfaces();
		ifaceA = ifaces[0];
		ifaceB = ifaces[1];
		
		connectedOnA = ifaces[0].getAllConnectedInterfaces()[0];
		connectedOnB = ifaces[1].getAllConnectedInterfaces()[0];
	}
		
	public void handle(Message m){
		if(connectedOnA == null){
			initBrick();
		}
		
		super.handle(m);

		if(m.getSource().equals(connectedOnA)){
			//This message is from 'A', send it out to 'B':
			Message m2 = m.duplicate();
			if(m2 instanceof AWACSMessage){
				((AWACSMessage)m2).setEarliestProcessTime(currentTickCount + 1);
			}
			sendToAll(m2, ifaceB);
		}
		else if(m.getSource().equals(connectedOnB)){
			Message m2 = m.duplicate();
			if(m2 instanceof AWACSMessage){
				((AWACSMessage)m2).setEarliestProcessTime(currentTickCount + 1);
			}
			sendToAll(m2, ifaceA);
		}
		else{
			System.out.println("Warning: Unhandled message on " + this);
		}
	}
	
}

