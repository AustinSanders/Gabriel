/*
 * Created on Aug 24, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class implements the RoleReceiver interface, 
 * providing commonality of User and Session  
 * 
 * @author Jie Ren
 */
abstract public class RoleReceiverImpl extends NamedEntityImpl implements RoleReceiver {
	//
	// Object methods
	//
	/**
	 * Create a role receiver
	 * 
	 * @param name the name of the use
	 */
	public RoleReceiverImpl(String name) {
		super(name);
	}
	
	/**
	 * Create a role receiver
	 * 
	 * @param name the name of the user or the session
	 */
	public RoleReceiverImpl(Name name) {
		this.theName = name;
	}
	
	/**
	 * Create a role receiver with a random name
	 */
	public RoleReceiverImpl() {
		super();
	}
	
	public boolean equals(java.lang.Object other) {
	    if (other == null)
	        return false;
	    if (getClass() != other.getClass())
	        return false;
		RoleReceiverImpl otherUser = (RoleReceiverImpl)other;
		if (this.theName.equals(otherUser.theName))
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
	// Role methods
	//
	Set		roles = new HashSet();

	public boolean addRole(Role aRole) {
		return roles.add(aRole);
	}
	
	public boolean addRoles(Set roles) {
	    return this.roles.addAll(roles);
	}
	
	public boolean hasRole(Role aRole) {
	    return roles.contains(aRole);
	}
	
	public Set getRoles() {
	    return roles;
	}
	
	abstract public boolean isAuthorized(Role aRole);
	
	public Set getAuthorizedRoles() {
		Set		authorizedRoles = new HashSet();
		for (Iterator i = roles.iterator(); i.hasNext(); ) {
			authorizedRoles.addAll(((Role)i.next()).getAuthorizedRoles());
		}
		return  authorizedRoles;
	}
	
	public boolean deleteRole(Role aRole) {
		return roles.remove(aRole);
	}
	
	public boolean deleteRoles(Set roles) {
	    return this.roles.removeAll(roles);
	}
	
	public abstract void deleteSelfFromRoles();
	
	public void clearRoles() {
		roles.clear();
	}

	public Set getPermissions() {
	    Set permissions = new HashSet();
	    for (Iterator i = getAuthorizedRoles().iterator(); i.hasNext();) {
	        Role r = (Role)i.next();
	        permissions.addAll(r.getAuthorizedPermissions());
	    }
	    return permissions;
	}
}
