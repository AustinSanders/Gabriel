//!!(C)!!
package c2.fw;

/**
 * Describes objects that manage C2 messages sent by C2 bricks.  The job of a
 * message handler is to collect messages (notifications and requests) as they 
 * are sent from bricks and provide them when called for by an
 * <code>ArchitectureEngine</code>.  Various queuing styles may be adopted
 * by a <code>MessageHandler</code>, including one-queue-per-system,
 * two-queues-per-brick, one-queue-per-weld, priority-based queueing, etc.
 * Regardless of the queuing method used, the MessageHandler should provide
 * this uniform interface to <code>ArchitectureEngine</code>s.
 
 * @see c2.fw.ArchitectureEngine
 * @see c2.fw.Message
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface MessageHandler extends MessageListener, MessageProvider, ArchitectureListener{
	
	//These functions used by the ArchitectureEngine to properly implement
	//their threading policies
	
	/**
	 * Determines if there is a message waiting on any brick on any interface.  Does not block.
	 * @return <code>true</code> if there is an unprocessed message on any brick, <code>false</code> otherwise.
	 */
	public boolean isMessageWaiting();
	
	/**
	 * Determines if there is a message waiting on the given brick on any interface.  Does not block.
	 * @param brickId <code>Identifier</code> of the brick to inspect.
	 * @return <code>true</code> if there is an unprocessed message on the brick, <code>false</code> otherwise.
	 */
	public boolean isMessageWaiting(Identifier brickId);

	/**
	 * Determines if there is a message waiting on any of the given bricks.  Does not block.
	 * @param brickIds <code>Identifier</code>s of the bricks to inspect.
	 * @return <code>true</code> if there is an unprocessed message on any of the bricks, 
	 * <code>false</code> otherwise.
	 */
	public boolean isMessageWaiting(Identifier[] brickIds);
	
	/**
	 * Determines if there is a message waiting on the given brick + interface.  Does not block.
	 * @param brickId <code>Identifier</code> of the brick to inspect.
	 * @param ifaceId <code>Identifier</code> of the interface to inspect.
	 * @return <code>true</code> if there is an unprocessed message on the given interface, 
	 * <code>false</code> otherwise.
	 */
	public boolean isMessageWaiting(Identifier brickId, Identifier ifaceId);

	/**
	 * Determines if there is a message waiting on the given brick + interface.  Does not block.
	 * @param idPair <code>BrickInterfaceIdPair</code> of the brick+interface to inspect.
	 * @return <code>true</code> if there is an unprocessed message on the given interface, 
	 * <code>false</code> otherwise.
	 */
	public boolean isMessageWaiting(BrickInterfaceIdPair idPair);
	
	/**
	 * Determines if there is a message waiting on the given brick on any of
	 * the given interfaces.  Does not block.
	 * @param brickId <code>Identifier</code> of the brick to inspect.
	 * @param interfaceIds <code>Identifier</code>s of the interfaces on that brick to inspect.
	 * @return <code>true</code> if there is an unprocessed message on any of the given interfaces, 
	 * <code>false</code> otherwise.
	 */
	public boolean isMessageWaiting(Identifier brickId, Identifier[] interfaceIds);
	
	/**
	 * Determines if there is a message waiting on any of the given
	 * brick+interface pairs.  Does not block.
	 * @param endpoints <code>Identifier</code>s of the brick+interface combinations to inspect.
	 * @return <code>true</code> if there is an unprocessed message on any of the given interfaces, 
	 * <code>false</code> otherwise.
	 */
	public boolean isMessageWaiting(BrickInterfaceIdPair[] endpoints);
	
	/**
	 * Waits for and retrieves the next message waiting on any brick.  Blocks the current thread
	 * if no messages are waiting.
	 * @return <code>Message</code> waiting for any brick.
	 * @exception InterruptedException if the thread is interrupted while waiting.
	 */
	public Message waitMessage() throws InterruptedException;
	
	/**
	 * Waits for and retrieves the next message waiting on the given brick.  Blocks the current 
	 * thread if no messages are waiting.
	 * @param brickId <code>Identifier</code> of the brick to inspect.
	 * @return <code>Message</code> waiting for any of the given bricks.
	 * @exception InterruptedException if the thread is interrupted while waiting.
	 */
	public Message waitMessage(Identifier brickId) throws InterruptedException;

	/**
	 * Waits for and retrieves the next message waiting on any of the given bricks.  Blocks the current 
	 * thread if no messages are waiting.
	 * @param brickIds <code>Identifier</code>s of the bricks to inspect.
	 * @return <code>Message</code> waiting for any of the given bricks.
	 * @exception InterruptedException if the thread is interrupted while waiting.
	 */
	public Message waitMessage(Identifier[] brickIds) throws InterruptedException;
	
	/**
	 * Waits for and retrieves the next message waiting on the given brick+interface.  
	 * Blocks the current thread if no messages are waiting.
	 * @param brickId <code>Identifier</code> of the brick to inspect.
	 * @param interfaceId <code>Identifier</code> of the interface on that brick to inspect.
	 * @return <code>Message</code> waiting on the given brick+interface.
	 * @exception InterruptedException if the thread is interrupted while waiting.
	 */
	public Message waitMessage(Identifier brickId, Identifier interfaceId) throws InterruptedException;

	/**
	 * Waits for and retrieves the next message waiting on the given brick on any
	 * of the given interfaces.  
	 * Blocks the current thread if no messages are waiting.
	 * @param brickId <code>Identifier</code> of the brick to inspect.
	 * @param interfaceIds <code>Identifier</code>s of the interfaces on that brick to inspect.
	 * @return <code>Message</code> waiting on the given brick+interface.
	 * @exception InterruptedException if the thread is interrupted while waiting.
	 */
	public Message waitMessage(Identifier brickId, Identifier[] interfaceIds) throws InterruptedException;
	
	/**
	 * Waits for and retrieves the next message waiting on the given brick+interface.  
	 * Blocks the current thread if no messages are waiting.
	 * @param endpoint <code>BrickInterfaceIdPair</code> of the brick+interface to inspect.
	 * @return <code>Message</code> waiting on the given brick+interface.
	 * @exception InterruptedException if the thread is interrupted while waiting.
	 */
	public Message waitMessage(BrickInterfaceIdPair endpoint) throws InterruptedException;
	
	/**
	 * Waits for and retrieves the next message waiting on any of the given
	 * brick+interface pairs.  Blocks the current thread if no messages are waiting.
	 * @param endpoints <code>Identifier</code>s of the brick+interface combinations to inspect.
	 * @return <code>Message</code> waiting for any of the given bricks.
	 * @exception InterruptedException if the thread is interrupted while waiting.
	 */
	public Message waitMessage(BrickInterfaceIdPair[] endpoints) throws InterruptedException;
	
	/**
	 * Gets the next message waiting on any brick.  Returns <code>null</code> immediately if no messages
	 * are waiting.  Does not block.
	 * @return A message waiting on any brick, or <code>null</code> if no messages are waiting.
	 */
	public Message getMessage();
	
	/**
	 * Gets the next message waiting on the given brick.  Returns <code>null</code> immediately if no messages
	 * are waiting.  Does not block.
	 * @param id <code>Identifier</code> of the brick to inspect.
	 * @return A message waiting on the given brick, or <code>null</code> if no messages are waiting.
	 */
	public Message getMessage(Identifier id);

	/**
	 * Gets the next message waiting on any of the given bricks.  Returns <code>null</code> 
	 * immediately if no messages are waiting.  Does not block.
	 * @param id <code>Identifier</code>s of the bricks to inspect.
	 * @return A message waiting on one of the given bricks, or <code>null</code> if no messages are waiting.
	 */
	public Message getMessage(Identifier[] id);
	
	/**
	 * Gets the next message waiting on the given brick + interface.  Does not block.
	 * @param brickId <code>Identifier</code> of the brick to inspect.
	 * @param ifaceId <code>Identifier</code> of the interface to inspect.
	 * @return A message, or <code>null</code> if no messages are waiting.
	 */
	public Message getMessage(Identifier brickId, Identifier ifaceId);

	/**
	 * Gets the next message waiting on the given brick + interface.  Does not block.
	 * @param idPair <code>BrickInterfaceIdPair</code> of the brick+interface to inspect.
	 * @return A message, or <code>null</code> if no messages are waiting.
	 */
	public Message getMessage(BrickInterfaceIdPair idPair);
	
	/**
	 * Gets a message waiting on the given brick on any of
	 * the given interfaces.  Does not block.
	 * @param brickId <code>Identifier</code> of the brick to inspect.
	 * @param interfaceIds <code>Identifier</code>s of the interfaces on that brick to inspect.
	 * @return A message, or <code>null</code> if no messages are waiting.
	 */
	public Message getMessage(Identifier brickId, Identifier[] interfaceIds);
	
	/**
	 * Gets a message waiting on any of the given
	 * brick+interface pairs.  Does not block.
	 * @param endpoints <code>Identifier</code>s of the brick+interface combinations to inspect.
	 * @param interfaceIds <code>Identifier</code>s of the interfaces on that brick to inspect.
	 * @return A message, or <code>null</code> if no messages are waiting.
	 */
	public Message getMessage(BrickInterfaceIdPair[] endpoints);
	
	/**
	 * Peeks at the next message waiting on any brick.  Returns <code>null</code> immediately if no messages
	 * are waiting.  Does not block.
	 * @return A message waiting on any brick, or <code>null</code> if no messages are waiting.
	 */
	public Message peekMessage();
	
	/**
	 * Peeks at the next message waiting on the given brick.  Returns <code>null</code> immediately if no messages
	 * are waiting.  Does not block.
	 * @param id <code>Identifier</code> of the brick to inspect.
	 * @return A message waiting on the given brick, or <code>null</code> if no messages are waiting.
	 */
	public Message peekMessage(Identifier id);

	/**
	 * Peeks at the next message waiting on any of the given bricks.  Returns <code>null</code> 
	 * immediately if no messages are waiting.  Does not block.
	 * @param id <code>Identifier</code>s of the bricks to inspect.
	 * @return A message waiting on one of the given bricks, or <code>null</code> if no messages are waiting.
	 */
	public Message peekMessage(Identifier[] id);
	
	/**
	 * Peeks at the next message waiting on the given brick + interface.  Does not block.
	 * @param brickId <code>Identifier</code> of the brick to inspect.
	 * @param ifaceId <code>Identifier</code> of the interface to inspect.
	 * @return A message, or <code>null</code> if no messages are waiting.
	 */
	public Message peekMessage(Identifier brickId, Identifier ifaceId);

	/**
	 * Peeks at the next message waiting on the given brick + interface.  Does not block.
	 * @param idPair <code>BrickInterfaceIdPair</code> of the brick+interface to inspect.
	 * @return A message, or <code>null</code> if no messages are waiting.
	 */
	public Message peekMessage(BrickInterfaceIdPair idPair);
	
	/**
	 * Peeks at a message waiting on the given brick on any of
	 * the given interfaces.  Does not block.
	 * @param brickId <code>Identifier</code> of the brick to inspect.
	 * @param interfaceIds <code>Identifier</code>s of the interfaces on that brick to inspect.
	 * @return A message, or <code>null</code> if no messages are waiting.
	 */
	public Message peekMessage(Identifier brickId, Identifier[] interfaceIds);
	
	/**
	 * Peeks at a message waiting on any of the given
	 * brick+interface pairs.  Does not block.
	 * @param endpoints <code>Identifier</code>s of the brick+interface combinations to inspect.
	 * @param interfaceIds <code>Identifier</code>s of the interfaces on that brick to inspect.
	 * @return A message, or <code>null</code> if no messages are waiting.
	 */
	public Message peekMessage(BrickInterfaceIdPair[] endpoints);
	
	public void setController(ArchitectureController controller);
	public ArchitectureController getController();
}

