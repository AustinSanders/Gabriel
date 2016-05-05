package c2.fw;

/**
 * Interface implemented by objects that are providers of
 * messages.
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface MessageProvider{

	/**
	 * Add a message listener who will be notified of messages being
	 * emitted from this <CODE>MessageProvider</CODE>.
	 * @param l Listener to add.
	 */
	public void addMessageListener(MessageListener l);
	
	/**
	 * Remove a message listener who will no longer be notified of messages being
	 * emitted from this <CODE>MessageProvider</CODE>.
	 * @param l Listener to remove.
	 */
	public void removeMessageListener(MessageListener l);

}

