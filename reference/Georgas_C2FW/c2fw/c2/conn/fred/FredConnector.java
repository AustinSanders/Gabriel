package c2.conn.fred;

import java.io.*;
import java.net.*;
import java.util.*;

import c2.fw.*;

import edu.uci.ics.namelessljm.*;

public class FredConnector extends AbstractBrick implements Connector, IFredConnector{

	public static final Identifier LOCAL_INTERFACE_ID = new SimpleIdentifier("IFACE_LOCAL");

	protected int heartbeatInterval;
	protected String groupName;
	protected Interface localIface;
	protected int localPort = -1;
	protected MaintenanceThread maintenanceThread;
	protected boolean initDone = false;
	protected String[] hosts = null;
	
	//Maps "hostname:port" to proxies
	protected HashMap remoteConnections = new HashMap();
	
	//Contains recently received messages
	protected AdaptingQueue recentMessageQueue;
	
	public FredConnector(Identifier id){
		this(id, new InitializationParameter[]{});
	}

	//InitializationParameters accepted:
	//groupName = [groupName]
	//heartbeatInterval = [heartbeatInterval]
	//host = [host] *allows multiple
	//  Where:
	//    groupName = the name of the Fred group for this connector to be a part of.
	//    heartbeatInterval = [heartbeat interval in ms]
	//    host = a Fred Host String (see FredDiscoveryServer)
	
	public FredConnector(Identifier id, InitializationParameter[] params){
		super(id);
		
		localIface = new SimpleInterface(LOCAL_INTERFACE_ID, this);

		//Set up defaults
		groupName = "$DEFAULT_GROUP$";
		heartbeatInterval = 2500;
		Vector hostStrings = new Vector();
	
		for(int i = 0; i < params.length; i++){
			String name = params[i].getName();
			String value = params[i].getValue();
			
			if(name.equals("groupName")){
				this.groupName = value;
			}
			else if(name.equals("heartbeatInterval")){
				try{
					this.heartbeatInterval = Integer.parseInt(value);
				}
				catch(NumberFormatException e){
					//Just leave the default
				}
			}
			else if(name.equals("host")){
				hostStrings.addElement(value);
			}
		}
		
		hosts = new String[hostStrings.size()];
		hostStrings.copyInto(hosts);
	}
	
	public Interface getInterface(Identifier id){
		if(id.equals(LOCAL_INTERFACE_ID)){
			return localIface;
		}
		else{
			return null;
		}
	}
	
	public Interface[] getAllInterfaces(){
		return new Interface[]{
			localIface
		};
	}
	
	public void init(){
		recentMessageQueue = new AdaptingQueue(50);
		localPort = LJMDeployment.deploy("FredConnector", this);
		maintenanceThread = new MaintenanceThread(heartbeatInterval);
		maintenanceThread.start();
		initDone = true;
	}	
	
	public void begin(){
	}
	
	public void end(){
	}
	
	public void destroy(){
		initDone = false;
		maintenanceThread.terminate();
		LJMDeployment.undeploy("FredConnector", localPort);
	}

	public void handleRemoteMessage(WrappedMessage m){
		if(initDone == false){
			return;
		}
		
		if(recentMessageQueue.contains(m)){
			return;
		}
		recentMessageQueue.enqueue(m);
		//System.out.println("SendToAll: " + m.getMessage());
		sendToAll(m.getMessage(), localIface);
	}
	
	Vector hostsToRemove = new Vector();
	
	public void handle(Message m){
		synchronized(remoteConnections){
			WrappedMessage wm = new WrappedMessage(m);
			recentMessageQueue.enqueue(wm);
			for(Iterator it = remoteConnections.keySet().iterator(); it.hasNext(); ){
				String remoteHostAndPort = (String)it.next();
				IFredConnector fc = (IFredConnector)remoteConnections.get(remoteHostAndPort);
				try{
					fc.handleRemoteMessage(wm);
				}
				catch(LJMException ljme){
					Throwable t = ljme.getTargetException();
					if(t != null){
						if(t instanceof java.io.NotSerializableException){
							t.printStackTrace();
						}
					}
					//ljme.printStackTrace();
					hostsToRemove.addElement(remoteHostAndPort);
				}
			}
			for(int i = 0; i < hostsToRemove.size(); i++){
				String remoteHostAndPort = (String)hostsToRemove.elementAt(i);
				int x = remoteHostAndPort.indexOf(":");
				String host = remoteHostAndPort.substring(0, x);
				String portS = remoteHostAndPort.substring(x+1);
				int port = Integer.parseInt(portS);
				removeGroupMember(host, port);
			}
			hostsToRemove.removeAllElements();
		}
	}

	protected void addGroupMember(String hostname, int port){
		synchronized(remoteConnections){
			String hostString = hostname + ":" + port;
			if(!remoteConnections.keySet().contains(hostString)){
				IFredConnector remoteFredConnector = 
					(IFredConnector)LJMProxyFactory.createProxy(hostname, port, 
					"FredConnector", new Class[]{IFredConnector.class});
				remoteConnections.put(hostString, remoteFredConnector);
			}
			//System.out.println("Added host: " + hostString);
		}
	}
	
	protected void removeGroupMember(String hostname, int port){
		String hostString = hostname + ":" + port;
		synchronized(remoteConnections){
 			remoteConnections.remove(hostString);
		}
	}
	
	protected void printGroupMembers(){
		for(Iterator it = remoteConnections.keySet().iterator(); it.hasNext(); ){
			System.out.println(it.next());
		}
	}	
	
	class MaintenanceThread extends Thread{
		//This thread heartbeats the local FredDiscoveryServer every once in a while.
		//If it finds that the server is down, it starts up a new one.
		
		private IFredDiscoveryServer fredDiscovery;
		private int intervalInMilliseconds;
		private boolean terminate;
		
		private Hashtable remoteFredDiscoveries;
		
		public MaintenanceThread(int intervalInMilliseconds){
			terminate = false;
			remoteFredDiscoveries = new Hashtable();
			this.intervalInMilliseconds = intervalInMilliseconds;
			this.setPriority(c2.fw.ThreadPriorities.HIGH_PRIORITY);
		}
		
		public void terminate(){
			this.terminate = true;
		}
		
		public void run(){
			while(!terminate){
				doHeartbeat();
				doDiscovery();
				//printGroupMembers();
				Thread.yield();
				try{
					Thread.sleep(intervalInMilliseconds);
				}
				catch(InterruptedException e){
				}
			}
		}
		
		protected void normalizeDiscoveryServer(){
			if(fredDiscovery == null){
				fredDiscovery = 
					(IFredDiscoveryServer)LJMProxyFactory.createProxy("127.0.0.1", IFredDiscoveryServer.DEFAULT_PORT, 
					"FredDiscoveryServer", new Class[]{IFredDiscoveryServer.class});
			}
			try{
				fredDiscovery._check();
			}
			catch(LJMException e){
				//There's something wrong with the current FredDiscoveryServer.  It either doesn't exist
				//yet or went down.  So, we go into contention and we attempt to be the master process
				//on this machine now.
				try{
					new FredDiscoveryServer(hosts);
				}
				catch(BindException be) {
					// ignore the binding exception
					// If multiple threads do the _check at the same time, they 
					// could all fail, and all try to create the server at the 
					// same time. A better solution is probably use a proper lock,
					// but ignoring is the easiest solution.
				}
				catch(IOException e2){
					throw new RuntimeException("Can't establish a local FredDiscoveryServer: " + e2.toString());
				}
				
				try{
					fredDiscovery._check();
				}
				catch(LJMException e3){
					fredDiscovery = null;
					throw new RuntimeException("Error communicating with local FredDiscoveryServer.");
				}
			}
		}
		
		protected void queryFredDiscoveryServer(IFredDiscoveryServer fd){
			try{
				int[] ports = fd.getPorts(groupName);
				//System.out.println("ports = " + c2.util.ArrayPrinter.arrayToString(ports));
				if((ports == null) || (ports.length == 0)){
					return;
				}
				String host = fd.getHostName();
				for(int i = 0; i < ports.length; i++){
					addGroupMember(host, ports[i]);
				}
			}
			catch(LJMException e){
				//Do nothing.
			}
		}

		protected void doDiscovery(){
			if(fredDiscovery == null){
				return;
			}
			//System.out.println("Querying local FredDiscoveryServer");
			queryFredDiscoveryServer(fredDiscovery);
			try{					
				String[] hosts = fredDiscovery.getKnownHosts();
				for(int i = 0; i < hosts.length; i++){
					try{
						IFredDiscoveryServer remoteFD = (IFredDiscoveryServer)remoteFredDiscoveries.get(hosts[i]);
						if(remoteFD == null){
							remoteFD = 
								(IFredDiscoveryServer)LJMProxyFactory.createProxy(hosts[i], IFredDiscoveryServer.DEFAULT_PORT, 
								"FredDiscoveryServer", new Class[]{IFredDiscoveryServer.class});
							remoteFredDiscoveries.put(hosts[i], remoteFD);
						}
						queryFredDiscoveryServer(remoteFD);
					}
					catch(LJMException iljme){
						iljme.printStackTrace();
						remoteFredDiscoveries.remove(hosts[i]);
						throw iljme;
					}
				}
			}
			catch(LJMException e){
				//Do nothing.
			}
		}
		
		protected void doHeartbeat(){
			try{
				normalizeDiscoveryServer();
			}
			catch(RuntimeException re){
				re.printStackTrace();
			}
			if(localPort != -1){
				try{
					fredDiscovery.advertiseNode(groupName, localPort, heartbeatInterval * 4);
				}
				catch(LJMException e){
					//Do nothing.  We will re-establish on the next heartbeat.
				}
			}
		}
	}
}
