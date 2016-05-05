/*
 * Created on Jul 18, 2005
 *
 */
package c2.fw.secure;

import c2.fw.Weld;

/**
 * A class that signifies a failure of adding or removing a weld that is caused by 
 * security reasons.  
 * 
 * @author Jie Ren
 */
public class WeldSecurityException extends SecurityException  {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	protected			String			action = "";
	protected			Weld 			w;

	/**
	 * Creates a new
	 * <code>WeldSecurityException<code> 
	 */
	public WeldSecurityException() {
		super("");
	}

	/**
	 * Creates a new
	 * <code>WeldSecurityException<code> with id and action
	 * 
	 * @param w the weld that cannot be created or removed because of security reasons
	 * @param action the action to perform, either AddWeld or RemoveWeld
	 */
	public WeldSecurityException(Weld w, String action) {
		super(w + action);
		this.w = w;
		this.action = action;
	}

	/**
	 * Gets a string representation of this exception.
	 * 
	 * @return String representation of this exception.
	 */
	public String toString(){
		return "Failure in " + action + " " + w;
	}
	
	/**
	 * Get the weld
	 * 
	 * @return the weld
	 */
	public Weld getWeld() {
		return w;
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
