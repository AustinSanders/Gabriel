
package c2demo.nad;

import java.awt.*;
import java.awt.geom.*;

public class RadarArc implements DrawableThing{
	private int radius;
	private int startAngle;
	private int extentAngle;
	
	public RadarArc(int radius, int startAngle, int extentAngle){
		this.radius = radius;
		this.startAngle = startAngle;
		this.extentAngle = extentAngle;
	}
	
	public void draw(Graphics2D g2d, int x, int y, double scalingFactor){
		double drawRadius = ((double)radius) * scalingFactor;
		double drawDiameter = drawRadius * 2;

		//System.out.println("Drawing start: " + startAngle + ", extent= " + extentAngle);
		
		int sa = startAngle;
		sa = 360 - startAngle;
		sa +=  90;
		int ea = -extentAngle;
		g2d.setPaint(Color.gray);
		Arc2D.Double radarArc = new Arc2D.Double(x, y, (int)drawDiameter, (int)drawDiameter, sa,
			ea, Arc2D.PIE);
		g2d.draw(radarArc);
			
		g2d.setPaint(Color.blue);
		Arc2D.Double edgeArc = new Arc2D.Double(x, y, (int)drawDiameter, (int)drawDiameter, sa,
			0, Arc2D.PIE);
		g2d.draw(edgeArc);
		
		//System.out.println("draw call: " + x + " " + y + " " + scalingFactor);
		//System.out.println("drawing RC at : " + x + " " + y + " " + drawDiameter);
		//Ellipse2D.Double radarCircle = new Ellipse2D.Double(x, y, (int)drawDiameter, (int)drawDiameter);		
		//g2d.draw(radarCircle);
	}
	

}