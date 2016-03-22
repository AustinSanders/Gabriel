package c2.fw;

/**
 * Describes objects that are <code>ArchitectureManager</code>s, but also
 * expose functionality that allows parts (or extensions) of the
 * framework running in-process to access bricks somewhat directly.
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface LocalArchitectureManager extends ArchitectureManager{
	/**
	 * Adds a C2 brick to the system.  The implementation of this method
	 * should also call <code>init()</code> on the component before it completes, and
	 * notify all <code>ArchitectureListener</code>s that a component has been added.
	 * @param comp <code>Component</code> to add.
	 */
	public void addBrick(Brick brick);

	/**
	 * Gets a reference to the brick with the given identifier.
	 * @param id <code>Identifier</code> of brick to get.
	 * @return Requested brick.
	 * @exception java.lang.IllegalArgumentException if the given brick doesn't exist.
	 */
	public Brick getBrick(Identifier id);

	/**
	 * Gets references to every brick managed by this <code>ArchitectureManager</code>.
	 * Can return a zero-element array if there are no bricks.
	 * @return An array of all bricks managed by this <code>ArchitectureManager</code>.
	 */
	public Brick[] getAllBricks();

}

