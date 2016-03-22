package c2.fw;

/**
 * Basic implementation of a C2 Interface.
 * @see c2.fw.AbstractInterface
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class SimpleInterface extends AbstractInterface implements Interface{

	/**
	 * Create a new SimpleInterface.
	 * @param id Identifier of this interface.
	 * @param brick Brick to which this interface should be attached.
	 */
	public SimpleInterface(Identifier id, Brick brick){
		super(id, brick);
	}

	public String toString(){
		return "SimpleInterface{id=\"" + getIdentifier() + "; brick=\"" + getBrick() + "\"};";
	}

}

