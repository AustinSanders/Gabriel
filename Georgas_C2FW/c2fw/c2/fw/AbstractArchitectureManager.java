//!!(C)!!

package c2.fw;

import java.util.Hashtable;
import java.util.Vector;

/**
 * The <code>AbstractArchitectureManager</code> class is a skeleton implementation
 * of some (mostly) boilerplate methods in the <code>ArchitectureManager</code>
 * interface.  Unless your custom <code>ArchitectureManager</code> has to
 * inherit from some other class, it should probably inherit from this one.
 *
 * @see c2.fw.ArchitectureManager
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public abstract class AbstractArchitectureManager implements ArchitectureManager, LocalArchitectureManager{

	/** <code>ArchitectureController</code> for this engine. */
	protected ArchitectureController controller = null;

	/**
	 * The list of <code>ArchitectureListener</code>s listening to changes in this
	 * architecture.
	 * @see c2.fw.ArchitectureListener
	 */
	protected Vector architectureListeners;
	
	/**
	 * Creates a new AbstractArchitectureManager with the given <code>MessageHandler</code>
	 * and <code>ArchitectureEngine</code>.  The <code>MessageHandler</code>'s
   * <code>setArchitectureManager</code> function is called first, then the
   * <code>ArchitectureEngine</code>'s is.
	 * @param messageHandler MessageHandler for this <code>ArchitectureManager</code>
	 * @param engine ArchitectureEngine for this <code>ArchitectureManager</code>
	 */
	public AbstractArchitectureManager(){
		architectureListeners = new Vector();
	}
	
	public void addArchitectureListener(ArchitectureListener l){
		architectureListeners.addElement(l);
	}
	
	public void removeArchitectureListener(ArchitectureListener l){
		architectureListeners.removeElement(l);
	}
	
	/**
	 * Sends a <code>brickAdded</code> event to all <code>ArchitectureListener</code>s.
	 * @param id Identifier of brick that was added.
	 * @see c2.fw.ArchitectureListener#componentAdded(ArchitectureManager, Identifier)
	 */
	protected void fireBrickAdded(Identifier id){
		synchronized(architectureListeners){
			for(int i = 0; i < architectureListeners.size(); i++){
				((ArchitectureListener)architectureListeners.elementAt(i)).brickAdded(this, id);
			}
		}
	}
	
	/**
	 * Sends a <code>brickRemoving</code> event to all <code>ArchitectureListener</code>s.
	 * @param id Identifier of brick that is going to be removed.
	 * @see c2.fw.ArchitectureListener#componentRemoved(ArchitectureManager, Identifier)
	 */
	protected void fireBrickRemoving(Identifier id){
		synchronized(architectureListeners){
			for(int i = 0; i < architectureListeners.size(); i++){
				((ArchitectureListener)architectureListeners.elementAt(i)).brickRemoving(this, id);
			}
		}
	}
	
	/**
	 * Sends a <code>brickRemoved</code> event to all <code>ArchitectureListener</code>s.
	 * @param id Identifier of brick that was removed.
	 * @see c2.fw.ArchitectureListener#componentRemoved(ArchitectureManager, Identifier)
	 */
	protected void fireBrickRemoved(Identifier id){
		synchronized(architectureListeners){
			for(int i = 0; i < architectureListeners.size(); i++){
				((ArchitectureListener)architectureListeners.elementAt(i)).brickRemoved(this, id);
			}
		}
	}

	/**
	 * Sends a <code>weldAdded</code> event to all <code>ArchitectureListener</code>s.
	 * @param w The weld that was added.
	 * @see c2.fw.ArchitectureListener#weldAdded(ArchitectureManager, Identifier, Identifier)
	 */
	protected void fireWeldAdded(Weld w){
		synchronized(architectureListeners){
			for(int i = 0; i < architectureListeners.size(); i++){
				((ArchitectureListener)architectureListeners.elementAt(i)).weldAdded(this, w);
			}
		}
	}
	
	/**
	 * Sends a <code>weldRemoving</code> event to all <code>ArchitectureListener</code>s.
	 * @param w The weld that is about to be removed.
	 * @see c2.fw.ArchitectureListener#weldRemoved(ArchitectureManager, Identifier, Identifier)
	 */
	protected void fireWeldRemoving(Weld w){
		synchronized(architectureListeners){
			for(int i = 0; i < architectureListeners.size(); i++){
				((ArchitectureListener)architectureListeners.elementAt(i)).weldRemoving(this, w);
			}
		}
	}
	
	/**
	 * Sends a <code>weldRemoved</code> event to all <code>ArchitectureListener</code>s.
	 * @param w The weld that was removed.
	 * @see c2.fw.ArchitectureListener#weldRemoved(ArchitectureManager, Identifier, Identifier)
	 */
	protected void fireWeldRemoved(Weld w){
		synchronized(architectureListeners){
			for(int i = 0; i < architectureListeners.size(); i++){
				((ArchitectureListener)architectureListeners.elementAt(i)).weldRemoved(this, w);
			}
		}
	}
	
	/**
	 * Sets the <code>ArchitectureController</code> for this manager.
	 * @param controller Controller for this manager.
	 */
	public void setController(ArchitectureController controller){
		this.controller = controller;
	}
	
	/**
	 * Gets the <code>ArchitectureController</code> for this manager.
	 * @return Controller for this manager.
	 */
	public ArchitectureController getController(){
		return controller;
	}
	
	//---------------------------------
	//Abstract methods
	//---------------------------------
	public abstract void addBrick(BrickDescription description, Identifier id)
		throws BrickNotFoundException, 
		BrickLoadFailureException, 
		UnsupportedBrickDescriptionException,
		BrickCreationException;
	public abstract void addBrick(BrickDescription description, Identifier id, InitializationParameter[] params)
		throws BrickNotFoundException, 
		BrickLoadFailureException, 
		UnsupportedBrickDescriptionException,
		BrickCreationException;

	public abstract void addBrick(Brick brick);
	
	public abstract void removeBrick(Identifier id);

	public abstract void addWeld(Weld w);
	public abstract void removeWeld(Weld w);

	public abstract void begin(Identifier id);
	public abstract void end(Identifier id);
	
	public abstract Identifier[] getBrickIdentifiers();

	public abstract Brick getBrick(Identifier id);
	public abstract Brick[] getAllBricks();	
	
	public abstract Weld[] getWelds();

}

