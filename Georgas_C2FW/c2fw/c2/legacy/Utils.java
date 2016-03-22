package c2.legacy;

import c2.fw.*;

public class Utils{

	public static NamedPropertyMessage tagAsC2Request(NamedPropertyMessage m){
		m.addParameter("C2_TYPE", "REQUEST");
		return m;
	}
	
	public static NamedPropertyMessage tagAsC2Notification(NamedPropertyMessage m){
		m.addParameter("C2_TYPE", "NOTIFICATION");
		return m;
	}
	
	public static NamedPropertyMessage createC2Request(String name){
		NamedPropertyMessage npm = new NamedPropertyMessage(name);
		npm.addParameter("C2_TYPE", "REQUEST");
		return npm;
	}

	public static NamedPropertyMessage createC2Notification(String name){
		NamedPropertyMessage npm = new NamedPropertyMessage(name);
		npm.addParameter("C2_TYPE", "NOTIFICATION");
		return npm;
	}
	
	public static boolean isC2Request(Message m){
		if(!(m instanceof NamedPropertyMessage)){
			return false;
		}
		
		NamedPropertyMessage npm = (NamedPropertyMessage)m;
		String c2Type = (String)npm.getParameter("C2_TYPE");
		if(c2Type == null){
			return false;
		}
		else if(c2Type.equals("REQUEST")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static boolean isC2Notification(Message m){
		if(!(m instanceof NamedPropertyMessage)){
			return false;
		}
		
		NamedPropertyMessage npm = (NamedPropertyMessage)m;
		String c2Type = (String)npm.getParameter("C2_TYPE");
		if(c2Type == null){
			return false;
		}
		else if(c2Type.equals("NOTIFICATION")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static Weld createC2Weld(Identifier topId, Identifier bottomId){
		return new SimpleWeld(
			new BrickInterfaceIdPair(topId, AbstractC2Brick.BOTTOM_INTERFACE_ID),
			new BrickInterfaceIdPair(bottomId, AbstractC2Brick.TOP_INTERFACE_ID)
		);
	}
}

