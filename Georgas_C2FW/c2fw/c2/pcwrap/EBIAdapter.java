package c2.pcwrap;

import c2.fw.*;
import java.lang.reflect.*;
import java.util.*;

public class EBIAdapter implements InvocationHandler{
	protected DelegateBrick brick;
	protected Class interfaceClass;		
	protected Interface iface;
	protected Identifier id;
	
	protected Object serLock = new Object();
	
	//Maps callIds to responseMessages
	protected HashMap holdingResponses = new HashMap();
	
	public EBIAdapter(DelegateBrick brick, Class interfaceClass, Interface iface){
		//this.id = new SimpleIdentifier(brick.getIdentifier() + ":" + interfaceClass.getName());
		this.brick = brick;
		this.iface = iface;
		this.interfaceClass = interfaceClass;
		this.id = new SimpleIdentifier(c2.util.UIDGenerator.generateUID("eah"));
		EBIWrapperUtils.addThreadMessageProcessor(brick, new CallResponseProcessor());
		//brick.addMessageProcessor(new CallResponseProcessor());
	}
	
	public Identifier getIdentifier(){
		return id;
	}
	
	public Class getInterfaceClass(){
		return interfaceClass;
	}
	
	static long counter = 0;
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
		synchronized(serLock){
			NamedPropertyMessage m = new NamedPropertyMessage(method.getName());
			Class[] parameterTypes = method.getParameterTypes();
			if(args == null){
				args = new Object[]{};
			}
			for(int i = 0; i < args.length; i++){
				Class c = parameterTypes[i];
				
				m.addParameter("paramType" + i, c2.util.ClassArrayEncoder.classToString(c));
				if(args[i] != null){
					m.addParameter("paramValue" + i, args[i]);
				}
				else{
					m.addParameter("paramValue" + i, new c2.pcwrap.NullParameterValue());
				}
			}
			m.addParameter("numParameters", args.length);
			
			m.addParameter("ebiMessageType", "outgoingCall");
			//String callId = c2.util.UIDGenerator.generateUID("call-" + id.toString());
			String callId = "call-" + id.toString() + (counter++);
			
			m.addParameter("callId", callId);
			m.addParameter("ebiAdapterHelperId", id);
			m.addParameter("targetInterface", interfaceClass.getName());

			//System.err.println("Brick: " + brick + "\nSending message: " + m.getName() + " and now waiting for response.");
			brick.sendToAll(m, iface);
			//Thread.yield();
			//System.err.println("SendToAll returned.");

			NamedPropertyMessage responseMessage = null;
			while(responseMessage == null){
			//while(holdingResponses.get(callId) == null){
				try{
					//System.out.println("Thread: " + Thread.currentThread() + " blocked waiting for call " + m.getName());
					serLock.wait();
					//System.out.println("Thread: " + Thread.currentThread() + " unblocked.");
					responseMessage = (NamedPropertyMessage)holdingResponses.remove(callId);
				}
				catch(InterruptedException e){
				}
			}
			//}while(responseMessage == null);
			//System.err.println("Brick: " + brick + "\n  got a response to message: " + m.getName());
			//Got a response
			//NamedPropertyMessage responseMessage;
			//synchronized(holdingResponses){
			//	responseMessage = (NamedPropertyMessage)holdingResponses.get(callId);
			//holdingResponses.remove(callId);
			//}
			boolean exceptionOccurred = responseMessage.getBooleanParameter("exceptionOccurred");
			if(!exceptionOccurred){
				Object retVal = responseMessage.getParameter("returnValue");
				//System.out.println("retval = " + retVal);
				if(retVal instanceof c2.pcwrap.NullParameterValue){
					return null;
				}
				else{
					return retVal;
				}
			}
			else{
				Throwable retVal = (Throwable)responseMessage.getParameter("returnValue");
				
				//This part here combines the stack traces of the calling thread and the
				//called thread, and takes out all the junk in between (the 85 proxies
				//and reflect calls and everything) so it looks close to a real procedure
				//call stack trace.
				Throwable dummy = new Throwable();
				StackTraceElement[] topStack = retVal.getStackTrace();
				StackTraceElement[] bottomStack = dummy.getStackTrace();
				c2.fw.SingleThreadVector combinedStackVector = new c2.fw.SingleThreadVector();
				
				for(int i = 0; i < topStack.length; i++){
					StackTraceElement elt = topStack[i];
					if(!elt.getClassName().startsWith("sun.reflect.")){
						if(!elt.getClassName().startsWith("java.lang.reflect.")){
							if(!elt.getClassName().startsWith("c2.pcwrap.")){
								if(!elt.getClassName().startsWith("$Proxy")){
									combinedStackVector.addElement(elt);
								}
							}
						}
					}
				}
				for(int i = 0; i < bottomStack.length; i++){
					StackTraceElement elt = bottomStack[i];
					if(!elt.getClassName().startsWith("sun.reflect.")){
						if(!elt.getClassName().startsWith("java.lang.reflect.")){
							if(!elt.getClassName().startsWith("c2.pcwrap.")){
								if(!elt.getClassName().startsWith("$Proxy")){
									combinedStackVector.addElement(elt);
								}
							}
						}
					}
				}
				
				StackTraceElement[] combinedStack = new StackTraceElement[combinedStackVector.size()];
				combinedStackVector.copyInto(combinedStack);
				
				retVal.setStackTrace(combinedStack);
				throw retVal;
			}
		}	
	}

	public void handleCallResponseMessage(NamedPropertyMessage m){
		//System.out.println("Got call response message: " + m);
		synchronized(serLock){
			String callId = (String)m.getParameter("callId");
			synchronized(holdingResponses){
				holdingResponses.put(callId, m);
			}
			serLock.notifyAll();
		}
	}
	
	class CallResponseProcessor implements MessageProcessor{
		public void handle(Message m){
			if(!(m instanceof NamedPropertyMessage)){
				return;
			}
			
			NamedPropertyMessage npm = (NamedPropertyMessage)m;
	
			try{
				Identifier ebiAdapterHelperId = (Identifier)npm.getParameter("ebiAdapterHelperId");
				if(ebiAdapterHelperId == null){
					//System.out.println("Got message without ebiAdapterHelperId: " + npm);
					return;
				}
				else if(ebiAdapterHelperId.equals(id)){
					handleCallResponseMessage(npm);
				}
			}
			catch(IllegalArgumentException iae){
				return;
			}
			catch(Exception e){
				e.printStackTrace();
				return;
			}
		}
	}
}

