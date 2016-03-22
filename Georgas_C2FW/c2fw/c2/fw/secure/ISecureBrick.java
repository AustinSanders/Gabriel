/*
 * Created on Jul 3, 2005
 *
 */
package c2.fw.secure;

import java.util.Set;

import c2.fw.DelegateBrick;

/**
 * A secure brick which can perform actions that require privileges. 
 * 
 * <p></p>
 * <p>
 * The privielges can come from several places. 
 * <ul>
 * <li>
 * They can be explicitly specified. Such privileges can be retried through
 * {@link #getPrivileges}
 * </li>
 * <li>
 * They can be retrieved through the running subject. A subject essentially 
 * designates what privileges the subject has.    
 * </li>
 * <li>
 * They can be retrieved indirectly through the subject's principals. This 
 * viewpoint comes from two sources of inspirations. First, in Java, a subject
 * has a set of principals, which are names can be used for different situations.
 * Second, in Role-based Access Control, a subject can perform multiple roles,
 * each role has a set of permission assignments (which are essentially 
 * privileges).
 * </li>
 * </ul>
 * </p>
 * <p></p>
 * <p>
 * Above is a privilege-centriv viewpoint. Another equivallent, yet probably
 * more natural viewpoin, is that a running subject posseses privileges,
 * and as an indirection the subject can possess multiple principals, from 
 * which the subject can associate privileges.
 * </p> 
 * 
 * @see ISubject
 * @see IPrivilege
 * @see IPrincipal
 * 
 * @author Jie Ren
 */
public interface ISecureBrick extends DelegateBrick {
	/**
	 * Get the subject for which this brick acts for 
	 * 
	 * @return the subject
	 */
	ISubject	getSubject();
	
	/**
	 * Get the principals for which this brick is acting
	 * 
	 * @return a set of principals. Could be an empty set if no 
	 * specific principals are specified. 
	 */
	Set			getPrincipals();
	
	/**
	 * Set the new principals
	 * 
	 * @param principals the new principals for which the brick is acting.
	 */
	void 		setPrincipals(Set principals);
	
	/**
	 * Get the privileges this brick possesses
	 * 
	 * @return	the set of privileges
	 */
	Set			getPrivileges();
	
	/**
	 * Get the policies this brick executes
	 * 
	 * @return the policies
	 */
	Set			getPolicies();
	
	/**
	 * Whether the access is allowed
	 * 
	 * @param	request	an access request
	 * @return	true if access is allowed, false if it is denied
	 */
	boolean		isAccessAllowed(String request);
}
