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
public class BusConnector extends AbstractC2DelegateBrick{

	private BrickInterfaceIdPair thisTop;
	private BrickInterfaceIdPair thisBottom;

	/**
	 * Creates a new <code>BusConnector</code> with the given <code>Identifier</code>.
	 * @param id <code>Identifier</code> for this <code>BusConnector</code>.
	 */
	public BusConnector(Identifier id){
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
		
	class BusConnectorMessageProcessor implements MessageProcessor{
		/**
		 * Handler for <code>Message</code>s.  Broadcasts C2-style notifications
		 * and requests.
		 * @param m Incoming <code>Message</code>.
		 */
		public void handle(Message m){
			//System.out.println(getIdentifier() + " handled a message: " + m);
			if(isNotification(m)){
				sendToAll(m, bottomIface);
			}
			else if(isRequest(m)){
				sendToAll(m, topIface);
			}
		}
	}
}

