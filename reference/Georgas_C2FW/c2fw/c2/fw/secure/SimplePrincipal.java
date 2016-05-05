/*
 * Created on Jul 11, 2005
 *
 */
package c2.fw.secure;

/**
 * A simple principal that has just a name
 *  
 * @author Jie Ren
 */
public class SimplePrincipal implements IPrincipal {
	
	protected	String	name;
	
	/**
	 * Create a principal based on a name
	 * @param name the name of the new principal
	 */
	public SimplePrincipal(String name) {
		this.name = name;
	}

	/**
	 * Get the name of the principal
	 * @return the name of the principal
	 */
	public String getName() {
		return name;
	}
}
