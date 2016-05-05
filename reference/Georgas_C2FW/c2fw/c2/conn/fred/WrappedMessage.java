package c2.conn.fred;

import c2.fw.Message;

public class WrappedMessage implements java.io.Serializable{

	private Message m;
	private String uid;
	
	public WrappedMessage(Message m){
		this.m = m;
		this.uid = c2.util.UIDGenerator.generateUID();
	}
	
	public boolean equals(Object o){
		if(!(o instanceof WrappedMessage)){
			return false;
		}
		WrappedMessage otherMessage = (WrappedMessage)o;
		return otherMessage.uid.equals(uid);
	}
	
	public int hashCode(){
		return uid.hashCode();
	}
	
	public String getUID(){
		return uid;
	}
	
	public Message getMessage(){
		return m;
	}

}

