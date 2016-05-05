//!!(C)!!

package c2.fw;

/**
 * The <code>Identifier</code> interface should be implemented by classes that can be used to 
 * identify C2 entities.
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */

public interface Identifier extends java.io.Serializable{

	/**
	 * Tests whether two identifiers are equivalent.  The other identifier does not necessarily
	 * have to be of the same type, as long as both identifiers could equally refer to the
	 * same entity.
	 * @param otherIdentifier Another object to compare to this one for equality.
	 * @return <code>true</code> if they are equivalent, <code>false</code> if they are not.
	 */
	public boolean equals(Object otherIdentifier);
	
	/**
	 * Returns the hash code of this object.  This must be overridden because
	 * we have also overridden the equals() method, changing its semantics.
	 * @return Hash code for this object.
	 */
	public int hashCode();
	
	/**
	 * Gets a human-readable representation of this <code>Identifier</code>.
	 * @return a human-readable representation of this <code>Identifier</code>.
	 */
	public String toString();

}

