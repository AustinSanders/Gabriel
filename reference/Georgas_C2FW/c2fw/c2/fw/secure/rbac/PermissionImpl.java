/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.HashSet;
import java.util.Set;

public class PermissionImpl extends NamedEntityImpl implements Permission {
	Operation		op;
	Object			o;
	
	/**
	 * Create a permission
	 * 
	 * @param operationName		the name of the operation of the permission
	 * @param objectName		the name of the object of the permission
	 */
	public PermissionImpl(String operationName, String objectName) {
		super(getPermissionName(operationName, objectName));
		op = new OperationImpl(operationName);
		o = new ObjectImpl(objectName);
	}

	/**
	 * Create a permission
	 * 
	 * @param anOperation		the operation of the permission
	 * @param anObject			the object of the permission
	 */
	public PermissionImpl(Operation anOperation, Object anObject) {
		super(getPermissionName(anOperation, anObject));
		op = anOperation;
		o = anObject;
	}

	/**
	 * Get a permission name from an operation name and an object name
	 *   
	 * @param operationName		the name of the operation of the permission
	 * @param objectName		the name of the object of the permission
	 * @return the permission name
     */	
	public static String getPermissionName(String operationName, String objectName) {
		return operationName + "/" + objectName; 
	}
	
	/**
	 * Get a permission name from an operation and an object
	 *   
	 * @param anOperation		the operation of the permission
	 * @param anObject			the object of the permission
	 * @return the permission name
     */	
	public static String getPermissionName(Operation anOperation, Object anObject) {
		return getPermissionName(anOperation.getNameString(), anObject.getNameString()); 
	}
	
	public void setOperation(Operation op) {
		this.op = op;
	}

	public void setObject(Object o) {
		this.o = o;
	}

	public Operation getOperation() {
		return op;
	}

	public Object getObject() {
		return o;
	}
	
	Set		roles = new HashSet();
	
	public void addRole(Role aRole) {
		roles.add(aRole);
	}
	
	public boolean deleteRole(Role aRole) {
		return roles.remove(aRole);
	}
}
