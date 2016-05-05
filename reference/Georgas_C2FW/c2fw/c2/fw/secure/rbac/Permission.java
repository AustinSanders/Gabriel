/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

/**
 * This interface designates a permission granted an operation on an object
 * 
 * @author Jie Ren
 */
public interface Permission extends NamedEntity {
	/**
	 * Set the operation that this permission grants
	 * 
	 * @param op the operation
	 */
	void		setOperation(Operation op);
	/**
	 * Set the object that this permission grants 
	 * 
	 * @param o the object
	 */
	void		setObject(Object o);
	/**
	 * Get the operation that this permision grants
	 * 
	 * @return the operation
	 */
	Operation	getOperation();
	/**
	 * Get the object that this permission grants
	 * 
	 * @return the object
	 */
	Object		getObject();
	/**
	 * Add a role to have this permission
	 * 
	 * @param aRole the role
	 */
	void		addRole(Role aRole);
	/**
	 * Delete a role who has this permission
	 * 
	 * @param aRole the role to delete
	 * @return true if the role has the permission, 
	 * false if the role does not have the permission 
	 */
	boolean		deleteRole(Role aRole);
}
