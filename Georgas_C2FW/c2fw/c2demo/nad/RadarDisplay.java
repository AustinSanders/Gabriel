package c2demo.nad;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RadarDisplay extends JComponent{
	
	private LocatedDrawableThing[] ldts;
	
	private int screenWidth;
	private int screenHeight;
	
	private int viewWidth = WorldParameters.WORLD_WIDTH;
	private int viewHeight = WorldParameters.WORLD_HEIGHT;
	
	public void updateDisplay(LocatedDrawableThing[] ldts){
		this.ldts = ldts;
		repaint();
	}
	
	public void setViewSize(int width, int height, double zoom){
		this.viewWidth = width;
		this.viewHeight = height;
	}
	
	public void updateSize(){
		this.screenWidth = getSize().width;
		this.screenHeight = getSize().height;
		if(screenWidth < screenHeight){
			screenHeight = screenWidth;
		}
		else{
			screenWidth = screenHeight;
		}
	}
	
	public int viewXtoScreenX(int viewX){
		double proportion = ((double)screenWidth) / ((double)viewWidth);
		//System.out.println("proportion = " + proportion);
		return (int)(((double)viewX) * proportion);
	}
	
	public int viewYtoScreenY(int viewY){
		double proportion = ((double)screenHeight) / ((double)viewHeight);
		return (int)(((double)viewY) * proportion);
	}
	
	public void paintComponent(Graphics g){
		clear(g);
		updateSize();
		Graphics2D g2d = (Graphics2D)g;
		g.setClip(0, 0, screenWidth, screenHeight);
		if(ldts == null){
			return;
		}
		double proportion = ((double)screenWidth) / ((double)viewWidth);
		for(int i = 0; i < ldts.length; i++){
			/*
			System.out.println("Drawing thing: " + 
				viewXtoScreenX(ldts[i].getX()) + " " + 
				viewYtoScreenY(ldts[i].getY()) + " " +
				ldts[i].getScale() * proportion);
			*/
			ldts[i].getDrawableThing().draw(g2d, 
				viewXtoScreenX((int)(ldts[i].getX())),
				viewYtoScreenY((int)(ldts[i].getY())),
				ldts[i].getScale() * proportion
				);
		}				
	}
	
	public void clear(Graphics g){
		super.paintComponent(g);
		//g.setBackground(Color.black);
		this.setBackground(Color.black);
		//g.fillRect(0, 0, getWidth, getHeight())
	}

}
