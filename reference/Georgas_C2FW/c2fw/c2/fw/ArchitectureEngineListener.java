//!!(C)!!
package c2.fw;

/**
 * Describes objects that receive events related to state changes in an 
 * <code>ArchitectureEngine</code>.  <code>ArchitectureEngineListener</code>s 
 * are notified when the engine or any bricks are started, stopped, 
 * suspended, or resumed.
 *
 * @see c2.fw.ArchitectureManager
 * @see c2.fw.ArchitectureEngine
 * @see c2.fw.AbstractArchitectureEngine
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface ArchitectureEngineListener{

	/**
	 * Callback function called when an <code>ArchitectureEngine</code> is
	 * started.
	 * @param engine The engine that was started.
	 */
	public void engineStarted(ArchitectureEngine engine);
	
	/**
	 * Callback function called when an <code>ArchitectureEngine</code> is
	 * stopped.
	 * @param engine The engine that was stopped.
	 */
	public void engineStopped(ArchitectureEngine engine);
	
	/**
	 * Callback function called when an <code>ArchitectureEngine</code> starts
	 * one or more bricks.
	 * @param engine The engine that changed the state of the bricks.
	 * @param ids Identifiers of the bricks that were started.
	 */
	public void bricksStarted(ArchitectureEngine engine, Identifier[] ids);

	/**
	 * Callback function called when an <code>ArchitectureEngine</code> suspends
	 * one or more bricks.
	 * @param engine The engine that changed the state of the bricks.
	 * @param ids Identifiers of the bricks that were suspended.
	 */
	public void bricksSuspended(ArchitectureEngine engine, Identifier[] ids);

	/**
	 * Callback function called when an <code>ArchitectureEngine</code> resumes
	 * one or more bricks.
	 * @param engine The engine that changed the state of the bricks.
	 * @param ids Identifiers of the bricks that were resumed.
	 */
	public void bricksResumed(ArchitectureEngine engine, Identifier[] ids);

	/**
	 * Callback function called when an <code>ArchitectureEngine</code> stops
	 * one or more bricks.
	 * @param engine The engine that changed the state of the bricks.
	 * @param ids Identifiers of the bricks that were stopped.
	 */
	public void bricksStopped(ArchitectureEngine engine, Identifier[] ids);
	

}

