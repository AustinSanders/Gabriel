package c2demo.nad;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

public class DrawableTrack implements DrawableThing, java.io.Serializable{
	private Track t;
	private ImageIcon fillIm;
	private ImageIcon im;
	private boolean grayedOut;
	private Font annotationFont;
	//private GrayFilter grayFilter;
	
	private static final int STANDARD_SIZE = 20;
	
	public DrawableTrack(Track t, ImageIcon fillIm, ImageIcon im, boolean grayedOut){
		this.t = t;
		this.fillIm = fillIm;
		this.im = im;
		this.grayedOut = grayedOut;
		
		this.annotationFont = new Font("SansSerif", Font.PLAIN, 10);
		//grayFilter = new GrayFilter(true, 50);
	}
	
	public void draw(Graphics2D g2d, int x, int y, double scalingFactor){
		int xsize = STANDARD_SIZE;
		int ysize = STANDARD_SIZE;
		
		Image fillImage = fillIm.getImage();
		Image entityImage = im.getImage();
		
		int actualFillWidth = fillImage.getWidth(null);
		int actualFillHeight = fillImage.getHeight(null);
		
		int actualEntityWidth = entityImage.getWidth(null);
		int actualEntityHeight = entityImage.getHeight(null);
		
		double scaledFillFactor =  (double)STANDARD_SIZE / (double)actualFillHeight;
		double scaledEntityFactor = (double)STANDARD_SIZE / (double)actualEntityHeight ;
		
		int drawFillWidth = (int)((double)actualFillWidth * scaledFillFactor);
		int drawFillHeight = (int)((double)actualFillHeight * scaledFillFactor);
		
		int drawEntityWidth = (int)((double)actualEntityWidth * scaledEntityFactor);
		int drawEntityHeight = (int)((double)actualEntityHeight * scaledEntityFactor);
		
		//ImageIcon scaledFillIm = new ImageIcon(fillIm.getImage().getScaledInstance(STANDARD_SIZE, STANDARD_SIZE, SCALE_DEFAULT));
		//ImageIcon scaledIm = new ImageIcon(im.getImage().getScaledInstance(STANDARD_SIZE, STANDARD_SIZE, SCALE_DEFAULT));
		
		/*
		System.out.println("FILLIM" + fillIm);
		if(fillIm == null){
			System.out.println(t);
		}
		*/
		
		int affiliation = Constants.UNKNOWN;
		try{
			affiliation = t.getAffiliation();
		}
		catch(IllegalArgumentException e){}
		
		String[] annotations = t.getAnnotations();
		
		if(!grayedOut){
			//g2d.drawImage(fillIm.getImage(), x, y, xsize, ysize, null);
			//g2d.drawImage(im.getImage(), x, y, xsize, ysize, null);
			g2d.drawImage(fillIm.getImage(), x - (drawEntityWidth / 2), y - (drawEntityHeight / 2), drawFillWidth, drawFillHeight, null);
			g2d.drawImage(im.getImage(), x - (drawEntityWidth / 2), y - (drawEntityHeight / 2), drawEntityWidth, drawEntityHeight, null);
			
			if(annotations != null){
				int heightOffset = 0;
				g2d.setFont(annotationFont);
				if(affiliation == Constants.FRIENDLY){
					g2d.setPaint(Color.blue);
				}
				else if(affiliation == Constants.UNKNOWN){
					g2d.setPaint(Color.lightGray);
				}
				else if(affiliation == Constants.HOSTILE){
					g2d.setPaint(Color.red);
				}
				
				for(int i = 0; i < annotations.length; i++){
					String a = annotations[i];
					g2d.drawString(a, x + drawEntityWidth + 2 - (drawEntityWidth / 2), y + heightOffset);
					Rectangle2D r2d = annotationFont.getStringBounds(a, g2d.getFontRenderContext());
					heightOffset += r2d.getHeight();
				}
			}
		}
		else{
			/*BufferedImage grayFillImage = new BufferedImage(xsize, ysize, BufferedImage.TYPE_BYTE_INDEXED);
			BufferedImage grayEntityImage = new BufferedImage(xsize, ysize, BufferedImage.TYPE_BYTE_INDEXED);
			
			Graphics2D gfig2d = grayFillImage.createGraphics();
			gfig2d.drawImage(fillIm.getImage(), 0, 0, xsize, ysize, null);
			
			Graphics2D geig2d = grayFillImage.createGraphics();
			geig2d.drawImage(im.getImage(), 0, 0, xsize, ysize, null);
			
			Image gfi = grayFillImage;
			Image gei = grayEntityImage;
			//Image gfi = grayFilter.createDisabledImage(grayFillImage);
			//Image gei = grayFilter.createDisabledImage(grayEntityImage);
			
			g2d.drawImage(gfi, x, y, xsize, ysize, null);
			g2d.drawImage(gei, x, y, xsize, ysize, null);
			*/
			
			//g2d.drawImage(fillIm.getImage(), x, y, xsize, ysize, null);
			//g2d.drawImage(im.getImage(), x, y, xsize, ysize, null);
			g2d.drawImage(fillIm.getImage(), x - (drawEntityWidth / 2), y - (drawEntityHeight / 2), drawFillWidth, drawFillHeight, null);
			g2d.drawImage(im.getImage(), x - (drawEntityWidth / 2), y - (drawEntityHeight / 2), drawEntityWidth, drawEntityHeight, null);

			//g2d.drawImage(fillIm.getImage(), x, y, drawFillWidth, drawFillHeight, null);
			//g2d.drawImage(im.getImage(), x, y, drawEntityWidth, drawEntityHeight, null);
			
			g2d.setPaint(Color.darkGray);
			//g2d.drawRoundRect(x, y, drawEntityWidth, drawEntityHeight, 5, 5);
			g2d.drawRoundRect(x - (drawEntityWidth / 2), y - (drawEntityHeight / 2), drawEntityWidth, drawEntityHeight, 5, 5);

			g2d.setPaint(Color.black);
			for(int i = 0; i < drawEntityWidth; i+=3){
				for(int j = 0; j < drawEntityHeight; j+=3){
					g2d.drawRect(x+i - (drawEntityWidth / 2), y+j - (drawEntityHeight / 2), 1, 1);
				}
			}
			g2d.setPaint(Color.black);
		}
		
		int arcHeading = 360-t.getHeading();
		arcHeading += 90;
		
		Arc2D.Double radarArc = new Arc2D.Double(x-5 - (drawEntityWidth / 2), y-5 - (drawEntityHeight / 2), (int)drawEntityWidth+10, (int)drawEntityHeight+10, arcHeading,
			0, Arc2D.PIE);
		g2d.setPaint(Color.white);
		g2d.draw(radarArc);
	}

}
