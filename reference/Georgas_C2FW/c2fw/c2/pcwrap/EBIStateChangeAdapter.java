package c2.pcwrap;

import c2.fw.*;
import java.lang.reflect.*;

public class EBIStateChangeAdapter implements InvocationHandler{
	
	private DelegateBrick b;
	private Interface iface;
	private Object api;
	private Class stateChangeInterfaceClass;
	private MessageFilter filter = null;
	
	public EBIStateChangeAdapter(DelegateBrick b, Interface iface, Object api, Class stateChangeInterfaceClass){
		this.b = b;
		this.iface = iface;
		this.api = api;
		this.stateChangeInterfaceClass = stateChangeInterfaceClass;
		
		establishStateChangeConnection();
	}
	
	public EBIStateChangeAdapter(DelegateBrick b, Interface iface, Object api, Class stateChangeInterfaceClass,
		MessageFilter filter){
		this(b, iface, api, stateChangeInterfaceClass);
		this.filter = filter;
	}
	
	public Object getApi(){
		return api;
	}
	
	public void destroy(){
		breakStateChangeConnection();
	}
	
	protected void establishStateChangeConnection(){
		changeStateChangeConnection(true);
	}
	
	protected void breakStateChangeConnection(){
		changeStateChangeConnection(false);
	}
	
	protected void changeStateChangeConnection(boolean establish){
		//If establish is true, we'll create the connection.  If not, we'll break it.
		
		//So, here we have to fake being a whateverlistener.  So, let's say that
		//our hypothetical class is a source of ActionEvents.  That means, if it
		//follows the usual convention, it can hook up to ActionListeners.  Therefore,
		//there should be two functions on there: addActionListener and removeActionListener.
		//Both of these should take an ActionListener as a parameter.  We're given the class
		//"java.foobar.whatever.ActionListener" so it should be pretty easy to guess the method
		//names.

		String interfaceName = stateChangeInterfaceClass.getName();
		
		//Cut off everything before the last '.'
		String shortName = interfaceName.substring(interfaceName.lastIndexOf(".") + 1);
		
		String addFunctionName = "add" + shortName;
		String removeFunctionName = "remove" + shortName;

		String fn;
		
		try{
			if(establish){
				fn = addFunctionName;
			}
			else{
				fn = removeFunctionName;
			}
			
			Method m = api.getClass().getMethod(fn, new Class[]{ stateChangeInterfaceClass });
			if(m == null){
				throw new IllegalArgumentException("Can't find method: " + fn + " in class " + stateChangeInterfaceClass);
			}
	
			Object proxyListener = Proxy.newProxyInstance(stateChangeInterfaceClass.getClassLoader(),
				new Class[] { stateChangeInterfaceClass }, this);
			
			try{
				m.invoke(api, new Object[] { proxyListener } );
			}
			catch(InvocationTargetException ite){
				throw ite.getTargetException();
			}
		}
		catch(IllegalAccessException iae){
			iae.printStackTrace();
			return;
		}
		catch(NoSuchMethodException nsme){
			nsme.printStackTrace();
			return;
		}
		catch(Throwable e){
			e.printStackTrace();
			return;
		}
	}
		
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
		NamedPropertyMessage m = new NamedPropertyMessage(method.getName());
		m.addParameter("stateChangeMessage", true);
		m.addParameter("stateChangeInterfaceClass", stateChangeInterfaceClass);
		
		Class[] parameterTypes = method.getParameterTypes();
		m.addParameter("stateChangeParameterTypes", parameterTypes);
		m.addParameter("stateChangeParameterValues", args);
		
		if(args != null){
			m.addParameter("numParameters", args.length);
			for(int i = 0; i < args.length; i++){
				Class c;
				if(args[i] == null){
					c = parameterTypes[i];
				}
				else{
					c = args[i].getClass();
				}
				
				m.addParameter("paramType" + i, c2.util.ClassArrayEncoder.classToString(c));
				m.addParameter("paramValue" + i, args[i]);
			}
		}
		else{
			m.addParameter("numParameters", 0);
		}

		if(filter != null){
			if(!filter.accept(m)){
				return null;
			}
		}
		b.sendToAll(m, iface);
		return null;
	}
	

}

