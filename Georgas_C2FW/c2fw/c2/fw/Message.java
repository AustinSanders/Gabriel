//!!(C)!!

package c2.fw;

/**
 * The <code>Message</code> interface should be implemented by any developers
 * attempting to create their own types of C2 messages.
 * <code>Message</code> is an abstract interface, and is meant to serve as a basis
 * for <code>Request</code>s and <code>Notification</code>s.
 *
 * @see c2.fw.NamedPropertyMessage
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */

public interface Message extends java.io.Serializable{

	/**
	 * Identifies the brick+interface that is the source of this message.
	 * @param srcIdPair <code>Identifiers</code> of the brick and interface
	 * on that brick that sent this message.
	 */
	public void setSource(BrickInterfaceIdPair srcIdPair);

	/**
	 * Identifies the interface that is the destination of this message.
	 * @param destIdPair <code>Identifiers</code> of the brick and interface on that brick 
	 * that should recieve this message.
	 */
	public void setDestination(BrickInterfaceIdPair destIdPair);
	
	/**
	 * Gets the identifier of the interface that sent this message.
	 * @return <code>BrickInterfaceIdPair</code> of the brick+interface that sent this message.
	 */
	public BrickInterfaceIdPair getSource();
	
	/**
	 * Gets the identifier of the interface that should receive this message.
	 * @return <code>BrickInterfaceIdPair</code> of the brick+interface that should receive this message.
	 */
	public BrickInterfaceIdPair getDestination();
	
	/**
	 * Creates a duplicate of this message that, when changed, will not affect the contents
	 * of this message.
	 * @return Message duplicate
	 */
	public Message duplicate();

	/**
	 * Gets a human readable representation of this message in an arbitrary format.
	 * @return a human readable representation of this <code>Message</code>.
	 */
	public String toString();

}

