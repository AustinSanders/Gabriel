package c2.pcwrap;

import java.lang.reflect.*;
import java.util.*;

class MethodCache{
	//Maps method names to arrays of elt[]
	private HashMap map;
		
	public MethodCache(Object o){
		map = new HashMap();
		Method[] ms = o.getClass().getMethods();
		for(int i = 0; i < ms.length; i++){
			String methodName = ms[i].getName();
			Elt elt = new Elt();
			elt.method = ms[i];
			Class[] pcs = ms[i].getParameterTypes();
			elt.parameterClasses = new String[pcs.length];
			for(int j = 0; j < pcs.length; j++){
				elt.parameterClasses[j] = c2.util.ClassArrayEncoder.classToString(pcs[j]);
			}
			ArrayList list = (ArrayList)map.get(methodName);
			if(list == null){
				list = new ArrayList();
			}
			list.add(elt);
			map.put(methodName, list);
		}
		for(Iterator it = map.keySet().iterator(); it.hasNext(); ){
			String name = (String)it.next();
			ArrayList list = (ArrayList)map.get(name);
			Elt[] arr = new Elt[list.size()];
			for(int i = 0; i < arr.length; i++){
				arr[i] = (Elt)list.get(i);
			}
			map.put(name, arr);
		}
		
	}
	
	public boolean arraysEqual(String[] arr1, String[] arr2){
		if(arr1.length != arr2.length) return false;
		for(int i = 0; i < arr1.length; i++){
			if(!arr1[i].equals(arr2[i])){
				return false;
			}
		}
		return true;
	}
	
	public Method getMethod(String name, String[] params){
		Elt[] elts = (Elt[])map.get(name);
		if(elts == null) return null;
		for(int i = 0; i < elts.length; i++){
			if(arraysEqual(params, elts[i].parameterClasses)){
				return elts[i].method;
			}
		}
		return null;
	}
	
	class Elt{
		public String[] parameterClasses;
		public Method method;
	}
}
