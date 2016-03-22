//!!(C)!!
package c2.fw;

/**
 * Interface for objects that describe types of Bricks.  <code>BrickDescription</code>s are
 * passed to <code>BrickLoader</code>s to describe the brick to load.
 *
 * @see c2.fw.Brick
 * @see c2.fw.BrickLoader
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */ 
public interface BrickDescription extends java.io.Serializable{

	/**
	 * Determines whether two BrickDescriptions are equal.
	 * @return <code>true</code> if the two descriptions match, <code>false</code> otherwise.
	 */
	public boolean equals(Object otherDescription);
	
	/**
	 * Returns the hash code of this object.  This must be overridden because
	 * we have also overridden the equals() method, changing its semantics.
	 * @return Hash code for this object.
	 */
	public int hashCode();

	/**
	 * Gets a string repesentation of this description.
	 * @return String representation of this description.
	 */
	public String toString();
}

