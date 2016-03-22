/*
 * Created on Jul 10, 2005
 *
 */
package c2.fw.secure;

import c2.fw.ArchitectureListener;
import c2.fw.ArchitectureManager;
import c2.fw.Brick;
import c2.fw.Identifier;
import c2.fw.LocalArchitectureManager;
import c2.fw.Message;
import c2.fw.MessageHandler;
import c2.fw.MessageListener;
import c2.fw.NamedPropertyMessage;
import c2.fw.OneQueuePerInterfaceMessageHandler;
import c2.fw.secure.xacml.DynamicPDP;
import c2.fw.secure.xacml.XACMLUtils;

public class SecureOneQueuePerInterfaceMessageHandler extends OneQueuePerInterfaceMessageHandler 
	implements MessageHandler, MessageListener, ArchitectureListener, IPEP	{
	public SecureOneQueuePerInterfaceMessageHandler(){
		super();
	}

	protected	DynamicPDP		pdp;

	public void setPDP(DynamicPDP pdp) {
	    this.pdp = pdp;
	}
	
    /* (non-Javadoc)
     * @see c2.fw.secure.IPEP#getPDP()
     */
    public DynamicPDP getPDP() {
        return pdp;
    }

    public synchronized void messageSent(Message m){
        /*
		Identifier	sourceBrickId = m.getSource().getBrickIdentifier();
		Brick	sourceBrick = manager.getBrick(sourceBrickId);
		Identifier	destBrickId = m.getDestination().getBrickIdentifier();
		Brick	destBrick = manager.getBrick(destBrickId);
		Interface	destIface = destBrick.getInterface(m.getDestination().
				getInterfaceIdentifier());
		if (destIface instanceof SimpleSecureInterface) {
			SimpleSecureInterface	secureIface = (SimpleSecureInterface)destIface;
			if (sourceBrick instanceof ISecureBrick) {
				ISecureBrick	secureSourceBrick = (ISecureBrick)sourceBrick;
				Set				provided = secureSourceBrick.getPrivileges();
				Set				required = secureIface.getSafeguards();
				if (!provided.containsAll(required)) {
					// reject, if secure requirements are set yet not met
					return;
				}
			}
		}
		*/
        if (pdp != null) {
            Brick	src = manager.getBrick(m.getSource().getBrickIdentifier());
            Brick	dst = manager.getBrick(m.getDestination().getBrickIdentifier());
            SecureC2Brick	srcBrick = null;
            if (src instanceof SecureC2Brick)
            	srcBrick = (SecureC2Brick)src;
            SecureC2Brick 	dstBrick = null;
            if (dst instanceof SecureC2Brick)
            	dstBrick = (SecureC2Brick)dst;
            if (srcBrick != null || dstBrick != null) {
                // we only check when one secure brick communicates with another brick
                boolean check = true;
                if (srcBrick != null && dstBrick != null) {
	                ISubject	srcSubject = srcBrick.getSubject();
	                ISubject	dstSubject = dstBrick.getSubject();
	                if (srcSubject != null && dstSubject != null && srcSubject.equals(dstSubject)) {
	                    // or two secure bricks with different subjects communicate with each other
	                    check = false;
	                }
                }
                // otherwise, a blind check is too slow 
                if (check) {
					String		accessRequest = XACMLUtils.createRequestForRouteExternal(
							(NamedPropertyMessage)m, srcBrick, dstBrick);
			        if (!pdp.evaluate(accessRequest)) {
			            // Just siliently return, so the thread would not terminate
			            // throw new RouteSecurityException((NamedPropertyMessage)m, 
			            //        XACMLUtils.ACTION_ROUTE_MESSAGE);
			            return;
			        }
                }
            }
        }
		super.messageSent(m);
	}

	protected	LocalArchitectureManager manager;
	public synchronized void brickAdded(ArchitectureManager manager, Identifier id){
		this.manager = (LocalArchitectureManager)manager;
		super.brickAdded(manager, id);
	}
}
