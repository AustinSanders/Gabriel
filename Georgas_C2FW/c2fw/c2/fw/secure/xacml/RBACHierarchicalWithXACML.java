/*
 * Created on Nov 1, 2005
 *
 */
package c2.fw.secure.xacml;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import c2.fw.secure.rbac.Name;
import c2.fw.secure.rbac.NameImpl;
import c2.fw.secure.rbac.Object;
import c2.fw.secure.rbac.ObjectImpl;
import c2.fw.secure.rbac.Operation;
import c2.fw.secure.rbac.OperationImpl;
import c2.fw.secure.rbac.Permission;
import c2.fw.secure.rbac.PermissionAlreadyExistsException;
import c2.fw.secure.rbac.PermissionDoesNotExistException;
import c2.fw.secure.rbac.RBACHierarchical;
import c2.fw.secure.rbac.RBACHierarchicalImpl;
import c2.fw.secure.rbac.Role;
import c2.fw.secure.rbac.RoleCycleException;
import c2.fw.secure.rbac.RoleDoesNotExistException;
import c2.fw.secure.rbac.RoleDoesNotHavePermissionException;
import c2.fw.secure.rbac.RoleExistsException;
import c2.fw.secure.rbac.RoleHierarchical;
import c2.fw.secure.rbac.RoleImpl;
import c2.fw.secure.rbac.RoleInheritanceDoesNotExistException;
import c2.fw.secure.rbac.RoleMultipleInheritanceException;
import c2.fw.secure.rbac.Session;
import c2.fw.secure.rbac.SessionDoesNotExistException;
import c2.fw.secure.rbac.SessionDoesNotHaveRoleException;
import c2.fw.secure.rbac.User;
import c2.fw.secure.rbac.UserAlreadyHasRoleException;
import c2.fw.secure.rbac.UserDoesNotExistException;
import c2.fw.secure.rbac.UserDoesNotHaveRoleException;
import c2.fw.secure.rbac.UserDoesNotHaveSessionException;
import c2.fw.secure.rbac.UserExistsException;
import c2.fw.secure.tm.DomainImpl;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.ParsingException;
import com.sun.xacml.PolicyReference;
import com.sun.xacml.Rule;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;

/**
 * 
 * This class provides an "implementation" of the RBAC standard.
 * 
 * After some experiments, we settle for this scheme
 * 	1) Use the XACML RBAC profile, which uses a role policy set (rps) and 
 * 		a permission policy set (rps) to define the association between
 * 		roles and permissions.
 *  2) Also use the XACML RBAC profile for assigning roles to users
 *  3) Use a side RBAC implementation based on our own engine, but this
 * 		engine is only populated through the XACML files. It is used to
 * 		supply the role(s) attribute for an evalution
 *  4) As a result, this RBAC implementation does not support programatic
 * 		change of roles/users/permissions. The chagne should be instantiated
 * 		by new XACML files.
 * There are other possibilities, but this scheme is
 *  1) Still has most, if not all, expressiveness of Core/Hierarchical RBAC
 *  2) In accordance with the RBAC profile
 *  3) Can correctly select the right initial policy for evaluation
 *  4) Can supply roles automatically so no redundance or inconsistency
 *  5) Does not introduce new syntaxes    
 * 
 * @author Jie Ren
 */
public class RBACHierarchicalWithXACML implements RBACHierarchical {
    // The side RBAC, only populated through XACML files
    // It will automatically create users/roles/permissions
	protected RBACHierarchical		rbac;
	// all policies
	protected Set					policies = new HashSet();
	// map from role id to the role policy set
	protected Map					rps = new HashMap();
	
	/**
	 * The Role Policy Set scheme.
	 * 
	 * The XACML RBAC profile uses a role policy set (RPS) and a permission 
	 * policy set (PPS) to implement role association and permission 
	 * inheritance. A RPS limits to certain subjects (with the required roles)
	 * and refers to a PPS. A PPS has no limit on subjects, and can refer
	 * to other more junior subjects. 
	 * 
	 * The profile also suggests a way of role assignment/enablement that
	 * assigns roles to users.
	 * 
	 * The way we use this profile is
	 * 	1) Each policy set should have a PolicySetId. The id for a RPS should
	 * 		be in the format "RPS:urnOfTheRole". The id for a PPS should be
	 * 		"PPS:urnOfTheRole". When they refer to each other, they should
	 * 		use this id as the PolicySetIdReference. The PPS is ued to 
	 * 		populate the side RBAC
	 *  2) A separate policy is used to assign roles to users. It can have any 
	 * 		id it wants. In each assignment rule, the role should be in the 
	 * 		format "urnOfTheRole", as used in previous XACML files. Such 
	 * 		assignment is used to populate the side RBAC.
	 *  3) No other syntax is needed or allowed at this moment for RBACHierarchical.
	 *  4) In a decentralized RBAC, the role name should be "urnOfTheRole@domain", 
	 * 		and the role assignment policy should be in a policy set with id
	 * 		of "UA:domain". This is not part of the RBAC profile. 
	 */
	public static final String	RPS = "RPS";
	/**
	 * The Permission Policy Set scheme
	 */
	public static final String  PPS = "PPS";
	/**
	 * The Role Assignment Policy scheme. Not required for this class.
	 */
	public static final String	UA = "UA";
	
	public RBACHierarchicalWithXACML(Set contents) {
	    this(contents, null);
	}
	
	/**
	 * Construct an RBAC repository based on a set of XACML policies.
	 * 
	 * @param contents a set of strings. Each string represents a policy. Each
	 * polich should follow the previous guidelines.
	 * @param domainName	the domainName for a RBACDecentralizedWithXACML
	 */
	public RBACHierarchicalWithXACML(Set contents, String domainName) {
	    initRBAC(domainName);
	    
	    if (contents == null) 
	        return;
	    
	    // remember all policies
		policies.addAll(contents);
		// the first parse of the policies.
		// We use SunXACML as the parsing engine
		DynamicPDP	pdp = new DynamicPDP(new HashSet(), policies);
		Set		policies = pdp.getPolicies();
		for (Iterator i = policies.iterator(); i.hasNext(); ) {
		    // get parsed policy
		    AbstractPolicy	policy = (AbstractPolicy)i.next();
			URI			id = policy.getId();
			if (isRPS(policy)) {
			    // For each RPS, record what role it is about
			    String	roleName = getRole(policy); 
				rps.put(roleName, pdp.getPolicyString(id));
			}
			else /*if (!isPPS(policy))*/ {
			    // For PPS or Role assignment, populate the side RBAC
				processPermissionRoleAssignment(rbac, policy);
			}
		}
	}
	
	protected void initRBAC(String domainName) {
	    rbac = new RBACHierarchicalImpl(SESSION_CONTINUE_DOWNGRADE, true, true);
	}
	
	// Currently these two decision only uses a name comparison
	/**
	 * Whether the policy is a role policy set
	 * 
	 * @param p the policy set
	 * @return true if the policy set is a role policy set
	 */
	public static boolean isRPS(AbstractPolicy p) {
	    URI		id = p.getId();
	    return RPS.equals(id.getScheme());
	}
	
	/**
	 * Whether the policy set is a permission policy set
	 * 
	 * @param p the policy set
	 * @return true if the policy set is a permission policy set
	 */
	public static boolean isPPS(AbstractPolicy p) {
	    URI		id = p.getId();
	    return PPS.equals(id.getScheme());
	}
	
	/**
	 * Whether the policy set is about user-role assignment
	 * 
	 * @param p the policy set
	 * @return true if the policy set is about user-role assignment
	 */
	public static boolean isUA(AbstractPolicy p) {
	    URI		id = p.getId();
	    return UA.equals(id.getScheme());
	}
	
	/**
	 * Whether the policy set is about related to RBAC
	 * 
	 * @param p the policy set
	 * @return true if the policy set is related to RBAC
	 */
	public static boolean isRBAC(AbstractPolicy p) {
	    return isRPS(p) || isPPS(p) || isUA(p);
	}
	
	/**
	 * Get the string for the role of a role policy set
	 *  
	 * @param p the role policy set
	 * @return the role that this role policy set is for
	 */
	public static String getRole(AbstractPolicy p) {
	    return p.getId().getSchemeSpecificPart();
	}
	
	/**
	 * Get the name of the domain for the role of a role policy set
	 * 
	 * @param p	the role policy set. Its name should be in the "role@domain" format
	 * @return the name of the domain. If no name is specified, a default empty 
	 * string is returned.
	 */
	public static String getDomain(AbstractPolicy p) {
	    String 	domain = null;
	    String 	role = getRole(p);
	    int		indexOfAt = role.lastIndexOf('@');
	    if (indexOfAt != -1) {
	        domain = role.substring(indexOfAt + 1);
	    }
	    return DomainImpl.getCanonicalName(domain);
	}
	
	/**
	 * Process the policy for user-role and role-permission assignments, 
	 * and role inheritance
	 *
	 * @param rbac the RBAC controller 
	 * @param p the policy
	 */
    public static void processPermissionRoleAssignment(RBACHierarchical rbac,
            AbstractPolicy p) {
    	List		rulesOfPolicy = new ArrayList();
    	List		policiesOfPolicy = new ArrayList();
    	XACMLUtils.getAllRulesAndPolicies(p, rulesOfPolicy, policiesOfPolicy);
    	
    	for (Iterator j = rulesOfPolicy.iterator(); j.hasNext(); ) {
    		handleRule(rbac, (Rule)j.next(), p);
    	}
    	
    	if (isPPS(p)) { 
	    	for (Iterator j = policiesOfPolicy.iterator(); j.hasNext(); ) {
	    	    AbstractPolicy	ap = (AbstractPolicy)j.next();
    	        if (isPPS(ap) && ap instanceof PolicyReference) {
    	            // A (senior) PPS is refering another (junior) PPS
    	            String		seniorRole = getRole(p);
    	            String		juniorRole = getRole(ap);
    	            try {
	    	            RoleHierarchical	senior = (RoleHierarchical)
	    	            	rbac.getRole(new NameImpl(seniorRole)); 
	    	            RoleHierarchical	junior = (RoleHierarchical)
		            		rbac.getRole(new NameImpl(juniorRole)); 
	    	            // This assumes the layout as specified in the RBAC 
	    	            //	profile (a senior PPS is refering to a junior PPS)
	    	            //  and they are named as our naming scheme "PPS:role"
	    	            rbac.addInheritance(senior, junior);
    	            }
    	            catch (Exception e) {
    	                e.printStackTrace();
    	            }
	    	    }
	    	}
    	}
    }
    
    /**
     * Handle a rule of a policy for user-role and role-permission assignments
     * 
     * @param rbac  the RBAC Controller
     * @param r		the rule
     * @param p		the policy set that this rule belongs to
     * @return		true if this rule has incurred processing, false otherwise
     */
    public static boolean handleRule(RBACHierarchical rbac, Rule r, AbstractPolicy p) {
        boolean		handled = false;
    	try {
			String		subject = XACMLUtils.getFirstMatchString(r.getTarget().getSubjects());
			String		action = XACMLUtils.getFirstMatchString(r.getTarget().getActions());
			List		resources = r.getTarget().getResources();
			if (action.equals(XACMLUtils.RBAC_ACTION_ENABLE_ROLE)) {
			    // This is a role assignment rule
			    // We could have checked the UA scheme here, using isUA(p)
				String		role = XACMLUtils.getFirstMatchString(resources);
				// populate the user-role relationship
				// users and roles are created on the fly in get
				rbac.assignUser(rbac.getUser(new NameImpl(subject)), rbac.getRole(new NameImpl(role)));
				handled = true;
			}
			else if (subject.equals("") && !action.equals("")) {
			    // This rule has no subject and an action
			    if(isPPS(p)) {			
			        // It is a permission policy set
					String		resource = XACMLUtils.getFirstMatchString(resources);
					String		role = getRole(p);
					Role		theRole = rbac.getRole(new NameImpl(role));
					// Need to use the get methods, to avoid both duplicates and non-existents
					// permissions are created on the fly in get
					Permission  thePermission = rbac.getPermission(
					        new OperationImpl(new NameImpl(action)),
				        	new ObjectImpl(new NameImpl(resource)));
					// populate the role-permission relationship
					rbac.grantPermission(thePermission, theRole);
					handled = true;
			    }
			}
			/*
			else if (subject.equals(XACMLUtils.SUBJECT_ID_SMS)) {
				if (action.equals(XACMLUtils.ACTION_ADD_USER)) {
					String		resource = getFirstMatchString(resources);
					rbac.addUser(new NameImpl(resource));
				}
				else if (action.equals(XACMLUtils.ACTION_ADD_ROLE)) {
					String		resource = getFirstMatchString(resources);
					rbac.addRole(new NameImpl(resource));
				}
				else if (action.equals(XACMLUtils.ACTION_ASSIGN_USER)) {
				    String		user = getMatchString(resources, XACMLUtils.RESOURCE_ID);
				    String		role = getMatchString(resources, XACMLUtils.ROLE_ID);
				    rbac.assignUser(rbac.getUser(new NameImpl(user)), rbac.getRole(new NameImpl(role)));
				}
			}
			*/
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return handled;
    }
    
	/**
     * Check whether the user can performa an operation on an object
     * 
     * @param aUser			the user
     * @param anOperation	the operation
     * @param anObject		the object
     * @return true if the operation is permitted, false if denifed
	 */
	public boolean checkAccess(String aUser, String anOperation, String anObject) {
	    return checkAccess(null, XACMLUtils.createRequest(aUser, 
		        anObject, anOperation));
	}
	
	/**
	 * Check whether the user can perform an operation.
	 * 
	 * This method finds the correct roles for this user, and use the correct
	 * policy to decide whether any of the roles the user possesses will
	 * allow the operation.
	 * 
	 * @param suppliedUser the user. It can be null, in which case the user
	 * will be infered from the request
	 * @param request the request in XACML Request format
	 * @return true if the operation is permitted, false if denifed
	 */
	public boolean checkAccess(User suppliedUser, String request) {
	    User		aUser = suppliedUser;
        Set 		attributes = null;
	    if (aUser == null) {
	        // No user is supplied, so we need to find it from the request
	        RequestCtx reqCtx = null;
			try {
				reqCtx = RequestCtx.getInstance(new ByteArrayInputStream(
				        request.getBytes()));
			} 
			catch (ParsingException e) {
				e.printStackTrace();
			}
	        Set subjects = reqCtx.getSubjects();
	        String 		userName = "";
	        if (subjects.size()==1) {
	            // Assume there is only one subject in the request
	            for (Iterator i = subjects.iterator(); i.hasNext(); ) {
	                Subject s = (Subject)i.next();
	                attributes = s.getAttributes();
	                userName = XACMLUtils.getRequestAttribute(attributes, XACMLUtils.SUBJECT_ID);
	                if (userName != null && !userName.equals("")) 
	                    break;
	            }
	            try {
	                // Use the name to get the user created in the RBAC
                    aUser = rbac.getUser(new NameImpl(userName));
                }
                catch (UserDoesNotExistException e) {
                    e.printStackTrace();
                }
	        }
	    }
		boolean		result = false;
		try {
		    // Get all roles
			Set roles = rbac.assingedRoles(aUser);
			if (roles.isEmpty()) {
			    // if  nothing from the rbac, then try roles from the request
			    if (attributes != null) {
			        Set roleNames = XACMLUtils.getRequestAttributes(attributes, 
			                XACMLUtils.RBAC_ATTRIBUTE_ID);
			        for (Iterator i = roleNames.iterator(); i.hasNext(); ) {
			            roles.add(new RoleImpl((String)i.next()));
			        }
			    }
			}
			for (Iterator i = roles.iterator(); i.hasNext();) {
				Role	r = (Role)i.next();
				// For this role, find its RPS
				String	rolePolicy = (String)rps.get(r.getNameString());
				if (rolePolicy == null)
				    // if we grab roles from the request, then the policy might be null
				    continue;
				
				Set		p = new HashSet();
				p.add(rolePolicy);
				// Put other policies as potentially reference 
				Set		potentialPolicies = new HashSet(policies);
				potentialPolicies.remove(rolePolicy);
				// Now we can set up the PDP
				DynamicPDP	pdp = new DynamicPDP(p, potentialPolicies, rbac);
				result = pdp.evaluate(request);
				// If any of the role policy allows the request, we are done
				// There can be other semantics, but this might be the most
				// straighforward
				if (result == true)
				    break;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// Below are the defined RBAC interfaces
	// The implementation does
	//		1) Does not support direct insertion. Instead, use XACML to 
	//			indirectly populate the side RBAC
	//		2) Does not support deletion (for simplicity)
	//		3) Does not support session
	//		4) Use the side RBAC to implement the administrative methods
	public User addUser(Name newUser) throws UserExistsException {
	    // We do not allow adding directly. Use Role Assignment policy to add indirectly
		throw new UnsupportedException("addUser should not be called");
	}

	public void deleteUser(User userToDelete) throws UserDoesNotExistException {
	    // Not allowing deletion
		throw new UnsupportedException("deleteUser should not be called");
	}

	public Role addRole(Name newRole) throws RoleExistsException {
	    //	We do not allow adding directly. Use Role Assignment policy to add indirectly
		throw new UnsupportedException("addRole should not be called");
	}

	public void deleteRole(Role roleToDelete) throws RoleDoesNotExistException {
	    // Not allowing deletion
		throw new UnsupportedException("deleteRole should not be called");
	}

	public void assignUser(User aUser, Role aRole) throws UserDoesNotExistException, RoleDoesNotExistException, UserAlreadyHasRoleException {
	    // We do not allow assignment directly. Use Role Assignment policy to assign indirectly
		throw new UnsupportedException("assignUser should not be called");
	}

	public void deassignUser(User aUser, Role aRole) throws UserDoesNotExistException, RoleDoesNotExistException, UserDoesNotHaveRoleException {
	    // Not allowing deassignment
		throw new UnsupportedException("deassignUser should not be called");
	}

	public Permission createPermission(Operation anOperation, Object anObject) throws PermissionAlreadyExistsException {
	    // We do not allow creation directly. Use permission policy to create indirectly
	    throw new UnsupportedException("createPermission should not be called");
	}

	public void grantPermission(Operation anOperation, Object anObject, Role aRole) throws PermissionDoesNotExistException, RoleDoesNotExistException {
	    // We do not allow grant directly. Use role policy's referal to permission policy to grant
		throw new UnsupportedException("grantPermission should not be called");
	}

	public void grantPermission(Permission aPermission, Role aRole) throws PermissionDoesNotExistException, RoleDoesNotExistException {
	    // As above 
		throw new UnsupportedException("grantPermission should not be called");
	}

	public void revokePermission(Operation anOperation, Object anObject, Role aRole) throws PermissionDoesNotExistException, RoleDoesNotExistException, RoleDoesNotHavePermissionException {
	    // Not allowing revoke
	    // Revoke can be done either through a new set of policies, 
	    // or more complexly, to remove the PolicySetIdReference in RPS
		throw new UnsupportedException("revokePermission should not be called");
	}

	public void revokePermission(Permission aPermission, Role aRole) throws PermissionDoesNotExistException, RoleDoesNotExistException, RoleDoesNotHavePermissionException {
	    // As above
		throw new UnsupportedException("revokePermission should not be called");
	}

	// No support for session in this implementation
	public Session createSession(User aUser, Set ars) throws UserDoesNotExistException {
		throw new UnsupportedException("createSession should not be called");
	}

	public void deleteSession(User aUser, Session aSession) throws UserDoesNotExistException, SessionDoesNotExistException, UserDoesNotHaveSessionException {
		throw new UnsupportedException("deleteSession should not be called");
	}

	public void addActiveRole(User aUser, Session aSession, Role aRole) throws UserDoesNotExistException, RoleDoesNotExistException, SessionDoesNotExistException, UserDoesNotHaveRoleException, UserDoesNotHaveSessionException {
		throw new UnsupportedException("addActiveRole should not be called");
	}

	public void dropActiveRole(User aUser, Session aSession, Role aRole) throws UserDoesNotExistException, RoleDoesNotExistException, SessionDoesNotExistException, UserDoesNotHaveRoleException, UserDoesNotHaveSessionException, SessionDoesNotHaveRoleException {
		throw new UnsupportedException("dropActiveRole should not be called");
	}

	public boolean checkAccess(Session aSession, Operation anOperation, Object anObject) throws SessionDoesNotExistException {
	    return rbac.checkAccess(aSession, anOperation, anObject);
	}

	public boolean checkAccess(User aUser, Operation anOperation, Object anObject) throws UserDoesNotExistException {
	    return rbac.checkAccess(aUser, anOperation, anObject);
	}
	
	public boolean checkAccess(Role aRole, Operation anOperation, Object anObject) throws RoleDoesNotExistException {
	    return rbac.checkAccess(aRole, anOperation, anObject);
	}
	
	// For these administrative methods, use the side RBAC to implement them
	// This requires the side RBAC to be properly populated
	public Set assignedUsers(Role aRole) throws RoleDoesNotExistException {
		return rbac.assignedUsers(aRole);
	}

	public Set assingedRoles(User aUser) throws UserDoesNotExistException {
		return rbac.assingedRoles(aUser);
	}

	public Set rolePermissions(Role aRole) throws RoleDoesNotExistException {
		return rbac.rolePermissions(aRole);
	}

	public Set userPermissions(User aUser) throws UserDoesNotExistException {
	    return rbac.userPermissions(aUser);
	}

	public Set sessionRoles(Session aSession) throws SessionDoesNotExistException {
		throw new UnsupportedException("sessionRoles should not be called");
	}

	public Set sessionPermissions(Session aSession) throws SessionDoesNotExistException {
		throw new UnsupportedException("sessionPermissions should not be called");
	}

	public Set roleOperationsOnObject(Role aRole, Object anObject) throws RoleDoesNotExistException {
	    return rbac.roleOperationsOnObject(aRole, anObject);
	}

	public Set userOperationsOnObject(User aUser, Object anObject) throws UserDoesNotExistException {
	    return rbac.userOperationsOnObject(aUser, anObject);
	}

	public boolean userExists(Name name) {
		return rbac.userExists(name);
	}

	public User getUser(Name name) throws UserDoesNotExistException {
		return rbac.getUser(name);
	}

	public boolean roleExists(Name name) {
		return rbac.roleExists(name);
	}

	public Role getRole(Name name) throws RoleDoesNotExistException {
		return rbac.getRole(name);
	}

	public boolean permissionExists(Name name) {
		return rbac.permissionExists(name);
	}
	
	public Permission getPermission(Operation anOperation, Object anObject) throws PermissionDoesNotExistException {
	    return rbac.getPermission(anOperation, anObject);
	}

	public boolean sessionExists(Name name) {
		throw new UnsupportedException("sessionExists should not be called");
	}

	public Session getSession(Name name) throws SessionDoesNotExistException {
	    throw new UnsupportedException("getSession should not be called");
	}
	
	public Set getUsers() {
	    return rbac.getUsers();
	}
	
	public Set getRoles() {
	    return rbac.getRoles();
	}
	
	public Set getPermissions() {
	    return rbac.getPermissions();
	}
	
	public Set getSessions() {
	    throw new UnsupportedException("getSessions should not be called");
	}
	
	public int getActiveSessionStrategy() {
		throw new UnsupportedException("getActiveSessionStrategy should not be called");
	}

	public void terminateSession(Session s) {
		throw new UnsupportedException("terminateSession should not be called");
	}

	public void addInheritance(RoleHierarchical ascendant, RoleHierarchical descendant) throws RoleDoesNotExistException, RoleCycleException, RoleMultipleInheritanceException {
	    // We do not allow adding inheritance directly. Use PPS reference to add indirectly
	    throw new UnsupportedException("addInheritance should not be called");
	}

	public void deleteInheritance(RoleHierarchical ascendant, RoleHierarchical descendant) throws RoleDoesNotExistException, RoleInheritanceDoesNotExistException {
	    // Not allowing deletion
		throw new UnsupportedException("deleteInheritance should not be called");	}

	public RoleHierarchical addAscendant(Name ascendantName, RoleHierarchical descendant) throws RoleExistsException, RoleDoesNotExistException, RoleCycleException, RoleMultipleInheritanceException {
	    // See addInheritance
		throw new UnsupportedException("addAscendant should not be called");
	}

	public RoleHierarchical addDescendant(Name descendantName, RoleHierarchical ascendant) throws RoleExistsException, RoleDoesNotExistException, RoleCycleException, RoleMultipleInheritanceException {
	    // See addInheritance
	    throw new UnsupportedException("addDescendant should not be called");
	}

	public Set authorizedUsers(Role aRole) throws RoleDoesNotExistException {
		return rbac.authorizedUsers(aRole);
	}

	public Set authorizedRoles(User aUser) throws UserDoesNotExistException {
		return rbac.authorizedRoles(aUser);
	}

	public boolean isGeneral() {
		return rbac.isGeneral();
	}
	
	public Set getJuniors(RoleHierarchical senior) {
	    return rbac.getJuniors(senior);
	}
	
	public Set getSeniors(RoleHierarchical junior) {
	    return rbac.getSeniors(junior);
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
            RBACHierarchicalWithXACML rbac = new RBACHierarchicalWithXACML(p);
            
            //if (rbac.checkAccess(null, getFileAsString(requestFile))) {
            if (rbac.checkAccess("Anne", "sign", "purchase order")) {
                System.out.println("True");
            }
            else {
                System.out.println("False");
            }

            try {
	            Set		users = rbac.getUsers();
	            for (Iterator i = users.iterator(); i.hasNext();) {
	                User	u = (User)i.next();
	                System.out.println("User " + u.getNameString());
	                Set		r = rbac.assingedRoles(u);
	                for (Iterator j = r.iterator(); j.hasNext(); ) {
		                System.out.println(((Role)j.next()).getNameString());
	                }
	                Set		ps = rbac.userPermissions(u);
	                for (Iterator j = ps.iterator(); j.hasNext(); ) {
		                System.out.println(((Permission)j.next()).getNameString());
	                }
	            }
	            
	            Set		roles = rbac.getRoles();
	            for (Iterator i = roles.iterator(); i.hasNext();) {
	                Role	r = (Role)i.next();
	                System.out.println("Role " + r.getNameString());
	                Set		u = rbac.assignedUsers(r);
	                for (Iterator j = u.iterator(); j.hasNext(); ) {
		                System.out.println(((User)j.next()).getNameString());
	                }
	                Set		ps = rbac.rolePermissions(r);
	                for (Iterator j = ps.iterator(); j.hasNext(); ) {
		                System.out.println(((Permission)j.next()).getNameString());
	                }

	                RoleHierarchical rh = (RoleHierarchical)r;
	                Set		juniors = rbac.getJuniors(rh);
	                Set		seniors = rbac.getSeniors(rh);
	                System.out.println("Junior");
	                for (Iterator j = juniors.iterator(); j.hasNext(); ) {
		                System.out.println(((Role)j.next()).getNameString());
	                }
	                System.out.println("Senior");
	                for (Iterator j = seniors.iterator(); j.hasNext(); ) {
		                System.out.println(((Role)j.next()).getNameString());
	                }
	            }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
    	}
    }
}
