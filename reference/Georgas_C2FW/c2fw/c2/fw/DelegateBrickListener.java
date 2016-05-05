package c2.fw;

/**
 * Callback interface for monitoring when the lists of
 * <CODE>MessageProcessor</CODE>s and <CODE>LifecycleProcessor</CODE>s
 * change on a <CODE>DelegateBrick</CODE>.
 *
 * @see c2.fw.DelegateBrick
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface DelegateBrickListener{

	/**
	 * Called when a message processor is added to a <CODE>DelegateBrick</CODE>.
	 * @param b Brick to which the processor was added.
	 * @param mp <CODE>MessageProcessor</CODE> that was added.
	 */
	public void messageProcessorAdded(DelegateBrick b, MessageProcessor mp);
	
	/**
	 * Called when a message processor is removed from a <CODE>DelegateBrick</CODE>.
	 * @param b Brick from which the processor was removed.
	 * @param mp <CODE>MessageProcessor</CODE> that was removed.
	 */
	public void messageProcessorRemoved(DelegateBrick b, MessageProcessor mp);
	
	/**
	 * Called when a lifecycle processor is added to a <CODE>DelegateBrick</CODE>.
	 * @param b Brick to which the processor was added.
	 * @param lp <CODE>LifecycleProcessor</CODE> that was added.
	 */
	public void lifecycleProcessorAdded(DelegateBrick b, LifecycleProcessor lp);

	/**
	 * Called when a lifecycle processor is removed from a <CODE>DelegateBrick</CODE>.
	 * @param b Brick from which the processor was removed.
	 * @param lp <CODE>LifecycleProcessor</CODE> that was removed.
	 */
	public void lifecycleProcessorRemoved(DelegateBrick b, LifecycleProcessor lp);

}
