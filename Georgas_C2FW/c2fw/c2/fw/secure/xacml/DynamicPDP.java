/*
 * Created on Jul 14, 2005
 *
 */
package c2.fw.secure.xacml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import c2.fw.secure.IndeterminateDecisionException;
import c2.fw.secure.rbac.RBACCore;

import com.sun.xacml.ConfigurationStore;
import com.sun.xacml.Indenter;
import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.ParsingException;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.impl.CurrentEnvModule;
import com.sun.xacml.finder.impl.FilePolicyModule;
import com.sun.xacml.finder.impl.SelectorModule;


/**
 * Adapted from SimplePDP
 * 
 */
public class DynamicPDP
{
    // The real pdp
    PDP 					pdp = null;
    // The policies
    String					allPolicies = "";
    PolicyFinder			policyFinder;
    StringPolicyModule		stringPolicyModule;

    // Set up a bypass for RBAC
    RBACCore				rbac;
    // Set up a set of supplied roles
    Set						suppliedRoles;
    
    public DynamicPDP(Set policies, Set potentialPolicies) {
    	initWithStrings(policies, potentialPolicies);
    }
    
    public DynamicPDP(Set policies, Set potentialPolicies, RBACCore rbac) {
    	this.rbac = rbac;
    	initWithStrings(policies, potentialPolicies);
    }

    public DynamicPDP(Set policies, Set potentialPolicies, Set suppliedRoles) {
    	this.suppliedRoles = suppliedRoles;
    	initWithStrings(policies, potentialPolicies);
    }

    /**
     * Initialize the pdp with sets of strings
     * 
     * @param policies				the set of strings for policies
     * @param potentialPolicies		the set of strings possibly referenced by 
     * the above set
     */
    protected void initWithStrings(Set policies, Set potentialPolicies) {
        stringPolicyModule = new StringPolicyModule(policies, potentialPolicies);

        policyFinder = new PolicyFinder();
        Set policyModules = new HashSet();
        policyModules.add(stringPolicyModule);
        policyFinder.setModules(policyModules);

        CurrentEnvModule envAttributeModule = new CurrentEnvModule();
        SelectorModule selectorAttributeModule = new SelectorModule();

        AttributeFinder attributeFinder = new AttributeFinder();
        List attributeModules = new ArrayList();
        attributeModules.add(envAttributeModule);
        attributeModules.add(selectorAttributeModule);
        if (rbac != null) {
            // if we are given an RBAC, we will use it to find the role attribute
            // for subjects(users)
	        RoleAttributeFinderModule		roleModule = new RoleAttributeFinderModule(rbac); 
	        attributeModules.add(roleModule);
        }
        if (suppliedRoles != null) {
            // if we are given a set of roles, we will use them as the roles 
            // of subjects(users)
	        RoleAttributeFinderModule		roleModule = new RoleAttributeFinderModule(suppliedRoles); 
	        attributeModules.add(roleModule);
        }
        attributeFinder.setModules(attributeModules);

        for (Iterator i = policies.iterator(); i.hasNext(); ) {
            allPolicies += (String)i.next(); 
        }
        for (Iterator i = potentialPolicies.iterator(); i.hasNext(); ) {
            allPolicies += (String)i.next(); 
        }
        
        //FunctionFactoryProxy proxy =
        //    StandardFunctionFactory.getNewFactoryProxy();
        //FunctionFactory factory = proxy.getConditionFactory();
        //factory.addFunction(new TimeInRangeFunction());
        //FunctionFactory.setDefaultFactory(proxy);

        pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null));
    }
    
    /**
     * Get all policies associated with the pdp
     * 
     * @return the set of all string policies
     */
    public Set	getPolicies() {
        return policyFinder.getPolicies();
    }

    /**
     * Get the policy string for a policy 
     * 
     * @param policyId the PolicySetId
     * @return	the string corresponding to the policyId, or null if no such string
     */
    public String getPolicyString(URI policyId) {
        return stringPolicyModule.getPolicyString(policyId);
    }
    
    // Since we assume the policy should always give an answer, 
    // so true means permit, false means deny.
    // If we cannot find an answer, we throw an exception
    /**
     * Evaluate a request against the policies
     * 
     * @param request the XML-formated string for the request
     * @return true if the request if permitted, false if it is denied
     * @throws IndeterminateDecisionException if we cannot find an answer
     */
    public boolean evaluate(String request)
    {
		boolean		result = false;
        // Here is the combination of a regular pdp and a RBAC
        // The current algorithm is if RBAC grants, then it is granted,
		// Otherwise, the regular PDP takes over
        if (secondary != null ) {
            result = secondary.checkAccess(null, request);
        }
        if (result)
            return result;
        
        RequestCtx reqCtx = null;
		try {
			reqCtx = RequestCtx.getInstance(new ByteArrayInputStream(request.getBytes()));
		} 
		catch (ParsingException e) {
			e.printStackTrace();
		}
        Set	results = pdp.evaluate(reqCtx).getResults();
        for (Iterator i = results.iterator(); i.hasNext(); ) {
        	Result r = (Result)i.next();
        	int	decision = r.getDecision();
        	if (decision==Result.DECISION_PERMIT)
        		result = true;
        	else if (decision==Result.DECISION_DENY)
        		result = false;
        	else if (decision==Result.DECISION_NOT_APPLICABLE)
        	    result = false;
        	else if (decision==Result.DECISION_INDETERMINATE)
                throw new IndeterminateDecisionException(request, allPolicies);
        }
        return result;
    }
    
    protected RBACHierarchicalWithXACML	secondary = null;
    
    public void setSecondaryRBAC(RBACHierarchicalWithXACML secondary) {
        this.secondary = secondary;
    }

    /**
     * Evalute a request in a file
     * @param requestFile	the file name of the request
     * @return the response context from the evaluation
     * @throws IOException
     * @throws ParsingException
     */
    public ResponseCtx evaluateFile(String requestFile) throws IOException,
			ParsingException {
		// setup the request based on the file
		RequestCtx request = RequestCtx.getInstance(new FileInputStream(
				requestFile));
		
		// evaluate the request
		return pdp.evaluate(request);
    }

    // copied from SimplePDP for testing with files
    public DynamicPDP(String [] policyFiles) throws Exception {
        // Create a PolicyFinderModule and initialize it...in this case,
        // we're using the sample FilePolicyModule that is pre-configured
        // with a set of policies from the filesystem
        FilePolicyModule filePolicyModule = new FilePolicyModule();
        for (int i = 0; i < policyFiles.length; i++)
            filePolicyModule.addPolicy(policyFiles[i]);

        // next, setup the PolicyFinder that this PDP will use
        policyFinder = new PolicyFinder();
        Set policyModules = new HashSet();
        policyModules.add(filePolicyModule);
        policyFinder.setModules(policyModules);

        // now setup attribute finder modules for the current date/time and
        // AttributeSelectors (selectors are optional, but this project does
        // support a basic implementation)
        CurrentEnvModule envAttributeModule = new CurrentEnvModule();
        SelectorModule selectorAttributeModule = new SelectorModule();

        // Setup the AttributeFinder just like we setup the PolicyFinder. Note
        // that unlike with the policy finder, the order matters here. See the
        // the javadocs for more details.
        AttributeFinder attributeFinder = new AttributeFinder();
        List attributeModules = new ArrayList();
        attributeModules.add(envAttributeModule);
        attributeModules.add(selectorAttributeModule);
        attributeFinder.setModules(attributeModules);

        // finally, initialize our pdp
        pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null));
    }

    public DynamicPDP() {
		try {
	        ConfigurationStore store = new ConfigurationStore();
	        store.useDefaultFactories();
	        pdp = new PDP(store.getDefaultPDPConfig());
		} 
		catch (ParsingException e) {
			e.printStackTrace();
		}
		catch (UnknownIdentifierException e) {
			e.printStackTrace();
		}
    }

    // initialize with file names
    private DynamicPDP(String [] policyFiles, boolean fileName) throws Exception {
        if (!fileName)
            return;
        
    	Set		rps = new HashSet();
    	Set		pps = new HashSet();
    	for (int i = 0; i<policyFiles.length; i++) {
    		String	content = "";
    		BufferedReader bf = new BufferedReader(new FileReader(new File(policyFiles[i])));
    		String		l = bf.readLine();
    		while (l != null) {
    			content += l;
    			l = bf.readLine();
    		}
    		if (policyFiles[i].indexOf("rps") != -1)
    			rps.add(content);
    		else
    			pps.add(content);
    	}
    	initWithStrings(rps, pps);
    }

    // test
    public static void main(String [] args) throws Exception {
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

            simplePDP = new DynamicPDP(policyFiles, true);
        }

        // evaluate the request
        ResponseCtx response = simplePDP.evaluateFile(requestFile);

        // for this sample program, we'll just print out the response
        response.encode(System.out, new Indenter());
    }
}

