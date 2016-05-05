package c2.util;

import c2.fw.*;

public class MessageSendProxy{

	protected Brick b;
	protected Interface iface;
	
	public MessageSendProxy(Brick b, Interface iface){
		this.b = b;
		this.iface = iface;
	}
	
	public void send(Message m){
		b.sendToAll(m, iface);
	}
}
