package c2.fw;

/**
 * This class describes a parameter that can be passed to a brick
 * to initialize it.  An array of these are passed to a BrickFactory
 * to set initialization parameters when a brick is created.
 *
 * @see c2.fw.BrickLoader
 * @see c2.fw.BrickFactory
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class InitializationParameter implements java.io.Serializable{

	protected String name;
	protected String value;
	
	protected InitializationParameter(){
		this.name = "";
		this.value = "";
	}

	public InitializationParameter(String name, String value){
		this.name = name;
		this.value = value;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setValue(String value){
		this.value = value;
	}

	public String getName(){
		return name;
	}
	
	public String getValue(){
		return value;
	}

	public String toString(){
		return name + "=" + value;
	}
  
}

