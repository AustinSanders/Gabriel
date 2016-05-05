/*
 * Created on Aug 22, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a user does not have a role
 * 
 * @author Jie Ren
 */
public class UserDoesNotHaveRoleException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public UserDoesNotHaveRoleException(User aUser, Role aRole) {
		super(aUser.getName() + " does not have " + aRole.getName());
	}

}
