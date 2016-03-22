/*
 * Created on Aug 24, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.Set;

/**
 * A role reciver. It can be either a role receiver, or a session.
 * 
 * @author Jie Ren
 */
public interface RoleReceiver extends NamedEntity {
	/**
	 * Add a role to the role receiver
	 * 
	 * @param aRole the role to add
	 * @return true if the role is a new role, false if the role receiver already has the role
	 */
	boolean		addRole(Role aRole);
	/**
	 * Add a set of roles to the role receiver
	 * 
	 * @param roles the set of roles to add
	 * @return true if the set of roles has changed because of the addition, false otherwise
	 */
	boolean		addRoles(Set roles);
	/**
	 * Get whether the role receiver has a role
	 * 
	 * @param aRole the role to check
	 * @return true if the role receiver has the role, false if the role receiver does not have the role
	 */
	boolean		hasRole(Role aRole);
	/**
	 * Get the roles that this role receiver has
	 * 
	 * @return the set of roles that this role receiver has
	 */
	Set			getRoles();
	/**
	 * Get whether the role receiver is authorized to a role, through role hierarchy.
	 * 
	 * @param aRole the role to check
	 * @return true if the role receiver is authorized role, either directly through 
	 * its own roles, or indirectly through a role junior to its own roles
	 */
	boolean		isAuthorized(Role aRole);
	/**
	 * Get the set of roles that this role receiver is authorized, either directly through 
	 * its own roles, or indirectly through a role junior to its own roles
	 * 
	 * @return the set of authorized roles
	 */
	Set			getAuthorizedRoles();
	/**
	 * Delete a role from the role receiver
	 * @param aRole		the role to delete
	 * @return true if the role receiver has the role, false if it does not have the role
	 */
	boolean		deleteRole(Role aRole);
	/**
	 * Delete a set of roles from the role receiver
	 * 
	 * @param roles the set of roles to delete
	 * @return true if the set of roles has changed because of the deletion, false otherwise
	 */
	boolean		deleteRoles(Set roles);
	/**
	 * Delete the role receiver from the roles it has
	 */
	void		deleteSelfFromRoles();
	/**
	 * Clear roles that the role receiver has
	 */
	void 		clearRoles();
	
	/**
	 * Get the set of permissions granted to the role receiver, through roles
	 *  
	 * @return the set of granted permissions
	 */
	Set			getPermissions();
}
