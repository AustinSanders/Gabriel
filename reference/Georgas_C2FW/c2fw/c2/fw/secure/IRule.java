/*
 * Created on Jul 14, 2005
 *
 */
package c2.fw.secure;

/**
 * The basic access control rule: whether a subject should be allowe an action on an object 
 * 
 * @author Jie Ren
 */
public interface IRule {
	/**
	 * Get the subject for this rule
	 * @return the subject
	 */
	String		getSubject();
	/**
	 * Get the object for this rule
	 * @return the object 
	 */
	String  	getObject();
	/**
	 * Get the action for this rule
	 * @return the action
	 */
	String  	getAction();
	/**
	 * Whether the access is allowed
	 * @return true is the action on the object by the subject is allowed, 
	 * false if it is prohibited
	 */
	boolean 	isAllowed();
}
