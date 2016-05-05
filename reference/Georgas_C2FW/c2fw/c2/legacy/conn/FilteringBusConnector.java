package c2.legacy.conn;

import c2.fw.*;
import c2.legacy.*;

import java.util.*;

/**
 * Generic C2 connector that implements a two-way bus.  Messages received on
 * from bricks below are broadcast to all bricks above, and vice-versa.
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class FilteringBusConnector extends AbstractC2DelegateBrick{

	private BrickInterfaceIdPair thisTop;
	private BrickInterfaceIdPair thisBottom;
	
	private Set interests = new HashSet();
	private Set exclusiveInterests = new HashSet();
	
	/**
	 * Creates a new <code>BusConnector</code> with the given <code>Identifier</code>.
	 * @param id <code>Identifier</code> for this <code>BusConnector</code>.
	 */
	public FilteringBusConnector(Identifier id){
		super(id);
		thisTop = new BrickInterfaceIdPair(getIdentifier(), topIface.getIdentifier());
		thisBottom = new BrickInterfaceIdPair(getIdentifier(), bottomIface.getIdentifier());
		addMessageProcessor(new FilterInterestMessageProcessor());
		addMessageProcessor(new BusConnectorMessageProcessor());
	}

	class FilterInterestMessageProcessor implements MessageProcessor{
		public synchronized void handle(Message m){
			if(m instanceof FilterInterestMessage){
				FilterInterestMessage fim = (FilterInterestMessage)m;
				int filterType = fim.getFilterType();
				if(filterType == FilterInterestMessage.FILTER_NONE){
					interests.remove(m);
					exclusiveInterests.remove(m);
				}
				else if(filterType == FilterInterestMessage.FILTER_INTEREST){
					interests.add(m);
				}
				else if(filterType == FilterInterestMessage.FILTER_EXCLUSIVE_INTEREST){
					ExclusiveFilter ef = new ExclusiveFilter();
					ef.filter = ((FilterInterestMessage)m).getFilter();
					//System.out.println(getIdentifier() + "Got a filterinterestmessage: " + ef.filter);
					ef.brickID = m.getSource().getBrickIdentifier();
					exclusiveInterests.add(ef);
				}
			}
		}
	}

	public void sendToOne(Message m, Identifier brickID, Interface iface){
		if(brickID == null){
			throw new IllegalArgumentException("BrickID in call to sendToOne() was null.");
		}
		if(iface == null){
			throw new IllegalArgumentException("Interface in call to sendToOne() was null.");
		}
		if(m == null){
			throw new IllegalArgumentException("Message in call to sendToOne() was null.");
		}
		
		Identifier otherInterfaceID = null;
		boolean isBottom = false;

		BrickInterfaceIdPair[] destinations = iface.getAllConnectedInterfaces();
			
		if(iface.equals(bottomIface)){
			for(int i = 0; i < destinations.length; i++){
				if(destinations[i].getBrickIdentifier().equals(brickID)){
					otherInterfaceID = destinations[i].getInterfaceIdentifier();
					break;
				}
			}
			if(otherInterfaceID == null){
				//this shouldn't happen but...
				otherInterfaceID = TOP_INTERFACE_ID;
			}
			isBottom = true;
		}
		if(iface.equals(topIface)){
			for(int i = 0; i < destinations.length; i++){
				if(destinations[i].getBrickIdentifier().equals(brickID)){
					otherInterfaceID = destinations[i].getInterfaceIdentifier();
					break;
				}
			}
			if(otherInterfaceID == null){
				//this shouldn't happen but...
				otherInterfaceID = BOTTOM_INTERFACE_ID;
			}
		}
		
		BrickInterfaceIdPair destination = new BrickInterfaceIdPair(brickID, otherInterfaceID);
		
		synchronized(rawMessageListeners){
			int size = rawMessageListeners.size();
			for(int i = 0; i < size; i++){
				MessageListener l = (MessageListener)rawMessageListeners.elementAt(i);
				Message md = m.duplicate();
				if(isBottom){
					md.setSource(thisBottom);
				}
				else{
					md.setSource(thisTop);
				}
				//md.setSource(new BrickInterfaceIdPair(this.getIdentifier(), iface.getIdentifier()));
				l.messageSent(md);
			}
		}
		
		Message md = m.duplicate();
		if(isBottom){
			md.setSource(thisBottom);
		}
		else{
			md.setSource(thisTop);
		}
		//md.setSource(new BrickInterfaceIdPair(this.getIdentifier(), iface.getIdentifier()));
		md.setDestination(destination);
		send(md, iface);
	}
	
	private boolean isNotification(Message m){
		if(m.getDestination().equals(thisTop)){
			return true;
		}
		if(Utils.isC2Notification(m)){
			return true;
		}
		return false;
	}
	
	private boolean isRequest(Message m){
		if(m.getDestination().equals(thisBottom)){
			return true;
		}
		if(Utils.isC2Request(m)) return true;
		return false;
	}
	
	class BusConnectorMessageProcessor implements MessageProcessor{
		/**
		 * Handler for <code>Message</code>s.  Broadcasts C2-style notifications
		 * and requests.
		 * @param m Incoming <code>Message</code>.
		 */
		public void handle(Message m){
			//Exclusive interests are always above.
			for(Iterator it = exclusiveInterests.iterator(); it.hasNext(); ){
				ExclusiveFilter f = (ExclusiveFilter)it.next();
				if(f.filter.accept(m)){
					//System.out.println("Filtered a message!");
					//Okay, we are filtering on this message.
					Identifier brickID = f.brickID;
					//System.out.println("Sending only to " + brickID);
					sendToOne(m, brickID, topIface);
					return;
				}
			}
			
			if(isNotification(m)){
				sendToAll(m, bottomIface);
			}					
			if(isRequest(m)){
				sendToAll(m, topIface);
			}			
		}
	}
		
	class ExclusiveFilter{
		public MessageFilter filter;
		public Identifier brickID;
		
		public boolean equals(Object o){
			if(!(o instanceof ExclusiveFilter)){
				return false;
			}
			ExclusiveFilter otherFilter = (ExclusiveFilter)o;
			return (otherFilter.filter.equals(filter) && otherFilter.brickID.equals(brickID));
		}
		
		public int hashCode(){
			return filter.hashCode() ^ brickID.hashCode();
		}
	}
}
