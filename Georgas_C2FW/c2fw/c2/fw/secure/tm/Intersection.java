package c2.fw.secure.tm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
/**
 * Represent a Intersection. This feature is not supported.
 */
public class Intersection implements RoleExpression {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	private List parts = new LinkedList();

	public Iterator getParts() {
		return parts.iterator();
	}

	public boolean equals(Object o) {
		return (o instanceof Intersection)
				&& parts.equals(((Intersection) o).parts);
	}

	public int hashCode() {
		return parts.hashCode();
	}

	public String toString() {
		return parts.toString();
	}

}
