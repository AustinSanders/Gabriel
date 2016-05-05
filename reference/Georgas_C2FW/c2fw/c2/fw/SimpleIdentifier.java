//!!(C)!!
package c2.fw;

/**
 * The <code>SimpleIdentifier</code> class describes identifiers with only one
 * property--a name.
 * 
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public final class SimpleIdentifier implements Identifier, java.io.Serializable{

	private String name;
	
	/**
	 * Creates a new <code>SimpleIdentifier</code> with the given name.
	 * @param name the name of the identified entity.
	 */
	public SimpleIdentifier(String name){
		this.name = name;
	}
	
	/**
	 * Gets the string name of this identifier.
	 * @return the name of the identified entity.
	 */
	public String getName(){
		return name;
	}

	/**
	 * Determines if two <code>Identifier</code>s are equal.
	 * @param otherIdentifier The other identifier to test for equality with this one.
	 * @return <code>true</code> if the other identifier is a <code>SimpleIdentifier</code> and has the
	 * same name as this one, <code>false</code> otherwise.
	 */
	public boolean equals(Object otherIdentifier){
		//System.out.println("Comparing " + toString() + " to " + otherIdentifier.toString() + ".");
		if(!(otherIdentifier instanceof SimpleIdentifier)){
			//System.out.println("Failed comparison due to type mismatch.");
			return false;
		}
		
		SimpleIdentifier id = (SimpleIdentifier)otherIdentifier;
		//System.out.println("Matched on type.  Checking names.");
		//System.out.println("Names match? " + this.getName().equals(id.getName()));
		return this.getName().equals(id.getName());
		//return this.name == id.name;
	}
	
	public int hashCode(){
		return /*SimpleIdentifier.class.hashCode() ^*/ name.hashCode();
	}
	
	/**
	 * Gets the name of this identifier.
	 * @return name of this identifier.
	 */
	public String toString(){
		return name;
	}

}

