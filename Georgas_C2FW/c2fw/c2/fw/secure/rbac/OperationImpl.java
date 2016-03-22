/*
 * Created on Aug 21, 2005
 *
 */
package c2.fw.secure.rbac;

public class OperationImpl extends NamedEntityImpl implements Operation {
	public OperationImpl(String name) {
		super(name);
	}
	
	public OperationImpl(Name name) {
	    super(name);
	}
}
