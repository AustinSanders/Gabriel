package c2.fw;

import java.util.Vector;

/**
 * Class implementing the boilerplate methods from the
 * <CODE>Interface</CODE> interface.
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public abstract class AbstractInterface implements Interface{

	private Identifier id = null;
	private Brick brick = null;
	private BrickInterfaceIdPair thisBrickInterfaceIdPair;
	private Vector connectedInterfaces = new Vector(10);
	private BrickInterfaceIdPair[] connectedInterfaceArray = new BrickInterfaceIdPair[0];
	/**
	 * Create a new <CODE>AbstractInterface</CODE> with the given
	 * identifier, attached to the given <CODE>Brick</CODE>.
	 * @param <CODE>id</CODE> Identifier of this interface.
	 * @param brick <CODE>Brick</CODE> to which this interface is attached.
	 */
	public AbstractInterface(Identifier id, Brick brick){
		this.id = id;
		this.brick = brick;
		thisBrickInterfaceIdPair = new BrickInterfaceIdPair(brick.getIdentifier(), id);
	}
		
	public Identifier getIdentifier(){
		return id;
	}
	
	public Brick getBrick(){
		return brick;
	}

	public void addConnectedInterface(BrickInterfaceIdPair id){
		synchronized(connectedInterfaces){
			connectedInterfaces.addElement(id);
			connectedInterfaceArray = (BrickInterfaceIdPair[])connectedInterfaces.toArray(new BrickInterfaceIdPair[0]);
		}
	}
	
	public void removeConnectedInterface(BrickInterfaceIdPair id){
		synchronized(connectedInterfaces){
			connectedInterfaces.removeElement(id);
			connectedInterfaceArray = (BrickInterfaceIdPair[])connectedInterfaces.toArray(new BrickInterfaceIdPair[0]);
		}
	}
			
	public BrickInterfaceIdPair[] getAllConnectedInterfaces(){
		synchronized(connectedInterfaces){
			//BrickInterfaceIdPair[] endpoints = new BrickInterfaceIdPair[connectedInterfaces.size()];
			//connectedInterfaces.copyInto(endpoints);
			return connectedInterfaceArray;
		}
	}
	
	public BrickInterfaceIdPair getBrickInterfaceIdPair(){
		return thisBrickInterfaceIdPair;
	}
	
}

