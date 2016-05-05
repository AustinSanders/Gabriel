/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RoleImpl extends NamedEntityImpl implements Role {
	//
	// Object methods
	//
	/**
	 * Create a role
	 * 
	 * @param name the name of the role
	 */
	public RoleImpl(String name) {
		super(name);
	}
	
	/**
	 * Create a role
	 * 
	 * @param name the name of the role
	 */
	public RoleImpl(Name name) {
		this.theName = name;
	}
	
	public boolean equals(java.lang.Object other) {
	    if (other == null)
	        return false;
	    if (getClass() != other.getClass())
	        return false;
		RoleImpl otherRole = (RoleImpl)other;
		if (this.theName.equals(otherRole.theName))
			return true;
		else
			return false;
	}
	
	public int hashCode() {
		// overriding this method is necessary for set containment test
		return theName.hashCode();
	}
	
	public String toString() {
		return theName.toString();
	}
	
	//
	// User methods
	//
	Set		users = new HashSet();
	
	public boolean addUser(User aUser) {
		return users.add(aUser);
	}
	
	public Set getUsers() {
	    return users;
	}
	
	public Set getAuthorizedUsers() {
	    return users;
	}
	
	public boolean deleteUser(User aUser){
		return users.remove(aUser);
	}
	
	public void clearUsers() {
		users.clear();
	}

	public void deleteSelfFromUsers() {
		for (Iterator i = users.iterator(); i.hasNext(); ) {
			User	u = (User)i.next();
			u.deleteRole(this);
		}
	}
	
	//
	// Session methods
	//
	Set		sessions = new HashSet();
	
	public void addSession(Session aSession) {
	    sessions.add(aSession);
	}
	
	public boolean deleteSession(Session aSession) {
	    return sessions.remove(aSession);
	}
	
	public void deleteSessions() {
		for (Iterator i = sessions.iterator(); i.hasNext(); ) {
			Session	s = (Session)i.next();
			s.delete(this);
		}
	}
	
	public void clearSessions() {
		sessions.clear();
	}
	
	//
	// Permission methods
	//
	Set 	permissions = new HashSet();
	
	public void addPermission(Permission aPermission) {
		permissions.add(aPermission);
	}
	
	public boolean hasPermission(Permission aPermission) {
	    return permissions.contains(aPermission);
	}
	
	public Set getPermissions() {
	    return permissions;
	}
	
	public Set getAuthorizedPermissions() {
	    return permissions;
	}
	
	public boolean deletePermission(Permission aPermission) {
		return permissions.remove(aPermission);
	}
	
	public void deleteSelfFromPermissions() {
		for (Iterator i = permissions.iterator(); i.hasNext(); ) {
			Permission	p = (Permission)i.next();
			p.deleteRole(this);
		}
	}
	
	public void clearPermissions() {
		permissions.clear();
	}
	
	public Set getAuthorizedRoles() {
	    Set authorizedRoles = new HashSet();
	    authorizedRoles.add(this);
	    return authorizedRoles;
	}
}
