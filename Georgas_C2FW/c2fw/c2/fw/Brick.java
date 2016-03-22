//!!(C)!!
package c2.fw;

/**
 * Interface that defines the basic capabilities of a C2 Component or Connector.
 * <code>Brick</code> is an abstract interface; actual components/connectors
 * should implement <code>Component</code> or <code>Connector</code>, respectively.
 * 
 * @see c2.fw.Component
 * @see c2.fw.Connector
 * @see c2.fw.AbstractBrick
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public abstract interface Brick extends MessageProvider{

	/**
	 * Gets the identifier of this brick.  Every brick must have a globally unique
	 * identifier.  The identifier can be any Java object that implements the
	 * <code>Identifier</code> interface.
	 * @return <code>Identifier</code> of this brick.
	 * @see c2.fw.Identifier
	 */
	public Identifier getIdentifier();
	
	/**
	 * Gets the type of this brick in a String.
	 * In the Java implementation, the type is the Brick's class name.
	 * @return Type of this brick.
   */
	public String getType();

	//The ArchitectureManager keeps, accessible
	//along with this brick, the list of bricks above
	//and below it.  However, it only uses ID's, not
	//actual references to the bricks, thus preserving the
	//"no shared memory/references" C2 rule.
	//When a weld or unweld happens, the list is updated.

	public Interface[] getAllInterfaces();
	public boolean hasInterface(Identifier ifaceId);
	public Interface getInterface(Identifier ifaceId);

	/**
	 * Initializes the brick after it has been created.
	 * This function is called by the <code>ArchitectureManager</code> before 
	 * the brick is expected to handle any messages.
	 */
	public void init();
	
	/**
	 * Sends initial messages and initializes the state of this brick.
	 * This function is called by the <code>ArchitectureManager</code> when
	 * this brick is welded into place and should send out its initial messages.
	 */
	public void begin();
	
	/**
	 * Sends final messages and deinitializes the state of this brick.
	 * This function is called by the <code>ArchitectureManager</code> when
	 * this brick is about to be dewelded out of its place and should send out
	 * its final messages.  It should not return unless the brick is ready to be
	 * unwelded.
	 */
	public void end();
	
	/**
	 * Destroys the brick before it is about to be removed from the system.
	 * This function is called by the <code>ArchitectureManager</code> when
	 * this brick is about to be removed from the system entirely, so it
	 * can deinitialize itself.
	 */
	public void destroy();

	/**
	 * Handles an incoming message
	 * @param m <code>Message</code> to handle.
	 */
	public void handle(Message m);
	
	/**
	 * Sends the given request out of this brick.  The message must
	 * have an appropriate source and destination.
	 * @param r <code>Request</code> to send.
	 */
	public void send(Message m, Interface iface);
	
	/** 
	 * Sends a message to all bricks welded onto the given interface.  The message will be
	 * duplicated before it is sent to each brick.
	 * @param m <code>Message</code> to send.  Note that this message does not need to be tagged
	 * with a source or destination.
	 */
	public void sendToAll(Message m, Interface iface);
	
	/**
	 * Adds a message listener object that is interested
	 * in messages emanating from this brick.
	 * @param l <code>MessageListener</code> that will receive and handle messages 
	 * sent from this brick.
	 */
	public void addMessageListener(MessageListener l);
	
	/**
	 * Removes a message listener object that is no longer interested
	 * in messages emanating from this brick.
	 * @param l <code>MessageListener</code> that will no longer 
	 * receive and handle messages sent from this brick.
	 */
	public void removeMessageListener(MessageListener l);
	
	/**
	 * Adds an ArchMessage listener object that is interested
	 * in ArchMessages emanating from this brick.
	 * @param l <code>ArchMessageListener</code> that will receive and handle messages 
	 * sent from this brick.
	 */
	public void addArchMessageListener(ArchMessageListener l);
	
	/**
	 * Removes an ArchMessage listener object that is no longer interested
	 * in ArchMessages emanating from this brick.
	 * @param l <code>ArchMessageListener</code> that will no longer 
	 * receive and handle messages sent from this brick.
	 */
	public void removeArchMessageListener(ArchMessageListener l);
	
	/**
	 * Called when this brick is started by the <code>ArchitectureEngine</code>.
	 * Note that this function usually does not have to do anything and can be
	 * left blank, but it gives the brick an opportunity to start resources
	 * if it has independent thread(s) of control.
	 */
	public void start();

	/**
	 * Called when this brick is suspended by the <code>ArchitectureEngine</code>.
	 * Note that this function usually does not have to do anything and can be
	 * left blank, but it gives the brick an opportunity to suspend resources
	 * if it has independent thread(s) of control.
	 */
	public void suspend();
	
	/**
	 * Called when this brick is resumed by the <code>ArchitectureEngine</code>.
	 * Note that this function usually does not have to do anything and can be
	 * left blank, but it gives the brick an opportunity to resume resources
	 * if it has independent thread(s) of control.
	 */
	public void resume();
	
	/**
	 * Called when this brick is stopped by the <code>ArchitectureEngine</code>.
	 * Note that this function usually does not have to do anything and can be
	 * left blank, but it gives the brick an opportunity to stop resources
	 * if it has independent thread(s) of control.
	 */
	public void stop();

	/**
	 * Set a name-value pair property on this brick.
	 * Any previous property with the same name will be overwritten.
	 * @param name Name of the property to set.
	 * @param value Value of the property to set.
	 */
	public void setProperty(String name, Object value);
	
	/**
	 * Get a list of properties that have been set on this brick.
	 * @return Array of property names.
	 */
	public String[] getPropertyNames();
	
	/**
	 * Get a property value that has been set on this brick, given
	 * the property name.
	 * @param name Property name.
	 * @return property value, or <CODE>null</CODE> if that property
	 * has not been set.
	 */
	public Object getProperty(String name);
	
	/**
	 * Removes a property previously set on this brick.
	 * @param name Property name to remove.
	 */
	public void removeProperty(String name);
	
	/**
	 * This function allows an external entity, usually one of
	 * the <code>ArchitectureManager</code>, <code>MessageHandler</code>,
	 * or <code>ArchitectureEngine</code>, to change the behavior for
	 * the sendToAll function by plugging in a new handler.
	 * 
	 * @param s The <code>SendToAllHandler</code> this brick should use.
	 * @see SendToAllHandler
	 */
	public void setSendToAllHandler(SendToAllHandler s);

}

