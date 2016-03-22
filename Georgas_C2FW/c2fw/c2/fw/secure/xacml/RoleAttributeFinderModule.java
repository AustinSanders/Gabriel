/*
 * Created on Nov 1, 2005
 *
 */
package c2.fw.secure.xacml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import c2.fw.secure.rbac.NameImpl;
import c2.fw.secure.rbac.RBACCore;
import c2.fw.secure.rbac.Role;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.finder.AttributeFinderModule;

public class RoleAttributeFinderModule extends AttributeFinderModule
{
    // The rbac from where to get role attributes
	RBACCore	rbac;
	static 	Set			roleURI = new HashSet();
	static 	URI			subjectIdentifier;
	static {
    	try {
			URI		role = new URI(XACMLUtils.RBAC_ATTRIBUTE_ID);
			roleURI.add(role);
			subjectIdentifier = new URI(XACMLUtils.SUBJECT_ID);			
		} catch (URISyntaxException e) {
			roleURI = null;
		}
	}
	
	/**
	 * Create a role attribute finder module based on a RBAC. This is used 
	 * in a single domain RBAC to find the roles when given a user name.
	 * 
	 * @param rbac the RBAC used to find the roles for a user
	 */
	public RoleAttributeFinderModule(RBACCore rbac) {
		this.rbac = rbac;
	}
	
	Set		suppliedRoles;
	/**
	 * Create a role attribute finder module based on a given set of roles. This
	 * is used in a cross domain RBTM to find what roles a remote user can play
	 * in a local domain. It is possible to use the rbac to find its trust manager
	 * and find what roles are available, but context information is lost (there
	 * are two domains involved, so a single domain RBAC is insufficient), thus
	 * we choose to let the RBTM trust manager supply the roles when it calculates
	 * the initial trusting relationships, a step that is necessary anyway to find
	 * the appropriate role policy.
	 * 
	 * @param suppliedRoles a set of supplied roles
	 */
	public RoleAttributeFinderModule(Set suppliedRoles) {
		this.suppliedRoles = suppliedRoles;
	}
	
    public boolean isDesignatorSupported() {
        return true;
    }

    public Set getSupportedDesignatorTypes() {
    	Set		s = new HashSet();
    	s.add(new Integer(AttributeDesignator.SUBJECT_TARGET));
        return s;
    }

    public Set getSupportedIds() {
        return roleURI;
    }

    public EvaluationResult findAttribute(URI attributeType, URI attributeId,
            URI issuer, URI subjectCategory,
            EvaluationCtx context,
            int designatorType) {
    	// Copied from TestAttributeFinderModule
    	//
        // make sure this is the subject attribute we support
        if (designatorType != AttributeDesignator.SUBJECT_TARGET)
            return new EvaluationResult(BagAttribute.
                                        createEmptyBag(attributeType));

        // make sure this is the identifier we support
        if (!attributeId.toString().equals(XACMLUtils.RBAC_ATTRIBUTE_ID))
            return new EvaluationResult(BagAttribute.
                                        createEmptyBag(attributeType));

        // make sure we've been asked for a string or a URI
        String		askedType = attributeType.toString();
        if (!askedType.equals(StringAttribute.identifier) && 
        	!askedType.equals(AnyURIAttribute.identifier) )
            return new EvaluationResult(BagAttribute.
                                        createEmptyBag(attributeType));

        // retrieve the subject identifer from the context, whatever its type is
        EvaluationResult result =
            context.getSubjectAttribute(null, subjectIdentifier,
                                        issuer, subjectCategory);
        if (result.indeterminate())
            return result;

        // check that we succeeded in getting the subject identifier
        BagAttribute bag = (BagAttribute)(result.getAttributeValue());
        if (bag.isEmpty()) {
            ArrayList code = new ArrayList();
            code.add(Status.STATUS_MISSING_ATTRIBUTE);
            Status status = new Status(code, "missing subject-id");
            return new EvaluationResult(status);
        }
        
        // finally, look for the subject who has the role-mapping defined,
        // and if they're the identified subject, add their role
        BagAttribute returnBag = null;
        Iterator it = bag.iterator();
        while (it.hasNext()) {
            StringAttribute attr = (StringAttribute)(it.next());
            Set 			set = new HashSet();
			try {
			    Set 		roles = suppliedRoles;
			    if (roles == null) {
			        // We are not given a set of roles, so use RBAC to find it out
				    // the user name
		            String			user = attr.getValue();
		            // the roles assigned
		            roles = rbac.assingedRoles(rbac.getUser(new NameImpl(user)));
			    }
	            for (Iterator i = roles.iterator(); i.hasNext(); ) {
		            // return them in proper type
	            	if (askedType.equals(StringAttribute.identifier)) {  
	            		set.add(new StringAttribute(((Role)i.next()).getNameString()));
	            	}
	            	else {
	            		set.add(new AnyURIAttribute(
	            				new URI(((Role)i.next()).getNameString())));
	            	}
	            }
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
            returnBag = new BagAttribute(attributeType, set);
            break;
        }

        return new EvaluationResult(returnBag);
    }
}
