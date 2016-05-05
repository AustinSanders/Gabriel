/*
 * Created on Aug 22, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a user does not exist
 * 
 * @author Jie Ren
 */
public class UserDoesNotExistException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public UserDoesNotExistException(User aUser) {
		this(aUser.getName());
	}

	public UserDoesNotExistException(Name userName) {
		super(userName + " does not exist");
	}
}
