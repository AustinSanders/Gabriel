/*
 * Created on Aug 22, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a role already exists
 * 
 * @author Jie Ren
 */
public class RoleExistsException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public RoleExistsException(Role aRole) {
		super(aRole.getName() + " exists");
	}

}
