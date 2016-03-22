/*
 * Created on Aug 23, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a user does not exist
 * 
 * @author Jie Ren
 */
public class RoleDoesNotHavePermissionException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public RoleDoesNotHavePermissionException(Role aRole, Permission aPermission) {
		super(aRole.getName() + " does not have " + aPermission.getName());
	}
}
