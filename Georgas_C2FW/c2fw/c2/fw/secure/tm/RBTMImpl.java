/*
 * Created on Oct 25, 2005
 *
 */
package c2.fw.secure.tm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import c2.fw.secure.rbac.Name;
import c2.fw.secure.rbac.NameImpl;
import c2.fw.secure.rbac.ObjectImpl;
import c2.fw.secure.rbac.OperationImpl;
import c2.fw.secure.rbac.Permission;
import c2.fw.secure.rbac.User;

/**
 * This is a wrapper of the CredentialManager of the original RBTM framework.
 * It answers the interesting queries about trusting relationships. 
 * 
 * @author Jie Ren
 */
public class RBTMImpl implements RBTM {
	protected	CredentialManager	cm = new CredentialManager();
	protected	boolean				autoAddOnGet = false;;		
	
	public RBTMImpl() {
	}
	
	public RBTMImpl(boolean autoAddOnGet) {
	    this();
	    this.autoAddOnGet = autoAddOnGet;
	}
	
	public void grantTrust(RoleDecentralized local, RoleExpression foreign) {
		StaticCredential credential = new StaticCredential(local, foreign);
		cm.addCredential(credential);
	}

	public void revokeTrust(RoleDecentralized local, RoleExpression foreign) throws TrustDoesNotExistException {
		StaticCredential credential = new StaticCredential(local, foreign);
		if (!cm.containsCredential(credential))
			throw new TrustDoesNotExistException(local, foreign);

		cm.removeCredential(credential);
	}

	public Set getTrustedForeignRoles(RoleDecentralized local) {
		Set		result = cm.backwardSearch(local).resultSet(); 
		return result;
	}
	
	public Set getTrustingForeignRoles(RoleDecentralized local) {
		Set		result = cm.forwardSearch(local).resultSet(); 
		return result;
	}

    public Set getTrustedLocalRoles(RoleDecentralized trustor, Domain trustee) {
        Set		result = new HashSet();
        Set		trusted = getTrustedForeignRoles(trustor);
        for (Iterator i = trusted.iterator(); i.hasNext(); ) {
            RoleDecentralized	r = (RoleDecentralized)i.next();
            if (r.getDomain().equals(trustee)) {
                result.add(r);
            }
        }
        return result;
    }
	
    public Set getTrustingLocalRoles(RoleDecentralized trustee, Domain trustor) {
        Set		result = new HashSet();
        Set		trusting = getTrustingForeignRoles(trustee);
        for (Iterator i = trusting.iterator(); i.hasNext(); ) {
            RoleDecentralized	r = (RoleDecentralized)i.next();
            if (r.getDomain().equals(trustor)) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Maps a domain name of a RBACDecentralized to the RBACDecentrazlized for retrieval
     */
    protected Map	nameToRBAC = new HashMap();
    
    public void addRBAC(RBACDecentralized rbac) {
        // A default domain's name should be Domain.DOMAIN_DEFAULT
        Name		domainName = rbac.getDomain().getName();
        if (!nameToRBAC.containsKey(domainName)) {
            nameToRBAC.put(domainName, rbac);
        }
    }

    public RBACDecentralized removeRBAC(RBACDecentralized rbac) {
        Name		domainName = rbac.getDomain().getName();
        return removeRBAC(domainName);
    }

    public RBACDecentralized removeRBAC(Name domainName) {
        return (RBACDecentralized)nameToRBAC.remove(domainName);
    }

    public RBACDecentralized getRBAC(Name domainName) {
        RBACDecentralized result = (RBACDecentralized)nameToRBAC.get(domainName);
        if (result==null && autoAddOnGet) {
            result = new RBACDecentralizedImpl(domainName, true);
            result.setTrustManager(this);
            addRBAC(result);
        }
        return result;
    }

    public RBACDecentralized getRBAC(RoleDecentralized role) {
        Name		domainName = role.getDomain().getName();
        return getRBAC(domainName);
    }

    public Collection getRBACs() {
        return nameToRBAC.values();
    }
    
    public RBTM getRealRBTM() {
        return this;
    }
    
    public boolean	checkAccess(Name localUser, Name localDomain, 
			Name foreignDomain, Name operation, Name object) {
        boolean result = false;
        try {
	        if (localDomain.equals(foreignDomain)) {
	            // An intra-domain check
	            RBACDecentralized	rbac = getRBAC(localDomain);
	            result = rbac.checkAccess(rbac.getUser(localUser), 
	                    new OperationImpl(operation), new ObjectImpl(object));
	        }
	        else {
	            // An inter-domain check
	            RBACDecentralized	local = getRBAC(localDomain);
	            RBACDecentralized	foreign = getRBAC(foreignDomain);
	            Set					localRoles = local.assingedRoles(local.getUser
	                    				(localUser));
	            for (Iterator i = localRoles.iterator(); i.hasNext() && !result; ) {
	                // each role this user can play
	                RoleDecentralized	localRole = (RoleDecentralized)i.next();
	                Set		foreignRoles = getTrustingLocalRoles(localRole, foreign.getDomain());
	                for (Iterator j = foreignRoles.iterator(); j.hasNext() && !result; ) {
	                    // The foreign roles that trust one of user's role
	                    RoleDecentralized foreignRole = (RoleDecentralized)j.next();
	                    result = foreign.checkAccess(foreignRole, 
	                            new OperationImpl(operation), new ObjectImpl(object));
	                }
	            }
	        }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
	public static void main(String[] args) {
	    try {
		    RBTM	rbtm = new RBTMImpl();
		    RBACDecentralized	me = new RBACDecentralizedImpl("me");
		    RBACDecentralized	other = new RBACDecentralizedImpl("other");
		    RBACDecentralized	other2 = new RBACDecentralizedImpl("other2");
		    RBACDecentralized	other3 = new RBACDecentralizedImpl("other3");
		    RBACDecentralized	other4 = new RBACDecentralizedImpl("other4");
		    me.setTrustManager(rbtm);
		    rbtm.addRBAC(me);
		    
		    RoleDecentralized	mother = (RoleDecentralized)me.addRole(new NameImpl("mother"));
		    RoleDecentralized	grandma = (RoleDecentralized)me.addRole(new NameImpl("grandma"));
		    RoleDecentralized	father = (RoleDecentralized)me.addRole(new NameImpl("father"));
		    RoleDecentralized	foreign = (RoleDecentralized)other.addRole(new NameImpl("dad"));
		    RoleDecentralized	foreign2 = (RoleDecentralized)other2.addRole(new NameImpl("mydad"));
		    RoleDecentralized	foreign22 = (RoleDecentralized)other2.addRole(new NameImpl("dad22"));
		    RoleDecentralized	foreign3 = (RoleDecentralized)other3.addRole(new NameImpl("daddad"));
		    RoleDecentralized	foreign33 = (RoleDecentralized)other3.addRole(new NameImpl("dad33"));
		    RoleDecentralized	foreign4 = (RoleDecentralized)other4.addRole(new NameImpl("daddad"));
		    LinkedRole			lr = new LinkedRole(foreign2, "daddad");
		    me.addInheritance(mother, grandma);
		    User g = me.addUser(new NameImpl("hh"));
		    Permission t = me.createPermission(new OperationImpl("reside"), new ObjectImpl("house"));
		    me.assignUser(g, grandma);
		    me.grantPermission(t, grandma);
		    rbtm.grantTrust(father, foreign);
		    rbtm.grantTrust(mother, foreign);
		    rbtm.grantTrust(foreign, foreign2);
		    rbtm.grantTrust(foreign2, foreign3);
		    rbtm.grantTrust(foreign2, foreign33);
		    rbtm.grantTrust(foreign2, foreign4);
		    rbtm.grantTrust(foreign22, foreign3);
		    rbtm.grantTrust(father, lr);
		    Set		r = rbtm.getTrustedForeignRoles(father);
		    for (Iterator i = r.iterator(); i.hasNext(); ) {
		        System.out.println(i.next());
		    }
		    Set		rr = rbtm.getTrustingForeignRoles(foreign3);
		    for (Iterator i = rr.iterator(); i.hasNext(); ) {
		        System.out.println(i.next());
		    }
		    Set		p = me.rolePermissions(foreign2);
		    for (Iterator i = p.iterator(); i.hasNext(); ) {
		        System.out.println(i.next());
		    }
		    rbtm.revokeTrust(mother, foreign);
		    p = me.rolePermissions(foreign2);
		    for (Iterator i = p.iterator(); i.hasNext(); ) {
		        System.out.println(i.next());
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
