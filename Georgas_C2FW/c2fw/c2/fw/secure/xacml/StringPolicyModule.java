/*
 * Created on Jul 14, 2005
 *
 */
package c2.fw.secure.xacml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.MatchResult;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicySet;
import com.sun.xacml.VersionConstraints;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;

/**
 * Adapted from FilePolicyModule
 */
public class StringPolicyModule extends PolicyFinderModule
    implements ErrorHandler
{
    // schema validation
    public static final String POLICY_SCHEMA_PROPERTY =
        "com.sun.xacml.PolicySchema";
    public static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";
    public static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";
    private File schemaFile;

    // the owner finder
    private PolicyFinder finder;
    
    // active policiy strings
    private Set activePolicyStrings = new HashSet();
    
    // active policies
    private Set activePolicies;

    // other policies, such as policy from the type, that might be referenced
    private Set potentialPolicyStrings = new HashSet();
    private Map potentialPolicies = new HashMap();

    // map from the policy id to the original string of the policy
    // it includes both the potentialPolicies map and the activePolicies set above
    private Map idToPolicy = new HashMap();

    private static final Logger logger =
        Logger.getLogger(StringPolicyModule.class.getName());

    /**
     * Create an empty module
     */
    public StringPolicyModule() {
        activePolicies = new HashSet();

        String schemaName = System.getProperty(POLICY_SCHEMA_PROPERTY);

        if (schemaName == null)
            schemaFile = null;
        else
            schemaFile = new File(schemaName);
    }

    /**
     * Create a module with a schema file
     * 
     * @param schemaFile the schema file for validation
     */
    public StringPolicyModule(File schemaFile) {
        activePolicies = new HashSet();

        this.schemaFile = schemaFile;
    }

    // Surprisingly this architecture solves two problems of the RBAC profile:
    //		1) An evaluation should evaluate against RPS, not PPS. It is solved
    //			by putting RPS in activePolicyStrings, and PPS in potentialPolicyStrings
    //		2) An evaluation might miss the intended RPS. It is solved by putting
    //			ALL RPS in activePolicyStrings, and each of them has a chance to be
    //			picked up
    // As an side effect, it can incur a problem when an subject has multiple 
    //		roles, and thus multiple policies apply. XACML does not allow this.
    // If the multiple roles are inheriting, then the most senior role can be 
    //		decided, and its policy can be used. However, the multiple roles
    //		can be unrelated, and they must be picked carefully for evaluation.
    //		But this picking process is the exact reason for the problem 2)
    // So, it might be inevitable to incur certain problems, when grouping 
    //		policies one way or the other. We could choose to group them together
    //		so a subject with a single role can automatically get its permissions.
    // But in the end, we choose to use a side RBAC implementation (which we 
    //		have finished) to record the roles and the policies, and pick up
    //		each role's policy individually, so we will not have a 
    //		multi-applicalbe situation. We also have a RoleAttributeFinderModule
    //		that uses the side RBAC implementation to supply all roles for
    //		requests, even if the original request has no role associated with it
    
    /**
     * Create a policy module and populate it with policy strings
     * 
     * @param activePolicyStrings		a set of strings of active policies
     * @param potentialPolicyStrings	a set of strings of policies referenced 
     * by the above set
     */
    public StringPolicyModule(Set activePolicyStrings, Set potentialPolicyStrings) {
        this();
        
        this.activePolicyStrings.addAll(activePolicyStrings);
        this.potentialPolicyStrings.addAll(potentialPolicyStrings);
    }

    public boolean isRequestSupported() {
        return true;
    }

    public void init(PolicyFinder finder) {
        this.finder = finder;
        this.finder.toString();

        Iterator it = activePolicyStrings.iterator();
        while (it.hasNext()) {
            String onePolicy = (String)(it.next());
            AbstractPolicy policy = loadPolicy(onePolicy, finder,
                                               schemaFile, this);
            if (policy != null) {
                // record this active policy
                activePolicies.add(policy);
                idToPolicy.put(policy.getId(), onePolicy);
            }
        }

        // Get ready for potential policies
        Iterator itp = potentialPolicyStrings.iterator();
        while (itp.hasNext()) {
            String onePolicy = (String)(itp.next());
            AbstractPolicy policy = loadPolicy(onePolicy, finder,
                                               schemaFile, this);
            if (policy != null) {
                // record this potential policy
                potentialPolicies.put(policy.getId(), policy);
                idToPolicy.put(policy.getId(), onePolicy);
            }
        }
    }

    /**
     * Add a policy 
     * 
     * @param onePolicy the policy to add. It would not be immediately parsed.
     * 
     * @return true if the policy is new, false if it is already added
     */
    /*
    public boolean addPolicy(String onePolicy) {
        return activePolicyStrings.add(onePolicy);
    }
    */

    /**
     * Get the string form of a policy
     * 
     * @param id the identifier of the policy
     * @return the string that has this identifier. It could be null if no such string
     */
    public String getPolicyString(URI id) {
        return (String)idToPolicy.get(id);
    }
    
    public static AbstractPolicy loadPolicy(String onePolicy,
                                            PolicyFinder finder) {
        return loadPolicy(onePolicy, finder, null, null);
    }

    public static AbstractPolicy loadPolicy(String onePolicy,
                                            PolicyFinder finder,
                                            File schemaFile,
                                            ErrorHandler handler) {
        try {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);

            DocumentBuilder db = null;

            factory.setNamespaceAware(true);

            if (schemaFile == null) {
                factory.setValidating(false);

                db = factory.newDocumentBuilder();
            } else {
                factory.setValidating(true);

                factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                factory.setAttribute(JAXP_SCHEMA_SOURCE, schemaFile);
                
                db = factory.newDocumentBuilder();
                db.setErrorHandler(handler);
            }

            Document doc = db.parse(new ByteArrayInputStream(onePolicy.getBytes()));
            
            Element root = doc.getDocumentElement();
            String name = root.getTagName();

            if (name.equals("Policy")) {
                return Policy.getInstance(root);
            } else if (name.equals("PolicySet")) {
                return PolicySet.getInstance(root, finder);
            } else {
                throw new Exception("Unknown root document type: " + name);
            }

        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING))
                logger.log(Level.WARNING, "Error reading policy from file " +
                           onePolicy, e);
        }

        return null;
    }

    /**
     * Get all policies
     * 
     * @return a set of active and potential policies.
     */
    public Set getPolicies() {
    	Set	s = new HashSet();
    	// get active policies
    	s.addAll(activePolicies);
    	// and potential policies
    	s.addAll(potentialPolicies.values());
    	return s;
    }
    
    /**
     * Always returns true, since reference-based retrieval is supported.
     *
     * @return true
     */
    public boolean isIdReferenceSupported() {
        return true;
    }

    /**
     * Tries to find one and only one matching policy given the idReference
     * If more than one policy is found, this is an error and must be reported
     * as such. If no policies are found, then an empty result must be
     * returned. By default this method returns an empty result. This method
     * should never return null.
     *
     * @param idReference an identifier specifying some policy
     * @param type type of reference (policy or policySet) as identified by
     *             the fields in <code>PolicyReference</code>
     * @param constraints any optional constraints on the version of the
     *                    referenced policy (this will never be null, but
     *                    it may impose no constraints, and in fact will
     *                    never impose constraints when used from a pre-2.0
     *                    XACML policy)
     *
     * @return the result of looking for a matching policy
     */
    public PolicyFinderResult findPolicy(URI idReference, int type,
                                         VersionConstraints constraints) {
        // check the potential policies cache
        AbstractPolicy	policy = (AbstractPolicy)potentialPolicies.get(idReference);
        // Should not check the active policies since they should be put there 
        // actively
        
        if (policy == null) {
            ArrayList code = new ArrayList();
            code.add(Status.STATUS_PROCESSING_ERROR);
            Status status = new Status(code,
                                       "couldn't load referenced policy");
            return new PolicyFinderResult(status);
        }
        else {
	        // return the referenced policy
	        return new PolicyFinderResult(policy);
        }
    }

    public PolicyFinderResult findPolicy(EvaluationCtx context) {
        AbstractPolicy selectedPolicy = null;
        // Just try to find an applicable active policy
        Iterator it = activePolicies.iterator();

        while (it.hasNext()) {
            AbstractPolicy policy = (AbstractPolicy)(it.next());

            MatchResult match = policy.match(context);
            int result = match.getResult();
            
            if (result == MatchResult.INDETERMINATE)
                return new PolicyFinderResult(match.getStatus());

            if (result == MatchResult.MATCH) {
                if (selectedPolicy != null) {
                    ArrayList code = new ArrayList();
                    code.add(Status.STATUS_PROCESSING_ERROR);
                    Status status = new Status(code, "too many applicable top-"
                                               + "level policies");
                    return new PolicyFinderResult(status);
                }

                selectedPolicy = policy;
            }
        }

        if (selectedPolicy != null)
            return new PolicyFinderResult(selectedPolicy);
        else
            return new PolicyFinderResult();
    }

    public void warning(SAXParseException exception) throws SAXException {
        if (logger.isLoggable(Level.WARNING))
            logger.warning("Warning on line " + exception.getLineNumber() +
                           ": " + exception.getMessage());
    }

    public void error(SAXParseException exception) throws SAXException {
        if (logger.isLoggable(Level.WARNING))
            logger.warning("Error on line " + exception.getLineNumber() +
                           ": " + exception.getMessage() + " ... " +
                           "Policy will not be available");

        throw new SAXException("error parsing policy");
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        if (logger.isLoggable(Level.WARNING))
            logger.warning("Fatal error on line " + exception.getLineNumber() +
                           ": " + exception.getMessage() + " ... " +
                           "Policy will not be available");

        throw new SAXException("fatal error parsing policy");
    }
}
