package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;
import java.awt.*;
import javax.swing.*;

public class MetadataArtistC2Component extends AbstractC2DelegateBrick{
	
	public MetadataArtistC2Component(Identifier id){
		super(id);
		addMessageProcessor(new MetadataArtistMessageHandler());
	}
	
	class MetadataArtistMessageHandler implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof RadarMetadataMessage){
				RadarMetadataMessage rmm = (RadarMetadataMessage)m;
				int direction = rmm.getDirection();
				int widthInDegrees = rmm.getWidthInDegrees();
				int dir2 = /*direction + */widthInDegrees;
				//System.out.println("Direction is supposed to be: " + rmm.getDirection());
				RadarArc ra = new RadarArc(4900, direction, dir2);
				LocatedDrawableThing ldt = new LocatedDrawableThing("RADAR_ARC",
					100, 100, ra);
				LocatedDrawableThing[] arr = new LocatedDrawableThing[]{ldt};
				sendToAll(new DrawableThingSetMessage(arr), bottomIface);
			}
		}
	}

}
