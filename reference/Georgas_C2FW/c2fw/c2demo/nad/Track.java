package c2demo.nad;

import c2.fw.*;

public class Track extends NamedPropertyMessage{
	public Track(String entityID){
		super("Track");
		super.addParameter("entityID", entityID);
	}
	
	public Track(String entityID, int entityType, int worldX, int worldY, int heading, int affiliation, String[] annotations){
		super("Track");
		super.addParameter("entityID", entityID);
		super.addParameter("entityType", entityType);
		super.addParameter("worldX", worldX);
		super.addParameter("worldY", worldY);
		super.addParameter("heading", heading);
		super.addParameter("affiliation", affiliation);
		super.addParameter("annotations", annotations);
	}

	protected Track(Track copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new Track(this);
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

	public void setWorldX(int worldX){
		addParameter("worldX", worldX);
	}

	public int getWorldX(){
		return getIntParameter("worldX");
	}

	public void setWorldY(int worldY){
		addParameter("worldY", worldY);
	}

	public int getWorldY(){
		return getIntParameter("worldY");
	}

	public void setGuessedWorldX(int worldX){
		addParameter("guessedWorldX", worldX);
	}
	
	public int getGuessedWorldX(){
		return getIntParameter("guessedWorldX");
	}
	
	public void setGuessedWorldY(int guessedWorldY){
		addParameter("guessedWorldY", guessedWorldY);
	}
	
	public int getGuessedWorldY(){
		return getIntParameter("guessedWorldY");
	}
	
	public void setHeading(int heading){
		addParameter("heading", heading);
	}

	public int getHeading(){
		return getIntParameter("heading");
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

