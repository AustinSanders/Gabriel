/*
 * Created on Jul 10, 2005
 *
 */
package c2.fw.secure;

/**
 * A privilege, which describes what action can be performed on an object.
 * 
 * The privilege combines an action and an object, which seems a rather common
 * combination of security literature. Also, our focus is less about object, 
 * or the object is implied.  
 * 
 * @author Jie Ren
 */
public interface IPrivilege {
	// for initialization parameter passing between SecureManagedSystem and SecureC2Brick
	public final static String	PRIVILEGE = "Privilege";
	public final static String	SAFEGUARD = "Safeguard";
	public final static String  ACTION = "Action";
	public final static String	OBJECT = "Object";
	public final static String	FOR_TYPE = "ForType";

	/**
	 * Get the action of this privilege
	 * 
	 * @return	a string representing the action
	 */
	public String getAction();
	
	/**
	 * Get the object of this privilege
	 * 
	 * @return a string representing the object. It would be an empty string
	 * if the privilege does not apply to a specific object. 
	 */
	public String getObject();
	
	/**
	 * Get the name of this privilege
	 * 
	 * @return the name that combines the action and the object
	 */
	public String getName();
}
