/*
 * Created on Jul 3, 2005
 *
 */
package c2.fw.secure;

import c2.fw.Brick;
import c2.fw.Interface;

/**
 * The secure connector enforces secure policies.
 * 
 * @author Jie Ren
 */
public interface ISecureConnector extends ISecureBrick {
	/**
	 * Whether this connector will accept a weld request to one of its interface
	 * from another interface of another brick
	 * 
	 * @param myInterface		the local interface of this connector 
	 * @param otherBrick		the other brick
	 * @param otherInterface	the other interface
	 * @return	true if the connector will accept the weld request, false if the 
	 * connector will reject the weld request 
	 */
	public boolean	acceptBrick(Interface myInterface, Brick otherBrick, Interface otherInterface);
}
