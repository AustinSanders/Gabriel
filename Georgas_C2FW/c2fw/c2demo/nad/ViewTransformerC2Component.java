package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;

public class ViewTransformerC2Component extends AbstractC2DelegateBrick{

	private int viewX;
	private int viewY;
	private double zoom;
	
	private DrawableThingSetMessage lastDtsm = null;
	
	public ViewTransformerC2Component(Identifier id){
		super(id);
		this.addLifecycleProcessor(new ViewTransformerLifecycleProcessor());
		this.addMessageProcessor(new ViewTransformerMessageProcessor());
	}
	
	class ViewTransformerLifecycleProcessor extends LifecycleAdapter{
		public void init(){
			viewX = 000;
			viewY = 0;
			zoom = 1.0d;
			new ZoomWidget();
		}
	}
	
	class ViewTransformerMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof DrawableThingSetMessage){
				DrawableThingSetMessage dtms = (DrawableThingSetMessage)m;
				LocatedDrawableThing[] ldts = dtms.getLocatedDrawableThings();
				LocatedDrawableThing[] transformedLdts = new LocatedDrawableThing[ldts.length];
				for(int i = 0; i < ldts.length; i++){
					LocatedDrawableThing transformedLdt = ldts[i].duplicate();
					transformedLdt.setScale(zoom);
					
					int newX = (int)((ldts[i].getX() - viewX) * zoom);
					int newY = (int)((ldts[i].getY() - viewY) * zoom);
					transformedLdt.setX(newX);
					transformedLdt.setY(newY);
					transformedLdts[i] = transformedLdt;
				}
				DrawableThingSetMessage transformedDtsm = new DrawableThingSetMessage(transformedLdts);
				//System.out.println("Sending transformed: " + transformedDtms);
				lastDtsm = transformedDtsm;
				sendToAll(transformedDtsm, bottomIface);
			}
		}
	}
	
	class ZoomWidget extends JFrame{
		public ZoomWidget(){
			super("ZoomWidget " + getIdentifier());
			getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));
			
			JButton leftButton = new JButton("Left");
			leftButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						double delta = (double)100 * (double)zoom;
						viewX -= (int)delta;
						if(lastDtsm != null){
							sendToAll(lastDtsm, bottomIface);
						}
					}
				}
			);
					
			JButton rightButton = new JButton("Right");
			rightButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						double delta = (double)100 * (double)zoom;
						viewX += (int)delta;
						if(lastDtsm != null){
							sendToAll(lastDtsm, bottomIface);
						}
					}
				}
			);

			JButton downButton = new JButton("Down");
			downButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						double delta = (double)100 * (double)zoom;
						viewY += (int)delta;
						if(lastDtsm != null){
							sendToAll(lastDtsm, bottomIface);
						}
					}
				}
			);

			JButton upButton = new JButton("Up");
			upButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						double delta = (double)100 * (double)zoom;
						viewY -= (int)delta;
						if(lastDtsm != null){
							sendToAll(lastDtsm, bottomIface);
						}
					}
				}
			);

			JButton inButton = new JButton("ZoomIn");
			inButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						zoom += 0.10d;
						if(lastDtsm != null){
							sendToAll(lastDtsm, bottomIface);
						}
					}
				}
			);
			
			JButton outButton = new JButton("ZoomOut");
			outButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						zoom -= 0.10d;
						if(zoom < 1.0d){
							zoom = 1.0d;
						}
						if(lastDtsm != null){
							sendToAll(lastDtsm, bottomIface);
						}
					}
				}
			);

			JButton worldButton = new JButton("World");
			worldButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						zoom = 1.0d;
						viewX = 0;
						viewY = 0;
						if(lastDtsm != null){
							sendToAll(lastDtsm, bottomIface);
						}
					}
				}
			);
			
			getContentPane().add(leftButton);
			getContentPane().add(rightButton);
			getContentPane().add(upButton);
			getContentPane().add(downButton);
			getContentPane().add(inButton);
			getContentPane().add(outButton);
			getContentPane().add(worldButton);
			
			this.setSize(300, 100);
			this.setVisible(true);
		}
	}
	
}
		
