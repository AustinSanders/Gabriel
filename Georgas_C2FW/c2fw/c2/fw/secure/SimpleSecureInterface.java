/*
 * Created on Jul 10, 2005
 *
 */
package c2.fw.secure;

import java.util.HashSet;
import java.util.Set;

import c2.fw.Brick;
import c2.fw.Identifier;

/**
 * An interface whose access requires privileges
 * TODO: this should be an incoming interface, how about outgoing ones 
 * 
 * @author Jie Ren
 */
public class SimpleSecureInterface extends SimpleDirectionalInterface implements SecureInterface {
	/**
	 * Create a secure interface
	 * @param id	the identifier of the interface
	 * @param brick the brick to which this interface belongs
	 */
	public SimpleSecureInterface(Identifier id, Brick brick){
		super(id, brick);
	}

	/**
	 * The set of safeguards (required privileges) that an accessor should possess
	 */
	protected	Set safeguards = new HashSet();

	/**
	 * Add one safeguard (required privilege)
	 * @param safeguard the new privilege
	 */
	public void addSafeguard(IPrivilege safeguard) {
		safeguards.add(safeguard);
	}
	
	public Set getSafeguards() {
		return safeguards;
	}
	
	public String toString(){
		return "SecureInterface{id=\"" + getIdentifier() + 
		"; brick=\"" + getBrick() + "; safeguards=\"" + safeguards + "\"};";
	}
}
