package c2.util;

/**
 * Code borrowed/modified from 
 * Data Structures & Problem Solving Using Java by Mark Allen Weiss, 1998. 
 */
public class GrowableWraparoundQueue implements IQueue
{
	private Object [ ] theArray;
	private int        currentSize;
	private int        front;
	private int        back;
	
	static final int DEFAULT_CAPACITY = 10;
	
	/**
	 * Construct the queue.
	 */
	public GrowableWraparoundQueue( )
	{
		theArray = new Object[ DEFAULT_CAPACITY ];
		makeEmpty( );
	}
	
	/**
	 * Construct the queue.
	 */
	public GrowableWraparoundQueue(int capacity)
	{
		theArray = new Object[capacity];
		makeEmpty( );
	}
	
	/**
	 * Test if the queue is logically empty.
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty( )
	{
		return currentSize == 0;
	}
	
	/**
	 * Get the least recently inserted item in the queue.
	 * Does not alter the queue.
	 * @return the least recently inserted item in the queue.
	 */
	public Object getFront( )
	{
		if( isEmpty( ) )
			return null;
		return theArray[ front ];
	}
	
	/**
	 * Return and remove the least recently inserted item
	 * from the queue.
	 * @return the least recently inserted item in the queue.
	 */
	public Object dequeue( )
	{
		if( isEmpty( ) )
			return null;
		currentSize--;
		
		Object returnValue = theArray[ front ];
		front = increment( front );
		return returnValue;
	}
	
	/**
	 * Insert a new item into the queue.
	 * @param X the item to insert.
	 */
	public void  enqueue( Object x )
	{
		if( currentSize == theArray.length )
			doubleQueue( );
		back = increment( back );
		theArray[ back ] = x;
		currentSize++;
	}
	
	public int getSize(){
		return currentSize;
	}
	
		/**
		 * Make the queue logically empty.
		 */
	public void makeEmpty( )
	{
		currentSize = 0;
		front = 0;
		back = -1;
	}
	
	/**
	 * Internal method to expand theArray.
	 */
	private void doubleQueue( )
	{
		Object [ ] newArray;
		
		newArray = new Object[ theArray.length * 2 ];
		
		// Copy elements that are logically in the queue
		for( int i = 0; i < currentSize; i++, front = increment( front ) )
			newArray[ i ] = theArray[ front ];
		
		theArray = newArray;
		front = 0;
		back = currentSize - 1;
	}
	
	/**
	 * Internal method to increment with wraparound.
	 * @param x any index in theArray's range.
	 * @return x+1, or 0 if x is at the end of theArray.
	 */
	private int increment( int x )
	{
		if( ++x == theArray.length )
			x = 0;
		return x;
	}
	
}

