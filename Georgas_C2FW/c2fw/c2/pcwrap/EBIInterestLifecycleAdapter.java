package c2.pcwrap;

import c2.fw.*;
import c2.legacy.conn.*;

public class EBIInterestLifecycleAdapter extends c2.fw.LifecycleAdapter{

	protected Brick brick;
	protected Interface iface;
	protected MessageFilter filter;
	
	public EBIInterestLifecycleAdapter(Brick brick, Interface iface, MessageFilter filter){
		this.filter = filter;
		this.brick = brick;
		this.iface = iface;
	}
	
	public void begin(){
		//System.out.println("Begin was called, sending fim");
		FilterInterestMessage fim = new FilterInterestMessage(FilterInterestMessage.FILTER_EXCLUSIVE_INTEREST,
			filter);
		brick.sendToAll(fim, iface);
	}

}
