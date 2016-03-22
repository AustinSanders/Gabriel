package c2demo.nad;

import java.awt.*;

public interface DrawableThing extends java.io.Serializable{
	
	public void draw(Graphics2D g2d, int x, int y, double scalingFactor);

}
