/*
 * Created on Jul 3, 2005
 *
 */
package c2.fw.secure;

import java.util.HashSet;
import java.util.Set;

import c2.fw.Identifier;
import c2.fw.InitializationParameter;
import c2.fw.SimpleIdentifier;
import c2.fw.secure.xacml.DynamicPDP;
import c2.legacy.AbstractC2DelegateBrick;

/**
 * A secure C2 brick that has subject, principals, and privileges.
 * 
 * @author Jie Ren
 */
public class SecureC2Brick extends AbstractC2DelegateBrick implements ISecureBrick, IPEP {
	protected   ISubject	subject;
	protected	Set			principals = new HashSet();
	protected   Set			privileges = new HashSet();
	protected	Set			structurePolicy = new HashSet();
	protected	Set			typePolicy = new HashSet();
	protected	Set			policies = new HashSet();
	
	// the security policy engine
	protected 	DynamicPDP	pdp;
	
	/**
	 * Create a secure C2 brick with an identifier
	 *
	 * @param thisId the identifier of the new brick
	 */
	public SecureC2Brick(Identifier thisId) {
		super(thisId);
	}

	/**
	 * Create a secure C2 brick with an identifier and an array of parameters
	 * @param id		the identifier of the new brick
	 * @param params	the array of parameters used to initialize the brick
	 */
	public SecureC2Brick(Identifier id, InitializationParameter[] params){
		super(id);
		
		for(int i = 0; i < params.length; i++){
			String name = params[i].getName();
			String value = params[i].getValue();
			
			if(name.equals(IPrincipal.PRINCIPAL)){
				principals.add(new SimplePrincipal(value));
			}

			if(name.equals(ISubject.SUBJECT)){
				subject = new SimpleSubject(value);
			}
			
			if(name.equals(IPrivilege.PRIVILEGE)) {
				privileges.add(SimplePrivilege.createPrivilege(value));
			}

			if(name.equals(IPolicy.STRUCTURE_POLICY)) {
				structurePolicy.add(value);
			}
			
			if(name.equals(IPolicy.TYPE_POLICY)) {
				typePolicy.add(value);
			}
			
			if(name.indexOf(IPrivilege.SAFEGUARD) != -1) {
				SimpleIdentifier	interfaceId = new SimpleIdentifier(
						name.substring(IPrivilege.SAFEGUARD.length()));
				SimpleSecureInterface	secureIface = null;
				if (interfaceId.equals(TOP_INTERFACE_ID)) {
					if (!(topIface instanceof SimpleSecureInterface)) {
						topIface = new SimpleSecureInterface(TOP_INTERFACE_ID, this);
					}
					secureIface = (SimpleSecureInterface)topIface;
				}
				if (interfaceId.equals(BOTTOM_INTERFACE_ID)) {
					if (!(bottomIface instanceof SimpleSecureInterface)) {
						bottomIface = new SimpleSecureInterface(BOTTOM_INTERFACE_ID, this);
					}
					secureIface = (SimpleSecureInterface)bottomIface;
				}
				secureIface.addSafeguard(SimplePrivilege.createPrivilege(value));
			}
		}

		/* A brick pdp is no longer created here. It is now created and set 
		 * in SecureManagedSystem. So we 1) reduce a pdp constructor call 2)
		 * no need to pass the policy as initParam, which is rather awkward for
		 * RBAC policies
		// make a policy
		if (!structurePolicy.isEmpty() || !typePolicy.isEmpty()) {
			// Here we only handle brickPolicy or typePolicy. The architectural 
			// policy is handled by ArchitectureController
			DynamicPDP	pdp;
			if (!structurePolicy.isEmpty()) {
				// Use structure policy, and have typePolicy as candidate
				pdp = new DynamicPDP(structurePolicy, typePolicy);
			}
			else {
				// Use the type policy
				pdp = new DynamicPDP(typePolicy, new HashSet());
			}
			setPDP(pdp);
			
			policies.addAll(structurePolicy);
			policies.addAll(typePolicy);
		}
		*/
	}
	
	public ISubject getSubject() {
		return subject;
	}
	
	public Set getPrincipals() {
		return principals;
	}

	public void setPrincipals(Set principals) {
		this.principals.clear();
		this.principals.addAll(principals);
	}
	
	public Set getPrivileges() {
		return privileges;
	}
	
	public Set getPolicies() {
		return policies;
	}
	
	public boolean isAccessAllowed(String accessRequest) {
		if (pdp == null)
			return true;
		return pdp.evaluate(accessRequest);
	}

    /* (non-Javadoc)
     * @see c2.fw.secure.IPEP#setPDP(c2.fw.secure.xacml.DynamicPDP)
     */
    public void setPDP(DynamicPDP pdp) {
        this.pdp = pdp;
        policies.clear();
        policies.addAll(pdp.getPolicies());
    }

    /* (non-Javadoc)
     * @see c2.fw.secure.IPEP#getPDP()
     */
    public DynamicPDP getPDP() {
        return pdp;
    }
}
