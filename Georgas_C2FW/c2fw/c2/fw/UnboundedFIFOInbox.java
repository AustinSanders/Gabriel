//!!(C)!!
package c2.fw;

import c2.util.GrowableWraparoundQueue;

/**
 * FIFO <code>Inbox</code> that can grow as big as memory and has additional blocking semantics.
 *
 * @see c2.fw.Inbox
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class UnboundedFIFOInbox implements Inbox{
	//private SingleThreadVector buf = new SingleThreadVector(20);
	private GrowableWraparoundQueue buf = new GrowableWraparoundQueue();
	
	/**
	 * Adds a message to the end of the Inbox queue.
	 * @param m <code>Message</code> to add to end of queue. 
	 */
	public synchronized boolean addIncomingMessage(Message m){
		buf.enqueue(m);
		notifyAll();
		return true;
	}
	
	/**
	 * Gets the next message from the queue.  Blocks until a message
	 * becomes available.
	 * @return Next <code>Message</code> in the queue.
	 */
	public synchronized Message waitMessage() throws InterruptedException{
		while(buf.isEmpty()){
			wait();
		}
		Message m = (Message)buf.dequeue();
		return m;
	}
	
	/**
	 * Gets the next message from the queue.  Returns <code> null if the
	 * queue is empty.
	 * @return Next <code>Message</code> in the queue, or <code>null</code>
	 * if the queue is empty.
	 */
	public synchronized Message getNextMessage(){
		if(buf.isEmpty()){
			return null;
		}
		Message m = (Message)buf.dequeue();
		return m;
	}
	
	/**
	 * Peeks at the next message from the queue.  Returns <code> null if the
	 * queue is empty.
	 * @return Next <code>Message</code> in the queue, or <code>null</code>
	 * if the queue is empty.
	 */
	public synchronized Message peekNextMessage(){
		if(buf.isEmpty()){
			return null;
		}
		Message m = (Message)buf.getFront();
		return m;
	}
	
	/**
	 * Determine whether the queue is empty.
	 * @return <code>true</code> if the queue is empty, <code>false</code> otherwise.
	 */
	public boolean isEmpty(){
		return buf.isEmpty();
	}
	
	/**
	 * Determine whether the queue is full.  Because this queue is unbounded,
	 * it will never be full.
	 * @return <code>false</code>.
	 */
	public boolean isFull(){
		return false;
	}
}	
