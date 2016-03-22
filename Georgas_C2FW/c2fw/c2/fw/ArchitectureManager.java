
//!!(C)!!

package c2.fw;

/**
 * The ArchitectureManager interface is implemented by classes that manage
 * the configuration of C2 archiectures.  An ArchitectureManager manages
 * the components, connectors, and welds in a running C2 architecture.
 * It is also responsible for generating events for ArchitectureListeners
 * when an architecture is changed.  Finally, any system dynamism is
 * managed at this level.  For instance, if a caller removes a component
 * from an architecture, the ArchitectureManager is responsible for
 * handling the messages and threads interacting with that component.
 * For this reason, it has references to both the MessageHandler and
 * ArchitectureEngine objects.
 *
 * <P>While authors can implement ArchitectureManagers directly, it is
 * usually more convenient to inherit from AbstractArchitectureManager.
 *
 * @see c2.fw.AbstractArchitectureManager
 * @see c2.fw.MessageHandler
 * @see c2.fw.ArchitectureEngine
 * 
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */

public interface ArchitectureManager{
	/**
	 * Adds a C2 brick to the system.  The implementation of this method
	 * should also call <code>init()</code> on the component before it completes, and
	 * notify all <code>ArchitectureListener</code>s that a component has been added.
	 * @param description <code>BrickDescription</code> describing the brick to load.
	 * @param id <code>Identifier</code> to be given to the brick when loaded.
	 * @exception BrickNotFoundException if the given description cannot be matched
	 * to a loadable brick.
	 * @exception BrickLoadFailureException if the brick was found but could
	 * not be loaded.
	 * @exception UnsupportedBrickDescriptionException if this manager cannot
	 * load the type of brick specified by the description.
	 * @exception BrickCreationException if the brick was found, but could
	 * not be instantiated.
	 */
	public void addBrick(BrickDescription description, Identifier id)
		throws BrickNotFoundException, 
		BrickLoadFailureException, 
		UnsupportedBrickDescriptionException,
		BrickCreationException;

	/**
	 * Adds a C2 brick to the system.  The implementation of this method
	 * should also call <code>init()</code> on the component before it completes, and
	 * notify all <code>ArchitectureListener</code>s that a component has been added.
	 * @param description <code>BrickDescription</code> describing the brick to load.
	 * @param id <code>Identifier</code> to be given to the brick when loaded.
	 * @param params Initialization parameters for the new brick.  If null, no
	 * parameters will be passed.
	 * @exception BrickNotFoundException if the given description cannot be matched
	 * to a loadable brick.
	 * @exception BrickLoadFailureException if the brick was found but could
	 * not be loaded.
	 * @exception UnsupportedBrickDescriptionException if this manager cannot
	 * load the type of brick specified by the description.
	 * @exception BrickCreationException if the brick was found, but could
	 * not be instantiated.
	 */
	public void addBrick(BrickDescription description, Identifier id, InitializationParameter[] params)
		throws BrickNotFoundException, 
		BrickLoadFailureException, 
		UnsupportedBrickDescriptionException,
		BrickCreationException;
	
	/**
	 * Removes a C2 brick from the system.  The implementation of this method
	 * should also call <code>destroy()</code> on the brick before it completes, and
	 * notify all <code>ArchitectureListener</code>s that a brick has been removed.
	 * @param id <code>Identifier</code> of brick to remove.
	 */
	public void removeBrick(Identifier id);

	/**
	 * Welds two C2 bricks together.  The implementation of this method
	 * should notify all <code>ArchitectureListener</code>s that a weld has been added.
	 * @param w <code>Weld</code> to add.
	 */
	public void addWeld(Weld w);

	/**
	 * Unwelds two C2 bricks.  The implementation of this method
	 * should notify all <code>ArchitectureListener</code>s that a weld has been removed.
	 * @param w <code>Weld</code> to remove.
	 */
	public void removeWeld(Weld w);

	/**
	 * Calls the <code>begin()</code> method on the given brick.  The <code>begin()</code> method
	 * is provided so that, after a component or connector is welded into
	 * place, it can send out initial messages to get itself started.
	 * @param id Identifier of component or connector to begin.
	 */
	public void begin(Identifier id);

	/**
	 * Calls the <code>end()</code> method on the given brick.  The <code>end()</code> method
	 * is provided so that, before a connector or component is unwelded
	 * or terminated, it can send out final messages before terminating.
	 * @param id Identifier of component or connector to end.
	 */
	public void end(Identifier id);
	
	/**
	 * Gets the identifiers of all bricks managed by this <code>ArchitectureManager</code>.
	 * @return Array of <code>Identifier</code>s of all the bricks managed by this
	 * manager.
	 */
	public Identifier[] getBrickIdentifiers();
	
	/**
	 * Gets the identifiers of all interfaces on a brick, given a brick identifier.
	 * @return Array of <code>Identifier</codes>s of all the interfaces on the given
	 * brick.
	 * @exception BrickNotFoundException if the given brick was not found.
	 */
	public Identifier[] getInterfaceIdentifiers(Identifier brickId) throws BrickNotFoundException;
	
	/**
	 * Gets references to every weld managed by this <code>ArchitectureManager</code>.
	 * Can return a zero-element array if there are no welds.
	 * @return An array of all welds managed by this <code>ArchitectureManager</code>.
	 */
	public Weld[] getWelds();
	
	/**
	 * Adds an object that will listen for architecture events
	 * coming from this <code>ArchitectureManager</code>.  The architecture
	 * manager is responsible for sending appropriate events
	 * to all listeners when a change is made to the architecture.
	 * @param l ArchitectureListener to add.
	 * @see #removeArchitectureListener(ArchitectureListener)
	 */
	public void addArchitectureListener(ArchitectureListener l);

	/**
	 * Removes an <code>ArchitectureListener</code> previously added with
	 * <code>addArchitectureListener()</code>.
	 * @param l ArchitectureListener to remove.
	 * @see #addArchitectureListener(ArchitectureListener)
	 */
	public void removeArchitectureListener(ArchitectureListener l);

	/**
	 * Adds an object that will listen for ArchMessages
	 * coming from any brick in this <code>ArchitectureManager</code>.
	 * ArchMessages are messages that are sent by a brick to the infrastructure
	 * to affect something outside the architecture itself (e.g. to ask for
	 * a system shutdown).
	 * @param l ArchMessageListener to add.
	 * @see #removeArchMessageListener(ArchMessageListener)
	 */
	public void addArchMessageListener(ArchMessageListener l);

	/**
	 * Removes an <code>ArchMessageListener</code> previously added with
	 * <code>addArchMessageListener()</code>.
	 * @param l ArchMessageListener to remove.
	 * @see #addArchMessageListener(ArchMessageListener)
	 */
	public void removeArchMessageListener(ArchMessageListener l);
	
	public void setController(ArchitectureController controller);
	public ArchitectureController getController();
}

