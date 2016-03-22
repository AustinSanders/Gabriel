/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.Set;

/**
 * This is an implementation of ANSI INCITS 359-2004, Role Based Access Control.
 *
 * <p> 
 * This interface implements the Core RBACCore component of the RBAC Reference 
 * Model. A subinterface, RBACHierarchical {@link RBACHierarchical}, implements 
 * the Hierarchical RBAC component. The Static Separation of Duty Relations component
 * and the Dynamic Separation of Duty Relations comopnent are not implemented, 
 * due to the relative irrelevance of those comonents to our research. However, 
 * it should be straightforward to add those components.
 * </p>
 * 
 * <p>
 * This is a procedural interface, modeled directly after the specifications 
 * of the standard. This interface is expected to be used as the access point
 * to the functionalities of RBAC. The constituent intefaces, such as {@link User},
 * {@link Role}, {@link Session}, and {@link Permission}, provide the real
 * functionalities, in a more object-oriented manner. This interface throws 
 * security related exceptions, while the individual interfaces return different
 * values to signify structural issues.
 * </p> 
 * 
 * <p>
 * To handle hierarchical roles, a new control interface, RBACHierarchical is 
 * defined, with new methods. Interface Role is extended to 
 * {@link RoleHierarchical}. Also, interface Role has hook methods 
 * ({@link Role#getAuthorizedUsers}, {@link Role#getAuthorizedPermissions}, 
 * {@link Role#getAuthorizedRoles}) that can incorporate hierarchical roles.
 * </p> 
 *
 * @author Jie Ren
 */
public interface RBACCore {
	/*
	 * Administrative Commands
	 */ 
	// Maintain USERS
	/**
	 * Add a new user.
	 * 
	 * @param newUser 		the name of the new User
	 * @return the newly added user
	 * @throws UserExistsException
	 */
	User 			addUser(Name newUser) throws UserExistsException;
	/**
	 * Delete a user.
	 * 
	 * @param userToDelete 	the the user to delete
	 * @throws UserDoesNotExistException
	 */
	void			deleteUser(User userToDelete) throws UserDoesNotExistException;

	// Maintain ROLES
	/**
	 * Add a new role.
	 * 
	 * @param newRole		the name of the new role
	 * @return the newly added role. 
	 * @throws RoleExistsException
	 */
	Role			addRole(Name newRole) throws RoleExistsException;
	/**
	 * Delete a role.
	 * 
	 * @param roleToDelete 	the role to delete
	 * @throws RoleDoesNotExistException
	 */
	void			deleteRole(Role roleToDelete) throws RoleDoesNotExistException;
	
	// Maintain User-to-Role Assignment (UA)
	/**
	 * Assign a role to a user.
	 * 
	 * @param aUser 		the user who will be assigned the role
	 * @param aRole 		the role assigned to the user
	 * @throws UserDoesNotExistException
	 * @throws RoleDoesNotExistException
	 * @throws UserAlreadyHasRoleException
	 */
	void			assignUser(User	aUser, Role	aRole) 
					throws 	UserDoesNotExistException, RoleDoesNotExistException,
							UserAlreadyHasRoleException;
	/**
	 * Deassing a role from a user.
	 * 
	 * RBACCore spec permits both deassigning just directly authorized roles or 
	 * deassigning all indirectly authorized roles
	 * 
	 * @param aUser 		the user whose roles will be deassinged
	 * @param aRole 		the role that is deassigned from the user
	 * @throws UserDoesNotExistException
	 * @throws RoleDoesNotExistException
	 * @throws UserDoesNotHaveRoleException
	 */   
	void			deassignUser(User aUser, Role aRole)
					throws 	UserDoesNotExistException, RoleDoesNotExistException,
							UserDoesNotHaveRoleException;
	
	// Maintain Permission-to-Role Assignment (PA)
	/**
	 * The standard does not specifiy a method to explicitly create new 
	 * permissions. This method serves the purpose of implicitly creating
	 * new permissions. 
	 * 
	 * @param anOperation 	the operation of the granted permission
	 * @param anObject 		the object of the granted permission
	 * @return the newly created permission 
	 */
	Permission		createPermission(Operation anOperation, Object anObject)
					throws PermissionAlreadyExistsException;
	 
	/**
	 * Grant a permission to a role.
	 * 
	 * @param anOperation 	the operation of the granted permission
	 * @param anObject 		the object of the granted permission
	 * @param aRole 		the role to receive the permission
	 * @throws PermissionDoesNotExistException
	 * @throws RoleDoesNotExistException
	 */
	void			grantPermission(Operation anOperation, Object anObject, Role aRole)
					throws PermissionDoesNotExistException, RoleDoesNotExistException;
	
	/**
	 * Grant a permission to a role.
	 * 
	 * This method provides a programatic convinience to invoke the above 
	 * conceptual method {@link #grantPermission(Operation, Object, Role)}.
	 * The standard does not include this method.  
	 * 
	 * @param aPermission 	the granted permission
	 * @param aRole 		the role to receive the permission
	 * @throws PermissionDoesNotExistException
	 * @throws RoleDoesNotExistException
	 */
	void			grantPermission(Permission aPermission, Role aRole)
					throws PermissionDoesNotExistException, RoleDoesNotExistException;
	
	/**
	 * Revoke a permission from a role.
	 * 
	 * @param anOperation	the operation of the revoked permission
	 * @param anObject		the object of the revoked permission
	 * @param aRole			the role to lose the permission
	 * @throws PermissionDoesNotExistException
	 * @throws RoleDoesNotExistException
	 * @throws RoleDoesNotHavePermissionException
	 */
	void			revokePermission(Operation anOperation, Object anObject, Role aRole)
					throws 	PermissionDoesNotExistException, RoleDoesNotExistException, 
							RoleDoesNotHavePermissionException;
	
	/**
	 * Revoke a permission from a role.
	 * 
	 * This method provides a programatic convinience to invoke the above 
	 * conceptual method {@link #revokePermission(Operation, Object, Role)}.
	 * The standard does not include this method.  
	 * 
	 * @param aPermission 	the revoked permission
	 * @param aRole			the role to lose the permission
	 * @throws PermissionDoesNotExistException
	 * @throws RoleDoesNotExistException
	 * @throws RoleDoesNotHavePermissionException
	 */
	void			revokePermission(Permission aPermission, Role aRole)
					throws 	PermissionDoesNotExistException, RoleDoesNotExistException,
							RoleDoesNotHavePermissionException;
	
	/*
	 * Supporting System Functions
	 */
	// Maintain SESSIONS
	/**
	 * Create a session for a user.
	 * 
	 * @param aUser			the user of the session
	 * @param ars			the active role set, a subset of user's roles 
	 * 						that should be active for the sesion. Can be null
	 * 						if roles are to be added later.
	 * @return a newly created session for the user
	 * @throws UserDoesNotExistException
	 */
	Session			createSession(User aUser, Set ars) throws UserDoesNotExistException;
	/**
	 * Delete a user session.
	 * 
	 * @param aUser			the user of the session being deleted
	 * @param aSession		the session being deleted
	 * @throws UserDoesNotExistException
	 * @throws SessionDoesNotExistException
	 * @throws UserDoesNotHaveSessionException 
	 */
	void			deleteSession(User aUser, Session aSession) 
					throws 	UserDoesNotExistException, SessionDoesNotExistException,
							UserDoesNotHaveSessionException;
	/**
	 * Add an active role for a user session.
	 * 
	 * @param aUser			the user of the session
	 * @param aSession		the session
	 * @param role			the role to add for the user session
	 * @throws UserDoesNotExistException
	 * @throws RoleDoesNotExistException
	 * @throws SessionDoesNotExistException
	 * @throws UserDoesNotHaveRoleException
	 * @throws UserDoesNotHaveSessionException 			
	 */
	void			addActiveRole(User aUser, Session aSession, Role aRole)
					throws  UserDoesNotExistException, RoleDoesNotExistException,
							SessionDoesNotExistException, UserDoesNotHaveRoleException,
							UserDoesNotHaveSessionException;
	/**
	 * Drop an active role from a user session
	 * 
	 * @param aUser			the user of the session
	 * @param aSession		the session
	 * @param role			the role to drop from the user session			
	 * @throws UserDoesNotExistException
	 * @throws RoleDoesNotExistException
	 * @throws SessionDoesNotExistException
	 * @throws UserDoesNotHaveRoleException
	 * @throws UserDoesNotHaveSessionException
	 * @throws SessionDoesNotHaveRoleException 			
	 */
	void			dropActiveRole(User aUser, Session aSession, Role aRole)
					throws  UserDoesNotExistException, RoleDoesNotExistException,
							SessionDoesNotExistException, UserDoesNotHaveRoleException,
							UserDoesNotHaveSessionException, SessionDoesNotHaveRoleException;
	/**
	 * Check whether the session has permission to operate an object
	 *  
	 * @param aSession		the user session to check permission for
	 * @param anOperation	the operation to perform
	 * @param anObject		the object to operate
	 * @return true if the session has the permission, false if the session 
	 * does not have the permission 
	 */
	boolean			checkAccess(Session aSession, Operation anOperation, Object anObject) 
					throws SessionDoesNotExistException;
	
	/*
	 * Review Functions
	 */  
	/**
	 * Get users assigned to a role.
	 * 
	 * @param aRole			the role to query the assigned users
	 * @return the set of users assigned to the role
	 * @throws RoleDoesNotExistException
	 */
	Set				assignedUsers(Role aRole) throws RoleDoesNotExistException;
	/**
	 * Get roles assigned to a user.
	 * 
	 * @param aUser			the user to query the assigned roles
	 * @return the set of roles assigned to the user
	 * @throws UserDoesNotExistException
	 */
	Set				assingedRoles(User aUser) throws UserDoesNotExistException;
	/**
	 * Get permissions assigned to a role.
	 * 
	 * @param aRole			the role to query permissions
	 * @return the set of permissions assigned to the role
	 * @throws RoleDoesNotExistException
	 */
	Set				rolePermissions(Role aRole) throws RoleDoesNotExistException;
	/**
	 * Get permissions assigned to a user.
	 * 
	 * @param aUser			the user to query permissions
	 * @return the set of permissions assigned to the user
	 * @throws UserDoesNotExistException
	 */
	Set				userPermissions(User aUser) throws UserDoesNotExistException;
	/**
	 * Get roles assigned to a session.
	 * 
	 * @param aSession		the session to query roles
	 * @return the set of roles assigned to the session
	 * @throws SessionDoesNotExistException
	 */
	Set				sessionRoles(Session aSession) throws SessionDoesNotExistException;
	/**
	 * Get permissions assigned to a session
	 * 
	 * @param aSession		the session to query permissions
	 * @return the set of permission assigned to the session
	 * @throws SessionDoesNotExistException
	 */
	Set				sessionPermissions(Session aSession) throws SessionDoesNotExistException;
	/**
	 * Get the operations a role can perform on an object.
	 * 
	 * @param aRole			the role to query operations
	 * @param anObject		the object to operate on
	 * @return the set of operations that the role can perform on the object
	 * @throws RoleDoesNotExistException
	 */
	Set				roleOperationsOnObject(Role aRole, Object anObject) throws RoleDoesNotExistException;
	/**
	 * Get the operations a user can perform on an object.
	 * 
	 * @param aUser			the user to query operations
	 * @param anObject		the object to operate on
	 * @return the set of operations that the user can perform on the object
	 * @throws UserDoesNotExistException
	 */
	Set				userOperationsOnObject(User aUser, Object anObject) throws UserDoesNotExistException;

	// below is not part of the standard
	/**
	 * Whether a user with a name exists
	 * 
	 * @param name 		the name of the user to check
	 * @return	true if there exists a user with such a name, false if there is no such user
	 */
	boolean			userExists(Name name);

	/**
	 * Get the user with a certain name
	 * 
	 * @param name		the name of the user to check
	 * @return	the user
	 * @throws UserDoesNotExistException
	 */
	User			getUser(Name name) throws UserDoesNotExistException;
	
	/**
	 * Whether a role with a name exists
	 * 
	 * @param name 		the name of the role to check
	 * @return	true if there exists a role with such a name, false if there is no such role
	 */
	boolean			roleExists(Name name);
	
	/**
	 * Get the role with a certain name
	 * 
	 * @param name		the name of the role to check
	 * @return	the role
	 * @throws RoleDoesNotExistException
	 */
	Role			getRole(Name name) throws RoleDoesNotExistException;
	
	/**
	 * Whether a permission with a name exists
	 * 
	 * @param name 		the name of the permission to check
	 * @return	true if there exists a permission with such a name, false if there is no such permission
	 */
	boolean			permissionExists(Name name);
	
	/**
	 * Get the permission for an operation on an object 
	 * 
	 * @param anOperation		the operation part of the permission
	 * @param anObject			the object part of the permission
	 * @return	the permission
	 * @throws RoleDoesNotExistException
	 */
	Permission		getPermission(Operation anOperation, Object anObject) throws PermissionDoesNotExistException;
	
	/**
	 * Whether a session with a name exists
	 * 
	 * @param name 		the name of the session to check
	 * @return	true if there exists a session with such a name, false if there is no such session
	 */
	boolean			sessionExists(Name name);

	/**
	 * Get the session with a certain name
	 * 
	 * @param name		the name of the session to check
	 * @return	the session
	 * @throws SessionDoesNotExistException
	 */
	Session			getSession(Name name) throws SessionDoesNotExistException;
	
	/**
	 * Get the set of all users
	 * 
	 * @return the set of all users
	 */
	Set				getUsers();
	
	/**
	 * Get the set of all roles
	 * 
	 * @return the set of all roles
	 */
	Set				getRoles();
	
	/**
	 * Get the set of all permissions
	 * 
	 * @return the set of all permissions
	 */
	Set				getPermissions();
	
	/**
	 * Get the set of all sessions
	 * 
	 * @return the set of all sessions
	 */
	Set				getSessions();
	
	/**
	 * Check whether the user has permission to operate an object
	 *  
	 * @param aUser			the user to check permission for
	 * @param anOperation	the operation to perform
	 * @param anObject		the object to operate
	 * @return true if the user has the permission, false if the user
	 * does not have the permission 
	 */
	boolean			checkAccess(User aUser, Operation anOperation, Object anObject) 
					throws UserDoesNotExistException;
	
	/**
	 * Check whether the role has permission to operate an object
	 *  
	 * @param aRole			the role to check permission for
	 * @param anOperation	the operation to perform
	 * @param anObject		the object to operate
	 * @return true if the role has the permission, false if the role 
	 * does not have the permission 
	 */
	boolean			checkAccess(Role aRole, Operation anOperation, Object anObject) 
					throws RoleDoesNotExistException;
	
	/**
	 * Specify the sessions should be terminated when a user or a role is deleted
	 */
	public static int	SESSION_TERMINATE = 0;
	/**
	 * Specify the sessions should continue normally
	 */
	public static int	SESSION_CONTINUE_NORMALLY = 1;
	/**
	 * Specify the sessions should continue, but with privileges of the deleted user
	 * or role stripped off
	 */
	public static int	SESSION_CONTINUE_DOWNGRADE = 2;
	/**
	 * Get the strategy to handle active sessions when a user or a role is deleted. 
	 * The strategy can be {@link #SESSION_TERMINATE },
	 * {@link #SESSION_CONTINUE_NORMALLY} , or {@link #SESSION_CONTINUE_DOWNGRADE}
	 * 
	 * @return the strategy for handling sessions when a user or a role is deleted
	 */
	int				getActiveSessionStrategy();
	/**
	 * Terminate a session
	 * 
	 * @param s the session to terminate
	 */
	void			terminateSession(Session s);
}
