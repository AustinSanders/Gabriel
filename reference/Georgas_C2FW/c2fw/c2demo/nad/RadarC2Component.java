package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;

import java.io.*;
import java.util.*;

public class RadarC2Component extends AbstractC2DelegateBrick{
	private static final int RADAR_WIDTH = 120;
	
	private RadarThread rt;
	
	private Map entitiesTracked = Collections.synchronizedMap(new HashMap());
	
	private boolean ending = false;
	private int radarSpeed = 6;
	private int radarAngle = 0;
	
	private boolean canTrackStealthAircraft = false;
	private boolean zapped = false;
	
	public RadarC2Component(Identifier id){
		super(id);
		addLifecycleProcessor(new RadarLifecycleProcessor());
		addMessageProcessor(new RadarControlMessageProcessor());
		addMessageProcessor(new CreateEntityMessageProcessor());
		addMessageProcessor(new ZapMessageProcessor());
	}
	
	public RadarC2Component(Identifier id, InitializationParameter[] ips){
		this(id);
		for(int i = 0; i < ips.length; i++){
			if(ips[i].getName().equals("canTrackStealth")){
				canTrackStealthAircraft = true;
			}
		}
	}
	
	protected void addEntityTracked(String id, int type, int[] wx, int[] wy, int spd, boolean terminate){
		synchronized(entitiesTracked){
			entitiesTracked.put(id, new EntityPath(id, type, wx, wy, spd, terminate));
		}
	}
	
	protected void removeEntityTracked(String id){
		synchronized(entitiesTracked){
			entitiesTracked.remove(id);
		}
	}
	
	class CreateEntityMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof CreateEntityMessage){
				CreateEntityMessage cem = (CreateEntityMessage)m;
				addEntityTracked(cem.getEntityID(), cem.getEntityType(),
					cem.getWaypointXs(), cem.getWaypointYs(), cem.getSpeed(), cem.getTerminate());
			}
		}
	}
	
	class RadarControlMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof NamedPropertyMessage){
				NamedPropertyMessage npm = (NamedPropertyMessage)m;
				if(npm.getName().equals("RadarControl")){
					try{
						int rs = npm.getIntParameter("radarSpeed");
						radarSpeed = rs;
					}
					catch(IllegalArgumentException e){}
					
					try{
						int rac = npm.getIntParameter("radarAngleChange");
						radarAngle += rac;
						if(radarAngle > 360){
							radarAngle = radarAngle % 360;
						}
						else if(radarAngle < 0){
							radarAngle = radarAngle + 360;
						}
					}
					catch(IllegalArgumentException e){}
				}
			}
		}
	}
	
	public void zap(){
		zapped = true;
		if(rt != null){
			rt.terminate();
		}
		writeRadarDataToFile();
	}
	
	class ZapMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof EMPZapMessage){
				zap();
			}
		}
	}
	
	public void writeRadarDataToFile(){
		try{
			File f = new File("radar-data.dat");
			f.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(radarSpeed);
			oos.writeInt(radarAngle);
			oos.writeObject(entitiesTracked);
			oos.close();
			fos.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	class RadarLifecycleProcessor extends LifecycleAdapter{
		public void init(){
			/*
			String id1 = "FA/18 100";
			int type1 = Constants.ENTITY_TYPE_AIR_FIXEDWING;
			int[] e1x = new int[]{7500, 9500, 9500, 7500};
			int[] e1y = new int[]{500, 500, 2500, 2500};
			//int[] e1x = new int[]{500, 5000};
			//int[] e1y = new int[]{100, 5000};
			
			addEntityTracked(id1, type1, e1x, e1y, 100);

			String id2 = "Cessna";
			int type2 = Constants.ENTITY_TYPE_AIR_FIXEDWING;
			//int[] e2x = new int[]{500, 2500, 2500, 500};
			//int[] e2y = new int[]{500, 500, 2500, 2500};
			int[] e2x = new int[]{500, 5000};
			int[] e2y = new int[]{100, 5000};
			
			addEntityTracked(id2, type2, e2x, e2y, 20);
			*/
		}
		
		public void begin(){
			//if(canTrackStealthAircraft){
				try{
					File f = new File("radar-data.dat");
					f.deleteOnExit();
					FileInputStream fis = new FileInputStream(f);
					ObjectInputStream ois = new ObjectInputStream(fis);
					radarSpeed = ois.readInt();
					radarAngle = ois.readInt();
					entitiesTracked = (Map)ois.readObject();
					ois.close();
					fis.close();
				}
				catch(FileNotFoundException fnfe){
				}
				catch(ClassNotFoundException cnfe){
					cnfe.printStackTrace();
				}
				catch(IOException e){
					e.printStackTrace();
				}
			//}
			//else{
			//	File f = new File("radar-data.dat");
			//	f.delete();
			//}
			
			rt = new RadarThread(Constants.TICK_INTERVAL_IN_MS);
			rt.start();
		}
		
		public void end(){
			ending = true;
			writeRadarDataToFile();
			if(rt != null)
			{
				rt.terminate();
			}
			try{
				Thread.sleep(Constants.TICK_INTERVAL_IN_MS);
			}
			catch(InterruptedException e){}
			
			sendStatusRemoving();
		}
	}
	
	public void stepRadarAngle(int degrees){
		//System.out.println("Radar angle at: " + radarAngle + "-" + (int)(radarAngle + 90));
		radarAngle += degrees;
		radarAngle = radarAngle % 360;
	}
	
	public boolean isVisibleOnRadarWorld(int worldX, int worldY){
		int minAngle = radarAngle;
		int maxAngle = radarAngle + RADAR_WIDTH;
		
		int targetAngle = Trigonometry.calculateAngleWorld(worldX, worldY);
		//System.out.println("Coord (" + worldX + "," + worldY + ") is supposedly at angle " + targetAngle);
		
		if(maxAngle > 360){
			if(targetAngle < 90){
				int ta2 = targetAngle + 360;
				if((ta2 > minAngle) && (ta2 < maxAngle)){
					return true;
				}
			}
		}
		return((targetAngle > minAngle) && (targetAngle < maxAngle));
	}
	
	public void sendRadarMetadata(){
		if(zapped){
			return;
		}
		RadarMetadataMessage rmm = new RadarMetadataMessage(radarAngle, RADAR_WIDTH);
		sendToAll(rmm, bottomIface);
	}
	
	public void sendDetectedEntities(){
		if(zapped){
			return;
		}
		synchronized(entitiesTracked){
			for(Iterator it = entitiesTracked.values().iterator(); it.hasNext(); ){
				EntityPath ep = (EntityPath)it.next();
				int epX = ep.getCurrentX();
				int epY = ep.getCurrentY();
				//System.out.println("Prepped entity: " + epX + ", " + epY);
				if(isVisibleOnRadarWorld(epX, epY)){
					//System.out.println("It's visible.");
					
					RadarDataMessage rdm = new RadarDataMessage(ep.getEntityID(),
						ep.getEntityType(), epX, epY);
					
					if(ep.getEntityID().indexOf("Stealth") != -1){
						if(!canTrackStealthAircraft){
							int r1 = (int)(Math.random() * 1000);
							int r2 = (int)(Math.random() * 1000);
							rdm = new RadarDataMessage(ep.getEntityID(),
								ep.getEntityType(), epX + r1, epY + r2);
						}
					}
							
					//System.out.println("Sending out plane: " + epX + "," + epY);
					sendToAll(rdm, bottomIface);
				}
			}
		}
	}
	
	public void sendHeartbeat(){
		if(zapped){
			return;
		}
		if(!ending){			
			ComponentStatusMessage csm = new ComponentStatusMessage(getIdentifier().toString(),
				ComponentStatusMessage.STATUS_OK, Constants.TICK_INTERVAL_IN_MS * 2);
			sendToAll(csm, bottomIface);
		}
	}
	
	public void sendStatusRemoving(){
		ComponentStatusMessage csm = new ComponentStatusMessage(getIdentifier().toString(),
			ComponentStatusMessage.STATUS_REMOVED, Constants.TICK_INTERVAL_IN_MS * 2);
		sendToAll(csm, bottomIface);
	}
	
	static class EntityPath implements java.io.Serializable{
		private String entityID;
		private int entityType;
		
		private int[] xWaypoints;
		private int[] yWaypoints;
		
		private int speed;
		
		private boolean terminate;
		
		private boolean missionDone = false;
		
		int lastWaypoint = 0;
		int nextWaypoint = 1;
		
		int xDiff, yDiff;
		int hypLength;
		
		int curX;
		int curY;
		
		int stepCount = 0;
		
		public EntityPath(String id, int entityType, int[] xWaypoints, int[] yWaypoints, int speed, boolean terminate){
			this.entityID = id;
			this.entityType = entityType;
			this.xWaypoints = xWaypoints;
			this.yWaypoints = yWaypoints;
			this.speed = speed;
			this.terminate = terminate;
			this.missionDone = false;
			
			setAtWaypoint(0);
		}
		
		public String getEntityID(){
			return entityID;
		}
		
		public int getEntityType(){
			return entityType;
		}
		
		public int getCurrentX(){
			return curX;
		}
		
		public int getCurrentY(){
			return curY;
		}
		
		private void setAtWaypoint(int waypoint){
			if(waypoint == (xWaypoints.length - 1)){
				if(terminate){
					missionDone = true;
				}
			}
			
			curX = xWaypoints[waypoint];
			curY = yWaypoints[waypoint];
			
			lastWaypoint = waypoint;
			nextWaypoint = (waypoint + 1) % xWaypoints.length;
			
			xDiff = xWaypoints[nextWaypoint] - xWaypoints[lastWaypoint];
			yDiff = yWaypoints[nextWaypoint] - yWaypoints[lastWaypoint];
			
			double dhl = Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
			
			hypLength = (int)dhl;
			stepCount = 0;
		}
		
		public void step(int numSteps){
			stepCount += numSteps;
			
			if(stepCount > hypLength){
				setAtWaypoint((lastWaypoint + 1) % xWaypoints.length);
				return;
			}
			
			double proportionAlong = (double)stepCount / (double)hypLength;
			double dlengthAlongX = (double)xDiff * proportionAlong;
			double dlengthAlongY = (double)yDiff * proportionAlong;
			
			int lengthAlongX = (int)dlengthAlongX;
			int lengthAlongY = (int)dlengthAlongY;
			
			curX = xWaypoints[lastWaypoint] + lengthAlongX;
			curY = yWaypoints[lastWaypoint] + lengthAlongY;
		}
		
		public void move(){
			step(speed);
		}
		
		public boolean isMissionDone(){
			return missionDone;
		}
	}
	
	class RadarThread extends Thread{
		private int interval;
		private boolean terminate;
		
		public RadarThread(int interval){
			this.interval = interval;
			this.terminate = false;
			this.setDaemon(true);
		}
		
		public void terminate(){
			this.terminate = true;
			interrupt();
		}
		
		public void run(){
			while(true){
				if(terminate){
					return;
				}
				synchronized(entitiesTracked){
					//System.out.println("Thread " + Thread.currentThread() + " Active!");
					EntityPath[] paths = (EntityPath[])entitiesTracked.values().toArray(new EntityPath[0]);
					//for(Iterator it = entitiesTracked.values().iterator(); it.hasNext(); ){
					for(int i = 0; i < paths.length; i++){
						//EntityPath ep = (EntityPath)it.next();
						EntityPath ep = paths[i];
						ep.move();
						if(ep.isMissionDone()){
							if(ep.getEntityID().indexOf("Missile") != -1){
								MissileExplosionMessage mem = new MissileExplosionMessage();
								sendToAll(mem, topIface);
							}
							removeEntityTracked(ep.getEntityID());
							RemoveEntityMessage rem = new RemoveEntityMessage(ep.getEntityID());
							sendToAll(rem, bottomIface);
						}
					}
					sendHeartbeat();
					stepRadarAngle(radarSpeed);
					sendRadarMetadata();
					sendDetectedEntities();
				}
				try{
					sleep(interval);
				}
				catch(InterruptedException e){}
			}
		}
	}
	

}
