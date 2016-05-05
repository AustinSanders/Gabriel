/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SessionImpl extends RoleReceiverImpl implements Session {
	RBACCore	rbac;
	
	/**
	 * Create a session, using a generated name
	 * 
	 * @param rbac	the RBACCore to which the session belongs
	 */
	public 			SessionImpl(RBACCore rbac) {
		super();
		this.rbac = rbac;
	}
	
	/**
	 * Create a session, with a given name
	 * 
	 * @param name	the name of the session
	 * @param rbac	the RBACCore to which the session belongs
	 */
	public 			SessionImpl(String name, RBACCore rbac) {
		super(name);
		this.rbac = rbac;
	}
	
	public void 	terminate() {
		clearUsers();
		clearRoles();
		rbac.terminateSession(this);
	}
	
    // The spec lists three options (there could be more): 
    //		1) let the session continue (and end normally),
    // 		2) terminate the session, or 
    //		3) let the session continue but strip it off the role
	public void 	delete() {
		switch (rbac.getActiveSessionStrategy()) {
	        case RBACCore.SESSION_TERMINATE:
	            terminate();
	            break;
	        
	        case RBACCore.SESSION_CONTINUE_NORMALLY:
	            break;
	        
	        case RBACCore.SESSION_CONTINUE_DOWNGRADE: 
	        default:
	        	// for this case we do not know what to do
				break;
		}
	}
	
	public void		delete(User aUser) {
		switch (rbac.getActiveSessionStrategy()) {
	        case RBACCore.SESSION_TERMINATE:
	            terminate();
	            break;
	        
	        case RBACCore.SESSION_CONTINUE_NORMALLY:
	            break;
	        
	        case RBACCore.SESSION_CONTINUE_DOWNGRADE: 
	        default:
				// remove the user from all participating sessions
	            // This option is actually not mentioned in the spec
				deleteUser(aUser);
				break;
		}
	}
	
	public void		delete(Role aRole) {
		switch (rbac.getActiveSessionStrategy()) {
	        case RBACCore.SESSION_TERMINATE:
	            terminate();
	            break;
	        
	        case RBACCore.SESSION_CONTINUE_NORMALLY:
	            break;
	        
	        case RBACCore.SESSION_CONTINUE_DOWNGRADE: 
	        default:
				deleteRole(aRole);
				break;
		}
	}
	
	public void		delete(User aUser, Role aRole) {
		if (hasUser(aUser) && isAuthorized(aRole)) {
			switch (rbac.getActiveSessionStrategy()) {
		        case RBACCore.SESSION_TERMINATE:
		            terminate();
		            break;
		        
		        case RBACCore.SESSION_CONTINUE_NORMALLY:
		            break;
		        
		        case RBACCore.SESSION_CONTINUE_DOWNGRADE: 
		        default:
					deleteUser(aUser);
					deleteRole(aRole);
					break;
			}
		}
	}
	
	//
	// Role methods
	//
	public void deleteSelfFromRoles() {
		Set		rolesForSession = getAuthorizedRoles();
	    for (Iterator i = rolesForSession.iterator(); i.hasNext(); ) {
	        Role  r = (Role)i.next();
	        r.deleteSession(this);
	    }
	}
	
	public boolean isAuthorized(Role aRole) {
		Set		rolesForSession = getAuthorizedRoles();
		return rolesForSession.contains(aRole);
	}
	
	//
	// User methods
	//
	Set		users = new HashSet();
	
	public void addUser(User aUser) {
	    users.add(aUser);
	}
	
	public boolean 	hasUser(User aUser) {
		return users.contains(aUser);
	}
	
	public boolean	deleteUser(User aUser) {
		return users.remove(aUser);
	}
	
	public void clearUsers() {
		users.clear();
	}
}
