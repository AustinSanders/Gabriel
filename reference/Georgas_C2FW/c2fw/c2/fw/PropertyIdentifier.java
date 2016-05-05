//!!(C)!!
package c2.fw;

import java.util.Properties;
import java.util.Enumeration;

/**
 * <code>Identifier</code> that represents an entity with a list
 * of name-value String pairs.
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class PropertyIdentifier implements Identifier, java.io.Serializable{

	private Properties props;
	
	/**
	 * Creates a new <code>PropertyIdentifier</code> with no properties.
	 */
	public PropertyIdentifier(){
		this.props = new Properties();
	}
	
	/**
	 * Adds a new name-value String pair property to this <code>Identifier</code>.
	 * @param name Name of the property.
	 * @param value Value of the property.
	 */
	public void addProperty(String name, String value){
		props.put(name, value);
	}
	
	/**
	 * Gets the value of a property given its name.
	 * @param name Name of the property.
	 * @return Value of the property, or <code>null</code> if there is none.
	 */
	public String getProperty(String name){
		return props.getProperty(name);
	}
	
	/**
	 * Returns an <code>Enumeration</code> of the property names in this
	 * <code>Identifier</code>.
	 * @return <code>Enumeration</code> of the property names (Strings) in this <code>Identifier</code>.
	 * @see java.util.Enumeration
	 */
	public Enumeration getPropertyNames(){
		return props.propertyNames();
	}
	
	/**
	 * Determines if two <code>Identifiers</code> are equal.
	 * @param otherIdentifier Other identifier to test for equality.
	 * @return <code>true</code> if the other identifier is a <code>PropertyIdentifier</code>
	 * and has all the same name-value mappings that this one does, <code>false</code> otherwise.
	 */
	public boolean equals(Object otherIdentifier){
		if(!(otherIdentifier instanceof PropertyIdentifier)){
			return false;
		}
		
		PropertyIdentifier id = (PropertyIdentifier)otherIdentifier;
		for(Enumeration en = props.propertyNames(); en.hasMoreElements(); ){
			String propName = (String)en.nextElement();
			String propValue = getProperty(propName);
			
			String otherPropValue = id.getProperty(propName);
			if(otherPropValue == null){
				return false;
			}
			if(!(propValue.equals(otherPropValue))){
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets a string representation of this <code>Identifier</code>.
	 * @return String representation of this <code>Identifier</code>.
	 */
	public String toString(){
		return props.toString();
	}

}

