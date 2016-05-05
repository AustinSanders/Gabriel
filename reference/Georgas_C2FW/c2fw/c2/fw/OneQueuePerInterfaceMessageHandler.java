package c2.fw;

import c2.util.*;

import java.util.Enumeration;
import java.util.*;
//import java.util.Random;

/**
 * <CODE>MessageHandler</CODE> that maintains a separate FIFO queue for
 * each interface on each brick in the architecture.
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class OneQueuePerInterfaceMessageHandler extends AbstractMessageHandler implements MessageHandler, MessageListener, ArchitectureListener{
	
	//Maps identifiers to queues that receive messages
	protected Map inboxes;
	
	protected LockPool lockPool = new LockPool();
	protected Mutex lockTableMutex = new Mutex();
	private Map lockMap = new HashMap();
	private Map reverseLockMap = new HashMap();

	public OneQueuePerInterfaceMessageHandler(){
		inboxes = Collections.synchronizedMap(new HashMap());
	}

	public boolean isMessageWaiting(BrickInterfaceIdPair[] endpoints){
		for(int i = 0; i < endpoints.length; i++){
			Inbox inbox = (Inbox)inboxes.get(endpoints[i]);
			if(inbox != null){
				if(!inbox.isEmpty()){
					return true;
				}
			}
		}
		return false;
	}
	
	public Message waitMessage(BrickInterfaceIdPair[] endpoints) throws InterruptedException{
		while(true){
			lockTableMutex.acquire();
			Object lock = lockPool.getLock();
			
			synchronized(lock){
				Message m = getMessage(endpoints);
				if(m != null){
					lockPool.returnLock(lock);
					lockTableMutex.release();
					return m;
				}
				
				reverseLockMap.put(lock, endpoints);
				for(int i = 0; i < endpoints.length; i++){
					HashSet lockList = (HashSet)lockMap.get(endpoints[i]);
					if(lockList == null){
						lockList = new HashSet();
					}
					synchronized(lockList){
						lockList.add(lock);
					}
					lockMap.put(endpoints[i], lockList);
				}
				
				
				//System.out.println(Thread.currentThread() + " waiting on lock: " + lock);
				lockTableMutex.release();
				lock.wait();
			}
		}
	}
	
	public /*synchronized*/ Message getMessage(BrickInterfaceIdPair[] endpoints){
		//System.out.println(Thread.currentThread() + " calling getMessage");
		synchronized(inboxes){
			for(int i = 0; i < endpoints.length; i++){
				Inbox inbox = (Inbox)inboxes.get(endpoints[i]);
				if(inbox != null){
					Message m = inbox.getNextMessage();
					if(m != null){
						return m;
					}
				}
			}
			return null;
		}
	}

	public synchronized Message peekMessage(BrickInterfaceIdPair[] endpoints){	
		for(int i = 0; i < endpoints.length; i++){
			Inbox inbox = (Inbox)inboxes.get(endpoints[i]);
			if(inbox != null){
				Message m = inbox.peekNextMessage();
				if(m != null){
					return m;
				}
			}
		}
		return null;
	}
	
	//--------------------------
	//MessageListener functions
	
	public synchronized void messageSent(Message m){
		lockTableMutex.acquire();

		super.messageSent(m);	
		Inbox inbox = (Inbox)inboxes.get(m.getDestination());
		if(inbox == null){
			throw new IllegalArgumentException("No such interface on brick: " + m.getDestination().getBrickIdentifier());
		}
		inbox.addIncomingMessage(m);
		
		//Notify waiters
		//System.out.println("notifying waiters: " + m.getDestination());

		Object[] locksToNotify;
		HashSet lockList = (HashSet)lockMap.get(m.getDestination());
		if(lockList == null){
			lockTableMutex.release();
			return;
		}
		synchronized(lockList){
			if(lockList.size() == 0){
				//Nobody is waiting on this object.  Return;
				lockTableMutex.release();
				return;
			}
		}
		synchronized(lockList){
			locksToNotify = lockList.toArray();
		}
		
		for(int i = 0; i < locksToNotify.length; i++){
			Object lock = locksToNotify[i];
			//Remove that lock from any lists it is on
			Object[] baseObjects = (Object[])reverseLockMap.get(lock);
			for(int j = 0; j < baseObjects.length; j++){
				HashSet individualLockList = (HashSet)lockMap.get(baseObjects[j]);
				individualLockList.remove(lock);
			}
			reverseLockMap.remove(lock);
		}
		
		for(int i = 0; i < locksToNotify.length; i++){
			synchronized(locksToNotify[i]){
				//System.out.println("Notifying on lock " + locksToNotify[i]);
				locksToNotify[i].notifyAll();
				lockPool.returnLock(locksToNotify[i]);
			}
		}
		lockTableMutex.release();
	}
	
	//------------------------------
	//ArchitectureListener functions
	
	//When a component gets added or removed, we have to start/stop listening
	//to messages emanating from that component.
	
	public synchronized void brickAdded(ArchitectureManager manager, Identifier id){
		Brick b = ((LocalArchitectureManager)manager).getBrick(id);
		Interface[] interfaces = b.getAllInterfaces();
		for(int i = 0; i < interfaces.length; i++){
			UnboundedFIFOInbox inbox = new UnboundedFIFOInbox();
			synchronized(inboxes){
				inboxes.put(new BrickInterfaceIdPair(b.getIdentifier(), interfaces[i].getIdentifier()), inbox);
			}
		}
		b.addMessageListener(this);
	}
	
	public synchronized void brickRemoving(ArchitectureManager manager, Identifier id){
		Brick b = ((LocalArchitectureManager)manager).getBrick(id);
		Interface[] interfaces = b.getAllInterfaces();
		b.removeMessageListener(this);
		for(int i = 0; i < interfaces.length; i++){
			synchronized(inboxes){
				inboxes.remove(new BrickInterfaceIdPair(b.getIdentifier(), interfaces[i].getIdentifier()));
			}
		}
	}
	
	public synchronized void brickRemoved(ArchitectureManager manager, Identifier id){
	}
		
	public void weldAdded(ArchitectureManager manager, Weld w){
	}
	
	public void weldRemoving(ArchitectureManager manager, Weld w){
	}
	
	public void weldRemoved(ArchitectureManager manager, Weld w){
	}

}

