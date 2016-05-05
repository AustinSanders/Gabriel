package c2demo.nad;

import java.awt.*;
import java.awt.geom.*;

public class RadarCircle implements DrawableThing{

	private int radius;
	
	public RadarCircle(int radius){
		this.radius = radius;
	}
	
	public void draw(Graphics2D g2d, int x, int y, double scalingFactor){
		//System.out.println("draw call: " + x + " " + y + " " + scalingFactor);
		double drawRadius = ((double)radius) * scalingFactor;
		double drawDiameter = drawRadius * 2;
		//System.out.println("drawing RC at : " + x + " " + y + " " + drawDiameter);
		Ellipse2D.Double radarCircle = new Ellipse2D.Double(x, y, (int)drawDiameter, (int)drawDiameter);		
		g2d.setPaint(Color.cyan);
		g2d.draw(radarCircle);
	}
	

}
