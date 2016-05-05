/*
 * Created on Aug 23, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a role does not exist
 * 
 * @author Jie Ren
 */
public class RoleCycleException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public RoleCycleException(Role ascendant, Role descendant) {
		super(descendant.getName() + " already inherits from " + ascendant.getName());
	}

}
