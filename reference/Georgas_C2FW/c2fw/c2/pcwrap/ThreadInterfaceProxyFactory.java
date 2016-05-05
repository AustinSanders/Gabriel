package c2.pcwrap;

import java.lang.reflect.*;

public class ThreadInterfaceProxyFactory{
	
	public static Object createThreadInterfaceProxy(Object trueObject, Class interfaceClass){
		return Proxy.newProxyInstance(ThreadInterfaceProxyFactory.class.getClassLoader(), 
			new Class[]{interfaceClass}, new ThreadInterfaceProxy(trueObject));  
	}
	
}

class ThreadInterfaceProxy extends Thread implements InvocationHandler{
	
	private Object serLock = new Object();
	private Object trueObject;
	private Method workMethod;
	private Object[] workParams;
	
	public ThreadInterfaceProxy(Object trueObject){
		this.trueObject = trueObject;
		this.setDaemon(true);
		this.start();
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
		if((method.getReturnType() == null) || (method.getReturnType().equals(void.class))){
			//We can split off a thread to handle this.
			addWork(method, args);
			return null;
		}
		else{
			//we have to process it straight-up, too bad.
			return method.invoke(trueObject, args);
		}
	}
	
	public void addWork(Method m, Object[] p){
		synchronized(serLock){
			this.workMethod = m;
			this.workParams = p;
			serLock.notifyAll();
		}
	}
	
	public void run(){
		synchronized(serLock){
			while(true){
				while(workMethod == null){
					try{
						serLock.wait();
					}
					catch(InterruptedException ie){}
				}
				//Okay, there's work to be done.
				//System.out.println("Got work!  Working!");
				try{
					workMethod.invoke(trueObject, workParams);
				}
				catch(Throwable t){
					throw new RuntimeException(t);
				}
				workMethod = null;
				workParams = null;
			}
		}
	}
}