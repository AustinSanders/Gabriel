package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;

import java.util.*;

public class TrackFusionC2Component extends AbstractC2DelegateBrick{
	
	private int fusionTime = 0;
	
	private TrackFusionThread tft = null;
	
	private Map trackDatabase = Collections.synchronizedMap(new HashMap());
	
	//Maps IDs to Integer headings
	private Map lastKnownHeadings = Collections.synchronizedMap(new HashMap());
	private Map lastKnownXdiffs = Collections.synchronizedMap(new HashMap());
	private Map lastKnownYdiffs = Collections.synchronizedMap(new HashMap());
	private Map lastKnownReportTimes = Collections.synchronizedMap(new HashMap());
	
	public TrackFusionC2Component(Identifier id){
		super(id);
		addLifecycleProcessor(new TrackFusionC2ComponentLifecycleProcessor());
		addMessageProcessor(new TrackFusionMessageProcessor());
	}
	
	class TrackFusionC2ComponentLifecycleProcessor extends LifecycleAdapter{
		public void begin(){
			tft = new TrackFusionThread(Constants.TICK_INTERVAL_IN_MS);
			tft.start();
		}
		
		public void end(){
			sendStatusRemoving();
			if(tft != null){
				tft.terminate();
			}
		}
	}
	
	private void updateIFFData(IFFDataMessage iffdm){
		String entityID = iffdm.getEntityID();
		Track t = (Track)trackDatabase.get(entityID);
		if(t == null){
			return;
		}
		t.setAffiliation(iffdm.getAffiliation());
		trackDatabase.put(entityID, t);
	}
		
	private void updateAnnotationData(AnnotationDataMessage adm){
		String entityID = adm.getEntityID();
		Track t = (Track)trackDatabase.get(entityID);
		if(t == null){
			return;
		}
		t.setAnnotations(adm.getAnnotations());
		trackDatabase.put(entityID, t);
	}
	
	public void sendHeartbeat(){
		ComponentStatusMessage csm = new ComponentStatusMessage(getIdentifier().toString(),
			ComponentStatusMessage.STATUS_OK, Constants.TICK_INTERVAL_IN_MS * 2);
		sendToAll(csm, bottomIface);
	}
	
	public void sendStatusRemoving(){
		ComponentStatusMessage csm = new ComponentStatusMessage(getIdentifier().toString(),
			ComponentStatusMessage.STATUS_REMOVED, Constants.TICK_INTERVAL_IN_MS * 2);
		sendToAll(csm, bottomIface);
	}
	
	private void updateRadarData(RadarDataMessage rdm){
		String entityID = rdm.getEntityID();
		Track t = (Track)trackDatabase.get(entityID);
		if(t == null){
			t = new Track(entityID);
		}
		
		int oldX = 0;
		int oldY = 0;
		int heading = 0;
		try{
			oldX = t.getWorldX();
			oldY = t.getWorldY();
			
			int xDiff = rdm.getWorldLocationX() - oldX;
			int yDiff = rdm.getWorldLocationY() - oldY;
			//System.out.println(entityID + "new x/y = (" + rdm.getWorldLocationX() + "," + rdm.getWorldLocationY() + ");");
			//System.out.println(entityID + "old x/y = (" + oldX + "," + oldY + ");");
			
			if((xDiff != 0) || (yDiff != 0)){
				heading = Trigonometry.calculateAngle(xDiff, yDiff);
				lastKnownHeadings.put(entityID, new Integer(heading));
				lastKnownXdiffs.put(entityID, new Integer(xDiff));
				lastKnownYdiffs.put(entityID, new Integer(yDiff));
				//System.out.println("Calculated heading (" + xDiff + "," + (yDiff) + ") of : " + entityID + " " + heading);
			}
			else{
				Integer hdng = (Integer)lastKnownHeadings.get(entityID);
				if(hdng != null){
					heading = hdng.intValue();
					//System.out.println("Looked up heading of : " + entityID + " " + heading);
				}
				else{
					heading = 0;
				}
			}
		}
		catch(Exception e){
		}
		
		lastKnownReportTimes.put(entityID, new Integer(fusionTime));
		
		t.setWorldX(rdm.getWorldLocationX());
		t.setWorldY(rdm.getWorldLocationY());
		t.setHeading(heading);
		
		t.setEntityType(rdm.getEntityType());
		
		trackDatabase.put(entityID, t);
	}
	
	class TrackFusionMessageProcessor implements MessageProcessor{
		public synchronized void handle(Message m){
			if(m instanceof RadarDataMessage){
				updateRadarData((RadarDataMessage)m);
			}
			else if(m instanceof RadarMetadataMessage){
				sendToAll(m, bottomIface);
			}
			else if(m instanceof IFFDataMessage){
				updateIFFData((IFFDataMessage)m);
			}
			else if(m instanceof AnnotationDataMessage){
				updateAnnotationData((AnnotationDataMessage)m);
			}
			else if(m instanceof RemoveEntityMessage){
				trackDatabase.remove(((RemoveEntityMessage)m).getEntityID());
				sendToAll(m, bottomIface);
				trackDatabase.remove(((RemoveEntityMessage)m).getEntityID() + "_guess");
				RemoveEntityMessage rem2 = (RemoveEntityMessage)m.duplicate();
				rem2.setEntityID(((RemoveEntityMessage)m).getEntityID() + "_guess");
				sendToAll(rem2, bottomIface);
			}
		}
	}

	class TrackFusionThread extends Thread{
		private int interval;
		private boolean terminate;
		
		public TrackFusionThread(int interval){
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
				sendHeartbeat();
				synchronized(trackDatabase){
					fusionTime++;
					for(Iterator it = trackDatabase.values().iterator(); it.hasNext(); ){
						Track t = (Track)it.next();
						
						Integer lkrtInt = (Integer)lastKnownReportTimes.get(t.getEntityID());
						if(lkrtInt != null){
							int lkrt = lkrtInt.intValue();
							//System.out.println("lkrt for " + t.getEntityID() + " = " + lkrt);
							int deltaT = fusionTime - lkrt;
							if(deltaT > 3){
								//We haven't gotten a report on this guy in several ticks.  Start
								//guessing position.
								Integer lastKnownXdiffInt = (Integer)lastKnownXdiffs.get(t.getEntityID());
								Integer lastKnownYdiffInt = (Integer)lastKnownYdiffs.get(t.getEntityID());
								if((lastKnownXdiffInt != null) && (lastKnownYdiffInt != null)){
									int lastKnownXdiff = lastKnownXdiffInt.intValue();
									int lastKnownYdiff = lastKnownYdiffInt.intValue();
									t.setGuessedWorldX(t.getWorldX() + (lastKnownXdiff * deltaT));
									t.setGuessedWorldY(t.getWorldY() + (lastKnownYdiff * deltaT));
								}
							}
							else{
								t.setGuessedWorldX(t.getWorldX());
								t.setGuessedWorldY(t.getWorldY());
								RemoveEntityMessage rem = new RemoveEntityMessage(t.getEntityID() + "_guess");
								sendToAll(rem, bottomIface);
							}
						}
						
						
						//System.out.println("Sending track: " + t);
						sendToAll(t, bottomIface);
					}
				}
				try{
					sleep(interval);
				}
				catch(InterruptedException e){}
			}
		}
	}
	
}
