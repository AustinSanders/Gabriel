package c2.util;

public class ArrayUtils{

	public static boolean equals(Object[] arr1, Object[] arr2){
		return java.util.Arrays.equals(arr1, arr2);
		/*
		if(arr1.length != arr2.length){
			return false;
		}
		
		for(int i = 0; i < arr1.length; i++){
			if((arr1[i] == null) && (arr2[i] == null)){
				continue;
			}
			if((arr1[i] == null) || (arr2[i] == null)){
				return false;
			}
			if(!arr1[i].equals(arr2[i])){
				return false;
			}
		}
		return true;
		*/
	}
	
	public static int arrayHashCode(Object[] arr){
		int hc = arr.length;
		for(int i = 0; i < arr.length; i++){
			if(arr[i] == null){
				hc ^= 0;
			}
			else{
				hc ^= arr[i].hashCode();
			}
		}
		return hc;
	}
	
	public static String arrayToString(byte[] array){
		if(array == null){
			return "null";
		}
		
		Class c = array.getClass();
		
		StringBuffer sb = new StringBuffer();
		sb.append("Array[elementType=\"");
		sb.append("int");
		sb.append("\", length=");
		sb.append(array.length);
		sb.append("]{");
		for(int i = 0; i < array.length; i++){			
			sb.append(array[i]);
			if(i != (array.length - 1)){
				sb.append(", ");
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static String arrayToString(int[] array){
		if(array == null){
			return "null";
		}
		
		Class c = array.getClass();
		
		StringBuffer sb = new StringBuffer();
		sb.append("Array[elementType=\"");
		sb.append("int");
		sb.append("\", length=");
		sb.append(array.length);
		sb.append("]{");
		for(int i = 0; i < array.length; i++){			
			sb.append(array[i]);
			if(i != (array.length - 1)){
				sb.append(", ");
			}
		}
		sb.append("}");
		return sb.toString();
	}
		
	public static String arrayToString(Object[] array){
		if(array == null){
			return "null";
		}
		
		Class c = array.getClass();
		Class t = c.getComponentType();
		boolean tIsArray = t.isArray();
		
		StringBuffer sb = new StringBuffer();
		sb.append("Array[elementType=\"");
		sb.append(t.getName());
		sb.append("\", length=");
		sb.append(array.length);
		sb.append("]{");
		for(int i = 0; i < array.length; i++){
			if(tIsArray){
				sb.append(arrayToString((Object[])array[i]));
			}
			else{
				sb.append(array[i]);
			}
			if(i != (array.length - 1)){
				sb.append(", ");
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
}

