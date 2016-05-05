//!!(C)!!
package c2.fw;

/**
 * Describes a simple weld between two C2 bricks.
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class SimpleWeld extends Object implements Weld, java.io.Serializable{

	private BrickInterfaceIdPair firstEndpt;
	private BrickInterfaceIdPair secondEndpt;
	
	/**
	 * Creates a new <code>SimpleWeld</code> attaching the first component
	 * to the second component.
	 * @param firstEndpt <code>BrickInterfaceIdPair</code> of the first brick+interface.
	 * @param secondId <code>BrickInterfaceIdPair</code> of the second brick+interface.
	 */
	public SimpleWeld(BrickInterfaceIdPair firstEndpt, BrickInterfaceIdPair secondEndpt){
		if((firstEndpt == null) || (secondEndpt == null)){
			throw new IllegalArgumentException();
		}
		
		this.firstEndpt = firstEndpt;
		this.secondEndpt = secondEndpt;
	}
	
	/**
	 * Gets the <code>BrickInterfaceIdPair</code> of the first brick+interface in this weld.
	 * @return <code>BrickInterfaceIdPair</code> of the first brick+interface in this weld.
	 */
	public BrickInterfaceIdPair getFirstEndpoint(){
		return firstEndpt;
	}
	
	/**
	 * Gets the <code>BrickInterfaceIdPair</code> of the second brick+interface in this weld.
	 * @return <code>BrickInterfaceIdPair</code> of the second brick+interface in this weld.
	 */
	public BrickInterfaceIdPair getSecondEndpoint(){
		return secondEndpt;
	}
	
	/** 
	 * Determines if this weld is equivalent to another weld.
	 * Two welds are equivalent if they have the same interfaces
	 * in them.
	 * @param otherWeld Weld to compare to this one.
	 */
	public boolean equals(Object otherWeld){
		if(!(otherWeld instanceof Weld)){
			return false;
		}
		
		Weld w = (Weld)otherWeld;
		
		if((firstEndpt.equals(w.getFirstEndpoint())) && (secondEndpt.equals(w.getSecondEndpoint()))){
			return true;
		}
		else if((secondEndpt.equals(w.getFirstEndpoint())) && (firstEndpt.equals(w.getSecondEndpoint()))){
			return true;
		}
		else{
			return false;
		}
	}
			
}

