/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;


public class NameImpl implements Name {
	String	name;

	/**
	 * Create a name
	 * 
	 * @param name the string value of the name
	 */
	public NameImpl(String name) {
		this.name = name;
	}
	
	public String	getValue() {
		return name;
	}
	
	public boolean equals(java.lang.Object other) {
	    if (other == null)
	        return false;
		if (getClass() != other.getClass())
			return false;
		NameImpl otherName = (NameImpl)other;
		if (this.name.equals(otherName.name))
			return true;
		else
			return false;
	}
	
	public int hashCode() {
		// overriding this method is necessary for set containment test
		return name.hashCode();
	}
	
	public String toString() {
		return name;
	}
}
