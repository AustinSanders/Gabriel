package c2demo.nad;

import c2.fw.*;

public class DrawableThingSetMessage extends NamedPropertyMessage{
	public DrawableThingSetMessage(LocatedDrawableThing[] locatedDrawableThings){
		super("DrawableThingSet");
		super.addParameter("locatedDrawableThings", locatedDrawableThings);
	}

	protected DrawableThingSetMessage(DrawableThingSetMessage copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new DrawableThingSetMessage(this);
	}

	public void setLocatedDrawableThings(LocatedDrawableThing[] locatedDrawableThings){
		addParameter("locatedDrawableThings", locatedDrawableThings);
	}

	public LocatedDrawableThing[] getLocatedDrawableThings(){
		return (LocatedDrawableThing[])getParameter("locatedDrawableThings");
	}

}

