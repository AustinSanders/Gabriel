//!!(C)!!
package c2.fw;

/**
 * Describes objects that are managed by <code>MessageHandler</code>s that hold messages for processing 
 * by the <code>ArchitectureEngine</code>.  These are usually, but not mandatorily, implemented
 * as some sort of queue.  Additional functionality may be provided as required by the
 * <code>MessageHandler</code>.
 * 
 * @see c2.fw.MessageHandler
 * @see c2.fw.UnboundedFIFOInbox
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface Inbox{
	/**
	 * Adds a new message to the <code>Inbox</code>.  Returns false if the
	 * message-add was unsuccessful because the box was full, etc. This call should not block.
	 * @param m <code>Message</code> to add to the inbox.
	 * @return <code>true</code> if the message was successfully added, <code>false</code> otherwise.
	 */
	public boolean addIncomingMessage(Message m);

	/**
	 * Gets the next message in the <code>Inbox</code>.  Returns <code>null</code>
	 * if there is no such message.  This call should not block.
	 * @return Next <code>Message</code> in the inbox, or <code>null</code> if the box is empty.
	 */
	public Message getNextMessage();

	/**
	 * Peeks at the next message in the <code>Inbox</code>.  Returns <code>null</code>
	 * if there is no such message.  This call should not block.
	 * @return Next <code>Message</code> in the inbox, or <code>null</code> if the box is empty.
	 */
	public Message peekNextMessage();
	
	/**
	 * Determine whether the inbox is full.
	 * @return <code>true</code> if the inbox is full, <code>false</code>otherwise.
	 */
	public boolean isFull();

	/**
	 * Determine whether the inbox is empty.
	 * @return <code>true</code> if the inbox is empty, <code>false</code>otherwise.
	 */
	public boolean isEmpty();

}

