package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;

import java.util.*;

public class WorldC2Component extends AbstractC2DelegateBrick{

	private Map objectMap = new HashMap();
	
	public WorldC2Component(Identifier id){
		super(id);
	
		objectMap.put("RC1", 
			new LocatedDrawableThing("RC1", 100, 100, new RadarCircle(4900)));
		
		objectMap.put("RC2", 
			new LocatedDrawableThing("RC2", 1500, 1500, new RadarCircle(3500)));

		objectMap.put("RC3", 
			new LocatedDrawableThing("RC3", 3000, 3000, new RadarCircle(2000)));

		addLifecycleProcessor(new WorldLifecycleProcessor());
		addMessageProcessor(new WorldMessageProcessor());
	}

	class WorldLifecycleProcessor extends LifecycleAdapter{
		public void begin(){
			WorldThread wt = new WorldThread(150);
			wt.start();
		}
	}
	
	class WorldMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof DrawableThingSetMessage){
				DrawableThingSetMessage dtsm = (DrawableThingSetMessage)m;
				LocatedDrawableThing[] arr = dtsm.getLocatedDrawableThings();
				synchronized(objectMap){
					for(int i = 0; i < arr.length; i++){
						objectMap.put(arr[i].getId(), arr[i]);
					}
				}
			}
			else if(m instanceof RemoveEntityMessage){
				synchronized(objectMap){
					objectMap.remove(((RemoveEntityMessage)m).getEntityID());
				}
			}
		}
	}
	
	class WorldThread extends Thread{
		private int interval;
		private boolean terminate;
		
		public WorldThread(int interval){
			this.interval = interval;
			this.terminate = false;
			this.setDaemon(true);
		}
		
		public void terminate(){
			this.terminate = true;
			interrupt();
		}
		
		protected void sendEntitySet(){
			synchronized(objectMap){
				LocatedDrawableThing[] ldts = (LocatedDrawableThing[])objectMap.values().toArray(new LocatedDrawableThing[0]);
				DrawableThingSetMessage dtms = new DrawableThingSetMessage(ldts);
				//System.out.println("Sending entity set.");
				sendToAll(dtms, bottomIface);
			}
		}
		
		public void run(){
			while(true){
				if(terminate){
					return;
				}
				sendEntitySet();
				try{
					sleep(interval);
				}
				catch(InterruptedException e){}
			}
		}
	}
	
}
