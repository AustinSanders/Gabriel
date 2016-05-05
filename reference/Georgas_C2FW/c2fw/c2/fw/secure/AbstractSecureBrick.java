/*
 * Created on Jul 3, 2005
 *
 */
package c2.fw.secure;

import c2.fw.AbstractDelegateBrick;
import c2.fw.Identifier;

abstract public class AbstractSecureBrick extends AbstractDelegateBrick implements ISecureBrick {
	AbstractSecureBrick(Identifier thisId) {
		super(thisId);
	}
}
