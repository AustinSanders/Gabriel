package c2.pcwrap;

import c2.fw.*;

import java.lang.reflect.*;

public class EBIStateChangeProviderProxy implements InvocationHandler{

	protected DelegateBrick targetBrick;
	protected Interface targetIface;
	
	public EBIStateChangeProviderProxy(DelegateBrick b, Interface iface){
		this.targetBrick = b;
		this.targetIface = iface;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
		String methodName = method.getName();
		if(methodName.startsWith("add") && methodName.endsWith("Listener")){
			Class[] methodParamTypes = method.getParameterTypes();
			if(methodParamTypes.length == 1){
				Class listenerClass = methodParamTypes[0];
				Object listenerObject = args[0];
				EBIWrapperUtils.addListener(targetBrick, targetIface, listenerClass, listenerObject);
				return null;
			}
		}
		else if(methodName.startsWith("remove") && methodName.endsWith("Listener")){
			Class[] methodParamTypes = method.getParameterTypes();
			if(methodParamTypes.length == 1){
				Class listenerClass = methodParamTypes[0];
				Object listenerObject = args[0];
				EBIWrapperUtils.removeListener(targetBrick, targetIface, listenerClass, listenerObject);
				return null;
			}
		}
		//This shouldn't happen
		throw new RuntimeException("Proxy can't handle method " + methodName);
	}
	
}
