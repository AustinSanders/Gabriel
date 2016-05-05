package c2.util;

public class LockPool{
	public GrowableWraparoundQueue q = new GrowableWraparoundQueue(50);
	
	public LockPool(){
	}
	
	public synchronized Object getLock(){
		Object o = q.dequeue();
		if(o == null){
			return new Object();
		}
		else{
			return o;
		}
	}
	
	public synchronized void returnLock(Object lock){
		if(q.getSize() > 40){
			return;
		}
		else{
			q.enqueue(lock);
		}
	}
}
