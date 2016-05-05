//!!(C)!!
package c2.fw;

/**
 * Internally used class that gives a brick its own thread of control.  Used by
 * <code>OneThreadPerBrickArchitectureEngine</code>.
 * 
 * @see c2.fw.OneThreadPerBrickArchitectureEngine
 * @see c2.fw.ArchitectureEngine
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
class BrickThread extends Thread{
	private Identifier brickId;
	private Brick brick;
	private boolean isActive;
	private boolean isWorking;
	private boolean isTerminating;
	private boolean isTerminated;
	
	private LocalArchitectureController controller;
	
	/**
	 * Creates a new <code>BrickThread</code> with the given message handler
	 * to drive the brick with the given id.
	 * @param handler <code>MessageHandler</code> from which to select messages for the brick.
	 * @param brickId <code>Identifier</code> indicating which brick this thread should drive.
	 */
	public BrickThread(LocalArchitectureController controller, Identifier brickId){
		super(brickId.toString() + "-thread");
		this.setPriority(ThreadPriorities.NORM_PRIORITY);
		this.controller = controller;
		this.brickId = brickId;
		this.brick = controller.getBrick(brickId);
		this.isActive = false;
		this.isWorking = false;
		this.isTerminated = false;
	}
	
	private Object activeLock = new Object();

	/**
	 * Control method for this <code>BrickThread</code>.
	 */
	public void run(){
		try{
			isTerminated = false;
			isWorking = true;
			while(true){
				if(isTerminating){
					isWorking = false;
					isTerminated = true;
					return;
				}
				try{
					synchronized(activeLock){
						isWorking = false;
						while(!isActive){
							activeLock.wait();
						}
						isWorking = true;
					}
					Message m = controller.waitMessage(brickId);
					brick.handle(m);
					Thread.yield();
				}
				catch(InterruptedException ie){
				}
			}
		}
		finally{
			isTerminated = true;
		}
	}
	
	int terminateCounter = 0;
	
	/**
	 * Sets this thread to start terminating.  This method
	 * periodically interrupts the BrickThread until it
	 * is sure the BrickThread is dead, then returns.
	 */
	public void terminate(){
		if(isTerminated()) return;
		synchronized(activeLock){
			isTerminating = true;
			interrupt();
			activeLock.notifyAll();
		}
		Thread.yield();
		while(!isTerminated()){
			try{
				Thread.sleep(10);
				terminate();
			}
			catch(InterruptedException ie){}
		}
	}

	/**
	 * Sets this thread to be active or inactive.  An active thread will process messages,
	 * an inactive thread will not.  If a thread is set inactive while processing a message,
	 * it will finish processing the message and then become inactive.
	 * @param active Whether the thread should be active (<code>true</code>) or inactive (<code>false</code>).
	 */
	public void setActive(boolean active){
		if(isTerminated()){
			System.err.println("Warning: setActive(" + active + ") called on terminated BrickThread " + this.toString());
			return;
		}
		synchronized(activeLock){
			this.isActive = active;
			interrupt();
			activeLock.notifyAll();
		}
		Thread.yield();
		while(isWorking() != active){
			try{
				Thread.sleep(15);
				setActive(active);
			}
			catch(InterruptedException ie){}
		}
	}
	
	/**
	 * Determines if this thread has actually terminated.  A terminated thread will no longer
	 * process messages.
	 * @return <code>true</code> if the thread has terminated, <code>false</code> otherwise.
	 */
	public synchronized boolean isTerminated(){
		if(isTerminated){
			return true;
		}
		if(!isAlive()){
			return true;
		}
		return false;
	}

	/**
	 * Determines whether this thread is active or not.
	 * @return <code>true</code> if the thread is active, <code>false</code> otherwise.
	 */
	public boolean isActive(){
		return isActive;
	}
	
	/**
	 * Determines whether this thread is terminating or not.  A terminating thread has been
	 * set to terminate, but has not yet done so.
	 * @return <code>true</code> if the thread is terminating, </code>false</code> otherwise.
	 */
	public boolean isTerminating(){
		return isTerminating;
	}
	
	/**
	 * Determines whether the thread is currently processing a message.
	 * @return <code>true</code> if the thread is processing a message, <code>false</code> otherwise.
	 */
	public boolean isWorking(){
		synchronized(activeLock){
			return isWorking;
		}
	}
}

