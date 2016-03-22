//!!(C)!!
package c2.fw;

import java.util.Vector;

/**
 * <code>ArchitectureEngine</code> that uses one pool of shepherd threads for
 * the entire system.  These shepherd threads are shared among all bricks
 * in the system.
 * <P><FONT COLOR=#FF0000>Note!</FONT> This engine does not prevent two threads
 * from entering a component at the same time.  Furthermore, it does not prevent
 * messages from being received out of order unless there is only one thread
 * in the pool.  Take care to observe this!
 * 
 * @see c2.fw.ShepherdThread
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class ThreadPoolArchitectureEngine extends AbstractArchitectureEngine implements ArchitectureEngine{
	/** Number of threads in the thread pool. */
	protected int numThreads;
	
	/** The array of shepherd threads. */
	protected ShepherdThread[] threads;
	
	/** The bricks that are active (running) in the system. */
	protected Vector activeBricks;
	
	/**
	 * Create a new <code>ThreadPoolArchitectureEngine</code> with a default
	 * amount of threads (4).
	 */
	public ThreadPoolArchitectureEngine(){
		this(4);
	}
	
	/**
	 * Create a new <code>ThreadPoolArchitectureEngine</code> with the given
	 * amount of threads.
	 * @param numThreads Number of threads in the pool.
	 */
	public ThreadPoolArchitectureEngine(int numThreads){
		super();
		this.numThreads = numThreads;
		activeBricks = new Vector();
	}
	
	/**
	 * Initializes this engine.  Creates the shepherd threads.
	 */
	protected void init(){
		threads = new ShepherdThread[numThreads];
		for(int i = 0; i < numThreads; i++){
			threads[i] = new ShepherdThread(this, controller);
		}
	}
	
	/**
	 * Starts the engine.  All shepherd threads are started and set to
	 * active.  No components are set active, so no messages will be
	 * processed yet.
	 */
	protected void doStartEngine(){
		for(int i = 0; i < threads.length; i++){
			threads[i].start();
			threads[i].setActive(true);
		}
	}

	/**
	 * Stops the engine.  Stops all the bricks by removing them from the
	 * active bricks list, then terminates all the shepherd threads and
	 * waits for them to finish.
	 */
	protected void doStopEngine(){
		//Stop all the bricks.
		doStop(getActiveBricks());
		
		//Stop the shepherd threads.
		for(int i = 0; i < threads.length; i++){
			threads[i].terminate();
		}
		int count = -1;
		while(count < threads.length){
			count = 0;
			for(int i = 0; i < threads.length; i++){
				if(threads[i].isTerminated()){
					count++;
				}
			}
			//If all threads haven't terminated, give them 100ms to do so,
			//then check again.
			//System.out.println("The count is " + count);
			if(count < threads.length){
				try{
					//System.out.println("Current thread = " + Thread.currentThread());
					Thread.sleep(100);
				}
				catch(InterruptedException e){
				}
			}
		}
	}
	
	/** 
	 * Gets a list of identifiers of active bricks (<code>STATE_OPEN_RUNNING</code>) in the
	 * system.
	 * @return Array of <code>Identifier</code>s of bricks in the <code>STATE_OPEN_RUNING</code> state.
	 */
	public Identifier[] getActiveBricks(){
		synchronized(activeBricks){
			Identifier[] ids = new Identifier[activeBricks.size()];
			activeBricks.copyInto(ids);
			return ids;
		}
	}

	private boolean handlingMessage(Identifier[] ids){
		for(int i = 0; i < threads.length; i++){
			if(threads[i].isWorking()){
				Identifier id = threads[i].whereWorking();
				if(id == null){
					//If id == null, the thread isn't working in the brick now, 
					//but it might be working
					//in the given brick soon (i.e. it's still processing the
					//next message.)  Better to be safe than sorry and
					//report a false positive.
					return true;
				}
				else{
					for(int j = 0; j < ids.length; j++){
						if(id.equals(ids[j])){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Starts the given set of bricks.  Adds them to the active bricks list.
	 * @param ids Identifiers of bricks to start.
	 */
	protected void doStart(Identifier[] ids){
		synchronized(activeBricks){
			for(int i = 0; i < ids.length; i++){
				if(!activeBricks.contains(ids[i])){
					activeBricks.addElement(ids[i]);
				}
			}
		}

		//Nudge all the shepherd threads so they wake up and notice the
		//change in the active-bricks list
		for(int i = 0; i < threads.length; i++){
			threads[i].cycle();
		}
	}
	
	/**
	 * Suspends the given bricks.  Removes them from the active list and waits
	 * until they are finished processing the current message (if any).
	 * @param ids <code>Identifier</code>s of bricks to suspend.
	 */
	protected void doSuspend(Identifier[] ids){
		synchronized(activeBricks){
			for(int i = 0; i < ids.length; i++){
				activeBricks.removeElement(ids[i]);
			}
		}

		//Nudge all the shepherd threads so they wake up and notice the
		//change in the active-bricks list
		for(int i = 0; i < threads.length; i++){
			threads[i].cycle();
		}
		
		//Wait for all threads processing messages in these bricks to come back.
		//Since these bricks are no longer on the active list, they won't process
		//any more messages.
		while(handlingMessage(ids)){
			try{
				Thread.sleep(100);
			}
			catch(InterruptedException e){
			}
		}
	}
	
	/**
	 * Resumes the given set of bricks.  Adds them to the active bricks list.
	 * @param ids Identifiers of bricks to resume.
	 */
	protected void doResume(Identifier[] ids){
		synchronized(activeBricks){
			for(int i = 0; i < ids.length; i++){
				if(!activeBricks.contains(ids[i])){
					activeBricks.addElement(ids[i]);
				}
			}
		}

		//Nudge all the shepherd threads so they wake up and notice the
		//change in the active-bricks list
		for(int i = 0; i < threads.length; i++){
			threads[i].cycle();
		}
	}
	
	/**
	 * Stops the given set of bricks.  Removes them from the active bricks list,
	 * and waits for them to finish the current message, if any.
	 * @param ids Identifiers of bricks to stop.
	 */
	protected void doStop(Identifier[] ids){
		synchronized(activeBricks){
			for(int i = 0; i < ids.length; i++){
				activeBricks.removeElement(ids[i]);
			}
		}

		//Nudge all the shepherd threads so they wake up and notice the
		//change in the active-bricks list
		for(int i = 0; i < threads.length; i++){
			threads[i].cycle();
		}
		
		//Wait for all threads processing messages in these bricks to come back.
		//Since these bricks are no longer on the active list, they won't process
		//any more messages.
		while(handlingMessage(ids)){
			try{
				Thread.sleep(100);
			}
			catch(InterruptedException e){
			}
		}
	}
	
}

