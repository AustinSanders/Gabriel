package c2.pcwrap;

public final class NullParameterValue implements java.io.Serializable{
	public int hashCode(){
		return NullParameterValue.class.hashCode();
	}
	public boolean equals(Object o){
		return(o instanceof NullParameterValue);
	}
}

