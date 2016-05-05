/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.Random;

public class NamedEntityImpl implements NamedEntity {
	protected 	Name	theName;
	static 	Random r = new Random();
	
	/**
	 * Create a named entity with a random name
	 *
	 */
	public NamedEntityImpl() {
		theName = new NameImpl(String.valueOf(r.nextInt()));
	}
	
	/**
	 * Create a named entity with the given name
	 * 
	 * @param name the name of the new entity
	 */
	public NamedEntityImpl(String name) {
		theName = new NameImpl(name);
	}

	/**
	 * Create a named entity with the given name
	 * 
	 * @param name the name of the new entity
	 */
	public NamedEntityImpl(Name name) {
		theName = name;
	}

	public void setName(Name name) {
		theName = name;
	}

	public Name getName() {
		return theName;
	}

	public String getNameString() {
		return theName.getValue();
	}
	
	public boolean equals(java.lang.Object other) {
	    if (other == null)
	        return false;
		if (getClass() != other.getClass())
			return false;
		NamedEntityImpl otherEntity = (NamedEntityImpl)other;
		if (this.theName.equals(otherEntity.theName))
			return true;
		else
			return false;
	}
	
	public int hashCode() {
		return theName.hashCode();
	}
	
	public String toString() {
		return theName.toString();
	}
}
