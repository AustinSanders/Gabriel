
package c2.conn.fred;

import java.io.*;
import java.net.*;
import java.util.*;

import edu.uci.ics.namelessljm.*;

public class FredDiscoveryServer implements IFredDiscoveryServer{
	
	private int port;
	private String localHostName;
	private InetAddress staticLocalHost;
	private InetAddress localHost;
	private InetAddress[] hostList = null;
	
	public FredDiscoveryServer() throws IOException{
		this(new String[]{"localhost/-.-.-.*"}, DEFAULT_PORT);
	}

	public FredDiscoveryServer(String[] hostStrings) throws IOException{
		this(hostStrings, DEFAULT_PORT);
	}
	
	public FredDiscoveryServer(String[] hostStrings, int port) throws IOException{
		this.port = port;
		LJMDeployment.deploy("FredDiscoveryServer", this, port);
		try{
			localHost = InetAddress.getLocalHost();
			staticLocalHost = InetAddress.getByName("127.0.0.1");
			localHostName = localHost.getHostName();
		}
		catch(UnknownHostException e){
			throw new IOException("Can't find local host.");
		}
		setHostList(hostStrings);

		Ponger ponger = new Ponger();
		ponger.start();
		
		Pinger pinger = new Pinger();
		pinger.start();
	}

	private static String[] breakIPAddress(String ipAddress){
		StringTokenizer st = new StringTokenizer(ipAddress, ".");
		if(st.countTokens() != 4){
			throw new IllegalArgumentException("Invalid address format: " + ipAddress);
		}
		String[] segments = new String[4];
		for(int i = 0; i < 4; i++){
			segments[i] = st.nextToken();
		}
		return segments;
	}
	
	private static int[] getRangeValues(String range){
		StringTokenizer st = new StringTokenizer(range, "-");
		if(st.countTokens() != 2){
			throw new IllegalArgumentException("Invalid range: " + range);
		}
		
		int[] segments = new int[2];
		String seg1 = st.nextToken();
		String seg2 = st.nextToken();
		
		try{
			segments[0] = Integer.parseInt(seg1);
			segments[1] = Integer.parseInt(seg2);
		}
		catch(NumberFormatException e){
			throw new IllegalArgumentException("Invalid range: " + range);
		}
		if(segments[0] > segments[1]){
			int tmp = segments[0];
			segments[0] = segments[1];
			segments[1] = tmp;
		}
		return segments;
	}
	
	private static String[] expandRange(String range){
		if(range.indexOf("-") == -1){
			return new String[]{range};
		}
		else{
			int[] rangeCaps = getRangeValues(range);
			String[] rangeValues = new String[rangeCaps[1] - rangeCaps[0] + 1];
			int j = 0;
			for(int i = rangeCaps[0]; i <= rangeCaps[1]; i++){
				rangeValues[j++] = i + "";
			}
			return rangeValues;
		}
	}
	
	private static String[] expandRanges(String[] rangedAddressSegments){
		String[] range1, range2, range3, range4;
		
		range1 = expandRange(rangedAddressSegments[0]);
		range2 = expandRange(rangedAddressSegments[1]);
		range3 = expandRange(rangedAddressSegments[2]);
		range4 = expandRange(rangedAddressSegments[3]);
		
		int j = 0;
		String[] ret = new String[range1.length * range2.length * range3.length * range4.length];
		for(int i1 = 0; i1 < range1.length; i1++){
			for(int i2 = 0; i2 < range2.length; i2++){
				for(int i3 = 0; i3 < range3.length; i3++){
					for(int i4 = 0; i4 < range4.length; i4++){
						ret[j++] = range1[i1] + "." + range2[i2] + "." + range3[i3] + "." + range4[i4];
					}
				}
			}
		}
		return ret;
	}
	
	public void setHostList(String[] hostListStrings) throws IOException{
		Vector v = new Vector();
		for(int i = 0; i < hostListStrings.length; i++){
			int slashIndex = hostListStrings[i].indexOf("/");
			if(slashIndex == -1){
				String baseHost = hostListStrings[i];
				InetAddress addr = InetAddress.getByName(baseHost);
				if((!addr.equals(localHost)) && (!addr.equals(staticLocalHost))){
					v.addElement(addr);
				}
			}
			else{
				String baseHost = hostListStrings[i].substring(0, slashIndex).trim();
				String subnetMask = hostListStrings[i].substring(slashIndex+1).trim();
				
				InetAddress baseAddr;
				if(baseHost.equals("localhost")){
					baseAddr = localHost;
				}
				else if(baseHost.startsWith("127.0.0.")){
					baseAddr = localHost;
				}
				else{
					baseAddr = InetAddress.getByName(baseHost);
				}
				String[] baseAddrSegments = breakIPAddress(baseAddr.getHostAddress());
				String[] subnetMaskSegments = breakIPAddress(subnetMask);
				
				String[] resultantSegments = new String[4];
				
				for(int j = 0; j < 4; j++){
					if(subnetMaskSegments[j].equals("-")){
						resultantSegments[j] = baseAddrSegments[j];
					}
					else if(subnetMaskSegments[j].equals("*")){
						resultantSegments[j] = "1-254";
					}
					else{
						//Will throw an exception if the range is a bad format.
						getRangeValues(subnetMaskSegments[j]);
						resultantSegments[j] = subnetMaskSegments[j];
					}
				}
				
				String[] allAddresses = expandRanges(resultantSegments);
				for(int j = 0; j < allAddresses.length; j++){
					InetAddress addr = InetAddress.getByName(allAddresses[j]);
					if((!addr.equals(localHost)) && (!addr.equals(staticLocalHost))){
						v.addElement(addr);
					}
				}
			}
		}
		hostList = new InetAddress[v.size()];
		v.copyInto(hostList);
	}
	
	//Maps host names to proxies
	private HashMap otherKnownHosts = new HashMap();
	
	private Object tableLock = new Object();
	
	private Hashtable nodeTable = new Hashtable();
	private Hashtable groupTable = new Hashtable();
	//Maps group:port strings to the times (Longs) when they should be un-advertised automatically.
	private Hashtable adDestroyTimes = new Hashtable();
	
	public String getHostName(){
		return localHostName;
	}
	
	public void advertiseNode(String groupName, int port, long advertiseTimeout){
		synchronized(tableLock){
			//System.out.println("Node advertised on port: " + port + " in group: " + groupName);
			String groupPortString = groupName + ":" + port;
			Long adDestroyTime = (Long)adDestroyTimes.get(groupPortString);
			if(adDestroyTime != null){
				//Just renew the time, don't go through all the unad-ad trouble.
				Long newAdDestroyTime = new Long(System.currentTimeMillis() + advertiseTimeout);
				adDestroyTimes.put(groupPortString, newAdDestroyTime);
				return;
			}
		
			unadvertiseNode(port);
			nodeTable.put(new Integer(port), groupName);
			int[] ports = (int[])groupTable.get(groupName);
			//System.out.println("Before advertisement, ports were: " + c2.util.ArrayPrinter.arrayToString(ports));
			if(ports == null){
				groupTable.put(groupName, new int[]{port});
			}
			else{
				int[] newPorts = new int[ports.length + 1];
				System.arraycopy(ports, 0, newPorts, 1, ports.length);
				newPorts[0] = port;
				groupTable.put(groupName, newPorts);
				//System.out.println("After advertisement, ports were: " + c2.util.ArrayPrinter.arrayToString(newPorts));
			}
			adDestroyTimes.put(groupPortString, new Long(System.currentTimeMillis() + advertiseTimeout));
		}
	}
	
	public void unadvertiseNode(int port){
		synchronized(tableLock){
			String groupName = (String)nodeTable.get(new Integer(port));
			if(groupName != null){
				nodeTable.remove(new Integer(port));
				adDestroyTimes.remove(groupName + ":" + port);
				int[] ports = (int[])groupTable.get(groupName);
				if(ports.length == 1){
					groupTable.remove(groupName);
					return;
				}
				else{
					int[] newPorts = new int[ports.length - 1];
					int j = 0;
					for(int i = 0; i < ports.length; i++){
						if(ports[i] != port){
							newPorts[j++] = ports[i];
						}
					}
					groupTable.put(groupName, newPorts);
				}							
			}
		}
	}
	
	public int[] getPorts(String groupName){
		//System.out.println("Getting ports for group: " + groupName);
		synchronized(tableLock){
			int[] ports = (int[])groupTable.get(groupName);
			if(ports == null){
				return new int[]{};
			}
			else{
				for(int i = 0; i < ports.length; i++){
					String groupPortString = groupName + ":" + ports[i];
					Long adDestroyTime = (Long)adDestroyTimes.get(groupPortString);
					if(adDestroyTime != null){
						long adt = adDestroyTime.longValue();
						long ctm = System.currentTimeMillis();
						if(adt < ctm){  //If it's time to destroy the ad
							unadvertiseNode(ports[i]);
						}
					}
				}
				//This is not perfect, because it will give one last wrong answer out,
				//but that will eventually work itself out.
				return ports;
			}
		}
	}
	
	public boolean hasGroup(String groupName){
		synchronized(tableLock){
			return groupTable.get(groupName) != null;
		}
	}
	
	public String getGroup(int port){
		synchronized(tableLock){
			return (String)nodeTable.get(new Integer(port));
		}
	}
	
	public void _check(){
	}
	
	public String[] getKnownHosts(){
		synchronized(otherKnownHosts){
			String[] arr = new String[otherKnownHosts.size()];
			int i = 0;
			for(Iterator it = otherKnownHosts.keySet().iterator(); it.hasNext(); ){
				arr[i++] = (String)it.next();
			}
			return arr;
		}
	}
	
	protected void syncHosts(){
		synchronized(otherKnownHosts){
			String[] okh = getKnownHosts();
			for(int j = 0; j < okh.length; j++){
				String otherHostname = okh[j];
				IFredDiscoveryServer otherHost = (IFredDiscoveryServer)otherKnownHosts.get(otherHostname);
				try{
					String[] otherHostKnownHosts = otherHost.getKnownHosts();
					for(int i = 0; i < otherHostKnownHosts.length; i++){
						addKnownHost(otherHostKnownHosts[i]);
					}
				}
				catch(LJMException e){
					removeKnownHost(otherHostname);
				}
			}
		}
	}
	
	protected void removeKnownHost(String hostname){
		synchronized(otherKnownHosts){
			otherKnownHosts.remove(hostname);
		}
	}
		
	protected void addKnownHost(String hostname){
		synchronized(otherKnownHosts){
			if(otherKnownHosts.get(hostname) == null){
				otherKnownHosts.put(hostname, (IFredDiscoveryServer)LJMProxyFactory.createProxy(hostname, IFredDiscoveryServer.DEFAULT_PORT, 
					"FredDiscoveryServer", new Class[]{IFredDiscoveryServer.class}));
			}
		}
	}

	public String toString(){
		return "FredDiscoveryServer on: " + localHostName + ":" + port;
	}
	
	class Pinger extends Thread{
		boolean terminate = false;
		DatagramSocket s = null;
		final byte[] pingdata = "FRED-PING".getBytes();
		Hashtable ht = new Hashtable();
		
		public Pinger() throws IOException{
			this.setPriority(c2.fw.ThreadPriorities.LOW_PRIORITY);
			s = new DatagramSocket();
		}
		
		public void terminate(){
			terminate = true;
		}
		
		public void run(){
			int i = 0;
			while(!terminate){
				pingHostList();
				if((i++) == 2){
					i = 0;
					syncHosts();
				}
				
				try{
					Thread.sleep(2500);
				}
				catch(InterruptedException e){}
			}
			s.close();
		}
		
		public void ping(InetAddress host){
			try{
				DatagramPacket pack = (DatagramPacket)ht.get(host);
				if(pack == null){
					pack = new DatagramPacket(pingdata, pingdata.length,
						host, port + 1);
					ht.put(host, pack);
				}
				// Do a send. Note that send takes a byte for the ttl and not an int.
				s.send(pack);
			}
			catch(IOException e){
				System.err.println("FredDiscoveryServer warning: " + e);
			}
		}
		
		public void pingHostList(){
			if(hostList == null){
				return;
			}
			
			for(int i = 0; i < hostList.length; i++){
				ping(hostList[i]);
			}
		}
	}
	
	class Ponger extends Thread{
		boolean terminate = false;
		DatagramSocket s = null;
		final byte[] pingdata = "FRED-PING".getBytes();
		
		public Ponger() throws IOException{
			this.setPriority(c2.fw.ThreadPriorities.LOW_PRIORITY);
			s = new DatagramSocket(port + 1);
			s.setSoTimeout(10000);
		}
		
		public void terminate(){
			terminate = true;
		}
		
		protected boolean isPingPacket(DatagramPacket pack){
			if(pack.getLength() != pingdata.length){
				return false;
			}
			for(int i = 0; i < pingdata.length; i++){
				byte[] data = pack.getData();
				if(pingdata[i] != data[i]){
					return false;
				}
			}
			return true;
		}
		
		public void run(){
			byte buf[] = new byte[1024];
			DatagramPacket pack = new DatagramPacket(buf, buf.length);
			while(!terminate){
				// Create a DatagramPacket and do a receive
				try{
					s.receive(pack);
					if(isPingPacket(pack)){
						addKnownHost(pack.getAddress().getHostAddress());
					}
				}
				catch(InterruptedIOException iioe){
					//Do nothing.
				}
				catch(IOException e){
					e.printStackTrace();
					terminate = true;
				}
			}
			s.close();
		}
		
	}
}


