/*
 * Created on Jul 18, 2005
 *
 */
package c2.fw.secure;

/**
 * This exception signifies that we cannot find an appropriate permit or deny 
 * decision after consulting a nominally complete policy.
 * 
 * @author Jie Ren
 */
public class IndeterminateDecisionException extends SecurityException {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	String	request = "";
    String	policy = "";

    public IndeterminateDecisionException(String request, String policy) {
        this.request = request;
        this.policy = policy;
    }
    
    public String toString() {
        return request + " is indeterminate from " + policy;
    }
}
