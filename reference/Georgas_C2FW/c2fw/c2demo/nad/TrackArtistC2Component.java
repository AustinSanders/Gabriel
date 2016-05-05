package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;

import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.*;

import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TrackArtistC2Component extends AbstractC2DelegateBrick{
	
	private static boolean inited = false;
	
	private static Map friendlyMap = new HashMap();
	private static Map unknownMap = new HashMap();
	private static Map enemyMap = new HashMap();
	
	public TrackArtistC2Component(Identifier id){
		super(id);
		staticinit();
		addMessageProcessor(new TrackArtistMessageProcessor());
	}

	public static void staticinit(){
		synchronized(TrackArtistC2Component.class){
			if(inited){
				return;
			}
			
			String[] imageSetPrefixes = new String[]{"fr","nk","en"};
			Map[] maps = new Map[]{friendlyMap, unknownMap, enemyMap};
			
			for(int i = 0; i < imageSetPrefixes.length; i++){
				Map theMap = maps[i];

				String imageSetName;
				imageSetName = imageSetPrefixes[i] + "air";
				
				theMap.put("air" + Constants.IMINDEX_AIR_FILL, 
					getApp6aImage(imageSetName, Constants.IMINDEX_AIR_FILL, true));					
				theMap.put("air" + Constants.IMINDEX_AIR_ASM, 
					getApp6aImage(imageSetName, Constants.IMINDEX_AIR_ASM, false));
				theMap.put("air" + Constants.IMINDEX_AIR_SAM, 
					getApp6aImage(imageSetName, Constants.IMINDEX_AIR_SAM, false));
				theMap.put("air" + Constants.IMINDEX_AIR_AAM, 
					getApp6aImage(imageSetName, Constants.IMINDEX_AIR_AAM, false));
				theMap.put("air" + Constants.IMINDEX_AIR_ROTORCRAFT, 
					getApp6aImage(imageSetName, Constants.IMINDEX_AIR_ROTORCRAFT, false));
				theMap.put("air" + Constants.IMINDEX_AIR_FIXEDWING, 
					getApp6aImage(imageSetName, Constants.IMINDEX_AIR_FIXEDWING, false));
				theMap.put("air" + Constants.IMINDEX_AIR_FIXEDWINGBOMBER, 
					getApp6aImage(imageSetName, Constants.IMINDEX_AIR_FIXEDWINGBOMBER, false));
				theMap.put("air" + Constants.IMINDEX_AIR_STEALTH, 
					getApp6aImage(imageSetName, Constants.IMINDEX_AIR_STEALTH, false));

				imageSetName = imageSetPrefixes[i] + "land";
				theMap.put("land" + Constants.IMINDEX_LAND_FILL, 
					getApp6aImage(imageSetName, Constants.IMINDEX_LAND_FILL, true));
				theMap.put("land" + Constants.IMINDEX_LAND_ARMOR, 
					getApp6aImage(imageSetName, Constants.IMINDEX_LAND_ARMOR, false));
				theMap.put("land" + Constants.IMINDEX_LAND_TROUPS, 
					getApp6aImage(imageSetName, Constants.IMINDEX_LAND_TROUPS, false));

				imageSetName = imageSetPrefixes[i] + "sea";
				theMap.put("sea" + Constants.IMINDEX_SEA_FILL, 
					getApp6aImage(imageSetName, Constants.IMINDEX_SEA_FILL, true));
				theMap.put("sea" + Constants.IMINDEX_SEA_BATTLESHIP, 
					getApp6aImage(imageSetName, Constants.IMINDEX_SEA_BATTLESHIP, false));
				theMap.put("sea" + Constants.IMINDEX_SEA_DESTROYER, 
					getApp6aImage(imageSetName, Constants.IMINDEX_SEA_DESTROYER, false));
				theMap.put("sea" + Constants.IMINDEX_SEA_AMPHIBIOUS, 
					getApp6aImage(imageSetName, Constants.IMINDEX_SEA_AMPHIBIOUS, false));
				theMap.put("sea" + Constants.IMINDEX_SEA_MINESWEEPER, 
					getApp6aImage(imageSetName, Constants.IMINDEX_SEA_MINESWEEPER, false));
				theMap.put("sea" + Constants.IMINDEX_SEA_HOVERCRAFT, 
					getApp6aImage(imageSetName, Constants.IMINDEX_SEA_HOVERCRAFT, false));
				theMap.put("sea" + Constants.IMINDEX_SEA_CARRIER, 
					getApp6aImage(imageSetName, Constants.IMINDEX_SEA_CARRIER, false));
			}

			inited = true;
		}
	}
	
	
	private static ImageIcon getApp6aImage(String set, int imageNumber, boolean color){
		try{
			String resourcePath = "/c2demo/nad/res/" + set + ".zip";
			String imgPath = set + "/" + (color ? "color/" : "") + "char" + Integer.toHexString(imageNumber) + ".gif";
			
			ZipInputStream zis = new ZipInputStream(TrackArtistC2Component.class.getResourceAsStream(resourcePath));
			while(zis.available() == 1){
				ZipEntry ze = zis.getNextEntry();
				if (ze == null)
					break;
				if (!ze.getName().equals(imgPath))
					continue;
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte buf[] = new byte[2048];
				while(true){
					int len = zis.read(buf, 0, buf.length);
					if(len != -1)
						baos.write(buf, 0, len);
					else{
						zis.close();
						baos.flush();
						baos.close();
						//System.err.println(imgPath);
						return new ImageIcon(baos.toByteArray());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public DrawableTrack getDrawableTrack(Track t, boolean grayed){
		ImageIcon fillIm = null;
		ImageIcon entityIm = null;
		
		int entityType = t.getEntityType();
		int affiliation = Constants.UNKNOWN;
		try{
			affiliation = t.getAffiliation();
		}
		catch(IllegalArgumentException iae){}
		
		Map theMap = null;
		switch(affiliation){
		case Constants.FRIENDLY:
			theMap = friendlyMap;
			break;
		case Constants.UNKNOWN:
			theMap = unknownMap;
			break;
		case Constants.HOSTILE:
			theMap = enemyMap;
			break;
		}
		
		switch(entityType){
		case Constants.ENTITY_TYPE_AIR_FIXEDWING:
			fillIm = (ImageIcon)theMap.get("air" + Constants.IMINDEX_AIR_FILL);
			entityIm = (ImageIcon)theMap.get("air" + Constants.IMINDEX_AIR_FIXEDWING);
			break;
		case Constants.ENTITY_TYPE_AIR_FIXEDWINGBOMBER:
			fillIm = (ImageIcon)theMap.get("air" + Constants.IMINDEX_AIR_FILL);
			entityIm = (ImageIcon)theMap.get("air" + Constants.IMINDEX_AIR_FIXEDWINGBOMBER);
			break;
		case Constants.ENTITY_TYPE_AIR_STEALTH:
			fillIm = (ImageIcon)theMap.get("air" + Constants.IMINDEX_AIR_FILL);
			entityIm = (ImageIcon)theMap.get("air" + Constants.IMINDEX_AIR_STEALTH);
			break;			
		case Constants.ENTITY_TYPE_AIR_ROTORCRAFT:
			fillIm = (ImageIcon)theMap.get("air" + Constants.IMINDEX_AIR_FILL);
			entityIm = (ImageIcon)theMap.get("air" + Constants.IMINDEX_AIR_ROTORCRAFT);
			break;			
		case Constants.ENTITY_TYPE_AIR_SAM:
			fillIm = (ImageIcon)theMap.get("air" + Constants.IMINDEX_AIR_FILL);
			entityIm = (ImageIcon)theMap.get("air" + Constants.IMINDEX_AIR_SAM);
			break;			
		case Constants.ENTITY_TYPE_LAND_ARMOR:
			fillIm = (ImageIcon)theMap.get("land" + Constants.IMINDEX_LAND_FILL);
			entityIm = (ImageIcon)theMap.get("land" + Constants.IMINDEX_LAND_ARMOR);
			break;			
		case Constants.ENTITY_TYPE_LAND_TROUPS:
			fillIm = (ImageIcon)theMap.get("land" + Constants.IMINDEX_LAND_FILL);
			entityIm = (ImageIcon)theMap.get("land" + Constants.IMINDEX_LAND_TROUPS);
			break;			
		case Constants.ENTITY_TYPE_SEA_BATTLESHIP:
			fillIm = (ImageIcon)theMap.get("sea" + Constants.IMINDEX_SEA_FILL);
			entityIm = (ImageIcon)theMap.get("sea" + Constants.IMINDEX_SEA_BATTLESHIP);
			break;			
		case Constants.ENTITY_TYPE_SEA_CARRIER:
			fillIm = (ImageIcon)theMap.get("sea" + Constants.IMINDEX_SEA_FILL);
			entityIm = (ImageIcon)theMap.get("sea" + Constants.IMINDEX_SEA_CARRIER);
			break;			
		case Constants.ENTITY_TYPE_SEA_DESTROYER:
			fillIm = (ImageIcon)theMap.get("sea" + Constants.IMINDEX_SEA_FILL);
			entityIm = (ImageIcon)theMap.get("sea" + Constants.IMINDEX_SEA_DESTROYER);
			break;			
		case Constants.ENTITY_TYPE_SEA_HOVERCRAFT:
			fillIm = (ImageIcon)theMap.get("sea" + Constants.IMINDEX_SEA_FILL);
			entityIm = (ImageIcon)theMap.get("sea" + Constants.IMINDEX_SEA_HOVERCRAFT);
			break;			
		case Constants.ENTITY_TYPE_SEA_MINESWEEPER:
			fillIm = (ImageIcon)theMap.get("sea" + Constants.IMINDEX_SEA_FILL);
			entityIm = (ImageIcon)theMap.get("sea" + Constants.IMINDEX_SEA_MINESWEEPER);
			break;			
		}

		//System.out.println("Fill IM = " + fillIm);
		return new DrawableTrack(t, fillIm, entityIm, grayed);
	}
	
	class TrackArtistMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			//System.out.println("Artist Got message: " + m);
			if(m instanceof Track){
				Track t = (Track)m;
				
				DrawableTrack dt = getDrawableTrack((Track)m, false);
				
				try{
					int guessedWorldX = t.getGuessedWorldX();
					int guessedWorldY = t.getGuessedWorldY();
					
					int worldX = t.getWorldX();
					int worldY = t.getWorldY();
					
					if((guessedWorldX != worldX) || (guessedWorldY != worldY)){
						DrawableTrack gdt = getDrawableTrack((Track)m, true);
						LocatedDrawableThing[] ldtArray = new LocatedDrawableThing[2];
						ldtArray[0] = new LocatedDrawableThing(t.getEntityID(), t.getWorldX(), t.getWorldY(), dt);
						ldtArray[1] = new LocatedDrawableThing(t.getEntityID() + "_guess", guessedWorldX, guessedWorldY, gdt);
						
						DrawableThingSetMessage dtsm = new DrawableThingSetMessage(ldtArray);
						//System.out.println("Got track: " + t);
						sendToAll(dtsm, bottomIface);
						return;
					}
				}
				catch(IllegalArgumentException iae){}
				DrawableThingSetMessage dtsm = new DrawableThingSetMessage(
					new LocatedDrawableThing[]{
						new LocatedDrawableThing(t.getEntityID(), t.getWorldX(), t.getWorldY(), dt)
					}
				);
				//System.out.println("Got track: " + t);
				sendToAll(dtsm, bottomIface);
			}
			else if(m instanceof RemoveEntityMessage){
				sendToAll(m, bottomIface);
			}
		}
		
	}

}
