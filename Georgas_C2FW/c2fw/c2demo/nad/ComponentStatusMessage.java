package c2demo.nad;

import c2.fw.*;

public class ComponentStatusMessage extends NamedPropertyMessage{
	
	public static final String STATUS_OK = "OK";
	public static final String STATUS_REMOVED = "Removed";
	
	public ComponentStatusMessage(String componentID, String status, int deadlineInterval){
		super("ComponentStatusMessage");
		super.addParameter("componentID", componentID);
		super.addParameter("status", status);
		super.addParameter("deadlineInterval", deadlineInterval);
	}

	protected ComponentStatusMessage(ComponentStatusMessage copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new ComponentStatusMessage(this);
	}

	public void setComponentID(String componentID){
		addParameter("componentID", componentID);
	}

	public String getComponentID(){
		return (String)getParameter("componentID");
	}

	public void setStatus(String status){
		addParameter("status", status);
	}

	public String getStatus(){
		return (String)getParameter("status");
	}

	public void setDeadlineInterval(int deadlineInterval){
		addParameter("deadlineInterval", deadlineInterval);
	}

	public int getDeadlineInterval(){
		return getIntParameter("deadlineInterval");
	}

}

