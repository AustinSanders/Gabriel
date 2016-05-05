package c2demo.nad;

import c2.fw.*;

public class AnnotationDataMessage extends NamedPropertyMessage{
	public AnnotationDataMessage(String entityID, String[] annotations){
		super("AnnotationDataMessage");
		super.addParameter("entityID", entityID);
		super.addParameter("annotations", annotations);
	}

	protected AnnotationDataMessage(AnnotationDataMessage copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new AnnotationDataMessage(this);
	}

	public void setEntityID(String entityID){
		addParameter("entityID", entityID);
	}

	public String getEntityID(){
		return (String)getParameter("entityID");
	}

	public void setAnnotations(String[] annotations){
		addParameter("annotations", annotations);
	}

	public String[] getAnnotations(){
		return (String[])getParameter("annotations");
	}

}

