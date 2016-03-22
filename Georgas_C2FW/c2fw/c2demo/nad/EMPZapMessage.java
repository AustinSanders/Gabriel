package c2demo.nad;

import c2.fw.*;

public class EMPZapMessage extends NamedPropertyMessage{
	public EMPZapMessage(){
		super("EMPZapMessage");
	}

	protected EMPZapMessage(EMPZapMessage copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new EMPZapMessage(this);
	}

}