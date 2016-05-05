/*
 * Created on Oct 25, 2005
 *
 */
package c2.fw.secure.tm;

import java.util.Collection;
import java.util.Set;

import c2.fw.secure.rbac.Name;

/**
 * This interface represents the trust management model that is based on 
 * decentralized roles. It allows those autonomous decentralized RBAC models
 * to trust each other.  
 * 
 * The RBAC standard defines users and roles. What is implicit is that these 
 * "entities" are defined within a single administration domain. In Professor 
 * Ninghui Li's RBTM framework, each entity, which seems like a user, actually defines 
 * its own roles. Thus, it does not directly correspond to the user in the RBAC
 * standard. Instead, it should be treated as the defining domain in which users
 * and roles are defined. In short, in RBAC users receive roles, and are defined 
 * in a domain; in Li's RBTM, entities, which look like users, define roles, and
 * are actually domains. 
 * 
 * Thus, a conceptual bridge is necessary to bring RBAC and RBTM together. The 
 * RBTM is used for inter-domain role trust grant, where RBAC is used for 
 * intra-domain role definition. Users only exist within RBAC. The credentials
 * part of the RBTM engine are adapted to suit our needs. The inference 
 * engine part of the RBTM framework, which deals with reasoning on supplied
 * credentials, can be used "as is", with a relaxation that a backward search, 
 * which originally finds entities that have a specific roles, will return the
 * set of trusted intermediate roles. From these roles it is possible to find 
 * the trusted users defined in each domain.
 * 
 * Theoretically it is possible to use Li's RBTM credentials to define users and
 * roles association. Since we already have RBAC to accompolish this task, we 
 * only use the role trust part of the original RBTM framework.       
 * 
 * @author Jie Ren
 */
public interface RBTM {
	/**
	 * Grant trust to foreign roles
	 *  
	 * @param local		the local role
	 * @param foreign	the foreign roles that are trusted to act as local roles
	 */
	void		grantTrust(RoleDecentralized local, RoleExpression foreign);
	
	/**
	 * Revoke previously granted trust to foreign roles
	 * 
	 * @param local		the local role
	 * @param foreign	the foreign roles that are trusted to act as local roles
	 * @throws TrustDoesNotExistException
	 */
	void		revokeTrust(RoleDecentralized local, RoleExpression foreign)
				throws TrustDoesNotExistException;
	
	/**
	 * Get the trusted foreign roles that act as the local role
	 * 
	 * @param local		the local role
	 * @return	the set of trusted foreign roles
	 */
	Set 		getTrustedForeignRoles(RoleDecentralized local);

	/**
	 * Get the trusting foreign roles that permit the local role to act for them
	 * 
	 * @param local		the local role
	 * @return	the set of trusting foreign roles
	 */
	Set 		getTrustingForeignRoles(RoleDecentralized local);
	
	/**
	 * Get the trusted roles in a domain that can act as a foreign role
	 * 
	 * @param trustor		the foreign role
	 * @param trustee		the local domain
	 * @return the set of roles in the local domain that can act for the foreign 
	 * role. It could be empty if no local role is trusted by the foreign role.
	 */
	Set			getTrustedLocalRoles(RoleDecentralized trustor, Domain trustee);

	/**
	 * Get the trusting roles in a domain that allows a foreign role to act for them
	 * 
	 * @param trustee		the foreign role
	 * @param trustor		the local domain
	 * @return the set of roles in the local domain that allows the foreign role 
	 * to act for them. It could be empty if no local role trusts the foreign role.
	 */
	Set			getTrustingLocalRoles(RoleDecentralized trustee, Domain trustor);

	/**
	 * Add a RBAC that uses this as its trust manager
	 * 
	 * @param rbac	the RBAC that uses this as the trust manager
	 */
	void					addRBAC(RBACDecentralized rbac);
	
	/**
	 * Remove a previously added RBAC
	 * 
	 * @param rbac	the RBAC to remove
	 * @return the removed RBAC. It would be the same if the input RBAC exists,
	 * null if the input RBAC does not exist
	 */
	RBACDecentralized		removeRBAC(RBACDecentralized rbac);
	
	/**
	 * Remove a previously added RBAC
	 * 
	 * @param domainName the domain name of the RBAC to remove
	 * @return the removed RBAC. Null if no existing RBAC matches the name
	 */
	RBACDecentralized		removeRBAC(Name domainName);
	
	/**
	 * Get an RBAC by name.
	 *  
	 * @param domainName the domain name of the RBAC to retrieve
	 * @return the retrieved RBAC. Null if no existing RBAC matches the name
	 */
	RBACDecentralized		getRBAC(Name domainName);

	/**
	 * Get an RBAC that owns a role
	 * 
	 * @param role the role whose owning RBAC is to retrieve
	 * @return the retrieved RBAC. Null if no existing RBAC owns the role
	 */
	RBACDecentralized		getRBAC(RoleDecentralized role);
	
	/**
	 * Get the collection of RBACs that uses this trust manager as the manager
	 * 
	 * @return the collection of RBACs
	 */
	Collection				getRBACs();
	
	/**
	 * Get the real RBTM used by this RBTM. 
	 * 
	 * @return for RBTMImpl, the value would be itself. For RBTMWithXACML, the 
	 * value is the internal RBTMImpl. 
	 */
	RBTM					getRealRBTM();
	
	/**
	 * Check whether a user can perform an operation on an object
	 * 
	 * @param localUser		the name of the user
	 * @param localDomain	the name of the user's domain
	 * @param foreignDomain the domain in which the user wants to perform. 
	 * Could be the same as the previous domain.
	 * @param operation		the name of the operation
	 * @param object		the name of the object
	 * @return true if the user can perform the action, false otherwise
	 */
	boolean					checkAccess(Name localUser, Name localDomain, 
	        							Name foreignDomain, Name operation, 
	        							Name object);
}
