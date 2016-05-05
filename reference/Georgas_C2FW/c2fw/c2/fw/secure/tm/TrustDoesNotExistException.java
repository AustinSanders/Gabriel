/*
 * Created on Oct 25, 2005
 *
 */
package c2.fw.secure.tm;

import c2.fw.secure.rbac.RBACException;

/**
 * Exception signifying that a trust relationship to be revoked does not exist
 * 
 * @author Jie Ren
 */
public class TrustDoesNotExistException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	
	public TrustDoesNotExistException(RoleDecentralized local, RoleExpression foreign) {
		super(local + " does not trust " + foreign);
	}
}
