/*
 * Created on Jul 3, 2005
 *
 */
package c2.fw.secure;

import c2.fw.Identifier;

/**
 * A class that signifies a failure of adding or removing a brick that is caused by 
 * security reasons.  
 * 
 * @author Jie Ren
 */
public class BrickSecurityException extends SecurityException {
	private static final long serialVersionUID = 1L;

	protected			String			action = "";
	protected			Identifier 		id;

	/**
	 * Creates a new
	 * <code>BrickSecurityException<code> 
	 */
	public BrickSecurityException() {
		super("");
	}

	/**
	 * Creates a new
	 * <code>BrickSecurityException<code> with id and action
	 * 
	 * @param id the identifier of the brick that cannot be created or removed because of security reasons
	 * @param action the action to perform, either AddBrick or RemoveBrick
	 */
	public BrickSecurityException(Identifier id, String action) {
		super(id + action);
		this.id = id;
		this.action = action;
	}

	/**
	 * Gets a string representation of this exception.
	 * 
	 * @return String representation of this exception.
	 */
	public String toString(){
		return "Failure in " + action + " " + id;
	}
	
	/**
	 * Get the identifier of the brick that could not be created or removed
	 * 
	 * @return the brick identifer
	 */
	public Identifier getBrickIdentifier() {
		return id;
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
