/*
 * Created on Oct 25, 2005
 *
 */
package c2.fw.secure.tm;

import c2.fw.secure.rbac.Name;
import c2.fw.secure.rbac.RBACHierarchical;

/**
 * The decentralized RBAC. It is an extension of the hierarchical RBAC model.
 * It corresponds to an administrative domain. 
 * 
 * @author Jie Ren
 */
public interface RBACDecentralized extends RBACHierarchical {
	/**
	 * Set the domain (the defining/owing concept) of this RBTM. 
	 * 
	 * @param domain the domain
	 */
	void		setDomain(Domain domain);

	/**
	 * Set the domain (the defining/owing concept) of this RBTM.
	 * 
	 * @param domainName the name of the domain
	 */
	void		setDomain(Name domainName);
	
	/**
	 * Get the domain of this RBTM.
	 * 
	 * @return the domain (the defining/owing concept)
	 */
	Domain		getDomain();
	
	/**
	 * Set the trust manager who manages cross domain trust
	 *  
	 * @param trustManager 		the trust manager
	 */
	void		setTrustManager(RBTM	trustManager);
	/**
	 * Get the trust manager
	 * 
	 * @return the trust manager. It could be null.
	 */
	RBTM		getTrustManager();
}
