/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.Set;

/**
 * This interface designates a user.
 *
 * User only maintains direct user-role assignment (UA). When queried for roles 
 * and permissions, it relies on roles, possibly through inheritances, to 
 * supply the information.   
 * 
 * @author Jie Ren
 */
public interface User extends RoleReceiver {
	/**
	 * Add a session to the user
	 * 
	 * @param aSession the session to add
	 */
	void		addSession(Session aSession);
	/**
	 * Get whether the user has the session
	 * 
	 * @param aSession the session to check
	 * @return true if the user has the session, false if the user does not 
	 * have the session
	 */
	boolean		hasSession(Session aSession);
	/**
	 * Get the set of active sessions the users participates
	 * 
	 * @return the set of active sessions 
	 */
	Set			getSessions();
	
	/**
	 * Delete a session from the user
	 * 
	 * @param aSession the session to delete
	 * @return true if the user has the session, false if the user does not 
	 * have the session
	 */
	boolean		deleteSession(Session aSession);

	/**
	 * Delete sessions that the user participates
	 */
	void		deleteSessions();
	
	/**
	 * Delete sessions that the user participates and has a role
	 * 
	 * @param aRole		aRole to filter the sessions
	 */
	void		deleteSessions(Role aRole);
	
	/**
	 * Clear sessions that the user participates
	 */
	void 		clearSessions();
}
