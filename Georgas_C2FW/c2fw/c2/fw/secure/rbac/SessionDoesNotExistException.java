/*
 * Created on Aug 23, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * Exception signifying that a session does not exist
 * 
 * @author Jie Ren
 */
public class SessionDoesNotExistException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public SessionDoesNotExistException(Session aSession) {
		this(aSession.getName());
	}
	
	public SessionDoesNotExistException(Name aSession) {
		super(aSession + " does not exist");
	}
}
