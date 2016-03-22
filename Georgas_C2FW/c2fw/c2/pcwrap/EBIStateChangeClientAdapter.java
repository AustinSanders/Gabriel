package c2.pcwrap;

import c2.fw.*;

import java.lang.reflect.*;
import java.util.*;

public class EBIStateChangeClientAdapter implements MessageProcessor{

	protected DelegateBrick brick;
	protected Interface iface;
	
	protected boolean listenersChanged = false;
	protected Class listenerClass = null;
	protected ArrayList listenerList = new ArrayList();
	protected Object[] listeners = new Object[0];
	
	public EBIStateChangeClientAdapter(DelegateBrick b, Interface iface, Class listenerClass){
		this.brick = b;
		this.iface = iface;
		this.listenerClass = listenerClass;
	}
	
	public Class getListenerClass(){
		return listenerClass;
	}
	
	public DelegateBrick getTargetBrick(){
		return brick;
	}
	
	public Interface getTargetInterface(){
		return iface;
	}
	
	public synchronized int getNumListeners(){
		return listenerList.size(); 
	}
	
	public synchronized void addListener(Object o){
		listenerList.add(o);
		listenersChanged = true;
	}
	
	public synchronized void removeListener(Object o){
		listenerList.remove(o);
		listenersChanged = true;
	}
	
	public void handle(Message m){
		if(m.getDestination().getInterfaceIdentifier().equals(iface.getIdentifier())){
			if(m instanceof NamedPropertyMessage){
				NamedPropertyMessage npm = (NamedPropertyMessage)m;
				Object scmParameter = npm.getParameter("stateChangeMessage");
				if ((scmParameter != null) && (scmParameter instanceof Boolean) && 
				((Boolean)scmParameter).booleanValue()){
					
					Object scicParameter = npm.getParameter("stateChangeInterfaceClass");
					if((scicParameter != null) && (scicParameter instanceof Class)){
						Class stateChangeInterfaceClass = (Class)scicParameter;
						if(stateChangeInterfaceClass.equals(listenerClass)){
							
							Object scptParameter = npm.getParameter("stateChangeParameterTypes");
							if((scptParameter != null) && (scptParameter instanceof Class[])){
								Class[] stateChangeParameterTypes = (Class[])scptParameter;
								
								Object scpvParameter = npm.getParameter("stateChangeParameterValues");
								if((scpvParameter != null) && (scpvParameter instanceof Object[])){
									if(listenersChanged){
										synchronized(this){
											listeners = listenerList.toArray();
											listenersChanged = false;
										}
									}
									
									if(listeners.length == 0){
										return;
									}
									
									try{
										Method methodToCall = listenerClass.getMethod(npm.getName(), 
											stateChangeParameterTypes);
										for(int i = 0; i < listeners.length; i++){
											methodToCall.invoke(listeners[i], ((Object[])scpvParameter));
										}
									}
									catch(NoSuchMethodException nsme){
										return;
									}
									catch(InvocationTargetException ite){
										ite.getCause().printStackTrace();
										return;
									}
									catch(IllegalAccessException iae){
										iae.printStackTrace();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

}
