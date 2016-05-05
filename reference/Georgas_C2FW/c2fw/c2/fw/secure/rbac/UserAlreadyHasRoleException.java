/*
 * Created on Aug 22, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a user already has a role
 * 
 * @author Jie Ren
 */
public class UserAlreadyHasRoleException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public UserAlreadyHasRoleException(User aUser, Role aRole) {
		super(aUser.getName() + " already has " + aRole.getName());
	}

}
