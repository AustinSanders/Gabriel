package c2.conn.fred;

public interface IFredDiscoveryServer{
	public static final int DEFAULT_PORT = 0xc4c4;

	public void _check();
	
	public void advertiseNode(String groupName, int port, long advertisingTimeout);
	public void unadvertiseNode(int port);
	public String getGroup(int port);
	public String getHostName();
	public int[] getPorts(String groupName);
	
	public String[] getKnownHosts();
	
}

