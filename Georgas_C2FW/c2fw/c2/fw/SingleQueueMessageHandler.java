//!!(C)!!
package c2.fw;

/**
 * <code>MessageHandler</code> that uses a single queue for all messages sent by all
 * bricks in the system.  The queue in this case is not a traditional queue, because
 * messages behind the front of the queue can be selected if a specific destination
 * component is requested.
 *
 * @see c2.fw.MessageHandler
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class SingleQueueMessageHandler extends AbstractMessageHandler implements MessageHandler, MessageListener, ArchitectureListener{
	/** The inbox storing messages for all the bricks. */
	protected UnboundedSelectableFIFOInbox inbox = null;
	
	/**
	 * Create a new <code>SingleQueueMessageHandler</code>.
	 */
	public SingleQueueMessageHandler(){
		inbox = new UnboundedSelectableFIFOInbox();
	}
	
	//These functions used by the ArchitectureEngine to properly implement
	//their threading policies
	
	public boolean isMessageWaiting(BrickInterfaceIdPair[] endpoints){
		return inbox.isMessageWaiting(endpoints);
	}
	
	public Message waitMessage(BrickInterfaceIdPair[] endpoints) throws InterruptedException{
		return inbox.waitMessage(endpoints);
	}
	
	public Message getMessage(BrickInterfaceIdPair[] endpoints){
		return inbox.getMessage(endpoints);
	}
	
	public Message peekMessage(BrickInterfaceIdPair[] endpoints){
		return inbox.peekMessage(endpoints);
	}
	
	//--------------------------
	//MessageListener functions
	
	//Here, we route messages to the appropriate queue.  Since there's only one
	//queue, it's not hard to decide which one gets the message.
	
	/**
	 * <code>MessageListener</code> function called when a message is sent
	 * by one of the bricks in the architecture.  Adds the message
	 * to the system queue.
	 * @param m <code>Message</code> that was sent.
	 */
	public void messageSent(Message m){
		//System.out.println("SingleQueueMessageHandler got message: " + m);
		super.messageSent(m);
		inbox.addIncomingMessage(m);
	}
	
	//------------------------------
	//ArchitectureListener functions
	
	//When a component gets added or removed, we have to start/stop listening
	//to messages emanating from that component.
	
	/**
	 * <code>ArchitectureListener</code> function that is called when a
	 * brick is added to the <code>ArchitectureManager</code>.  For
	 * this <code>MessageHandler</code>, it simply adds this <code>MessageHandler</code>
	 * as a <code>MessageListener</code> for the new brick.
	 * @param manager <code>ArchitectureManager</code> sending this event.
	 * @param id Identifier of the brick that was added.
	 */
	public void brickAdded(ArchitectureManager manager, Identifier id){
		Brick b = ((LocalArchitectureManager)manager).getBrick(id);
		b.addMessageListener(this);
	}
	
	/**
	 * <code>ArchitectureListener</code> function that is called when a
	 * brick is removed from the <code>ArchitectureManager</code>.  For
	 * this <code>MessageHandler</code>, it simply removes this <code>MessageHandler</code>
	 * as a <code>MessageListener</code> for the removed brick.
	 * @param manager <code>ArchitectureManager</code> sending this event.
	 * @param id Identifier of the brick that was removed.
	 */
	public void brickRemoving(ArchitectureManager manager, Identifier id){
		Brick b = ((LocalArchitectureManager)manager).getBrick(id);
		b.removeMessageListener(this);
	}
	
	/**
	 * <code>ArchitectureListener</code> function with no implementation.
	 * This object does not need an implementation of this function.
	 * @param manager <code>ArchitectureManager</code> that sent this 
	 * @param id identifier of <code>Brick</code> that was removed.
	 */
	public void brickRemoved(ArchitectureManager manager, Identifier id){}
	
	//Since we don't manage individual queues for each component
	//we don't have to implement these functions.
	/**
	 * <code>ArchitectureListener</code> function with no implementation.
	 * This object does not need an implementation of this function.
	 * @param manager <code>ArchitectureManager</code> that sent this event.
	 * @param w <code>Weld</code> that was added.
	 */
	public void weldAdded(ArchitectureManager manager, Weld w){
	}
	
	/**
	 * <code>ArchitectureListener</code> function with no implementation.
	 * This object does not need an implementation of this function.
	 * @param manager <code>ArchitectureManager</code> that sent this 
	 * @param w <code>Weld</code> that was removed.
	 */
	public void weldRemoving(ArchitectureManager manager, Weld w){
	}
	
	/**
	 * <code>ArchitectureListener</code> function with no implementation.
	 * This object does not need an implementation of this function.
	 * @param manager <code>ArchitectureManager</code> that sent this 
	 * @param w <code>Weld</code> that was removed.
	 */
	public void weldRemoved(ArchitectureManager manager, Weld w){
	}

}

