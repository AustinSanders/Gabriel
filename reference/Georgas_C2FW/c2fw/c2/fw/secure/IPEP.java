/*
 * Created on Jul 18, 2005
 *
 */
package c2.fw.secure;

import c2.fw.secure.xacml.DynamicPDP;

/**
 * This models a Policy Enforcement Point (PEP). It has a Policy Decision Point 
 * that it should consult to.
 * 
 * @author Jie Ren
 */
public interface IPEP {
    /**
     * Set	the Policy Decision Point (PDP) for this Policy Enforcement Point (PEP)
     * @param pdp
     */
    void 	setPDP(DynamicPDP pdp);
    
    /**
     * Get the PDP
     * @return the pdp
     */
    DynamicPDP	getPDP();
}
