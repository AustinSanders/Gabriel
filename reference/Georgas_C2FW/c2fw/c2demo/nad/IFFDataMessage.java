package c2demo.nad;

import c2.fw.*;

public class IFFDataMessage extends NamedPropertyMessage{
	public IFFDataMessage(String entityID, int affiliation){
		super("IFFDataMessage");
		super.addParameter("entityID", entityID);
		super.addParameter("affiliation", affiliation);
	}

	protected IFFDataMessage(IFFDataMessage copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new IFFDataMessage(this);
	}

	public void setEntityID(String entityID){
		addParameter("entityID", entityID);
	}

	public String getEntityID(){
		return (String)getParameter("entityID");
	}

	public void setAffiliation(int affiliation){
		addParameter("affiliation", affiliation);
	}

	public int getAffiliation(){
		return getIntParameter("affiliation");
	}

}

