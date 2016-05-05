//!!(C)!!
package c2.fw;

/**
 * Interface describing a C2 weld between two bricks.  A weld consists
 * of two brick+interface identifiers.  They are listed as "first" and "second,"
 * but the order does not matter.
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface Weld extends java.io.Serializable{

	/**
	 * Determines if two welds are equal.  Two welds are considered to be
	 * equal if they have the same two endpoints.
	 * @param otherWeld the weld object to compare to this one.
	 */
	public boolean equals(Object otherWeld);
	
	/**
	 * Gets the identifier of the first brick in this weld.
	 * @return <code>BrickInterfaceIdPair</code> of the first brick+interface.
	 */
	public BrickInterfaceIdPair getFirstEndpoint();	

	/*
	 * Gets the identifier of the second brick in this weld.
	 * @return <code>BrickInterfaceIdPair</code> of the second brick+interface.
	 */
	public BrickInterfaceIdPair getSecondEndpoint();

}

