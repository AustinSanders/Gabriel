package c2.fw;

import java.util.*;

/**
 * An <CODE>ArchitectureEngine</CODE> that uses one thread to iterate all the
 * components in an architecture.  It is also steppable, meaning that the 
 * thread will not be activated until the <CODE>step()</CODE> function
 * is called. 
 *
 * @see c2.fw.SteppableArchitectureEngine
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class OneThreadSteppableArchitectureEngine extends AbstractArchitectureEngine implements Timer,
SteppableArchitectureEngine{

	protected Hashtable activeBricks;
	protected SteppableThread th;
	protected SingleThreadVector timerListeners;
	protected int tickCount = -1;
	protected boolean isIdle = false;
	protected Object idleLock = new Object();
	
	public OneThreadSteppableArchitectureEngine(){
		super();
		activeBricks = new Hashtable();
		timerListeners = new SingleThreadVector();
	}

	protected void init(){
	}

	public void step(){
		if(getEngineState() != ENGINESTATE_STARTED){
			throw new RuntimeException("Invalid engine state.");
		}
		
		fireTick();
		th.addStep();
	}
	
	public void doStartEngine(){
		System.out.println("Engine started.");
		th = new SteppableThread();
		th.start();
	}
	
	public void doStopEngine(){
		if(th != null){
			th.terminate();
		}
	}
	
	public void doStart(Identifier[] ids){
		synchronized(activeBricks){
			for(int i = 0; i < ids.length; i++){
				//System.out.println(ids[i] + " started.");
				activeBricks.put(ids[i], controller.getBrick(ids[i]));
			}
		}
	}
	
	public void doSuspend(Identifier[] ids){
		synchronized(activeBricks){
			for(int i = 0; i < ids.length; i++){
				activeBricks.remove(ids[i]);
			}
		}
	}
	
	public void doResume(Identifier[] ids){
		synchronized(activeBricks){
			for(int i = 0; i < ids.length; i++){
				activeBricks.put(ids[i], controller.getBrick(ids[i]));
			}
		}
	}
	
	public void doStop(Identifier[] ids){
		synchronized(activeBricks){
			for(int i = 0; i < ids.length; i++){
				activeBricks.remove(ids[i]);
			}
		}
	}
	
	public void addTimerListener(TimerListener l){
		synchronized(timerListeners){
			timerListeners.addElement(l);
		}
	}
	
	public void removeTimerListener(TimerListener l){
		synchronized(timerListeners){
			timerListeners.removeElement(l);
		}
	}
	
	protected void fireTick(){
		tickCount++;
		synchronized(timerListeners){
			int size = timerListeners.size();
			for(int i = 0; i < size; i++){
				TimerListener tl = (TimerListener)timerListeners.elementAt(i);
				tl.tick(tickCount);
			}
		}
	}
	
	public boolean isIdle(){
		synchronized(idleLock){
			return isIdle;
		}
	}
	
	public void waitUntilIdle() throws InterruptedException{
		synchronized(idleLock){
			while(!isIdle()){
				idleLock.wait();
			}
		}
	}

	public void brickAdded(ArchitectureManager manager, Identifier id){
		super.brickAdded(manager, id);
		Brick b = ((LocalArchitectureManager)manager).getBrick(id);
		if(b instanceof TimerListener){
			this.addTimerListener((TimerListener)b);
		}
	}
	
	public void brickRemoving(ArchitectureManager manager, Identifier id){
		super.brickRemoved(manager, id);
		Brick b = ((LocalArchitectureManager)manager).getBrick(id);
		if(b instanceof TimerListener){
			this.removeTimerListener((TimerListener)b);
		}
	}

	class SteppableThread extends Thread{
		
		protected int stepCount = 0;
		protected Object stepLock = new Object();
		protected boolean terminate = false;
		
		public SteppableThread(){
			super();
			this.setPriority(Thread.NORM_PRIORITY);
		}
		
		public void terminate(){
			terminate = true;
			synchronized(stepLock){
				stepLock.notifyAll();
			}
		}
				
		public void addStep(){
			try{
				waitUntilIdle();
			}
			catch(InterruptedException e){}
		
			synchronized(stepLock){
				synchronized(idleLock){
					stepCount++;
					stepLock.notify();
					isIdle = false;
				}
			}
		}
		
		private void removeStep(){
			synchronized(stepLock){
				stepCount--;
			}
		}
		
		public synchronized void run(){
			while(true){
				if(terminate){
					synchronized(idleLock){
						isIdle = true;
						idleLock.notifyAll();
					}
					return;
				}
				synchronized(stepLock){
					while(stepCount == 0){
						try{
							synchronized(idleLock){
								isIdle = true;
								idleLock.notifyAll();
							}
							stepLock.wait();
							isIdle = false;
						}
						catch(InterruptedException e){}
						if(terminate){
							isIdle = true;
							return;
						}
					}
					removeStep();
				}
				synchronized(activeBricks){
					for(Enumeration en = activeBricks.elements(); en.hasMoreElements(); ){
						Brick b = (Brick)en.nextElement();
						Message m = getController().peekMessage(b.getIdentifier());
						if(m != null){
							if(m instanceof DeferredMessage){
								//System.out.println("Got deferred message: " + m);
								long processTime = ((DeferredMessage)m).getEarliestProcessTime();
								//System.out.println("process time = " + processTime);
								//System.out.println("tick count = " + tickCount);
								if(processTime <= tickCount){
									m = getController().getMessage(b.getIdentifier());
									b.handle(m);
									Thread.yield();
								}
							}
							else{
								m = getController().getMessage(b.getIdentifier());
								b.handle(m);
								Thread.yield();
							}
						}
					}
				}
			}
		}
	}
}

