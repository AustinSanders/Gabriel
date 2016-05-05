package c2demo.awacs;

import c2.fw.*;
import c2.legacy.*;

public class VisioMessageAnimator implements MessageListener{

	public boolean working = true;

	archstudio.comp.visio.COMBottomConnector visioConnector = null;

	String myName = "VisioMessageAnimator";

	// listener to AWACS message, handle them directly to Visio
	public VisioMessageAnimator(ArchitectureController controller){
		((MessageProvider)controller).addMessageListener(this);
		InitializationParameter[]  params = {
					new InitializationParameter("localPort", "5555"),
					new InitializationParameter("remotePort", "6666"),
					new InitializationParameter("remoteHost", "localhost"),
					new InitializationParameter("owner", myName),

		};
		visioConnector =
					new archstudio.comp.visio.COMBottomConnector(
							new SimpleIdentifier("VisioConnector"), params);
		visioConnector.init();
		visioConnector.begin();
	}

	public void messageSent(Message m){
		if(!working){
			return;
		}

		if(m instanceof AWACSMessage){
			AWACSMessage am = (AWACSMessage)m;
			String payload = am.getPayload();
			BrickInterfaceIdPair src = am.getSource();
			BrickInterfaceIdPair dest = am.getDestination();

			// construct a new message to deliver to Visio
			BrickInterfaceIdPair fake = new BrickInterfaceIdPair(new SimpleIdentifier(myName), AbstractC2DelegateBrick.BOTTOM_INTERFACE_ID);
			NamedPropertyMessage nm = new NamedPropertyMessage("AWACS_Notification");
			nm.setSource(fake);
			nm.addParameter("Event", "Messaging");
			nm.addParameter("Source", src.getBrickIdentifier().toString());
			nm.addParameter("Destination", dest.getBrickIdentifier().toString());
			nm.addParameter("Message", payload);
			nm.addParameter("URL", "JustFakeToSimplifyVBACode");
			visioConnector.handle(nm);
		}
	}

}

