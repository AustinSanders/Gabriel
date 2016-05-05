//!!(C)!!
package c2.fw;

/**
 * Describes objects that are notified when messages are sent from a C2 brick.
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface MessageListener{

	/**
	 * Called when a message is sent from the source brick.
	 * @param r <code>Request</code> that was sent.
	 */
	public void messageSent(Message m);

}

