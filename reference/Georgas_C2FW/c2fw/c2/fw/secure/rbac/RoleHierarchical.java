/*
 * Created on Aug 24, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.Set;

/**
 * This designates a role that can participate in a role hierarchy
 * 
 * For role hierarchy, an asendant designates a role that inherits (and thus 
 * having more permissions), and a descendant designates a role that is being
 * inherited (and thus having fewer permissions). This is for immediate 
 * inheritance. The closure of immediate inheritance (that is, indirect 
 * inheritance), are calculated through existing juniors and seniors 
 * relationship.  
 *  
 * @author Jie Ren
 */
public interface RoleHierarchical extends Role {
	/**
	 * Add an ascendant, that is, an immediate role who inherits from this role
	 * (and has more permissions than this role)
	 * 
	 * @param ascendant		the ascendant
	 */
	void		addAscendant(RoleHierarchical ascendant);
	
	/**
	 * Add a descendant, that is, an immediate role who this role inherits from 
	 * (and has less permissions than this role)
	 * 
	 * @param descendant	the descendant
	 */
	void		addDescendant(RoleHierarchical descendant);

	/**
	 * Get whether this role is an asendant of the descendant role
	 * 
	 * A.isAscendantOf(B) |= A>>B
	 * 
	 * @param descendant the descendant role
	 * @return true if there is a direct inheritance from the descendant
	 * role to this role, false otherwise
	 */
	boolean		isAscendantOf(RoleHierarchical descendant);
	
	/**
	 * Get whether this role is a descendant of the ascendant role
	 * 
	 * @param ascendant the ascendant role
	 * @return true if there is a direct inheritance from this
	 * role to the ascendant role, false otherwise
	 */
	boolean		isDescendantOf(RoleHierarchical ascendant);
	
	/**
	 * Get all ascendants.
	 * 
	 * @return the set of ascendants
	 */
	Set			getAsendants();
	
	/**
	 * Get all descendants
	 * 
	 * @return the set of descendants
	 */
	Set			getDescendants();
	
	/**
	 * Delete an ascendant
	 * 
	 * @param ascendant the ascendant to delete
	 */
	void		deleteAscendant(RoleHierarchical ascendant);
	
	/**
	 * Delete a descendant
	 * 
	 * @param descendant the descendant to delete
	 */
	void		deleteDescendant(RoleHierarchical descendant);
	
	/**
	 * Add a junior role that this role inherits from .
	 * 
	 * @param junior an inherited role
	 */
	void		addJunior(RoleHierarchical junior);
	
	/**
	 * Add a set of junior roles that this role inherits from
	 * 
	 * @param juniors the set of inherited roles
	 * @return true if the set of juniors of this role has changed, false otherwise
	 */
	boolean		addJuniors(Set juniors);
	
	/**
	 * Add a senior role that inherits from this role.
	 * 
	 * @param senior an inheriting role
	 */
	void		addSenior(RoleHierarchical senior);
	
	/**
	 * Add a set of senior roles that inherit from this role
	 * 
	 * @param seniors the set of inheriting roles
	 * @return true if the set of seniors of this role has changed, false otherwise
	 */
	boolean		addSeniors(Set seniors);
	
	/**
	 * Get whether this role is a junior of the senior role
	 * 
	 * @param senior the senior role
	 * @return true if there is a (possibly indirect) inheritance from this
	 * role to the senior role, false otherwise
	 */
	boolean		isJuniorOf(RoleHierarchical senior);
	
	/**
	 * Get whether this role is a senior of the junior role
	 * 
	 * A.isSeniorOf(B) |= A>=B
	 * 
	 * @param junior the junior role
	 * @return true if there is a (possibly indirect) inheritance from the junior 
	 * role to this role, false otherwise
	 */
	boolean		isSeniorOf(RoleHierarchical junior);
	
	/**
	 * Get all junior roles that this role inherits from
	 * 
	 * @return the set of inherited roles.
	 */
	Set			getJuniors();
	
	/**
	 * Get all senior roles that inherit from this role
	 * 
	 * @return the set of inheriting roles
	 */
	Set			getSeniors();

	/**
	 * Delete a junior
	 * 
	 * @param junior a junior role to delete
	 */
	void		deleteJunior(RoleHierarchical junior);
	
	/**
	 * Delete a senior
	 * 
	 * @param senior a senior role to delete
	 */
	void		deleteSenior(RoleHierarchical senior);
	
	/**
	 * Clear the set of juniors
	 */
	void		clearJuniors();

	/**
	 * Clear the set of seniors
	 */
	void		clearSeniors();
	
	/**
	 * Delete the role from the hierarchy 
	 */
	void		deleteSelfFromHierarchy();
}
