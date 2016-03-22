/*
 * Created on Aug 23, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a session does not have a role
 * 
 * @author Jie Ren
 */
public class SessionDoesNotHaveRoleException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public SessionDoesNotHaveRoleException(Session aSession, Role aRole) {
		super(aSession.getName() + " does not have " + aRole.getName());
	}
}
