package c2.conn.fred;

//Represents a fixed-size recent-object-cache falloff queue that grows its size
//when the flow of messages through it gets higher
//and reduces its size when it drops.

//Such queues are well known in systems like Kerberos.  Basically, this is
//a cache of recent things.  "Recency" is determined by whether something
//is still in the queue or not.  If it's still in the queue, it's been
//seen recently.  Of course, the idea is that the queue won't grow
//indefinitely, so there has to be a cutoff point.  However, this
//cutoff point depends on how fast things go into the queue.
//If the queue is hit with a firehose of recently
//seen things in a short amount of time, it will grow to accommodate this,
//so that things that just got added won't fly out the back end of the queue.
//When things slow down (i.e. the queue has not rolled over in two minutes,
//the queue will be sized down to save memory).

public class AdaptingQueue{

	private int initialSize;
	private Object[] q = null;
	private long[] times = null;
	private int headPointer;
	
	public static void main(String[] args){
		AdaptingQueue queue = new AdaptingQueue(5);
		for(int i = 0; i < 8; i++){
			System.out.println("Enqueuing: " + i);
			queue.enqueue(new Integer(i));
			try{
				Thread.sleep(50);
			}
			catch(InterruptedException e){
			}
		}
		System.out.println(c2.util.ArrayUtils.arrayToString(queue.getRecentEntries(40)));
	}
	
	public AdaptingQueue(){
		this(30);
	}
	
	public AdaptingQueue(int size){
		this.initialSize = size;
		q = new Object[size];
		times = new long[size];
		for(int i = 0; i < size; i++){
			q[i] = null;
			times[i] = -1;
		}
		headPointer = 0;
	}
	
	public boolean contains(Object o){
		for(int i = 0; i < q.length; i++){
			int pos = headPointer - i - 1;
			if(pos < 0){
				pos += q.length;
			}
			if(q[pos] == null){
				return false;
			}
			if(q[pos].equals(o)){
				return true;
			}
		}
		return false;
	}	
	
	public Object[] getRecentEntries(int numberOfEntries){
		//System.out.println("headpointer = " + headPointer);
		Object[] entries = new Object[numberOfEntries];
		int pos = headPointer - 1;
		if(pos == -1) pos = q.length - 1;
		
		for(int i = 0; i < numberOfEntries; i++){
			if(q[pos] == null){
				return entries;
			}
			entries[i] = q[pos];
			int endPos = headPointer - 1;
			if(endPos == -1) endPos = q.length - 1;
			pos--;
			if(pos == -1){
				pos = q.length - 1;
			}
			if(pos == endPos){
				return entries;
			}
		}
		return entries;
	}
	
	private long[] getRecentTimes(int numberOfEntries){
		long[] entries = new long[numberOfEntries];
		int pos = headPointer - 1;
		if(pos == -1) pos = q.length - 1;
		
		for(int i = 0; i < numberOfEntries; i++){
			if(q[pos] == null){
				return entries;
			}
			entries[i] = times[pos];
			pos--;
			int endPos = headPointer - 1;
			if(endPos == -1) endPos = q.length - 1;
			if(pos == -1){
				pos = q.length - 1;
			}
			if(pos == endPos){
				return entries;
			}
		}
		return entries;
	}
	
	public void resize(int newSize){
		//System.out.println("Resizing to: " + newSize);
		if(newSize < initialSize){
			resize(initialSize);
		}
		if(newSize == q.length){
			return;
		}
		Object[] newQ = new Object[newSize];
		long[] newTimes = new long[newSize];
		
		Object[] recentQ = getRecentEntries(newSize);
		long[] recentTimes = getRecentTimes(newSize);
		for(int i = 0; i < newSize; i++){
			newQ[newSize - i - 1] = recentQ[i];
			newTimes[newSize - i - 1] = recentTimes[i];
			headPointer = 0;
		}
		q = newQ;
		times = newTimes;
	}
	
	public void enqueue(Object o){
		if(q[headPointer] == null){
			q[headPointer] = o;
			times[headPointer] = System.currentTimeMillis();
			headPointer++;
			if(headPointer == q.length){
				headPointer = 0;
			}
			return;
		}
		
		long oldTime = times[headPointer];
		long curTime = System.currentTimeMillis();
		
		if((curTime - oldTime) < 10000){
			resize(q.length * 2);
		}
		else if((curTime - oldTime) > 120000){
			if(q.length > initialSize){
				resize(q.length >> 1);
			}
		}
		
		q[headPointer] = o;
		times[headPointer] = curTime;
		headPointer++;
		if(headPointer == q.length){
			headPointer = 0;
		}
	}

}

