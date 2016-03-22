//!!(C)!!
package c2.fw;

import c2.util.GrowableWraparoundQueue;

import java.util.*;

/**
 * A basic <code>ArchitectureManager</code> that manages components, connectors, and
 * welds.
 * 
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class SimpleArchitectureManager extends AbstractArchitectureManager implements ArchitectureManager, ArchMessageListener{

	//Routines grab this lock when they change the architecture so they can do it transactionally.
	private Object archLock;
	
	//Maps BrickDescriptions to BrickFactories.
	private Hashtable factoryTable;
	private Vector brickLoaders;
	
	//Maps identifiers to c2.fw.Components and c2.fw.Connectors
	//private Hashtable bricksTable;
	protected Map bricksTable;
	protected Vector welds;

	private Vector archMessageListeners = new Vector();
	private ArchMessageThread archMessageThread;
	
	/**
	 * Creates a new <code>SimpleArchitectureManager</code> with messages handled by
	 * the given <code>MessageHandler</code> and driven by the given <code>ArchitectureEngine</code>.
	 * @param controller <code>ArchitectureController</code> for this <code>ArchitectureManager</code>.
	 */
	public SimpleArchitectureManager(){
		super();
		bricksTable = Collections.synchronizedMap(new HashMap());
		welds = new Vector();
		factoryTable = new Hashtable();
		brickLoaders = new Vector();
		registerBrickLoader(new JavaNetBrickLoader());
		archMessageThread = new ArchMessageThread();
		//engine = null;
	}
	
	/**
	 * Registers another <code>BrickLoader</code> that this manager
	 * will use to try to load bricks.  The <code>SimpleArchitectureManager</code>
	 * will automatically use a <code>JavaBrickLoader</code>, so there's no
	 * need to register one of those.
   * @param loader <code>BrickLoader</code> to register.
   */
	public void registerBrickLoader(BrickLoader loader){
		brickLoaders.insertElementAt(loader, 0);
	}
	
	private BrickFactory getBrickFactory(BrickDescription description)
		throws BrickNotFoundException, 
		BrickLoadFailureException, 
		UnsupportedBrickDescriptionException,
		BrickCreationException
	{
		int ubdeCount = 0;
		BrickFactory factory = (BrickFactory)factoryTable.get(description);
		if(factory == null){
			//Try to find a factory that can load this brick.
			for(Enumeration en = brickLoaders.elements(); en.hasMoreElements(); ){
				BrickLoader loader = (BrickLoader)en.nextElement();
				try{
					factory = loader.load(description);
					if(factory != null){
						factoryTable.put(description, factory);
						break;
					}
				}
				catch(BrickNotFoundException bnfe){
				}
				catch(BrickLoadFailureException blfe){
					//we should have been able to load this brick,
					//but it failed.  That's an error.
					throw(blfe);
				}
				catch(UnsupportedBrickDescriptionException ubde){
					ubdeCount++;
				}
			}
			if(factory == null){
				if(ubdeCount == brickLoaders.size()){
					throw new UnsupportedBrickDescriptionException("Can't load: " + description.toString());
				}
				else{
					throw new BrickNotFoundException("Can't load: " + description.toString());
				}
			}
		}
		
		return factory;
	}

	public synchronized void addBrick(BrickDescription description, Identifier id)
		throws BrickNotFoundException, 
		BrickLoadFailureException, 
		UnsupportedBrickDescriptionException,
		BrickCreationException
	{
		this.addBrick(description, id, null);
	}
	
	public synchronized void addBrick(BrickDescription description, Identifier id, 
		InitializationParameter[] params)
		throws BrickNotFoundException, 
		BrickLoadFailureException, 
		UnsupportedBrickDescriptionException,
		BrickCreationException
	{
		BrickFactory factory = getBrickFactory(description);
		//System.out.println("factory = " + factory);
		Brick b = factory.create(id, params);
		//System.out.println("b = " + b);
		this.addBrick((Brick)b);
	}
	
	/**
	 * Adds a new brick to this architecture.  All <code>ArchitectureListener</code>s will be 
	 * notified of the change.
	 * @param b Brick to add.
	 */
	public synchronized void addBrick(Brick b){
		if(b == null){
			throw new IllegalArgumentException("Null Parameter");
		}
		if(bricksTable.containsKey(b.getIdentifier())){
			throw new IllegalArgumentException("Duplicate identifier.");
		}
		
		//b.addArchMessageListener(this);
		
		bricksTable.put(b.getIdentifier(), b);
		b.addArchMessageListener(this);
		fireBrickAdded(b.getIdentifier());
		b.init();
	}
	
	/**
	 * Adds a new <code>Weld</code> to the architecture.  All <code>ArchitectureListener</code>s will be 
	 * notified of the change.  Also adds identifier references to each of the bricks, as
	 * appropriate.
	 * @param w Weld to add.
	 */
	public synchronized void addWeld(Weld w){
		if(w == null){
			throw new IllegalArgumentException("Null parameter.");
		}

		BrickInterfaceIdPair firstEndpoint = w.getFirstEndpoint();
		BrickInterfaceIdPair secondEndpoint = w.getSecondEndpoint();
		
		//System.out.println("topId = " + topId);
		//System.out.println("topId.class = " + topId.getClass());
		//System.out.println("topId.hashcode = " + topId.hashCode());
		//for(Enumeration en = componentsTable.keys(); en.hasMoreElements(); ){
			//Identifier id2 = (Identifier)en.nextElement();
			//System.out.println("Got from c.t.: " + id2);
			//System.out.println("Equal? " + id2.equals(topId));
			//System.out.println("Equal<->? " + topId.equals(id2));
			//System.out.println("hashcode = " + id2.hashCode());
		//}
		
		Brick firstBrick = (Brick)bricksTable.get(firstEndpoint.getBrickIdentifier());
		if(firstBrick == null){
			throw new IllegalArgumentException("EndpointThing brick does not exist: " + firstEndpoint);
		}
		
		Interface firstIface = firstBrick.getInterface(firstEndpoint.getInterfaceIdentifier());
		if(firstIface == null){
			throw new IllegalArgumentException("EndpointThing interface does not exist: " + firstEndpoint);
		}
		
		Brick secondBrick = (Brick)bricksTable.get(secondEndpoint.getBrickIdentifier());
		if(secondBrick == null){
			throw new IllegalArgumentException("EndpointThing brick does not exist: " + secondEndpoint);
		}

		Interface secondIface = secondBrick.getInterface(secondEndpoint.getInterfaceIdentifier());
		if(secondIface == null){
			throw new IllegalArgumentException("EndpointThing interface does not exist: " + secondEndpoint);
		}
			
		welds.addElement(w);
		
		firstIface.addConnectedInterface(new BrickInterfaceIdPair(secondEndpoint.getBrickIdentifier(),
			secondEndpoint.getInterfaceIdentifier()));
		secondIface.addConnectedInterface(new BrickInterfaceIdPair(firstEndpoint.getBrickIdentifier(),
			firstEndpoint.getInterfaceIdentifier()));
		
		fireWeldAdded(w);		
	}

	/**
	 * Gets the brick with the given <code>Identifier</code>.
	 * @param id <code>Identifier</code> of the brick to get.
	 * @return Requested brick.
	 * @exception IllegalArgumentException if the brick does not exist.
	 */
	public Brick getBrick(Identifier id){
		Brick b = (Brick)bricksTable.get(id);
		if(b == null){
			System.out.println(bricksTable);
			throw new IllegalArgumentException("No such brick: " + id + ".");
		}
		return b;
	}
	
	/**
	 * Get the identifiers of all the bricks in the architecture. 
	 * May return a zero-length array if there are no bricks.
	 * @return Array of Identifiers of all the bricks in the architecture.
	 */
	public synchronized Identifier[] getBrickIdentifiers(){
		return (Identifier[])bricksTable.keySet().toArray(new Identifier[0]);
	}
	
	/**
	 * Get all the bricks in the architecture. May return a zero-length
	 * array if there are no bricks.
	 * @return Array of all the bricks in the architecture.
	 */
	public Brick[] getAllBricks(){
		return (Brick[])bricksTable.values().toArray(new Brick[0]);
	}

	public Identifier[] getInterfaceIdentifiers(Identifier brickId) throws BrickNotFoundException{
		Brick b = getBrick(brickId);
		if(b == null){
			throw new BrickNotFoundException();
		}
		Interface[] ifaces = b.getAllInterfaces();
		Identifier[] ids = new Identifier[ifaces.length];
		for(int i = 0; i < ifaces.length; i++){
			ids[i] = ifaces[i].getIdentifier();
		}
		return ids;
	}
		
	/**
	 * Gets all the welds in the architecture.  May return a zero-length
	 * array if there are no welds.
	 * @return Array of all the welds in the architecture.
	 */
	public Weld[] getWelds(){
		Weld[] ws = new Weld[welds.size()];
		welds.copyInto(ws);
		return ws;
	}
	
	/**
	 * Removes the given weld from the architecture.
	 * Weld equality is determined by the <code>equals</code> method.
	 * @see c2.fw.Weld#equals(java.lang.Object)
	 * @exception IllegalArgumentException if the Weld does not exist.
	 */
	public synchronized void removeWeld(Weld w){
		if(w == null){
			throw new IllegalArgumentException("Null parameter.");
		}

		BrickInterfaceIdPair firstEndpoint = w.getFirstEndpoint();
		BrickInterfaceIdPair secondEndpoint = w.getSecondEndpoint();
		
		Brick firstBrick = (Brick)bricksTable.get(firstEndpoint.getBrickIdentifier());
		if(firstBrick == null){
			throw new IllegalArgumentException("EndpointThing brick does not exist.");
		}
		
		Interface firstIface = firstBrick.getInterface(firstEndpoint.getInterfaceIdentifier());
		if(firstIface == null){
			throw new IllegalArgumentException("EndpointThing interface does not exist.");
		}
		
		Brick secondBrick = (Brick)bricksTable.get(secondEndpoint.getBrickIdentifier());
		if(secondBrick == null){
			throw new IllegalArgumentException("EndpointThing brick does not exist.");
		}

		Interface secondIface = secondBrick.getInterface(secondEndpoint.getInterfaceIdentifier());
		if(secondIface == null){
			throw new IllegalArgumentException("EndpointThing interface does not exist.");
		}

		fireWeldRemoving(w);
		
		for(int i = 0; i < welds.size(); i++){
			if(w.equals(welds.elementAt(i))){
				welds.removeElementAt(i);
				break;
			}
		}
		
		firstIface.removeConnectedInterface(secondEndpoint);
		secondIface.removeConnectedInterface(firstEndpoint);
		fireWeldRemoved(w);
		
	}
	
	/**
	 * Removes the brick with the given identifier from the system.
	 * Welds to/from that brick are automatically removed as well.
	 * @param id Identifier of brick to remove.
	 * @exception IllegalArgumentException if the brick does not exist.
	 */
	public synchronized void removeBrick(Identifier id){
		if(id == null){
			throw new IllegalArgumentException("Null parameter.");
		}
		
		Brick b = (Brick)bricksTable.get(id);
		if(b == null){
			throw new IllegalArgumentException("No such component.");
		}

		fireBrickRemoving(id);
		
		b.removeArchMessageListener(this);
		b.destroy();
		
		Weld[] welds = getWelds();
		for(int i = 0; i < welds.length; i++){
			BrickInterfaceIdPair bp1 = welds[i].getFirstEndpoint();
			if(bp1.getBrickIdentifier().equals(id)){
				removeWeld(welds[i]);
				continue;
			}
			BrickInterfaceIdPair bp2 = welds[i].getSecondEndpoint();
			if(bp2.getBrickIdentifier().equals(id)){
				removeWeld(welds[i]);
				continue;
			}
		}
		
		bricksTable.remove(id);
		fireBrickRemoved(id);
	}
	
	/**
	 * Calls the <code>begin()</code> method on the brick with the given
	 * identifier.
	 * @param <code>Identifier</code> of brick to begin.
	 */
	public void begin(Identifier id){
		if(id == null){
			throw new IllegalArgumentException("Null parameter.");
		}
		
		Brick b = (Brick)bricksTable.get(id);
		if(b == null){
			throw new IllegalArgumentException("No such brick.");
		}
		b.begin();
	}
	
	/**
	 * Calls the <code>end()</code> method on the brick with the given
	 * identifier.
	 * @param <code>Identifier</code> of brick to end.
	 */
	public void end(Identifier id){
		if(id == null){
			throw new IllegalArgumentException("Null parameter.");
		}
		
		Brick b = (Brick)bricksTable.get(id);
		if(b == null){
			throw new IllegalArgumentException("No such brick.");
		}
		b.end();
	}

	/**
	 * Adds an ArchMessage listener object that is interested
	 * in ArchMessages emanating from this architecture.
	 * @param l <code>ArchMessageListener</code> that will receive and handle messages 
	 * sent from this architecture.
	 */
	public void addArchMessageListener(ArchMessageListener l){
		archMessageListeners.addElement(l);
	}
	
	/**
	 * Removes an ArchMessage listener object that is no longer interested
	 * in ArchMessages emanating from this architecture.
	 * @param l <code>ArchMessageListener</code> that will no longer 
	 * receive and handle messages sent from this architecture.
	 */
	public void removeArchMessageListener(ArchMessageListener l){
		archMessageListeners.removeElement(l);
	}
	
	public void archMessageSent(Message m){
		archMessageThread.post(m);
	}
	
	class ArchMessageThread extends Thread{
		
		protected Object lock = new Object();
		protected GrowableWraparoundQueue messageQueue = new GrowableWraparoundQueue();
		protected boolean shouldTerminate = false;
		
		public ArchMessageThread(){
			this.setDaemon(true);
			this.start();			
		}
		
		public void terminate(){
			shouldTerminate = true;
			this.interrupt();
		}
		
		public void post(Message m){
			synchronized(lock){
				messageQueue.enqueue(m);
				lock.notify();
			}
			Thread.yield();
		}
		
		public void run(){
			while(true){
				if(shouldTerminate){
					return;
				}
			
				//System.out.println("Checking for messages.");
				while(!messageQueue.isEmpty()){
					Message m = null;
					m = (Message)messageQueue.dequeue();
					//done so you don't get concurrent modification exceptions
					ArchMessageListener[] amls = (ArchMessageListener[])archMessageListeners.toArray(new ArchMessageListener[0]);
					for(int i = 0; i < amls.length; i++){
						amls[i].archMessageSent(m);
					}
					//Thread.yield();
				}
				synchronized(lock){
					if(messageQueue.isEmpty()){
						try{
							lock.wait();
						}
						catch(InterruptedException e){}
					}
				}
			}
		}
	}
	
	/**
	 * Processes C2 <code>ArchMessage</code>s sent by bricks in the architecture.
	 * Is capable of handling <code>ShutdownArchMessage</code>s properly.
	 * @param m <code>ArchMessage</code> to process.
	 * @see c2.fw.ShutdownArchMessage
	 */
	/*
	public void archMessageSent(ArchMessage m){
		//System.out.println("***Got Arch Message: " + m);
		if(m instanceof ShutdownArchMessage){
			int returnCode = ((ShutdownArchMessage)m).getReturnCode();
			int shutdownType = ((ShutdownArchMessage)m).getShutdownType();
			
			if(shutdownType == ShutdownArchMessage.SHUTDOWN_NORMAL){
				if(controller != null){
					controller.stopEngine();
				}
				System.exit(returnCode);
			}
			else if(shutdownType == ShutdownArchMessage.SHUTDOWN_NOW){
				System.exit(returnCode);
			}
		}
	}
	*/
}


