/*
 * Created on Jul 3, 2005
 *
 */
package c2.fw.secure;

import c2.fw.Brick;
import c2.fw.BrickInterfaceIdPair;
import c2.fw.Identifier;
import c2.fw.InitializationParameter;
import c2.fw.Interface;
import c2.fw.Message;
import c2.fw.MessageProcessor;
import c2.fw.NamedPropertyMessage;
import c2.fw.SimpleWeld;
import c2.fw.Weld;
import c2.fw.secure.xacml.XACMLUtils;
import c2.legacy.Utils;

public class SecureC2Connector extends SecureC2Brick implements ISecureConnector {

	public SecureC2Connector(Identifier id) {
		super(id);
		prepare();
	}

	public SecureC2Connector(Identifier id, InitializationParameter[] params){
		super(id, params);
		prepare();
	}

	protected void prepare() {
		thisTop = new BrickInterfaceIdPair(getIdentifier(), topIface.getIdentifier());
		thisBottom = new BrickInterfaceIdPair(getIdentifier(), bottomIface.getIdentifier());
		addMessageProcessor(new MessageProcessor() {
			public void handle(Message m){
			    // Make a duplicate so the interal routing policy can have correct src/dst
			    // But this loses the original context (src/dst), so we opt not to set src/dst
			    /*
			    Message m = om.duplicate();
				if(isNotification(om)){
				    m.setSource(thisTop);
				    m.setDestination(thisBottom);
				}
				else if (isRequest(om)) {
				    m.setSource(thisBottom);
				    m.setDestination(thisTop);
				}
				*/
				
				// should we forward this message?
				if (!forward(m)) {
					return;
				}

				if(isNotification(m)){
					sendToAll(m, bottomIface);
				}
				else if(isRequest(m)){
					sendToAll(m, topIface);
				}
			}
		});
	}
	
	public boolean acceptBrick(Interface myInterface, Brick otherBrick, Interface otherInterface) {
		if (pdp != null) {
			BrickInterfaceIdPair thisBi = new BrickInterfaceIdPair(getIdentifier(), 
					myInterface.getIdentifier());
			BrickInterfaceIdPair thatBi = new BrickInterfaceIdPair(otherBrick.getIdentifier(), 
					otherInterface.getIdentifier());
			Weld w = new SimpleWeld(thisBi, thatBi);
			String	request = XACMLUtils.createRequestForWeld(XACMLUtils.SUBJECT_ID_SMS, 
					w, XACMLUtils.ACTION_ADD_WELD);
			if (!isAccessAllowed(request)) {
				return false;
			}
		}
		return true;
	}
	
	private BrickInterfaceIdPair thisTop;
	private BrickInterfaceIdPair thisBottom;

	private boolean isNotification(Message m){
		if(Utils.isC2Notification(m)){
			return true;
		}
		if(m.getDestination().equals(thisTop)){
			return true;
		}
		return false;
	}
	
	private boolean isRequest(Message m){
		if(Utils.isC2Request(m)) {
			return true;
		}
		if(m.getDestination().equals(thisBottom)){
			return true;
		}
		return false;
	}

	private boolean forward(Message m) {
		boolean		forwarding = true;
		if (pdp != null && m instanceof NamedPropertyMessage) {
			// Generate an XML segment for the message
			NamedPropertyMessage npm = (NamedPropertyMessage)m;
			String		accessRequest = XACMLUtils.createRequestForRouteInternal(
			        this, npm);
			try {
				if (!isAccessAllowed(accessRequest)) {
					forwarding = false;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				forwarding = false;
			}
		}
		return forwarding;
	}
}
