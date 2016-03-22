/*
 * Created on Aug 23, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a permission does not exist
 * 
 * @author Jie Ren
 */
public class PermissionDoesNotExistException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public PermissionDoesNotExistException(Permission aPermission) {
		this(aPermission.getName());
	}

	public PermissionDoesNotExistException(Name aName) {
		super(aName + " does not exist");
	}
}

