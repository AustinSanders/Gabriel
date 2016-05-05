/*
 * Created on Jul 15, 2005
 *
 */
package c2.fw.secure;

import java.util.Set;

/**
 * A secure interface is an incoming interface (or an incomingoutgoing interface)
 * whose access is protected by a set of safeguards. The safeguards are the privileges
 * that an accessing brick should possess before it can access the interface.  
 * 
 * @author Jie Ren
 */
public interface SecureInterface extends IncomingInterface {
	/**
	 * Get the set of safeguards that protect this interface.
	 *  
	 * @return the set of safe guards. Could be an empty set
	 */
	Set		getSafeguards();
}
