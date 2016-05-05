package c2.fw;

/**
 * Interface that can be implemented by a <code>SendToAllHandler</code>.
 * <code>SendToAllHandler</code>s implement different behavior for a
 * brick that calls sendToAll() to multicast a message; this allows
 * different message handlers that can use an underlying multicast
 * mechanism (such as a JMS server) to leverage that mechanism.
 * 
 * @author Roshni Malani
 */

public interface SendToAllHandler{
	
	/**
	 * This function is called when a brick calls sendToAll.
	 * @param brick The brick sending the message.
	 * @param iface The interface on which the message is being sent out.
	 * @param m The message being sent.
	 */
	public void doSendToAll(Brick brick, Interface iface, Message m);
}
