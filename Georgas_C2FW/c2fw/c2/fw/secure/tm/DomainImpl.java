package c2.fw.secure.tm;

import c2.fw.secure.rbac.Name;
import c2.fw.secure.rbac.NamedEntityImpl;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
/**
 * An object of the DomainImpl class represents an defining domain in RBTM.
 */
public class DomainImpl extends NamedEntityImpl implements Domain {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	//
	// Object methods
	//
	/**
	 * Create a domain
	 * 
	 * @param name the name of the domain
	 */
	public DomainImpl(String name) {
		super(name);
	}
	
	/**
	 * Create a domain
	 * 
	 * @param name the name of the domain
	 */
	public DomainImpl(Name name) {
		super(name);
	}

	public boolean isDefault() {
	    boolean	result = false;
	    String	name = theName.getValue();
	    if (name.equals("") || name.equals(DOMAIN_DEFAULT))
	        result = true;
	    return result;
	}
	
	public static boolean isDefault(String domainName) {
	    return 	domainName == null || domainName.equals("") ||
	    		domainName.equals(DOMAIN_DEFAULT);
	}
	
	/**
	 * Get the cononical name for a domain name. This is mainly used
	 * to get a canonical name for the default domain, since the domain
	 * might be absent, or with an empty string name. 
	 * 
	 * @param domainName the domain name
	 * @return if the input name is null or "", then the return value is
	 * "default", otherwise is the same as the input name 
	 */
	public static String getCanonicalName(String domainName) {
	    String	result = DOMAIN_DEFAULT;
	    if (domainName==null || domainName.equals("") || 
	            domainName.equals(DOMAIN_DEFAULT))
	        return result;
	    else
	        return domainName;
	}
	
	public boolean equals(java.lang.Object other) {
	    if (other == null)
	        return false;
	    if (getClass() != other.getClass())
	        return false;
		DomainImpl otherDomain = (DomainImpl)other;
		if (this.theName.equals(otherDomain.theName))
			return true;
		else
			return false;
	}
	
	public int hashCode() {
		// overriding this method is necessary for set containment test
		return theName.hashCode();
	}
	
	public String toString() {
		return theName.toString();
	}
	
	public int compareTo(Object o) {
		return theName.toString().compareTo(((DomainImpl) o).theName.toString());
	}
}
