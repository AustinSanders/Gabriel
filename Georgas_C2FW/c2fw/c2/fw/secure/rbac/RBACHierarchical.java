/*
 * Created on Aug 24, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.Set;

/**
 * This interface defines new methods for the Hierarchical RBAC component. Some 
 * methods of the Core RBAC component are redefined in the implementation.  
 * 
 * @author Jie Ren
 */
public interface RBACHierarchical extends RBACCore {
	// Maintain RH
	/**
	 * Add an inheritance relationship.
	 * 
	 * For limited hierarchical RBACCore, this method would limit to one ascendant role
	 *
	 * @param ascendant		the inheriting role with more permissons
	 * @param descendant 	the inherited role with less permissions
	 * @throws RoleDoesNotExistException
	 * @throws RoleCycleException
	 * @throws RoleMultipleInheritanceException
	 */
	void			addInheritance(RoleHierarchical ascendant, RoleHierarchical descendant) 
					throws 	RoleDoesNotExistException, RoleCycleException, 
							RoleMultipleInheritanceException;
	/**
	 * Delete an inheritance relationship
	 * 
	 * Like deassignUser, deleteInheritance can either delete just directly 
	 * inherited roles, or delete all indirectly inherited roles.
	 *
	 * @param ascendant		the inheriting role with more permissons
	 * @param descendant 	the inherited role with less permissions
	 * @throws RoleDoesNotExistException
	 * @throws RoleInheritanceDoesNotExistException
	 */
	void			deleteInheritance(RoleHierarchical ascendant, RoleHierarchical descendant)
					throws RoleDoesNotExistException, RoleInheritanceDoesNotExistException;
	/**
	 * Add an asendant for a role.
	 * 
	 * @param ascendantName the name of the ascendant
	 * @param descendant	the inherited role
	 * @return a newly created inheriting role
	 * @throws RoleExistsException
	 * @throws RoleDoesNotExistException
	 * @throws RoleCycleException
	 * @throws RoleMultipleInheritanceException
	 */
	RoleHierarchical	addAscendant(Name ascendantName, RoleHierarchical descendant)
					throws 	RoleExistsException, RoleDoesNotExistException, 
							RoleCycleException, RoleMultipleInheritanceException;
	/**
	 * Add a descendant for a role
	 * 
	 * @param descendantName the name of the descendant
	 * @param ascendant		the inheriting role
	 * @return a newly created inherited role
	 * @throws RoleExistsException
	 * @throws RoleDoesNotExistException
	 * @throws RoleCycleException
	 * @throws RoleMultipleInheritanceException
	 */
	RoleHierarchical	addDescendant(Name descendantName, RoleHierarchical ascendant)
					throws 	RoleExistsException, RoleDoesNotExistException, 
							RoleCycleException, RoleMultipleInheritanceException;
	
	/**
	 * Get the users authorized to a given role.
	 * 
	 * With hierarchical direct or indirect inheritance
	 * 
	 * @param aRole			the role to query users
	 * @return the set of users authorized to the given role
	 * @throws RoleDoesNotExistException
	 */
	Set				authorizedUsers(Role aRole) throws RoleDoesNotExistException;
	/**
	 * Get the roles authorized to a given user.
	 * 
	 * With hierarchical direct or indirect inheritance
	 * 
	 * @param aUser			the user to query roles
	 * @return the set of roles authorized to the given user
	 * @throws UserDoesNotExistException
	 */
	Set				authorizedRoles(User aUser) throws UserDoesNotExistException;

	/*
	 * These functions are not defined in the standard. 
	 * They control whether the hierarchical RBACCore is general (allowing 
	 * multiple inheritance) or limited (allowing only single inheritance),
	 * and whether propagating through indirect inheritance is allowed  
	 */
	/**
	 * Get whether the current hierarchical RBACCore is general or limited.
	 * 
	 * @return true if the Hierarchical RBACCore is general, false if it is limited.
	 */
	boolean			isGeneral();
	
	/**
	 * Get the set of junior roles for a senior role
	 * 
	 * @param senior	a senior role
	 * @return a set of roles that the input role inherits from
	 */
	Set				getJuniors(RoleHierarchical senior);

	/**
	 * Get the set of senior roles for a junior role
	 * 
	 * @param junior	a senior role
	 * @return a set of roles, each of which inherits from the input role
	 */
	Set				getSeniors(RoleHierarchical junior);
}
