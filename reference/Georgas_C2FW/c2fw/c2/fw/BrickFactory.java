//!!(C)!!
package c2.fw;

/**
 * Describes objects that can create instances of certain brick types.
 * Each brick factory creates instances of a single type of brick.
 * <code>BrickFactory</code> are usually created by <code>BrickLoader</code>s.
 * 
 * @see c2.fw.BrickLoader
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface BrickFactory{

	/**
	 * Create a new brick given the initialization parameters.
	 * @param id Identifier for the new <code>Brick</code>.
	 * @param parameters Initialization parameters for the new <code>Brick</code>.
	 * @return Newly created <code>Brick</code>.
	 * @exception BrickCreationException if the brick could not be created.
	 * @exception IllegalArgumentException if the parameter list is invalid for the brick type.
	 */
	public Brick create(Identifier id, InitializationParameter[] parameters) throws BrickCreationException;

}

