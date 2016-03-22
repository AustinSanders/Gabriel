package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import edu.uci.ics.widgets.*;

public class RadarDisplayC2Component extends AbstractC2DelegateBrick{
	
	private RadarDisplayFrame frame;
	
	public RadarDisplayC2Component(Identifier id){
		super(id);
		frame = new RadarDisplayFrame(id.toString());
		this.addMessageProcessor(new RadarDisplayMessageProcessor());
	}

	class RadarDisplayMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof DrawableThingSetMessage){
				DrawableThingSetMessage dtsm = (DrawableThingSetMessage)m;
				LocatedDrawableThing[] ldts = dtsm.getLocatedDrawableThings();
				frame.render(ldts);
			}
		}
	}
	
	class RadarDisplayFrame extends JFrame{
		private RadarDisplay radarDisplay;
		
		public RadarDisplayFrame(String name){
			super(name);
			init();
		}
		
		private void init(){
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().setBackground(Color.black);
			
			this.radarDisplay = new RadarDisplay();
			this.getContentPane().add("Center", radarDisplay);
			
			this.setSize(500, 500);
			this.setVisible(true);
			this.setLocation(100, 100);
			this.validate();
			this.repaint();
		}
		
		public void render(LocatedDrawableThing[] ldts){
			radarDisplay.updateDisplay(ldts);
		}
		
	}
	
	
	
}
