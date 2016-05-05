package c2.fw;

import java.util.Vector;

/**
 * FIFO <code>Inbox</code> that can grow as big as memory and has additional blocking and 
 * queue-jumping semantics.  Essentially, it's not truly FIFO since another object can
 * request a message for a given component and, if it's not at the front of the queue,
 * this inbox will retrieve it from the middle of the queue.
 *
 * @see c2.fw.Inbox
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class UnboundedSelectableFIFOInbox implements Inbox{
	private Vector buf = new Vector(20);
	private Object lock = new Object();
	
	public boolean addIncomingMessage(Message m){
		buf.addElement(m);
		synchronized(lock){
			lock.notifyAll();
		}
		return true;
	}

	//public boolean isMessageWaiting();
	//public boolean isMessageWaiting(Identifier brickId);
	//public boolean isMessageWaiting(Identifier[] brickIds);
	//public boolean isMessageWaiting(Identifier brickId, Identifier ifaceId);
	//public boolean isMessageWaiting(BrickInterfaceIdPair idPair);
	//public boolean isMessageWaiting(Identifier brickId, Identifier[] interfaceIds);
	
	public synchronized boolean isMessageWaiting(BrickInterfaceIdPair[] endpoints){
		for(int i = 0; i < buf.size(); i++){
			Message m = (Message)buf.elementAt(i);
			for(int j = 0; j < endpoints.length; j++){
				if(m.getDestination().equals(endpoints[j])){
					return true;
				}
			}
		}
		return false;
	}
	
	//public Message waitMessage() throws InterruptedException;
	//public Message waitMessage(Identifier brickId) throws InterruptedException;
	//public Message waitMessage(Identifier[] brickIds) throws InterruptedException;
	//public Message waitMessage(Identifier brickId, Identifier interfaceId) throws InterruptedException;
	//public Message waitMessage(Identifier brickId, Identifier[] interfaceIds) throws InterruptedException;
	//public Message waitMessage(BrickInterfaceIdPair endpoint) throws InterruptedException;
	
	public Message waitMessage(BrickInterfaceIdPair[] endpoints) throws InterruptedException{
		while(true){
			Message m = getMessage(endpoints);
			if(m != null){
				return m;
			}
			//If we get here, no messages for that destination were waiting.
			//Block until a new message comes in, then check again.
			synchronized(lock){
				lock.wait();
			}
		}
	}
	
	//public Message getMessage();
	//public Message getMessage(Identifier id);
	//public Message getMessage(Identifier[] id);
	//public Message getMessage(Identifier brickId, Identifier ifaceId);
	//public Message getMessage(BrickInterfaceIdPair idPair);
	//public Message getMessage(Identifier brickId, Identifier[] interfaceIds);
	
	public synchronized Message getMessage(BrickInterfaceIdPair[] endpoints){
		for(int i = 0; i < buf.size(); i++){
			Message m = (Message)buf.elementAt(i);
			for(int j = 0; j < endpoints.length; j++){
				if(m.getDestination().equals(endpoints[j])){
					buf.removeElementAt(i);
					return m;
				}
			}
		}
		return null;
	}
	
	public synchronized Message getNextMessage(){
		if(buf.size() == 0){
			return null;
		}
		Message m = (Message)buf.elementAt(0);
		buf.removeElementAt(0);
		return m;
	}

	public synchronized Message peekMessage(BrickInterfaceIdPair[] endpoints){
		for(int i = 0; i < buf.size(); i++){
			Message m = (Message)buf.elementAt(i);
			for(int j = 0; j < endpoints.length; j++){
				if(m.getDestination().equals(endpoints[j])){
					return m;
				}
			}
		}
		return null;
	}
	
	public synchronized Message peekNextMessage(){
		if(buf.size() == 0){
			return null;
		}
		Message m = (Message)buf.elementAt(0);
		return m;
	}

	/**
	 * Determine if the Inbox is empty.
	 * @return <code>true</code> if the inbox is empty, <code>false</code> otherwise.
	 */
	public boolean isEmpty(){
		return buf.size() == 0;
	}
	
	/**
	 * Determine if the inbox is full.  Since the inbox size is unbounded, this
	 * will never return <code>true</code>.
	 * @return <code>false</code>.
	 */
	public boolean isFull(){
		return false;
	}
}	



