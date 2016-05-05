/*
 * Created on Jul 15, 2005
 *
 */
package c2.fw.secure;

public class SimplePolicy implements IPolicy {
	protected String	policy;
	
	public SimplePolicy(String policy) {
		this.policy = policy;
	}
}
