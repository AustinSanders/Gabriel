/*
 * Created on Jul 15, 2005
 *
 */
package c2.fw.secure;

import c2.fw.Brick;
import c2.fw.Identifier;
import c2.fw.SimpleInterface;

public class SimpleDirectionalInterface extends SimpleInterface implements DirectionalInterface {
	protected	int				direction = DirectionalInterface.DIRECTION_INOUT;

	/**
	 * Create a simple directional interface
	 * 
	 * @param id	the identifier of the interface
	 * @param brick the brick to which this interface belongs
	 */
	public SimpleDirectionalInterface(Identifier id, Brick brick){
		super(id, brick);
	}
	
	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
}
