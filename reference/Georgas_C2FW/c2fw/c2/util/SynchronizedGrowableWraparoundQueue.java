package c2.util;

public class SynchronizedGrowableWraparoundQueue implements IQueue{
	
	private GrowableWraparoundQueue q; 
	
	public SynchronizedGrowableWraparoundQueue()
	{
		q = new GrowableWraparoundQueue();
	}
	
	public SynchronizedGrowableWraparoundQueue(int capacity)
	{
		q = new GrowableWraparoundQueue(capacity);
	}
	
	public synchronized boolean isEmpty( )
	{
		return q.isEmpty();
	}
	
	public synchronized Object getFront( )
	{
		return q.getFront();
	}
	
	public synchronized Object dequeue( )
	{
		return q.dequeue();
	}
	
	public synchronized void  enqueue( Object x )
	{
		q.enqueue(x);
	}
	
	public synchronized int getSize(){
		return q.getSize();
	}
	
	public synchronized void makeEmpty( )
	{
		q.makeEmpty();
	}
	
}

