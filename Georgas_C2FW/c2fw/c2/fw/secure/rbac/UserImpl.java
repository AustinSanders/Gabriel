/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UserImpl extends RoleReceiverImpl implements User {
	//
	// Object methods
	//
	/**
	 * Create a user
	 * 
	 * @param name the name of the use
	 */
	public UserImpl(String name) {
		super(name);
	}
	
	/**
	 * Create a user
	 * 
	 * @param name the name of the user
	 */
	public UserImpl(Name name) {
		super(name);
	}
	
	public boolean equals(java.lang.Object other) {
	    if (other == null)
	        return false;
	    if (getClass() != other.getClass())
	        return false;
		UserImpl otherUser = (UserImpl)other;
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
	public boolean isAuthorized(Role aRole) {
		Set		authorizedUsers = aRole.getAuthorizedUsers();
		return authorizedUsers.contains(this);
	}
	
	public void deleteSelfFromRoles() {
		for (Iterator i = roles.iterator(); i.hasNext(); ) {
			Role	r = (Role)i.next();
			r.deleteUser(this);
		}
	}
	
	//
	// Session methods
	//
	Set		sessions = new HashSet();

	public void addSession(Session aSession) {
	    sessions.add(aSession);
	}
	
	public boolean hasSession(Session aSession) {
	    return sessions.contains(aSession);
	}
	
	public Set getSessions() {
		return sessions;
	}
	
	public boolean deleteSession(Session aSession) {
	    return sessions.remove(aSession);
	}
	
	public void deleteSessions() {
		// The spec says the sessions of the user can be terminated abruptly 
		// 		or naturally (i.e. let it end by itself)
		//		Still, this is just a structure decision at this level. How the 
		//		session terminates should be integrated with the OS infrastructure
		for (Iterator i = sessions.iterator(); i.hasNext(); ) {
			Session	s = (Session)i.next();
			s.delete(this);
		}
	}

	public void deleteSessions(Role aRole) {
		for (Iterator i = sessions.iterator(); i.hasNext(); ) {
			Session	s = (Session)i.next();
			s.delete(this, aRole);
		}
	}
	
	public void clearSessions() {
		sessions.clear();
	}
}
