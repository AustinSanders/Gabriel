/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * A named entity: a user, a role, an object, an operation
 * 
 * @author Jie Ren
 */
public interface NamedEntity {
	/**
	 * Set the name of this object
	 * 
	 * @param name	the name of this object
	 */
	void	setName(Name name);
	/**
	 * Get the name of this object
	 * 
	 * @return the name of this object
	 */
	Name	getName();
	/**
	 * Get the string valueof the name
	 * 
	 * @return the string value of the name of this object
	 */
	String	getNameString();
}
