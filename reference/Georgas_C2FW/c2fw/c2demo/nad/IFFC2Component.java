package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;

import java.io.*;
import java.util.*;

public class IFFC2Component extends AbstractC2DelegateBrick{
	
	private IFFThread ifft;
	
	private boolean zapped = false;
	private Map entitiesTracked = new HashMap();
	
	public IFFC2Component(Identifier id){
		super(id);
		addLifecycleProcessor(new IFFLifecycleProcessor());
		addMessageProcessor(new CreateEntityMessageProcessor());
		addMessageProcessor(new ZapMessageProcessor());
	}
	
	protected void addEntityTracked(String id, int affiliation){
		entitiesTracked.put(id, new Integer(affiliation));
	}
	
	protected void removeEntityTracked(String id){
		entitiesTracked.remove(id);
	}
	
	class CreateEntityMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof CreateEntityMessage){
				CreateEntityMessage cem = (CreateEntityMessage)m;
				addEntityTracked(cem.getEntityID(), cem.getAffiliation());
			}
		}
	}
	
	public void zap(){
		zapped = true;
		if(ifft != null){
			ifft.terminate();
		}
		writeIFFDataToFile();
	}
	
	class ZapMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof EMPZapMessage){
				zap();
			}
		}
	}
	
	public void writeIFFDataToFile(){
		try{
			File f = new File("iff-data.dat");
			f.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(entitiesTracked);
			oos.close();
			fos.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	class IFFLifecycleProcessor extends LifecycleAdapter{
		public void init(){
			/*
			String id1 = "FA/18 100";
			int affiliation1 = Constants.FRIENDLY;
			addEntityTracked(id1, affiliation1);
			
			String id2 = "Cessna";
			int affiliation2 = Constants.UNKNOWN;
			addEntityTracked(id2, affiliation2);
			 */
		}
		
		public void begin(){
			try{
				File f = new File("iff-data.dat");
				f.deleteOnExit();
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
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
			
			ifft = new IFFThread(Constants.TICK_INTERVAL_IN_MS);
			ifft.start();
		}
		
		public void end(){
			sendStatusRemoving();
			if(ifft != null)
			{
				ifft.terminate();
			}
			writeIFFDataToFile();
		}
	}
	
	public void sendHeartbeat(){
		if(zapped){
			return;
		}
		ComponentStatusMessage csm = new ComponentStatusMessage(getIdentifier().toString(),
			ComponentStatusMessage.STATUS_OK, Constants.TICK_INTERVAL_IN_MS * 2);
		sendToAll(csm, bottomIface);
	}
	
	public void sendStatusRemoving(){
		ComponentStatusMessage csm = new ComponentStatusMessage(getIdentifier().toString(),
			ComponentStatusMessage.STATUS_REMOVED, Constants.TICK_INTERVAL_IN_MS * 2);
		sendToAll(csm, bottomIface);
	}

	public void sendDetectedEntities(){
		if(zapped){
			return;
		}
		for(Iterator it = entitiesTracked.keySet().iterator(); it.hasNext(); ){
			String entityId = (String)it.next();
			Integer affiliationInt = (Integer)entitiesTracked.get(entityId);
			
			if(affiliationInt != null){
				IFFDataMessage iffdm = new IFFDataMessage(entityId, affiliationInt.intValue());
				sendToAll(iffdm, bottomIface);
			}
		}
	}
	
	class IFFThread extends Thread{
		private int interval;
		private boolean terminate;
		
		public IFFThread(int interval){
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
				sendDetectedEntities();
				try{
					sleep(interval);
				}
				catch(InterruptedException e){}
			}
		}
	}

}
