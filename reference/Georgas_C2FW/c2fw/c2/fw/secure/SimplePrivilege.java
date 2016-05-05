/*
 * Created on Jul 10, 2005
 *
 */
package c2.fw.secure;

/**
 * An implenetation of IPrivilege,  
 * 
 * @author Jie Ren
 */
public class SimplePrivilege implements IPrivilege {
	protected 	String 		action = "";
	protected	String		object = "";
	
	/**
	 * Create a privilege based on an action. The object part would be empty. 
	 * @param action a string representing an action
	 */
	public SimplePrivilege(String action) {
		this.action = action;
	}
	
	/**
	 * Create a privilege based on an action and an object
	 * @param action a string representing an action 
	 * @param object a string representing an object
	 */
	public SimplePrivilege(String action, String object) {
		this.action = action;
		this.object = object;
	}
	
	public String getAction() {
		return action;
	}

	public String getObject() {
		return object;
	}
	
	public String getName() {
	    return action + actionObjectSeparator + object;
	}
	
	public String toString() {
	    return getName();
	}
	
	private static String actionObjectSeparator = "->";
	/**
	 * Create a simple privilege based on a string
	 * @param privilege a string representing a privilege. If both the action
	 * and the object exist, they should be separated by "->", that is, the 
	 * privilege should look like "action->object" 
	 * @return
	 */
	public static IPrivilege createPrivilege(String privilege) {
		int		actionEnd = privilege.indexOf(actionObjectSeparator);
		if (actionEnd == -1) {
			// just action
			return (new SimplePrivilege(privilege)); 
		}
		else {
			return (new SimplePrivilege(privilege.substring(0, actionEnd),
					privilege.substring(actionEnd + actionObjectSeparator.length())));
		}
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof SimplePrivilege))
			return false;
		SimplePrivilege otherPrivilege = (SimplePrivilege)other;
		if (this.action.equals(otherPrivilege.action) &&
			this.object.equals(otherPrivilege.object))
			return true;
		else
			return false;
	}
	
	public int hashCode() {
		// overriding this method is necessary for set containment test
		if (object != null)
			return action.hashCode() + object.hashCode();
		else
			return action.hashCode();
	}
}
