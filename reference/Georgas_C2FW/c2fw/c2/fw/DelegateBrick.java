package c2.fw;

import java.util.Collection;

/**
 * Extension of the Brick interface for two purposes:
 * <OL>
 * <LI>To add the ability to delegate message processing
 * away from a single <CODE>handle()</CODE> method to
 * a number of indepdendent <CODE>MessageProcessor</CODE>s.
 * <LI>To add the ability to delegate lifecycle methods
 * away from single <CODE>init()</CODE>, <CODE>begin()</CODE>,
 * <CODE>end()</CODE>, and <CODE>destroy()</CODE> methods to
 * a set of independent <CODE>LifecycleProcessor</CODE>s.
 * <LI>To add simple name-value pair properties to bricks.
 * </OL>
 * 
 * The <CODE>DelegateBrick</CODE>'s delegation routines incur a <I>very</I> slight overhead,
 * but the extensibility benefits outweigh the performance penalty.  All Bricks in
 * an architecture should probably implement <CODE>DelegateBrick</CODE>, then.
 *
 * @see c2.fw.AbstractDelegateBrick
 * @see c2.fw.Brick
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface DelegateBrick extends Brick{

	/**
	 * Add a message processor to this brick.  The message processor's
	 * <CODE>handle(...)</CODE> method will be called whenever a message
	 * is received by this brick.
	 * @see c2.fw.MessageProcessor
	 * @param mp The <CODE>MessageProcessor</CODE> to add.
	 */
	public void addMessageProcessor(MessageProcessor mp);	
	
	/**
	 * Remove a message processor from this brick.  The message processor's
	 * <CODE>handle(...)</CODE> method will no longer be called whenever a 
	 * message is received by this brick.
	 * @see c2.fw.MessageProcessor
	 * @param mp The <CODE>MessageProcessor</CODE> to remove.
	 */
	public void removeMessageProcessor(MessageProcessor mp);
	
	/**
	 * Get all the message processors on this brick.
	 * @return Array of <CODE>MessageProcessor</CODE>s that are processing
	 * messages on this brick.
	 */
	public MessageProcessor[] getMessageProcessors();

	/**
	 * Add a lifecycle processor to this brick.  The lifecycle processor's
	 * <CODE>init(...)</CODE>, <CODE>begin(...)</CODE>, <CODE>end(...)</CODE>,
	 * and <CODE>destroy(...)</CODE> methods will be called at appropriate
	 * times in the brick's lifecycle.
	 * @see c2.fw.LifecycleProcessor
	 * @param lp The <CODE>LifecycleProcessor</CODE> to add.
	 */
	public void addLifecycleProcessor(LifecycleProcessor lp);	
	
	/**
	 * Remove a lifecycle processor from this brick.  The lifecycle processor's
	 * <CODE>init(...)</CODE>, <CODE>begin(...)</CODE>, <CODE>end(...)</CODE>,
	 * and <CODE>destroy(...)</CODE> methods will no longer be called 
	 * in the brick's lifecycle.
	 * @see c2.fw.LifecycleProcessor
	 * @param lp The <CODE>LifecycleProcessor</CODE> to add.
	 */
	public void removeLifecycleProcessor(LifecycleProcessor mp);
	
	/**
	 * Get all the lifecycle processors on this brick.
	 * @return Array of <CODE>LifecycleProcessor</CODE>s that exist on this brick.
	 */
	public LifecycleProcessor[] getLifecycleProcessors();
	
	/**
	 * Add a listener object that will be notified when the
	 * list of message processors or lifecycle processors
	 * on this brick changes.
	 * @param l Delegate brick listener to add.
	 */
	public void addDelegateBrickListener(DelegateBrickListener l);
	
	/**
	 * Get all of the DelegateBrickListeners on this brick.
	 * @return Array of <CODE>DelegateBrickListener</CODE>s that exist on this brick.
	 */
	public DelegateBrickListener[] getDelegateBrickListeners();
	
	/**
	 * Remove a listener object that will no longer be notified when the
	 * list of message processors or lifecycle processors
	 * on this brick changes.
	 * @param l Delegate brick listener to remove.
	 */
	public void removeDelegateBrickListener(DelegateBrickListener l);
}

