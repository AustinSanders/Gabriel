/*
 * Created on Jul 18, 2005
 *
 */
package c2.fw.secure;

import c2.fw.NamedPropertyMessage;

/**
 * A security exception for failing to route message
 * 
 * @author Jie Ren
 */
public class RouteSecurityException extends SecurityException  {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	protected			String					action = "";
	protected			NamedPropertyMessage	npm;

	/**
	 * Creates a new
	 * <code>RouteSecurityException<code> 
	 */
	public RouteSecurityException() {
		super("");
	}

	/**
	 * Creates a new
	 * <code>RouteSecurityException<code> with message and action
	 * 
	 * @param npm the message that fails to route
	 * @param action the action to perform, should be Route
	 */
	public RouteSecurityException(NamedPropertyMessage npm, String action) {
		super(npm + action);
		this.npm = npm;
		this.action = action;
	}

	/**
	 * Gets a string representation of this exception.
	 * 
	 * @return String representation of this exception.
	 */
	public String toString(){
		return "Failure in " + action + " " + npm;
	}
	
	/**
	 * Get the named property message
	 * 
	 * @return the message
	 */
	public NamedPropertyMessage getRouteMessage() {
		return npm;
	}
	
	/**
	 * Get the action invovled for the exception
	 * 
	 * @return the action
	 */
	public String	getAction() {
	    return action;
	}
}
