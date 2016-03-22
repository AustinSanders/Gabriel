//!!(C)!!
package c2.fw;

/**
 * Internally used class that represents a Thread that processes arbitrary messages in any
 * brick for which they are destined.  A shepherd thread takes a message for an arbitrary
 * brick and processes it.  It is not bound to any particular brick.
 *
 * @see c2.fw.ThreadPoolArchitectureEngine
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
class ShepherdThread extends Thread{

	private boolean isActive;
	private boolean isWaiting;
	private boolean isProcessingMessages;
	private boolean isTerminating;
	private boolean isTerminated;
	private Brick whereWorking;
	
	private ThreadPoolArchitectureEngine engine; 
	private LocalArchitectureController controller;
	
	/**
	 * Creates a new Thread bound to the given <code>ThreadPoolArchitectureEngine</code>
	 * and <code>MessageHandler</code>.
	 * @param engine <code>ThreadPoolArchitectureEngine</code> managing this thread.
	 * @param handler <code>MessageHandler</code> with messages this thread will
	 * process.
	 */
	public ShepherdThread(ThreadPoolArchitectureEngine engine, LocalArchitectureController controller){
		this.engine = engine;
		this.controller = controller;
		this.setPriority(ThreadPriorities.NORM_PRIORITY);
		this.isActive = false;
		this.isProcessingMessages = false;
		this.whereWorking = null;
		this.isWaiting = false;
		this.isTerminated = false;
	}
	
	/**
	 * Main method for this thread.  While the thread is active, it retrieves a message
	 * from any active component and processes it.  If the thread is still active,
	 * it processes another message, etc.
	 */ 
	public void run(){
		while(true){
			if(isTerminating){
				isProcessingMessages = false;
				isTerminated = true;
				return;
			}
			//this synchronized block assures us that we're
			//never actually going to process a message while
			//isProcessingMessages = false
			while(!isActive){
				synchronized(this){
					try{
						isProcessingMessages = false;
						isWaiting = true;
						wait();
						isWaiting = false;
					}
					catch(InterruptedException e){
						isWaiting = false;
						if(isTerminating){
							return;
						}
					}
					isProcessingMessages = true;
				}
			}
			
			//OK, we're not terminating and we're active.
			//Let's get a message and handle it.
			try{
				Identifier[] ids = engine.getActiveBricks();
				isWaiting = true;
				Message m = controller.waitMessage(ids);
				isWaiting = false;
				
				Brick whereWorking = controller.getBrick(m.getDestination().getBrickIdentifier());
				whereWorking.handle(m);
				Thread.yield();
			}
			catch(InterruptedException e){
				isWaiting = false;
				//continues...
			}
			isProcessingMessages = false;
		}
	}
	
	/**
	 * Sets this thread to start terminating.  Callers should check later to find out if
	 * the thread has actually terminated.
	 */
	public synchronized void terminate(){
		isTerminating = true;
		if(isWaiting){
			interrupt();
		}
		notifyAll();
	}
	
	/**
	 * Determines if this thread has actually terminated.  A terminated thread will no longer
	 * process messages.
	 * @return <code>true</code> if the thread has terminated, <code>false</code> otherwise.
	 */
	public synchronized boolean isTerminated(){
		return isTerminated;
	}
	
	/**
	 * Sets this thread to be active or inactive.  An active thread will process messages,
	 * an inactive thread will not.  If a thread is set inactive while processing a message,
	 * it will finish processing the message and then become inactive.
	 * @param active Whether the thread should be active (<code>true</code>) or inactive (<code>false</code>).
	 */
	public synchronized void setActive(boolean active){
		if(this.isActive == active){
			return;
		}
		this.isActive = active;
		if(isWaiting){
			interrupt();
		}
		notifyAll();
	}
	
	/**
	 * Causes this thread to wake up and check its state.
	 * Usually called when there has been a change in the active/inactive brick list.
	 */
	public void cycle(){
		if(isWaiting){
			interrupt();
		}
		synchronized(this){
			notifyAll();
		}
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
		return isProcessingMessages;
	}
	
	/**
	 * If the component is working, determines which brick it is working in.
	 * @return <code>Identifier</code> of the brick processing a message, or <code>null</code>
	 * if it is unknown or the thread is not processing messages.
	 */
	public Identifier whereWorking(){
		if(whereWorking == null){
			return null;
		}
		return whereWorking.getIdentifier();
	}
	
}

