package c2.util;

import edu.uci.ics.xarchutils.ObjRef;

/**
 * This is a utility base Thread class for executing an
 * activity once after a given delay where additional requests
 * to execute the activity during the delay period reset
 * the delay.  This is useful, for example, in dealing with cases
 * where each of a burst of events would cause a 
 * computationally-intensive operation that is only dependent
 * upon the last event.  So, let's say we get 1000 events inside
 * two seconds.  Each of them should trigger the recomputation,
 * but we don't want to actually recompute 1000 times when once
 * at the end would be enough.  This allows us to ignore all but
 * the last request, and, after a short delay, perform recompuation
 * once.
 */
public abstract class DelayedExecuteOnceThread extends Thread{
	protected boolean shouldTerminate = false;
	protected int delay;
	
	public DelayedExecuteOnceThread(int delay){
		this.delay = delay;
	}
	
	public synchronized void execute(){
		this.interrupt();
	}
	
	public synchronized void terminate(){
		this.shouldTerminate = true;
		this.notifyAll();
	}
	
	public abstract void doExecute();
		
	public synchronized void run(){
		while(!shouldTerminate){
			try{
				this.wait();
			}
			catch(InterruptedException ie){}
			if(shouldTerminate) return;

			while(true){
				try{
					this.wait(delay);
					break;
				}
				catch(InterruptedException ie2){}
			}
			if(shouldTerminate) return;
			doExecute();
		}
	}
}
