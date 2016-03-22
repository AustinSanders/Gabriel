/*
 * Created on Nov 4, 2005
 *
 */
package c2.fw.secure.xacml;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import c2.fw.secure.rbac.Name;
import c2.fw.secure.rbac.NameImpl;
import c2.fw.secure.tm.Domain;
import c2.fw.secure.tm.DomainImpl;
import c2.fw.secure.tm.RBACDecentralized;
import c2.fw.secure.tm.RBACDecentralizedImpl;
import c2.fw.secure.tm.RBTM;
import c2.fw.secure.tm.RoleDecentralized;

import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;

public class RBACDecentralizedWithXACML extends RBACHierarchicalWithXACML implements RBACDecentralized {
    RBACDecentralized		rbacd;
    
    public RBACDecentralizedWithXACML(Set content, String domainName) {
        super(content, domainName);
    }
    
    public RBACDecentralizedWithXACML() {
        super(null, null);
    }

	protected void initRBAC(String domainName) {
	    // instantiate a more powerful side RBAC
	    rbac = new RBACDecentralizedImpl(domainName, true);
	    // keep a rerence to it
	    rbacd = (RBACDecentralized)rbac;
	}
	
	// use the more powerful side RBAC
    public void setDomain(Domain domain) {
        rbacd.setDomain(domain);
    }

    public void setDomain(Name domainName) {
        rbacd.setDomain(domainName);
    }

    public Domain getDomain() {
        return rbacd.getDomain();
    }

    public void setTrustManager(RBTM trustManager) {
        rbacd.setTrustManager(trustManager);
    }

    public RBTM getTrustManager() {
        return rbacd.getTrustManager();
    }
    
    public boolean checkExternalAccess(String request) {
        // Parse the XACML request
        RequestCtx reqCtx = null;
		try {
			reqCtx = RequestCtx.getInstance(new ByteArrayInputStream(
			        request.getBytes()));
		} 
		catch (ParsingException e) {
			e.printStackTrace();
		}
        // Get deciding domain
	    String decidingDomain = XACMLUtils.getRequestAttribute(
                    reqCtx.getResource(), XACMLUtils.XADL_DOMAIN_NAME);
		if (decidingDomain == null) {
            decidingDomain = XACMLUtils.getRequestAttribute(
                reqCtx.getAction(), XACMLUtils.XADL_DOMAIN_NAME);
        }
        decidingDomain = DomainImpl.getCanonicalName(decidingDomain);
        // It's not in our domain, return false
        if (!getDomain().getNameString().equals(decidingDomain))
            return false;
        
		// Get invoking domain
		String	invokingDomain = null;
        Set subjects = reqCtx.getSubjects();
        for (Iterator i = subjects.iterator(); i.hasNext() && 
        		invokingDomain == null; ) {
            invokingDomain = XACMLUtils.getRequestAttribute(
                ((Subject)i.next()).getAttributes(), XACMLUtils.XADL_DOMAIN_NAME);
        }
        invokingDomain = DomainImpl.getCanonicalName(invokingDomain);
        // Get invoking user
        String 		userName = "";
        for (Iterator i = subjects.iterator(); i.hasNext(); ) {
            Subject s = (Subject)i.next();
            Set attributes = s.getAttributes();
            String user = XACMLUtils.getRequestAttribute(attributes, XACMLUtils.SUBJECT_ID);
            if (user != null) {
                userName = user;
                break;  
            }
        }

        Name	localDomain = new NameImpl(decidingDomain);
        Name	foreignDomain = new NameImpl(invokingDomain);
        Name	foreignUser = new NameImpl(userName);
        RBTM	rbtm = getTrustManager();
        RBACDecentralized	foreign = rbtm.getRBAC(foreignDomain);
        RBACDecentralized	local = rbtm.getRBAC(localDomain);
    	try {
	        Set	foreignRoles = foreign.assingedRoles(foreign.getUser
	                				(foreignUser));
	        boolean		result = false;
	        for (Iterator i = foreignRoles.iterator(); i.hasNext() && !result; ) {
	            // each role this user can play
	            RoleDecentralized	forignRole = (RoleDecentralized)i.next();
	            Set		localRoles = rbtm.getTrustingLocalRoles(forignRole, local.getDomain());
	            for (Iterator j = localRoles.iterator(); j.hasNext() && !result; ) {
	                // The local roles that trust one of the foreign user's role
	                RoleDecentralized localRole = (RoleDecentralized)j.next();
        			// For this role, find its RPS
        			String	rolePolicy = (String)rps.get(localRole.getNameString());
        			Set		p = new HashSet();
        			p.add(rolePolicy);
        			// Put other policies as potentially reference 
        			Set		potentialPolicies = new HashSet(policies);
        			potentialPolicies.remove(rolePolicy);
        			// Now we can set up the PDP
        			DynamicPDP	pdp = new DynamicPDP(p, potentialPolicies, localRoles);
        			result = pdp.evaluate(request);
        			// If any of the role policy allows the request, we are done
        			// There can be other semantics, but this might be the most
        			// straighforward
        			if (result == true)
        			    return true;
            	}
            }
        }
    	catch (Exception e) {
    		e.printStackTrace();
    	}
        return false;
    }
}