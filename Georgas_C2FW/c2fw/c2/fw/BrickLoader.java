//!!(C)!!
package c2.fw;

/**
 * Interface that describes objects that can load bricks into the system.
 *
 * @see c2.fw.Brick
 * @see c2.fw.BrickFactory
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface BrickLoader{
	
	/**
	 * Loads a brick into the system.  Returns a <code>BrickFactory</code> that can create instances
	 * of the brick.  For instance, a BusConnector might be used several times in a single
	 * system.  So, you load one BusConnector <code>BrickFactory</code> and generate multiple
	 * instances of the connector from the factory.
	 *
	 * @param description <code>BrickDescription</code> describing the type of brick to load.
	 * @exception BrickNotFoundException If the brick is not found by this brick loader.
	 * @exception BrickLoadFailureException If the brick was found, but could not be loaded.
	 * @exception UnsupportedBrickDescriptionException If this <code>BrickLoader</code> cannot 
	 * load bricks of the type specified in the description.
	 */
	public BrickFactory load(BrickDescription description)
		throws BrickNotFoundException, 
		BrickLoadFailureException, 
		UnsupportedBrickDescriptionException;
	
}

