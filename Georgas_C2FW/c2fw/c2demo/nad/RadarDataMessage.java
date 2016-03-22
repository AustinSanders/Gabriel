package c2demo.nad;

import c2.fw.*;

public class RadarDataMessage extends NamedPropertyMessage{
	public RadarDataMessage(String entityID, int entityType, int worldLocationX, int worldLocationY){
		super("RadarDataMessage");
		super.addParameter("entityID", entityID);
		super.addParameter("entityType", entityType);
		super.addParameter("worldLocationX", worldLocationX);
		super.addParameter("worldLocationY", worldLocationY);
	}

	protected RadarDataMessage(RadarDataMessage copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new RadarDataMessage(this);
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

	public void setWorldLocationX(int worldLocationX){
		addParameter("worldLocationX", worldLocationX);
	}

	public int getWorldLocationX(){
		return getIntParameter("worldLocationX");
	}

	public void setWorldLocationY(int worldLocationY){
		addParameter("worldLocationY", worldLocationY);
	}

	public int getWorldLocationY(){
		return getIntParameter("worldLocationY");
	}

}
