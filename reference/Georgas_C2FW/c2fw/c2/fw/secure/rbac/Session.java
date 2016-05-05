/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;


/**
 * This interface designates a session
 * 
 * @author Jie Ren
 */
public interface Session extends RoleReceiver {
	/**
	 * Delete a session. Implement the DeleteSession function of the standard.
	 */
	void 		delete();
	/**
	 * Delete a session.
	 * 
	 * @param aUser	a user who should be stripped from the session if the 
	 * active session strategy allows the session to continue
	 */
	void		delete(User aUser);
	/**
	 * Delete a session.
	 * 
	 * @param aRole	a role who should be stripped from the session if the 
	 * active session strategy allows the session to continue
	 */
	void		delete(Role aRole);
	/**
	 * Delete a session.
	 * 
	 * @param aUser	a user 
	 * @param aRole a role who should be stripped from the session if the 
	 * active session strategy allows the session to continue
	 */
	void		delete(User aUser, Role aRole);

	/**
	 * Add a user, to the session.
	 * 
	 * @param aUser the user to add
	 */
	void		addUser(User aUser);
	/**
	 * Get whether the session has a user
	 * 
	 * @param aUser the user to check
	 * @return true if the session has the user
	 */
	boolean		hasUser(User aUser);
	/**
	 * Delete a user from this session
	 * 
	 * @param aUser the user to delete
	 * @param true if the user has the session, false if the user does not have 
	 * the session
	 */
	boolean 	deleteUser(User aUser);
	/**
	 * Clear users
	 */
	void		clearUsers();

	/**
	 * Terminate a session
	 */
	void		terminate();
}
