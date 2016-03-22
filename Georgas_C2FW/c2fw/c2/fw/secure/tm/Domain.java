package c2.fw.secure.tm;

import java.io.Serializable;

import c2.fw.secure.rbac.NamedEntity;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
/**
 * A domain represents an independently managed domain. It maintains its 
 * own RBAC definition, and can trust other domains' RBAC definition.
 */
public interface Domain 
	extends RoleExpression, NamedEntity, Serializable, Comparable {
    /**
     * One of the names for the default domain. The other is the empty string.
     */
	public static final String DOMAIN_DEFAULT = "default";
	
	/**
	 * Whether this domain designates a default domain. A default domain
	 * has a name as "" or "default"
	 * 
	 * @return true if this domain is the default domain
	 */
	public boolean isDefault();
}
