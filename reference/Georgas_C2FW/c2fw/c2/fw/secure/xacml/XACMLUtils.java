/*
 * Created on Jul 14, 2005
 *
 */
package c2.fw.secure.xacml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import c2.fw.BrickInterfaceIdPair;
import c2.fw.InitializationParameter;
import c2.fw.NamedPropertyMessage;
import c2.fw.Weld;
import c2.fw.secure.IPrincipal;
import c2.fw.secure.IPrivilege;
import c2.fw.secure.IRule;
import c2.fw.secure.ISecureBrick;
import c2.fw.secure.ISecureConnector;
import c2.fw.secure.ISubject;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.Indenter;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicySet;
import com.sun.xacml.Rule;
import com.sun.xacml.Target;
import com.sun.xacml.TargetMatch;
import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.combine.CombiningAlgFactory;
import com.sun.xacml.combine.RuleCombiningAlgorithm;
import com.sun.xacml.cond.Evaluatable;
import com.sun.xacml.cond.Function;
import com.sun.xacml.cond.FunctionFactory;
import com.sun.xacml.cond.MatchFunction;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Subject;

public class XACMLUtils {
    // OASIS XACML
	public static final String SUBJECT_ID 			= 
				"urn:oasis:names:tc:xacml:1.0:subject:subject-id";
	public static final String RESOURCE_ID 			= 
				"urn:oasis:names:tc:xacml:1.0:resource:resource-id";
	public static final String ACTION_ID 			= 
				"urn:oasis:names:tc:xacml:1.0:action:action-id";

	// RBAC Profile
	// http://docs.oasis-open.org/xacml/2.0/access_control-xacml-2.0-rbac-profile1-spec-os.pdf
	public static final String RBAC_PROFILE_ID = 
		"urn:oasis:names:tc:xacml:2.0:profiles:rbac:core-hierarchical";
	public static final String RBAC_ATTRIBUTE_ID = 
		"urn:oasis:names:tc:xacml:2.0:subject:role";
	public static final String RBAC_SUBJECT_REA =
		"urn:oasis:names:tc:xacml:2.0:subject-category:role-enablement-authority";
	public static final String RBAC_ACTION_HAS_ROLE =
		"urn:oasis:names:tc:xacml:2.0:actions:hasPrivilegesOfRole";
	public static final String RBAC_ACTION_ENABLE_ROLE = 
		"urn:oasis:names:tc:xacml:2.0:actions:enableRole";
	
	// XADL
	public static final	String URN_XADL				= "urn:xadl:";
	
	public static final String XADL_RESOURCE		= URN_XADL + "resource:";
	public static final String XADL_ACTION			= URN_XADL + "action:";
	public static final String XADL_DOMAIN			= URN_XADL + "domain:";
	public static final String XADL_DOMAIN_NAME		= URN_XADL + "domain:name";
	
	public static final String XADL_SUBJECT			= URN_XADL + "subject";
	public static final String XADL_PRINCIPAL		= URN_XADL + "principal";		
	public static final String XADL_PRIVILEGE		= URN_XADL + "privilege";		

	public static final String XADL_SRC				= ":src";
	public static final String XADL_SUBJECT_SRC		= XADL_SUBJECT + XADL_SRC;
	public static final String XADL_PRINCIPAL_SRC	= XADL_PRINCIPAL + XADL_SRC;		
	public static final String XADL_PRIVILEGE_SRC	= XADL_PRIVILEGE + XADL_SRC;		
	public static final String XADL_DST				= ":dst";
	public static final String XADL_SUBJECT_DST		= XADL_SUBJECT + XADL_DST;
	public static final String XADL_PRINCIPAL_DST	= XADL_PRINCIPAL + XADL_DST;		
	public static final String XADL_PRIVILEGE_DST	= XADL_PRIVILEGE + XADL_DST;		
	
	// These are the values for the action-id
	public static final String ACTION_ADD_BRICK 	= XADL_ACTION + "AddBrick";
	public static final String ACTION_REMOVE_BRICK 	= XADL_ACTION + "RemoveBrick";
	public static final String ACTION_BEGIN_BRICK 	= XADL_ACTION + "BeginBrick";
	public static final String ACTION_END_BRICK 	= XADL_ACTION + "EndBrick";
	public static final String ACTION_ADD_WELD 		= XADL_ACTION + "AddWeld";
	public static final String ACTION_REMOVE_WELD 	= XADL_ACTION + "RemoveWeld";
	public static final String ACTION_ROUTE_MESSAGE	= XADL_ACTION + "RouteMessage";
	
	// RBAC methods. 
	public static final String ACTION_RBAC 			= XADL_ACTION + "rbac:";
	public static final String ACTION_ADD_USER 		= ACTION_RBAC + "addUser";
	public static final String ACTION_ADD_ROLE 		= ACTION_RBAC + "addRole";
	public static final String ACTION_ASSIGN_USER 	= ACTION_RBAC + "assignUser";

	// RBTM methods
	public static final String ACTION_TRUST_ROLE	= XADL_ACTION + "Trust";

	// These are the attribute ids for the resource
	// The naming scheme mimics the xADL structure involved.
	// For example, to specifiy the resrouce attribute
	// value represents a component type, the id of the 
	// resource attribute is "urn:xadl:ArchTypes:ComponentType:id",
	// because in xADL, the ComponentType tag lies under ArchTypes,
	// with an attribute of id
	//
	// For actions of AddBrick and RemoveBrick
	public static final String RESOURCE_COMPONENT_TYPE	= 
				"urn:xadl:ArchTypes:ComponentType:id";
	public static final String RESOURCE_CONNECTOR_TYPE	= 
				"urn:xadl:ArchTypes:ConnectorType:id";
	public static final String RESOURCE_COMPONENT_ID	= 
				"urn:xadl:archStructure:component:id";
	public static final String RESOURCE_CONNECTOR_ID	= 
				"urn:xadl:archStructure:connector:id";
	// For actions of AddWeld, RemoveWeld (two of the points)
	public static final String RESOURCE_LINK_POINT	= 
				"urn:xadl:archStructure:link:point";
	// For action RouteMessage
	public static final String RESOURCE_ROUTE_MESSAGE	= 
				"RouteMessage";
	public static final String RESOURCE_MESSAGE_SOURCE	= 
				"urn:xadl:archStructure:link:pointSource";
	public static final String RESOURCE_MESSAGE_DESTINATION	= 
				"urn:xadl:archStructure:link:pointDestination";
	public static final String SUBJECT_COMPONENT_ID	= 
				"urn:xadl:archStructure:component:id";
	public static final String SUBJECT_CONNECTOR_ID	= 
				"urn:xadl:archStructure:connector:id";

	// The value for the subject-id, when the SecureManagedSystem is 
	// requesting permissions
	public static final String SUBJECT_ID_SMS = "SecureManagedSystem";
	
	public static final String REQUEST_NS 			= 
		"xmlns=\"urn:oasis:names:tc:xacml:1.0:context\"\n" + 
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
		"xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
        "xmlns:types=\"http://www.ics.uci.edu/pub/arch/xArch/types.xsd\"\n" +
        "xmlns:messages=\"http://www.ics.uci.edu/pub/arch/xArch/messages.xsd\"\n" +
        "xmlns:security=\"http://www.ics.uci.edu/pub/arch/xArch/security.xsd\"";

	/**
     * Simple helper routine that creates a TargetMatch instance.
     *
     * @param type the type of match
     * @param functionId the matching function identifier
     * @param designator the AttributeDesignator used in this match
     * @param value the AttributeValue used in this match
     *
     * @return the matching element
     */
    public static TargetMatch createTargetMatch(int type, String functionId,
                                                AttributeDesignator designator,
                                                AttributeValue value) {
        try {
            // get the factory that handles Target functions and get an
            // instance of the right function
            FunctionFactory factory = FunctionFactory.getTargetInstance();
            Function function = factory.createFunction(functionId);
        
            // create the TargetMatch
            return new TargetMatch(type, function, designator, value);
        } 
        catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates the Target used in the Condition. This Target specifies that
     * the Condition applies to anyone taking the action commit.
     *
     * @param subjectName the name of the subject
     * @param resourceName the name of the resource
     * @param actionName the name of the action
     * @return the target
     *
     * @throws URISyntaxException if there is a problem with any of the URIs
     */
    public static Target createRuleTarget(String subjectName, String resourceName, 
    		String actionName) throws URISyntaxException {
        List subjects 		= new ArrayList();
        List resources 		= new ArrayList();
        List actions 		= new ArrayList();

        // create the Subject section
        List subject = new ArrayList();
        
        String subjectMatchId = MatchFunction.NAME_REGEXP_STRING_MATCH;
        URI subjectDesignatorType = new URI(StringAttribute.identifier);
        URI subjectDesignatorId = new URI(SUBJECT_ID);
        AttributeDesignator subjectDesignator =
            new AttributeDesignator(AttributeDesignator.SUBJECT_TARGET,
                                    subjectDesignatorType,
                                    subjectDesignatorId, false);
        StringAttribute subjectValue = new StringAttribute(subjectName);
        subject.add(createTargetMatch(TargetMatch.SUBJECT, subjectMatchId,
                                      subjectDesignator, subjectValue));
        // put the Subject sections into their lists
        subjects.add(subject);

        // create the Resource section
        List resource = new ArrayList();

        String resourceMatchId = MatchFunction.NAME_REGEXP_STRING_MATCH;
        URI resourceDesignatorType = new URI(StringAttribute.identifier);
        URI resourceDesignatorId = new URI(RESOURCE_ID);
        AttributeDesignator resourceDesignator = 
            new AttributeDesignator(AttributeDesignator.RESOURCE_TARGET,
                                    resourceDesignatorType,
                                    resourceDesignatorId, false);
        StringAttribute resourceValue = new StringAttribute(resourceName);
        resource.add(createTargetMatch(TargetMatch.RESOURCE, resourceMatchId,
                                       resourceDesignator, resourceValue));
        resources.add(resource);

        // create the Action section
        List action = new ArrayList();

        String actionMatchId = MatchFunction.NAME_REGEXP_STRING_MATCH;
        URI actionDesignatorType = new URI(StringAttribute.identifier);
        URI actionDesignatorId = new URI(ACTION_ID);
        AttributeDesignator actionDesignator = 
            new AttributeDesignator(AttributeDesignator.ACTION_TARGET,
                                    actionDesignatorType,
                                    actionDesignatorId, false);
        StringAttribute actionValue = new StringAttribute(actionName);
        action.add(createTargetMatch(TargetMatch.ACTION, actionMatchId,
                                     actionDesignator, actionValue));
        // put the Action section in the Actions list
        actions.add(action);

        // create & return the new Target
        return new Target(subjects, resources, actions);
    }

    /**
     * Creates the Rule used in the Policy.
     *
     * @param ruleId the id of the new rule
     * @param subjectName the name of the subject
     * @param resourceName the name of the resource
     * @param actionName the name of the action
     * @return the rule
     *
     * @throws URISyntaxException if there is a problem with any of the URIs
     */
    public static Rule createRule(String ruleId, String subjectName, 
    		String resourceName, String actionName, int effect) 
    				throws URISyntaxException {
        // define the identifier for the rule
        URI uriRuleId = new URI(ruleId);

        // get the Target for the rule
        Target target = createRuleTarget(subjectName, resourceName, actionName);

        return new Rule(uriRuleId, effect, null, target, null);
    }

    /**
     * Create a policy
     * 
     * @param policyIdentifier	the id of the new policy
     * @param description	the description of the new policy
     * @param combiningAlgorithm	the algorithm to combine the rules
     * @param rules	an array of specifications for the rules, each specify
     * whether a subject can perform an action on an object
     * @return the policy that combines all rules with the combining algorithm
     */
    public static String createPolicy(String policyIdentifier, String description, 
    		String combiningAlgorithm, IRule[] rules) {
    	try {
	        // define the identifier for the policy
	        URI policyId = new URI(policyIdentifier);
	
	        // get the combining algorithm for the policy
	        //URI combiningAlgId = new URI(OrderedPermitOverridesRuleAlg.algId);
	        URI combiningAlgId = new URI(combiningAlgorithm);
	        CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
	        RuleCombiningAlgorithm combiningAlg =
	            (RuleCombiningAlgorithm)(factory.createAlgorithm(combiningAlgId));
	
	        // create a list for the rules and add our rules in order
	        List ruleList = new ArrayList();
	
	        for (int i =0; i<rules.length; i++) {
	            int effect = Result.DECISION_DENY;
	            if (rules[i].isAllowed()) {
	            	effect = Result.DECISION_PERMIT;
	            }
	        	Rule	r = createRule("Rule" + i, rules[i].getSubject(), 
	        			rules[i].getObject(), rules[i].getAction(), effect);
	        	ruleList.add(r);
	        }
	
	        // create the default, fall-through rule
	        Rule defaultRule = new Rule(new URI("FinalRule"), Result.DECISION_DENY,
	                                    null, null, null);
	        ruleList.add(defaultRule);
	
	        // create the policy. We use no policy target so each rule is independent
	        Policy policy = new Policy(policyId, null, combiningAlg, description,
	        		new Target(null, null, null), ruleList);
	
	        ByteArrayOutputStream	s = new ByteArrayOutputStream();
	        policy.encode(s, new Indenter());
	        return s.toString();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }

    /**
     * Create a request for access
     * 
     * @param subjectName	the requesting subject
     * @param resourceName	the requested resource
     * @param actionName	the requested action that the subject wants to 
     * perform on the resource
     * @return	a string representation of the request
     * @throws URISyntaxException
     */
    public static String createRequest(String subjectName, String resourceName, 
    		String actionName) {
    	return createRequest(subjectName, resourceName, actionName, null);
    }

    /**
     * Create a request for access, with supplied resource content
     * 
     * @param subjectName	the requesting subject
     * @param resourceName	the requested resource
     * @param actionName	the requested action that the subject wants to 
     * perform on the resource
     * @param resourceConetnt the optional resource content
     * @return	a string representation of the request
     * @throws URISyntaxException
     */
    public static String createRequest(String subjectName, String resourceName, 
    		String actionName, String resourceContent) {
    	try {
	        HashSet attributes = new HashSet();
	        // setup the id and value for the requesting subject
	        URI subjectId = new URI(SUBJECT_ID);
	        StringAttribute subjectValue = new StringAttribute(subjectName);
	        attributes.add(new Attribute(subjectId, null, null, subjectValue));
	        // bundle the attributes in a Subject with the default category
	        HashSet subjects = new HashSet();
	        subjects.add(new Subject(attributes));
	
	        HashSet resource = new HashSet();
	        // the resource being requested
	        StringAttribute resourceValue =
	            new StringAttribute(resourceName);
	        // create the resource using a standard, required identifier for
	        // the resource being requested
	        resource.add(new Attribute(new URI(EvaluationCtx.RESOURCE_ID),
	                                   null, null, resourceValue));
	        
	        HashSet action = new HashSet();
	        // create the action
	        action.add(new Attribute(new URI(ACTION_ID), null, null,
	                                 new StringAttribute(actionName)));
	
	        return createRequest(subjects, resource, action, resourceContent);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * Create a request for access, with supplied resource content
     * 
     * @param subject		the set of attributes of the requesting subject
     * @param resource		the set of attributes of the requested resource
     * @param action		the set of attributes of the requested action 
     * that the subject wants to perform on the resource
     * @param resourceConetnt the optional resource content
     * @return	a string representation of the request
     */
    public static String createRequest(Set subject, Set resource, 
		Set action, String resourceContent) {
        // create the new Request...note that the Environment must be specified
        // using a valid Set, even if that Set is empty
        RequestCtx request =
            new RequestCtx(subject, resource,
                           action, new HashSet(), resourceContent);
        request.setNamespace(REQUEST_NS);
        
        ByteArrayOutputStream	s = new ByteArrayOutputStream();
        request.encode(s, new Indenter());
        return s.toString();
    }
    
    /**
     * Create a request for add or remove a component
     * 
     * @param subjectName	the name of the subject. Usually "SecureManagedSystem"
     * @param componentType	the type of the component
     * @param componentId	the id of the component
     * @param actionName	the action requested. Should be either "AddBrick" or "RemoveBrick"
     * @param initParams	optional init parameters. Now used for principals

     * @return the request XML string
     */
    public static String createRequestForComponent(String subjectName, 
    		String componentType, String componentId, String actionName, 
    		InitializationParameter[] initParams) {
    	try {
	        HashSet attributes = new HashSet();
	        attributes.add(new Attribute(new URI(SUBJECT_ID), null, null, 
	        		new StringAttribute(subjectName)));
	        HashSet subjects = new HashSet();
	        subjects.add(new Subject(attributes));
	
	        HashSet resource = new HashSet();
	        // The resource id is the component id
	        resource.add(new Attribute(new URI(EvaluationCtx.RESOURCE_ID),
	                     null, null, new StringAttribute(componentId)));
	        // Also attach the component type
	        resource.add(new Attribute(new URI(RESOURCE_COMPONENT_TYPE),
	                null, null, new StringAttribute(componentType)));
	        if (initParams != null) {
	        	for (int i = 0; i < initParams.length; i++) {
	        		InitializationParameter ip = initParams[i];
	        		if (ip.getName().equals(c2.fw.secure.IPrincipal.PRINCIPAL)) {
	        	        resource.add(new Attribute(new URI(XADL_PRINCIPAL),
	        	                null, null, new StringAttribute(ip.getValue())));
	        		}
	        	}
	        }
	        
	        HashSet action = new HashSet();
	        action.add(new Attribute(new URI(ACTION_ID), null, null,
	                      new StringAttribute(actionName)));
	
	        return createRequest(subjects, resource, action, null);
    	}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    /**
     * Create a request for add or remove a component
     * 
     * @param subjectName	the name of the subject. Usually "SecureManagedSystem"
     * @param connectorType	the type of the component
     * @param connectorId	the id of the component
     * @param actionName	the action requested. Should be either "AddBrick" or "RemoveBrick"
     * @param initParams	optional init parameters. Now used for principals
     * 
     * @return the request XML string
     */
    public static String createRequestForConnector(String subjectName, 
    		String connectorType, String connectorId, String actionName, 
    		InitializationParameter[] initParams) {
    	try {
	        HashSet attributes = new HashSet();
	        attributes.add(new Attribute(new URI(SUBJECT_ID), null, null, 
	        		new StringAttribute(subjectName)));
	        HashSet subjects = new HashSet();
	        subjects.add(new Subject(attributes));
	
	        HashSet resource = new HashSet();
	        // The resource id is the component id
	        resource.add(new Attribute(new URI(EvaluationCtx.RESOURCE_ID),
	                     null, null, new StringAttribute(connectorId)));
	        // Also attach the component type
	        resource.add(new Attribute(new URI(RESOURCE_CONNECTOR_TYPE),
	                null, null, new StringAttribute(connectorType)));
	        if (initParams != null) {
	        	for (int i = 0; i < initParams.length; i++) {
	        		InitializationParameter ip = initParams[i];
	        		if (ip.getName().equals(c2.fw.secure.IPrincipal.PRINCIPAL)) {
	        	        resource.add(new Attribute(new URI(XADL_PRINCIPAL),
	        	                null, null, new StringAttribute(ip.getValue())));
	        		}
	        	}
	        }
	        
	        HashSet action = new HashSet();
	        action.add(new Attribute(new URI(ACTION_ID), null, null,
	                      new StringAttribute(actionName)));
	
	        return createRequest(subjects, resource, action, null);
    	}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    /**
     * Create a request for add or remove a component
     * @param subjectName	the name of the subject. Usually "SecureManagedSystem"
     * @param weld			the weld to operate
     * @param actionName	the action requested. Should be either "AddWeld" or "RemoveWeld"
     * @return
     */
    public static String createRequestForWeld(String subjectName, 
    		Weld w, String actionName) {
    	try {
	        HashSet attributes = new HashSet();
	        attributes.add(new Attribute(new URI(SUBJECT_ID), null, null, 
	        		new StringAttribute(subjectName)));
	        HashSet subjects = new HashSet();
	        subjects.add(new Subject(attributes));
	
    		String	point1 = generateAnchorId(w.getFirstEndpoint());
    		String	point2 = generateAnchorId(w.getSecondEndpoint());
	        HashSet resource = new HashSet();
	        // We generate the resrouce-id, which is a required part of XACML
	        // The policy should not care this id (use a regexp-match .*), 
	        // since this id is not important (the two points are important) 
	        resource.add(new Attribute(new URI(EvaluationCtx.RESOURCE_ID),
	                     null, null, new StringAttribute(point1 + "_to_" + point2)));
	        // Attach the two points
	        // They share the same resource attribute id. XACML will return 
	        // them in a bag when queried for the id
	        resource.add(new Attribute(new URI(RESOURCE_LINK_POINT),
	                null, null, new StringAttribute(point1)));
	        resource.add(new Attribute(new URI(RESOURCE_LINK_POINT),
	                null, null, new StringAttribute(point2)));
	        
	        HashSet action = new HashSet();
	        action.add(new Attribute(new URI(ACTION_ID), null, null,
	                      new StringAttribute(actionName)));
	
	        return createRequest(subjects, resource, action, null);
    	}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    /**
     * Create a request for routing a message within a brick
     * 
     * @param brick			the brick to route the message
     * @param npm			the message to route
     * @return a request. Its ResourceContent section can be used for 
     * content-based access control. 
     */
	public static String createRequestForRouteInternal(ISecureBrick brick, NamedPropertyMessage npm) {
		try {
			String	resourceContent = getResourceContent(npm);
			
	        HashSet attributes = new HashSet();
	        // setup the id and value for the requesting subject
	        ISubject	subject = brick.getSubject();
	        String		brickId = brick.getIdentifier().toString();
	        if (subject != null) {
		        attributes.add(new Attribute(new URI(SUBJECT_ID), null, null, 
		        		new StringAttribute(subject.getSubjectId())));
		        attributes.add(new Attribute(new URI(XADL_SUBJECT),
	                    null, null, new StringAttribute(subject.getSubjectId())));
		        if (brick instanceof ISecureConnector) {
			        attributes.add(new Attribute(new URI(SUBJECT_CONNECTOR_ID),
		                    null, null, new StringAttribute(brickId)));
		        }
		        else {
			        attributes.add(new Attribute(new URI(SUBJECT_COMPONENT_ID),
		                    null, null, new StringAttribute(brickId)));
		        }
	        }
	        else {
		        attributes.add(new Attribute(new URI(SUBJECT_ID), null, null, 
		        		new StringAttribute(brickId)));
	        }
	        Set principals = brick.getPrincipals();
            for (Iterator i = principals.iterator(); i.hasNext(); ) {
                IPrincipal	p = (IPrincipal)i.next();
		        attributes.add(new Attribute(new URI(XADL_PRINCIPAL),
	                    null, null, new StringAttribute(p.getName())));
		        attributes.add(new Attribute(new URI(RBAC_ATTRIBUTE_ID),
	                    null, null, new StringAttribute(p.getName())));
            }
	        Set privileges = brick.getPrivileges();
            for (Iterator i = privileges.iterator(); i.hasNext(); ) {
                IPrivilege	p = (IPrivilege)i.next();
		        attributes.add(new Attribute(new URI(XADL_PRIVILEGE),
	                    null, null, new StringAttribute(p.getName())));
            }
	        // bundle the attributes in a Subject with the default category
	        HashSet subjects = new HashSet();
	        subjects.add(new Subject(attributes));
	        
	        HashSet resource = new HashSet();
	        // the resource being requested
	        StringAttribute resourceValue =
	            new StringAttribute(RESOURCE_ROUTE_MESSAGE);
	        // create the resource using a standard, required identifier for
	        // the resource being requested
	        resource.add(new Attribute(new URI(EvaluationCtx.RESOURCE_ID),
	                                   null, null, resourceValue));
	        
	        HashSet action = new HashSet();
	        // create the action
	        action.add(new Attribute(new URI(ACTION_ID), null, null,
	                                 new StringAttribute(ACTION_ROUTE_MESSAGE)));
	
			return createRequest(
				subjects, resource, action, resourceContent);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
    /**
     * Create a request for routing a message between bricks
     * 
     * @param npm			the message to route
     * @param srcBrick		the source brick of the message
     * @param dstBrick		the destination brick of the message
     * @return a request. Its ResourceContent section can be used for 
     * content-based access control. 
     */
	public static String createRequestForRouteExternal(NamedPropertyMessage npm, 
			ISecureBrick srcBrick, ISecureBrick dstBrick) {
		try {
			String	resourceContent = getResourceContent(npm);
			
	        HashSet attributes = new HashSet();
	        // setup the id and value for the requesting subject
	        attributes.add(new Attribute(new URI(SUBJECT_ID), null, null, 
	        		new StringAttribute(XACMLUtils.SUBJECT_ID_SMS)));
	        // bundle the attributes in a Subject with the default category
	        HashSet subjects = new HashSet();
	        subjects.add(new Subject(attributes));
	        
	        HashSet resource = new HashSet();
	        // the resource being requested
	        StringAttribute resourceValue =
	            new StringAttribute(RESOURCE_ROUTE_MESSAGE);
	        // create the resource using a standard, required identifier for
	        // the resource being requested
	        resource.add(new Attribute(new URI(EvaluationCtx.RESOURCE_ID),
	                                   null, null, resourceValue));
	        if (srcBrick != null) {
		        ISubject	subject = srcBrick.getSubject();
		        if (subject != null) {
			        resource.add(new Attribute(new URI(XADL_SUBJECT_SRC),
		                    null, null, new StringAttribute(subject.getSubjectId())));
		        }
		        Set principals = srcBrick.getPrincipals();
	            for (Iterator i = principals.iterator(); i.hasNext(); ) {
	                IPrincipal	p = (IPrincipal)i.next();
			        resource.add(new Attribute(new URI(XADL_PRINCIPAL_SRC),
		                    null, null, new StringAttribute(p.getName())));
	            }
		        Set privileges = srcBrick.getPrivileges();
	            for (Iterator i = privileges.iterator(); i.hasNext(); ) {
	                IPrivilege	p = (IPrivilege)i.next();
			        resource.add(new Attribute(new URI(XADL_PRIVILEGE_SRC),
		                    null, null, new StringAttribute(p.getName())));
	            }
	        }
	        if (dstBrick != null) {
		        ISubject	subject = dstBrick.getSubject();
		        if (subject != null) {
			        resource.add(new Attribute(new URI(XADL_SUBJECT_DST),
		                    null, null, new StringAttribute(subject.getSubjectId())));
		        }
		        Set principals = dstBrick.getPrincipals();
	            for (Iterator i = principals.iterator(); i.hasNext(); ) {
	                IPrincipal	p = (IPrincipal)i.next();
			        resource.add(new Attribute(new URI(XADL_PRINCIPAL_DST),
		                    null, null, new StringAttribute(p.getName())));
	            }
		        Set privileges = dstBrick.getPrivileges();
	            for (Iterator i = privileges.iterator(); i.hasNext(); ) {
	                IPrivilege	p = (IPrivilege)i.next();
			        resource.add(new Attribute(new URI(XADL_PRIVILEGE_DST),
		                    null, null, new StringAttribute(p.getName())));
	            }
	        }
	        
	        HashSet action = new HashSet();
	        // create the action
	        action.add(new Attribute(new URI(ACTION_ID), null, null,
	                                 new StringAttribute(ACTION_ROUTE_MESSAGE)));
	
			return createRequest(
				subjects, resource, action, resourceContent);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Create a resource content from a message
	 * 
	 * @param npm the message
	 * @return the resource content
	 */
	private static String getResourceContent(NamedPropertyMessage npm) {
		// This mimics the messages schema
		String		messageCategory	= "request";
		if ("Notification".equalsIgnoreCase(npm.getParameter("C2_TYPE").toString())) {
			messageCategory = "notification";
		}
		String		resourceContent = "";
		// with addition of pointSource and pointDestination
		// Note, these two are not defined in any schema, and they are magically
		// interpreted by SunXACML
		resourceContent += "\n<security:pointSource " +
			"xlink:href=\"#" + generateAnchorId(npm.getSource()) + "\" " + 
			"xsi:type=\"instance:XMLLink\" xlink:type=\"simple\" />\n";
		resourceContent += "<security:pointDestination " +
			"xlink:href=\"#" + generateAnchorId(npm.getDestination()) + "\" " + 
			"xsi:type=\"instance:XMLLink\" xlink:type=\"simple\" />\n";
		resourceContent += "<security:routeMessage " +
			"messages:id=\"" + npm.getName() + "\" " + 
			"messages:kind=\"" + messageCategory + "\" " +
			"xsi:type=\"messages:NamedPropertyMessage\">\n";
		resourceContent += "\t<messages:description xsi:type=\"instance:Description\">" +
			"Dummy</messages:description>\n";
		resourceContent += 
			"\t<messages:count xsi:type=\"messages:Count\">1</messages:count>\n";
		resourceContent += 
			"\t<messages:type xsi:type=\"messages:MessageType\">" +
			"c2.fw.NamedPropertyMessage</messages:type>\n";
		resourceContent += 
			"\t<messages:name xsi:type=\"messages:MessageName\">" +
			npm.getName() + "</messages:name>\n";
		String[] params = npm.getAllPropertyNames();
		for (int i = 0; i<params.length; i++) {
			resourceContent += "\t<messages:namedProperty " +
					"xsi:type=\"messages:NamedProperty\">\n";
			resourceContent += "\t\t<messages:name xsi:type=\"messages:PropertyName\">";
			resourceContent += (params[i] + "</messages:name>\n");
			resourceContent += "\t\t<messages:value xsi:type=\"messages:PropertyValue\">";
			resourceContent += (npm.getParameter(params[i]) + "</messages:value>\n");
			resourceContent += "\t</messages:namedProperty>\n";
		}
		resourceContent += "</security:routeMessage>\n";
		return resourceContent;
	}
	
    /**
     * Use a broad-first search to get all descendant rules/policies of a policy.
     * 
     * This should return the rules in accordance with their order of apperance.
     * The policy is returned to analyze the role inheritance relationship.
     * 
     * @param p 		the policy to analyze
     * @param rules		the returned list of descendant rules for the policy
     * @param policies  the returned list of descendant policies for the policy
     */ 
    public static void getAllRulesAndPolicies(AbstractPolicy p, List rules, List policies) {
        // rules return the collected descendant rules, 
        // policies return the collected descendant policies
    	List	children = p.getChildren();
    	for (Iterator i = children.iterator(); i.hasNext(); ) {
    		java.lang.Object	r = i.next();
    		if (r instanceof Rule) { 
    			rules.add(r);
    		}
    		else if (r instanceof AbstractPolicy) {
    		    policies.add(r);
    		    List l = new ArrayList();
    		    List ps = new ArrayList();
    		    getAllRulesAndPolicies((AbstractPolicy)r, l, ps);
    			rules.addAll(l);
    			policies.addAll(ps);        
    		}
    	}
    }
    
    /**
     * Get the first string of Subject/Resource/Action
     * 
     * @param l the list of target matches
     * @return the string corresponding to the value of the first 
     * subject/resource/action. It will be an empty string if there is no such string. 
     */
    public static String getFirstMatchString(List l) {
    	// l is Subjects/Resources/Actions, 
    	// which is a list of Subject/Resource/Action, 
    	// each element of which is a list of SubjectMatch/ResourceMatch/ActionMatch
    	// We try to get the first match's string value, if there ever is one
    	String					result = "";
    	TargetMatch				match = null;
    	if (l != null) {
	    	List	aMatch = (List)l.get(0);
	    	if (aMatch != null)  
	    		match = (TargetMatch)aMatch.get(0);
	    	if (match != null) {
	    	    AttributeValue	attr = match.getMatchValue();
	    	    if (attr instanceof StringAttribute) {
	    	        result = ((StringAttribute)attr).getValue();
	    	    }
	    	    else if (attr instanceof AnyURIAttribute) {
	    	        result = ((AnyURIAttribute)attr).getValue().toString();
	    	    }
	    	}
    	}
    	return result;
    }
    
    /**
     * Get an attribute from a Subjects/Resources/Actions
     * @param l the list of target matches
     * @param attributeId the identifier of the attribute to look
     * @return the string value for the attribute, or empty if there is none
     */
    public static String getMatchString(List l, String attributeId) {
    	String					result = "";
    	TargetMatch				match = null;
    	if (l != null) {
    	    // See above for the structure of l
	    	List	aMatch = (List)l.get(0);
	    	if (aMatch != null) {
	    	    for (Iterator i = aMatch.iterator(); i.hasNext() && result.equals(""); ) {
		    		match = (TargetMatch)i.next();
			    	if (match != null) {
			    	    Evaluatable	d = match.getMatchEvaluatable();
			    	    if (d instanceof AttributeDesignator && 
			    	        ((AttributeDesignator)d).getId().toString().equals(attributeId)) {
				    	    AttributeValue	attr = match.getMatchValue();
				    	    if (attr instanceof StringAttribute) {
				    	        result = ((StringAttribute)attr).getValue();
				    	    }
				    	    else if (attr instanceof AnyURIAttribute) {
				    	        result = ((AnyURIAttribute)attr).getValue().toString();
				    	    }
			    	    }
			    	}
	    	    }
	    	}
    	}
    	return result;
    }

	/**
	 * Get an attribute value from a set of request attributes
	 * @param attributes	the set of request attributes
	 * @param attributeId	the attribute whose value is being searched
	 * @return the attribute value, or null if no such value
	 */
	public static String getRequestAttribute(Set attributes, String attributeId) {
	    String	result = null;
        for (Iterator ai = attributes.iterator(); ai.hasNext(); ) {
            Attribute a = (Attribute)ai.next();
            if (a.getId().toString().equals(attributeId)) {
                // This attribute has the correct id
	    	    AttributeValue	value = a.getValue();
	    	    // Use the correct type to retrieve the string content
	    	    if (value instanceof StringAttribute) {
	    	        result = ((StringAttribute)value).getValue();
	    	        break;
	    	    }
	    	    else if (value instanceof AnyURIAttribute) {
	    	        result = ((AnyURIAttribute)value).getValue().toString();
	    	        break;
	    	    }
            }
        }
        return result;
	}
	
	/**
	 * Get attribute values from a set of request attributes
	 * @param attributes	the set of request attributes
	 * @param attributeId	the attribute whose value is being searched
	 * @return the attribute values, or empty set if no such value
	 */
	public static Set getRequestAttributes(Set attributes, String attributeId) {
	    Set	result = new HashSet();
        for (Iterator ai = attributes.iterator(); ai.hasNext(); ) {
            Attribute a = (Attribute)ai.next();
            if (a.getId().toString().equals(attributeId)) {
                // This attribute has the correct id
	    	    AttributeValue	value = a.getValue();
	    	    // Use the correct type to retrieve the string content
	    	    if (value instanceof StringAttribute) {
	    	        result.add(((StringAttribute)value).getValue());
	    	    }
	    	    else if (value instanceof AnyURIAttribute) {
	    	        result.add(((AnyURIAttribute)value).getValue().toString());
	    	    }
            }
        }
        return result;
	}
	
	/**
	 * Generate an anchor id
	 * @param bi	the brick interface id pair
	 * @return an anchor id in the form of "BrickId.InterfaceId"
	 */
	public static String generateAnchorId(BrickInterfaceIdPair bi) {
		return 	bi.getBrickIdentifier().toString() + "." + 
				bi.getInterfaceIdentifier().toString();
	}

	/**
	 * Return the file content as a string
	 * 
	 * @param fileName	the name of the file
	 * @return the content of the file as a string
	 */
	public static String getFileAsString(String fileName) {
	    String	content = "";
        try {
            BufferedReader bf = new BufferedReader(new FileReader(new File(fileName)));
            String		l = bf.readLine();
    		while (l != null) {
    			content += l;
    			l = bf.readLine();
    		}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	    return content;
	}

	/**
	 * Get a policy from a string
	 * @param onePolicy		the policy string
	 * @return a policy object, or null if something is wrong with the string
	 */
    public static AbstractPolicy getPolicy(String onePolicy) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setIgnoringComments(true);
			factory.setNamespaceAware(true);
			factory.setValidating(false);

			DocumentBuilder db = factory.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(onePolicy
					.getBytes()));

			Element root = doc.getDocumentElement();
			String name = root.getTagName();

			if (name.equals("Policy")) {
				return Policy.getInstance(root);
			} 
			else if (name.equals("PolicySet")) {
				return PolicySet.getInstance(root);
			} 
			else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}