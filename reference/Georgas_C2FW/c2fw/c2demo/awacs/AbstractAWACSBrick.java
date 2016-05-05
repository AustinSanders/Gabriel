package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

public abstract class AbstractAWACSBrick extends AbstractBrick implements TimerListener{

	protected Hashtable interfaces;
	protected int currentTickCount = 0;
	
	public AbstractAWACSBrick(IComponent compDesc){
		super(new SimpleIdentifier(compDesc.getId()));
		interfaces = new Hashtable();
		Collection ifaces = compDesc.getAllInterfaces();
		for(Iterator it = ifaces.iterator(); it.hasNext(); ){
			IInterface iface = (IInterface)it.next();
			Identifier ifaceId = new SimpleIdentifier(iface.getId());
			SimpleInterface si = new SimpleInterface(ifaceId, this);
			interfaces.put(ifaceId, si);
		}
	}

	public AbstractAWACSBrick(IConnector connDesc){
		super(new SimpleIdentifier(connDesc.getId()));
		interfaces = new Hashtable();
		Collection ifaces = connDesc.getAllInterfaces();
		for(Iterator it = ifaces.iterator(); it.hasNext(); ){
			IInterface iface = (IInterface)it.next();
			Identifier ifaceId = new SimpleIdentifier(iface.getId());
			SimpleInterface si = new SimpleInterface(ifaceId, this);
			interfaces.put(ifaceId, si);
		}
	}
	
	public Interface getInterface(Identifier id){
		return (Interface)interfaces.get(id);
	}
	
	public Interface[] getAllInterfaces(){
		Interface[] arr = new Interface[interfaces.size()];
		int i = 0;
		for(Enumeration en = interfaces.elements(); en.hasMoreElements(); ){
			arr[i] = (Interface)en.nextElement();
			i++;
		}
		return arr;
	}

	//Methods to be implemented by the child classes	
	public abstract void init();
	public abstract void begin();
	public abstract void end();
	public abstract void destroy();	
	public abstract void handle(Message m);
	
	public void tick(int tickCount){
		currentTickCount = tickCount;
		//System.out.println("I got a timer tick, with tickCount=" + tickCount);
	}

	protected void sendAWACSMessage(String destinationProcessName, String msg){
		AWACSMessage m = new AWACSMessage();
		m.setPayload(getIdentifier() + " " + msg);
		m.setEarliestProcessTime(currentTickCount + 1);
		
		SimpleIdentifier id = new SimpleIdentifier(getIdentifier().toString() + "_I_" + destinationProcessName);
		//System.out.println("Getting interface: " + id);
		Interface iface = getInterface(id);
		if(iface == null){
			throw new NullPointerException();
		}
		System.out.println(getIdentifier() + " sent a message to interface " + iface + ", message={\"" + m + "\"} at tickCount={" + currentTickCount + "};");
			
		sendToAll(m, iface);
	}

	protected void sendRawAWACSMessage(String destinationProcessName, AWACSMessage m){
		m.setEarliestProcessTime(currentTickCount + 1);
		
		SimpleIdentifier id = new SimpleIdentifier(getIdentifier().toString() + "_I_" + destinationProcessName);
		//System.out.println("Getting interface: " + id);
		Interface iface = getInterface(id);
		if(iface == null){
			System.out.println("Couldn't find interface: " + id);
			System.out.println("Message being sent: " + m);
			throw new NullPointerException();
		}
		System.out.println(getIdentifier() + " sent a message to interface " + iface + ", message={\"" + m + "\"} at tickCount={" + currentTickCount + "};");
			
		sendToAll(m, iface);
	}
	
	protected boolean originatedFrom(Message msg, String srcProcessName){
		if(msg.getDestination().getInterfaceIdentifier().toString().equals(getIdentifier() + "_I_" + srcProcessName)){
			return true;
		}
		else{
			//System.out.println("Question: did " + msg + " originate from " + srcProcessName + "?");
			//System.out.println("msg.getSource().getInterfaceIdentifier().toString() == " + msg.getSource().getInterfaceIdentifier().toString());
			//System.out.println("getIdentifier() + I + srcProcessName == " + getIdentifier() + "_I_" + srcProcessName);
			return false;
		}
	}
	
	protected int getFTNumber(){
		if(getIdentifier().toString().endsWith("1")){
			return 1;
		}
		else if(getIdentifier().toString().endsWith("2")){
			return 2;
		}
		else{
			return 0;
		}
	}
	
	protected void workloadCPU(double amt){
	}
	
	protected void workloadCPUDisk(double cpuAmt, double diskAmt){
	}

	protected void workloadCPUSCSI(double cpuAmt, double scsiAmt){
	}

	protected void workloadCPUNIC1NIC2(double cpuAmt, double nic1Amt, double nic2Amt){
	}
	
	protected void workloadAB1AB2(double ab1Amt, double ab2Amt){
	}
	
}

