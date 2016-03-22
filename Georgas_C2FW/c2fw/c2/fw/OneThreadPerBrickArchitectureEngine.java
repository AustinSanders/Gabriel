package c2.fw;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <code>ArchitectureEngine</code> that assigns one thread to each brick in the
 * architecture.  Messages are handled in sequence by the brick.
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */

public class OneThreadPerBrickArchitectureEngine extends AbstractArchitectureEngine implements ArchitectureEngine, ArchitectureListener{
	/** Maps identifiers to threads */
	protected Hashtable threads;
	
	/**
	 * Creates a new OneThreadPerBrickArchitectureEngine.
	 */
	public OneThreadPerBrickArchitectureEngine(){
		super();
		threads = new Hashtable();
	}
	
	/**
	 * This is a no-op for this engine; all threads are created
	 * when bricks are added.
	 */
	protected void init(){
	}
	
	/**
	 * Starts each brick thread.
	 */
	public void doStartEngine(){
		//init();
		for(Enumeration en = threads.elements(); en.hasMoreElements(); ){
			BrickThread th = (BrickThread)en.nextElement();
			th.start();
		}
	}
		
	/**
	 * Sends a termination signal to each brick thread and waits for all of them
	 * to terminate.
	 */
	public void doStopEngine(){
		for(Enumeration en = threads.elements(); en.hasMoreElements(); ){
			BrickThread th = (BrickThread)en.nextElement();
			th.terminate();
		}
	}

	private boolean handlingMessage(BrickThread[] bts){
		for(int i = 0; i < bts.length; i++){
			if(bts[i].isWorking()){
				return true;
			}
		}
		return false;
	}
	
	private boolean isTerminated(BrickThread[] bts){
		for(int i = 0; i < bts.length; i++){
			if(!bts[i].isTerminated()){
				return false;
			}
		}
		return true;
	}

	/**
	 * Starts the given bricks by setting each of their threads to "active."
	 * @param ids Identifiers of the bricks to start.
	 */
	protected void doStart(Identifier[] ids){
		BrickThread[] bts = new BrickThread[ids.length];
		for(int i = 0; i < ids.length; i++){
			//System.out.println("Starting specific brick: " + ids[i]);
			//System.err.println("Looking for brick: " + ids[i]);
			//System.out.println("My controller is: " + getController());
			//System.out.println("The brickthreads I have are: " + threads);
			BrickThread bt = (BrickThread)threads.get(ids[i]);
			if(bt == null){
				throw new IllegalArgumentException("The brick " + ids[i] + " does not exist.");
			}
			else{
				bts[i] = bt;
			}
		}
		
		for(int i = 0; i < bts.length; i++){
			bts[i].setActive(true);
		}
	}
		
	/**
	 * Suspends the given bricks by setting each of their threads to "inactive" and waiting
	 * for the threads to stop processing messages.
	 * @param ids Identifiers of the bricks to suspend.
	 */
	protected void doSuspend(Identifier[] ids){
		//System.err.println("In doSuspend");
		BrickThread[] bts = new BrickThread[ids.length];
		for(int i = 0; i < ids.length; i++){
			BrickThread bt = (BrickThread)threads.get(ids[i]);
			if(bt == null){
				throw new IllegalArgumentException("The brick " + ids[i] + " does not exist.");
			}
			else{
				bts[i] = bt;
			}
		}
		
		//System.out.println("Setting all inactive.");
		for(int i = 0; i < bts.length; i++){
			bts[i].setActive(false);
		}
		
		//System.err.println("Checking for handle.");
		//Wait for all the threads to stop handling messages.
		while(handlingMessage(bts)){
			try{
				Thread.sleep(100);
			}
			catch(InterruptedException e){
			}
		}
		//System.err.println("Done with doSuspend.");
	}
		
	/**
	 * Resumes the given bricks by setting each of their threads to "active".
	 * @param ids Identifiers of the bricks to suspend.
	 */
	protected void doResume(Identifier[] ids){
		BrickThread[] bts = new BrickThread[ids.length];
		for(int i = 0; i < ids.length; i++){
			BrickThread bt = (BrickThread)threads.get(ids[i]);
			if(bt == null){
				throw new IllegalArgumentException("The brick " + ids[i] + " does not exist.");
			}
			else{
				bts[i] = bt;
			}
		}
		
		for(int i = 0; i < bts.length; i++){
			bts[i].setActive(true);
		}
	}
		
	/**
	 * Stops the given bricks by sending the "terminate" signal to each of the brick threads and
	 * waiting for them to stop processing messages.
	 * @param ids Identifiers of the bricks to stop.
	 */
	protected void doStop(Identifier[] ids){
		BrickThread[] bts = new BrickThread[ids.length];
		for(int i = 0; i < ids.length; i++){
			BrickThread bt = (BrickThread)threads.get(ids[i]);
			if(bt == null){
				throw new IllegalArgumentException("The brick " + ids[i] + " does not exist.");
			}
			else{
				bts[i] = bt;
			}
		}
		
		//Wait for all the threads to stop handling messages.
		for(int i = 0; i < bts.length; i++){
			bts[i].terminate();
		}
	}

	/**
	 * Called when a brick is added to the <code>ArchitectureManager</code>.
	 * Adds the brick to the list of bricks managed by this engine and
	 * creates a new thread for it.  If the engine has already been
	 * started, then the thread is started automatically.
	 * @param manager The <code>ArchitectureManager</code> that sent this event.
	 * @param id The <code>Identifier</code> of the brick that was added.
	 */
	public void brickAdded(ArchitectureManager manager, Identifier id){
		super.brickAdded(manager, id);
		BrickThread th = new BrickThread(controller, id);
		//Thread will start but will not be active.
		if(getEngineState() == ArchitectureEngine.ENGINESTATE_STARTED){
			th.start();
		}
		threads.put(id, th);
	}
	
	/**
	 * Called when a brick is removed from the <code>ArchitectureManager</code>.
	 * Removes the brick from the list of bricks managed by this engine.
	 * Bricks should not be removed until they are stopped.
	 * @param manager The <code>ArchitectureManager</code> that sent this event.
	 * @param id The <code>Identifier</code> of the brick that was removed.
	 */
	public void brickRemoved(ArchitectureManager manager, Identifier id){
		//System.err.println("Component removed: " + id);
		super.brickRemoved(manager, id);
		threads.remove(id);
	}

}

