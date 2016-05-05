package c2.fw;

/**
 * Class used to refer to a unique Brick+Interface combination.
 * Because this framework allows duplicate interface IDs (on separate
 * bricks), it is necessary to refer to a specific interface
 * with both its own identifier and the identifier of the brick
 * to which it's attached.
 *
 * @see c2.fw.Weld
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public final class BrickInterfaceIdPair implements java.io.Serializable{

	private Identifier brickId;
	private Identifier interfaceId;
	
	/**
	 * Create a new <CODE>BrickInterfaceIdPair</CODE>.
	 * @param brickId Identifier of the brick.
	 * @param interfaceId Identifier of the interface.
	 */
	public BrickInterfaceIdPair(Identifier brickId, Identifier interfaceId){
		this.brickId = brickId;
		this.interfaceId = interfaceId;
	}
	
	/**
	 * Get the identifier of the brick in this Brick+Interface ID pair.
	 * @return Identifier of the brick.
	 */
	public Identifier getBrickIdentifier(){
		return brickId;
	}
	
	/**
	 * Get the identifier of the interface in this Brick+Interface ID pair.
	 * @return Identifier of the brick.
	 */
	public Identifier getInterfaceIdentifier(){
		return interfaceId;
	}

	/**
	 * Overridden for <CODE>equals()</CODE>.
	 */
	public int hashCode(){
		if((brickId == null) && (interfaceId == null)){
			return 1;
		}
		else if((brickId == null) && (interfaceId != null)){
			return interfaceId.hashCode();
		}
		else if((brickId != null) && (interfaceId == null)){
			return brickId.hashCode();
		}
		else{
			return brickId.hashCode() ^ interfaceId.hashCode();
		}
	}
	
	/**
	 * Two <CODE>BrickInterfaceIdPair</CODE>s are equal if their
	 * Brick and Interface IDs are equal() (memberwise equality).
	 * @param o Another object, presumably a <CODE>BrickInterfaceIdPair</CODE>
	 * to compare to this one.
	 * @return <CODE>true</CODE> if <CODE>o</CODE> is a <CODE>BrickInterfaceIdPair</CODE>
	 * and its brick and interface Identifiers are equal to this
	 * <CODE>BrickInterfaceIdPair</CODE>'s.  <CODE>false</CODE> otherwise.
	 */
	public boolean equals(Object o){
		if(!(o instanceof BrickInterfaceIdPair)){
			return false;
		}
		if(o.hashCode() != hashCode()) return false;
		BrickInterfaceIdPair b2 = (BrickInterfaceIdPair)o;
		return (b2.brickId.equals(brickId) && b2.interfaceId.equals(interfaceId));
	}
	
	/**
	 * Return a string representation of this <CODE>BrickInterfaceIdPair</CODE>.
	 * @return a string representation of this <CODE>BrickInterfaceIdPair</CODE>. 
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("BrickInterfaceIdPair{brickId=\"");
		sb.append(brickId);
		sb.append("\", interfaceId=\"");
		sb.append(interfaceId);
		sb.append("\"};");
		return sb.toString();
	}
}

