package c2.legacy.conn;

import c2.fw.*;

public class FilterInterestMessage extends NamedPropertyMessage{
	
	public static final int FILTER_EXCLUSIVE_INTEREST = 100;
	public static final int FILTER_INTEREST = 200;
	public static final int FILTER_NONE = 300;
	
	public FilterInterestMessage(int filterType, /*Identifier brickID,*/ MessageFilter filter){
		super("FilterInterestMessage");
		super.addParameter("filterType", filterType);
		//super.addParameter("brickID", brickID);
		super.addParameter("filter", filter);
	}
	
	protected FilterInterestMessage(FilterInterestMessage copyMe){
		super(copyMe);
	}
	
	public Message duplicate(){
		return new FilterInterestMessage(this);
	}

	public int getFilterType(){
		return super.getIntParameter("filterType");
	}
	
	//public Identifier getBrickID(){
	//	return (Identifier)super.getParameter("brickID");
	//}
	
	public MessageFilter getFilter(){
		return (MessageFilter)super.getParameter("filter");
	}		
	
}