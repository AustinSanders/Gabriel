package c2.fw;

import c2.fw.*;

public class ShutdownArchMessage extends NamedPropertyMessage{
	
	/** Shutdown in an ordered fashion */
	public static final int SHUTDOWN_NORMAL = 100;
	
	/** Shutdown as quickly as possible, possibly avoiding
	    calling end() and stop() routines. */
	public static final int SHUTDOWN_NOW = 200;
	
	public ShutdownArchMessage(int returnCode, int shutdownType, String reason){
		super("ShutdownArchMessage");
		super.addParameter("returnCode", returnCode);
		super.addParameter("shutdownType", shutdownType);
		super.addParameter("reason", reason);
	}

	protected ShutdownArchMessage(ShutdownArchMessage copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new ShutdownArchMessage(this);
	}

	public void setReturnCode(int returnCode){
		addParameter("returnCode", returnCode);
	}

	public int getReturnCode(){
		return getIntParameter("returnCode");
	}

	public void setShutdownType(int shutdownType){
		addParameter("shutdownType", shutdownType);
	}

	public int getShutdownType(){
		return getIntParameter("shutdownType");
	}

	public void setReason(String reason){
		addParameter("reason", reason);
	}

	public String getReason(){
		return (String)getParameter("reason");
	}

}


