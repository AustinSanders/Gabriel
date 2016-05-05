package c2.fw;

/**
 * An <CODE>Interface</CODE> object represents an interface on a
 * <CODE>Brick</CODE>.   Interfaces are the 'ports' where
 * messages arrive and leave a brick.  Each interface object
 * contains a list of other interfaces (and their bricks)
 * to which it is connected, mostly to support filtering 
 * and selective message processing by <CODE>Brick</CODE>s.
 *
 * @see c2.fw.Brick
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface Interface{

	/**
	 * Get the <CODE>Identifier</CODE> of this interface.
	 * @return interface <CODE>Identifier</CODE>.
	 */
	public Identifier getIdentifier();
	
	/**
	 * Get the brick to which this interface is attached.
	 * @return <CODE>Brick</CODE> to which this interface
	 * is attached.
	 */
	public Brick getBrick();

	/**
	 * Add a reference to this Interface indicating that it is
	 * connected, by means of a <CODE>Weld</CODE>, to an interface
	 * on another brick.
	 * @see c2.fw.Weld
	 * @param ids Brick+Interface ID Pair of other interface.
	 */
	public void addConnectedInterface(BrickInterfaceIdPair ids);
	
	/**
	 * Remove a reference to this Interface indicating that it is
	 * no longer connected, by means of a <CODE>Weld</CODE>, to an interface
	 * on another brick.
	 * @see c2.fw.Weld
	 * @param ids Brick+Interface ID Pair of other interface.
	 */
	public void removeConnectedInterface(BrickInterfaceIdPair ids);
	
	/**
	 * Get a list of interfaces to which this interface is connected
	 * by means of a <CODE>Weld</CODE>.
	 * @return list of interfaces.
	 */
	public BrickInterfaceIdPair[] getAllConnectedInterfaces();

	/**
	 * Get the Brick-interface ID pair for this interface
	 * @return This brick's <CODE>BrickInterfaceIDPair</CODE>.
	 */
	public BrickInterfaceIdPair getBrickInterfaceIdPair();
}

