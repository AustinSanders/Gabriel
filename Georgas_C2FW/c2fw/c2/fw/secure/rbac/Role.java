/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.Set;

/**
 * This interface designates a role.
 * 
 * Three methods could involve role hierarchy, getAuthorizedUsers,  
 * getAuthorizedPermissions, and getAuthorizedRoles. Other methods only handle 
 * directly assigned users, permissions, and sessions.
 * 
 * @author Jie Ren
 */
public interface Role extends NamedEntity {
	/**
	 * Add a user to this role
	 * 
	 * @param aUser the new user to have this role
	 * @return true if the user is a new user, false if the role already has the user
	 */
	boolean		addUser(User aUser);
	
	/**
	 * Get the set of users assigned to this role.
	 * 
	 * @return the set of assigned users
	 */
	Set			getUsers();

	/**
	 * Get the set of all authorized users. In Core RBAC, this includes only
	 * users directly assigned the role, and is the same as getUsers. In 
	 * Hierarchical RBAC, this also includes other users through senior roles.
	 * This is always computed dynamically.
	 * 
	 * @return the set of authorized users.
	 */
	Set			getAuthorizedUsers();

	/**
	 * Delete a user from this role
	 * 
	 * @param aUser the user to delete
	 * @return true if the role has the user, false if it does not
	 */	
	boolean 	deleteUser(User aUser);

	/**
	 * Delete the role from users who has it
	 *
	 */
	void		deleteSelfFromUsers();

	/**
	 * Clear all users who has this role
	 */
	void 		clearUsers();

	/**
	 * Add a session to this role
	 * 
	 * @param aSession the session to add
	 */
	void		addSession(Session aSession);

	/**
	 * Delete a session from this role
	 * 
	 * @param aSession the session to delete
	 * @return true if the role has the session, false if the role does not
	 * have the session
	 */
	boolean		deleteSession(Session aSession);
	
	/**
	 * Delete sessions that the role participates
	 */
	void		deleteSessions();

	/**
	 * Clear sessions that the role participates
	 */
	void		clearSessions();

	/**
	 * Add a permission to the role
	 * 
	 * @param aPermission the newly added permission
	 */
	void		addPermission(Permission aPermission);
	
	/**
	 * Get whether the role has the permission
	 * 
	 * @param aPermission the permission to check
	 * @return true if the role has the permission, false otherwise.
	 */
	boolean		hasPermission(Permission aPermission);
	
	/**
	 * Get assigned permissions of this role
	 * 
	 * @return the set of assigned permissions
	 */
	Set			getPermissions();
	
	/**
	 * Get authorized permissons of this role. In Core RBAC, this is 
	 * the same as getPermissions. In Hierarchical RBAC, this also
	 * includes permissions inherited through junior roles. This is always
	 * computed dynamically.
	 *  
	 * @return the set of all authorized permissions
	 */
	Set			getAuthorizedPermissions();
	
	/**
	 * Delete a permission from the role
	 * 
	 * @param aPermission the permission to delete
	 * @return true if the role has the permission, false 
	 * if it does not have the permission
	 */
	boolean		deletePermission(Permission aPermission);

	/**
	 * Delete the role from permissions who has it
	 *
	 */
	void		deleteSelfFromPermissions();

	/**
	 * Clear all users who has this role
	 */
	void 		clearPermissions();
	
	/**
	 * Get roles authorized on this role. In Core RBAC, this only includes 
	 * the role itself. In Hierarchical RBAC, it also includes the junior
	 * roles.
	 * 
	 * @return the set of all authorized roles.
	 */
	Set			getAuthorizedRoles();
}
