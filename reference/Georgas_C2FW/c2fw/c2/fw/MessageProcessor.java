package c2.fw;

/**
 * A <CODE>MessageProcessor</CODE> is a small class that can process a
 * message.  <CODE>MessageProcessor</CODE>s are primarily used by
 * <CODE>DelegateBrick</CODE>s.
 * @see c2.fw.DelegateBrick
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */ 
public interface MessageProcessor{
	
	/**
	 * Handle a message.  If a message processor chooses not to handle
	 * the message, it should return quickly, using as little computing power
	 * as possible.  Wasteful message processors can account for a lot of
	 * inefficiency in even medium-sized systems.
	 * 
	 * @param m Message to handle.
	 */
	public void handle(Message m);

}

