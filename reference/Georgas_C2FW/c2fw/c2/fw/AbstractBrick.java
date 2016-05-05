//!!(C)!!
package c2.fw;

import java.util.*;

/**
 * Implements boilerplate methods in the <code>Brick</code> interface.
 * <B><FONT COLOR="#FF0000">NOTE!</FONT></B> The preferred base class is now {@link c2.fw.AbstractDelegateBrick}.
 * @see c2.fw.AbstractDelegateBrick
 * @see c2.fw.DelegateBrick
 * @see c2.fw.Brick
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */ 
public abstract class AbstractBrick implements Brick{

	/** Identifier of this brick */
	private Identifier id;
	
	/** Objects listening to messages emanating from this brick. */
	protected Vector messageListeners = new Vector(10);
	protected MessageListener[] messageListenerArray = new MessageListener[0];
	
	protected Vector archMessageListeners = new Vector(10);
	
	/** Objects listening to raw messages emanating from this brick. */
	protected SingleThreadVector rawMessageListeners = new SingleThreadVector(10);
	
	/** Metadata property table for this brick */
	protected Hashtable properties;

	/** SendToAllHandler for this brick */
	protected SendToAllHandler sendToAllHandler = null;

	/** 
	 * Creates a new brick with the given identifier.
	 * @param thisId <code>Identifier</code> of this brick.
	 */
	public AbstractBrick(Identifier thisId){
		this.id = thisId;
		properties = new Hashtable();
	}
	
	/**
	 * Returns the identifier of this brick.
	 * @return <code>Identifier</code> of this brick.
	 */
	public Identifier getIdentifier(){
		return id;
	}
	
	/**
	 * Returns a string representing the type of this brick.
	 * @return Type of this brick.
	 */
	public String getType(){
		return this.getClass().getName();
	}
	
	/**
	 * Adds the given <code>MessageListener</code> to the list of
	 * objects that will be notified when a C2 message is sent from
	 * this brick.
	 * @param l <code>MessageListener</code> to add.
	 */
	public void addMessageListener(MessageListener l){
		synchronized(messageListeners){
			messageListeners.addElement(l);
			messageListenerArray = (MessageListener[])messageListeners.toArray(new MessageListener[0]);
		}
	}
	
	/**
	 * Removes the given <code>MessageListener</code> from the list of
	 * objects that will be notified when a C2 message is sent from
	 * this brick.
	 * @param l <code>MessageListener</code> to remove.
	 */
	public void removeMessageListener(MessageListener l){
		synchronized(messageListeners){
			messageListeners.removeElement(l);
			messageListenerArray = (MessageListener[])messageListeners.toArray(new MessageListener[0]);
		}
	}

	/**
	 * Adds the given <code>MessageListener</code> to the list of
	 * objects that will be notified when a C2 message is sent from
	 * this brick.  Raw message listeners receive messages going to any
	 * interface, but destinations are not attached.  That is, if a message
	 * is sent to an interface that's not connected to anything,
   * raw message listeners will still hear about it.
	 * @param l <code>MessageListener</code> to add.
	 */
	public void addRawMessageListener(MessageListener l){
		synchronized(rawMessageListeners){
			rawMessageListeners.addElement(l);
		}
	}
	
	/**
	 * Removes the given raw <code>MessageListener</code> from the list of
	 * objects that will be notified when a C2 message is sent from
	 * this brick.  Raw message listeners receive messages going to any
	 * interface, but destinations are not attached.  That is, if a message
	 * is sent to an interface that's not connected to anything,
   * raw message listeners will still hear about it.
	 * @param l <code>MessageListener</code> to remove.
	 */
	public void removeRawMessageListener(MessageListener l){
		synchronized(rawMessageListeners){
			rawMessageListeners.removeElement(l);
		}
	}
	
	/**
	 * Sends the given message out of this brick.  The message must
	 * have an appropriate source and destination.
	 * @param r <code>Request</code> to send.
	 */
	public void send(Message m, Interface iface){
		//System.out.println("Brick: " + this + " sending request: " + r);
		if(m.getDestination() == null){
			throw new IllegalArgumentException("Call to send() without message destination.");
		}
		if(m.getSource() == null){
			throw new IllegalArgumentException("Call to send() without message source.");
		}
		
		synchronized(messageListeners){
			int length = messageListenerArray.length;
			for(int i = 0; i < length; i++){
				//MessageListener l = (MessageListener)messageListeners.elementAt(i);
				messageListenerArray[i].messageSent(m);
			}
		}
	}
	
	public void setSendToAllHandler(SendToAllHandler sendToAllHandler) {
		this.sendToAllHandler = sendToAllHandler;
	}
	
	/** 
	 * Sends a message to all bricks welded onto the given interface.  The message will be
	 * duplicated before it is sent to each brick.
	 * @param m <code>Message</code> to send.  Note that this message does not need to be tagged
	 * with a source or destination.
	 */
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
				md.setDestination(destinations[i]);
				send(md, iface);
			}
		}
	}

	public Identifier[] getAllInterfaceIds(){
		Interface[] interfaces = getAllInterfaces();
		
		Identifier[] ids = new Identifier[interfaces.length];
		for(int i = 0; i < interfaces.length; i++){
			ids[i] = interfaces[i].getIdentifier();
		}
		return ids;
	}
	
	public boolean hasInterface(Identifier id){
		return getInterface(id) != null;
	}
	
	public void setProperty(String name, Object value){
		properties.put(name, value);
	}
	
	public Object getProperty(String name){
		return properties.get(name);
	}
	
	public String[] getPropertyNames(){
		synchronized(properties){
			String[] arr = new String[properties.size()];
			int i = 0;
			for(Iterator it = properties.keySet().iterator(); it.hasNext(); ){
				arr[i++] = (String)it.next();
			}
			return arr;
		}
	}
	
	public void removeProperty(String name){
		properties.remove(name);
	}

	public void addArchMessageListener(ArchMessageListener l){
		archMessageListeners.addElement(l);
	}
	
	public void removeArchMessageListener(ArchMessageListener l){
		archMessageListeners.removeElement(l);
	}
	
	public void sendArchMessage(Message m){
		m.setSource(new BrickInterfaceIdPair(getIdentifier(), new SimpleIdentifier("$$INFRASTRUCTURE_IFACE$$")));
		synchronized(archMessageListeners){
			int size = archMessageListeners.size();
			for(int i = 0; i < size; i++){
				((ArchMessageListener)archMessageListeners.elementAt(i)).archMessageSent(m);
			}
		}
	}
	
	//Methods to be implemented by the child classes
	
	public abstract Interface getInterface(Identifier id);
	public abstract Interface[] getAllInterfaces();
	
	public abstract void init();
	public abstract void begin();
	public abstract void end();
	public abstract void destroy();
	public abstract void handle(Message m);
	
	//Thread related methods with no bodies, can be overridden by
	//child classes.
	
	public void start(){
	}
	public void suspend(){
	}
	public void resume(){
	}
	public void stop(){
	}
	
	public String toString(){
		return "ArchitectureBrick{Class=\"" + getClass().getName() + "\"; id=\"" + id + "\"};"; 
	}
}

