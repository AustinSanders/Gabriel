/*
 * Created on Nov 4, 2005
 *
 */
package c2.fw.secure.xacml;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import c2.fw.secure.rbac.Name;
import c2.fw.secure.rbac.NameImpl;
import c2.fw.secure.tm.Domain;
import c2.fw.secure.tm.DomainImpl;
import c2.fw.secure.tm.RBACDecentralized;
import c2.fw.secure.tm.RBTM;
import c2.fw.secure.tm.RBTMImpl;
import c2.fw.secure.tm.RoleDecentralized;
import c2.fw.secure.tm.RoleExpression;
import c2.fw.secure.tm.TrustDoesNotExistException;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.ParsingException;
import com.sun.xacml.Rule;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;

/**
 * This combines XACML and RBTM
 * 
 * @author Jie Ren
 */
public class RBTMWithXACML implements RBTM {
    RBTM    rbtm = new RBTMImpl(false);
    // Map the domain name to its related RPS/PPS/UA policies. The "" is used 
    // for the default domain. 
    Map		domainToPolicies = new HashMap();
    // Map the domain name to its constructed RBACDecentrazlied
    Map		domainToRBAC = new HashMap();
    
	/**
	 * Construct a set of decentralized RBAC repositories based on a set of 
	 * XACML policies. 
	 * 
	 * If no decentralized role is needed, then the RBACHierarchicalWithXACML 
	 * should be used. If such roles are needed, then RBTMWithXACML, instead of
	 * RBACDecentralziedXACML, should be used as the starting point for 
	 * absorbing XACML policies, because the RBTM is in a better place to 
	 * coordiante the whole process. 
	 * 
	 * @param contents a set of strings. 
	 */
	public RBTMWithXACML(Set contents) {
	    if (contents == null) 
	        return;
	    
		// the first parse of the policies.
		// We use SunXACML as the parsing engine
		DynamicPDP	pdp = new DynamicPDP(new HashSet(), contents);
		Set		policies = pdp.getPolicies();
		for (Iterator i = policies.iterator(); i.hasNext(); ) {
		    // get parsed policy
		    AbstractPolicy	policy = (AbstractPolicy)i.next();
			if (!isTM(policy)) {
			    // Remeber to which domain the policy applies
			    String	domainName = RBACHierarchicalWithXACML.getDomain(policy);
			    Set		domainPolicies = (Set)domainToPolicies.get(domainName);
			    if (domainPolicies==null) {
			        domainPolicies = new HashSet();
			        domainToPolicies.put(domainName, domainPolicies);
			    }
				domainPolicies.add(pdp.getPolicyString(policy.getId()));
			}
		}
		
		// Initialize all RBACs first, use this as the trust manager
		Set		domains = domainToPolicies.keySet();
		for (Iterator i = domains.iterator(); i.hasNext(); ) {
		    String	domainName = DomainImpl.getCanonicalName((String)i.next());
		    Set		policiesForDomain = (Set)domainToPolicies.get(domainName);
		    RBACDecentralized	rbac = new RBACDecentralizedWithXACML(
		            policiesForDomain, domainName);
		    rbac.setTrustManager(this);
		    this.addRBAC(rbac);
		}

		for (Iterator i = policies.iterator(); i.hasNext(); ) {
		    // get parsed policy
		    AbstractPolicy	policy = (AbstractPolicy)i.next();
			if (isTM(policy)) {
			    // Handle trust management policy set
			    processTrustManagement(this, policy);
			}
		}
	}
	
	/**
	 * The trust management policy set scheme
	 */
	public static final String	TM = "TM";
	
	/**
	 * Whether the policy is a trust management policy set
	 * 
	 * @param p the policy set
	 * @return true if the policy set is a trust management policy set
	 */
	public static final boolean isTM(AbstractPolicy p) {
	    return p.getId().getScheme().equals(TM);
	}
	
	/**
	 * Process the policy for trust management rules
	 *
	 * @param rbtm the RBTM engine
	 * @param p the policy
	 */
    public static void processTrustManagement(RBTM rbtm, AbstractPolicy p) {
    	List		rulesOfPolicy = new ArrayList();
    	List		policiesOfPolicy = new ArrayList();
    	XACMLUtils.getAllRulesAndPolicies(p, rulesOfPolicy, policiesOfPolicy);
    	
    	for (Iterator j = rulesOfPolicy.iterator(); j.hasNext(); ) {
    		handleRule(rbtm, (Rule)j.next(), p);
    	}
    }
    
    public void grantTrust(RoleDecentralized local, RoleExpression foreign) {
        throw new UnsupportedException("grantTrust should not be called");
    }

    public void revokeTrust(RoleDecentralized local, RoleExpression foreign) throws TrustDoesNotExistException {
        throw new UnsupportedException("revokeTrust should not be called");
    }

    public Set getTrustedForeignRoles(RoleDecentralized local) {
        return rbtm.getTrustedForeignRoles(local);
    }

    public Set getTrustingForeignRoles(RoleDecentralized local) {
        return rbtm.getTrustingForeignRoles(local);
    }

    public Set getTrustedLocalRoles(RoleDecentralized trustor, Domain trustee) {
        return rbtm.getTrustedLocalRoles(trustor, trustee);
    }

    public Set getTrustingLocalRoles(RoleDecentralized trustee, Domain trustor) {
        return rbtm.getTrustingLocalRoles(trustee, trustor);
    }

    public void addRBAC(RBACDecentralized rbac) {
        rbtm.addRBAC(rbac);
    }

    public RBACDecentralized removeRBAC(RBACDecentralized rbac) {
        return removeRBAC(rbac.getDomain().getName());
    }

    public RBACDecentralized removeRBAC(Name domainName) {
        return rbtm.removeRBAC(domainName);
    }

    public RBACDecentralized getRBAC(Name domainName) {
        RBACDecentralized	rbac = rbtm.getRBAC(domainName);
        if (rbac == null) {
            // always do autoAddOnGet
            // We cannot let the side RBTM do this autoAddOnGet, since it will 
            //		just create a normal RBACDecentralizedImpl
		    Set		policiesForDomain = (Set)domainToPolicies.get(domainName.getValue());
		    if (policiesForDomain==null) 
		        policiesForDomain = new HashSet();
		    rbac = new RBACDecentralizedWithXACML(
		            policiesForDomain, domainName.getValue());
		    rbac.setTrustManager(this);
		    this.addRBAC(rbac);
            
        }
        return rbac;
    }

    public RBACDecentralized getRBAC(RoleDecentralized role) {
        return getRBAC(role.getDomain().getName());
    }

    public Collection getRBACs() {
        return rbtm.getRBACs();
    }
    
    public RBTM getRealRBTM() {
        return rbtm;
    }

    public boolean	checkAccess(Name localUser, Name localDomain, 
			Name foreignDomain, Name operation, Name object) {
        return rbtm.checkAccess(localUser, localDomain, foreignDomain, operation, object);
    }
    
    /**
     * Check whether the access expressed in an XACML request is allowed
     * 
     * @param request the request in XACML format
     * @return true if the access is allowed, false otherwise
     */
	public boolean checkAccess(String request) {
	    // Parse request
        RequestCtx reqCtx = null;
		try {
			reqCtx = RequestCtx.getInstance(new ByteArrayInputStream(
			        request.getBytes()));
		} 
		catch (ParsingException e) {
			e.printStackTrace();
		}
		// Get invoking domain (by checking domain attribute of subject)
		String	invokingDomain = null;
        Set subjects = reqCtx.getSubjects();
        for (Iterator i = subjects.iterator(); i.hasNext() && 
        		invokingDomain == null; ) {
            invokingDomain = XACMLUtils.getRequestAttribute(
                ((Subject)i.next()).getAttributes(), XACMLUtils.XADL_DOMAIN_NAME);
        }
        // Get deciding domain (by checking domain attribute of resource or action)
	    String decidingDomain = XACMLUtils.getRequestAttribute(
                    reqCtx.getResource(), XACMLUtils.XADL_DOMAIN_NAME);
		if (decidingDomain == null) {
            decidingDomain = XACMLUtils.getRequestAttribute(
                reqCtx.getAction(), XACMLUtils.XADL_DOMAIN_NAME);
        }
		// whether the two domains are of the same 
        decidingDomain = DomainImpl.getCanonicalName(decidingDomain);
        invokingDomain = DomainImpl.getCanonicalName(invokingDomain);
        // Get where the decision should be made
        RBACDecentralizedWithXACML	rbac = (RBACDecentralizedWithXACML)rbtm.getRBAC(new NameImpl(decidingDomain));
        boolean		result = false;
        if (invokingDomain.equals(decidingDomain)) {
            // This will result in a regular domain-less check
            result = rbac.checkAccess(null, request); 
        }
        else {
            // This will result in a trust management enabled check
            result = rbac.checkExternalAccess(request);
        }
        return result;
	}

    public static boolean handleRule(RBTM rbtm, Rule r, AbstractPolicy p) {
        boolean		handled = false;
        if (!handled) {
			String		action = XACMLUtils.getFirstMatchString(r.getTarget().getActions());
			if (action.equals(XACMLUtils.ACTION_TRUST_ROLE)) {
			    try {
			        List		subjects = r.getTarget().getSubjects();
			        List		resources = r.getTarget().getResources();
				    String		trustingDomain = DomainImpl.getCanonicalName(
				            		XACMLUtils.getMatchString(subjects, 
				            		        XACMLUtils.XADL_DOMAIN_NAME));
				    String		trustingRole = XACMLUtils.getMatchString(subjects, 
				            		XACMLUtils.RBAC_ATTRIBUTE_ID);
				    String		trustedDomain = DomainImpl.getCanonicalName(
				            		XACMLUtils.getMatchString(resources, 
				            		        XACMLUtils.XADL_DOMAIN_NAME));
				    String		trustedRole = XACMLUtils.getMatchString(resources, 
		            				XACMLUtils.RBAC_ATTRIBUTE_ID);
				    RBACDecentralized	trustingRBAC = rbtm.getRBAC(new NameImpl(trustingDomain));
				    RBACDecentralized	trustedRBAC = rbtm.getRBAC(new NameImpl(trustedDomain));
				    RoleDecentralized 	trusting = (RoleDecentralized)
				    			trustingRBAC.getRole(new NameImpl(trustingRole));
				    RoleDecentralized 	trusted = (RoleDecentralized)
	    						trustedRBAC.getRole(new NameImpl(trustedRole));
				    rbtm.getRealRBTM().grantTrust(trusting, trusted);
				}
	        	catch (Exception e) {
	        		e.printStackTrace();
	        	}
        	}
        }
        return handled;
    }

	public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: -config <request>");
            System.out.println("       <request> <policy> [policies]");
            System.exit(1);
        }
        
        DynamicPDP simplePDP = null;
        String requestFile = null;
        
        if (args[0].equals("-config")) {
            requestFile = args[1];
            simplePDP = new DynamicPDP();
        } else {
            requestFile = args[0];
            String [] policyFiles = new String[args.length - 1];
            
            for (int i = 1; i < args.length; i++)
                policyFiles[i-1] = args[i];

            Set		p = new HashSet();
            for (int i = 0; i<policyFiles.length; i++) {
                p.add(XACMLUtils.getFileAsString(policyFiles[i]));
            }
            RBTMWithXACML rbtm = new RBTMWithXACML(p);
            
            if (rbtm.checkAccess(XACMLUtils.getFileAsString(requestFile))) {
            //if (rbtm.checkAccess("Anne", "sign", "purchase order")) {
                System.out.println("True");
            }
            else {
                System.out.println("False");
            }

            try {
                Set		domains = new HashSet();
                domains.add(new NameImpl(DomainImpl.DOMAIN_DEFAULT));
                domains.add(new NameImpl("me"));
                domains.add(new NameImpl("urn:xadl:domain:name:other"));
                for (Iterator k = domains.iterator(); k.hasNext(); ) {
	                RBACDecentralized me = rbtm.getRBAC((Name)k.next());
	                Set		rolesOfMe = me.getRoles();
	                for (Iterator i = rolesOfMe.iterator(); i.hasNext();) {
	                    RoleDecentralized rd = (RoleDecentralized)i.next();
	        		    Set		r = rbtm.getTrustedForeignRoles(rd);
	        		    for (Iterator j = r.iterator(); j.hasNext(); ) {
	        		        System.out.println(j.next());
	        		    }
	        		    Set		rr = rbtm.getTrustingForeignRoles(rd);
	        		    for (Iterator j = rr.iterator(); j.hasNext(); ) {
	        		        System.out.println(j.next());
	        		    }
	                }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
    	}
    }
}
