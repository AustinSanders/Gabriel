package c2.fw;

public interface MessageFilter extends java.io.Serializable{

	public boolean accept(Message m);

}
