//!!(C)!!
package c2.fw;

/**
 * Callback interface for objects interested in changes to an <code>ArchitectureManager</code>.
 * <code>ArchitectureListener</code>s are notified when components, connectors, and welds
 * are added or removed from the architecture.
 * 
 * @see c2.fw.ArchitectureManager
 * @see c2.fw.ArchitectureEngine
 * @see c2.fw.MessageHandler
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface ArchitectureListener{
	
	/**
	 * Called when a brick is added to the <code>ArchitectureManager</code>.
	 * @param manager Manager that added the brick.
	 * @param id Identifier of the brick that was added.
	 */
	public void brickAdded(ArchitectureManager manager, Identifier id);

	/**
	 * Called when a brick is about to be removed from the 
	 * <code>ArchitectureManager</code>, but before it is actually
	 * removed.
	 * @param manager Manager that removed the brick.
	 * @param id Identifier of the brick that is going to be removed.
	 */
	public void brickRemoving(ArchitectureManager manager, Identifier id);
	
	/**
	 * Called when a brick is removed from the <code>ArchitectureManager</code>.
	 * @param manager Manager that removed the brick.
	 * @param id Identifier of the brick that was removed.
	 */
	public void brickRemoved(ArchitectureManager manager, Identifier id);
	
	/**
	 * Called when a weld is added to the <code>ArchitectureManager</code>.
	 * @param manager Manager that added the weld.
	 * @param w Weld that was added.
	 */
	public void weldAdded(ArchitectureManager manager, Weld w);

	/**
	 * Called when a weld is about to be removed from the 
	 * <code>ArchitectureManager</code>.
	 * @param manager Manager that removed the weld.
	 * @param w Weld that is about to be removed.
	 */
	public void weldRemoving(ArchitectureManager manager, Weld w);
	
	/**
	 * Called when a weld is removed from the <code>ArchitectureManager</code>.
	 * @param manager Manager that removed the weld.
	 * @param w Weld that was removed.
	 */
	public void weldRemoved(ArchitectureManager manager, Weld w);
}

