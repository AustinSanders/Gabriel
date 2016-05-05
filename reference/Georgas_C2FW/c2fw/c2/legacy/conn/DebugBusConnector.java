//!!(C)!!
package c2.legacy.conn;

import c2.fw.*;
import c2.legacy.*;

/**
 * Generic C2 connector that implements a two-way bus.  Messages received on
 * from bricks below are broadcast to all bricks above, and vice-versa.
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class DebugBusConnector extends AbstractC2DelegateBrick{

	private BrickInterfaceIdPair thisTop;
	private BrickInterfaceIdPair thisBottom;

	/**
	 * Creates a new <code>BusConnector</code> with the given <code>Identifier</code>.
	 * @param id <code>Identifier</code> for this <code>BusConnector</code>.
	 */
	public DebugBusConnector(Identifier id){
		super(id);
		addMessageProcessor(new BusConnectorMessageProcessor());
		thisTop = new BrickInterfaceIdPair(getIdentifier(), topIface.getIdentifier());
		thisBottom = new BrickInterfaceIdPair(getIdentifier(), bottomIface.getIdentifier());
	}

	private boolean isNotification(Message m){
		if(Utils.isC2Notification(m)){
			return true;
		}
		if(m.getDestination().equals(thisTop)){
			return true;
		}
		return false;
	}
	
	private boolean isRequest(Message m){
		if(Utils.isC2Request(m)) return true;
		if(m.getDestination().equals(thisBottom)){
			return true;
		}
		return false;
	}
	
	public void sendToAll(Message m, Interface iface){
		if(iface == null){
			throw new IllegalArgumentException("Interface in call to sendToAll() was null.");
		}
		if(m == null){
			throw new IllegalArgumentException("Message in call to sendToAll() was null.");
		}
		
		BrickInterfaceIdPair bip = iface.getBrickInterfaceIdPair();
		BrickInterfaceIdPair[] destinations = iface.getAllConnectedInterfaces();
		
		synchronized(rawMessageListeners){
			int size = rawMessageListeners.size();
			for(int i = 0; i < size; i++){
				MessageListener l = (MessageListener)rawMessageListeners.elementAt(i);
				Message md = m.duplicate();
				md.setSource(bip);
				l.messageSent(md);
			}
		}		
		
		if(sendToAllHandler != null){
			sendToAllHandler.doSendToAll(this, iface, m);
		}
		else{
			for(int i = 0; i < destinations.length; i++){
				Message md = m.duplicate();
				md.setSource(bip);
				System.out.println("Sending message to " + destinations[i] + " : " + md);
				md.setDestination(destinations[i]);
				send(md, iface);
			}
		}
	}
	
		
	class BusConnectorMessageProcessor implements MessageProcessor{
		/**
		 * Handler for <code>Message</code>s.  Broadcasts C2-style notifications
		 * and requests.
		 * @param m Incoming <code>Message</code>.
		 */
		public void handle(Message m){
			System.out.println(getIdentifier() + " handled a message: " + m);
			if(isNotification(m)){
				System.out.println(" > sending to bottom");
				sendToAll(m, bottomIface);
			}
			else if(isRequest(m)){
				System.out.println(" > sending to top");
				sendToAll(m, topIface);
			}
		}
	}
}

