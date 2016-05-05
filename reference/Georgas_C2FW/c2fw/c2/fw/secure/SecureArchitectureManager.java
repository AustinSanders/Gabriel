/*
 * Created on Jul 3, 2005
 *
 */
package c2.fw.secure;

import c2.fw.Brick;
import c2.fw.BrickInterfaceIdPair;
import c2.fw.Interface;
import c2.fw.SimpleArchitectureManager;
import c2.fw.Weld;
import c2.fw.secure.xacml.DynamicPDP;
import c2.fw.secure.xacml.XACMLUtils;

public class SecureArchitectureManager extends SimpleArchitectureManager 
				implements IPEP {
    protected	DynamicPDP	pdp;
    
	// When modifying ArchStudio, the components are c2.fw, archstudio, and c2demo
	// should remain in their respective packages. There is a loader boundary.
	public SecureArchitectureManager(){
		super();
		registerBrickLoader(new SecureJavaNetBrickLoader());
	}
	
	public void setPDP(DynamicPDP pdp) {
	    this.pdp = pdp;
	}
	
	public DynamicPDP	getPDP() {
	    return pdp;
	}

	public synchronized void addWeld(Weld w){
		if(w == null){
			throw new IllegalArgumentException("Null parameter.");
		}

		// First, check if gloabally we do not allow, 
	    if (pdp != null) {
	        String	addWeldRequest = XACMLUtils.createRequestForWeld(
	                XACMLUtils.SUBJECT_ID_SMS, w, XACMLUtils.ACTION_ADD_WELD);
	        if (!pdp.evaluate(addWeldRequest)) {
	            throw new WeldSecurityException(w, XACMLUtils.ACTION_ADD_WELD);
	        }
	    }
	    
		BrickInterfaceIdPair firstEndpoint = w.getFirstEndpoint();
		BrickInterfaceIdPair secondEndpoint = w.getSecondEndpoint();
		
		Brick firstBrick = (Brick)bricksTable.get(firstEndpoint.getBrickIdentifier());
		if(firstBrick == null){
			throw new IllegalArgumentException("EndpointThing brick does not exist: " + firstEndpoint);
		}
		
		Interface firstIface = firstBrick.getInterface(firstEndpoint.getInterfaceIdentifier());
		if(firstIface == null){
			throw new IllegalArgumentException("EndpointThing interface does not exist: " + firstEndpoint);
		}
		
		Brick secondBrick = (Brick)bricksTable.get(secondEndpoint.getBrickIdentifier());
		if(secondBrick == null){
			throw new IllegalArgumentException("EndpointThing brick does not exist: " + secondEndpoint);
		}

		Interface secondIface = secondBrick.getInterface(secondEndpoint.getInterfaceIdentifier());
		if(secondIface == null){
			throw new IllegalArgumentException("EndpointThing interface does not exist: " + secondEndpoint);
		}

		// Second, check whether any locally involved would disallow
		// Ask two bricks whether they will accept the weld request, 
		if (firstBrick instanceof ISecureConnector ) {
			ISecureConnector isc = (ISecureConnector)firstBrick;
			if (!isc.acceptBrick(firstIface, secondBrick, secondIface))
			    throw new WeldSecurityException(w, XACMLUtils.ACTION_ADD_WELD);
		}
		if (secondBrick instanceof ISecureConnector) {
			ISecureConnector isc = (ISecureConnector)secondBrick;
			if (!isc.acceptBrick(secondIface, firstBrick, firstIface))
			    throw new WeldSecurityException(w, XACMLUtils.ACTION_ADD_WELD);
		}
		
		welds.addElement(w);
		
		firstIface.addConnectedInterface(new BrickInterfaceIdPair(secondEndpoint.getBrickIdentifier(),
			secondEndpoint.getInterfaceIdentifier()));
		secondIface.addConnectedInterface(new BrickInterfaceIdPair(firstEndpoint.getBrickIdentifier(),
			firstEndpoint.getInterfaceIdentifier()));
		
		fireWeldAdded(w);
	}
}
