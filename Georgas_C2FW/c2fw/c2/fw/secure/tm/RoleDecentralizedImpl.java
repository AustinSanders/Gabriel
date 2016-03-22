package c2.fw.secure.tm;

import java.io.Serializable;

import c2.fw.secure.rbac.Name;
import c2.fw.secure.rbac.RoleHierarchicalImpl;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
public class RoleDecentralizedImpl extends RoleHierarchicalImpl 
			implements Serializable, RoleDecentralized {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	private Domain domain;

	public RoleDecentralizedImpl(Name domain, Name role) {
	    super(role);
	    this.domain = new DomainImpl(domain);
	}

	public RoleDecentralizedImpl(String domainName, String roleName) {
	    super(roleName);
		domain = new DomainImpl(domainName);
	}

	public RoleDecentralizedImpl(Domain domain, String roleName) {
	    super(roleName);
		this.domain = domain;
	}

	public RoleDecentralizedImpl(Domain domain, Name role) {
	    super(role);
		this.domain = domain;
	}
	
	public RoleDecentralizedImpl(Name role) {
	    super(role);
	    // create a default domain
	    this.domain = new DomainImpl(Domain.DOMAIN_DEFAULT);
	}

	public RoleDecentralizedImpl(String roleName) {
	    super(roleName);
	    // create a default domain
	    this.domain = new DomainImpl(Domain.DOMAIN_DEFAULT);
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public void setDomain(Domain domain) {
	    this.domain = domain;
	}

	public String toString() {
		return domain.toString() + "." + super.getNameString();
	}

	public int hashCode() {
		int		hashCode = getName().hashCode();
		if (getDomain() != null) {
			// This method is called during super class initiliazation, before
			// the domain is properly set
			hashCode += getDomain().hashCode() * 2;
		}
		return hashCode;
	}

	public boolean equals(Object o) {
		return (o instanceof RoleDecentralizedImpl) && getDomain().equals(((RoleDecentralizedImpl) o).getDomain())
				&& getName().equals(((RoleDecentralizedImpl) o).getName());
	}
}
