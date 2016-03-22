//!!(C)!!
package c2.fw;

import java.util.*;

/**
 * Implements a set of (mostly) boilerplate methods for an <code>ArchitectureEngine</code>.
 * This uses an additional administrative control thread that allows components and connectors
 * to (directly or indirectly) change their own state (running, stopped, etc.).  If you are
 * implementing an <code>ArchitectureEngine</code> and do not have to inherit from some other
 * class, it is a good idea to inherit from <code>AbstractArchitectureEngine</code>.
 *
 * @see c2.fw.ArchitectureEngine
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */ 
public abstract class AbstractArchitectureEngine extends Thread implements ArchitectureEngine, ArchitectureListener{

	/** Indicates that the engine is waiting to be started. */
	protected static final int WAIT_ENGINE = 0;
	
	/** Indicates that the engine should be started. */
	protected static final int START_ENGINE = 1000;
	
	/** Indicates that the engine should check its status. */
	protected static final int CHECK_ENGINE = 2000;
	
	/** Indicates that the engine should stop. */
	protected static final int STOP_ENGINE = 4000;
	
	/** Indicates that a brick should be started. */
	protected static final int START_BRICK = 10000;

	/** Indicates that a brick should be suspended. */
	protected static final int SUSPEND_BRICK = 11000;

	/** Indicates that a brick should be resumed. */
	protected static final int RESUME_BRICK = 12000;

	/** Indicates that a brick should be stopped. */
	protected static final int STOP_BRICK = 13000;
	
	//Track and manage the state of the engine
	private boolean initialized;
	
	/** The actions to take on the engine. */
	protected Vector engineActions;
	
	/** The current state of the engine. */
	protected int engineState;
	
	/** This lock should be acquired before changing the engine state. */
	protected Object engineStateLock;
	
	//Track and manage the state of each brick
	
	/** This lock should be acquired before changing the brick actions. */
	protected Object brickStateLock;
	
	/** This Vector contains actions that should be performed on bricks, in order as a queue. */
	protected Vector brickActions;
	
	/** This table maps brick identifiers to brick states */
	protected Hashtable brickStates;
	
	/** <code>ArchitectureController</code> for this engine. */
	protected LocalArchitectureController controller = null;
	
	/** <code>ArchitectureEngineListener</code>s for this engine. */
	protected Vector engineListeners = new Vector();
	
	/**
	 * Creates a new AbstractArchitectureEngine.
	 * The initial state of the engine is ENGINESTATE_READY and the engine
	 * contains no references to bricks.
	 */
	public AbstractArchitectureEngine(){
		this.engineActions = new Vector();
		engineStateLock = new Object();
		engineState = ENGINESTATE_READY;
		
		brickStateLock = new Object();
		brickActions = new Vector();
		brickStates = new Hashtable();
		
		this.start();
	}
	
	/**
	 * Implementation of the administrative thread for this engine.  The
	 * administrative thread starts, suspends, resumes, and stops the
	 * bricks in the architecture.  The administrative thread is required
	 * to be separate so a brick can change its own state.  For instance,
	 * without an administrative thread, a brick that tries to stop itself
	 * will deadlock since the control thread for that brick will 
	 * wait for itself to go idle, which it won't, since it's waiting
	 * for itself to stop.
	 */
	public synchronized void run(){
		while(true){
			while(engineActions.size() == 0){
				try{
					//System.err.println("Engine waiting to wake.");
					wait();
					//System.err.println("Engine woke.");
				}
				catch(InterruptedException e){
				}
			}

			int engineAction = ((Integer)engineActions.elementAt(0)).intValue();
			engineActions.removeElementAt(0);
			
			//System.err.println("Engine checking.");
			
			//We may have been woken up to check the engine.  Let's do it.
			while(true){
				BrickAction ba;
				synchronized(brickActions){
					if(brickActions.size() == 0){
						break;
					}
					ba = (BrickAction)brickActions.elementAt(0);
					brickActions.removeElementAt(0);
				}
				
				int brickAction = ba.getBrickAction();
				Identifier[] ids = ba.getIdentifiers();
				switch(brickAction){
				case START_BRICK:
					synchronized(brickStateLock){
						//System.err.println("Got start action.");
						//System.err.println(brickStates);
						doStart(ids);
						for(int i = 0; i < ids.length; i++){
							//System.err.println("Getting brick: " + ids[i]);
							Brick b = controller.getBrick(ids[i]);
							b.start();
						}
						synchronized(brickStates){
							for(int i = 0; i < ids.length; i++){
								brickStates.put(ids[i], new Integer(STATE_OPEN_RUNNING));
							}
						}
						brickStateLock.notifyAll();
					}
					fireBricksStarted(ids);
					break;
				case SUSPEND_BRICK:
					synchronized(brickStateLock){
						doSuspend(ids);
						for(int i = 0; i < ids.length; i++){
							Brick b = controller.getBrick(ids[i]);
							b.suspend();
						}
						synchronized(brickStates){
							for(int i = 0; i < ids.length; i++){
								brickStates.put(ids[i], new Integer(STATE_OPEN_NOTRUNNING_SUSPENDED));
							}
						}
						brickStateLock.notifyAll();
					}
					fireBricksSuspended(ids);
					break;
				case RESUME_BRICK:
					synchronized(brickStateLock){
						doResume(ids);
						for(int i = 0; i < ids.length; i++){
							Brick b = controller.getBrick(ids[i]);
							b.resume();
						}
						synchronized(brickStates){
							for(int i = 0; i < ids.length; i++){
								brickStates.put(ids[i], new Integer(STATE_OPEN_RUNNING));
							}
						}
						brickStateLock.notifyAll();
					}
					fireBricksResumed(ids);
					break;
				case STOP_BRICK:
					synchronized(brickStateLock){
						doStop(ids);
						for(int i = 0; i < ids.length; i++){
							Brick b = controller.getBrick(ids[i]);
							b.stop();
						}
						synchronized(brickStates){
							for(int i = 0; i < ids.length; i++){
								brickStates.put(ids[i], new Integer(STATE_CLOSED_COMPLETED));
							}
						}
						brickStateLock.notifyAll();
					}
					fireBricksStopped(ids);
					break;
				}
			}
			
			switch(engineAction){
			case START_ENGINE:
				synchronized(engineStateLock){
					//System.err.println("Engine started.");
					if(!initialized){
						initThis();
						init();
						initialized = true;
					}
					doStartEngine();
					engineState = ENGINESTATE_STARTED;
					engineStateLock.notifyAll();
				}
				fireEngineStarted();
				break;
			case STOP_ENGINE:
				synchronized(engineStateLock){
					doStopEngine();
					engineState = ENGINESTATE_STOPPED;
					engineStateLock.notifyAll();
				}
				fireEngineStopped();
				break;
			}
			
			//engineAction = WAIT_ENGINE;
		}
	}
	
	/**
	 * Returns the state of the engine.
	 * @return state of the engine
	 */
	public int getEngineState(){
		return engineState;
	}
	
	/**
	 * Blocks the current thread until the engine enters the given state or
	 * the thread is interrupted.
	 * @param state State to wait for.
	 * @exception InterruptedException if the thread is interrupted.
	 */
	public void waitEngineState(int state) throws InterruptedException{
		synchronized(engineStateLock){
			while(getEngineState() != state){
				engineStateLock.wait();
			}
		}
	}
	
	/**
	 * Initializes the engine at the <code>AbstractArchitectureManager</code> level.
	 */
	private void initThis(){
		//System.out.println("initThis called.");
		Brick[] bricks = controller.getAllBricks();
		for(int i = 0; i < bricks.length; i++){
			//System.err.println("got brick: " + bricks[i]);
			brickStates.put(bricks[i].getIdentifier(), new Integer(STATE_OPEN_NOTRUNNING));
		}
	}
	
	/**
	 * Begins starting the engine.  Callers should check the engine state later to find out
	 * whether the engine has actually started or not.
	 */
	public synchronized void startEngine(){
		//engineAction = START_ENGINE;
		engineActions.addElement(new Integer(START_ENGINE));
		notify();
	}
	
	/**
	 * Causes the administrative thread to wake and check the state of the engine.
	 * Usually called when a change has been made (mostly in the bricks) that needs
	 * to be checked by the administrative thread.
	 */
	protected synchronized void checkEngine(){
		//engineAction = CHECK_ENGINE;
		engineActions.addElement(new Integer(CHECK_ENGINE));
		notify();
	}
	
	/**
	 * Begins stopping the engine.  Callers should check the engine state later to find out
	 * whether the engine has actually stopped or not.
	 */
	public synchronized void stopEngine(){
		//engineAction = STOP_ENGINE;
		engineActions.addElement(new Integer(STOP_ENGINE));
		notify();
	}

	/**
	 * Begins starting all the bricks managed by the engine.  Callers should check the
	 * brick state(s) later to find out whether the bricks have started or not.
	 */
	public void startAll(){
		start(getAllIds());
	}
	
	/**
	 * Begins suspending all the bricks managed by the engine.  Callers should check the
	 * brick state(s) later to find out whether the bricks have suspended or not.
	 */
	public void suspendAll(){
		suspend(getAllIds());
	}
	
	/**
	 * Begins resuming all the bricks managed by the engine.  Callers should check the
	 * brick state(s) later to find out whether the bricks have resumed or not.
	 */
	public void resumeAll(){
		resume(getAllIds());
	}
	
	/**
	 * Begins stopping all the bricks managed by the engine.  Callers should check the
	 * brick state(s) later to find out whether the bricks have stopped or not.
	 */
	public void stopAll(){
		stop(getAllIds());
	}
	
	/**
	 * Gets all the identifiers of all the bricks driven by this engine.
	 * @return Array of all identifiers driven by this engine.
	 */
	private Identifier[] getAllIds(){
		synchronized(brickStates){
			int size = brickStates.size();
			Identifier[] ids = new Identifier[size];
			int i = 0;
			for(Enumeration en = brickStates.keys(); en.hasMoreElements(); ){
				ids[i++] = (Identifier)en.nextElement();
			}
			return ids;
		}
	}
	
	/**
	 * Begins starting the brick with the given identifier.  Callers should check the
	 * state of the brick later to figure out whether it has started.
	 * @param id <code>Identifier</code> of brick to start.
	 */
	public void start(Identifier id){
		start(new Identifier[]{id});
	}

	/**
	 * Begins suspending the brick with the given identifier.  Callers should check the
	 * state of the brick later to figure out whether it has suspended.
	 * @param id <code>Identifier</code> of brick to suspend.
	 */
	public void suspend(Identifier id){
		suspend(new Identifier[]{id});
	}
	
	/**
	 * Begins resuming the brick with the given identifier.  Callers should check the
	 * state of the brick later to figure out whether it has resumed.
	 * @param id <code>Identifier</code> of brick to resume.
	 */
	public void resume(Identifier id){
		resume(new Identifier[]{id});
	}
	
	/**
	 * Begins stopping the brick with the given identifier.  Callers should check the
	 * state of the brick later to figure out whether it has stopped.
	 * @param id <code>Identifier</code> of brick to stop.
	 */
	public void stop(Identifier id){
		stop(new Identifier[]{id});
	}
	
	/**
	 * Begins starting the bricks with the given identifiers.  Callers should check the
	 * state of the bricks later to figure out whether they have started.
	 * @param id <code>Identifier</code>s of bricks to start.
	 */
	public void start(Identifier[] ids){
		//new Exception("Starting called:").printStackTrace();
		//for(int i = 0; i < ids.length; i++){ System.err.println("starting: " + ids[i]);		}
		BrickAction ba = new BrickAction(START_BRICK, ids);
		synchronized(brickActions){
			brickActions.addElement(ba);
		}
		checkEngine();
	}
	
	/**
	 * Begins suspending the bricks with the given identifiers.  Callers should check the
	 * state of the bricks later to figure out whether they have suspended.
	 * @param id <code>Identifier</code>s of bricks to suspend.
	 */
	public void suspend(Identifier[] ids){
		BrickAction ba = new BrickAction(SUSPEND_BRICK, ids);
		synchronized(brickActions){
			brickActions.addElement(ba);
		}
		checkEngine();
	}

	/**
	 * Begins resuming the bricks with the given identifiers.  Callers should check the
	 * state of the bricks later to figure out whether they have resumed.
	 * @param id <code>Identifier</code>s of bricks to resume.
	 */
	public void resume(Identifier[] ids){
		BrickAction ba = new BrickAction(RESUME_BRICK, ids);
		synchronized(brickActions){
			brickActions.addElement(ba);
		}
		checkEngine();
	}
	
	/**
	 * Begins stopping the bricks with the given identifiers.  Callers should check the
	 * state of the bricks later to figure out whether they have stopped.
	 * @param id <code>Identifier</code>s of bricks to stop.
	 */
	public void stop(Identifier[] ids){
		BrickAction ba = new BrickAction(STOP_BRICK, ids);
		synchronized(brickActions){
			brickActions.addElement(ba);
		}
		checkEngine();
	}
	
	/**
	 * Gets the states of all the bricks driven by this engine.
	 * Because the states are not ordered, this method is usually used to get a
	 * picture of the state of the whole system (are all bricks stopped, etc.).
	 * @return an array specifying the state of each brick.
	 */
	public int[] getStateAll(){
		synchronized(brickStates){
			int size = brickStates.size();
			int[] states = new int[size];
			int i = 0;
			for(Enumeration en = brickStates.elements(); en.hasMoreElements(); ){
				states[i++] = ((Integer)en.nextElement()).intValue();
			}
			return states;
		}
	}
	
	/**
	 * Gets the state of a brick driven by this engine.
	 * @param id <code>Identifier</code> of the brick.
	 * @return State of the brick.
	 */
	public int getState(Identifier id){
		Integer state = (Integer)brickStates.get(id);
		if(state == null){
			throw new IllegalArgumentException("Brick " + id + " does not exist.");
		}
		return state.intValue();
	}
	
	/**
	 * Gets the states of a set of bricks driven by this engine.
	 * @param ids <code>Identifier</code>s of the bricks.
	 * @return States of the bricks, in the same order as the array of ids.
	 */
	public int[] getState(Identifier[] ids){
		int[] states = new int[ids.length];
		synchronized(brickStates){
			for(int i = 0; i < ids.length; i++){
				states[i] = getState(ids[i]);
			}
		}
		return states;
	}
	
	/**
	 * Blocks the current thread until all the bricks managed by the engine
	 * have reached a certain state.
	 * @param state Brick state to wait for.
	 * @exception InterruptedException if the current thread is interrupted.
	 */
	public void waitStateAll(int state) throws InterruptedException{
		waitState(getAllIds(), state);
	}
	
	/**
	 * Blocks the current thread until a brick managed by the engine
	 * has reached a certain state.
	 * @param id Brick to wait for.
	 * @param state Brick state to wait for.
	 * @exception InterruptedException if the current thread is interrupted.
	 */
	public void waitState(Identifier id, int state) throws InterruptedException{
		waitState(new Identifier[]{id}, state);
	}
	
	/**
	 * Blocks the current thread until a set of bricks managed by the engine
	 * has reached a certain state.
	 * @param ids Bricks to wait for.
	 * @param state Brick state to wait for.
	 * @exception InterruptedException if the current thread is interrupted.
	 */
	public void waitState(Identifier[] ids, int state) throws InterruptedException{
		int[] stateArray = new int[ids.length];
		for(int i = 0; i < stateArray.length; i++){
			stateArray[i] = state;
		}
		waitState(ids, stateArray);
	}
	
	/**
	 * Blocks the current thread until a set of bricks managed by the engine
	 * has reached a certain set of states.
	 * @param ids Brick to wait for.
	 * @param state Brick states to wait for.  This array must be the same length as the <code>ids</code> array.
	 * @exception InterruptedException if the current thread is interrupted.
	 */
	public void waitState(Identifier[] ids, int[] state) throws InterruptedException{
		if(ids.length != state.length){
			throw new IllegalArgumentException("Length of identifier array must equal length of state array.");
		}
		synchronized(brickStateLock){
			while(true){
				boolean mismatch = false;
				int[] currentStates = getState(ids);
				for(int i = 0; i < currentStates.length; i++){
					if(currentStates[i] != state[i]){
						mismatch = true;
						break;
					}
				}
				if(!mismatch){
					return;		//All done waiting
				}
				//We have to wait.
				brickStateLock.wait(250);
			}
		}
	}
	
	/**
	 * Get the identifiers of any bricks with the given state.
	 * @param state Identifiers of any bricks with this state will be returned.
	 * @return Identifiers of any bricks with the given state.
	 */
	public Identifier[] getBrickIds(int state){
		return getBrickIds(new int[]{state});
	}
	
	/**
	 * Get the identifiers of any bricks with any of the given states.
	 * @param states Identifiers of any bricks with any of these states will be returned.
	 * @return Identifiers of any bricks with the given states.
	 */
	public Identifier[] getBrickIds(int[] states){
		synchronized(brickStates){
			Identifier[] bigArray = new Identifier[brickStates.size()];
			int realLen = 0;
			for(Enumeration en = brickStates.keys(); en.hasMoreElements(); ){
				Identifier id = (Identifier)en.nextElement();
				int st = ((Integer)brickStates.get(id)).intValue();
				for(int j = 0; j < states.length; j++){
					if(st == states[j]){
						bigArray[realLen++] = id;
						break;
					}
				}
			}
			if(realLen == 0){
				return new Identifier[0];
			}
			else{
				Identifier[] realArray = new Identifier[realLen];
				System.arraycopy(bigArray, 0, realArray, 0, realLen);
				return realArray;
			}
		}
	}

	//ArchitectureEngineListener methods
	
	/**
	 * Adds an <code>ArchitectureEngineListener</code> to this object that watches
	 * changes to brick and engine states.
	 * @param l Listener to add.
	 */
	public void addArchitectureEngineListener(ArchitectureEngineListener l){
		synchronized(engineListeners){
			engineListeners.addElement(l);
		}
	}
	
	/**
	 * Removes an <code>ArchitectureEngineListener</code> from this object that will no longer watch
	 * changes to brick and engine states.
	 * @param l Listener to remove.
	 */
	public void removeArchitectureEngineListener(ArchitectureEngineListener l){
		synchronized(engineListeners){
			engineListeners.removeElement(l);
		}
	}

	/**
	 * Notifies <code>ArchitectureEngineListener</code>s that this engine has started.
	 */
	protected void fireEngineStarted(){
		synchronized(engineListeners){
			for(int i = 0; i < engineListeners.size(); i++){
				((ArchitectureEngineListener)engineListeners.elementAt(i)).engineStarted(this);
			}
		}
	}

	/**
	 * Notifies <code>ArchitectureEngineListener</code>s that this engine has started.
	 */
	protected void fireEngineStopped(){
		synchronized(engineListeners){
			for(int i = 0; i < engineListeners.size(); i++){
				((ArchitectureEngineListener)engineListeners.elementAt(i)).engineStopped(this);
			}
		}
	}

	/**
	 * Notifies <code>ArchitectureEngineListener</code>s that this engine has started one
	 * or more bricks.
	 * @param ids Identifiers of bricks that were started.
	 */
	protected void fireBricksStarted(Identifier[] ids){
		synchronized(engineListeners){
			for(int i = 0; i < engineListeners.size(); i++){
				((ArchitectureEngineListener)engineListeners.elementAt(i)).bricksStarted(this, ids);
			}
		}
	}
	
	/**
	 * Notifies <code>ArchitectureEngineListener</code>s that this engine has suspended one
	 * or more bricks.
	 * @param ids Identifiers of bricks that were suspended.
	 */
	protected void fireBricksSuspended(Identifier[] ids){
		synchronized(engineListeners){
			for(int i = 0; i < engineListeners.size(); i++){
				((ArchitectureEngineListener)engineListeners.elementAt(i)).bricksSuspended(this, ids);
			}
		}
	}
	
	/**
	 * Notifies <code>ArchitectureEngineListener</code>s that this engine has resumed one
	 * or more bricks.
	 * @param ids Identifiers of bricks that were resumed.
	 */
	protected void fireBricksResumed(Identifier[] ids){
		synchronized(engineListeners){
			for(int i = 0; i < engineListeners.size(); i++){
				((ArchitectureEngineListener)engineListeners.elementAt(i)).bricksResumed(this, ids);
			}
		}
	}
	
	/**
	 * Notifies <code>ArchitectureEngineListener</code>s that this engine has stopped one
	 * or more bricks.
	 * @param ids Identifiers of bricks that were stopped.
	 */
	protected void fireBricksStopped(Identifier[] ids){
		synchronized(engineListeners){
			for(int i = 0; i < engineListeners.size(); i++){
				((ArchitectureEngineListener)engineListeners.elementAt(i)).bricksStopped(this, ids);
			}
		}
	}
	
	//ArchitectureListener methods
	
	/**
	 * <code>ArchitectureListener</code> method that adds bricks to the engine
	 * as they are added to the manager.  Bricks in this engine start in the
	 * <code>STATE_OPEN_NOTRUNNING</code> state.
	 * @param manager <code>ArchitectureManager</code> that made this call.
	 * @param id Identifier of the brick that was added.
	 */
	public void brickAdded(ArchitectureManager manager, Identifier id){
		brickStates.put(id, new Integer(STATE_OPEN_NOTRUNNING));
	}
	
	/**
	 * <code>ArchitectureListener</code> method that removes bricks from the engine
	 * as they are removed from the manager.  Bricks should not be removed until 
	 * they are stopped or in a state where they are able to be removed.
	 * @param manager <code>ArchitectureManager</code> that made this call.
	 * @param id Identifier of the brick that was removed.
	 */
	public void brickRemoved(ArchitectureManager manager, Identifier id){
		brickStates.remove(id);
	}
	
	/**
	 * Empty.
	 */
	public void brickRemoving(ArchitectureManager manager, Identifier id){
	}
	
	//We don't care about when welds are added or removed in this class.
	/**
	 * Empty.
	 */
	public void weldAdded(ArchitectureManager manager, Weld w){}
	/**
	 * Empty.
	 */
	public void weldRemoving(ArchitectureManager manager, Weld w){}
	/**
	 * Empty.
	 */
	public void weldRemoved(ArchitectureManager manager, Weld w){}
	
	/**
	 * Sets the <code>ArchitectureController</code> for this engine.
	 * @param controller Controller for this engine.
	 */
	public void setController(ArchitectureController controller){
		if(!(controller instanceof LocalArchitectureController)){
			throw new IllegalArgumentException("This engine does not support non-local ArchitectureControllers.");
		}
		this.controller = (LocalArchitectureController)controller;
		controller.addArchitectureListener(this);
	}
	
	/**
	 * Gets the <code>ArchitectureController</code> for this engine.
	 * @return Controller for this engine.
	 */
	public ArchitectureController getController(){
		return controller;
	}
	
	//-------------------------------------------------------------------------
	//Abstract methods to be implemented by subclasses
	
	/**
	 * This method is called when the engine is started so threads can be created
	 * for bricks in the architecture when necessary.  <B>This method must be implemented
	 * by subclasses.</B>
	 */
	protected abstract void init();
	
	//These must guarantee that the engine/brick is
	//stopped/started/suspended/whatever when they return
	//to the best of the engine's ability.
	
	/**
	 * This method is called when the engine should be started.  
	 * <B>This method must be implemented by subclasses.</B>  Subclasses should
	 * ensure that the engine is started and running when this method call
	 * returns.
	 */
	protected abstract void doStartEngine();
	
	/**
	 * This method is called when the engine should be stopped.
	 * <B>This method must be implemented by subclasses.</B>  Subclasses should
	 * ensure that the engine is stopped when this method call
	 * returns.
	 */
	protected abstract void doStopEngine();
	
	/**
	 * This method is called when the engine should start a set of bricks.
	 * <B>This method must be implemented by subclasses.</B>  Subclasses should
	 * ensure that the bricks are started when this method call
	 * returns.
	 * @param ids Identifiers of bricks to start.
	 */
	protected abstract void doStart(Identifier[] ids);

	/**
	 * This method is called when the engine should suspend a set of bricks.
	 * <B>This method must be implemented by subclasses.</B>  Subclasses should
	 * ensure that the bricks are suspended when this method call
	 * returns.
	 * @param ids Identifiers of bricks to suspend.
	 */
	protected abstract void doSuspend(Identifier[] ids);

	/**
	 * This method is called when the engine should resume a set of bricks.
	 * <B>This method must be implemented by subclasses.</B>  Subclasses should
	 * ensure that the bricks are resumed when this method call
	 * returns.
	 * @param ids Identifiers of bricks to resume.
	 */
	protected abstract void doResume(Identifier[] ids);

	/**
	 * This method is called when the engine should stop a set of bricks.
	 * <B>This method must be implemented by subclasses.</B>  Subclasses should
	 * ensure that the bricks are stopped when this method call
	 * returns.
	 * @param ids Identifiers of bricks to stop.
	 */
	protected abstract void doStop(Identifier[] ids);

}

