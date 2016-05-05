package c2.fw.secure.tm;

import c2.fw.secure.rbac.RBACException;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
public class CredentialParsingException extends RBACException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	CredentialParsingException(String info) {
		super(info);
	}
}