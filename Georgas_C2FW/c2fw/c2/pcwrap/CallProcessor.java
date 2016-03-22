package c2.pcwrap;

import c2.fw.*;

import java.lang.reflect.*;

public class CallProcessor implements MessageProcessor{

	protected DelegateBrick b;
	protected Interface iface;
	protected Object api;
	protected Class interfaceClass;
	protected MethodCache methodCache;
	
	public CallProcessor(DelegateBrick b, Interface iface, Object api, Class interfaceClass){
		this.b = b;
		this.api = api;
		this.methodCache = new MethodCache(api);
		this.iface = iface;
		this.interfaceClass = interfaceClass;
	}

	public Object getApi(){
		return api;
	}
	
	public void handle(Message m){
		//System.out.println("CallProcessor handle() got a message: " + m);
		if(!(m instanceof NamedPropertyMessage)){
			return;
		}
		//System.out.println("It was an NPM");
		
		NamedPropertyMessage npm = (NamedPropertyMessage)m;

		String targetClass = (String)npm.getParameter("targetInterface");
		if(targetClass == null){
			return;
		}
		else if(!targetClass.equals(interfaceClass.getName())){
			//Not targeted to our interface
			return;
		}
		
		String methodName = npm.getName();
		//System.out.println("The method name is: " + methodName);
		int numParameters = -1;
		try{
			numParameters = npm.getIntParameter("numParameters");
		}
		catch(IllegalArgumentException iae){
			return;
		}
		
		//System.out.println("The number of params is: " + numParameters);
		
		//Class[] paramClasses = new Class[numParameters];
		String[] paramClasses = new String[numParameters];
		Object[] paramValues = new Object[numParameters];
		
		for(int i = 0; i < numParameters; i++){
			paramClasses[i] = (String)npm.getParameter("paramType" + i);
			//try{
				//paramClasses[i] = c2.util.ClassArrayEncoder.stringToClass((String)npm.getParameter("paramType" + i));
			//}
			//catch(ClassNotFoundException e){
			//	e.printStackTrace();
			//	return;
			//}
			
			paramValues[i] = npm.getParameter("paramValue" + i);
			if(paramValues[i] == null){
				return;
			}
			if(paramValues[i] instanceof c2.pcwrap.NullParameterValue){
				paramValues[i] = null;
			}
		}
		
		String callId = (String)npm.getParameter("callId");
		if(callId == null){
			return;
		}
		//System.out.println("The call ID is: " + callId);

		//for(int i = 0; i < paramClasses.length; i++){
		//	System.out.println("The parameter class is: " + paramClasses[i]);
		//}
		
		try{
			Method method = methodCache.getMethod(methodName, paramClasses);
			//Method method = api.getClass().getMethod(methodName, paramClasses);
			if(method == null){
				//Method wasn't found.
				return;
			}
			
			//System.out.println("method = " + method);
			
			Object retVal = method.invoke(api, paramValues);
			
			//if(retVal != null) System.out.println("retVal.class = " + retVal.getClass());
			
			//System.out.println("invocation returned.  retval = " + retVal);
			NamedPropertyMessage responseMessage = new NamedPropertyMessage(methodName);
			responseMessage.addParameter("exceptionOccurred", false);
			responseMessage.addParameter("callId", callId);
			responseMessage.addParameter("ebiAdapterHelperId", npm.getParameter("ebiAdapterHelperId"));

			responseMessage.addParameter("returnType", c2.util.ClassArrayEncoder.classToString(method.getReturnType()));
			if(retVal == null){
				responseMessage.addParameter("returnValue", new c2.pcwrap.NullParameterValue());
			}
			else{
				responseMessage.addParameter("returnValue", retVal);
			}
			b.sendToAll(responseMessage, iface);
		}
		//catch(NoSuchMethodException nsme){
		//	return;
		//}
		catch(IllegalAccessException iae){
			iae.printStackTrace();
			return;
		}
		catch(InvocationTargetException ite){
			//System.out.println("****Got an ite exception in CallProcessor!*****");
			Throwable t = ite.getTargetException();
			//t.printStackTrace();
			//System.err.println("********");
			NamedPropertyMessage responseMessage = new NamedPropertyMessage(methodName);
			responseMessage.addParameter("exceptionOccurred", true);
			responseMessage.addParameter("returnType", c2.util.ClassArrayEncoder.classToString(t.getClass()));
			responseMessage.addParameter("returnValue", t);
			responseMessage.addParameter("callId", callId);
			responseMessage.addParameter("ebiAdapterHelperId", npm.getParameter("ebiAdapterHelperId"));
			b.sendToAll(responseMessage, iface);
		}
		/*
		catch(RuntimeException re){
			System.out.println("****Got a straight-up runtime exception in CallProcessor!*****");
			re.printStackTrace();
			throw re;
		}*/
	}
}

