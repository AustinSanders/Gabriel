/*
 * Created on Aug 22, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a role does not exist
 * 
 * @author Jie Ren
 */
public class RoleDoesNotExistException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public RoleDoesNotExistException(Role aRole) {
		this(aRole.getName());
	}

	public RoleDoesNotExistException(Name roleName) {
		super(roleName + " does not exist");
	}
}
