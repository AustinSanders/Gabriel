package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RadarControllerComponent extends AbstractC2DelegateBrick{

	//The main application window
	protected RadarControllerFrame rcFrame = null;

	public RadarControllerComponent(Identifier id){
		super(id);
		addLifecycleProcessor(new RadarControllerLifecycleProcessor());
	}

	class RadarControllerLifecycleProcessor extends LifecycleAdapter{
		
		public void begin(){
			newWindow();
		}
		
		public void end(){
			if(rcFrame != null){
				rcFrame.setVisible(false);
				rcFrame.dispose();
			}
		}
	}

	public void newWindow(){
		//This makes sure we only have one active window open.
		if(rcFrame == null){
			rcFrame = new RadarControllerFrame();
		}
		else{
			rcFrame.requestFocus();
		}
	}
	
	
	class RadarControllerFrame extends JFrame{
		public RadarControllerFrame(){
			super("AWACS Radar Controller");
			init();
		}
		
		//This is pretty standard Swing GUI stuff in Java.
		private void init(){
			Toolkit tk = getToolkit();
			Dimension screenSize = tk.getScreenSize();
			double xSize = (300);
			double ySize = (100);
			double xPos = (screenSize.getWidth() * 0.70);
			double yPos = (screenSize.getHeight() * 0.50);

			JButton bHaltRadar = new JButton("Halt");
			bHaltRadar.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						NamedPropertyMessage npm = new NamedPropertyMessage("RadarControl");
						npm.addParameter("radarSpeed", 0);
						sendToAll(npm, topIface);
					}
				}
			);
					
			JButton bResumeRadar = new JButton("Resume");
			bResumeRadar.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						NamedPropertyMessage npm = new NamedPropertyMessage("RadarControl");
						npm.addParameter("radarSpeed", 6);
						sendToAll(npm, topIface);
					}
				}
			);
			
			JButton bClockwise = new JButton("Clockwise");
			bClockwise.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						NamedPropertyMessage npm = new NamedPropertyMessage("RadarControl");
						npm.addParameter("radarAngleChange", 10);
						sendToAll(npm, topIface);
					}
				}
			);
			
			JButton bCounterclockwise = new JButton("Counter-Clockwise");
			bCounterclockwise.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						NamedPropertyMessage npm = new NamedPropertyMessage("RadarControl");
						npm.addParameter("radarAngleChange", -10);
						sendToAll(npm, topIface);
					}
				}
			);
			
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			mainPanel.add(bHaltRadar);
			mainPanel.add(bResumeRadar);
			mainPanel.add(bClockwise);
			mainPanel.add(bCounterclockwise);
			
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add("Center", mainPanel);

			setVisible(true);
			setSize((int)xSize, (int)ySize);
			setLocation((int)xPos, (int)yPos);
			setVisible(true);
			paint(getGraphics());
		}
		
	}

}
