
package c2.pcwrap;

import c2.fw.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Provides several useful static methods for enabling components to both produce and
 * service Event-based Procedure Calls (EPCs).
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class EBIWrapperUtils{

	/**
	 * Add an asynchronous event listener to an interface on
	 * a given brick.  
	 * 
	 * <p>This is useful to process messages
	 * sent by a brick that has deployed an EPC service (using
	 * the <code>deployService</code> call) specifying one or more
	 * <code>stateChangeInterfaces</code>.  We will call this brick
	 * the 'called brick.'  Assume the called
	 * brick calls <code>deployService</code> and specifies
	 * that one of the <code>stateChangeInterfaces</code> is
	 * <code>FooListener.class</code>.  This allows caller bricks
	 * (or other bricks) to add <code>FooListener</code>s to
	 * the interface on which they would receive, e.g., 
	 * <code>FooEvents</code>.  Once the given <code>FooListener</code>
	 * is added, it will be automatically called just as if it were
	 * a local <code>FooListener</code> in the called brick.
	 * 
	 * @param b The brick that will receive events from the
	 * brick that has deployed an EPC service.
	 * @param targetIface The interface on the given brick that
	 * will receive messages from the brick that has deployed
	 * an EPC service.
	 * @param listenerClass The listener class implemented by
	 * <code>listenerObject</code>; e.g., <code>FooListener.class</code>
	 * @param listenerObject The actual listener object that implements
	 * the <code>listenerClass</code> interface.
	 */
	public static void addListener(DelegateBrick b, Interface targetIface, 
	Class listenerClass, Object listenerObject){
		if(listenerObject == null){
			throw new IllegalArgumentException("Attempt to add a null listener object");
		}
		synchronized(b){
			EBIStateChangeClientAdapter stateChangeClientAdapter = null;
			
			//See if there's already a message processor for this
			//brick + interface + listener-class
			MessageProcessor[] mps = b.getMessageProcessors();
			for(int i = 0; i < mps.length; i++){
				if(mps[i] instanceof EBIStateChangeClientAdapter){
					EBIStateChangeClientAdapter potentialStateChangeClientAdapter =
						(EBIStateChangeClientAdapter)mps[i];
					if(targetIface.getIdentifier().equals(potentialStateChangeClientAdapter.getTargetInterface().getIdentifier())){
						if(listenerClass.equals(potentialStateChangeClientAdapter.getListenerClass())){
							//We already have a message processor enabled for this interface/
							//listener class combination
							stateChangeClientAdapter = potentialStateChangeClientAdapter;
							break;
						}
					}
				}
			}
			boolean found = (stateChangeClientAdapter != null);
			if(!found){
				stateChangeClientAdapter = new EBIStateChangeClientAdapter(b, targetIface, listenerClass);
			}
			stateChangeClientAdapter.addListener(listenerObject);
			if(!found){
				b.addMessageProcessor(stateChangeClientAdapter);
			}
		}
	}
	
	/**
	 * Removes an asynchronous event listener from an interface
	 * on a given brick.  
	 * 
	 * <p>See <code>addListener</code> for a 
	 * description on how to add a listener.
	 * @param b The brick on which the listener was added.
	 * @param targetIface The interface on which the listener was added.
	 * @param listenerClass The listener class implemented by the listener.
	 * @param listenerObject The listener object to remove.
	 */
	public static void removeListener(DelegateBrick b, Interface targetIface, 
	Class listenerClass, Object listenerObject){
		if(listenerObject == null){
			throw new IllegalArgumentException("Attempt to add a null listener object");
		}
		synchronized(b){
			EBIStateChangeClientAdapter stateChangeClientAdapter = null;
			
			//Find the message processor that would keep this
			//listener
			MessageProcessor[] mps = b.getMessageProcessors();
			for(int i = 0; i < mps.length; i++){
				if(mps[i] instanceof EBIStateChangeClientAdapter){
					EBIStateChangeClientAdapter potentialStateChangeClientAdapter =
						(EBIStateChangeClientAdapter)mps[i];
					if(targetIface.getIdentifier().equals(potentialStateChangeClientAdapter.getTargetInterface().getIdentifier())){
						if(listenerClass.equals(potentialStateChangeClientAdapter.getListenerClass())){
							//We have a message processor enabled for this interface/
							//listener class combination
							stateChangeClientAdapter = potentialStateChangeClientAdapter;
							break;
						}
					}
				}
			}
			if(stateChangeClientAdapter == null){
				//Didn't find it, must not be a listener on this object.
				return;
			}
			synchronized(stateChangeClientAdapter){
				stateChangeClientAdapter.removeListener(listenerObject);
				int numListeners = stateChangeClientAdapter.getNumListeners();
				if(numListeners == 0){
					//No more listeners; remove message processor					
					b.removeMessageProcessor(stateChangeClientAdapter);
				}
			}
		}
	}

	/**
	 * Creates a local event-provider proxy that allows brick
	 * code to use conventional <code>addXXXListener</code> and
	 * <code>removeXXXListener</code> calls to handle asynchronous
	 * events coming from other bricks that have deployed EPC
	 * services.
	 * 
	 * <p>The methods <code>addListener</code> and <code>removeListener</code>
	 * allow users to add conventional Java-style listeners to a brick so
	 * that they can receive asynchronous messages from other bricks that
	 * have deployed EPC services as method calls.  However, these methods
	 * are not like traditional <code>addXXXListener</code> and
	 * <code>removeXXXListener</code> calls because they require knowledge
	 * of bricks, interfaces, etc.
	 * 
	 * <p>This call creates a message provider proxy instance that allows
	 * users to make ordinary-looking 
	 * <code>addXXXListener</code>/<code>removeXXXListener</code> calls
	 * but still receive asynchronous event calls from bricks that have
	 * deployed an EPC service that uses <code>stateChangeListeners</code>.
	 * 
	 * <p>To use this call, a message provider interface must be defined
	 * that includes the following listener-pattern methods:
	 * 
	 * <p><code>public void addFooListener(FooListener l);</code>
	 * <br><code>public void removeFooListener(FooListener l);</code>
	 * 
	 * <p>The returned object will be an instance of this provider interface.
	 * 
	 * <p>So, assume the following interface is defined:
	 * 
	 * <p><pre>public interface FooEventProvider{
	 *   public void addFooListener(FooListener l);
	 *   public void removeFooListener(FooListener l);
	 * }</pre>
	 * 
	 * <p>And a brick deploys a service thusly:
	 * 
	 * <p><pre>deployService(this, callIface, callIface, trueObject,
	 *   new Class[]{SomeAPI.class}, new Class[]{FooListener.class});</pre>
	 *   
	 * <p>Then a client brick can add FooListeners as such:
	 * 
	 * <p><pre>FooEventProvider fooProvider = 
	 *   (FooEventProvider)EBIWrapperUtils.createStateChangeProviderProxy(
	 *   thisBrick, receivingIface, FooEventProvider.class);
	 * 
	 * FooListener someFooListener = ...;
	 *   ...
	 * fooProvider.addFooListener(someFooListener);
	 *   ...
	 * fooProvider.removeFooListener(someFooListener);</pre>
	 * 
	 * @param b The brick in which to create the proxy object.
	 * @param targetIface The interface that will receive asynchronous
	 * messages from another brick that has deployed an EPC service.
	 * @param providerInterfaceClass The provider interface class
	 * that the returned object will implement (see above).
	 * @return An object implementing <code>providerInterfaceClass</code>'s
	 * interface that can be used to add and remove listeners.
	 */
	public static Object createStateChangeProviderProxy(DelegateBrick b, Interface targetIface,
	Class providerInterfaceClass){
		if(providerInterfaceClass == null){
			throw new IllegalArgumentException("Provider interface class cannot be null.");
		}
		if(!providerInterfaceClass.isInterface()){
			throw new IllegalArgumentException("Provider interface class must be an interface class.");
		}
		EBIStateChangeProviderProxy providerProxy = new EBIStateChangeProviderProxy(b, targetIface);
		return Proxy.newProxyInstance(providerInterfaceClass.getClassLoader(),
      new Class[]{providerInterfaceClass}, providerProxy);
	}
	
	/**
	 * Create a local proxy within a given <CODE>DelegateBrick</CODE> that can make calls,
	 * via events, to a stub in another component.  So, if another component has called
	 * <CODE>EBIWrapperUtils.deployService</CODE> to deploy a service, this routine can
	 * be called in a given component to create a proxy to that service.  For example,
	 * let's assume that another component has exposed an interface called XArchFlatInterface.
	 * The call to this method would be:
	 * <P><CODE>XArchFlatInterface flat = (XArchFlatInterface)EBIWrapperUtils.addExternalService(thisBrick,
	 * MY_INTERFACE, XArchFlatInterface.class);</CODE>.
	 * @param b Brick within which to create the proxy, usually <CODE>this</CODE>.
	 * @param iface Interface on Brick <CODE>b</CODE> on which the outgoing calls will be made.
	 * @param serviceClass Class of the remote interface to proxy.
	 * @return An object implementing the class specified in the <CODE>serviceClass</CODE> parameter.
	 */
	public static Object addExternalService(DelegateBrick b, Interface iface, Class serviceClass){
		checkMessageProcessors(b);
		
		boolean found = false;
		DelegateBrickListener[] dbls = b.getDelegateBrickListeners();
		for(int i = 0; i < dbls.length; i++){
			if(dbls[i] instanceof EBIDelegateBrickListener){
				found = true;
				break;
			}
		}
		if(!found){
			b.addDelegateBrickListener(new EBIDelegateBrickListener());
		}
		
		EBIAdapter ebiAdapter = new EBIAdapter(b, serviceClass, iface);
		Object o  = Proxy.newProxyInstance(serviceClass.getClassLoader(),
			new Class[] { serviceClass }, ebiAdapter);
 		return o;
	}
	
	private static void checkMessageProcessors(DelegateBrick b){
		MessageProcessor[] mps = b.getMessageProcessors();
		ThreadMessageProcessor tmp = null;
		for(int i = 0; i < mps.length; i++){
			if(!(mps[i] instanceof ThreadMessageProcessor)){
				if(tmp == null){
					tmp = new ThreadMessageProcessor();
					tmp.setBrickName(b.getIdentifier().toString());
				}
				b.removeMessageProcessor(mps[i]);
				tmp.addMessageProcessor(mps[i]);
			}
		}
		if(tmp != null){
			b.addMessageProcessor(tmp);
		}
	}

	/**
	 * Deploy an EPC service that can be called by other components.  A "service"
	 * is one or more Java interfaces implemented by an object.  In addition
	 * to responding to calls on these interfaces, this function can automatically
	 * create "listeners" to watch the object for state changes and emit state change
	 * events when one of the listeners is called.  The object providing
	 * the service must implement the following "listener" pattern methods:
	 * <P><CODE>public void addFooListener(FooListener l);</CODE>
	 * <BR><CODE>public void removeFooListener(FooListener l);</CODE>
	 * <P>In this case, you can pass <CODE>FooListener.class</CODE> as one of the
	 * array elements in the <CODE>stateChangeInterfaces</CODE> parameter of this
	 * call, and Foo events will be emitted on the <CODE>stateChangeIface</CODE>.
	 * 
	 * @param b Brick deploying the EPC service.
	 * @param callIface Interface on brick <CODE>b</CODE> on which calls will arrive and
	 * call responses will be sent.
	 * @param stateChangeIface Interface on brick <CODE>b</CODE> on which state change notifications
	 * will be sent (may be the same as <CODE>callIface</CODE>).
	 * @param api The object actually implementing the service (may be <CODE>b</CODE>).
	 * @param interfaces Java interface(s) that are implemented by <CODE>api</CODE> that are deployed
	 * as part of the service.
	 * @param stateChangeInterfaces "Listener" interface(s) to monitor and emit state change messages
	 * for. Zero length arrays are acceptable here.
	 */
	public static void deployService(DelegateBrick b, Interface callIface, Interface stateChangeIface, 
	Object api, Class[] interfaces, Class[] stateChangeInterfaces){
		for(int i = 0; i < interfaces.length; i++){
			CallProcessor cp = new CallProcessor(b, callIface, api, interfaces[i]);
			addThreadMessageProcessor(b, cp);

			//Add an exclusive interest filter
			EBIInterestLifecycleAdapter eila = new EBIInterestLifecycleAdapter(b, callIface, 
				new EBIInterestFilter(interfaces[i].getName().toString()));
			b.addLifecycleProcessor(eila);
		}
		for(int i = 0; i < stateChangeInterfaces.length; i++){
			EBIStateChangeAdapter sca = new EBIStateChangeAdapter(b, stateChangeIface, api, stateChangeInterfaces[i]);
			Vector v = (Vector)b.getProperty("stateChangeAdapters");
			if(v == null){
				v = new Vector();
			}
			v.addElement(sca);
			b.setProperty("stateChangeAdapters", v);
		}
	}
	
	/**
	 * Undeploy a service previously deployed with <CODE>deployService(...)</CODE>.
	 * @param b Brick from which to undeploy the service.
	 * @param api Object implementing the service.
	 */
	public static void undeployService(DelegateBrick b, Object api){
		MessageProcessor[] mps = b.getMessageProcessors();
		//Remove the ThreadMessageProcessors
		for(int i = 0; i < mps.length; i++){
			if(mps[i] instanceof ThreadMessageProcessor){
				MessageProcessor[] intMps = ((ThreadMessageProcessor)mps[i]).getMessageProcessors();
				boolean remove = true;
				for(int j = 0; j < intMps.length; j++){
					if(intMps[j] instanceof CallProcessor){
						CallProcessor cp = (CallProcessor)intMps[j];
						if(cp.getApi() == api){
							remove = true;
							break;
						}
					}
				}
				if(remove){
					((ThreadMessageProcessor)mps[i]).terminate(true);
					b.removeMessageProcessor(mps[i]);
				}
			}
		}
		
		//Remove the appropriate stateChange adapters
		Vector v = (Vector)b.getProperty("stateChangeAdapters");
		if(v == null){
			return;
		}
		Object[] arr = v.toArray();
		for(int i = 0; i < arr.length; i++){
			EBIStateChangeAdapter sca = (EBIStateChangeAdapter)arr[i];
			if(sca.getApi() == api){
				sca.destroy();
				v.removeElement(sca);
			}
		}
		if(v.size() != 0){
			b.setProperty("stateChangeAdapters", v);
		}
		else{
			b.removeProperty("stateChangeAdapters");
		}
	}
	
	public static void addThreadMessageProcessor(DelegateBrick b, MessageProcessor mp){
		addThreadMessageProcessor(b, mp, c2.fw.ThreadPriorities.NORM_PRIORITY);
	}

	public static void addThreadMessageProcessor(DelegateBrick b, MessageProcessor[] mps){
		addThreadMessageProcessor(b, mps, c2.fw.ThreadPriorities.NORM_PRIORITY);
	}		
	
	public static void addThreadMessageProcessor(DelegateBrick b, MessageProcessor mp, int priority){
		ThreadMessageProcessor tmp = new ThreadMessageProcessor();
		tmp.setBrickName(b.getIdentifier().toString());
		tmp.addMessageProcessor(mp);
		b.addMessageProcessor(tmp);
		tmp.setPriority(priority);
	}

	public static void addThreadMessageProcessor(DelegateBrick b, MessageProcessor[] mps, int priority){
		ThreadMessageProcessor tmp = new ThreadMessageProcessor();
		tmp.setBrickName(b.getIdentifier().toString());
		for(int i = 0; i < mps.length; i++){
			tmp.addMessageProcessor(mps[i]);
		}
		
		b.addMessageProcessor(tmp);
		tmp.setPriority(priority);
	}
	
	
	static int counter = 0;
	
	public static Method guessMethod(Object proxy, String methodName, Object[] params){
		Method[] methods = proxy.getClass().getMethods();
		Method m = null;
		for(int i = 0; i < methods.length; i++){
			if(methods[i].getName().equals(methodName)){
				Class[] methodParamTypes = methods[i].getParameterTypes();
				if(methodParamTypes.length == params.length){
					boolean allMatch = true;
					for(int j = 0; j < params.length; j++){
						if(params[j] == null){
							//It can match the class if and only if the class
							//isn't a basic type.
							if(methodParamTypes[j].isPrimitive()){
								allMatch = false;
								break;
							}
						}
						else{
							if(methodParamTypes[j].isPrimitive()){
								if(methodParamTypes[j].equals(Boolean.TYPE)){
									if(!(params[i] instanceof Boolean)){
										allMatch = false;
										break;
									}
								}
								else if(methodParamTypes[j].equals(Byte.TYPE)){
									if(!(params[i] instanceof Byte)){
										allMatch = false;
										break;
									}
								}
								else if(methodParamTypes[j].equals(Short.TYPE)){
									if(!(params[i] instanceof Short)){
										allMatch = false;
										break;
									}
								}
								else if(methodParamTypes[j].equals(Character.TYPE)){
									if(!(params[i] instanceof Character)){
										allMatch = false;
										break;
									}
								}
								else if(methodParamTypes[j].equals(Integer.TYPE)){
									if(!(params[i] instanceof Integer)){
										allMatch = false;
										break;
									}
								}
								else if(methodParamTypes[j].equals(Long.TYPE)){
									if(!(params[i] instanceof Long)){
										allMatch = false;
										break;
									}
								}
								else if(methodParamTypes[j].equals(Float.TYPE)){
									if(!(params[i] instanceof Float)){
										allMatch = false;
										break;
									}
								}
								else if(methodParamTypes[j].equals(Double.TYPE)){
									if(!(params[i] instanceof Double)){
										allMatch = false;
										break;
									}
								}
							}
							else{
								if(!methodParamTypes[j].isInstance(params[i])){
									allMatch = false;
									break;
								}
							}
						}		
					}
					if(allMatch){
						return methods[i];
					}
				}
			}
		}
		return null;
	}

	public static Message createAsyncCallMessage(Object proxy, String methodName, Object[] params){
		NamedPropertyMessage m = new NamedPropertyMessage(methodName);

		Method method = guessMethod(proxy, methodName, params);
		if(method == null){
			throw new IllegalArgumentException("Can't find method.");
		}
		Class[] parameterTypes = method.getParameterTypes();
		if(params == null){
			params = new Object[]{};
		}
		for(int i = 0; i < params.length; i++){
			Class c = parameterTypes[i];
			
			m.addParameter("paramType" + i, c2.util.ClassArrayEncoder.classToString(c));
			if(params[i] != null){
				m.addParameter("paramValue" + i, params[i]);
			}
			else{
				m.addParameter("paramValue" + i, new c2.pcwrap.NullParameterValue());
			}
		}
		m.addParameter("numParameters", params.length);
		
		m.addParameter("ebiMessageType", "outgoingCall");
		//String callId = c2.util.UIDGenerator.generateUID("call-" + id.toString());
		String callId = "call-async" + (counter++);
		
		m.addParameter("callId", callId);
		m.addParameter("ebiAdapterHelperId", "async");
		EBIAdapter ebiAdapter = (EBIAdapter)Proxy.getInvocationHandler(proxy);
		m.addParameter("targetInterface", ebiAdapter.getInterfaceClass().getName());
		return m;
	}

}

class EBIDelegateBrickListener implements DelegateBrickListener{
	public void messageProcessorAdded(DelegateBrick brick, MessageProcessor mp){
		if(!(mp instanceof ThreadMessageProcessor)){
			brick.removeMessageProcessor(mp);
			EBIWrapperUtils.addThreadMessageProcessor(brick, mp);
		}
	}
	
	public void messageProcessorRemoved(DelegateBrick brick, MessageProcessor mp){
		if(!(mp instanceof ThreadMessageProcessor)){
			MessageProcessor[] mps = brick.getMessageProcessors();
			for(int i = 0; i < mps.length; i++){
				if(mps[i] instanceof ThreadMessageProcessor){
					ThreadMessageProcessor tmp = (ThreadMessageProcessor)mps[i];
					MessageProcessor[] imps = tmp.getMessageProcessors();
					for(int j = 0; j < imps.length; j++){
						if(imps[j] == mp){
							brick.removeMessageProcessor(tmp);
							break;
						}
					}
				}
			}
		}
	}

	public void lifecycleProcessorAdded(DelegateBrick brick, LifecycleProcessor lp){}
	public void lifecycleProcessorRemoved(DelegateBrick brick, LifecycleProcessor lp){}
	
}

