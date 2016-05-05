/*
 * Created on Jul 14, 2005
 *
 */
package c2.fw.secure;

public class SimpleRule implements IRule {
	protected	String		subject;
	protected	String		object;
	protected	String		action;
	protected	boolean		allowed;
	
	/**
	 * Create a simple rule
	 * @param subject	the subject
	 * @param object	the object
	 * @param action	the action that the subject wants to perform on the object
	 * @param allowed	whether the action is allowed
	 */
	public SimpleRule(String subject, String object, String action, boolean allowed) {
		this.subject = subject;
		this.object = object;
		this.action = action;
		this.allowed = allowed;
	}
	
	public String getSubject() {
		return subject;
	}

	public String getObject() {
		return object;
	}

	public String getAction() {
		return action;
	}

	public boolean isAllowed() {
		return allowed;
	}
}
