package c2demo.nad;

import c2.fw.*;

public class CreateEntityMessage extends NamedPropertyMessage{
	public CreateEntityMessage(String entityID, int entityType, int[] waypointXs, int[] waypointYs, int speed, boolean terminate, int affiliation, String[] annotations){
		super("CreateEntityMessage");
		super.addParameter("entityID", entityID);
		super.addParameter("entityType", entityType);
		super.addParameter("waypointXs", waypointXs);
		super.addParameter("waypointYs", waypointYs);
		super.addParameter("speed", speed);
		super.addParameter("terminate", terminate);
		super.addParameter("affiliation", affiliation);
		super.addParameter("annotations", annotations);
	}

	protected CreateEntityMessage(CreateEntityMessage copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new CreateEntityMessage(this);
	}

	public void setEntityID(String entityID){
		addParameter("entityID", entityID);
	}

	public String getEntityID(){
		return (String)getParameter("entityID");
	}

	public void setEntityType(int entityType){
		addParameter("entityType", entityType);
	}

	public int getEntityType(){
		return getIntParameter("entityType");
	}

	public void setWaypointXs(int[] waypointXs){
		addParameter("waypointXs", waypointXs);
	}

	public int[] getWaypointXs(){
		return (int[])getParameter("waypointXs");
	}

	public void setWaypointYs(int[] waypointYs){
		addParameter("waypointYs", waypointYs);
	}

	public int[] getWaypointYs(){
		return (int[])getParameter("waypointYs");
	}

	public void setSpeed(int speed){
		addParameter("speed", speed);
	}

	public int getSpeed(){
		return getIntParameter("speed");
	}

	public void setTerminate(boolean terminate){
		addParameter("terminate", terminate);
	}

	public boolean getTerminate(){
		return getBooleanParameter("terminate");
	}
	
	public void setAffiliation(int affiliation){
		addParameter("affiliation", affiliation);
	}

	public int getAffiliation(){
		return getIntParameter("affiliation");
	}

	public void setAnnotations(String[] annotations){
		addParameter("annotations", annotations);
	}

	public String[] getAnnotations(){
		return (String[])getParameter("annotations");
	}

}

