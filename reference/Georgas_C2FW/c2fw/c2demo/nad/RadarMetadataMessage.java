package c2demo.nad;

import c2.fw.*;

public class RadarMetadataMessage extends NamedPropertyMessage{
	public RadarMetadataMessage(int direction, int widthInDegrees){
		super("RadarMetadataMessage");
		super.addParameter("direction", direction);
		super.addParameter("widthInDegrees", widthInDegrees);
	}

	protected RadarMetadataMessage(RadarMetadataMessage copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new RadarMetadataMessage(this);
	}

	public void setDirection(int direction){
		addParameter("direction", direction);
	}

	public int getDirection(){
		return getIntParameter("direction");
	}

	public void setWidthInDegrees(int widthInDegrees){
		addParameter("widthInDegrees", widthInDegrees);
	}

	public int getWidthInDegrees(){
		return getIntParameter("widthInDegrees");
	}

}

