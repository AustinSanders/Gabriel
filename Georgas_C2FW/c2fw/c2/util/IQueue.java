package c2.util;

/**
 * @author edashofy
 */
public interface IQueue {
	/**
	 * Test if the queue is logically empty.
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty();

	/**
	 * Get the least recently inserted item in the queue.
	 * Does not alter the queue.
	 * @return the least recently inserted item in the queue.
	 */
	public Object getFront();

	/**
	 * Return and remove the least recently inserted item
	 * from the queue.
	 * @return the least recently inserted item in the queue.
	 */
	public Object dequeue();

	/**
	 * Insert a new item into the queue.
	 * @param X the item to insert.
	 */
	public void enqueue(Object x);

	public int getSize();

	/**
	 * Make the queue logically empty.
	 */
	public void makeEmpty();
}