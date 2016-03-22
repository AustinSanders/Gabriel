package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;

import java.util.*;

public class AnnotationC2Component extends AbstractC2DelegateBrick{
	
	private AnnotationThread at;
	
	private Map entitiesTracked = new HashMap();
	
	public AnnotationC2Component(Identifier id){
		super(id);
		addLifecycleProcessor(new AnnotationLifecycleProcessor());
		addMessageProcessor(new CreateEntityMessageProcessor());
	}
	
	protected void addEntityTracked(String id, List annotations){
		entitiesTracked.put(id, annotations);
	}
	
	protected void removeEntityTracked(String id){
		entitiesTracked.remove(id);
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

	class CreateEntityMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof CreateEntityMessage){
				CreateEntityMessage cem = (CreateEntityMessage)m;
				addEntityTracked(cem.getEntityID(), Arrays.asList(cem.getAnnotations()));
			}
		}
	}
	
	class AnnotationLifecycleProcessor extends LifecycleAdapter{
		public void init(){
			/*
			String id1 = "FA/18 100";
			ArrayList annotations1 = new ArrayList();
			annotations1.add("FA/18 Hornet");
			annotations1.add("France");
			addEntityTracked(id1, annotations1);

			String id2 = "Cessna";
			ArrayList annotations2 = new ArrayList();
			annotations2.add("Cessna Skyhawk");
			annotations2.add("N8244101");
			addEntityTracked(id2, annotations2);
			*/
		}
		
		public void begin(){
			at = new AnnotationThread(Constants.TICK_INTERVAL_IN_MS);
			at.start();
		}
		
		public void end(){
			sendStatusRemoving();
			if(at != null)
			{
				at.terminate();
			}
		}
	}
	
	public void sendDetectedEntities(){
		for(Iterator it = entitiesTracked.keySet().iterator(); it.hasNext(); ){
			String entityId = (String)it.next();
			List annotationList = (List)entitiesTracked.get(entityId);
			
			if(annotationList != null){
				String[] annotations = (String[])annotationList.toArray(new String[0]);
				AnnotationDataMessage adm = new AnnotationDataMessage(entityId, annotations);
				sendToAll(adm, bottomIface);
			}
		}
	}
	
	class AnnotationThread extends Thread{
		private int interval;
		private boolean terminate;
		
		public AnnotationThread(int interval){
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