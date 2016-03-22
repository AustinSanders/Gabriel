package c2.fw;

public abstract class AbstractMessage implements Message{

	protected BrickInterfaceIdPair source;
	protected BrickInterfaceIdPair destination;
	
	public AbstractMessage(){
		this.source = null;
		this.destination = null;
	}
	
	/*
	public AbstractMessage(Identifier sourceId, Identifier destinationId){
		this.source = source;
		this.destination = destination;
	}
	*/
	
	public void setSource(BrickInterfaceIdPair srcPair){
		this.source = srcPair;
	}
	
	public void setDestination(BrickInterfaceIdPair destPair){
		this.destination = destPair;
	}
	
	public BrickInterfaceIdPair getSource(){
		return source;
	}
	
	public BrickInterfaceIdPair getDestination(){
		return destination;
	}
	
	public abstract Message duplicate();

}

