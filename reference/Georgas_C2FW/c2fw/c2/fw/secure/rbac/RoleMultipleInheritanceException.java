/*
 * Created on Aug 24, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a role cannot have multiple inheritance in a limited hierarchical RBACCore model
 * 
 * @author Jie Ren
 */
public class RoleMultipleInheritanceException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public RoleMultipleInheritanceException(Role aRole) {
		super(aRole.getName() + " already has a descendant");
	}

}

