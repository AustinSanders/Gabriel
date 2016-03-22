//!!(C)!!

package c2.fw;

import java.util.*;

/**
 * The <code>AbstractMessageHandler</code> class is a skeleton implementation
 * of some (mostly) boilerplate methods in the <code>MessageHandler</code>
 * interface.  Unless your custom <code>MessageHandler</code> has to
 * extend some other class, it should probably extend this one.
 *
 * @see c2.fw.MessageHandler
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public abstract class AbstractMessageHandler implements MessageHandler, MessageProvider{

	/** <code>ArchitectureController</code> for this engine. */
	protected LocalArchitectureController controller = null;
	protected Vector messageListeners = new Vector();
	
	//Maps brick identifiers to all their BrickInterfaceIdPairs (endpoints)
	protected HashMap brickEndpointCache = new HashMap();
	
	/**
	 * Sets the <code>ArchitectureController</code> for this handler.
	 * @param controller Controller for this handler.
	 */
	public void setController(ArchitectureController controller){
		if(!(controller instanceof LocalArchitectureController)){
			throw new IllegalArgumentException("This handler does not support non-local ArchitectureControllers.");
		}
		this.controller = (LocalArchitectureController)controller;
		controller.addArchitectureListener(this);
		controller.addArchitectureListener(new CacheArchitectureListener());
	}
	
	class CacheArchitectureListener implements ArchitectureListener{
		public void brickAdded(ArchitectureManager m, Identifier id){
		}
		public void brickRemoving(ArchitectureManager m, Identifier id){
		}
		public void brickRemoved(ArchitectureManager m, Identifier id){
			brickEndpointCache.remove(id);
		}
		public void weldAdded(ArchitectureManager m, Weld w){
		}
		public void weldRemoving(ArchitectureManager m, Weld w){
		}
		public void weldRemoved(ArchitectureManager m, Weld w){
		}
	}
	
	/**
	 * Gets the <code>ArchitectureController</code> for this handler.
	 * @return Controller for this handler.
	 */
	public ArchitectureController getController(){
		return controller;
	}

	public boolean isMessageWaiting(){
		return isMessageWaiting(getController().getBrickIdentifiers());
	}
	
	public boolean isMessageWaiting(Identifier brickId){
		return isMessageWaiting(new Identifier[]{brickId});
	}

	public boolean isMessageWaiting(Identifier[] brickIds){
		ArchitectureController c = getController();
		ArrayList v = null;
		for(int i = 0; i < brickIds.length; i++){
			BrickInterfaceIdPair[] endpoints = (BrickInterfaceIdPair[])brickEndpointCache.get(brickIds[i]);
			if(endpoints != null){
				if(brickIds.length == 1){
					return isMessageWaiting(endpoints);
				}
				if(v == null){
					v = new ArrayList(brickIds.length * 3);
				}
				for(int j = 0; j < endpoints.length; j++){
					v.add(endpoints[j]);
				}
			}
			else{
				try{
					Identifier[] ifaceIds = c.getInterfaceIdentifiers(brickIds[i]);
					endpoints = new BrickInterfaceIdPair[ifaceIds.length];
					if(v == null){
						v = new ArrayList(brickIds.length * 3);
					}
					for(int j = 0; j < ifaceIds.length; j++){
						BrickInterfaceIdPair biip = new BrickInterfaceIdPair(brickIds[i], ifaceIds[j]);
						v.add(biip);
						endpoints[j] = biip;
					}
					brickEndpointCache.put(brickIds[i], endpoints);
				}
				catch(BrickNotFoundException wontHappen){
					wontHappen.printStackTrace();
				}
			}
		}
		if(v == null){
			return isMessageWaiting(new BrickInterfaceIdPair[0]);
		}
		return isMessageWaiting((BrickInterfaceIdPair[])v.toArray(new BrickInterfaceIdPair[0]));
	}
	
	public boolean isMessageWaiting(Identifier brickId, Identifier ifaceId){
		return isMessageWaiting(new BrickInterfaceIdPair[]{new BrickInterfaceIdPair(brickId, ifaceId)});
	}

	public boolean isMessageWaiting(BrickInterfaceIdPair idPair){
		return isMessageWaiting(new BrickInterfaceIdPair[]{idPair});
	}
	
	public boolean isMessageWaiting(Identifier brickId, Identifier[] interfaceIds){
		BrickInterfaceIdPair[] endpoints = new BrickInterfaceIdPair[interfaceIds.length];
		for(int i = 0; i < endpoints.length; i++){
			endpoints[i] = new BrickInterfaceIdPair(brickId, interfaceIds[i]);
		}
		return isMessageWaiting(endpoints);
	}
	
	/** Implementors should implement at least this function. */
	public abstract boolean isMessageWaiting(BrickInterfaceIdPair[] endpoints);
	
	public Message waitMessage() throws InterruptedException{
		return waitMessage(getController().getBrickIdentifiers());
	}
	
	public Message waitMessage(Identifier brickId) throws InterruptedException{
		return waitMessage(new Identifier[]{brickId});
	}

	public Message waitMessage(Identifier[] brickIds) throws InterruptedException{
		ArchitectureController c = getController();
		ArrayList v = null;
		for(int i = 0; i < brickIds.length; i++){
			BrickInterfaceIdPair[] endpoints = (BrickInterfaceIdPair[])brickEndpointCache.get(brickIds[i]);
			if(endpoints != null){
				if(brickIds.length == 1){
					return waitMessage(endpoints);
				}
				if(v == null){
					v = new ArrayList(brickIds.length * 3);
				}
				for(int j = 0; j < endpoints.length; j++){
					v.add(endpoints[j]);
				}
			}
			else{
				try{
					Identifier[] ifaceIds = c.getInterfaceIdentifiers(brickIds[i]);
					endpoints = new BrickInterfaceIdPair[ifaceIds.length];
					if(v == null){
						v = new ArrayList(brickIds.length * 3);
					}
					for(int j = 0; j < ifaceIds.length; j++){
						BrickInterfaceIdPair biip = new BrickInterfaceIdPair(brickIds[i], ifaceIds[j]);
						v.add(biip);
						endpoints[j] = biip;
					}
					brickEndpointCache.put(brickIds[i], endpoints);
				}
				catch(BrickNotFoundException wontHappen){
					wontHappen.printStackTrace();
				}
			}
		}
		if(v == null){
			return waitMessage(new BrickInterfaceIdPair[0]);
		}
		return waitMessage((BrickInterfaceIdPair[])v.toArray(new BrickInterfaceIdPair[0]));
	}
	
	public Message waitMessage(Identifier brickId, Identifier ifaceId) throws InterruptedException{
		return waitMessage(new BrickInterfaceIdPair[]{new BrickInterfaceIdPair(brickId, ifaceId)});
	}

	public Message waitMessage(Identifier brickId, Identifier[] interfaceIds) throws InterruptedException{
		BrickInterfaceIdPair[] endpoints = new BrickInterfaceIdPair[interfaceIds.length];
		for(int i = 0; i < endpoints.length; i++){
			endpoints[i] = new BrickInterfaceIdPair(brickId, interfaceIds[i]);
		}
		return waitMessage(endpoints);
	}
	
	public Message waitMessage(BrickInterfaceIdPair endpoint) throws InterruptedException{
		return waitMessage(new BrickInterfaceIdPair[]{endpoint});
	}

	/** Implementors need to implement at least this function */
	public abstract Message waitMessage(BrickInterfaceIdPair[] endpoints) throws InterruptedException;
	
	public Message getMessage(){
		return getMessage(getController().getBrickIdentifiers());
	}
	
	public Message getMessage(Identifier brickId){
		return getMessage(new Identifier[]{brickId});
	}

	public Message getMessage(Identifier[] brickIds){
		ArchitectureController c = getController();
		SingleThreadVector v = new SingleThreadVector(brickIds.length * 3);
		for(int i = 0; i < brickIds.length; i++){
			try{
				Identifier[] ifaceIds = c.getInterfaceIdentifiers(brickIds[i]);
				for(int j = 0; j < ifaceIds.length; j++){
					v.addElement(new BrickInterfaceIdPair(brickIds[i], ifaceIds[j]));
				}
			}
			catch(BrickNotFoundException wontHappen){
				wontHappen.printStackTrace();
			}
		}
		BrickInterfaceIdPair[] endpoints = new BrickInterfaceIdPair[v.size()];
		v.copyInto(endpoints);
		return getMessage(endpoints);
	}
	
	public Message getMessage(Identifier brickId, Identifier ifaceId){
		return getMessage(new BrickInterfaceIdPair[]{new BrickInterfaceIdPair(brickId, ifaceId)});
	}

	public Message getMessage(BrickInterfaceIdPair idPair){
		return getMessage(new BrickInterfaceIdPair[]{idPair});
	}
	
	public Message getMessage(Identifier brickId, Identifier[] interfaceIds){
		BrickInterfaceIdPair[] endpoints = new BrickInterfaceIdPair[interfaceIds.length];
		for(int i = 0; i < endpoints.length; i++){
			endpoints[i] = new BrickInterfaceIdPair(brickId, interfaceIds[i]);
		}
		return getMessage(endpoints);
	}
	
	/** Implementors need to at least implement this function */
	public abstract Message getMessage(BrickInterfaceIdPair[] endpoints);
	
	public Message peekMessage(){
		return peekMessage(getController().getBrickIdentifiers());
	}
	
	public Message peekMessage(Identifier brickId){
		return peekMessage(new Identifier[]{brickId});
	}

	public Message peekMessage(Identifier[] brickIds){
		ArchitectureController c = getController();
		SingleThreadVector v = new SingleThreadVector(brickIds.length * 3);
		for(int i = 0; i < brickIds.length; i++){
			try{
				Identifier[] ifaceIds = c.getInterfaceIdentifiers(brickIds[i]);
				for(int j = 0; j < ifaceIds.length; j++){
					v.addElement(new BrickInterfaceIdPair(brickIds[i], ifaceIds[j]));
				}
			}
			catch(BrickNotFoundException wontHappen){
				wontHappen.printStackTrace();
			}
		}
		BrickInterfaceIdPair[] endpoints = new BrickInterfaceIdPair[v.size()];
		v.copyInto(endpoints);
		return peekMessage(endpoints);
	}
	
	public Message peekMessage(Identifier brickId, Identifier ifaceId){
		return peekMessage(new BrickInterfaceIdPair[]{new BrickInterfaceIdPair(brickId, ifaceId)});
	}

	public Message peekMessage(BrickInterfaceIdPair idPair){
		return peekMessage(new BrickInterfaceIdPair[]{idPair});
	}
	
	public Message peekMessage(Identifier brickId, Identifier[] interfaceIds){
		BrickInterfaceIdPair[] endpoints = new BrickInterfaceIdPair[interfaceIds.length];
		for(int i = 0; i < endpoints.length; i++){
			endpoints[i] = new BrickInterfaceIdPair(brickId, interfaceIds[i]);
		}
		return peekMessage(endpoints);
	}
	
	/** Implementors need to at least implement this function */
	public abstract Message peekMessage(BrickInterfaceIdPair[] endpoints);
	
	public void addMessageListener(MessageListener l){
		messageListeners.addElement(l);
	}
	
	public void removeMessageListener(MessageListener l){
		messageListeners.removeElement(l);
	}
	
	protected void fireMessageSent(Message m){
		for(Enumeration en = messageListeners.elements(); en.hasMoreElements(); ){
			((MessageListener)en.nextElement()).messageSent(m);
		}
	}
	
	public void messageSent(Message m){
		fireMessageSent(m);
	}
	
}

