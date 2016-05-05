package c2.fw;

import java.lang.reflect.*;
import java.util.*;

/**
 * Internally used class that emulates multiple inheritance for
 * <CODE>ArchitectureController</CODE> objects.
 * @see c2.fw.ArchitectureControllerFactory
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */

class ProxyArchitectureControllerInvocationHandler implements InvocationHandler{

	protected static boolean initDone = false;
	protected static HashMap managerMethods;
	protected static HashMap handlerMethods;
	protected static HashMap engineMethods;
	
	protected static HashMap actualManagerMethods;
	protected static HashMap actualHandlerMethods;
	protected static HashMap actualEngineMethods;
	
	protected LocalArchitectureManager manager;
	protected MessageHandler handler;
	protected ArchitectureEngine engine;
	
	public ProxyArchitectureControllerInvocationHandler(
	LocalArchitectureManager manager,
	MessageHandler handler,
	ArchitectureEngine engine){
		this.manager = manager;
		this.handler = handler;
		this.engine = engine;
		init();
	}

	public void init(){
		if(initDone){
			return;
		}
		Method[] methods;
		
		methods = LocalArchitectureManager.class.getMethods();
		managerMethods = new HashMap();
		for(int i = 0; i < methods.length; i++){
			managerMethods.put(getMethodSignature(methods[i]), methods[i]);
		}

		methods = MessageHandler.class.getMethods();
		handlerMethods = new HashMap();
		for(int i = 0; i < methods.length; i++){
			handlerMethods.put(getMethodSignature(methods[i]), methods[i]);
		}
		
		methods = ArchitectureEngine.class.getMethods();
		engineMethods = new HashMap();
		for(int i = 0; i < methods.length; i++){
			engineMethods.put(getMethodSignature(methods[i]), methods[i]);
		}

		methods = manager.getClass().getMethods();
		actualManagerMethods = new HashMap();
		for(int i = 0; i < methods.length; i++){
			actualManagerMethods.put(getMethodSignature(methods[i]), methods[i]);
		}

		methods = handler.getClass().getMethods();
		actualHandlerMethods = new HashMap();
		for(int i = 0; i < methods.length; i++){
			actualHandlerMethods.put(getMethodSignature(methods[i]), methods[i]);
		}
		
		methods = engine.getClass().getMethods();
		actualEngineMethods = new HashMap();
		for(int i = 0; i < methods.length; i++){
			actualEngineMethods.put(getMethodSignature(methods[i]), methods[i]);
		}
		
		initDone = true;
	}
	
	private static String getMethodSignature(Method m){
		StringBuffer sb = new StringBuffer();
		//sb.append(m.getModifiers());
		//sb.append("$$$");
		sb.append(m.getReturnType().getName());
		sb.append("$$$");
		sb.append(m.getName());
		sb.append("$$$");
		Class[] c = m.getParameterTypes();
		for(int i = 0; i < c.length; i++){
			sb.append(c[i].getName());
			sb.append("$$$");
		}
		c = m.getExceptionTypes();
		for(int i = 0; i < c.length; i++){
			sb.append(c[i].getName());
			sb.append("$$$");
		}
		return sb.toString();
	}

	//private Map methodCache = new HashMap();
	private Map methodCache = new c2.util.CachedMap();
	
	private synchronized void cacheMethod(Method calledMethod, Method trueMethod, Object targetObject){
		methodCache.put(calledMethod, new Object[]{trueMethod, targetObject});
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
		if(!initDone){
			init();
		}
		
		try{
			Object[] mom = (Object[])methodCache.get(method);
			if(mom != null){
				return ((Method)mom[0]).invoke(mom[1], args);
			}
			
			String sig = getMethodSignature(method);
			Method m = (Method)managerMethods.get(sig);
			if(m != null){
				cacheMethod(method, m, manager);
				return m.invoke(manager, args);
			}
			else{
				m = (Method)handlerMethods.get(sig);
				if(m != null){
					cacheMethod(method, m, handler);
					return m.invoke(handler, args);
				}
				else{
					m = (Method)engineMethods.get(sig);
					if(m != null){
						cacheMethod(method, m, engine);
						return m.invoke(engine, args);
					}
					else{
						//Check the actual methods.
						m = (Method)actualManagerMethods.get(sig);
						if(m != null){
							cacheMethod(method, m, manager);
							return m.invoke(manager, args);
						}
						else{
							m = (Method)actualHandlerMethods.get(sig);
							if(m != null){
								cacheMethod(method, m, handler);
								return m.invoke(handler, args);
							}
							else{
								m = (Method)actualEngineMethods.get(sig);
								if(m != null){
									cacheMethod(method, m, engine);
									return m.invoke(engine, args);
								}
								else{
									throw new IllegalArgumentException("Invalid interface method called: " + method.getName() + ".");
									
								}
							}
						}
					}
				}
			}
		}
		catch(InvocationTargetException e){
			throw e.getTargetException();
		}
	}
}
