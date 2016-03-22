/*
 * Created on Aug 24, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a permission already exists
 * 
 * @author Jie Ren
 */
public class PermissionAlreadyExistsException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public PermissionAlreadyExistsException(Permission aPermission) {
		super(aPermission.getName() + " already exists");
	}
}
