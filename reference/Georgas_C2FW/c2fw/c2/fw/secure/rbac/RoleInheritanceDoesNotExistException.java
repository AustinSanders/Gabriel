/*
 * Created on Aug 23, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that an ascendant role does not inherit from a descendant role
 * 
 * @author Jie Ren
 */
public class RoleInheritanceDoesNotExistException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public RoleInheritanceDoesNotExistException(Role ascendant, Role descendant) {
		super(ascendant.getName() + " does not inherits from " + descendant.getName());
	}
}