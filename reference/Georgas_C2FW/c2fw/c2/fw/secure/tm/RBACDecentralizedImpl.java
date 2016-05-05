/*
 * Created on Oct 25, 2005
 *
 */
package c2.fw.secure.tm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import c2.fw.secure.rbac.Name;
import c2.fw.secure.rbac.Object;
import c2.fw.secure.rbac.RBACCore;
import c2.fw.secure.rbac.RBACHierarchicalImpl;
import c2.fw.secure.rbac.Role;
import c2.fw.secure.rbac.RoleDoesNotExistException;

public class RBACDecentralizedImpl extends RBACHierarchicalImpl implements RBACDecentralized {
	protected	Domain				domain;
	protected	RBTM				trustManager;

	public RBACDecentralizedImpl() {
	    this("", SESSION_CONTINUE_DOWNGRADE, false, true);
	}
	
	public RBACDecentralizedImpl(boolean autoAddOnGet) {
	    this("", SESSION_CONTINUE_DOWNGRADE, autoAddOnGet, true);
	}
	
	public RBACDecentralizedImpl(String domainOwner) {
		super(RBACCore.SESSION_CONTINUE_DOWNGRADE, false, true);
		domain = new DomainImpl(domainOwner);
	}
	
	public RBACDecentralizedImpl(Name domainOwner) {
		super(RBACCore.SESSION_CONTINUE_DOWNGRADE, false, true);
		domain = new DomainImpl(domainOwner);
	}
	
	public RBACDecentralizedImpl(String domainOwner, boolean autoAddOnGet) {
		super(RBACCore.SESSION_CONTINUE_DOWNGRADE, autoAddOnGet, true);
		domain = new DomainImpl(domainOwner);
	}
	
	public RBACDecentralizedImpl(Name domainOwner, boolean autoAddOnGet) {
		super(RBACCore.SESSION_CONTINUE_DOWNGRADE, autoAddOnGet, true);
		domain = new DomainImpl(domainOwner);
	}
	
	public RBACDecentralizedImpl(String domainOwner, int activeSessionStrategy, 
	        boolean autoAddOnGet, boolean isGeneral) {
		super(activeSessionStrategy, autoAddOnGet, isGeneral);
		domain = new DomainImpl(domainOwner);
	}
	
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	public void setDomain(Name domainName) {
	    domain = new DomainImpl(domainName);
	}

	public Domain getDomain() {
		return domain;
	}

	public Role createARole(Name roleName) {
	    return new RoleDecentralizedImpl(domain, roleName);
	}
	
	public void setTrustManager(RBTM trustManager) {
	    this.trustManager = trustManager;
	}
	
	public RBTM getTrustManager() {
	    return trustManager;
	}
    
    private Set getRoleSet(Role aRole) {
        Set		roleSet = new HashSet();
        if (roleExists(aRole.getName())) {
            roleSet.add(aRole);
        }
        // is this a foreign role that we trust?
        else if (trustManager != null && aRole instanceof RoleDecentralized) {
            roleSet.addAll(trustManager.getTrustingLocalRoles(
                    (RoleDecentralized)aRole, domain));
        }
        return roleSet;
    }

    // The following methods override their implementations in 
    // RBACHierarchicalImpl and RBACCoreImpl. These methods are
    // overriden because they receive a role parameter, which
    // can be a RoleDecentralized foreign role. This is expected
    // to be the main access matterns where a foreign role is asked
    // about whether it should be allowed certain permissions. 
    //
    // A tigher integration is possible. For example, all roles can be
    // could be expanded to include the possibility of being a 
    // RoleDecentralized. But this poses a big change to the RBAC
    // standard, and to merge the two implementations might not pay 
    // off. We keep to the decision that RBAC is used for intra domain
    // decisions and RBTM is only used for inter domain role trust
    // relationships. The trust relationships are further consulted
    // for user and permissions operations.
    // 
    // A key method, getAuthorizedRoles (defined in Role), is overriden
    // in RoleHierarchical (to include junior roles), but is not 
    // overriden in RoleDecentralized (which it could have included
    // foreign roles that trust this role), for the reason outlined 
    // above. Instead, it is expected the RBTM is used to explicitly
    // find the role trust relationships. 
    public Set assignedUsers(Role aRole) throws RoleDoesNotExistException {
        Set roleSet = getRoleSet(aRole);
        if (roleSet.isEmpty())
            throw new RoleDoesNotExistException(aRole);
        Set		result = new HashSet();
        for (Iterator i = roleSet.iterator(); i.hasNext();) 
            result.addAll(super.assignedUsers((Role)i.next()));
        return result;
    }

    public Set roleOperationsOnObject(Role aRole, Object anObject)
            throws RoleDoesNotExistException {
        Set roleSet = getRoleSet(aRole);
        if (roleSet.isEmpty())
            throw new RoleDoesNotExistException(aRole);
        Set		result = new HashSet();
        for (Iterator i = roleSet.iterator(); i.hasNext();) 
            result.addAll(super.roleOperationsOnObject((Role)i.next(), anObject));
        return result;
    }

    public Set rolePermissions(Role aRole) throws RoleDoesNotExistException {
        Set roleSet = getRoleSet(aRole);
        if (roleSet.isEmpty())
            throw new RoleDoesNotExistException(aRole);
        Set		result = new HashSet();
        for (Iterator i = roleSet.iterator(); i.hasNext();) 
            result.addAll(super.rolePermissions((Role)i.next()));
        return result;
    }
    
    public Set authorizedUsers(Role aRole) throws RoleDoesNotExistException {
        Set roleSet = getRoleSet(aRole);
        if (roleSet.isEmpty())
            throw new RoleDoesNotExistException(aRole);
        Set		result = new HashSet();
        for (Iterator i = roleSet.iterator(); i.hasNext();) 
            result.addAll(super.authorizedUsers((Role)i.next()));
        return result;
    }
}
