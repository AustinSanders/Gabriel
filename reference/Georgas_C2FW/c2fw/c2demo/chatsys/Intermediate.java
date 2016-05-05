package c2demo.chatsys;

//Import framework classes
import java.util.Calendar;
import java.util.GregorianCalendar;

import c2.fw.*;
import java.util.*;

//Import "legacy" (i.e. standard 2-interface) C2
//support
import c2.legacy.*;

public class Intermediate extends AbstractC2DelegateBrick{

	//Boilerplate constructor
	public Intermediate(Identifier id){
		super(id);
		addMessageProcessor(new IntermediateC2ComponentMessageProcessor());
	}

	class IntermediateC2ComponentMessageProcessor implements MessageProcessor {
		public void handle(Message m){
			//Only handle chat messages from the bottom interface

			//bottomIface is the interface variable representing the
			//bottom interface of this component, available in
			//the AbstractC2Brick superclass.
			if(m.getDestination().getInterfaceIdentifier().equals(bottomIface.getIdentifier())){
				if(m instanceof NamedPropertyMessage){
					NamedPropertyMessage npm = (NamedPropertyMessage)m;
					if(npm.getName().equals("ChatMessage")){
						//This is a message we should relay
						String text = (String)npm.getParameter("text");

						GregorianCalendar now = new GregorianCalendar();
						StringBuffer buf = new StringBuffer();
						buf.append("[" + now.get(Calendar.HOUR) + ":");
						buf.append(now.get(Calendar.MINUTE) + ":");
						buf.append(Integer.toString(now.get(Calendar.SECOND)) + "]");

						String mod = new String(buf.toString() + " " + text);
						NamedPropertyMessage newM = new NamedPropertyMessage("ChatMessage");
						newM.addParameter("text", mod);

						sendToAll(newM, topIface);

					}
				}
			} else {
				sendToAll(m, bottomIface);
			}
		}
	}
}