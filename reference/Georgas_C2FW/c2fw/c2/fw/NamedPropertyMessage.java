//!!(C)!!
package c2.fw;

import java.lang.reflect.*;
import java.util.*;

/**
 * Describes a C2 Message that has one name (a String) and zero or more properties (String to Object
 * mappings).
 */
public class NamedPropertyMessage extends AbstractMessage implements Message, java.io.Serializable, Cloneable{

	/** Name of the message */
	protected String name;
	
	/** Message Properties */
	protected Map props;
	
	/**
	 * Creates a new <code>NamedPropertyMessage</code> with the given name.
	 * @param name Name of this message.
	 */
	public NamedPropertyMessage(String name){
		this.name = name;
		this.props = new HashMap();
	}
	
	protected NamedPropertyMessage(NamedPropertyMessage copyMe){
		this.name = copyMe.name;
		this.props = new HashMap(copyMe.props);
		//this.props = (HashMap)(copyMe.props).clone();
		/*
		this.props = new Hashtable();
		
		for(Iterator it = copyMe.props.keySet().iterator(); it.hasNext(); ){
			Object key = it.next();
			Object value = copyMe.props.get(key);
			Object keyClone = cloneObject(key);
			Object valueClone = cloneObject(value);
			this.props.put(keyClone, valueClone);
		}
		*/
	}
	
	private static final Class[] emptyClassArray = new Class[]{};
	private static final Object[] emptyObjectArray = new Object[]{};
	
	protected static Object cloneObject(Object o){
		if(o == null) return null;
		Method cloneMethod = null;
		try{
			cloneMethod = o.getClass().getMethod("clone", emptyClassArray);
		}
		catch(NoSuchMethodException e){
			return o;
		}
		
		if(cloneMethod != null){
			try{
				Object clonedObject = cloneMethod.invoke(o, emptyObjectArray);
				return clonedObject;
			}
			catch(Exception e){
				return o;
			}
		}
		else{
			return o;
		}
	}
			
	
	public Object clone() throws CloneNotSupportedException{
		NamedPropertyMessage m = new NamedPropertyMessage(this);
		return m;
	}
	
	//!!!
	public Message duplicate(){
		try{
			return (Message)clone();
		}
		catch(CloneNotSupportedException cnse){
			//won't happen;
			return null;
		}
		
		//NamedPropertyMessage m2 = new NamedPropertyMessage(name);
		//m2.props = (Hashtable)props.clone();
		//return m2;
	}

	private int getHashCode(Object o){
		if(o == null){
			return 0;
		}
		Class c = o.getClass();
		
		if(c.isArray()){
			return Array.getLength(o);
		}
		else{
			return o.hashCode();
		}
	}	
	
	private boolean realEquals(Object o1, Object o2){
		if((o1 == null) && (o2 == null)){
			return true;
		}
		if((o1 == null) || (o2 == null)){
			return false;
		}
		
		Class o1Class = o1.getClass();
		Class o2Class = o2.getClass();
		
		if((o1Class.isArray()) && (o2Class.isArray())){
			Class o1ct = o1Class.getComponentType();
			Class o2ct = o2Class.getComponentType();
			
			if(o1ct.isPrimitive() && o2ct.isPrimitive()){
				if(!o1ct.equals(o2ct)){
					return false;
				}
				if(o1ct.equals(boolean.class)){
					return Arrays.equals((boolean[])o1, (boolean[])o2);
				}
				if(o1ct.equals(byte.class)){
					return Arrays.equals((byte[])o1, (byte[])o2);
				}
				if(o1ct.equals(short.class)){
					return Arrays.equals((short[])o1, (short[])o2);
				}					
				if(o1ct.equals(int.class)){
					return Arrays.equals((int[])o1, (int[])o2);
				}					
				if(o1ct.equals(char.class)){
					return Arrays.equals((char[])o1, (char[])o2);
				}					
				if(o1ct.equals(long.class)){
					return Arrays.equals((long[])o1, (long[])o2);
				}					
				if(o1ct.equals(float.class)){
					return Arrays.equals((float[])o1, (float[])o2);
				}					
				if(o1ct.equals(double.class)){
					return Arrays.equals((double[])o1, (double[])o2);
				}					
				throw new RuntimeException("Bad primitive type mojo!");
			}
			if(o1ct.isPrimitive() || o2ct.isPrimitive()){
				return false;
			}
			//They're both not primitive
			//Check to see if it's an array-of-arrays
			if(o1ct.isArray() && o2ct.isArray()){
				Object[] arr1 = (Object[])o1;
				Object[] arr2 = (Object[])o2;
				if(arr1.length != arr2.length){
					return false;
				}
				for(int i = 0; i < arr1.length; i++){
					if(!realEquals(arr1[i], arr2[i])){
						return false;
					}
				}
				//The contents are equal
				return true;
			}
			else if(o1ct.isArray() || (o2ct.isArray())){
				//Only one component type is an array
				return false;
			}
			else{
				//Neither component type is an array
				return Arrays.equals((Object[])o1, (Object[])o2);
			}
		}
		else if(o1Class.isArray() || o2Class.isArray()){
			//Only one of the two objects is an array
			return false;
		}
		else{
			//Neither one is an array.
			return o1.equals(o2);
		}
	}			
		
	public boolean equals(Object o){
		if(!(o instanceof NamedPropertyMessage)){
			return false;
		}
		NamedPropertyMessage om = (NamedPropertyMessage)o;
		if((name == null) && (om.name != null)){
			return false;
		}
		if((name != null) && (om.name == null)){
			return false;
		}
		if((name != null) && (om.name != null) && (!name.equals(om.name))){
			return false;
		}
		
		Map ht1 = props;
		Map ht2 = om.props;
		
		if(ht1.size() != ht2.size()){
			return false;
		}
		
		for(Iterator it = ht1.keySet().iterator(); it.hasNext(); ){
			Object key = it.next();
			Object ht1Value = ht1.get(key);
			Object ht2Value = ht2.get(key);
			if(ht2Value == null){
				return false;
			}
			
			if(!realEquals(ht1Value, ht2Value)){
				return false;
			}
		}
		return true;
	}
		
	public int hashCode(){
		int hc = getHashCode(name);
		for(Iterator it = props.keySet().iterator(); it.hasNext(); ){
			Object key = it.next();
			Object propValue = props.get(key);
			hc ^= getHashCode(key);
			hc ^= getHashCode(propValue);
		}
		return hc;
	}
	
	/**
	 * Sets the name of this message.
	 * @param name Name of this message.
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Gets the name of this message.
	 * @return Name of this message.
	 */
	public String getName(){
		return name;
	}

	/**
	 * Adds a String-Byte mapping to the parameter list of this message.
	 * @param name Name of the parameter.
	 * @param value Value of the parameter.
	 */
  public void addParameter(String name, byte value){
		props.put(name, new Byte(value));
	}
	
	/**
	 * Adds a String-Short mapping to the parameter list of this message.
	 * @param name Name of the parameter.
	 * @param value Value of the parameter.
	 */
  public void addParameter(String name, short value){
		props.put(name, new Short(value));
	}
	
	/**
	 * Adds a String-Character mapping to the parameter list of this message.
	 * @param name Name of the parameter.
	 * @param value Value of the parameter.
	 */
  public void addParameter(String name, char value){
		props.put(name, new Character(value));
	}
	
	/**
	 * Adds a String-Integer mapping to the parameter list of this message.
	 * @param name Name of the parameter.
	 * @param value Value of the parameter.
	 */
  public void addParameter(String name, int value){
		props.put(name, new Integer(value));
	}
	
	/**
	 * Adds a String-Long mapping to the parameter list of this message.
	 * @param name Name of the parameter.
	 * @param value Value of the parameter.
	 */
  public void addParameter(String name, long value){
		props.put(name, new Long(value));
	}
	
	/**
	 * Adds a String-Float mapping to the parameter list of this message.
	 * @param name Name of the parameter.
	 * @param value Value of the parameter.
	 */
  public void addParameter(String name, float value){
		props.put(name, new Float(value));
	}
	
	/**
	 * Adds a String-Double mapping to the parameter list of this message.
	 * @param name Name of the parameter.
	 * @param value Value of the parameter.
	 */
  public void addParameter(String name, double value){
		props.put(name, new Double(value));
	}
	
	/**
	 * Adds a String-Boolean mapping to the parameter list of this message.
	 * @param name Name of the parameter.
	 * @param value Value of the parameter.
	 */
  public void addParameter(String name, boolean value){
		props.put(name, new Boolean(value));
	}
	
	/**
	 * Adds a String-Object mapping to the parameter list of this message.
	 * @param name Name of the parameter.
	 * @param value Value of the parameter.
	 */
  public void addParameter(String name, Object value){
		if(value == null){
			props.put(name, new NullPropertyValue());
		}
		else{
			props.put(name, value);
		}
  }
 
	/**
	 * Gets a parameter from this message.
	 * @param name Name of the parameter.
	 * @return Value of the parameter, or <code>null</code> if there is no such parameter.
	 */
	public Object getParameter(String name){
		Object o = props.get(name);
		if(o instanceof NullPropertyValue){
			return null;
		}
		return o; //props.get(name);
	}
  
	public boolean hasParameter(String name){
		Object o = props.get(name);
		return (o != null);
	}
	
	/**
	 * Gets a parameter from this message as a byte value.
	 * @param name Name of the parameter.
	 * @return Value of the parameter as a byte.
	 * @exception IllegalArgumentException if the parameter does not exist or is
	 * not of the correct type.
	 */
	public byte getByteParameter(String name){
		try{
			return ((Byte)getParameter(name)).byteValue();
		}
		catch(NullPointerException npe){
			throw new IllegalArgumentException("No such parameter.");
		}
		catch(ClassCastException cce){
			throw new IllegalArgumentException("Parameter name given did not represent a byte.");
		}
	}
			
	/**
	 * Gets a parameter from this message as a short value.
	 * @param name Name of the parameter.
	 * @return Value of the parameter as a short.
	 * @exception IllegalArgumentException if the parameter does not exist or is
	 * not of the correct type.
	 */
	public short getShortParameter(String name){
		try{
			return ((Short)getParameter(name)).shortValue();
		}
		catch(NullPointerException npe){
			throw new IllegalArgumentException("No such parameter.");
		}
		catch(ClassCastException cce){
			throw new IllegalArgumentException("Parameter name given did not represent a short.");
		}
	}
	
	/**
	 * Gets a parameter from this message as a char value.
	 * @param name Name of the parameter.
	 * @return Value of the parameter as a char.
	 * @exception IllegalArgumentException if the parameter does not exist or is
	 * not of the correct type.
	 */
	public char getCharParameter(String name){
		try{
			return ((Character)getParameter(name)).charValue();
		}
		catch(NullPointerException npe){
			throw new IllegalArgumentException("No such parameter.");
		}
		catch(ClassCastException cce){
			throw new IllegalArgumentException("Parameter name given did not represent a char.");
		}
	}
	
	/**
	 * Gets a parameter from this message as an integer value.
	 * @param name Name of the parameter.
	 * @return Value of the parameter as an int.
	 * @exception IllegalArgumentException if the parameter does not exist or is
	 * not of the correct type.
	 */
	public int getIntParameter(String name){
		try{
			return ((Integer)getParameter(name)).intValue();
		}
		catch(NullPointerException npe){
			throw new IllegalArgumentException("No such parameter.");
		}
		catch(ClassCastException cce){
			throw new IllegalArgumentException("Parameter name given did not represent an integer.");
		}
	}
	
	/**
	 * Gets a parameter from this message as a long value.
	 * @param name Name of the parameter.
	 * @return Value of the parameter as a long.
	 * @exception IllegalArgumentException if the parameter does not exist or is
	 * not of the correct type.
	 */
	public long getLongParameter(String name){
		try{
			return ((Long)getParameter(name)).longValue();
		}
		catch(NullPointerException npe){
			throw new IllegalArgumentException("No such parameter.");
		}
		catch(ClassCastException cce){
			throw new IllegalArgumentException("Parameter name given did not represent a long.");
		}
	}
	
	/**
	 * Gets a parameter from this message as a short value.
	 * @param name Name of the parameter.
	 * @return Value of the parameter as a double.
	 * @exception IllegalArgumentException if the parameter does not exist or is
	 * not of the correct type.
	 */
	public double getDoubleParameter(String name){
		try{
			return ((Double)getParameter(name)).doubleValue();
		}
		catch(NullPointerException npe){
			throw new IllegalArgumentException("No such parameter.");
		}
		catch(ClassCastException cce){
			throw new IllegalArgumentException("Parameter name given did not represent a double.");
		}
	}
	
	/**
	 * Gets a parameter from this message as a float value.
	 * @param name Name of the parameter.
	 * @return Value of the parameter as a float.
	 * @exception IllegalArgumentException if the parameter does not exist or is
	 * not of the correct type.
	 */
	public float getFloatParameter(String name){
		try{
			return ((Float)getParameter(name)).floatValue();
		}
		catch(NullPointerException npe){
			throw new IllegalArgumentException("No such parameter.");
		}
		catch(ClassCastException cce){
			throw new IllegalArgumentException("Parameter name given did not represent a float.");
		}
	}
	
	/**
	 * Gets a parameter from this message as a boolean value.
	 * @param name Name of the parameter.
	 * @return Value of the parameter as a boolean.
	 * @exception IllegalArgumentException if the parameter does not exist or is
	 * not of the correct type.
	 */
	public boolean getBooleanParameter(String name){
		try{
			return ((Boolean)getParameter(name)).booleanValue();
		}
		catch(NullPointerException npe){
			throw new IllegalArgumentException("No such parameter.");
		}
		catch(ClassCastException cce){
			throw new IllegalArgumentException("Parameter name given did not represent a boolean.");
		}
	}
	
	/**
	 * Removes a parameter from this message.  Does nothing if the parameter does
	 * not exist.
	 * @param name Name of the parameter to remove.
	 */
	public void removeParameter(String name){
		props.remove(name);
	}
  
	/**
	 * Returns a String representation of this message. 
	 * @return String representation of this message.
	 */
  public String toString(){
		return name + "[Message] (from:" + source + ")(to:" + destination + ")(" + props.toString() + ")";
  }
	
	/**
	 * Gets a Map representation of the name/value pair properties in this
	 * message.
	 * @return Map representation of name/value pair properties.
	 */
	public Map getPropertyMap(){
		return new HashMap(props);
		//return (Map)props.clone();
	}
	
	/**
	 * Gets all the property names in this message as an array
	 * of strings.
	 * @return All property names in an array.
	 */
	public String[] getAllPropertyNames(){
		Set s = props.keySet();
		return (String[])s.toArray(new String[0]);
	}
	
	static class NullPropertyValue implements java.io.Serializable{
		public boolean equals(Object o){
			if(o instanceof NullPropertyValue){
				return true;
			}
			return false;
		}
		
		public int hashCode(){
			return 0;
		}
	};

}

