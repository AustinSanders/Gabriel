/*
 * Created on Jul 3, 2005
 *
 */
package c2.fw.secure;

import c2.fw.Brick;
import c2.fw.BrickCreationException;
import c2.fw.Identifier;
import c2.fw.InitializationParameter;
import c2.fw.JavaClassBrickFactory;

/**
 * The class now does nothing. It used to be a factory that creates bricks for a class, if the creation is allowed. 
 * 
 * @author Jie Ren
 */
public class SecureJavaClassBrickFactory extends JavaClassBrickFactory {
	/**
	 * Create a brick factory for a class
	 * 
	 * @param c the class for whose instances this factory will create
	 */
	public SecureJavaClassBrickFactory(Class c) {
		super(c);
	}

	/*
	DynamicPDP	pdp;
	
	public void setSecurePolicy(String policy) {
		Set	policies = new HashSet();
		policies.add(policy);
		pdp = new DynamicPDP(policies, null);
	}
	*/
	
	/**
	 * {@inheritDoc}
	 * 
	 * This method throws a BrickSecurityException if the creation fails
	 * because of security reasons
	 */
	public Brick create(Identifier id, InitializationParameter[] initParams)
			throws BrickCreationException {
		/*
		 * This check is not used. The check of creation is now conducted in 
		 * SecureManagedSystem, where all information can be easily obtained 
		if (ISecureBrick.class.isAssignableFrom(c)) {
			if (initParams != null ) {
				String	subject = null;
				String 	object = null;
				String	action = null;
				for (int i = 0; i<initParams.length; i++) {
					InitializationParameter ip = initParams[i];
					if (ip.getName().equals(ISubject.SUBJECT))
						subject = ip.getValue();
					if (ip.getName().equals(IPrivilege.OBJECT))
						object = ip.getValue();
					if (ip.getName().equals(IPrivilege.ACTION))
						action = ip.getValue();
				}
				if (pdp != null && subject != null && object != null && action != null) {
					String request = XACMLUtils.createRequest(subject, object, action);
					if (!pdp.evaluate(request)) {
						throw new BrickSecurityException(id, XACMLUtils.ACTION_ADD_BRICK);
					}
				}
			}
		}
		*/
		
		return super.create(id, initParams);
	}
}
