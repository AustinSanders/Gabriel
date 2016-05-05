//!!(C)!!
package c2.fw;

/**
 * Data structure used internally to represent actions that an engine
 * must do to a brick or set of bricks.
 *
 * @see c2.fw.Brick
 * @see c2.fw.ArchitectureEngine
 * @see c2.fw.AbstractArchitectureEngine
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
class BrickAction{

	private int brickAction;
	private Identifier[] ids;
	
	/**
	 * Creates a new <code>BrickAction</code> with the given action code and identifiers.
	 * @param brickAction Action code to apply to bricks.
	 * @param ids Identifiers of bricks to do action on.
	 */
	public BrickAction(int brickAction, Identifier[] ids){
		this.brickAction = brickAction;
		this.ids = ids;
	}
	
	/**
	 * Gets the action code for this <code>BrickAction</code>.
	 * @return Action code for this <code>BrickAction</code>.
	 */
	public int getBrickAction(){
		return brickAction;
	}
	
	/**
	 * Gets the identifiers for this <code>BrickAction</code>.
	 * @return <code>Identifier</code>s for this <code>BrickAction</code>.
	 */
	public Identifier[] getIdentifiers(){
		return ids;
	}

}

