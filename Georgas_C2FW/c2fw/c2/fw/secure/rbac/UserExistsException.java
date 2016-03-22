/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a user already exists
 * 
 * @author Jie Ren
 */
public class UserExistsException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public UserExistsException(User aUser) {
		super(aUser.getName() + " exists");
	}
}
