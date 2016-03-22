package c2demo.nad;

import c2.fw.*;

public class RemoveEntityMessage extends NamedPropertyMessage{
	public RemoveEntityMessage(String entityID){
		super("RemoveEntityMessage");
		super.addParameter("entityID", entityID);
	}

	protected RemoveEntityMessage(RemoveEntityMessage copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new RemoveEntityMessage(this);
	}

	public void setEntityID(String entityID){
		addParameter("entityID", entityID);
	}

	public String getEntityID(){
		return (String)getParameter("entityID");
	}

}

