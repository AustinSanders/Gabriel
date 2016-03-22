/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implement the RBACCore interface 
 * 
 * @author Jie Ren
 */
public class RBACCoreImpl implements RBACCore {
	// Relations depicted in p.6, Figure 2 of the standard
	// USERS
	Set		allUsers = new HashSet();
	Set		USERS = allUsers;
	// ROLES
	Set		allRoles = new HashSet();
	Set		ROLES = allRoles;
	// PERMS
	Set		allPermissions = new HashSet();
	Set		PRMS = allPermissions;
	// SESSIONS
	Set		allSessions = new HashSet();
	Set		SESSIONS = allSessions;

	// Mapping of names to USERS/ROLES/PERMS/SESSIONS
	Map		nameToUser = new HashMap();
	Map		nameToRole = new HashMap();
	Map		nameToSession = new HashMap();
	Map		nameToPermission = new HashMap();
	
	// the strategy to handle active sessions when a user or a role is deleted
	int			activeSessionStrategy = RBACCore.SESSION_CONTINUE_DOWNGRADE;

	// whether we should automatically add users and roles when we call 
	//  getUser by name and getRole by name
	boolean		autoAddOnGet = false;

	/**
	 * Create an implementation with RBACCore.SESSION_CONTINUE_DOWNGRADE 
	 * and no autoAddOnGet
	 *
	 */
	public RBACCoreImpl() {
	    this(SESSION_CONTINUE_DOWNGRADE, false);
	}
	
	/**
	 * Create an implementation with a session strategy and no autoAddOnGet
	 * 
	 * @param activeSessionStrategy the active session strategy
	 */
	public RBACCoreImpl(int activeSessionStrategy) {
		this(activeSessionStrategy, false);
	}

	/**
	 * Create an implementation with RBACCore.SESSION_CONTINUE_DOWNGRADE
	 * and a choice on auto addition
	 * 
	 * @param autoAddOnGet true if users and roles should be automatically added
	 * on get operations
	 */
	public RBACCoreImpl(boolean autoAddOnGet) {
		this(SESSION_CONTINUE_DOWNGRADE, autoAddOnGet);
	}
	
	/**
	 * Create an implementation with with an active session strategy
	 * and a choice on auto addition
	 * 
	 * @param activeSessionStrategy the active session strategy
	 * @param autoAddOnGet true if users and roles should be automatically added
	 * on get operations
	 */
	public RBACCoreImpl(int activeSessionStrategy, boolean autoAddOnGet) {
		this.activeSessionStrategy = activeSessionStrategy;
		this.autoAddOnGet = autoAddOnGet;
	}
	
	// For each method below, we first update the collective data structure,
	//		then we update each individual class
    // p11, the page number in the standard where the Z spec is given
	public User addUser(Name newUser) throws UserExistsException {
		User	u = createAUser(newUser);
		if (allUsers.contains(u))
			throw new UserExistsException(u);

		allUsers.add(u);
		nameToUser.put(newUser, u);
		
		// If the user does not exist in USERS, it should not interfere
		//   with SESSIONS. But we clean user_sessions anyway
		u.deleteSessions();
		u.clearSessions();
		
		return u;
	}

    // p12
	public void deleteUser(User userToDelete) throws UserDoesNotExistException {
	    if (!allUsers.contains(userToDelete)) 
	        throw new UserDoesNotExistException(userToDelete);
	    
	    userToDelete.deleteSessions();
	    userToDelete.clearSessions();
	    
	    userToDelete.deleteSelfFromRoles();
	    userToDelete.clearRoles();

	    allUsers.remove(userToDelete);
	    nameToUser.remove(userToDelete.getName());
	}
	
	public boolean userExists(Name name) {
		return nameToUser.containsKey(name);
	}
	
	public User getUser(Name name) throws UserDoesNotExistException {
	    User result = (User)nameToUser.get(name);	
	    if (result==null) {
	        if (!autoAddOnGet)
	            throw new UserDoesNotExistException(name);
	        else {
	            try {
                    result = addUser(name);
                }
                catch (UserExistsException e) {
                    // Impossible
                }
	        }
	    }    
	    return result;
	}
	
	public Set getUsers() {
	    return allUsers;
	}
	
    // p12
	public Role addRole(Name newRole) throws RoleExistsException {
	    Role	r = createARole(newRole);
	    if (allRoles.contains(r))
	        throw new RoleExistsException(r);
	    
	    allRoles.add(r);
	    nameToRole.put(newRole, r);

	    return r;
	}

	// p12
	public void deleteRole(Role roleToDelete) throws RoleDoesNotExistException {
	    if (!allRoles.contains(roleToDelete)) 
	        throw new RoleDoesNotExistException(roleToDelete);
	    
	    roleToDelete.deleteSessions();
	    roleToDelete.clearSessions();
	    
	    roleToDelete.deleteSelfFromUsers();
	    roleToDelete.clearUsers();
	    
	    roleToDelete.deleteSelfFromPermissions();
	    roleToDelete.clearPermissions();
	    
	    moreDeleteRole(roleToDelete);
	    
	    allRoles.remove(roleToDelete);
	    nameToRole.remove(roleToDelete.getName());
	}
	
	protected void moreDeleteRole(Role roleToDelete) {
	    if(roleToDelete != null);
	}
	
	public boolean roleExists(Name name) {
		return nameToRole.containsKey(name);
	}
	
	public Role getRole(Name name) throws RoleDoesNotExistException {
	    Role result = (Role)nameToRole.get(name);	
	    if (result==null) {
	        if (!autoAddOnGet)
	            throw new RoleDoesNotExistException(name);
	        else {
	            try {
                    result = addRole(name);
                }
                catch (RoleExistsException e) {
                    // Impossible
                }
	        }
	    }    
	    return result;
	}
	
	public Set getRoles() {
	    return allRoles;
	}
	
	// p12
	public void assignUser(User aUser, Role aRole)
				throws 	UserDoesNotExistException, RoleDoesNotExistException,
						UserAlreadyHasRoleException {
		if (!allUsers.contains(aUser))
			throw new UserDoesNotExistException(aUser);
		
		if (!allRoles.contains(aRole))
			throw new RoleDoesNotExistException(aRole);
		
		boolean	userHasRole = !aUser.addRole(aRole);
		boolean roleHasUser = !aRole.addUser(aUser); 
		if (userHasRole || roleHasUser)
			throw new UserAlreadyHasRoleException(aUser, aRole);
	}

	// p13 
	public void deassignUser(User aUser, Role aRole) 
	    		throws 	UserDoesNotExistException, RoleDoesNotExistException,
	    				UserDoesNotHaveRoleException {
		if (!allUsers.contains(aUser))
			throw new UserDoesNotExistException(aUser);
		
		if (!allRoles.contains(aRole))
			throw new RoleDoesNotExistException(aRole);

		aUser.deleteSessions(aRole);
		
		boolean	userHasRole = aUser.deleteRole(aRole);
		boolean roleHasUser = aRole.deleteUser(aUser); 
		if (!userHasRole || !roleHasUser)
			throw new UserDoesNotHaveRoleException(aUser, aRole);
	}

	public Permission createPermission(Operation anOperation, Object anObject) 
				throws PermissionAlreadyExistsException {
		Permission p = createAPermission(anOperation, anObject);
		if (allPermissions.contains(p)) 
		    throw new PermissionAlreadyExistsException(p);
		allPermissions.add(p);
		nameToPermission.put(p.getName(), p);
		return p;
	}
		
	// p13
	public void grantPermission(Operation anOperation, Object anObject, Role aRole) 
				throws PermissionDoesNotExistException, RoleDoesNotExistException {
		Permission p = createAPermission(anOperation, anObject);
		if (!allPermissions.contains(p))
		    throw new PermissionDoesNotExistException(p);
		
		if (!allRoles.contains(aRole)) 
			throw new RoleDoesNotExistException(aRole);
		
		doGrantPermission(p, aRole);
	}
	
	// p14
	public void grantPermission(Permission aPermission, Role aRole) 
				throws PermissionDoesNotExistException, RoleDoesNotExistException {
		if (!allPermissions.contains(aPermission)) 
		    throw new PermissionDoesNotExistException(aPermission);
		
		if (!allRoles.contains(aRole)) 
			throw new RoleDoesNotExistException(aRole);
		
		doGrantPermission(aPermission, aRole);
	}
	
	private void doGrantPermission(Permission aPermission, Role aRole) {
		aRole.addPermission(aPermission);
		aPermission.addRole(aRole);
	}
	
	
	// p14
	public void revokePermission(Operation anOperation, Object anObject, Role aRole) 
				throws 	PermissionDoesNotExistException, RoleDoesNotExistException,
						RoleDoesNotHavePermissionException {
		Name permissionName = new NameImpl(PermissionImpl.getPermissionName(
		        			anOperation, anObject));
		Permission p = (Permission)nameToPermission.get(permissionName);
		if (p == null) {
			throw new PermissionDoesNotExistException(p);
		}
		
		if (!allRoles.contains(aRole)) 
			throw new RoleDoesNotExistException(aRole);

		if (!doRevokePermission(p, aRole))
			throw new RoleDoesNotHavePermissionException(aRole, p);
	}

	public void revokePermission(Permission aPermission, Role aRole) 
				throws 	PermissionDoesNotExistException, RoleDoesNotExistException,
						RoleDoesNotHavePermissionException {
		if (!allPermissions.contains(aPermission)) {
			throw new PermissionDoesNotExistException(aPermission);
		}
		
		if (!allRoles.contains(aRole)) 
			throw new RoleDoesNotExistException(aRole);

		if (!doRevokePermission(aPermission, aRole))
			throw new RoleDoesNotHavePermissionException(aRole, aPermission);
	}
	
	/**
	 * do revoking a permission from a role
	 * 
	 * @param aPermission	the permission to revoke
	 * @param aRole			the role from whom to revoke
	 * @return true if the role has the permission (which has just been revoked),
	 * false if the role does not have the permission
	 */
	private boolean doRevokePermission(Permission aPermission, Role aRole) {
		boolean	roleHasPermission = aRole.deletePermission(aPermission);
		boolean	permissionHasRole = aPermission.deleteRole(aRole);
		return roleHasPermission && permissionHasRole;
	}
	
	public boolean permissionExists(Name name) {
		return nameToPermission.containsKey(name);
	}
	
	public Permission			getPermission(Operation anOperation, Object anObject) 
		throws PermissionDoesNotExistException {
		Name name = new NameImpl(PermissionImpl.getPermissionName(
    			anOperation, anObject));
	    Permission result = (Permission)nameToPermission.get(name);	
	    if (result==null) {
	        if (!autoAddOnGet)
	            throw new PermissionDoesNotExistException(name);
	        else {
	            try {
                    result = createPermission(anOperation, anObject);
                }
                catch (PermissionAlreadyExistsException e) {
                    // Impossible
                }
	        }
	    }    
	    return result;
	    
	}
	
	public Set getPermissions() {
	    return allPermissions;
	}
	
	// p14 (core), p19 (hierarchical)
	public Session createSession(User aUser, Set ars) throws UserDoesNotExistException {
	    if (!allUsers.contains(aUser))
	        throw new UserDoesNotExistException(aUser);
	    
	    Session s = createASession();
	    allSessions.add(s);
	    nameToSession.put(s.getName(), s);

	    aUser.addSession(s);
	    s.addUser(aUser);

	    // This handles both core and hierarchical cases
	    //Set	rolesForUser = aUser.getAuthorizedRoles();
	    //The above statement would activate all roles
	    if (ars != null) {
		    Set	rolesForUser = ars;
		    s.addRoles(rolesForUser);
		    for (Iterator i = rolesForUser.iterator(); i.hasNext(); ) {
		        Role  r = (Role)i.next();
		        r.addSession(s);
		    }
	    }
	    
		return s;
	}

	// p14
	public void deleteSession(User aUser, Session aSession) 
				throws 	UserDoesNotExistException, SessionDoesNotExistException, 
						UserDoesNotHaveSessionException {
		if (!allUsers.contains(aUser))
		    throw new UserDoesNotExistException(aUser);
		
		if (!allSessions.contains(aSession)) 
		    throw new SessionDoesNotExistException(aSession);
		
		if (!aUser.hasSession(aSession))
		    throw new UserDoesNotHaveSessionException(aUser, aSession);
		
		aUser.deleteSession(aSession);
		aSession.deleteUser(aUser);
		
		aSession.deleteSelfFromRoles();
		aSession.clearRoles();
		
		nameToSession.remove(aSession.getName());
		allSessions.remove(aSession);
	}

	public boolean sessionExists(Name name) {
		return nameToSession.containsKey(name);
	}
	
	public Session getSession(Name name) throws SessionDoesNotExistException {
	    Session result = (Session)nameToSession.get(name);	
	    if (result==null) {
	        // do no autoAddOnGet
	        throw new SessionDoesNotExistException(name);
	    }    
	    return result;
	}
	
	public Set getSessions() {
	    return allSessions;
	}
	
	// p15 (core), p19 (hierarchical)
	public void addActiveRole(User aUser, Session aSession, Role aRole) 
				throws  UserDoesNotExistException, RoleDoesNotExistException,
						SessionDoesNotExistException, UserDoesNotHaveRoleException,
						UserDoesNotHaveSessionException {
		if (!allUsers.contains(aUser))
		    throw new UserDoesNotExistException(aUser);
		
		if (!allRoles.contains(aRole))
		    throw new RoleDoesNotExistException(aRole);
		
		if (!allSessions.contains(aSession)) 
		    throw new SessionDoesNotExistException(aSession);
		
		if (!aUser.hasSession(aSession))
		    throw new UserDoesNotHaveSessionException(aUser, aSession);
		
		if (!aUser.isAuthorized(aRole))
		    throw new UserDoesNotHaveRoleException(aUser, aRole);
		
		aSession.addRole(aRole);
		aRole.addSession(aSession);
	}

	// p15
	public void dropActiveRole(User aUser, Session aSession, Role aRole) 
				throws  UserDoesNotExistException, RoleDoesNotExistException,
						SessionDoesNotExistException, UserDoesNotHaveRoleException,
						UserDoesNotHaveSessionException, SessionDoesNotHaveRoleException {
		if (!allUsers.contains(aUser))
		    throw new UserDoesNotExistException(aUser);
		
		if (!allRoles.contains(aRole))
		    throw new RoleDoesNotExistException(aRole);
		
		if (!allSessions.contains(aSession)) 
		    throw new SessionDoesNotExistException(aSession);
		
		// This condition is actually missing from the standard
		if (!aUser.isAuthorized(aRole))
		    throw new UserDoesNotHaveRoleException(aUser, aRole);
		
		if (!aUser.hasSession(aSession))
		    throw new UserDoesNotHaveSessionException(aUser, aSession);

		if (!aSession.isAuthorized(aRole))
		    throw new SessionDoesNotHaveRoleException(aSession, aRole);
		
		aSession.deleteRole(aRole);
		aRole.deleteSession(aSession);
	}

	// p15
	public boolean checkAccess(Session aSession, Operation anOperation, Object anObject) 
				throws SessionDoesNotExistException {
	    if (!allSessions.contains(aSession))
	        throw new SessionDoesNotExistException(aSession);
	    
	    Permission	p = createAPermission(anOperation, anObject);
	    if(aSession.getPermissions().contains(p))
	        return true;
	    else
	        return false;
	}

	public boolean checkAccess(User aUser, Operation anOperation, Object anObject) 
				throws UserDoesNotExistException {
		if (!allUsers.contains(aUser))
		    throw new UserDoesNotExistException(aUser);
		
		Permission	p = createAPermission(anOperation, anObject);
		if(aUser.getPermissions().contains(p))
		    return true;
		else
		    return false;
	}

	public boolean checkAccess(Role aRole, Operation anOperation, Object anObject) 
				throws RoleDoesNotExistException {
		if (!allRoles.contains(aRole))
		    throw new RoleDoesNotExistException(aRole);
		
		Permission	p = createAPermission(anOperation, anObject);
		if(aRole.getPermissions().contains(p))
		    return true;
		else
		    return false;
	}

	// p16
	public Set assignedUsers(Role aRole) throws RoleDoesNotExistException {
	    if (!allRoles.contains(aRole))
	        throw new RoleDoesNotExistException(aRole);
	    
		return aRole.getUsers();
	}

	// p16
	public Set assingedRoles(User aUser) throws UserDoesNotExistException {
	    if (!allUsers.contains(aUser))
	        throw new UserDoesNotExistException(aUser);
	    
		return aUser.getRoles();
	}

	// p16 (core), p20 (hierarichical)
	public Set rolePermissions(Role aRole) throws RoleDoesNotExistException {
	    if (!allRoles.contains(aRole))
	        throw new RoleDoesNotExistException(aRole);
	    
		return aRole.getAuthorizedPermissions();
	}

	// p16 (core), p20 (hierarchical)
	public Set userPermissions(User aUser) throws UserDoesNotExistException {
	    if (!allUsers.contains(aUser))
	        throw new UserDoesNotExistException(aUser);
	    
		return aUser.getPermissions();
	}

	// p16
	public Set sessionRoles(Session aSession) throws SessionDoesNotExistException {
	    if (!allSessions.contains(aSession))
	        throw new SessionDoesNotExistException(aSession);
	    
	    // The spec does not suggest that is would be different for hierarchical RBAC
	    // Which might be inconsistent
		return aSession.getAuthorizedRoles();
	}
	
	// p17
	public Set sessionPermissions(Session aSession) throws SessionDoesNotExistException {
	    if (!allSessions.contains(aSession))
	        throw new SessionDoesNotExistException(aSession);
	    
	    // The spec does not suggest that is would be different for hierarchical RBAC
	    // Which might be inconsistent
		return aSession.getPermissions();
	}

	// p17 (core), p21 (hierarchical)
	public Set roleOperationsOnObject(Role aRole, Object anObject) 
				throws RoleDoesNotExistException {
	    if (!allRoles.contains(aRole))
	        throw new RoleDoesNotExistException(aRole);

	    return getOperationsForObject(aRole.getAuthorizedPermissions(), anObject);
	}

	private Set getOperationsForObject(Set permissions, Object anObject) {
	    Set	operations = new HashSet();
	    for (Iterator i = permissions.iterator(); i.hasNext();) {
	        Permission p = (Permission)i.next();
	        if (p.getObject().equals(anObject)) {
	            operations.add(p.getOperation());
	        }
	    }
	    return operations;
	}

	// p17 (core), p21 (hierarchical)
	public Set userOperationsOnObject(User aUser, Object anObject) 
				throws UserDoesNotExistException {
	    if (!allUsers.contains(aUser))
	        throw new UserDoesNotExistException(aUser);
	    
	    Set permissions = aUser.getPermissions();
	    return getOperationsForObject(permissions, anObject);
	}

	// p20 
	public Set authorizedUsers(Role aRole) throws RoleDoesNotExistException {
	    if (!allRoles.contains(aRole))
	        throw new RoleDoesNotExistException(aRole);
	    
		return aRole.getAuthorizedUsers();
	}

	// p20
	public Set authorizedRoles(User aUser) throws UserDoesNotExistException {
	    if (!allUsers.contains(aUser))
	        throw new UserDoesNotExistException(aUser);
	    
		return aUser.getAuthorizedRoles();
	}

	public int getActiveSessionStrategy() {
	    return activeSessionStrategy;
	}

	public void terminateSession(Session s) {
		allSessions.remove(s);
		nameToSession.remove(s.getName());
	}
	
	// Factory methods to provide indirection
	public User	createAUser(Name userName) {
	    return new UserImpl(userName);
	}
	
	public Role createARole(Name roleName) {
	    return new RoleImpl(roleName);
	}
	
	public Session createASession() {
	    return new SessionImpl(this);
	}
	
	public Permission createAPermission(Operation op, Object o) {
	    return new PermissionImpl(op, o);
	}
	
	public static void main(String[] args) {
		try {
			RBACCore	rbac = new RBACCoreImpl(RBACCore.SESSION_CONTINUE_DOWNGRADE);
			Name	nameJie = new NameImpl("jie");
			User 	userJie = rbac.addUser(nameJie);
			User	userJoe = rbac.addUser(new NameImpl("joe"));
			rbac.deleteUser(userJie);
			rbac.addUser(nameJie);
			Name	nameGrad = new NameImpl("grad");
			Role	roleGrad = rbac.addRole(nameGrad);
			Role	roleCandidate = rbac.addRole(new NameImpl("candidate"));
			rbac.assignUser(userJie, roleGrad);
			rbac.assignUser(userJie, roleCandidate);
			rbac.assignUser(userJoe, roleGrad);
			rbac.deassignUser(userJie, roleGrad);
			Set		rolesOfJie = rbac.assingedRoles(userJie);
			for (Iterator i = rolesOfJie.iterator(); i.hasNext(); ) {
				Role	r = (Role)i.next();
				System.out.println(r.toString());
			}
			rbac.assignUser(userJie, roleGrad);
			rolesOfJie = rbac.assingedRoles(userJie);
			for (Iterator i = rolesOfJie.iterator(); i.hasNext(); ) {
				Role	r = (Role)i.next();
				System.out.println(r.toString());
			}
			Set		usersOfCandidate = rbac.assignedUsers(roleCandidate);
			for (Iterator i = usersOfCandidate.iterator(); i.hasNext(); ) {
				User	u = (User)i.next();
				System.out.println(u.toString());
			}
			
			Name nameFile = new NameImpl("file");
			Object obFile = new ObjectImpl(nameFile);
			Name nameOpen = new NameImpl("open");
			Operation opOpen = new OperationImpl(nameOpen);
			Operation opClose = new OperationImpl(new NameImpl("close"));
			Permission openFile = rbac.createPermission(opOpen, obFile);
			Permission closeFile = rbac.createPermission(opClose, obFile); 
			rbac.grantPermission(openFile, roleGrad);
			rbac.grantPermission(openFile, roleCandidate);
			rbac.revokePermission(openFile, roleGrad);
			rbac.grantPermission(closeFile, roleCandidate);
			Set		permissionsOfGrad = rbac.rolePermissions(roleGrad);
			for (Iterator i = permissionsOfGrad.iterator(); i.hasNext(); ) {
				Permission	p = (Permission)i.next();
				System.out.println(p.toString());
			}
			Set		permissionsOfCandiate = rbac.rolePermissions(roleCandidate);
			for (Iterator i = permissionsOfCandiate.iterator(); i.hasNext(); ) {
				Permission	p = (Permission)i.next();
				System.out.println(p.toString());
			}
			Set		permissionsOfJoe = rbac.userPermissions(userJoe);
			for (Iterator i = permissionsOfJoe.iterator(); i.hasNext(); ) {
				Permission	p = (Permission)i.next();
				System.out.println(p.toString());
			}
			Set 	fileOperations = rbac.roleOperationsOnObject(roleCandidate, obFile);
			for (Iterator i = fileOperations.iterator(); i.hasNext(); ) {
				Operation	op = (Operation)i.next();
				System.out.println(op.toString());
			}
			fileOperations = rbac.userOperationsOnObject(userJoe, obFile);
			for (Iterator i = fileOperations.iterator(); i.hasNext(); ) {
				Operation	op = (Operation)i.next();
				System.out.println(op.toString());
			}
			
			Set		initialRoles = new HashSet();
			initialRoles.add(roleCandidate);
			Session		s = rbac.createSession(userJie, initialRoles);
			Role		roleMaster = rbac.addRole(new NameImpl("master"));
			rbac.assignUser(userJie, roleMaster);
			rbac.addActiveRole(userJie, s, roleMaster);
			Set rolesOfS = rbac.sessionRoles(s);
			for (Iterator i = rolesOfS.iterator(); i.hasNext(); ) {
				Role	r = (Role)i.next();
				System.out.println(r.toString());
			}
			rbac.dropActiveRole(userJie, s, roleMaster);
			rolesOfS = rbac.sessionRoles(s);
			for (Iterator i = rolesOfS.iterator(); i.hasNext(); ) {
				Role	r = (Role)i.next();
				System.out.println(r.toString());
			}
			Set permissionsOfS = rbac.sessionPermissions(s);
			for (Iterator i = permissionsOfS.iterator(); i.hasNext(); ) {
				Permission	p = (Permission)i.next();
				System.out.println(p.toString());
			}
			System.out.println(rbac.checkAccess(s, opOpen, obFile));
			rbac.deleteSession(userJie, s);
			rbac.deleteRole(roleGrad);
			rbac.terminateSession(s);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
