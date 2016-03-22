/*
 * Created on Aug 23, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a user does not have a session
 * 
 * @author Jie Ren
 */
public class UserDoesNotHaveSessionException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public UserDoesNotHaveSessionException(User aUser, Session aSession) {
		super(aUser.getName() + " does not have " + aSession.getName());
	}

}
