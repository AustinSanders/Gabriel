//!!(C)!!

package c2.fw;

/**
 * Specifies an interface for classes implementing a threading/scheduling policy
 * for an <code>ArchitectureManager</code>.  The job of an <code>ArchitectureEngine</code>,
 * while it is running, is to get a message from a <code>MessageHandler</code> and
 * process it by passing it to the destination brick's <code>handle</code> method
 * as appropriate.  Policies that can be encapsulated in the engine include
 * Thread-Pool, One-Thread-Per-Brick, etc.  Most developers will probably find it convenient
 * to extend <code>AbstractArchitectureEngine</code> when implementing their own engine.
 * 
 * @see c2.fw.ArchitectureManager
 * @see c2.fw.AbstractArchitectureEngine
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface ArchitectureEngine{

	//Possible states the engine can be in.
	/** Engine state: ready, waiting to be started */
	public static final int ENGINESTATE_READY = 40;
	/** Engine state: started, able to process messages */
	public static final int ENGINESTATE_STARTED = 50;
	/** Engine state: stopped, no longer able to process messages */
	public static final int ENGINESTATE_STOPPED = 60;
	
	//Possible states a component/connector can be in.
	
	/** Brick state: Ready, but not started yet. */
	public static final int STATE_OPEN_NOTRUNNING = 100;
	/** Brick state: Started, but suspended (not processing messages). */
	public static final int STATE_OPEN_NOTRUNNING_SUSPENDED = 200;
	/** Brick state: Started, processing messages. */
	public static final int STATE_OPEN_RUNNING = 300;
	/** Brick state: Stopped, no longer processing messages. */
	public static final int STATE_CLOSED_COMPLETED = 400;
	/** Brick state: Stopped, no longer processing messages, completed abnormally. */
	public static final int STATE_CLOSED_ABNORMALCOMPLETED = 500;
	/** Brick state: Stopped, no longer processing messages, was terminated abnormally. */
	public static final int STATE_CLOSED_ABNORMALCOMPLETED_TERMINATED = 600;
	/** Brick state: Stopped, no longer processing messages, aborted internally. */
	public static final int STATE_CLOSED_ABNORMALCOMPLETED_ABORTED = 700;
	
	/**
	 * Sets the architecture controller associated with this engine.
	 * @param manager The <code>ArchitectureController</code> driven by this engine.
	 */
	public void setController(ArchitectureController controller);

	/**
	 * Gets the architecture controller associated with this engine.
	 * @return The <code>ArchitectureController</code> driven by this engine.
	 */
	public ArchitectureController getController();

	/**
	 * Adds the specified <code>ArchitectureEngineListener</code> to receive events
	 * from this <code>ArchitectureEngine</code>.
	 * @param l the <code>ArchitectureEngineListener</code>.
	 */
	public void addArchitectureEngineListener(ArchitectureEngineListener l);
	
	/**
	 * Removes the specified <code>ArchitectureEngineListener</code> so that it no
	 * longer receives events from this <code>ArchitectureEngine</code>.
	 * @param l the <code>ArchitectureEngineListener</code>.
	 */
	public void removeArchitectureEngineListener(ArchitectureEngineListener l);
	
	/**
	 * Begins starting the engine.  Callers should check later to find out when the engine
	 * has actually started.  All bricks start in the <code>STATE_OPEN_NOTRUNNING</code> state.
	 */
	public void startEngine();

	/**
	 * Begins stopping the engine.  Callers should check later to find out when the engine
	 * has actually stopped.  Callers should stop all bricks before stopping the engine.
	 */
	public void stopEngine();

	/**
	 * Gets the state of the engine as one of the <code>ENGINESTATE_*</code> values.
	 * @return State of the engine.
	 */
	public int getEngineState();

	/**
	 * Blocks the current thread until the engine reaches the given state.
	 * @param state The engine state to wait for.
	 * @exception InterruptedException if the current thread is interrupted while waiting.
	 */
	public void waitEngineState(int state) throws InterruptedException;
	
	/**
	 * Begins starting all the bricks managed by the engine.  Callers should check the
	 * brick state(s) later to find out whether the bricks have started or not.
	 */
	public void startAll();

	/**
	 * Begins suspending all the bricks managed by the engine.  Callers should check the
	 * brick state(s) later to find out whether the bricks have suspended or not.
	 */
	public void suspendAll();

	/**
	 * Begins resuming all the bricks managed by the engine.  Callers should check the
	 * brick state(s) later to find out whether the bricks have resumed or not.
	 */
	public void resumeAll();

	/**
	 * Begins stopping all the bricks managed by the engine.  Callers should check the
	 * brick state(s) later to find out whether the bricks have stopped or not.
	 */
	public void stopAll();
	
	/**
	 * Begins starting the brick with the given identifier.  Callers should check the
	 * state of the brick later to figure out whether it has started.
	 * @param id <code>Identifier</code> of brick to start.
	 */
	public void start(Identifier id);

	/**
	 * Begins suspending the brick with the given identifier.  Callers should check the
	 * state of the brick later to figure out whether it has suspended.
	 * @param id <code>Identifier</code> of brick to suspend.
	 */
	public void suspend(Identifier id);

	/**
	 * Begins resuming the brick with the given identifier.  Callers should check the
	 * state of the brick later to figure out whether it has resumed.
	 * @param id <code>Identifier</code> of brick to resume.
	 */
	public void resume(Identifier id);

	/**
	 * Begins stopping the brick with the given identifier.  Callers should check the
	 * state of the brick later to figure out whether it has stopped.
	 * @param id <code>Identifier</code> of brick to stop.
	 */
	public void stop(Identifier id);
	
	/**
	 * Begins starting the bricks with the given identifiers.  Callers should check the
	 * state of the bricks later to figure out whether they have started.
	 * @param id <code>Identifier</code>s of bricks to start.
	 */
	public void start(Identifier[] ids);

	/**
	 * Begins suspending the bricks with the given identifiers.  Callers should check the
	 * state of the bricks later to figure out whether they have suspended.
	 * @param id <code>Identifier</code>s of bricks to suspend.
	 */
	public void suspend(Identifier[] ids);

	/**
	 * Begins resuming the bricks with the given identifiers.  Callers should check the
	 * state of the bricks later to figure out whether they have resumed.
	 * @param id <code>Identifier</code>s of bricks to resume.
	 */
	public void resume(Identifier[] ids);

	/**
	 * Begins stopping the bricks with the given identifiers.  Callers should check the
	 * state of the bricks later to figure out whether they have stopped.
	 * @param id <code>Identifier</code>s of bricks to stop.
	 */
	public void stop(Identifier[] ids);
	
	/**
	 * Gets the states of all the bricks driven by this engine.
	 * Because the states are not ordered, this method is usually used to get a
	 * picture of the state of the whole system (are all bricks stopped, etc.).
	 * @return an array specifying the state of each brick.
	 */
	public int[] getStateAll();

	/**
	 * Gets the state of a brick driven by this engine.
	 * @param id <code>Identifier</code> of the brick.
	 * @return State of the brick.
	 */
	public int getState(Identifier id);

	/**
	 * Gets the states of a set of bricks driven by this engine.
	 * @param ids <code>Identifier</code>s of the bricks.
	 * @return States of the bricks, in the same order as the array of ids.
	 */
	public int[] getState(Identifier[] ids);
	
	/**
	 * Get the identifiers of any bricks with the given state.
	 * @param state Identifiers of any bricks with this state will be returned.
	 * @return Identifiers of any bricks with the given state.
	 */
	public Identifier[] getBrickIds(int state);

	/**
	 * Get the identifiers of any bricks with any of the given states.
	 * @param states Identifiers of any bricks with any of these states will be returned.
	 * @return Identifiers of any bricks with the given states.
	 */
	public Identifier[] getBrickIds(int[] states);
	
	/**
	 * Blocks the current thread until all the bricks managed by the engine
	 * have reached a certain state.
	 * @param state Brick state to wait for.
	 * @exception InterruptedException if the current thread is interrupted.
	 */
	public void waitStateAll(int state) throws InterruptedException;

	/**
	 * Blocks the current thread until a brick managed by the engine
	 * has reached a certain state.
	 * @param id Brick to wait for.
	 * @param state Brick state to wait for.
	 * @exception InterruptedException if the current thread is interrupted.
	 */
	public void waitState(Identifier id, int state) throws InterruptedException;

	/**
	 * Blocks the current thread until a set of bricks managed by the engine
	 * has reached a certain state.
	 * @param ids Bricks to wait for.
	 * @param state Brick state to wait for.
	 * @exception InterruptedException if the current thread is interrupted.
	 */
	public void waitState(Identifier[] ids, int state) throws InterruptedException;
	
	/**
	 * Blocks the current thread until a set of bricks managed by the engine
	 * has reached a certain set of states.
	 * @param ids Brick to wait for.
	 * @param state Brick states to wait for.  This array must be the same length as the <code>ids</code> array.
	 * @exception InterruptedException if the current thread is interrupted.
	 */
	public void waitState(Identifier[] ids, int[] state) throws InterruptedException;

}

