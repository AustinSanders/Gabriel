/*
 * Created on Oct 24, 2005
 *
 */
package c2.fw.secure.tm;

import c2.fw.secure.rbac.RoleHierarchical;

/**
 * A role in a decentralized world. It belongs to one domain. 
 * 
 * @author Jie Ren
 */
public interface RoleDecentralized extends RoleHierarchical, RoleExpression {
    /**
     * Set the domain in which the role is defined 
     * @param owing		the domain that defines this role
     */
    void		setDomain(Domain owing);
    
    /**
     * Get the domain in which the role is defined
     * @return	the domain that defines this role
     */
    Domain		getDomain();
}
