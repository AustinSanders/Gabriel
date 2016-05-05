/*
 * Created on Jul 15, 2005
 *
 */
package c2.fw.secure;

import c2.fw.Interface;

/**
 * Marker for a directional interface for xADL 
 * 
 * @author Jie Ren
 */
public interface DirectionalInterface extends Interface {
	public static final int		DIRECTION_IN = 0;
	public static final int		DIRECTION_OUT = 1;
	public static final	int		DIRECTION_INOUT = 2;

	/**
	 * Set the direction for this interface
	 * 
	 * @param direction one of DIRECTION_IN, DIRECTOIN_OUT, or DIRECTION_INOUT
	 */
	void	setDirection(int direction);

	/**
	 * Get the direction for this interface.
	 * 
	 * @return the direction. Should be one of DIRECTION_IN, DIRECTOIN_OUT, or DIRECTION_INOUT.
	 */
	int		getDirection();
}
