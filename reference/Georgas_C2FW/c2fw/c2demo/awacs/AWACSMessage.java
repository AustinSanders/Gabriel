package c2demo.awacs;

import c2.fw.*;

public class AWACSMessage extends AbstractMessage implements DeferredMessage{

	private String payload;
	private int earliestProcessTime = 0;

	private double length;
	private String destPlatform;
	private String destProcess;
	private String srcPlatform;
	private String srcProcess;
	private String route;
	
	public AWACSMessage(){
		super();
	}
	
	/*
	public AWACSMessage(Identifier id1, Identifier id2){
		super(id1, id2);
	}
	*/
	
	public void setPayload(String payload){
		this.payload = payload;
	}
	
	public String getPayload(){
		return payload;
	}
	
	
	public void setLength(double length){
		this.length = length;
	}
	
	public double getLength(){
		return length;
	}
	
	public void setDestinationPlatform(String destPlatform){
		this.destPlatform = destPlatform;
	}
	
	public String getDestinationPlatform(){
		return destPlatform;
	}
	
	public void setDestinationProcess(String destProcess){
		this.destProcess = destProcess;
	}
	
	public String getDestinationProcess(){
		return destProcess;
	}
	
	public void setSourcePlatform(String srcPlatform){
		this.srcPlatform = srcPlatform;
	}
	
	public String getSourcePlatform(){
		return srcPlatform;
	}
	
	public void setSourceProcess(String srcProcess){
		this.srcProcess = srcProcess;
	}
	
	public String getSourceProcess(){
		return srcProcess;
	}

	public void setRoute(String route){
		this.route = route;
	}
	
	public String getRoute(){
		return route;
	}
	
	public Message duplicate(){
		AWACSMessage dup = new AWACSMessage();
		dup.setPayload(payload);
		dup.setEarliestProcessTime(earliestProcessTime);
		dup.setRoute(route);
		dup.setSourceProcess(srcProcess);
		dup.setDestinationProcess(destProcess);
		dup.setSourcePlatform(srcPlatform);
		dup.setDestinationPlatform(destPlatform);
		dup.setLength(length);
		return dup;
	}

	public void setEarliestProcessTime(long time){
		earliestProcessTime = (int)time;
	}
	
	public long getEarliestProcessTime(){
		return (long)earliestProcessTime;
	}
	
	public String toString(){
		 return "AWACSMessage{payload=\"" + payload + "\"; earliestProcessTime=\"" + earliestProcessTime + "\"; destinationPlatform=\"" + destPlatform + "; sourcePlatform=\"" + srcPlatform + "\"; sourceProcess=\"" + srcProcess + "\"};";
	}

}

